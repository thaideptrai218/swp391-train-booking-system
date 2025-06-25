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
        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));
        System.out.println("Action: " + action + ", UserID: " + userId);

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            User existingUser = userOpt.get();

            switch (action) {
                case "delete":
                    if ("Customer".equals(existingUser.getRole())) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot delete customer accounts. Only locking is allowed.");
                        return;
                    }
                    userRepository.deleteById(userId);
                    logAudit(request, "User", "Delete", String.valueOf(userId), existingUser.toString(), null);
                    break;
                case "lock":
                    userRepository.lockById(userId);
                    logAudit(request, "User", "Lock", String.valueOf(userId), existingUser.toString(), null);
                    break;
                case "unlock":
                    userRepository.unlockById(userId);
                    logAudit(request, "User", "Unlock", String.valueOf(userId), existingUser.toString(), null);
                    break;
                case "hide":
                    userRepository.hideById(userId);
                    logAudit(request, "User", "Hide", String.valueOf(userId), existingUser.toString(), null);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                    return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/userManagement");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error performing action");
        }
    }

    private void logAudit(HttpServletRequest request, String tableName, String action, String rowId, String oldValue, String newValue) {
        String sql = "INSERT INTO dbo.AuditLogs (LogTime, Username, TableName, RowId, ColumnName, OldValue, NewValue) VALUES (GETDATE(), ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBContext.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            HttpSession session = request.getSession(false);
            String loggedInUsername = (session != null && session.getAttribute("userName") != null) ? session.getAttribute("userName").toString() : "N/A";

            preparedStatement.setString(1, loggedInUsername);
            preparedStatement.setString(2, tableName);
            preparedStatement.setString(3, rowId);
            preparedStatement.setString(4, null); // ColumnName is not applicable here
            preparedStatement.setString(5, oldValue);
            preparedStatement.setString(6, newValue);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the exception to a file or monitoring system
        }
    }
}
