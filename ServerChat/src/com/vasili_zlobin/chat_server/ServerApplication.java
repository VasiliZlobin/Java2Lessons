package com.vasili_zlobin.chat_server;

public class ServerApplication {
    public static void main(String[] args) {
        ServerService.getInstance().start();
    }
}
