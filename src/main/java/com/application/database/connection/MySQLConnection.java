package com.application.database.connection;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLConnection {
    private MysqlDataSource dataSource;
    private Connection dbcon;
    
    public MySQLConnection(String user, String password, String serverName) {
        dataSource = new MysqlDataSource();
        
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setServerName(serverName);
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
