package vn.vnrailway.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBContext {

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=TrainTicketSystemDB_V1_Revised;encrypt=false";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "sa123456";
    private static final String DB_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    /**
     * Establishes a connection to the database.
     *
     * @return A Connection object to the database.
     * @throws SQLException           if a database access error occurs.
     * @throws ClassNotFoundException if the JDBC driver class is not found.
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("SQL Server JDBC Driver not found: " + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database. URL: "
                    + DB_URL.substring(0, DB_URL.indexOf(";databaseName=") + 15) + "****" + ", User: " + DB_USER
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
                System.out.println("Connection Test Failed!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Connection Test Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
