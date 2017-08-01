package com.application.gui.controllers;


import com.application.database.sql.DatabaseFilter;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RawSQLFilterConstructorController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(RawSQLFilterConstructorController.class.getCanonicalName());
    
    private DatabaseFilter filter;
    
    @FXML
    private TextField filterNameField;
    
    @FXML
    private TextArea sqlQueryArea;
    
    @FXML
    public void submitButtonClicked() {
        constructFilter();
    }
    
    private void constructFilter() {
        if (validateInput()) {
            filter = new DatabaseFilter(filterNameField.getText(), sqlQueryArea.getText());
            closeWindow();
        }
    }
    
    private boolean validateInput() {
        if (filterNameField.getText().isEmpty()) {
            MyAlerts.showWarningAlert("Błąd", "Podane dane nie przeszły walidacji.",
                    "Nazwa filtru nie może być pusta.", true);
            return false;
        }
        
        if (sqlQueryArea.getText().isEmpty()) {
            MyAlerts.showWarningAlert("Błąd", "Podane dane nie przeszły walidacji.",
                    "Pole z SQL Query nie może być puste.", true);
            return false;
        }
        
        return true;
    }
    
    @FXML
    public void cancelButtonClicked() {
        filter = null;
        closeWindow();
    }
    
    @Override
    synchronized Object getResult() {
        try {
            waitForResults();
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Waiting for results interrupted.");
            return null;
        }
        
        return filter;
    }
    
    @Override
    synchronized public void closeWindow() {
        FilterConstructorController.rawSqlConstructorWindowClosed();
        resultsReady = true;
        notifyAll();
        stage.close();
    }
}
