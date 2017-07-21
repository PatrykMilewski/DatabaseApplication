package com.application.gui.windows;

import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Logger;

public class MainWindow extends Application {
    
    private static Logger log = LoggerFactory.getLogger(MainWindow.class.getCanonicalName());
    
    private final URL fxmlUrl = getClass().getResource("/fxml/mainWindow.fxml");
    
    private MainWindowController controller;
    private Stage stage;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.setTitle("Baza danych");
        stage.setMinWidth(400);
        stage.setMinHeight(200);
        setIcon();
        stage.show();
    }
    
    
    @Override
    public void stop() {
        controller.closeApplication();
        System.exit(0);
    }
    
    private void setIcon() {
        stage.getIcons().add(new Image("images/logo.png"));
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
