package com.application.gui.controllers;


import com.application.database.sql.DatabaseFilter;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RawSQLFilterConstructorWindowController extends Controller {
    
    private static Logger log =
            LoggerFactory.getLogger(RawSQLFilterConstructorWindowController.class.getCanonicalName());
    
    private Connection connection;
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
        
        if (!MainWindowController.validateFiltersName(filterNameField.getText())) {
            MyAlerts.showWarningAlert("Błąd", "Podane dane nie przeszły walidacji.",
                    "Istnieje już filtr o takiej nazwie.", true);
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
    
    public void setConnection(Connection connection) {
        this.connection = connection;
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
        FilterConstructorWindowController.rawSqlConstructorWindowClosed();
        resultsReady = true;
        notifyAll();
        stage.close();
    }
}
