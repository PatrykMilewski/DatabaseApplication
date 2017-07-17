package com.application.gui.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginWindow extends Window {
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
}
