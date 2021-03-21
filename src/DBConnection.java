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
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Properties object to store DB user credentials
            Properties credentials = new Properties();
            // TODO: replace with our credentials
            credentials.put("user", "myuser");
            credentials.put("password", "mypassword");
            // TODO: replace with our URL
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/avtalebok?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false",credentials);
        }
        // If unable to connect to the MySQL server
        catch (Exception e) {
            throw new RuntimeException("Unable to connect", e);
        }
    }
}