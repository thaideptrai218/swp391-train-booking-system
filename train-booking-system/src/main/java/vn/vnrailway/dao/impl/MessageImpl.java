package vn.vnrailway.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.MessageDAO;
import vn.vnrailway.model.Message;
import vn.vnrailway.model.MessageSummary;

public class MessageImpl implements MessageDAO {
    @Override
    public List<Message> getChatMessages(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT MessageID, UserID, StaffID, Content, Timestamp, SenderType FROM Messages WHERE UserID = ? ORDER BY Timestamp ASC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(
                            rs.getInt("MessageID"),
                            rs.getInt("UserID"),
                            rs.getInt("StaffID"),
                            rs.getString("Content"),
                            rs.getTimestamp("Timestamp").toLocalDateTime(),
                            rs.getString("SenderType"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public void saveMessage(int userId, Integer staffId, String content, String senderType) {
        String sql = "INSERT INTO Messages (UserID, StaffID, Content, SenderType) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setObject(2, staffId);
            ps.setString(3, content);
            ps.setString(4, senderType);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Message> getNewMessages(int userId, int lastMessageId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT MessageID, UserID, StaffID, Content, Timestamp, SenderType FROM Messages WHERE UserID = ? AND MessageID > ? ORDER BY Timestamp ASC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, lastMessageId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(
                            rs.getInt("MessageID"),
                            rs.getInt("UserID"),
                            rs.getInt("StaffID"),
                            rs.getString("Content"),
                            rs.getTimestamp("Timestamp").toLocalDateTime(),
                            rs.getString("SenderType"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public List<Message> getChatMessagesByUserId(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT MessageID, UserID, StaffID, Content, Timestamp, SenderType FROM Messages WHERE UserID = ? ORDER BY Timestamp ASC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message msg = new Message(
                            rs.getInt("MessageID"),
                            rs.getInt("UserID"),
                            rs.getInt("StaffID"),
                            rs.getString("Content"),
                            rs.getTimestamp("Timestamp").toLocalDateTime(),
                            rs.getString("SenderType"));
                    messages.add(msg);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public List<MessageSummary> getChatSummariesWithPagination(int offset, int limit) {
        List<MessageSummary> summaries = new ArrayList<>();
        String sql = "SELECT u.UserID, u.FullName, u.Email, cm.Content AS LastMessage " +
                "FROM Users u " +
                "LEFT JOIN (SELECT c1.UserID, c1.Content, c1.Timestamp " +
                "           FROM Messages c1 " +
                "           INNER JOIN (SELECT UserID, MAX(Timestamp) AS MaxTimestamp " +
                "                       FROM Messages " +
                "                       WHERE SenderType = 'Customer' " +
                "                       GROUP BY UserID) c2 " +
                "           ON c1.UserID = c2.UserID AND c1.Timestamp = c2.MaxTimestamp " +
                "           WHERE c1.SenderType = 'Customer') cm " +
                "ON u.UserID = cm.UserID " +
                "WHERE EXISTS (SELECT 1 FROM Messages WHERE UserID = u.UserID AND SenderType = 'Customer') " +
                "ORDER BY u.UserID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MessageSummary summary = new MessageSummary(
                            rs.getInt("UserID"),
                            rs.getString("FullName"),
                            rs.getString("Email"),
                            rs.getString("LastMessage"));
                    summaries.add(summary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summaries;
    }

    public int getTotalChatSummariesCount() {
        int count = 0;
        String sql = "SELECT COUNT(DISTINCT u.UserID) " +
                "FROM Users u " +
                "WHERE EXISTS (SELECT 1 FROM Messages WHERE UserID = u.UserID AND SenderType = 'Customer')";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
