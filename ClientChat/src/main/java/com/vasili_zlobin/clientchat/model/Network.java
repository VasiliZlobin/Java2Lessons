package com.vasili_zlobin.clientchat.model;

import com.vasili_zlobin.chat.command.Command;
import com.vasili_zlobin.clientchat.ClientChatApplication;
import com.vasili_zlobin.clientchat.controllers.AuthController;
import com.vasili_zlobin.clientchat.dialogs.Dialogs;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Network {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    private static final int START_TIMEOUT_RECONNECT = 100;
    private static final int MAX_TIMEOUT_RECONNECT = 500;
    private static Network networkInstance;

    private final String address;
    private final int port;
    private final List<ReadMessagesListener> listeners = new CopyOnWriteArrayList<>();
    private final int[] pairTryReconnect = new int[2];
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean connected;
    private Thread readMessagesProcess;
    private Thread tryReconnectProcess;

    private Network(String address, int port) {
        this.address = address;
        this.port = port;
    }

    private Network() {
        this(SERVER_ADDRESS, SERVER_PORT);
    }

    public static Network getInstance() {
        if (networkInstance == null) {
            networkInstance = new Network();
        }
        return networkInstance;
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

    private void processTryReconnect() {
        tryReconnectProcess = new Thread(() -> {
            while (!connected) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                int sleep = getNextTimeout();
                if (sleep <= MAX_TIMEOUT_RECONNECT) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (connectToServer()) {
                        trySilentAuthorisation();
                        break;
                    }
                } else {
                    Platform.runLater(() -> {
                        Dialogs.NetworkError.BREAK_CONNECT.show(getAuthController().getAuthStage());
                        ClientChatApplication.getInstance().showAuthWindow();
                    });
                    break;
                }
            }
        });
        tryReconnectProcess.start();
    }

    private void trySilentAuthorisation() {
        AuthController authController = getAuthController();
        authController.setReconnectAuthListener();
        String login = authController.getLastLogin();
        String password = authController.getLastPassword();
        if (login != null && password != null) {
            authController.sendAuthCommand(login, password, authController.getAuthStage());
        }
    }

    private int getNextTimeout() {
        int result = pairTryReconnect[0] + pairTryReconnect[1];
        pairTryReconnect[0] = pairTryReconnect[1];
        pairTryReconnect[1] = result;
        return result;
    }

    private AuthController getAuthController() {
        return ClientChatApplication.getInstance().getAuthController();
    }

    public void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public boolean connectToServer() {
        try {
            socket = new Socket(address, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            readMessagesProcess = startReadMessagesProcess();
            connected = true;
            pairTryReconnect[0] = START_TIMEOUT_RECONNECT;
            pairTryReconnect[1] = START_TIMEOUT_RECONNECT;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connected;
    }

    public void sendMessage(String message, String receiver) throws IOException {
        Command command;
        if (receiver == null) {
            command = Command.publicMessageCommand(message);
        } else {
            command = Command.privateMessageCommand(receiver, message);
        }
        sendCommand(command);
    }

    public Thread startReadMessagesProcess() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    Command command = readCommand();
                    for (ReadMessagesListener listener : listeners) {
                        listener.processReceivedCommand(command);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    close();
                    break;
                }
            }
        });
        thread.start();
        return thread;
    }

    public void close() {
        try {
            connected = false;
            if (readMessagesProcess != null && !readMessagesProcess.isInterrupted()) {
                readMessagesProcess.interrupt();
            }
            if (tryReconnectProcess != null && !tryReconnectProcess.isInterrupted()) {
                tryReconnectProcess.interrupt();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (getAuthController().isSuccessAuth()) {
                getAuthController().setSuccessAuth(false);
                processTryReconnect();
            }
        } catch (IOException e) {
            System.err.println("Не удалось закрыть соединение");
            e.printStackTrace();
        }
    }

    public ReadMessagesListener addReadMessagesListener(ReadMessagesListener listener) {
        this.listeners.add(listener);
        return listener;
    }

    public void removeReadMessagesListener(ReadMessagesListener listener) {
        this.listeners.remove(listener);
    }

    public boolean isConnected() {
        return connected;
    }
}
