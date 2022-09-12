package com.vasili_zlobin.chat.command.commands;

import java.io.Serializable;

public class AuthOkCommandData implements Serializable {
    private final String userName;
    private final String history;

    public AuthOkCommandData(String userName) {
        this.userName = userName;
        this.history = null;
    }

    public AuthOkCommandData(String userName, String history) {
        this.userName = userName;
        this.history = history;
    }

    public String getUserName() {
        return userName;
    }

    public String getHistory() {
        return history;
    }
}
