package vn.vnrailway.controller.customer;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.dao.impl.FeedbackDAOImpl;
import vn.vnrailway.model.Feedback;
import vn.vnrailway.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
            List<Feedback> feedbacks = feedbackDAO.getFeedbacksByUserId(loggedInUser.getUserID());
            System.out.println("Number of feedbacks for user " + loggedInUser.getUserID() + ": " + feedbacks.size());
            request.setAttribute("feedbacks", feedbacks);
        } else {
            System.out.println("No loggedInUser in session");
            request.setAttribute("feedbacks", new ArrayList<>());
        }
        System.out.println(
                "Forwarding to JSP with feedbacks size: " + ((List<?>) request.getAttribute("feedbacks")).size());
        request.getRequestDispatcher("/WEB-INF/jsp/customer/feedback.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String feedbackContent = request.getParameter("feedbackContent");
        String ticketType = request.getParameter("ticketType");
        String ticketName = request.getParameter("ticketName");
        String description = request.getParameter("description");

        int feedbackTypeId = 1;
        if ("2".equals(ticketType))
            feedbackTypeId = 2;
        if ("3".equals(ticketType))
            feedbackTypeId = 3;

        HttpSession session = request.getSession();
        Integer userId = null;
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            userId = loggedInUser.getUserID();
        }

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackTypeId(feedbackTypeId);
        feedback.setFullName(fullName);
        feedback.setEmail(email);
        feedback.setFeedbackContent(feedbackContent);
        feedback.setTicketName(ticketName);
        feedback.setDescription(description);
        feedback.setSubmittedAt(new Date());
        feedback.setStatus("Pending");

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        feedbackDAO.saveFeedback(feedback);

        request.setAttribute("feedbackSuccess", true);
        doGet(request, response); // Gọi lại doGet để hiển thị danh sách
    }
}