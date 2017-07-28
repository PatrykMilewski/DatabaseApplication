package com.application.gui.windows;


import com.application.gui.controllers.FilterConstructorWindowController;
import com.application.gui.controllers.RawSQLFilterConstructorWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class FilterConstructorWindow extends Window {
    
    public FilterConstructorWindow(Connection connection) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader()
                .getResource("fxml/filterConstructorWindow.fxml"));
        
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        ((FilterConstructorWindowController) controller).setConnection(connection);
        stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Zaawansowany konstruktor filtru.");
        setIcon();
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> controller.closeWindow());
        stage.show();
    }
}
