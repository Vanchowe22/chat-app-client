package com.example.chatapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatApplication extends Application {
    private String nickname;
    private Controller controller;
    private ListView<String> userList;
    private static Stage primaryStage;
    private static Scene connectScene;
    private TextArea chatDisplay;
    private TextField messageField;
    private TextField hostField;
    private TextField portField;
    private Label hostLabel;
    private Label portLabel;
    private Button sendButton;
    private Button languageButton;
    private Button connectButton;
    private Button endSessionButton;
    private boolean isEnglish = true;
    private Label name;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chat Client");

        languageButton = new Button("Bulgarian");
        TextField nicknameField = new TextField();
        connectButton = new Button("Connect");
        VBox connectLayout = new VBox(10);
        name = new Label("Enter name:");
        hostLabel = new Label("Enter host:");
        portLabel = new Label("Enter port:");
        hostField = new TextField();
        portField = new TextField();

        connectLayout.getChildren().addAll(name, nicknameField, hostLabel, hostField, portLabel, portField, connectButton, languageButton);
        connectScene = new Scene(connectLayout, 300, 300);


        languageButton.setOnAction(event -> {
            if (isEnglish) {
                isEnglish = false;
            } else {
                isEnglish = true;
            }
            this.changeLanguage();
        });

        BorderPane root = new BorderPane();

        userList = new ListView<>();
        userList.setPrefWidth(150);

        chatDisplay = new TextArea();
        chatDisplay.setEditable(false);

        messageField = new TextField();
        messageField.setPromptText("Type your message...");
        messageField.setPrefHeight(50);

        sendButton = new Button("Send");
        endSessionButton = new Button("End Session");

        VBox messageInputBox = new VBox(10);
        messageInputBox.getChildren().addAll(messageField, sendButton, endSessionButton);

        root.setLeft(userList);
        root.setCenter(chatDisplay);
        root.setBottom(messageInputBox);

        root.setPadding(new Insets(10));

        Scene chat = new Scene(root, 600, 400);

        endSessionButton.setOnAction(event -> controller.endSession(connectScene, primaryStage, nickname));
        sendButton.setOnAction(event -> {
            controller.sendMessage(messageField.getText(), nickname);
            messageField.clear();
        });
        connectButton.setOnAction(event -> {
            try {
                String nickname = nicknameField.getText().trim();
                if (!isValidNickname(nickname)) {
                    showAlert(true, "Containing forbidden symbols");
                    return;
                }
                if (hostField.getText().isEmpty()) {
                    showAlert(true, "Write valid host");
                }
                int port = Integer.parseInt(portField.getText());
                this.nickname = nickname;
                controller = new Controller();
                controller.connectToServer(nickname, hostField.getText(), port);
                primaryStage.setScene(chat);
                controller.receiveMessages(chatDisplay, userList);
            } catch (Exception e) {
                showAlert(true, "Should be a number");
            }
        });

        primaryStage.setOnCloseRequest(event -> {
            if (controller != null) {
                controller.close();
            }
        });

        primaryStage.setTitle("Chat Application");
        primaryStage.setScene(connectScene);
        primaryStage.show();
    }


    private boolean isValidNickname(String nickname) {
        if ((nickname.contains("[") || nickname.contains("]")) && nickname.isEmpty()) {
            return false;
        }
        return true;
    }

    public static void showAlert(boolean invalidNickname, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(text);
        if (invalidNickname) {
            alert.setOnCloseRequest(event -> {
                primaryStage.setScene(connectScene);
            });
        }
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

    private void changeLanguage() {
        if (!isEnglish) {
            sendButton.setText("Изпрати");
            languageButton.setText("Английски");
            connectButton.setText("Свържи се");
            hostLabel.setText("Хост:");
            portLabel.setText("Порт:");
            endSessionButton.setText("Прекрати сесията");
            name.setText("Въведи име:");
            messageField.setPromptText("Въведи съобщение...");
        } else {
            sendButton.setText("Send");
            languageButton.setText("Bulgarian");
            connectButton.setText("Connect");
            hostLabel.setText("Enter host:");
            portLabel.setText("Enter port:");
            endSessionButton.setText("End Session");
            name.setText("Enter name:");
            messageField.setPromptText("Type your message...");
        }
    }
}