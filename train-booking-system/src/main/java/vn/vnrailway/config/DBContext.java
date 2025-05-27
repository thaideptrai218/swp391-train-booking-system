package vn.vnrailway.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBContext {

    private static Properties dbProperties = new Properties();

    static {
        // Load db.properties from the classpath (src/main/resources)
        try (InputStream input = DBContext.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("CRITICAL ERROR: db.properties file not found in classpath. Application cannot connect to the database. Please ensure 'db.properties' is in 'src/main/resources'.");
                // Optionally throw an exception to halt application startup if DB is critical
                // throw new RuntimeException("db.properties not found in classpath");
            } else {
                dbProperties.load(input);
                // System.out.println("Successfully loaded db.properties from classpath."); // Optional: for debugging
            }
        } catch (IOException ex) {
            System.err.println("CRITICAL ERROR: IOException while loading db.properties from classpath. Details: " + ex.getMessage());
            // Optionally throw an exception
            // throw new RuntimeException("Failed to load db.properties from classpath due to IOException", ex);
        }

        // Load the JDBC driver
        try {
            Class.forName(dbProperties.getProperty("db.driver"));
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found: " + e.getMessage());
            // Consider throwing a runtime exception
        } catch (NullPointerException e) {
            System.err.println("db.driver property might be missing in db.properties or db.properties not loaded: "
                    + e.getMessage());
            // Consider throwing a runtime exception
        }
    }

    /**
     * Establishes a connection to the database using properties file.
     *
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        String dbUrl = dbProperties.getProperty("db.url");
        String dbUser = dbProperties.getProperty("db.username");
        String dbPassword = dbProperties.getProperty("db.password");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            System.err.println(
                    "Database configuration properties (url, username, password) are not fully loaded. Check db.properties and static initializer block.");
            throw new SQLException("Database configuration properties are missing.");
        }

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            String maskedUrl = dbUrl;
            if (dbUrl != null && dbUrl.contains(";databaseName=")) {
                maskedUrl = dbUrl.substring(0, dbUrl.indexOf(";databaseName=") + 15) + "****";
            }
            System.err.println("Failed to connect to the database. URL: "
                    + maskedUrl
                    + ", User: " + dbUser
                    + " Error: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    /**
     * Closes the given database connection.
     *
     * @param connection The connection to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection: " + e.getMessage());
            }
        }
    }

    // Optional: Main method for testing the connection
    public static void main(String[] args) {
        try (Connection conn = DBContext.getConnection()) {
            if (conn != null) {
                System.out.println("Connection Test Successful!");
            } else {
                System.out.println("Connection Test Failed! (Connection object is null)");
            }
        } catch (SQLException e) {
            System.err.println("Connection Test Failed (SQLException): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) { // Catch other potential exceptions from static block or runtime issues
            System.err.println("Connection Test Failed (General Exception): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
