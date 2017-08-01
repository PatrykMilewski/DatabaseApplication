package com.application.database.sql;



public class DatabaseCell {
    
    private String columnName;
    private int index;
    
    DatabaseCell(String columnName, int index) {
        this.columnName = columnName;
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    @Override
    public String toString() {
        return columnName;
    }
}
