package com.application.database.tables;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableInfo {
    
    public int columnsCount;
    public String tableName;
    public String columnNames[];
    //public int columnTypes[];
    
    public void setMetaData(ResultSetMetaData meta) throws SQLException {
        columnsCount = meta.getColumnCount();
        tableName = meta.getTableName(1);
        columnNames = new String[columnsCount];
        //columnTypes = new int[columnsCount];
        
        for (int i = 1; i <= columnsCount; i++) {
            columnNames[i - 1] = meta.getColumnName(i);
            //columnTypes[i - 1] = meta.getColumnType(i);
        }
    }
}
