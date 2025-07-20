package vn.vnrailway.controller.staff;

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

import java.io.IOException;
import java.util.List;

@WebServlet("/staff/feedback")
public class FeedbackListServlet extends HttpServlet {
    private static final int ITEMS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();

        // lấy trang hiện tại, mặc định là 1 nếu không có
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            currentPage = Integer.parseInt(pageParam);
        }
        if (currentPage < 1)
            currentPage = 1;

        // tính toán offset và giới hạn
        int offset = (currentPage - 1) * ITEMS_PER_PAGE;
        List<Feedback> feedbacks = feedbackDAO.getFeedbacksWithPagination(offset, ITEMS_PER_PAGE);

        // tính tổng số phản hồi để xác định tổng số trang
        int totalFeedbacks = feedbackDAO.getTotalFeedbackCount();
        int totalPages = (int) Math.ceil((double) totalFeedbacks / ITEMS_PER_PAGE);

        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/feedback-list.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int feedbackId = Integer.parseInt(request.getParameter("feedbackId"));
        String responseText = request.getParameter("response");

        FeedbackDAO feedbackDAO = new FeedbackDAOImpl();
        Feedback feedback = feedbackDAO.getFeedbackById(feedbackId);
        HttpSession session = request.getSession();
        Integer staffId = null;
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            staffId = loggedInUser.getUserID();
        }
        if (feedback != null) {
            feedback.setResponse(responseText);
            feedback.setRespondedAt(new java.util.Date());
            if (staffId != null) {
                feedback.setRespondedByUserId(staffId);
            }
            feedbackDAO.updateFeedback(feedback);
        }

        response.sendRedirect(request.getContextPath() + "/staff/feedback");
    }
}