package com.application.gui.controllers;

import com.application.database.tables.TableInfo;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.MyAlerts;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.util.Callback;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLQueryWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private static final double COLUMNWIDTH = 100;
    
    private Connection connection;
    private TableInfo tableInfo;
    
    @FXML
    private TextArea queryTextArea;
    
    @FXML
    private TableView<ObservableList> resultsTable;
    
    @FXML
    public void processQuery() {
        tableInfo = new TableInfo();
        resultsTable.getColumns().clear();
        
        try {
            PreparedStatement statement = connection.prepareStatement(queryTextArea.getText());
            ResultSet resultSet;
    
            if (statement.execute()) {
                resultSet = statement.getResultSet();
                ResultSetMetaData meta = resultSet.getMetaData();
                tableInfo.setMetaData(meta);
                constructColumns();
    
                resultsTable.setItems(handleResultsSet(resultSet));
            }
            else {
                int updateCount = statement.getUpdateCount();
                if (updateCount != -1)
                    MyAlerts.showInfoAlert("Wykonanie SQL Query",
                            "Pomyślnie wykonano polecenie SQL.",
                            "Zaktualizowano " + updateCount + " rekordów.");
                else {
                    StringBuilder sb = new StringBuilder();
                    while (statement.getMoreResults()) {
                        sb.append(statement.getResultSet().toString());
                        sb.append('\n');
                    }
                    MyAlerts.showInfoAlertMultipleSets(updateCount, sb.toString());
                }
                
            }
        }
        catch (SQLException e) {
            MyAlerts.showExceptionAlert("Wyjątek podczas wykonywania SQL query.", e);
        }
    }
    
    private void constructColumns() {
        for (int i = 0; i < tableInfo.columnsCount; i++) {
            final int j = i;
            TableColumn<ObservableList, String> tableColumn = new TableColumn<>(tableInfo.columnNames[i]);
            //tableColumn.setPrefWidth(tableInfo.columnDisplaySize[i]);
            tableColumn.setMaxWidth(COLUMNWIDTH);
            
            Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>> callback =
                    param -> new SimpleStringProperty((param.getValue().get(j).toString()));
        
            tableColumn.setCellValueFactory(callback);
        
            resultsTable.getColumns().add(tableColumn);
        }
    }
    
    private ObservableList<ObservableList> handleResultsSet(ResultSet resultSet) throws SQLException {
        ObservableList<ObservableList> data = FXCollections.observableArrayList();
    
        while(resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 0; i < tableInfo.columnsCount; i++)
                row.add(resultSet.getString(i + 1));
        
            data.add(row);
        }
        return data;
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
