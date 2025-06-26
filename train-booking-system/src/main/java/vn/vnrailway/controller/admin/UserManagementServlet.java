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

        try {
            List<User> users = userRepository.findAll();
            request.setAttribute("users", users);
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
