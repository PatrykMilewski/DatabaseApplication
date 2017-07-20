package com.application.gui.windows;

import com.application.gui.controllers.SQLQueryWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class SQLQueryWindow extends Window {
    public SQLQueryWindow(Connection connection) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/sqlQueryWindow.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        ((SQLQueryWindowController) controller).setConnection(connection);
        stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
