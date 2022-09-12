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
    private ReadMessagesListener reconnectAuthListener;
    private boolean successAuth = false;
    private String lastLogin;
    private String lastPassword;

    @FXML
    public TextField nickField;

    @FXML
    public TextField loginField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button authButton;

    public Stage getAuthStage() {
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

        setLastLogin(login);
        setLastPassword(password);
        String nick = nickField.getText();
        nick = nick != null && !nick.isBlank() ? nick : null;
        sendAuthCommand(nick, login, password, getAuthStage());
    }

    public void sendAuthCommand(String nick, String login, String password, Stage stage) {
        try {
            Network.getInstance().sendCommand(Command.authCommand(login, password, nick));
        } catch (IOException e) {
            Dialogs.NetworkError.SEND_MESSAGE.show(stage);
            e.printStackTrace();
        }
    }

    public void setOnCloseWindowAuth() {
        ClientChatApplication application = ClientChatApplication.getInstance();
        application.getAuthStage().setOnCloseRequest(windowEvent -> {
            if (!isSuccessAuth()) {
                Network.getInstance().close();
                application.closeClientChatApplication();
            }
        });
    }

    public void startHandlerAuth() {
        commandListener = Network.getInstance().addReadMessagesListener(command -> {
            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    Platform.runLater(() -> {
                        setSuccessAuth(true);
                        ClientChatApplication.getInstance().switchToMainChatWindow(data.getUserName(), data.getHistory());
                    });
                    break;
                }
                case ERROR: {
                    setLastLogin(null);
                    setLastPassword(null);
                    ErrorCommandData data = (ErrorCommandData) command.getData();
                    Platform.runLater(() -> Dialogs.AuthError.INVALID_CREDENTIALS.show(getAuthStage(), data.getErrorMessage()));
                    break;
                }
            }
        });
    }

    public void setReconnectAuthListener() {
        reconnectAuthListener = Network.getInstance().addReadMessagesListener(command -> {
            switch (command.getType()) {
                case AUTH_OK: {
                    AuthOkCommandData data = (AuthOkCommandData) command.getData();
                    Platform.runLater(() -> {
                        setSuccessAuth(true);
                        Network.getInstance().removeReadMessagesListener(reconnectAuthListener);
                        ClientChatApplication.getInstance().getChatStage().setTitle(data.getUserName());
                    });
                    break;
                }
                case ERROR: {
                    ErrorCommandData data = (ErrorCommandData) command.getData();
                    Platform.runLater(() -> {
                        Network.getInstance().removeReadMessagesListener(reconnectAuthListener);
                        Dialogs.AuthError.INVALID_CREDENTIALS.show(getAuthStage(), data.getErrorMessage());
                        ClientChatApplication.getInstance().showAuthWindow();
                    });
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

    public void setSuccessAuth(boolean success) {
        this.successAuth = success;

    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastPassword() {
        return lastPassword;
    }

    public void setLastPassword(String lastPassword) {
        this.lastPassword = lastPassword;
    }
}
