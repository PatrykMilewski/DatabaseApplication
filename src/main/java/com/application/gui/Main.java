package com.application.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

public class Main extends Application {
    
    private static Logger log = Logger.getLogger(Main.class.getCanonicalName());
    private final URL fxmlUrl = getClass().getResource("/fxml/mainWindow.fxml");
    
    protected static boolean debug = false;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(fxmlUrl);
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
