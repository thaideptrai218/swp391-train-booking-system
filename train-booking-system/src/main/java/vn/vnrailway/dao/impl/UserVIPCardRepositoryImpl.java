package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.model.UserVIPCard;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserVIPCardRepository for database operations.
 */
public class UserVIPCardRepositoryImpl implements UserVIPCardRepository {

    /**
     * Maps ResultSet to UserVIPCard object
     */
    private UserVIPCard mapResultSetToUserVIPCard(ResultSet rs) throws SQLException {
        UserVIPCard userVIPCard = new UserVIPCard();
        userVIPCard.setUserVIPCardID(rs.getInt("UserVIPCardID"));
        userVIPCard.setUserID(rs.getInt("UserID"));
        userVIPCard.setVipCardTypeID(rs.getInt("VIPCardTypeID"));
        
        Timestamp purchaseDateTimestamp = rs.getTimestamp("PurchaseDate");
        if (purchaseDateTimestamp != null) {
            userVIPCard.setPurchaseDate(purchaseDateTimestamp.toLocalDateTime());
        }
        
        Timestamp expiryDateTimestamp = rs.getTimestamp("ExpiryDate");
        if (expiryDateTimestamp != null) {
            userVIPCard.setExpiryDate(expiryDateTimestamp.toLocalDateTime());
        }
        
        userVIPCard.setActive(rs.getBoolean("IsActive"));
        userVIPCard.setTransactionReference(rs.getString("TransactionReference"));
        
        return userVIPCard;
    }

    @Override
    public UserVIPCard create(UserVIPCard userVIPCard) throws SQLException {
        String sql = "INSERT INTO UserVIPCards (UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, userVIPCard.getUserID());
            statement.setInt(2, userVIPCard.getVipCardTypeID());
            statement.setTimestamp(3, Timestamp.valueOf(userVIPCard.getPurchaseDate()));
            statement.setTimestamp(4, Timestamp.valueOf(userVIPCard.getExpiryDate()));
            statement.setBoolean(5, userVIPCard.isActive());
            statement.setString(6, userVIPCard.getTransactionReference());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user VIP card failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userVIPCard.setUserVIPCardID(generatedKeys.getInt(1));
                    return userVIPCard;
                } else {
                    throw new SQLException("Creating user VIP card failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public Optional<UserVIPCard> findById(int userVIPCardID) throws SQLException {
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE UserVIPCardID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userVIPCardID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUserVIPCard(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserVIPCard> findActiveByUserId(int userID) throws SQLException {
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE UserID = ? AND IsActive = 1 AND ExpiryDate > GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUserVIPCard(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserVIPCard> findAllByUserId(int userID) throws SQLException {
        List<UserVIPCard> userVIPCards = new ArrayList<>();
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE UserID = ? ORDER BY PurchaseDate DESC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    userVIPCards.add(mapResultSetToUserVIPCard(rs));
                }
            }
        }
        return userVIPCards;
    }

    @Override
    public Optional<UserVIPCard> findByTransactionReference(String transactionReference) throws SQLException {
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE TransactionReference = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, transactionReference);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUserVIPCard(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean updateStatus(int userVIPCardID, boolean isActive) throws SQLException {
        String sql = "UPDATE UserVIPCards SET IsActive = ? WHERE UserVIPCardID = ?";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setBoolean(1, isActive);
            statement.setInt(2, userVIPCardID);
            
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public int deactivateAllForUser(int userID) throws SQLException {
        String sql = "UPDATE UserVIPCards SET IsActive = 0 WHERE UserID = ? AND IsActive = 1";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            return statement.executeUpdate();
        }
    }

    @Override
    public List<UserVIPCard> findExpired() throws SQLException {
        List<UserVIPCard> expiredCards = new ArrayList<>();
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE IsActive = 1 AND ExpiryDate <= GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            
            while (rs.next()) {
                expiredCards.add(mapResultSetToUserVIPCard(rs));
            }
        }
        return expiredCards;
    }

    @Override
    public int deactivateExpired() throws SQLException {
        String sql = "UPDATE UserVIPCards SET IsActive = 0 WHERE IsActive = 1 AND ExpiryDate <= GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            return statement.executeUpdate();
        }
    }

    @Override
    public boolean hasActiveVIPCard(int userID) throws SQLException {
        String sql = "SELECT 1 FROM UserVIPCards WHERE UserID = ? AND IsActive = 1 AND ExpiryDate > GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public double getVIPDiscountPercentage(int userID) throws SQLException {
        String sql = "SELECT vct.DiscountPercentage FROM UserVIPCards uvc " +
                    "JOIN VIPCardTypes vct ON uvc.VIPCardTypeID = vct.VIPCardTypeID " +
                    "WHERE uvc.UserID = ? AND uvc.IsActive = 1 AND uvc.ExpiryDate > GETDATE()";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userID);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("DiscountPercentage");
                }
            }
        }
        return 0.0; // No active VIP card
    }

    @Override
    public List<UserVIPCard> findExpiringSoon(int daysFromNow) throws SQLException {
        List<UserVIPCard> expiringSoonCards = new ArrayList<>();
        String sql = "SELECT UserVIPCardID, UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference " +
                    "FROM UserVIPCards WHERE IsActive = 1 AND ExpiryDate > GETDATE() " +
                    "AND ExpiryDate <= DATEADD(DAY, ?, GETDATE()) ORDER BY ExpiryDate ASC";
        
        try (Connection connection = DBContext.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, daysFromNow);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    expiringSoonCards.add(mapResultSetToUserVIPCard(rs));
                }
            }
        }
        return expiringSoonCards;
    }
}