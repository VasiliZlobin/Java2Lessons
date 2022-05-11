package com.vasili_zlobin.clientchat.model;

import com.vasili_zlobin.chat.command.Command;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Network {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8189;
    private static Network networkInstance;

    private final String address;
    private final int port;
    private final List<ReadMessagesListener> listeners = new CopyOnWriteArrayList<>();
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private boolean connected;
    private Thread readMessagesProcess;

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
