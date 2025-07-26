package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.model.UserVIPCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserVIPCardRepositoryImpl implements UserVIPCardRepository {

    @Override
    public void save(UserVIPCard userVIPCard) throws SQLException {
        String sql = "INSERT INTO UserVIPCards (UserID, VIPCardTypeID, PurchaseDate, ExpiryDate, IsActive, TransactionReference) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userVIPCard.getUserID());
            ps.setInt(2, userVIPCard.getVipCardTypeID());
            ps.setTimestamp(3, Timestamp.valueOf(userVIPCard.getPurchaseDate()));
            ps.setTimestamp(4, Timestamp.valueOf(userVIPCard.getExpiryDate()));
            ps.setBoolean(5, userVIPCard.isActive());
            ps.setString(6, userVIPCard.getTransactionReference());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<UserVIPCard> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM UserVIPCards WHERE UserID = ? AND IsActive = 1 ORDER BY ExpiryDate DESC";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserVIPCard(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<UserVIPCard> findActiveByUserId(int userId) throws SQLException {
        List<UserVIPCard> cards = new ArrayList<>();
        String sql = "SELECT * FROM UserVIPCards WHERE UserID = ? AND IsActive = 1";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cards.add(mapRowToUserVIPCard(rs));
                }
            }
        }
        return cards;
    }

    @Override
    public Optional<UserVIPCard> findByTransactionReference(String transactionReference) throws SQLException {
        String sql = "SELECT * FROM UserVIPCards WHERE TransactionReference = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, transactionReference);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUserVIPCard(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void deactivateAllForUser(int userId) throws SQLException {
        String sql = "UPDATE UserVIPCards SET IsActive = 0 WHERE UserID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public void create(UserVIPCard userVIPCard) throws SQLException {
        save(userVIPCard);
    }

    @Override
    public void update(UserVIPCard userVIPCard) throws SQLException {
        String sql = "UPDATE UserVIPCards SET UserID = ?, VIPCardTypeID = ?, PurchaseDate = ?, ExpiryDate = ?, IsActive = ?, TransactionReference = ? WHERE UserVIPCardID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userVIPCard.getUserID());
            ps.setInt(2, userVIPCard.getVipCardTypeID());
            ps.setTimestamp(3, Timestamp.valueOf(userVIPCard.getPurchaseDate()));
            ps.setTimestamp(4, Timestamp.valueOf(userVIPCard.getExpiryDate()));
            ps.setBoolean(5, userVIPCard.isActive());
            ps.setString(6, userVIPCard.getTransactionReference());
            ps.setInt(7, userVIPCard.getUserVIPCardID());
            ps.executeUpdate();
        }
    }

    @Override
    public float getVIPDiscountPercentage(int userId) throws SQLException {
        String sql = "SELECT vct.DiscountPercentage FROM UserVIPCards uvc JOIN VIPCardTypes vct ON uvc.VIPCardTypeID = vct.VIPCardTypeID WHERE uvc.UserID = ? AND uvc.IsActive = 1 AND uvc.ExpiryDate > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("DiscountPercentage");
                }
            }
        }
        return 0;
    }

    private UserVIPCard mapRowToUserVIPCard(ResultSet rs) throws SQLException {
        return new UserVIPCard(
                rs.getInt("UserVIPCardID"),
                rs.getInt("UserID"),
                rs.getInt("VIPCardTypeID"),
                rs.getTimestamp("PurchaseDate").toLocalDateTime(),
                rs.getTimestamp("ExpiryDate").toLocalDateTime(),
                rs.getBoolean("IsActive"),
                rs.getString("TransactionReference")
        );
    }
}
