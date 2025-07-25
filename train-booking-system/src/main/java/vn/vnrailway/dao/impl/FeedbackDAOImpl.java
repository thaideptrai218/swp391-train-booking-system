package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.model.Feedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAOImpl implements FeedbackDAO {

    @Override
    public void saveFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedback (UserID, FeedbackTypeID, FullName, Email, FeedbackContent, TicketName, Description, SubmittedAt, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (feedback.getUserId() != null) {
                ps.setInt(1, feedback.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, feedback.getFeedbackTypeId());
            ps.setString(3, feedback.getFullName());
            ps.setString(4, feedback.getEmail());
            ps.setString(5, feedback.getFeedbackContent());
            ps.setString(6, feedback.getTicketName());
            ps.setString(7, feedback.getDescription());
            ps.setTimestamp(8, new Timestamp(feedback.getSubmittedAt().getTime()));
            ps.setString(9, feedback.getStatus());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    feedback.setFeedbackId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Feedback> getPendingFeedbacks() {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE Status = 'Pending' ORDER BY SubmittedAt DESC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("FeedbackID"));
                feedback.setUserId(rs.getInt("UserID"));
                feedback.setFeedbackTypeId(rs.getInt("FeedbackTypeID"));
                feedback.setFullName(rs.getString("FullName"));
                feedback.setEmail(rs.getString("Email"));
                feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                feedback.setTicketName(rs.getString("TicketName"));
                feedback.setDescription(rs.getString("Description"));
                feedback.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
                feedback.setStatus(rs.getString("Status"));
                feedback.setResponse(rs.getString("Response"));
                feedback.setRespondedAt(rs.getTimestamp("RespondedAt"));
                feedback.setRespondedByUserId(rs.getInt("RespondedByUserID"));
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

    @Override
    public void updateFeedback(Feedback feedback) {
        String sql = "UPDATE Feedback SET Response = ?, RespondedAt = ?, RespondedByUserID = ?, Status = 'Reviewed' WHERE FeedbackID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, feedback.getResponse());
            ps.setTimestamp(2, new Timestamp(feedback.getRespondedAt() != null ? feedback.getRespondedAt().getTime()
                    : new java.util.Date().getTime()));
            ps.setInt(3, feedback.getRespondedByUserId() != null ? feedback.getRespondedByUserId() : 0);
            ps.setInt(4, feedback.getFeedbackId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Feedback getFeedbackById(int feedbackId) {
        Feedback feedback = null;
        String sql = "SELECT * FROM Feedback WHERE FeedbackID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    feedback = new Feedback();
                    feedback.setFeedbackId(rs.getInt("FeedbackID"));
                    feedback.setUserId(rs.getInt("UserID"));
                    feedback.setFeedbackTypeId(rs.getInt("FeedbackTypeID"));
                    feedback.setFullName(rs.getString("FullName"));
                    feedback.setEmail(rs.getString("Email"));
                    feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                    feedback.setTicketName(rs.getString("TicketName"));
                    feedback.setDescription(rs.getString("Description"));
                    feedback.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
                    feedback.setStatus(rs.getString("Status"));
                    feedback.setResponse(rs.getString("Response"));
                    feedback.setRespondedAt(rs.getTimestamp("RespondedAt"));
                    feedback.setRespondedByUserId(rs.getInt("RespondedByUserID"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedback;
    }

    @Override
    public List<Feedback> getFeedbacksByUserId(Integer userId) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE UserID = ? ORDER BY SubmittedAt DESC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) {
                ps.setInt(1, userId);
                System.out.println("Querying feedbacks for UserID: " + userId);
            } else {
                ps.setNull(1, Types.INTEGER);
                System.out.println("UserID is null, querying all feedbacks");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackId(rs.getInt("FeedbackID"));
                    feedback.setUserId(rs.getInt("UserID"));
                    feedback.setFeedbackTypeId(rs.getInt("FeedbackTypeID"));
                    feedback.setFullName(rs.getString("FullName"));
                    feedback.setEmail(rs.getString("Email"));
                    feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                    feedback.setTicketName(rs.getString("TicketName"));
                    feedback.setDescription(rs.getString("Description"));
                    feedback.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
                    feedback.setStatus(rs.getString("Status"));
                    feedback.setResponse(rs.getString("Response"));
                    feedback.setRespondedAt(rs.getTimestamp("RespondedAt"));
                    feedback.setRespondedByUserId(rs.getInt("RespondedByUserID"));
                    feedbacks.add(feedback);
                }
                System.out.println("Found " + feedbacks.size() + " feedbacks");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

    // phương thức phân trang
    @Override
    public List<Feedback> getFeedbacksWithPagination(int offset, int limit) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY SubmittedAt DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback feedback = new Feedback();
                    feedback.setFeedbackId(rs.getInt("FeedbackID"));
                    feedback.setUserId(rs.getInt("UserID"));
                    feedback.setFeedbackTypeId(rs.getInt("FeedbackTypeID"));
                    feedback.setFullName(rs.getString("FullName"));
                    feedback.setEmail(rs.getString("Email"));
                    feedback.setFeedbackContent(rs.getString("FeedbackContent"));
                    feedback.setTicketName(rs.getString("TicketName"));
                    feedback.setDescription(rs.getString("Description"));
                    feedback.setSubmittedAt(rs.getTimestamp("SubmittedAt"));
                    feedback.setStatus(rs.getString("Status"));
                    feedback.setResponse(rs.getString("Response"));
                    feedback.setRespondedAt(rs.getTimestamp("RespondedAt"));
                    feedback.setRespondedByUserId(rs.getInt("RespondedByUserID"));
                    feedbacks.add(feedback);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }

    // đếm tổng số phản hồi
    public int getTotalFeedbackCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Feedback";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}