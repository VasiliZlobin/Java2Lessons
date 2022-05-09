package com.vasili_zlobin.chat_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String COMMAND_AUTH = "/auth";
    private static final String COMMAND_AUTH_OK = "/authok";
    private static final String COMMAND_PERSONAL = "/w";
    private static final String COMMAND_BREAK = "/end";
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private String name;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    private void authenticate() throws IOException {
        try {
            while (true) {
                String message = inputStream.readUTF();
                if (message.startsWith(COMMAND_AUTH)) {
                    String[] parts = message.split(" ");
                    name = ServerService.getInstance().getAuthService().getUsernameByLoginPassword(parts[1], parts[2]);
                    if (name == null) {
                        sendMessage("Указаны неправильные логин и пароль");
                    } else if (ServerService.getInstance().subscribe(this)) {
                        sendMessage(COMMAND_AUTH_OK + " " + name);
                        break;
                    } else {
                        sendMessage("Пользователь уже авторизован в чате");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to authenticate user");
            throw e;
        }
    }

    private void readMessages() throws IOException {
        try {
            while (true) {
                String message = inputStream.readUTF();
                if (message.startsWith(COMMAND_BREAK)) {
                    break;
                }
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Failed read message from " + name);
            throw e;
        }
    }

    private void processMessage(String message) throws IOException {
        if (message.startsWith(COMMAND_PERSONAL)) {
            String[] parts = message.split(" ");
            ServerService.getInstance().sendPersonalMessage(parts[2], parts[1]);
        } else {
            ServerService.getInstance().broadcastMessage(message, name);
        }
    }

    public String getName() {
        return name;
    }

    public void handle() {
        new Thread(() -> {
            try {
                authenticate();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    public void closeConnection() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed close connection");
            e.printStackTrace();
        } finally {
            ServerService.getInstance().unsubscribe(this);
        }
    }
}