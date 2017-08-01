package com.application.database.sql;


import com.application.database.tables.TableInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseRow {

    private boolean valueChanged[];
    
    private int changesCounter = 0;
    
    public TableInfo tableInfo;
    public ObservableList<String> columnValues, columnValuesOld;
    public int size;
    public String tableName;
    public List<String> columnNames;
    
    public List<DatabaseCell> cellsList;
    
    public DatabaseRow(TableInfo tableInfo, ObservableList<String> columnValues) {
        size = tableInfo.getTableMetaData().columnsCount;
        tableName = tableInfo.getTableMetaData().tableName;
        columnNames = tableInfo.getTableMetaData().columnNames;
        this.tableInfo = tableInfo;
        this.columnValues = columnValues;
        this.columnValuesOld = FXCollections.observableArrayList(columnValues);
        valueChanged = new boolean[size];
    
        cellsList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            cellsList.add(i, new DatabaseCell(tableInfo.getTableMetaData().columnNames.get(i), i));
            valueChanged[i] = false;
        }
    }
    
    public void changeValue(String newValue, int index) {
        columnValues.set(index, newValue);
        if (!valueChanged[index])
            changesCounter++;
        
        valueChanged[index] = true;
    }
    
    public int getChangesCounter() {
        return changesCounter;
    }
}
