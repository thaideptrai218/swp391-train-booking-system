package vn.vnrailway.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.vnrailway.dao.UserDAO;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/resetpassword"})
public class ResetPasswordServlet extends HttpServlet {

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

        UserDAO userDAO = new UserDAO();
        boolean updateSuccess = userDAO.updatePassword(email, newPassword);
        System.out.println("Password update result: " + updateSuccess);

        if (updateSuccess) {
            session.invalidate();
            request.setAttribute("message", "Mật khẩu đã được đặt lại thành công.");
            request.setAttribute("messageType", "success");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "Có lỗi xảy ra khi đặt lại mật khẩu.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/newpassword.jsp").forward(request, response);
        }
    }
}