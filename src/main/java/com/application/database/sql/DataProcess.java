package com.application.database.sql;


import com.application.database.tables.TableInfo;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import com.sun.rowset.CachedRowSetImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DataProcess {
    
    private static final double COLUMNWIDTH = 100;
    
    private static Logger log = LoggerFactory.getLogger(DataProcess.class.getCanonicalName());
    
    private DataProcess() {}
    
    public static CachedRowSet processSQLCommand(Connection connection, String sqlCommand) {
        PreparedStatement statement = null;
        ResultSet resultSet;
        CachedRowSet cachedRowSet = null;
        TableInfo tableInfo = new TableInfo();
        
        try {
            statement = connection.prepareStatement(sqlCommand);
            statement.execute();
            resultSet = statement.getResultSet();
            cachedRowSet = new CachedRowSetImpl();
            cachedRowSet.populate(resultSet);
            
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
    
    public static ObservableList<ObservableList> handleResultsSet(CachedRowSet cachedRowSet, TableInfo tableInfo)
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
    
    public static void handleSingleResultSet(TableView<ObservableList> tableView, CachedRowSet cachedRowSet) {
        try {
            ResultSetMetaData meta = cachedRowSet.getMetaData();
            TableInfo tableInfo = new TableInfo();
            tableInfo.setMetaData(meta);
            DataProcess.constructColumns(tableView, tableInfo);
            ObservableList<ObservableList> data = DataProcess.handleResultsSet(cachedRowSet, tableInfo);
            tableView.setItems(data);
            cachedRowSet.close();
        }
        catch (SQLException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e, true);
        }
    }
    
    public static void constructColumns(TableView<ObservableList> tableView, TableInfo tableInfo) {
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
}
