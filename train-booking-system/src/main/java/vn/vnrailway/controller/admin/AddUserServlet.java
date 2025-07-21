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
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String role = request.getParameter("role");

        // Server-side validation
        boolean isValid = true;
        if (fullName == null || !fullName.matches("^[a-zA-Z\\sÀ-ỹ]+$")) {
            request.setAttribute("errorMessage", "Họ và tên không hợp lệ.");
            isValid = false;
        }
        if (email == null || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            request.setAttribute("errorMessage", "Email không hợp lệ.");
            isValid = false;
        }
        if (phoneNumber == null || !phoneNumber.matches("^0\\d{9,10}$")) {
            request.setAttribute("errorMessage", "Số điện thoại không hợp lệ.");
            isValid = false;
        }
        if (password == null || password.length() < 8) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 8 ký tự.");
            isValid = false;
        }

        if (!isValid) {
            request.getRequestDispatcher("/WEB-INF/jsp/admin/addUser.jsp").forward(request, response);
            return;
        }

        try {
            if (userRepository.findByEmail(email).isPresent()) {
                request.setAttribute("errorMessage", "Email đã tồn tại.");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/addUser.jsp").forward(request, response);
                return;
            }
            if (userRepository.findByPhone(phoneNumber).isPresent()) {
                request.setAttribute("errorMessage", "Số điện thoại đã tồn tại.");
                request.getRequestDispatcher("/WEB-INF/jsp/admin/addUser.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error checking user existence");
            return;
        }

        User newUser = new User();
        newUser.setPasswordHash(vn.vnrailway.utils.HashPassword.hashPassword(password));
        newUser.setFullName(fullName);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setRole(role);
        newUser.setActive(true);

        try {
            User savedUser = userRepository.save(newUser);
            String newValue = String.format("Thêm tài khoản với ID: %d, tên: %s, email: %s, sđt: %s, role: %s",
                    savedUser.getUserID(), savedUser.getFullName(), savedUser.getEmail(), savedUser.getPhoneNumber(), savedUser.getRole());
            logAudit(request, "Add", savedUser.getEmail(), null, newValue);
            response.sendRedirect(request.getContextPath() + "/admin/userManagement");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error adding user: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/admin/addUser.jsp").forward(request, response);
        }
    }

    private void logAudit(HttpServletRequest request, String action, String targetEmail, String oldValue, String newValue) {
        String sql = "INSERT INTO dbo.AuditLogs (EditorEmail, Action, TargetEmail, OldValue, NewValue, LogTime) VALUES (?, ?, ?, ?, ?, GETDATE())";
        try (java.sql.Connection connection = vn.vnrailway.utils.DBContext.getConnection();
             java.sql.PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            jakarta.servlet.http.HttpSession session = request.getSession(false);
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
