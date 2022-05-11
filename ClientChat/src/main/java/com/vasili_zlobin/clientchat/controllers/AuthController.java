package com.vasili_zlobin.clientchat.controllers;

import com.vasili_zlobin.chat.command.Command;
import com.vasili_zlobin.chat.command.commands.AuthOkCommandData;
import com.vasili_zlobin.chat.command.commands.ErrorCommandData;
import com.vasili_zlobin.clientchat.ClientChatApplication;
import com.vasili_zlobin.clientchat.dialogs.Dialogs;
import com.vasili_zlobin.clientchat.model.Network;
import com.vasili_zlobin.clientchat.model.ReadMessagesListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
    private ReadMessagesListener commandListener;
    private boolean successAuth = false;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button authButton;

    private void setOnCloseWindowAuth() {
        ClientChatApplication application = ClientChatApplication.getInstance();
        application.getAuthStage().setOnCloseRequest(windowEvent -> {
            if (!isSuccessAuth()) {
                Network.getInstance().close();
                application.closeClientChatApplication();
            }
        });
    }

    private Stage getAuthStage() {
        return ClientChatApplication.getInstance().getAuthStage();
    }

    @FXML
    public void executeAuth() {
        String login = loginField.getText();
        String password = passwordField.getText();

        if (login == null || password == null || login.isBlank() || password.isBlank()) {
            Dialogs.AuthError.EMPTY_CREDENTIALS.show(getAuthStage());
            return;
        }

        if (!isConnectedToServer()) {
            Dialogs.NetworkError.SERVER_CONNECT.show(getAuthStage());
            return;
        }

        try {
            Network.getInstance().sendCommand(Command.authCommand(login, password));
        } catch (IOException e) {
            Dialogs.NetworkError.SEND_MESSAGE.show(getAuthStage());
            e.printStackTrace();
        }
    }

    public void startHandlerAuth() {
        setOnCloseWindowAuth();
        commandListener = Network.getInstance().addReadMessagesListener(command -> {
            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    Platform.runLater(() -> {
                        setSuccessAuth();
                        ClientChatApplication.getInstance().switchToMainChatWindow(data.getUserName());
                    });
                    break;
                }
                case ERROR: {
                    ErrorCommandData data = (ErrorCommandData) command.getData();
                    Platform.runLater(() -> Dialogs.AuthError.INVALID_CREDENTIALS.show(getAuthStage(), data.getErrorMessage()));
                    break;
                }
            }
        });
    }

    public boolean isConnectedToServer() {
        Network network = Network.getInstance();
        return (network.isConnected() || network.connectToServer());
    }

    public void close() {
        Network.getInstance().removeReadMessagesListener(commandListener);
    }

    public boolean isSuccessAuth() {
        return successAuth;
    }

    private void setSuccessAuth() {
        this.successAuth = true;
    }
}
