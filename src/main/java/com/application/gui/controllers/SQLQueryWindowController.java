package com.application.gui.controllers;

import com.application.database.sql.Query;
import com.application.database.tables.TableInfo;
import com.application.gui.abstracts.exceptions.IllegalQueryInTableLockStateException;
import com.application.gui.abstracts.exceptions.QueryInTableLockNotFound;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import com.application.gui.elements.containers.LockingHashMap;
import com.application.gui.elements.containers.QueryInTable;
import com.application.gui.elements.controllers.ThreadsController;
import com.sun.rowset.CachedRowSetImpl;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    
    private LockingHashMap<QueryInTable> queryInTablesMap = new LockingHashMap<>();
    private static Map<QueryInTable, Thread> queryInTabLockNotify = new ConcurrentHashMap<>();
    
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
            Query query = new Query(sqlCommand);
            CachedRowSet cachedRowSet = processSQLCommand(query.getNextPackQuery());
            Tab tab = new Tab("Tabela " + tabsCounter);
            QueryInTable queryInTable = new QueryInTable(tab, new TableView<>(), query);
            tabsCounter++;
            queryInTablesMap.addAndLock(queryInTable);
            Platform.runLater(() -> displayResults(queryInTable, cachedRowSet));
        }
    }
    
    synchronized private void processQuery(QueryInTable queryInTable) {
        //todo remove old tabs from pane mechanism to avoid memory leak
        
        while(queryInTablesMap.isLocked(queryInTable)) {
            if (!queryInTabLockNotify.containsKey(queryInTable))
                queryInTabLockNotify.put(queryInTable, Thread.currentThread());
            
            try {
                wait();
            } catch (InterruptedException e) {
                log.log(Level.WARNING, e.getMessage(), e);
            }
        }
        
        CachedRowSet cachedRowSet = processSQLCommand(queryInTable.query.getNextPackQuery());
        Platform.runLater(() -> displayResults(queryInTable, cachedRowSet));
        queryInTablesMap.addAndLock(queryInTable);
    }
    
    private void displayResults(QueryInTable queryInTable, CachedRowSet cachedRowSet) {
        tabPane.getTabs().add(queryInTable.tab);
        queryInTable.initialize();
    
        handleSingleResultSet(queryInTable.tableView, cachedRowSet);
        queryInTablesMap.unlockElement(queryInTable);
        queryInTable.tab.setOnClosed(event -> queryInTablesMap.forceRemove(queryInTable));
        
//        stage.show();
//
//        for (Node n : queryInTable.tableView.lookupAll(".scroll-bar")) {
//            if (n instanceof ScrollBar)
//                System.out.println("dupa");
//        }
//
//        queryInTable.tableView.setOnScroll(event -> {
//
//        });
//
//
//        ScrollBar tableScrollBar = (ScrollBar) queryInTable.tableView.lookup(".scroll-bar");
//        if (tableScrollBar != null) {
//            tableScrollBar.valueProperty().addListener((obs, oldValue, newValue) -> {
//                if (newValue.doubleValue() >= (4 * tableScrollBar.getMax() / 5)) {
//                    try {
//                        updateTable(queryInTable);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
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
    
    synchronized public static <E> void elementUnlocked(E element) {
        QueryInTable queryInTable = (QueryInTable) element;
        if (queryInTabLockNotify.containsKey(queryInTable)) {
            Thread thread = queryInTabLockNotify.get(queryInTable);
            thread.notify();
        }
    }
    
    private void updateTable(QueryInTable queryInTable)
            throws IllegalQueryInTableLockStateException, QueryInTableLockNotFound {
        
        if (queryInTablesMap.contains(queryInTable)) {
            if (queryInTablesMap.isLocked(queryInTable))
                throw new IllegalQueryInTableLockStateException();
            
            Thread thread = new Thread(() -> processQuery(queryInTable));
            threadsController.addThread(thread);
            thread.start();
        }
        else
            throw new QueryInTableLockNotFound();
    }
}
