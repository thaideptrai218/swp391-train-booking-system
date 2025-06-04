package vn.vnrailway.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import vn.vnrailway.utils.DBContext;
import vn.vnrailway.utils.HashPassword;

public class UserDAO {
    
    public boolean checkEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM dbo.Users WHERE Email = ?";
        System.out.println("Checking email existence: " + email);
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean exists = rs.getInt(1) > 0;
                    System.out.println("Email exists: " + exists);
                    return exists;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE dbo.Users SET PasswordHash = ? WHERE Email = ?";
        System.out.println("Debug - Password Update:");
        System.out.println("Email: " + email);
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Hash the new password using the same method as registration
            String hashedPassword = HashPassword.hashPassword(newPassword);
            System.out.println("Password hashed successfully");
            
            ps.setString(1, hashedPassword);
            ps.setString(2, email);
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to verify the database structure
    public void verifyDatabaseStructure() {
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME = 'Users'";
                    
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("Users Table Structure:");
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("DATA_TYPE");
                int maxLength = rs.getInt("CHARACTER_MAXIMUM_LENGTH");
                System.out.println(columnName + " - " + dataType + " (" + maxLength + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error checking table structure: " + e.getMessage());
        }
    }
}