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

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/customer/feedback.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String comment = request.getParameter("comment");

        Feedback feedback = new Feedback(title, comment);

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        feedbackDAO.saveFeedback(feedback);

        response.sendRedirect("/train-booking-system/feedback");
    }
}
