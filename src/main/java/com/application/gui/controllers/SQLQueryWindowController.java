package com.application.gui.controllers;

import com.application.database.tables.TableInfo;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import com.application.gui.elements.controllers.ThreadsController;
import com.sun.rowset.CachedRowSetImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLQueryWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private static final double COLUMNWIDTH = 100;
    private static final int DBFETCHSIZE = 100;
    
    private Connection connection;
    private TableInfo tableInfo;
    private int tabsCounter = 1;
    
    private ThreadsController threadsController = new ThreadsController();
    
    @FXML
    private TextArea queryTextArea;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    public void initialize() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
    }
    
    @FXML
    public void menuBarProcessQueryClicked() {
        String sqlQuery = queryTextArea.getText();
        Thread queryThread = new Thread(() -> processQuery(sqlQuery));
        threadsController.addThread(queryThread);
        queryThread.start();
    }
    
    synchronized private void processQuery(String queryAreaText) {
        //todo remove old tabs from pane mechanism to avoid memory leak
        
        for (String sqlCommand : queryAreaText.split(";")) {
            CachedRowSet cachedRowSet = processSQLCommand(sqlCommand);
            Tab tab = new Tab("Tabela " + tabsCounter);
            tabsCounter++;
            Platform.runLater(() -> displayResults(new TableView<>(), tab, cachedRowSet));
        }
    }
    
    private void displayResults(TableView<ObservableList> tableView, Tab tab, CachedRowSet cachedRowSet) {
        tabPane.getTabs().add(tab);
        tab.setContent(tableView);
    
        handleSingleResultSet(tableView, cachedRowSet);
    }
    
    private CachedRowSet processSQLCommand(String sqlCommand) {
        PreparedStatement statement = null;
        ResultSet resultSet;
        CachedRowSet cachedRowSet = null;
        tableInfo = new TableInfo();
    
        try {
            statement = connection.prepareStatement(sqlCommand);
            statement.execute();
            resultSet = statement.getResultSet();
            cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.populate(resultSet);
            resultSet.setFetchSize(DBFETCHSIZE);
    
            if (statement.getUpdateCount() != -1)
                MyAlerts.showInfoAlert("Wykonanie SQL Query",
                        "Pomyślnie wykonano polecenie SQL.",
                        "Zaktualizowano " + statement.getUpdateCount() + " rekordów.", false);
            
            
        } catch (SQLException e) {
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e, false);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return cachedRowSet;
    }
    
    private void constructColumns(TableView<ObservableList> tableView, TableInfo tableInfo) {
        for (int i = 0; i < tableInfo.columnsCount; i++) {
            final int finalIterator = i;
            TableColumn<ObservableList, String> tableColumn = new TableColumn<>(tableInfo.columnNames[i]);
            //tableColumn.setPrefWidth(tableInfo.columnDisplaySize[i]);
            tableColumn.setMaxWidth(COLUMNWIDTH);
            
            Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>> callback =
                    param -> new SimpleStringProperty((param.getValue().get(finalIterator).toString()));
        
            tableColumn.setCellValueFactory(callback);
    
            tableView.getColumns().add(tableColumn);
        }
    }
    
    private ObservableList<ObservableList> handleResultsSet(CachedRowSet cachedRowSet, TableInfo tableInfo)
            throws SQLException {
        
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        while (cachedRowSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < tableInfo.columnsCount; i++)
                row.add(cachedRowSet.getString(i + 1));
            
            data.add(row);
        }
        return data;
    }
    
    private void handleSingleResultSet(TableView<ObservableList> tableView, CachedRowSet cachedRowSet) {
        try {
            ResultSetMetaData meta = cachedRowSet.getMetaData();
            tableInfo.setMetaData(meta);
            constructColumns(tableView, tableInfo);
            ObservableList<ObservableList> data = handleResultsSet(cachedRowSet, tableInfo);
            tableView.setItems(data);
            cachedRowSet.close();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e, true);
        }
    }
    
    @Override
    public void exit() {
        MainWindowController.setIsSQLQueryWindowOpen(false);
        threadsController.killThreads();
        stage.close();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    synchronized Object getResult() {
        while(!resultsReady) {
            try {
                wait();
            } catch (InterruptedException e) {
                log.log(Level.INFO, "Waiting for results in SQL Query interrupted.");
            }
        }
        
        return resultsReady ? 0 : 1;
    }
}
