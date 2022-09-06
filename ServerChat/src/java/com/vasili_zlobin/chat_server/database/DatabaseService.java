package com.vasili_zlobin.chat_server.database;

import java.sql.*;

public class DatabaseService {
    private static DatabaseService databaseInstance;
    private static Connection connection;

    private DatabaseService() {
        try {
            initUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseService getInstance() {
        if (databaseInstance == null) {
            databaseInstance = new DatabaseService();
        }
        return databaseInstance;
    }

    private void initUsers() throws SQLException {
        connect();
        if (getCountUsers() == 0) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (nick, login, password) VALUES (?, ?, ?);");
            connection.setAutoCommit(false);
            for (int i = 0; i < 5; i++) {
                statement.setString(1, "user" + i);
                statement.setString(2, "login" + i);
                statement.setString(3, "pass" + i);
                statement.execute();
            }
            connection.setAutoCommit(true);
        }
    }

    private int getCountUsers() throws SQLException {
        int result = 0;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM users;");
        if (rs.next()) {
            result = rs.getInt(1);
        }
        statement.close();
        return result;
    }

    public void connect() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:ServerChat/history.db3");
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    public String getUserName(String login, String password) throws SQLException {
        String result = null;
        if (connection != null) {
            PreparedStatement statement = connection.prepareStatement("SELECT nick FROM users u WHERE u.login = ? AND u.password = ?;");
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = rs.getString(1);
            }
        }
        return result;
    }

    public boolean changeUserName(String currentName, String nameNew) throws SQLException {
        boolean result = false;
        if (connection != null) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE nick = ?");
            statement.setString(1, nameNew);
            ResultSet rs = statement.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                statement = connection.prepareStatement("UPDATE users SET nick = ? WHERE nick = ?");
                statement.setString(1, nameNew);
                statement.setString(2, currentName);
                statement.execute();
                result = true;
            }
        }
        return result;
    }

    public boolean addNewUser(String nick, String login, String password) throws SQLException {
        boolean result = false;
        if (connection != null) {
            PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users WHERE nick = ? OR login = ?");
            statement.setString(1, nick);
            statement.setString(2, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                statement = connection.prepareStatement("INSERT INTO users (nick, login, password) VALUES (?, ?, ?)");
                statement.setString(1, nick);
                statement.setString(2, login);
                statement.setString(3, password);
                statement.execute();
                result = true;
            }
        }
        return result;
    }
}
