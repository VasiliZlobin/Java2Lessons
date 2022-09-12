package com.vasili_zlobin.clientchat;

import com.vasili_zlobin.clientchat.controllers.AuthController;
import com.vasili_zlobin.clientchat.controllers.ClientChatController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientChatApplication extends Application {
    private static ClientChatApplication mainInstance;

    private Stage chatStage;
    private Stage authStage;
    private FXMLLoader chatWindowLoader;
    private FXMLLoader authWindowLoader;

    public ClientChatApplication() {
        super();
        if (mainInstance == null) {
            mainInstance = this;
        }
    }

    public static ClientChatApplication getInstance() {
        return mainInstance;
    }

    private void initAuthWindow() throws IOException {
        authWindowLoader = new FXMLLoader();
        authWindowLoader.setLocation(ClientChatApplication.class.getResource("auth-template.fxml"));
        AnchorPane authDialogPanel = authWindowLoader.load();

        authStage = new Stage();
        authStage.initOwner(chatStage);
        authStage.setTitle("Вход в сетевой чат");
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setScene(new Scene(authDialogPanel));
    }

    private void initChatWindow() throws IOException {
        chatWindowLoader = new FXMLLoader();
        chatWindowLoader.setLocation(ClientChatApplication.class.getResource("chat-template.fxml"));

        Parent root = chatWindowLoader.load();
        Scene scene = new Scene(root);
        chatStage.setScene(scene);
    }

    private void initViews() throws IOException {
        initChatWindow();
        initAuthWindow();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        chatStage = primaryStage;
        initViews();
        showAuthWindow();
    }

    public void showAuthWindow() {
        getChatStage().close();
        AuthController authController = getAuthController();
        authController.setOnCloseWindowAuth();
        authController.startHandlerAuth();
        authStage.showAndWait();
    }

    public void switchToMainChatWindow(String userName, String history) {
        chatStage.setTitle(userName);
        ClientChatController controller = getChatController();
        controller.setUserName(userName);
        controller.appendMessageToChat(null, history, null);
        controller.startMessagesHandler();
        getAuthController().close();
        authStage.close();
        chatStage.show();
    }

    public Stage getChatStage() {
        return chatStage;
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public ClientChatController getChatController() {
        return chatWindowLoader.getController();
    }

    public AuthController getAuthController() {
        return authWindowLoader.getController();
    }

    public void closeClientChatApplication() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch();
    }
}