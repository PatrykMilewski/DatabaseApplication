package com.application.gui.controllers;

import com.application.database.connection.MySQLConnection;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.controllers.ThreadsController;
import com.application.gui.elements.loaders.UserDataLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

public class LoginWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(LoginWindowController.class.getCanonicalName());

    private static final int AUTOCOMPLETIONDELAY = 30;
    
    private static ThreadsController threadsController = new ThreadsController();
    private static UserDataLoader userDataLoader = new UserDataLoader();
    
    private MySQLConnection databaseConnection;
    private String baseAndServerName = "localhost:testowa", username = "test", password = "982563";
    private Set<String> savedHosts, savedUsers;
    
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
    private ToggleSwitch saveSettingsSwitch;
    
    @FXML
    public void initialize() {
        resultsReady = false;
        loadUserSettingsFromFile();
        AutoCompletionBinding<String> autoCompletionBinding;
        if (savedUsers != null)
            TextFields.bindAutoCompletion(userNameField, savedUsers).setDelay(AUTOCOMPLETIONDELAY);
        
        if (savedHosts != null)
            TextFields.bindAutoCompletion(baseAndServerNameField, savedHosts).setDelay(AUTOCOMPLETIONDELAY);
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
            refreshInputData();
            String hostAndBase[] = baseAndServerName.split(":");
            resultsReady = false;
            databaseConnection = new MySQLConnection(username, password, hostAndBase[0], hostAndBase[1]);
            try {
                databaseConnection.connectToDatabase();
                resultsReady = true;
            }
            catch (SQLException e) {
                errorLabel.setText("Błąd: "+ translateLoginError(e.getErrorCode()));
            }
        }
        else {
            errorLabel.setText("Uzupełnij wszystkie pola");
        }
        
        if (resultsReady) {
            notifyAll();
            if (saveSettingsSwitch.isSelected())
                saveHostsAndUserInfo();
            exit();
        }
    }
    
    private void refreshInputData() {
        baseAndHostNameEntered();
        userNameEntered();
        passwordEntered();
    }
    
    private void loadUserSettingsFromFile() {
        userDataLoader.loadData();
        savedHosts = userDataLoader.getSavedHosts();
        savedUsers = userDataLoader.getSavedUsers();
    }
    
    private void saveHostsAndUserInfo() {
        userDataLoader.addNewData(baseAndServerNameField.getText(), userNameField.getText());
        Thread saveDataWorker = new Thread(userDataLoader::saveData);
        saveDataWorker.start();
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
            default: return "nieznany kod błędu (" + code + ")";
        }
    }
    
    @Override
    public void exit() {
        MainWindowController.setIsLoginWindowOpen(false);
        threadsController.killThreads();
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
