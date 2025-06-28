package vn.vnrailway.dao;

import vn.vnrailway.model.Feedback;
import java.util.List;

public interface FeedbackDAO {
    void saveFeedback(Feedback feedback);

    List<Feedback> getAllFeedbacks();

    List<Feedback> getPendingFeedbacks();

    void updateFeedback(Feedback feedback);

    Feedback getFeedbackById(int feedbackId);

    List<Feedback> getFeedbacksByUserId(Integer userId);
}