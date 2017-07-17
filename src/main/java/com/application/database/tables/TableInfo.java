package com.application.database.tables;


import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class TableInfo {
    
    private Connection connection;
    public int columnsCount;
    public String tableName;
    public String columnNames[];
    public int columnTypes[];
    public int columnDisplaySize[];
    
    public void setMetaData(ResultSetMetaData meta) throws SQLException {
        columnsCount = meta.getColumnCount();
        tableName = meta.getTableName(1);
        columnNames = new String[columnsCount];
        columnTypes = new int[columnsCount];
        columnDisplaySize = new int[columnsCount];
        
        for (int i = 1; i <= columnsCount; i++) {
            columnNames[i - 1] = meta.getColumnName(i);
            columnTypes[i - 1] = meta.getColumnType(i);
            columnDisplaySize[i - 1] = meta.getColumnDisplaySize(i);
        }
    }
}
