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
        // Path to the db.properties file relative to the project root directory
        // This path is primarily for running directly (e.g., the main method from an
        // IDE or command line)
        // It is NOT reliable for WAR deployments if the working directory assumption is
        // incorrect.
        // Assumes the current working directory is C:\\Users\\PC\\SWP391 (i.e., the
        // parent of 'train-booking-system')
        String propertiesFilePath = "train-booking-system/src/main/java/vn/vnrailway/config/db.properties";

        try (InputStream input = new java.io.FileInputStream(propertiesFilePath)) {
            dbProperties.load(input);
            // System.out.println("Successfully loaded db.properties from: " +
            // propertiesFilePath); // Optional: for debugging
        } catch (java.io.FileNotFoundException ex) {
            System.err.println("CRITICAL ERROR: db.properties file not found at '" + propertiesFilePath
                    + "'. Application cannot connect to the database. Please ensure the file exists at this location relative to the project root. Details: "
                    + ex.getMessage());
            // Consider throwing a RuntimeException to halt initialization if properties are
            // essential
            // throw new RuntimeException("Failed to load db.properties, file not found: " +
            // propertiesFilePath, ex);
        } catch (IOException ex) {
            System.err.println("CRITICAL ERROR: IOException while loading db.properties from '" + propertiesFilePath
                    + "'. Details: " + ex.getMessage());
            // Consider throwing a RuntimeException
            // throw new RuntimeException("Failed to load db.properties due to IOException:
            // " + propertiesFilePath, ex);
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
