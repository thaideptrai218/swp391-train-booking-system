package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

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

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            user.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        Timestamp lastLoginTimestamp = rs.getTimestamp("LastLogin");
        if (lastLoginTimestamp != null) {
            user.setLastLogin(lastLoginTimestamp.toLocalDateTime());
        }
        return user;
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin FROM Users WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin FROM Users WHERE Email = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) throws SQLException {
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin FROM Users WHERE PhoneNumber = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserID, FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin FROM Users";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO Users (FullName, Email, PhoneNumber, PasswordHash, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getIdCardNumber());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());

            if (user.getCreatedAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(user.getCreatedAt()));
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }

            if (user.getLastLogin() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(user.getLastLogin()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserID(generatedKeys.getInt(1));
                } else {
                    System.err.println("Creating user succeeded, but no ID was obtained. UserID might not be auto-generated or not configured to be returned.");
                }
            }
        }
        return user;
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE Users SET FullName = ?, Email = ?, PhoneNumber = ?, PasswordHash = ?, IDCardNumber = ?, Role = ?, IsActive = ?, LastLogin = ? WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getPasswordHash());
            ps.setString(5, user.getIdCardNumber());
            ps.setString(6, user.getRole());
            ps.setBoolean(7, user.isActive());

            if (user.getLastLogin() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(user.getLastLogin()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            ps.setInt(9, user.getUserID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean lockById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean unlockById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 1 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean hideById(int userId) throws SQLException {
        String sql = "UPDATE Users SET IsActive = 0 WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<Object[]> getLogsByPage(int page, int pageSize) throws SQLException {
        List<Object[]> auditLogs = new ArrayList<>();
        String sql = "SELECT LogId, LogTime, Username, TableName, RowId, ColumnName, OldValue, NewValue FROM dbo.AuditLogs ORDER BY LogId ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] log = new Object[8];
                    log[0] = rs.getInt("LogId");
                    log[1] = rs.getTimestamp("LogTime");
                    log[2] = rs.getString("Username");
                    log[3] = rs.getString("TableName");
                    log[4] = rs.getString("RowId");
                    log[5] = rs.getString("ColumnName");
                    log[6] = rs.getString("OldValue");
                    log[7] = rs.getString("NewValue");
                    auditLogs.add(log);
                }
            }
        }
        return auditLogs;
    }

    @Override
    public int getTotalLogCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.AuditLogs";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
