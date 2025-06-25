package vn.vnrailway.controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.dao.impl.FeedbackDAOImpl;
import vn.vnrailway.model.Feedback;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff/feedback")
public class FeedbackListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        List<Feedback> feedbacks = feedbackDAO.getAllFeedbacks();

        request.setAttribute("feedbacks", feedbacks);

        request.getRequestDispatcher("/WEB-INF/jsp/staff/feedback-list.jsp")
               .forward(request, response);
    }
}
