package com.vasili_zlobin.lesson6;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConsoleChatServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(Network.SERVER_PORT)) {
            System.out.println("Сервер запущен, ожидаем подключения клиента...");
            Socket socket = serverSocket.accept();
            System.out.println("Клиент подключился");
            Network network = new Network(socket, "Сервер");
            Thread threadConsole = new Thread(network::waitConsoleInput);
            Thread threadNetwork = new Thread(network::waitNetworkInput);
            threadConsole.start();
            threadNetwork.start();
        } catch (IOException e) {
            System.err.println("Ошибка соединения с клиентом");
        }
    }
}
