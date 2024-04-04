package com.example.chatapp;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class Controller {
    private Socket socket;
    private BufferedReader inputStream;
    private PrintWriter outputStream;
    private final Helpers helpers = new Helpers();
    private String nickname;
    private final Logger logger = new Logger();

    public void connectToServer(String nickname) {
        try {
            this.nickname = nickname;
            socket = new Socket("localhost", 8000);
            outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.println("C:" + nickname);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void endSession(Scene connectScene, Stage primaryStage, String nickname) {
        try {
            logger.info("L:" + nickname);
            outputStream.println("L:" + nickname);
            primaryStage.setScene(connectScene);
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    public void sendMessage(String text, String nickname) {
        String textString = text;
        try {
            if (text.length() > 200) {
                logger.warn("Too many symbols");
                throw new Exception("Too many symbols");
            } else if (text.isEmpty()) {
                logger.info("Trying to send empty message");
                return;
            }
            outputStream.println("T:" + nickname + ": " + textString);
        } catch (Exception e) {
            logger.severe(e.getMessage());
            HelloApplication.showAlert(false, e.getMessage());
        }
    }

    public void receiveMessages(TextArea messageArea, ListView<String> userList) {
        new Thread(() -> {
            try {
                inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                StringBuilder messages = new StringBuilder();
                String message;
                while ((message = inputStream.readLine()) != null) {
                    if (message.startsWith("U")) {
                        logger.info(message);
                        helpers.updateList(message, userList);
                    } else if (message.startsWith("T") || message.startsWith("J")) {
                        logger.info(message);
                        helpers.getNewMessages(message, messages, messageArea);
                    } else if (message.startsWith("L")) {
                        logger.info(message);
                        if (!helpers.leavingChat(message, messages, nickname)) {
                            Platform.runLater(() -> messageArea.setText(messages.toString()));
                        } else {
                            break;
                        }
                    } else if (message.startsWith("N")) {
                        logger.warn(message);
                        throw new AlreadyHereException(message.substring(2));
                    } else if (message.startsWith("A")) {
                        logger.info(message);
                        helpers.loadAllMessages(message, messages, messageArea);
                    }
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
            } catch (AlreadyHereException e) {
                Platform.runLater(() -> HelloApplication.showAlert(true, e.getMessage()));
            } finally {
                try {
                    socket.close();
                    inputStream.close();
                    outputStream.close();
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
            }
        }).start();
    }

    public void close() {
        logger.info("L:" + nickname);
        outputStream.println("L:" + nickname);
    }
}
