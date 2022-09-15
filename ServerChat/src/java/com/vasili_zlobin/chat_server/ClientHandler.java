package com.vasili_zlobin.chat_server;

import com.vasili_zlobin.chat.command.Command;
import com.vasili_zlobin.chat.command.CommandType;
import com.vasili_zlobin.chat.command.commands.AuthCommandData;
import com.vasili_zlobin.chat.command.commands.PrivateMessageCommandData;
import com.vasili_zlobin.chat.command.commands.PublicMessageCommandData;
import com.vasili_zlobin.chat_server.database.DatabaseService;
import com.vasili_zlobin.chat_server.database.HistoryFilesService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private static final int AUTH_TIMEOUT_MS = 120_000;
    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final Object syncObject = new Object();
    private String userName;
    private String login;
    private Thread handleThread;
    private Thread timeoutThread;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new ObjectInputStream(socket.getInputStream());
        outputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    private void authenticate() throws IOException {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                Command command = readCommand();
                if (command == null) {
                    continue;
                }
                if (command.getType() == CommandType.AUTH) {
                    AuthCommandData data = (AuthCommandData) command.getData();
                    ServerService server = ServerService.getInstance();
                    String nickname = data.getNickname();
                    String tempName = server.getAuthService().getUsernameByLoginPassword(data.getLogin(), data.getPassword());
                    if (tempName == null && addNewUserInDatabase(nickname, data.getLogin(), data.getPassword())) {
                        tempName = nickname;
                    }
                    if (tempName == null) {
                        sendCommand(Command.errorCommand("Логин или пароль указаны некорректно"));
                    } else if (server.isUserNameBusy(tempName)) {
                        sendCommand(Command.errorCommand("Пользователь уже авторизован в чате"));
                    } else {
                        synchronized (syncObject) {
                            if (nickname != null && !nickname.equals(tempName) && changeUserName(tempName, nickname)) {
                                tempName = nickname;
                            }
                            userName = tempName;
                            login = data.getLogin();
                            sendCommand(Command.authOkCommand(userName, HistoryFilesService.loadHistory(login)));
                            server.subscribe(this);
                            timeoutThread.interrupt();
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to authenticate user");
            throw e;
        }
    }

    private boolean addNewUserInDatabase(String nick, String login, String password) {
        boolean result = false;
        if (nick != null && login != null && password != null) {
            try {
                result = DatabaseService.getInstance().addNewUser(nick, login, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean changeUserName(String currentName, String nameNew) {
        boolean result = false;
            try {
                result = DatabaseService.getInstance().changeUserName(currentName, nameNew);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return result;
    }

    private Command readCommand() throws IOException {
        Command command = null;
        try {
            command = (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to read Command class");
            e.printStackTrace();
        }
        return command;
    }

    private void readMessages() throws IOException {
        try {
            while (true) {
                Command command = readCommand();
                if (command == null) {
                    continue;
                }
                processMessage(command);
            }
        } catch (IOException e) {
            System.err.println("Failed read message from " + userName);
            throw e;
        }
    }

    private void processMessage(Command command) throws IOException {
        switch (command.getType()) {
            case PRIVATE_MESSAGE: {
                PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                ServerService.getInstance().sendPersonalMessage(data.getMessage(), userName, data.getReceiver());
                break;
            }
            case PUBLIC_MESSAGE: {
                PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                ServerService.getInstance().broadcastMessage(data.getMessage(), userName);
                break;
            }
        }
    }

    private void checkAuthTimeout() {
        timeoutThread = new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIMEOUT_MS);
                synchronized (syncObject) {
                    if (!Thread.currentThread().isInterrupted() && userName == null) {
                        closeConnection();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Check timeout has interrupted");
            }
        });
        timeoutThread.start();
    }

    public String getUserName() {
        return userName;
    }

    public void handle() {
        handleThread = new Thread(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!Thread.currentThread().isInterrupted()) {
                    closeConnection();
                }
            }
        });
        handleThread.start();
        checkAuthTimeout();
    }

    public void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public void sendMessage(String sender, String message) throws IOException {
        HistoryFilesService.saveHistory(login, sender, message);
        sendCommand(Command.receivedMessageCommand(sender, message));
    }

    public void closeConnection() {
        try {
            if (timeoutThread != null && !timeoutThread.isInterrupted()) {
                timeoutThread.interrupt();
            }
            if (handleThread != null && !handleThread.isInterrupted()) {
                handleThread.interrupt();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Failed close connection");
            e.printStackTrace();
        } finally {
            ServerService.getInstance().unsubscribe(this);
        }
    }
}