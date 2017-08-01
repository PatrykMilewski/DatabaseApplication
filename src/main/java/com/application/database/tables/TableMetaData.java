package com.application.database.tables;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableMetaData {
    
    public int columnsCount;
    public String tableName;
    public List<String> columnNames;
    //public int columnTypes[];
    
    public TableMetaData(ResultSetMetaData meta) throws SQLException {
        columnsCount = meta.getColumnCount();
        tableName = meta.getTableName(1);
        columnNames = new ArrayList<>(columnsCount);
        //columnTypes = new int[columnsCount];
    
        for (int i = 1; i <= columnsCount; i++) {
            columnNames.add(i - 1, meta.getColumnName(i));
            //columnTypes[i - 1] = meta.getColumnType(i);
        }
    }
}
