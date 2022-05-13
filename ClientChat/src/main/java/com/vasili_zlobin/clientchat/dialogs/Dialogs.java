package com.vasili_zlobin.clientchat.dialogs;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class Dialogs {

    public enum AuthError {
        EMPTY_CREDENTIALS("Логин и пароль должны быть указаны"),
        INVALID_CREDENTIALS("Логин или пароль указаны некорректно");

        private static final String TYPE = "Ошибка авторизации";
        private static final String TITLE = TYPE;

        private final String message;

        AuthError(String message) {
            this.message = message;
        }

        public void show(Stage owner) {
            showDialog(Alert.AlertType.ERROR, owner, TITLE, TYPE, message);
        }

        public void show(Stage owner, String overrideMessage) {
            showDialog(Alert.AlertType.ERROR, owner, TITLE, TYPE, overrideMessage);
        }
    }

    public enum NetworkError {
        SERVER_CONNECT("Не удалось соединиться с сервером"),
        SEND_MESSAGE("Не удалось отправить сообщение серверу"),
        BREAK_CONNECT("Потеряно соединение с сервером");

        private static final String TYPE = "Ошибка передачи данных по сети";
        private static final String TITLE = "Сетевая ошибка";

        private final String message;

        NetworkError(String message) {
            this.message = message;
        }

        public void show(Stage owner) {
            showDialog(Alert.AlertType.ERROR, owner, TITLE, TYPE, message);
        }
    }

    private static void showDialog(Alert.AlertType dialogType, Stage owner, String title, String type, String message) {
        Alert alert = new Alert(dialogType);
        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
