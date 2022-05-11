package com.vasili_zlobin.chat.command.commands;

import java.io.Serializable;

public class ReceivedMessageCommandData implements Serializable {
    private final String sender;
    private final String message;

    public ReceivedMessageCommandData(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
