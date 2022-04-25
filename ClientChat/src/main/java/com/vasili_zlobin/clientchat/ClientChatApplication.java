package com.vasili_zlobin.clientchat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientChatApplication.class.getResource("chat-template.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Java FX Application");
        stage.setScene(scene);

        ClientChatController controller = fxmlLoader.getController();
        controller.userList.getItems().addAll("user1", "user2", "user3");

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}