package vn.vnrailway.controller.staff;

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
import vn.vnrailway.dao.MessageDAO;
import vn.vnrailway.dao.impl.MessageImpl;
import vn.vnrailway.model.Message;

@WebServlet("/staff/fetch-messages")
public class StaffFetchMessagesServlet extends HttpServlet {
    private MessageDAO messageDAO = new MessageImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdParam = request.getParameter("userId");
        String lastMessageIdParam = request.getParameter("lastMessageId");

        if (userIdParam == null || userIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        int lastMessageId = lastMessageIdParam != null ? Integer.parseInt(lastMessageIdParam) : 0;

        List<Message> newMessages = messageDAO.getMessagesAfter(userId, lastMessageId);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), newMessages);
        ;
    }
}
