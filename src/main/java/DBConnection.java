package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


public abstract class DBConnection {
    protected Connection connection;

    // Empty constructor
    public DBConnection() {
    }

    // Method for connecting to the MySQL server
    public void connect() {
        try {
            // Properties object to store DB user credentials
            Properties credentials = new Properties();
            credentials.put("user", "root");
            credentials.put("password", "12345678");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/piazzini?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false",credentials);
        }
        // If unable to connect to the MySQL server
        catch (Exception e) {
            throw new RuntimeException("Unable to connect", e);
        }
    }
}