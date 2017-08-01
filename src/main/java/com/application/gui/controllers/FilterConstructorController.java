package com.application.gui.controllers;


import com.application.database.sql.DatabaseFilter;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import com.application.gui.elements.controllers.ThreadsController;
import com.application.gui.windows.RawSqlFilterConstructorWindow;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FilterConstructorController extends Controller {
    
    private static final String DEFAULT_FILTER_NAME = "Filtr ";

    private static Logger log = LoggerFactory.getLogger(FilterConstructorController.class.getCanonicalName());
    
    private static boolean rawSqlConstructorWindowOpen = false;
    private static int filtersCounter = 0;
    
    private RawSqlFilterConstructorWindow rawSqlFilterConstructorWindow;
    private ThreadsController threadsController = new ThreadsController();
    
    private DatabaseFilter databaseFilter = null;
    private String sqlQuery, filterName;
    private boolean sortDescending;
    private int limitFrom, limitTo;
    private List<String> groupBy = new LinkedList<>();
    private List<String> selectColumns = new LinkedList<>();
    private List<String> fromTables = new LinkedList<>();
    
    static void rawSqlConstructorWindowClosed() {
        FilterConstructorController.rawSqlConstructorWindowOpen = false;
    }
    
    @Override
    synchronized Object getResult() {
        try {
            waitForResults();
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Waiting for results interrupted.");
            return null;
        }
        
        return databaseFilter;
    }
    
    @Override
    synchronized public void closeWindow() {
        threadsController.killThreads();
        resultsReady = true;
        notifyAll();
        stage.close();
    }
    
    @FXML
    public void buttonActionOpenRawSql() {
        if (rawSqlConstructorWindowOpen) {
            MyAlerts.showWarningAlert("Błąd", "Nie można otworzyć okna.",
                    "Okno do zaawansowanego tworzenia filtrów jest już otwarte!", true);
            log.log(Level.WARNING, "Raw filter constructor window already opened.");
            return;
        }
        
        try {
            rawSqlFilterConstructorWindow = new RawSqlFilterConstructorWindow();
            Thread rawSqlConstructorWorker = new Thread(this::waitForRawSqlFilter);
            threadsController.addThread(rawSqlConstructorWorker);
            rawSqlConstructorWorker.start();
            
            rawSqlConstructorWindowOpen = true;
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            MyAlerts.showExceptionAlert("Błąd", e, true);
        }
    }
    
    
    @FXML
    public void createFilterButtonClicked() {
        if (validateInput()) {
            //todo
        }
    }
    
    private boolean validateInput() {
        //todo
        return false;
    }
    
    @FXML
    public void cancelButtonClicked() {
        databaseFilter = null;
        closeWindow();
    }
    
    private void waitForRawSqlFilter() {
        databaseFilter = (DatabaseFilter) rawSqlFilterConstructorWindow.getController().getResult();
    
        if (databaseFilter != null)
            Platform.runLater(this::closeWindow);
    }
}
