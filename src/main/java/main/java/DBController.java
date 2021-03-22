package main.java;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBController {
    private Connection connection;
    private String current_user_email;
    private String current_course_id;
    // Update this attribute when either email or course_id change
    private boolean isInstructor;

    // Empty constructor
    public DBController() {
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

    // Return -1 for exception
    // Return 0 for successful login
    // Return 1 for invalid credentials
    // TODO: test method
    public int userLogin(String email, String password) {
        try {
            PreparedStatement query = this.connection.prepareStatement(String.format(
                    "SELECT email FROM piazza_user " +
                    "WHERE piazza_user.password=%s " +
                    "AND piazza_user.email=%s", password, email));

            ResultSet queryResult = query.executeQuery();

            if (queryResult.next()) {
                this.current_user_email = queryResult.getString("email");
                System.out.println("You are now logged in as " + this.current_user_email);
                return 0;
            }
            else {
                System.out.println("The username and/or password is wrong. Please try again.");
                return 1;
            }
        }

        catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
