package com.vasili_zlobin.clientchat.controllers;

import com.vasili_zlobin.chat.command.commands.ReceivedMessageCommandData;
import com.vasili_zlobin.chat.command.commands.UpdateUserListCommandData;
import com.vasili_zlobin.clientchat.ClientChatApplication;
import com.vasili_zlobin.clientchat.dialogs.Dialogs;
import com.vasili_zlobin.clientchat.model.Network;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class ClientChatController {

    private String userName;

    @FXML
    public TextField messageField;

    @FXML
    public Button sendMessageButton;

    @FXML
    public TextArea messageTextArea;

    @FXML
    public ListView<String> userList;

    public void appendMessageToChat(String sender, String message, String prefix) {
        if (message == null || message.isEmpty()) {
            return;
        }
        if (prefix != null) {
            messageTextArea.appendText(prefix);
            messageTextArea.appendText(System.lineSeparator());
        }
        if (sender != null) {
            messageTextArea.appendText(sender + ": ");
        }
        messageTextArea.appendText(message);
        messageTextArea.appendText(System.lineSeparator());
        messageTextArea.appendText(System.lineSeparator());
    }

    private Stage getChatStage() {
        return ClientChatApplication.getInstance().getChatStage();
    }

    @FXML
    public void addMessageText() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            String receiver = null;

            if (!userList.getSelectionModel().isEmpty()) {
                receiver = userList.getSelectionModel().getSelectedItem();
            }

            if (receiver == null || !receiver.equals(getUserName())) {
                try {
                    Network.getInstance().sendMessage(message, receiver);
                    appendMessageToChat("Я", message, DateFormat.getDateTimeInstance().format(new Date()));
                } catch (IOException e) {
                    Dialogs.NetworkError.SEND_MESSAGE.show(getChatStage());
                    e.printStackTrace();
                }
            }
        }
        messageField.clear();
        messageField.setFocusTraversable(true);
        Platform.runLater(() -> messageField.requestFocus());
    }

    public void startMessagesHandler() {
        getChatStage().setOnCloseRequest(windowEvent -> Network.getInstance().close());
        Network.getInstance().addReadMessagesListener(command -> {
            switch (command.getType()) {
                case RECEIVED_MESSAGE: {
                    ReceivedMessageCommandData data = (ReceivedMessageCommandData) command.getData();
                    appendMessageToChat(data.getSender(), data.getMessage(), DateFormat.getDateTimeInstance().format(new Date()));
                    break;
                }
                case UPDATE_USER_LIST: {
                    UpdateUserListCommandData data = (UpdateUserListCommandData) command.getData();
                    Platform.runLater(() -> userList.setItems(FXCollections.observableArrayList(data.getUsers())));
                    break;
                }
            }
        });
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}