package com.vasili_zlobin.clientchat.model;

import com.vasili_zlobin.chat.command.Command;

public interface ReadMessagesListener {
    void processReceivedCommand(Command command);
}
