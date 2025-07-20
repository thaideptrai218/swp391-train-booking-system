package vn.vnrailway.controller.staff;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.MessageDAO;
import vn.vnrailway.dao.impl.MessageImpl;
import vn.vnrailway.model.MessageSummary;
import vn.vnrailway.model.User;

@WebServlet("/staff-message")
public class StaffMessageServlet extends HttpServlet {
    private static final int ITEMS_PER_PAGE = 5;
    private MessageDAO messageDAO = new MessageImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            currentPage = Integer.parseInt(pageParam);
        }
        if (currentPage < 1)
            currentPage = 1;

        int offset = (currentPage - 1) * ITEMS_PER_PAGE;
        List<MessageSummary> chatSummaries = messageDAO.getChatSummariesWithPagination(offset, ITEMS_PER_PAGE);

        int totalSummaries = messageDAO.getTotalChatSummariesCount();
        int totalPages = (int) Math.ceil((double) totalSummaries / ITEMS_PER_PAGE);

        String userId = request.getParameter("userId");
        if (userId != null) {
            int uid = Integer.parseInt(userId);
            request.setAttribute("selectedUserId", uid);
            request.setAttribute("chatMessages", messageDAO.getChatMessagesByUserId(uid));
        }

        request.setAttribute("chatSummaries", chatSummaries);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/staff-message.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = Integer.parseInt(request.getParameter("userId"));
        String message = request.getParameter("message");
        if (message != null && !message.isEmpty()) {
            int staffId = (int) loggedInUser.getUserID();
            messageDAO.saveMessage(userId, staffId, message, "Staff");
        }
        String pageParam = request.getParameter("page");
        String redirectUrl = request.getContextPath() + "/staff-message?userId=" + userId;
        if (pageParam != null && !pageParam.isEmpty()) {
            redirectUrl += "&page=" + pageParam;
        }
        response.sendRedirect(redirectUrl);
    }
}
