package com.project.ws.services;

import com.project.ws.common.Utils;

import java.sql.Connection;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection conn;
    private DatabaseConnection(){
        conn = Utils.makeJDBCConnection();
    }

    public static DatabaseConnection getInstance() {
        if(instance==null){
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConn() {
        return conn;
    }
}
