package vn.vnrailway.controller.admin;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/editUser")
public class EditUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            User user = userOpt.get();
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/editUser.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching user");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String role = request.getParameter("role");
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));

        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            User existingUser = userOpt.get();
            
            logFieldChange(request, "Users", String.valueOf(userId), "FullName", existingUser.getFullName(), fullName);
            logFieldChange(request, "Users", String.valueOf(userId), "Email", existingUser.getEmail(), email);
            logFieldChange(request, "Users", String.valueOf(userId), "PhoneNumber", existingUser.getPhoneNumber(), phoneNumber);
            logFieldChange(request, "Users", String.valueOf(userId), "Role", existingUser.getRole(), role);
            logFieldChange(request, "Users", String.valueOf(userId), "IsActive", String.valueOf(existingUser.isActive()), String.valueOf(isActive));

            existingUser.setFullName(fullName);
            existingUser.setEmail(email);
            existingUser.setPhoneNumber(phoneNumber);
            existingUser.setRole(role);
            existingUser.setActive(isActive);

            userRepository.update(existingUser);
            response.sendRedirect(request.getContextPath() + "/admin/userManagement");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error updating user");
        }
    }

    private void logFieldChange(HttpServletRequest request, String tableName, String rowId, String columnName, String oldValue, String newValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            String sql = "INSERT INTO dbo.AuditLogs (LogTime, Username, TableName, RowId, ColumnName, OldValue, NewValue) VALUES (GETDATE(), ?, ?, ?, ?, ?, ?)";
            try (Connection connection = vn.vnrailway.utils.DBContext.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                HttpSession session = request.getSession(false);
                String loggedInUsername = "N/A";
                if (session != null) {
                    User loggedInUser = (User) session.getAttribute("loggedInUser");
                    if (loggedInUser != null) {
                        loggedInUsername = loggedInUser.getEmail();
                    }
                }

                preparedStatement.setString(1, loggedInUsername);
                preparedStatement.setString(2, tableName);
                preparedStatement.setString(3, rowId);
                preparedStatement.setString(4, columnName);
                preparedStatement.setString(5, oldValue);
                preparedStatement.setString(6, newValue);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
