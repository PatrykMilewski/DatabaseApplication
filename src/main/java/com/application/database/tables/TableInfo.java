package com.application.database.tables;

public class TableInfo {
    
    private TableMetaData tableMetaData;
    private String sqlQuery;
    private String tableName;
    
    public TableInfo(TableMetaData tableMetaData, String sqlQuery, String tableName) {
        this.tableMetaData = tableMetaData;
        this.sqlQuery = sqlQuery;
        this.tableName = tableName;
    }
    
    public TableMetaData getTableMetaData() {
        return tableMetaData;
    }
    
    public String getSqlQuery() {
        return sqlQuery;
    }
    
    public String getTableName() {
        return tableName;
    }
}
