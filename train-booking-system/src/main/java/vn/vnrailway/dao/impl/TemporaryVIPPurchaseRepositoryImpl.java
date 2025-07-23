package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TemporaryVIPPurchaseRepository;
import vn.vnrailway.model.TemporaryVIPPurchase;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TemporaryVIPPurchaseRepository for database operations.
 */
public class TemporaryVIPPurchaseRepositoryImpl implements TemporaryVIPPurchaseRepository {

    /**
     * Maps ResultSet to TemporaryVIPPurchase object
     */
    private TemporaryVIPPurchase mapResultSetToTemporaryVIPPurchase(ResultSet rs) throws SQLException {
        TemporaryVIPPurchase tempVIPPurchase = new TemporaryVIPPurchase();
        tempVIPPurchase.setTempVIPPurchaseID(rs.getInt("TempVIPPurchaseID"));
        tempVIPPurchase.setSessionID(rs.getString("SessionID"));
        
        int userID = rs.getInt("UserID");
        if (!rs.wasNull()) {
            tempVIPPurchase.setUserID(userID);
        }
        
        tempVIPPurchase.setVipCardTypeID(rs.getInt("VIPCardTypeID"));
        tempVIPPurchase.setDurationMonths(rs.getInt("DurationMonths"));
        tempVIPPurchase.setPrice(rs.getBigDecimal("Price"));
        
        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            tempVIPPurchase.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        
        Timestamp expiresAtTimestamp = rs.getTimestamp("ExpiresAt");
        if (expiresAtTimestamp != null) {
            tempVIPPurchase.setExpiresAt(expiresAtTimestamp.toLocalDateTime());
        }
        
        return tempVIPPurchase;
    }

    @Override
    public TemporaryVIPPurchase create(TemporaryVIPPurchase tempVIPPurchase) throws SQLException {
        String sql = "INSERT INTO TemporaryVIPPurchases (SessionID, UserID, VIPCardTypeID, DurationMonths, Price, CreatedAt, ExpiresAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, tempVIPPurchase.getSessionID());
            if (tempVIPPurchase.getUserID() != null) {
                statement.setInt(2, tempVIPPurchase.getUserID());
            } else {
                statement.setNull(2, Types.INTEGER);
            }
            statement.setInt(3, tempVIPPurchase.getVipCardTypeID());
            statement.setInt(4, tempVIPPurchase.getDurationMonths());
            statement.setBigDecimal(5, tempVIPPurchase.getPrice());
            statement.setTimestamp(6, Timestamp.valueOf(tempVIPPurchase.getCreatedAt()));
            statement.setTimestamp(7, Timestamp.valueOf(tempVIPPurchase.getExpiresAt()));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating temporary VIP purchase failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tempVIPPurchase.setTempVIPPurchaseID(generatedKeys.getInt(1));
                    return tempVIPPurchase;
                } else {
                    throw new SQLException("Creating temporary VIP purchase failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Optional<TemporaryVIPPurchase> findById(int tempVIPPurchaseID) throws SQLException {
        String sql = "SELECT TempVIPPurchaseID, SessionID, UserID, VIPCardTypeID, DurationMonths, Price, CreatedAt, ExpiresAt " +
                    "FROM TemporaryVIPPurchases WHERE TempVIPPurchaseID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, tempVIPPurchaseID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTemporaryVIPPurchase(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TemporaryVIPPurchase> findBySessionId(String sessionID) throws SQLException {
        String sql = "SELECT TempVIPPurchaseID, SessionID, UserID, VIPCardTypeID, DurationMonths, Price, CreatedAt, ExpiresAt " +
                    "FROM TemporaryVIPPurchases WHERE SessionID = ? AND ExpiresAt > GETDATE() ORDER BY CreatedAt DESC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, sessionID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTemporaryVIPPurchase(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<TemporaryVIPPurchase> findByUserId(int userID) throws SQLException {
        String sql = "SELECT TempVIPPurchaseID, SessionID, UserID, VIPCardTypeID, DurationMonths, Price, CreatedAt, ExpiresAt " +
                    "FROM TemporaryVIPPurchases WHERE UserID = ? AND ExpiresAt > GETDATE() ORDER BY CreatedAt DESC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTemporaryVIPPurchase(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateExpiry(int tempVIPPurchaseID, int newExpiryMinutes) throws SQLException {
        String sql = "UPDATE TemporaryVIPPurchases SET ExpiresAt = DATEADD(MINUTE, ?, GETDATE()) WHERE TempVIPPurchaseID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, newExpiryMinutes);
            statement.setInt(2, tempVIPPurchaseID);
            
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int tempVIPPurchaseID) throws SQLException {
        String sql = "DELETE FROM TemporaryVIPPurchases WHERE TempVIPPurchaseID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, tempVIPPurchaseID);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteBySessionId(String sessionID) throws SQLException {
        String sql = "DELETE FROM TemporaryVIPPurchases WHERE SessionID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, sessionID);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<TemporaryVIPPurchase> findExpired() throws SQLException {
        List<TemporaryVIPPurchase> expiredPurchases = new ArrayList<>();
        String sql = "SELECT TempVIPPurchaseID, SessionID, UserID, VIPCardTypeID, DurationMonths, Price, CreatedAt, ExpiresAt " +
                    "FROM TemporaryVIPPurchases WHERE ExpiresAt <= GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                expiredPurchases.add(mapResultSetToTemporaryVIPPurchase(rs));
            }
        }
        return expiredPurchases;
    }

    @Override
    public int cleanupExpired() throws SQLException {
        String sql = "DELETE FROM TemporaryVIPPurchases WHERE ExpiresAt <= GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            return statement.executeUpdate();
        }
    }

    @Override
    public boolean existsValidBySessionId(String sessionID) throws SQLException {
        String sql = "SELECT 1 FROM TemporaryVIPPurchases WHERE SessionID = ? AND ExpiresAt > GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, sessionID);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }
}