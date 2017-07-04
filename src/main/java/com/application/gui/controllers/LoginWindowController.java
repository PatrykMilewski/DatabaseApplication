package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import com.application.gui.Main;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.logging.Logger;

public class LoginWindowController {
    
    private static Logger log = Logger.getLogger(LoginWindowController.class.getCanonicalName());
    
    private MySQLConnection databaseConnection;
    private String basename, username, password;
    
    @FXML
    private TextField baseNameField;
    
    @FXML
    private TextField userNameField;
    
    @FXML
    private PasswordField passwordField;
    
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
            databaseConnection = new MySQLConnection(username, password, basename);
            try {
                databaseConnection.connectToDatabase();
            }
            catch (SQLException e) {
            
            }
        }
    }
    
    @FXML
    public void cancel() {
    
    }
}
