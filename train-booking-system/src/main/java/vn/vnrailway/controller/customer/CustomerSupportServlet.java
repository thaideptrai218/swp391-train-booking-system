package vn.vnrailway.controller.customer;

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
import vn.vnrailway.model.Message;
import vn.vnrailway.model.User;

@WebServlet("/customer-support")
public class CustomerSupportServlet extends HttpServlet {
    private MessageDAO messageDAO = new MessageImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        int userId = loggedInUser.getUserID();
        List<Message> chatMessages = messageDAO.getChatMessages(userId);
        request.setAttribute("chatMessages", chatMessages);
        request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-support.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        String message = request.getParameter("message");
        if (message != null && !message.isEmpty()) {
            int userId = loggedInUser.getUserID();
            messageDAO.saveMessage(userId, null, message, "Customer");
        }
        response.sendRedirect(request.getContextPath() + "/customer-support");
    }
}
