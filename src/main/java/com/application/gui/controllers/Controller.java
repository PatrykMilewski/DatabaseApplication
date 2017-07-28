package com.application.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Controller {
    abstract Object getResult();
    boolean resultsReady;
    Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public abstract void closeWindow();
    
    void setImageOnButton(Button button, String path, double width, double height) {
        ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(path)));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        
        button.setGraphic(imageView);
    }
    
    synchronized void waitForResults() throws InterruptedException {
        while (!resultsReady) {
            wait();
        }
    }
}
