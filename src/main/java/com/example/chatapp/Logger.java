package com.example.chatapp;

import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger clientLogger = java.util.logging.Logger.getLogger("ClientLogger");
    private FileHandler clientFileHandler;
    private SimpleFormatter formatter;

    public Logger() {
        try {
            clientFileHandler = new FileHandler("client.log");
            formatter = new SimpleFormatter();
            clientFileHandler.setFormatter(formatter);
            clientLogger.addHandler(clientFileHandler);
        } catch (Exception e) {
            clientLogger.info(e.getMessage());
        }
    }

    public void info(String text) {
        clientLogger.info(text);
    }

    public void warn(String text) {
        clientLogger.warning(text);
    }

    public void severe(String text) {
        clientLogger.severe(text);
    }
}
