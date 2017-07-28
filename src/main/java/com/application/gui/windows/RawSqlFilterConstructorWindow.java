package com.application.gui.windows;

import com.application.gui.controllers.Controller;
import com.application.gui.controllers.RawSQLFilterConstructorWindowController;
import com.application.gui.controllers.SQLQueryWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class RawSqlFilterConstructorWindow extends Window {
    
    public RawSqlFilterConstructorWindow(Connection connection) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader()
                .getResource("fxml/rawSqlFilterConstructorWindow.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        ((RawSQLFilterConstructorWindowController) controller).setConnection(connection);
        stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Zaawansowany konstruktor filtru.");
        setIcon();
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> controller.closeWindow());
        stage.show();
    }
}
