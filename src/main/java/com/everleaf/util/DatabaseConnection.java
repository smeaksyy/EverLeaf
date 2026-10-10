package com.everleaf.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;
    
    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.err.println("Error loading database configuration or driver");
        }
        
        URL = props.getProperty("db.url", "jdbc:mysql://localhost:3306/everleaf_db");
        USER = props.getProperty("db.username", "root");
        PASSWORD = props.getProperty("db.password", "root");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
