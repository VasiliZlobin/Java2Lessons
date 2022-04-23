package com.vasili_zlobin.clientchat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.text.DateFormat;
import java.util.Date;

public class ClientChatController {

    @FXML
    public TextField messageField;

    @FXML
    public Button sendMessageButton;

    @FXML
    public TextArea messageTextArea;

    @FXML
    public ListView userList;

    public void addMessageText(ActionEvent actionEvent) {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            messageTextArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
            messageTextArea.appendText(System.lineSeparator());
            if (!userList.getSelectionModel().isEmpty()) {
                String sender = userList.getSelectionModel().getSelectedItem().toString();
                messageTextArea.appendText(String.format("%s: ", sender));
            }
            messageTextArea.appendText(message);
            messageTextArea.appendText(System.lineSeparator());
            messageTextArea.appendText(System.lineSeparator());
            messageField.setFocusTraversable(true);
        }
        messageField.clear();
        Platform.runLater(() -> messageField.requestFocus());
    }
}