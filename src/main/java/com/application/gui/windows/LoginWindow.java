package com.application.gui.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginWindow extends Window {
    public LoginWindow() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/loginWindow.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage = new Stage();
        stage.setTitle("");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(root));
        setIcon();
        controller.setStage(stage);
        stage.setOnCloseRequest(event -> controller.closeWindow());
        stage.show();
    }
}
