package com.vasili_zlobin.lesson6;

import java.io.IOException;
import java.net.Socket;

public class ConsoleChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket(Network.SERVER_ADDRESS, Network.SERVER_PORT)) {
            Network network = new Network(socket, "Клиент");
            System.out.println("Подключились к серверу");
            Thread threadConsole = new Thread(network::waitConsoleInput);
            Thread threadNetwork = new Thread(network::waitNetworkInput);
            threadConsole.start();
            threadNetwork.start();
            threadNetwork.join();
            threadConsole.join();
            network.closeConnection();
        } catch (IOException e) {
            System.err.println("Ошибка соединения с сервером");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
