package com.application.gui.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginWindow {
    private Stage stage;
    
    public LoginWindow() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/loginWindow.fxml"));
        stage = new Stage();
        stage.setTitle("");
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    public Stage getStage() {
        return stage;
    }
}
