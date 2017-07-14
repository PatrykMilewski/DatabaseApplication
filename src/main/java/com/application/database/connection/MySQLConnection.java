package com.application.database.connection;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {
    
    private final static int SQLPORT = 3306;
    
    private MysqlDataSource dataSource;
    private Connection dbcon;
    
    public MySQLConnection(String user, String password, String serverName, String baseName) {
        dataSource = new MysqlDataSource();
        
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setServerName(serverName);
        dataSource.setDatabaseName(baseName);
        dataSource.setPort(SQLPORT);
    }
    
    public void connectToDatabase() throws SQLException {
        dbcon = dataSource.getConnection();
    }
    
    
    public Connection getDbcon() {
        return dbcon;
    }
    
    public void setDbcon(Connection dbcon) {
        this.dbcon = dbcon;
    }
}
