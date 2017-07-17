package com.application.gui.controllers;

import com.application.database.tables.TableInfo;
import com.application.gui.abstracts.factories.LoggerFactory;
import com.application.gui.elements.alerts.ExceptionAlert;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLQueryWindowController extends Controller {
    
    private static Logger log = LoggerFactory.getLogger(MainWindowController.class.getCanonicalName());
    
    private static final double COLUMNWIDTH = 100;
    
    private Connection connection;
    private ObservableList<TableColumn> tableColumns;
    
    @FXML
    private TextArea queryTextArea;
    
    @FXML
    private TableView resultsTable;
    
    @FXML
    public void processQuery() {
        TableInfo tableInfo = new TableInfo();
        resultsTable.getColumns().clear();
        
        try {
            PreparedStatement statement = connection.prepareStatement(queryTextArea.getText());
            ResultSetMetaData meta = statement.getMetaData();
            tableInfo.setMetaData(meta);
            tableColumns = FXCollections.observableArrayList(new ArrayList<>(tableInfo.columnsCount));
            
            for (int i = 0; i < tableInfo.columnsCount; i++) {
                final int j = i;
                TableColumn tableColumn = new TableColumn(tableInfo.columnNames[i]);
                //tableColumn.setPrefWidth(tableInfo.columnDisplaySize[i]);
                tableColumn.setMaxWidth(COLUMNWIDTH);
                tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty((param.getValue().get(j).toString()));
                    }
                });
                tableColumns.add(tableColumn);
            }
            
            resultsTable.getColumns().addAll(tableColumns);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            
            while(resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 0; i < tableInfo.columnsCount; i++)
                    row.add(resultSet.getString(i + 1));
                
                data.add(row);
            }
            
            resultsTable.setItems(data);
            resultsTable.refresh();
        }
        catch (SQLException e) {
            ExceptionAlert exceptionAlert = new ExceptionAlert("Wyjątek podczas wykonywania SQL query.", e);
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
}
