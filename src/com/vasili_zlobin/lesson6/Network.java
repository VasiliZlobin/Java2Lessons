package com.vasili_zlobin.lesson6;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Network {
    private static final String COMMAND_BREAK = "/end";
    static final String SERVER_ADDRESS = "localhost";
    static final int SERVER_PORT = 8189;

    private final Socket socket;
    private final DataInputStream socketInput;
    private final DataOutputStream socketOutput;
    private final String hostName;
    private final Scanner scanner = new Scanner(System.in);
    private boolean closeSession = false;

    public Network(Socket socket, String hostName) throws IOException {
        this.socket = socket;
        this.hostName = hostName;
        this.socketInput = new DataInputStream(socket.getInputStream());
        this.socketOutput = new DataOutputStream(socket.getOutputStream());
    }

    private void sendMessage(String message) throws IOException {
        socketOutput.writeUTF(message);
    }

    private String receiveMessage() {
        try {
            return socketInput.readUTF();
        } catch (IOException e) {
            return COMMAND_BREAK;
        }
    }

    public void closeConnection() {
        try {
            socketInput.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия входящего потока для " + hostName);
        }
        try {
            socketOutput.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия исходящего потока для " + hostName);
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Ошибка закрытия сокета соединения для " + hostName);
        }
    }

    public void waitConsoleInput() {
        while (!closeSession) {
            String message = scanner.nextLine();
            try {
                if (closeSession) {
                    break;
                } else if (message.equals(COMMAND_BREAK)) {
                    closeSession = true;
                    sendMessage(message);
                    break;
                }
                if (!message.trim().isEmpty()) {
                    sendMessage(String.format("%s: %s", hostName, message));
                }
            } catch (IOException e) {
                System.err.println("Ошибка отправки сообщения от " + hostName);
                closeSession = true;
                break;
            }
        }
    }

    public void waitNetworkInput() {
        while (!closeSession) {
            String message = receiveMessage();
            if (message.equals(Network.COMMAND_BREAK)) {
                closeSession = true;
                System.out.println("Сеанс закрыт.");
                System.exit(0);
                break;
            }
            System.out.println(message);
        }
    }
}