package com.application.gui.windows;

import com.application.gui.controllers.Controller;
import com.application.gui.controllers.LoginWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginWindow {
    private Stage stage;
    private LoginWindowController controller;
    
    public LoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/loginWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage = new Stage();
        stage.setTitle("");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        controller.setStage(stage);
        stage.show();
    }
    
    public Stage getStage() {
        return stage;
    }
    
    public Controller getController() {
        return controller;
    }
}
