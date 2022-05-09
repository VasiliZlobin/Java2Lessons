package com.vasili_zlobin.chat_server;

import com.vasili_zlobin.chat_server.authenticate.AuthInterface;
import com.vasili_zlobin.chat_server.authenticate.BaseAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerService {
    private static final int SERVER_PORT = 8189;
    private static ServerService serverInstance;
    private final AuthInterface authService;
    private final Map<String, ClientHandler> clients;

    private ServerService() {
        clients = new HashMap<>();
        this.authService = BaseAuthService.getInstance();
    }

    private void waitAndProcess(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).handle();
            } catch (IOException e) {
                System.err.println("Failed client's connection");
                e.printStackTrace();
            }
        }
    }

    public static ServerService getInstance() {
        if (serverInstance == null) {
            serverInstance = new ServerService();
        }
        return serverInstance;
    }

    public AuthInterface getAuthService() {
        return authService;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Chat server has started");
            authService.start();
            waitAndProcess(serverSocket);
        } catch (IOException e) {
            System.err.printf("Port %d is busy.", SERVER_PORT);
        }
    }

    public synchronized boolean subscribe(ClientHandler client) {
        boolean result = false;
        String name = client.getName();
        if (!clients.containsKey(name)) {
            clients.put(name, client);
            result = true;
        }
        return result;
    }

    public synchronized void unsubscribe(ClientHandler client) {
        String name = client.getName();
        clients.remove(name);
    }

    public synchronized void broadcastMessage(String message, String senderName) throws IOException {
        for (String name : clients.keySet()) {
            if (!name.equals(senderName)) {
                clients.get(name).sendMessage(message);
            }
        }
    }

    public synchronized void sendPersonalMessage(String message, String sourceName) throws IOException {
        if (clients.containsKey(sourceName)) {
            clients.get(sourceName).sendMessage(message);
        }
    }
}