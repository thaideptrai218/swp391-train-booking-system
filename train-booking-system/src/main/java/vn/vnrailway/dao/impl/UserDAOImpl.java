package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.UserDAO;
import vn.vnrailway.dto.payment.CustomerDetailsDto;
import vn.vnrailway.model.User;
// import vn.vnrailway.config.DBContext; // If DBContext is used for connections

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class UserDAOImpl implements UserDAO {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setFullName(rs.getString("FullName"));
        user.setEmail(rs.getString("Email"));
        user.setPhoneNumber(rs.getString("PhoneNumber"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setIdCardNumber(rs.getString("IDCardNumber"));
        user.setRole(rs.getString("Role"));
        user.setActive(rs.getBoolean("IsActive"));
        
        Timestamp createdAtTs = rs.getTimestamp("CreatedAt");
        if (createdAtTs != null) {
            user.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        Timestamp lastLoginTs = rs.getTimestamp("LastLogin");
        if (lastLoginTs != null) {
            user.setLastLogin(lastLoginTs.toLocalDateTime());
        }
        user.setGuestAccount(rs.getBoolean("IsGuestAccount"));
        return user;
    }

    @Override
    public User findByEmail(Connection conn, String email) throws SQLException {
        String sql = "SELECT * FROM dbo.Users WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public long insertUser(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO dbo.Users (FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, IsGuestAccount, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash()); // Can be null
            ps.setString(5, user.getIdCardNumber()); // Can be null
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());
            ps.setBoolean(8, user.isGuestAccount());
            ps.setTimestamp(9, user.getCreatedAt() != null ? Timestamp.valueOf(user.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // Return generated UserID
                    }
                }
            }
        }
        return -1; // Or throw exception if failed
    }

    @Override
    public boolean updateUser(Connection conn, User user) throws SQLException {
        String sql = "UPDATE dbo.Users SET FullName = ?, Email = ?, PhoneNumber = ?, PasswordHash = ?, IDCardNumber = ?, Role = ?, IsActive = ?, LastLogin = ?, IsGuestAccount = ? WHERE UserID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getIdCardNumber());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());
            ps.setTimestamp(8, user.getLastLogin() != null ? Timestamp.valueOf(user.getLastLogin()) : null);
            ps.setBoolean(9, user.isGuestAccount());
            ps.setInt(10, user.getUserID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public User findById(Connection conn, long userId) throws SQLException {
        String sql = "SELECT * FROM dbo.Users WHERE UserID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User findOrCreateUser(Connection conn, CustomerDetailsDto customerDetails) throws SQLException {
        User user = findByEmail(conn, customerDetails.getEmail());
        if (user == null) {
            User newUser = new User();
            newUser.setFullName(customerDetails.getFullName());
            newUser.setEmail(customerDetails.getEmail());
            newUser.setPhoneNumber(customerDetails.getPhoneNumber());
            newUser.setIdCardNumber(customerDetails.getIdCardNumber());
            newUser.setRole("Customer"); 
            newUser.setActive(true); 
            newUser.setGuestAccount(true); 
            newUser.setCreatedAt(LocalDateTime.now());
            // PasswordHash is null for guest/newly created user from booking
            
            long newUserId = insertUser(conn, newUser);
            if (newUserId != -1) {
                newUser.setUserID((int)newUserId); 
                return newUser;
            } else {
                throw new SQLException("Failed to create new user during findOrCreateUser.");
            }
        } else {
            // Optionally update existing user details if different and if policy allows
            boolean changed = false;
            if (customerDetails.getFullName() != null && !customerDetails.getFullName().equals(user.getFullName())) {
                user.setFullName(customerDetails.getFullName());
                changed = true;
            }
            if (customerDetails.getPhoneNumber() != null && !customerDetails.getPhoneNumber().equals(user.getPhoneNumber())) {
                user.setPhoneNumber(customerDetails.getPhoneNumber());
                changed = true;
            }
            if (customerDetails.getIdCardNumber() != null && !customerDetails.getIdCardNumber().equals(user.getIdCardNumber())) {
                user.setIdCardNumber(customerDetails.getIdCardNumber());
                changed = true;
            }
            if (changed) {
                updateUser(conn, user);
            }
        }
        return user;
    }

    @Override
    public boolean checkEmailExists(Connection conn, String email) throws SQLException {
        User user = findByEmail(conn, email);
        return user != null;
    }

    @Override
    public boolean updatePassword(Connection conn, String email, String newPasswordHash) throws SQLException {
        String sql = "UPDATE dbo.Users SET PasswordHash = ? WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setString(2, email);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}
