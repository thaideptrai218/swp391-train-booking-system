package vn.vnrailway.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBContext {
    private static final Properties props;
    
    static {
        props = new Properties();
        try (InputStream input = DBContext.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            props.load(input);
            Class.forName(props.getProperty("db.driver"));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Cannot load database properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.username"),
            props.getProperty("db.password")
        );
    }
}