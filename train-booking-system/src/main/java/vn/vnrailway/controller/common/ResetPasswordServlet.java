package vn.vnrailway.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException; // Added for UserRepository
import java.util.Optional; // Added for UserRepository
import vn.vnrailway.dao.UserDAO;
import vn.vnrailway.dao.UserRepository; // Added
import vn.vnrailway.dao.impl.UserRepositoryImpl; // Added
import vn.vnrailway.model.User; // Added
import vn.vnrailway.utils.HashPassword; // Added

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/newpassword"})
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        String email = (String) session.getAttribute("email");
        
        System.out.println("Debug - Reset Password:");
        System.out.println("OTP Verified: " + otpVerified);
        System.out.println("Email from session: " + email);

        if (otpVerified == null || !otpVerified || email == null) {
            System.out.println("Session validation failed");
            response.sendRedirect(request.getContextPath() + "/forgotpassword.jsp");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        System.out.println("New password length: " + (newPassword != null ? newPassword.length() : "null"));
        System.out.println("Passwords match: " + (newPassword != null && newPassword.equals(confirmPassword)));

        if (newPassword == null || newPassword.trim().isEmpty() || 
            !newPassword.equals(confirmPassword)) {
            request.setAttribute("message", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
            return;
        }

        // Check if new password is the same as the old password
        UserRepository userRepository = new UserRepositoryImpl();
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User currentUser = userOptional.get();
                if (HashPassword.checkPassword(newPassword, currentUser.getPasswordHash())) {
                    request.setAttribute("message", "Mật khẩu mới không được trùng với mật khẩu cũ.");
                    request.setAttribute("messageType", "error");
                    request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
                    return;
                }
            } else {
                // Should not happen if email in session is valid and OTP was verified
                request.setAttribute("message", "Không tìm thấy thông tin người dùng.");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
                return;
            }
        } catch (SQLException e) {
            // Log the error and inform the user
            e.printStackTrace(); // Consider a proper logging mechanism
            request.setAttribute("message", "Lỗi truy vấn cơ sở dữ liệu khi kiểm tra mật khẩu cũ.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        // Hash the new password before updating
        String hashedNewPassword = HashPassword.hashPassword(newPassword);
        boolean updateSuccess = userDAO.updatePassword(email, hashedNewPassword);
        System.out.println("Password update result: " + updateSuccess);

        if (updateSuccess) {
            session.invalidate();
            request.setAttribute("message", "Mật khẩu đã được đặt lại thành công!");
            request.setAttribute("messageType", "success");
            request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "Có lỗi xảy ra khi đặt lại mật khẩu.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
        }
    }
}
