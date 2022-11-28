package com.vasili_zlobin.chat_server;

import com.vasili_zlobin.chat.command.Command;
import com.vasili_zlobin.chat_server.authenticate.AuthInterface;
import com.vasili_zlobin.chat_server.authenticate.DatabaseAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerService {
    private static final int SERVER_PORT = 8189;
    private static ServerService serverInstance;
    private final AuthInterface authService;
    private final Map<String, ClientHandler> clients;
    private final Logger logger = LogManager.getLogger(ServerService.class);

    private ServerService() {
        this.clients = new HashMap<>();
        this.authService = DatabaseAuthService.getInstance();
    }

    private void waitAndProcess(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).handle();
            } catch (IOException e) {
                logger.error("Failed client's connection", e);
            }
        }
    }

    private void broadcastUserList() {
        String[] users = new String[clients.size()];
        clients.keySet().toArray(users);
        Arrays.sort(users);
        Command command = Command.updateUserListCommand(Arrays.asList(users));
        for (String client : clients.keySet()) {
            try {
                clients.get(client).sendCommand(command);
            } catch (IOException e) {
                logger.error(String.format("Failed to send user list for %s", client), e);
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

    public Logger getLogger() {
        return logger;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            logger.info("Chat server has started");
            authService.start();
            waitAndProcess(serverSocket);
        } catch (IOException e) {
            logger.error(String.format("Port %d is busy.", SERVER_PORT), e);
        }
    }

    public synchronized boolean isUserNameBusy(String name) {
        return clients.containsKey(name);
    }

    public synchronized void subscribe(ClientHandler client) {
        clients.put(client.getUserName(), client);
        broadcastUserList();
    }

    public synchronized void unsubscribe(ClientHandler client) {
        String name = client.getUserName();
        clients.remove(name);
        if (!clients.isEmpty()) {
            broadcastUserList();
        }
    }

    public synchronized void broadcastMessage(String message, String sender) throws IOException {
        for (String name : clients.keySet()) {
            if (!name.equals(sender)) {
                clients.get(name).sendMessage(sender, message);
            }
        }
    }

    public synchronized void sendPersonalMessage(String message, String sender, String receiver) throws IOException {
        if (clients.containsKey(receiver)) {
            clients.get(receiver).sendMessage(sender, message);
        }
    }

}