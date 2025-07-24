package vn.vnrailway.controller.customer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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

@WebServlet("/customer-messages")
public class MessageFetchServlet extends HttpServlet {
    private MessageDAO messageDAO = new MessageImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"User not logged in\"}");
            return;
        }
        int userId = loggedInUser.getUserID();
        String lastIdParam = request.getParameter("lastMessageId");
        int lastMessageId = lastIdParam != null ? Integer.parseInt(lastIdParam) : 0;
        List<Message> messages = messageDAO.getNewMessages(userId, lastMessageId);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), messages);
    }
}