package com.vasili_zlobin.chat_server.authenticate;

import com.vasili_zlobin.chat_server.database.DatabaseService;

import java.sql.SQLException;

public class DatabaseAuthService implements AuthInterface {
    private static DatabaseAuthService authInstance;
    private static DatabaseService databaseService;

    private DatabaseAuthService() {
    }

    public static DatabaseAuthService getInstance() {
        if (authInstance == null) {
            authInstance = new DatabaseAuthService();
        }
        return authInstance;
    }

    @Override
    public void start() {
        databaseService = DatabaseService.getInstance();
        System.out.println("Database authenticate server has started");
    }

    @Override
    public void stop() {
        try {
            databaseService.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Database authenticate server has stopped");
    }

    @Override
    public String getUsernameByLoginPassword(String login, String password) {
        try {
            return DatabaseService.getInstance().getUserName(login, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
