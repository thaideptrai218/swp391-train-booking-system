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

@WebServlet("/admin/addUser")
public class AddUserServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserRepositoryImpl userRepository = new UserRepositoryImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/admin/addUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String role = request.getParameter("role");

        User newUser = new User();
        newUser.setPasswordHash(vn.vnrailway.utils.HashPassword.hashPassword(password));
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setRole(role);
        newUser.setActive(true);

        try {
            User savedUser = userRepository.save(newUser);
            logAudit(request, "Users", "Insert", String.valueOf(savedUser.getUserID()), null, savedUser.toString());
            response.sendRedirect(request.getContextPath() + "/admin/userManagement");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding user");
        }
    }

    private void logAudit(HttpServletRequest request, String tableName, String action, String rowId, String oldValue,
            String newValue) {
        String sql = "INSERT INTO dbo.AuditLogs (LogTime, Username, TableName, RowId, ColumnName, OldValue, NewValue) VALUES (GETDATE(), ?, ?, ?, ?, ?, ?)";
        try (java.sql.Connection connection = vn.vnrailway.utils.DBContext.getConnection();
                java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            jakarta.servlet.http.HttpSession session = request.getSession(false);
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
            preparedStatement.setString(4, action); // Using ColumnName to store the action
            preparedStatement.setString(5, oldValue);
            preparedStatement.setString(6, newValue);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
