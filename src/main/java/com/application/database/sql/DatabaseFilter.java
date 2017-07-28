package com.application.database.sql;

import com.application.gui.abstracts.factories.LoggerFactory;

import java.util.logging.Logger;

public class DatabaseFilter {
    
    private static Logger log = LoggerFactory.getLogger(DatabaseFilter.class.getCanonicalName());
    
    private String name;
    private String sqlQuery = "";
    private boolean validated = false;
    
    public DatabaseFilter(String name, String sqlQuery) {
        this.name = name;
        this.sqlQuery = sqlQuery;
    }
    
    public String getName() {
        return name;
    }
    
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
    
    public String getSqlQuery() {
        return sqlQuery;
    }
    
    public boolean isValidated() {
        return validated;
    }
    
    public void setValidated(boolean validated) {
        this.validated = validated;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
