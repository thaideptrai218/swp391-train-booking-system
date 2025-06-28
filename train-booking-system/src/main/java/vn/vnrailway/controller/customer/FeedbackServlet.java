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

import java.util.Date;

@WebServlet("/feedback")
public class FeedbackServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        // Ánh xạ ticketType sang FeedbackTypeID
        int feedbackTypeId = 1; // Default là Góp ý (ID 1)
        if ("2".equals(ticketType))
            feedbackTypeId = 2; // Phản ánh
        if ("3".equals(ticketType))
            feedbackTypeId = 3; // Đề xuất

        // Lấy UserID từ session (nếu đã đăng nhập)
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
        doGet(request, response);
    }
}