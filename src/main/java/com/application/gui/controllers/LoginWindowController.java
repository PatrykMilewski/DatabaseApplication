package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import com.application.gui.abstracts.factories.LoggerFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class.getCanonicalName());
    
    private MySQLConnection databaseConnection;
    private Stage stage;
    private String baseAndServerName = "localhost:testowa", username = "test", password = "982563";
    
    private Boolean imageCheckboxesStances[] = new Boolean[2];
    
    private boolean loginCancelled = false;
    
    @FXML
    private TextField baseAndServerNameField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Image hostImage;
    
    @FXML
    private Image userImage;
    
    public LoginWindowController() {
        resultsReady = false;
        imageCheckboxesStances[0] = false;
        imageCheckboxesStances[1] = false;
    }
    
    @FXML
    public void baseAndHostNameEntered() {
        baseAndServerName = baseAndServerNameField.getText();
    }
    
    @FXML
    public void userNameEntered() {
        username = userNameField.getText();
    }
    
    @FXML
    public void passwordEntered() {
        password = passwordField.getText();
    }
    
    @FXML
    synchronized public void login() {
        if (baseAndServerName.length() != 0 && username.length() != 0 && password.length() != 0) {
            String hostAndBase[] = baseAndServerName.split(":");
            resultsReady = false;
            databaseConnection = new MySQLConnection(username, password, hostAndBase[0], hostAndBase[1]);
            try {
                databaseConnection.connectToDatabase();
                resultsReady = true;
            }
            catch (SQLException e) {
                errorLabel.setText("Error code: "+ translateLoginError(e.getErrorCode()));
            }
        }
        
        if (resultsReady) {
            notifyAll();
            exit();
        }
    }
    
    @FXML
    synchronized public void cancel() {
        loginCancelled = true;
        resultsReady = true;
        notifyAll();
        stage.close();
    }
    
    @FXML
    public void mouseEnteredHost() {
        if (imageCheckboxesStances[0])
            swapImage(hostImage, "images/login/db.png", imageCheckboxesStances[0]);
        else
            swapImage(hostImage, "images/login/db-rev.png", imageCheckboxesStances[0]);
    }
    
    @FXML
    public void mouseExitedHost() {
        if (imageCheckboxesStances[0]);
            //swapImage(hostImage, "");
    }
    
    @FXML
    public void mouseEnteredUser() {
    
    }

    @FXML
    public void mouseExitedUser() {
    
    }
    
    @FXML
    public void hostImageClicked() {
    
    }
    
    @FXML
    public void userImageClicked() {
    
    }
    
    private void swapImage(Image image, String imageUrl, Boolean checkBox) {
        image = new Image(imageUrl);
        checkBox = false;
    }
    
    private String translateLoginError(int code) {
        switch (code) {
            default: return "unknown code (" + code + ")";
        }
    }
    
    void exit() {
        stage.close();
    }
    
    @Override
    synchronized Object getResult() {
        while (!resultsReady && !loginCancelled) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return (resultsReady && !loginCancelled) ? databaseConnection.getDbcon() : null;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
