package com.vasili_zlobin.clientchat;

import com.vasili_zlobin.clientchat.controllers.AuthController;
import com.vasili_zlobin.clientchat.controllers.ClientChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientChatApplication extends Application {
    private static ClientChatApplication mainInstance;

    private Stage chatStage;
    private Stage authStage;

    public ClientChatApplication() {
        super();
        if (mainInstance == null) {
            mainInstance = this;
        }
    }

    public static ClientChatApplication getInstance() {
        return mainInstance;
    }

    private void createAuthDialog(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ClientChatApplication.class.getResource("auth-template.fxml"));
        AnchorPane authDialogPanel = fxmlLoader.load();

        authStage = new Stage();
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.setScene(new Scene(authDialogPanel));

        AuthController controller = fxmlLoader.getController();
        controller.startHandlerAuth();

        authStage.showAndWait();
    }

    private ClientChatController createChatDialog(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ClientChatApplication.class.getResource("chat-template.fxml"));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        chatStage.setTitle("Java FX Application");
        chatStage.setScene(scene);

        ClientChatController controller = fxmlLoader.getController();
        controller.userList.getItems().addAll("user1", "user2", "user3");

        primaryStage.show();
        return controller;
    }

    private boolean connectToServer() {
        boolean result = Network.getInstance().connectToServer();
        if (!result) {
            showErrorDialog("Не удалось подключиться к серверу");
        }
        chatStage.setOnCloseRequest(windowEvent -> Network.getInstance().close());
        return result;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        chatStage = primaryStage;

        ClientChatController controller = createChatDialog(primaryStage);
        if (connectToServer()) {
            createAuthDialog(primaryStage);
            controller.startMessagesHandler();
        }
    }

    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Stage getChatStage() {
        return chatStage;
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public static void main(String[] args) {
        launch();
    }
}