package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import com.application.gui.abstracts.factories.LoggerFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class.getCanonicalName());
    
    private MySQLConnection databaseConnection;
    private Stage stage;
    private String baseAndServerName = "localhost:testowa", username = "test", password = "982563";
    
    private boolean loginCancelled = false;
    
    @FXML
    private TextField baseAndServerNameField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    public LoginWindowController() {
        resultsReady = false;
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
