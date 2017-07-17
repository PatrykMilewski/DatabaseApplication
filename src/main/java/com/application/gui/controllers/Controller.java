package com.application.gui.controllers;

import javafx.stage.Stage;

public abstract class Controller {
    abstract Object getResult();
    boolean resultsReady;
    protected Stage stage;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void exit() {
        stage.close();
    }
}
