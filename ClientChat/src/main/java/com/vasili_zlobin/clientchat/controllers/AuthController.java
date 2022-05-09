package com.vasili_zlobin.clientchat.controllers;

import com.vasili_zlobin.clientchat.ClientChatApplication;
import com.vasili_zlobin.clientchat.Network;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AuthController {
    public static final String COMMAND_AUTH = "/auth";
    public static final String COMMAND_AUTH_OK = "/authok";

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button authButton;

    @FXML
    public void executeAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            ClientChatApplication.getInstance().showErrorDialog("Логин и пароль должны быть указаны");
            return;
        }

        String message = String.format("%s %s %s", COMMAND_AUTH, login, password);
        try {
            Network.getInstance().sendMessage(message);
        } catch (IOException e) {
            ClientChatApplication.getInstance().showErrorDialog("Ошибка передачи данных по сети");
            e.printStackTrace();
        }
    }

    public void startHandlerAuth() {
        Network.getInstance().receiveMessages(message -> {
            if (message.startsWith(COMMAND_AUTH_OK)) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> {
                    String[] parts = message.split(" ");
                    ClientChatApplication mainChat = ClientChatApplication.getInstance();
                    mainChat.getChatStage().setTitle(parts[1]);
                    mainChat.getAuthStage().close();
                });
            } else {
                Platform.runLater(() -> ClientChatApplication.getInstance().showErrorDialog(message));
            }
        });
    }
}
