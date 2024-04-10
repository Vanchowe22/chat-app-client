package com.example.chatapp;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.Arrays;
import java.util.Objects;

public class Helpers {
    public void updateList(String message, ListView<String> userList) {
        String[] users = message.substring(2).split("\\|");
        Platform.runLater(() -> userList.setItems(FXCollections.observableArrayList(Arrays.asList(users))));
    }

    public void getNewMessages(String message, StringBuilder messages, TextArea messageArea) {
        messages.append(message.substring(2)).append("\n");
        Platform.runLater(() -> {
            messageArea.setText(messages.toString());
            messageArea.positionCaret(messageArea.getLength());
        });
    }

    public void loadAllMessages(String message, StringBuilder messages, TextArea messageArea) {
        String[] oldMessages = message.substring(2).split("\\|");
        for (String text : oldMessages) {
            messages.append(text).append("\n");
        }
        Platform.runLater(() -> {
            messageArea.setText(messages.toString());
            messageArea.positionCaret(messageArea.getLength());
        });
    }

    public boolean leavingChat(String message, StringBuilder messages, String nickname) {
        messages.append(message.substring(2)).append("\n");
        String name = message.split(" ")[0].substring(2);
        return Objects.equals(nickname, name);
    }

    public void alert(boolean isNickname, String message) {
        Platform.runLater(() -> ChatApplication.showAlert(isNickname, message));
    }
}
