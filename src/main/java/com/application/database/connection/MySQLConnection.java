package com.application.database.connection;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {
    
    private final static int SQL_PORT = 3306;
    
    private MysqlDataSource dataSource;
    private Connection dbCon;
    
    public MySQLConnection(String user, String password, String serverName, String baseName) {
        dataSource = new MysqlDataSource();
        
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setServerName(serverName);
        dataSource.setDatabaseName(baseName);
        dataSource.setPort(SQL_PORT);
    }
    
    public void connectToDatabase() throws SQLException {
        dbCon = dataSource.getConnection();
    }
    
    
    public Connection getDBCon() {
        return dbCon;
    }
    
}
