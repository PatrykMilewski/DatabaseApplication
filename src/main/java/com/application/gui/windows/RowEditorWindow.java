package com.application.gui.windows;

import com.application.database.sql.DatabaseRow;
import com.application.gui.controllers.RowEditorController;
import com.application.gui.controllers.SQLQueryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;

public class RowEditorWindow extends Window {
    
    public RowEditorWindow(String windowName, Connection connection, DatabaseRow databaseRow) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/rowEditorWindow.fxml"));
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage = new Stage();
        controller.setStage(stage);
        ((RowEditorController) controller).setConnection(connection);
        ((RowEditorController) controller).setDatabaseRow(databaseRow);
        ((RowEditorController) controller).loadColumnsList();
        stage.setTitle(windowName);
        stage.initStyle(StageStyle.UNIFIED);
        setIcon();
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> controller.closeWindow());
        stage.show();
        ((RowEditorController) controller).initializeWindow();
    }
}
