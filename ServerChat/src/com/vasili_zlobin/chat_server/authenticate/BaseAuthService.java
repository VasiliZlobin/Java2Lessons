package com.vasili_zlobin.chat_server.authenticate;

import java.util.HashMap;
import java.util.Map;

public class BaseAuthService implements AuthInterface {
    private static class User {
        private final String userName;
        private final String login;
        private final String password;

        private User(String userName, String login, String password) {
            this.userName = userName;
            this.login = login;
            this.password = password;
        }
    }

    private static BaseAuthService authInstance;
    private final Map<String, User> knownUsers = new HashMap<>();

    private BaseAuthService() {
        for (int i = 0; i < 5; i++) {
            User newUser = new User("user" + i, "login" + i, "pass" + i);
            knownUsers.put(newUser.login, newUser);
        }
    }

    public static BaseAuthService getInstance() {
        if (authInstance == null) {
            authInstance = new BaseAuthService();
        }
        return authInstance;
    }

    @Override
    public void start() {
        System.out.println("Authenticate server has started");
    }

    @Override
    public void stop() {
        System.out.println("Authenticate server has stopped");
    }

    @Override
    public String getUsernameByLoginPassword(String login, String password) {
        User user = knownUsers.get(login);
        if (user != null && user.password.equals(password)) {
            return user.userName;
        }
        return null;
    }
}
