package vn.vnrailway.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.model.Feedback;

public class FeedbackDAOImpl implements FeedbackDAO {

    private DataSource dataSource;

    public FeedbackDAOImpl() {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/FeedbackDB");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedback (Title, Comment) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, feedback.getTitle());
            preparedStatement.setString(2, feedback.getComment());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately (e.g., log it, rethrow it, etc.)
        }
    }
}
