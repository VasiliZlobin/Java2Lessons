package com.vasili_zlobin.clientchat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class Network {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    private static final String COMMAND_BREAK = "/end";
    private static Network networkInstance;

    private final String address;
    private final int port;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

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

    public boolean connectToServer() {
        try {
            socket = new Socket(address, port);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
    }

    public void receiveMessages(Consumer<String> messageHandler) {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    String message = inputStream.readUTF();
                    messageHandler.accept(message);
                } catch (IOException e) {
                    System.err.println("Не удалось получить сообщение от сервера");
                    e.printStackTrace();
                    break;
                }
            }
        });
        thread.start();
    }

    public void close() {
        try {
            sendMessage(COMMAND_BREAK);
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Не удалось закрыть соединение");
            e.printStackTrace();
        }
    }
}
