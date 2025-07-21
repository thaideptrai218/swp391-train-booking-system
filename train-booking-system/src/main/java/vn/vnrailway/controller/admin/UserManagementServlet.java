package vn.vnrailway.controller.admin;

import java.io.IOException;
import java.util.List;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.utils.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/userManagement")
public class UserManagementServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            request.setAttribute("user", user);
        }

        String searchTerm = request.getParameter("searchTerm");
        if (searchTerm != null) {
            searchTerm = searchTerm.trim();
        }
        String role = request.getParameter("role");
        String status = request.getParameter("status");

        int page = 1;
        int recordsPerPage = 20;
        if (request.getParameter("page") != null) {
            page = Integer.parseInt(request.getParameter("page"));
        }

        try {
            List<User> users = userRepository.searchAndFilterUsers(searchTerm, role, status, (page - 1) * recordsPerPage, recordsPerPage);
            int noOfRecords = userRepository.countFilteredUsers(searchTerm, role, status);
            int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);

            request.setAttribute("users", users);
            request.setAttribute("noOfPages", noOfPages);
            request.setAttribute("currentPage", page);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("selectedRole", role);
            request.setAttribute("selectedStatus", status);

            request.getRequestDispatcher("/WEB-INF/jsp/admin/userManagement.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching users");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // This servlet no longer handles actions, so this method can be left empty
        // or redirect to the user management page.
        response.sendRedirect(request.getContextPath() + "/admin/userManagement");
    }

    private void logAudit(HttpServletRequest request, String action, String targetEmail, String oldValue, String newValue) {
        String sql = "INSERT INTO dbo.AuditLogs (EditorEmail, Action, TargetEmail, OldValue, NewValue, LogTime) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            HttpSession session = request.getSession(false);
            String editorEmail = "N/A";
            if (session != null) {
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                if (loggedInUser != null) {
                    editorEmail = loggedInUser.getEmail();
                }
            }

            preparedStatement.setString(1, editorEmail);
            preparedStatement.setString(2, action);
            preparedStatement.setString(3, targetEmail);
            preparedStatement.setString(4, oldValue);
            preparedStatement.setString(5, newValue);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
