package com.application.gui.windows;


import com.application.gui.controllers.FilterConstructorController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class FilterConstructorWindow extends Window {
    
    public FilterConstructorWindow() throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader()
                .getResource("fxml/filterConstructorWindow.fxml"));
        
        Parent root = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage = new Stage();
        controller.setStage(stage);
        stage.setTitle("Zaawansowany konstruktor filtru.");
        setIcon();
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> controller.closeWindow());
        stage.show();
    }
}
