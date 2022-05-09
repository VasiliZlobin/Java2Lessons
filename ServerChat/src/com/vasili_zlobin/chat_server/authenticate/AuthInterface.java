package com.vasili_zlobin.chat_server.authenticate;

public interface AuthInterface {
    void start();
    void stop();
    String getUsernameByLoginPassword(String login, String password);
}
