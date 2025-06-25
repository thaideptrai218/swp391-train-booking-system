package vn.vnrailway.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/auditLog")
public class AuditLogServlet extends HttpServlet {
    private final UserRepository userRepository = new UserRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("loggedInUser");
            request.setAttribute("user", user);
        }

        int page = 1;
        int pageSize = 10;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        try {
            List<Object[]> auditLogs = userRepository.getLogsByPage(page, pageSize);
            int totalLogs = userRepository.getTotalLogCount();
            int totalPages = (int) Math.ceil((double) totalLogs / pageSize);

            request.setAttribute("auditLogs", auditLogs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/auditLog.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Database error while retrieving audit logs", e);
        }
    }
}
