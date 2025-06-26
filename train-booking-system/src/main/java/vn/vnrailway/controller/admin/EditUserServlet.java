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
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            doPost(request, response);
            return;
        }
        
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
        String action = request.getParameter("action");
        if (action != null && action.equals("delete")) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            try {
                Optional<User> userOpt = userRepository.findById(userId);
                if (!userOpt.isPresent()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                    return;
                }
                User existingUser = userOpt.get();
                userRepository.lockById(userId); // Perform a soft delete by locking the account
                String newValue = String.format("Xóa tài khoản với ID: %d, tên: %s, email: %s, sđt: %s, role: %s",
                        existingUser.getUserID(), existingUser.getFullName(), existingUser.getEmail(), existingUser.getPhoneNumber(), existingUser.getRole());
                logFieldChange(request, existingUser.getEmail(), "User", "Locked", newValue);
                response.sendRedirect(request.getContextPath() + "/admin/userManagement");
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting user");
            }
        } else {
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
                
                logFieldChange(request, existingUser.getEmail(), "FullName", existingUser.getFullName(), fullName);
                logFieldChange(request, existingUser.getEmail(), "Email", existingUser.getEmail(), email);
                logFieldChange(request, existingUser.getEmail(), "PhoneNumber", existingUser.getPhoneNumber(), phoneNumber);
                logFieldChange(request, existingUser.getEmail(), "Role", existingUser.getRole(), role);
                logFieldChange(request, existingUser.getEmail(), "IsActive", String.valueOf(existingUser.isActive()), String.valueOf(isActive));

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
    }

    private void logFieldChange(HttpServletRequest request, String targetEmail, String columnName, String oldValue, String newValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            String sql = "INSERT INTO dbo.AuditLogs (EditorEmail, Action, TargetEmail, OldValue, NewValue, LogTime) VALUES (?, ?, ?, ?, ?, GETDATE())";
            try (Connection connection = vn.vnrailway.utils.DBContext.getConnection();
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
                preparedStatement.setString(2, "Edit " + columnName);
                preparedStatement.setString(3, targetEmail);
                preparedStatement.setString(4, oldValue);
                preparedStatement.setString(5, newValue);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
