package com.vasili_zlobin.clientchat.controllers;

import com.vasili_zlobin.clientchat.Network;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ClientChatController {

    private static final String COMMAND_PERSONAL = "/w";

    @FXML
    public TextField messageField;

    @FXML
    public Button sendMessageButton;

    @FXML
    public TextArea messageTextArea;

    @FXML
    public ListView userList;

    private void appendMessageToChat(String sender, String message) {
        messageTextArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
        messageTextArea.appendText(System.lineSeparator());
        if (sender != null) {
            messageTextArea.appendText(sender + ": ");
        }
        messageTextArea.appendText(message);
        messageTextArea.appendText(System.lineSeparator());
        messageTextArea.appendText(System.lineSeparator());
    }

    @FXML
    public void addMessageText() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            String receiver = null;
            if (!userList.getSelectionModel().isEmpty()) {
                receiver = userList.getSelectionModel().getSelectedItem().toString();
            }
            String forSend = receiver == null ? message : String.format("%s %s %s", COMMAND_PERSONAL, receiver, message);
            try {
                Network.getInstance().sendMessage(forSend);
            } catch (IOException e) {
                System.err.println("Ошибка отправки сообщения");
                e.printStackTrace();
            }
            appendMessageToChat("Я", message);
        }
        messageField.clear();
        messageField.setFocusTraversable(true);
        Platform.runLater(() -> messageField.requestFocus());
    }

    public void startMessagesHandler() {
        Network.getInstance().receiveMessages(message -> appendMessageToChat("Server", message));
    }
}