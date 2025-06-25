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
        String sql = "INSERT INTO Feedbacks (customer_name, customer_email, subject, message, submission_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, feedback.getCustomerName());
            ps.setString(2, feedback.getCustomerEmail());
            ps.setString(3, feedback.getSubject());
            ps.setString(4, feedback.getMessage());
            ps.setTimestamp(5, new Timestamp(feedback.getSubmissionDate().getTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Feedback> getAllFeedbacks() {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT feedback_id, customer_name, customer_email, subject, message, submission_date FROM Feedbacks ORDER BY submission_date DESC";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setFeedbackId(rs.getInt("feedback_id"));
                feedback.setCustomerName(rs.getString("customer_name"));
                feedback.setCustomerEmail(rs.getString("customer_email"));
                feedback.setSubject(rs.getString("subject"));
                feedback.setMessage(rs.getString("message"));
                feedback.setSubmissionDate(rs.getTimestamp("submission_date"));
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }
}
