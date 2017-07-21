package com.application.gui.elements.infobox;

import com.application.gui.abstracts.factories.LoggerFactory;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogBox extends Thread {
    
    private final static String LEVEL_COLORS[] = new String[3];
    private static Logger log;

    static {
        LEVEL_COLORS[0] = "red";
        LEVEL_COLORS[1] = "black";
        LEVEL_COLORS[2] = "black";
        log = LoggerFactory.getLogger(LogBox.class.getCanonicalName());
    }
    
    private Queue<SingleLog> logQueue = new LinkedBlockingQueue<>();
    
    private Label logLabel;
    
    public LogBox(Label logLabel) {
        this.logLabel = logLabel;
    }
    
    @Override
    synchronized public void run() {
        SingleLog log;
        
        while(true) {
            if (logQueue.size() == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    LogBox.log.log(Level.WARNING, "Logger interrupted, closing it's thread.");
                    return;
                }
            }
            else {
                log = logQueue.remove();
                handleLog(log);
            }
        }
    }
    
    private void addLog(SingleLog log) {
        logQueue.add(log);
    }
    
    public void addLog(String text, Level level) {
        addLog(new SingleLog(text, level));
        synchronized (this) {
            notifyAll();
        }
    }
    
    private void handleLog(SingleLog log) {
        Level type = log.getType();
        LogBox.log.log(Level.INFO, "Adding a new log with level " + log.getType().toString() + ".");
        if (type == Level.SEVERE)
            Platform.runLater(() -> changeLabel(log.getText(), LEVEL_COLORS[0]));
        else if (type == Level.WARNING)
            Platform.runLater(() -> changeLabel(log.getText(), LEVEL_COLORS[1]));
        else
            Platform.runLater(() -> changeLabel(log.getText(), LEVEL_COLORS[2]));
    }
    
    private void changeLabel(String text, String color) {
        synchronized (this) {
            logLabel.setText(text);
            logLabel.setStyle("-fx-text-fill: " + color);
        }
    }
}
