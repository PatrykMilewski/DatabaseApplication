package com.application.gui.windows;

import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

public class MainWindow extends Application {
    
    private static Logger log = LoggerFactory.getLogger(MainWindow.class.getCanonicalName());
    
    private final URL fxmlUrl = getClass().getResource("/fxml/mainWindow.fxml");
    
    private FXMLLoader loader;
    private MainWindowController controller;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        controller = loader.getController();
        primaryStage.setScene(new Scene(root));
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(200);
        primaryStage.show();
    }
    
    
    @Override
    public void stop() {
        controller.closeApplication();
        System.exit(0);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
