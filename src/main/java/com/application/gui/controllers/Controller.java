package com.application.gui.controllers;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public abstract class Controller {
    abstract Object getResult();
    boolean resultsReady;
    Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void closeWindow() {
        stage.close();
    }
    
    void setImageOnButton(Button button, String path, double width, double height) {
        ImageView imageView = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(path)));
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        
        button.setGraphic(imageView);
    }
}
