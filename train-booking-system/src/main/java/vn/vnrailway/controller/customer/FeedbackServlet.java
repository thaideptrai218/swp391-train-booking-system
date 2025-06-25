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

import java.util.Date;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/customer/feedback.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerName = request.getParameter("customerName");
        String customerEmail = request.getParameter("customerEmail");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        Feedback feedback = new Feedback();
        feedback.setCustomerName(customerName);
        feedback.setCustomerEmail(customerEmail);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        feedback.setSubmissionDate(new Date());

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        feedbackDAO.saveFeedback(feedback);

        response.sendRedirect(request.getContextPath() + "/feedback");
    }
}
