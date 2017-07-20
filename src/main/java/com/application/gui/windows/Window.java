package com.application.gui.windows;

import com.application.gui.controllers.Controller;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public abstract class Window {
    protected Controller controller;
    FXMLLoader fxmlLoader;
    Stage stage;
    
    public Controller getController() {
        return controller;
    }
}
