package vn.vnrailway.dao;

import vn.vnrailway.model.Feedback;
import java.util.List;

public interface FeedbackDAO {
    void saveFeedback(Feedback feedback);

    List<Feedback> getPendingFeedbacks();

    void updateFeedback(Feedback feedback);

    Feedback getFeedbackById(int feedbackId);

    List<Feedback> getFeedbacksByUserId(Integer userId);

    List<Feedback> getFeedbacksWithPagination(int offset, int limit);

    int getTotalFeedbackCount();
}