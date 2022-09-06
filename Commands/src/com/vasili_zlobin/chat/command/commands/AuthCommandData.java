package com.vasili_zlobin.chat.command.commands;

import java.io.Serializable;

public class AuthCommandData implements Serializable {
    private final String login;
    private final String password;
    private final String nickname;

    public AuthCommandData(String login, String password, String nickname) {
        this.login = login;
        this.password = password;
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}
