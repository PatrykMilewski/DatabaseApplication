package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginWindowController extends Controller {
    
    private static Logger log = Logger.getLogger(LoginWindowController.class.getCanonicalName());
    
    private MySQLConnection databaseConnection;
    private String basename, username, password;
    private Stage loginStage;
    
    private boolean loginCancelled = false;
    
    @FXML
    private TextField baseNameField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    LoginWindowController(Stage loginStage) {
        this.loginStage = loginStage;
        resultsReady = false;
    }
    
    @FXML
    public void baseNameEntered() {
        basename = baseNameField.getText();
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
    public void login() {
        if (basename.length() != 0 && username.length() != 0 && password.length() != 0) {
            resultsReady = false;
            databaseConnection = new MySQLConnection(username, password, basename);
            try {
                databaseConnection.connectToDatabase();
                resultsReady = true;
            }
            catch (SQLException e) {
                errorLabel.setText("Error code: "+ translateLoginError(e.getErrorCode()));
            }
        }
        if (resultsReady)
            notifyAll();
    }
    
    @FXML
    public void cancel() {
        loginCancelled = true;
        loginStage.close();
    }
    
    private String translateLoginError(int code) {
        switch (code) {
            default: return "unknown code (" + code + ")";
        }
    }
    
    @Override
    Object getResult() {
        while (!resultsReady) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (loginCancelled)
            return null;
        else
            return databaseConnection;
    }
}
