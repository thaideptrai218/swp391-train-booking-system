package vn.vnrailway.controller.customer;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.dao.impl.FeedbackDAOImpl;
import vn.vnrailway.model.Feedback;

@WebServlet("/submitFeedback")
public class SubmitFeedbackServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String comment = request.getParameter("comment");

        // Feedback feedback = new Feedback(title, comment);

        // FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        // feedbackDAO.saveFeedback(feedback);

        response.sendRedirect("/train-booking-system/feedback");
    }
}
