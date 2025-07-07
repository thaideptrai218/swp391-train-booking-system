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
    private MessageDAO messageDAO = new MessageImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<MessageSummary> chatSummaries = messageDAO.getChatSummaries();
        String userId = request.getParameter("userId");
        if (userId != null) {
            int uid = Integer.parseInt(userId);
            request.setAttribute("selectedUserId", uid);
            request.setAttribute("chatMessages", messageDAO.getChatMessagesByUserId(uid));
        }
        request.setAttribute("chatSummaries", chatSummaries);
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
        response.sendRedirect(request.getContextPath() + "/staff-message?userId=" + userId);
    }
}
