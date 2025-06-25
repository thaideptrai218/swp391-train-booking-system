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

        String customerName = request.getParameter("fullName");
        String customerEmail = request.getParameter("email");
        String message = request.getParameter("feedbackContent");
        // Assuming 'subject' is not directly from the form, or can be derived/defaulted
        String subject = "General Feedback"; // Default subject or derive from ticketType if needed

        Feedback feedback = new Feedback();
        feedback.setCustomerName(customerName);
        feedback.setCustomerEmail(customerEmail);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        feedback.setSubmissionDate(new Date());

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        feedbackDAO.saveFeedback(feedback);

        request.setAttribute("feedbackSuccess", true);
        doGet(request, response); // Forward to doGet to display the JSP
    }
}
