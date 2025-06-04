package vn.vnrailway.controller.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "VerifyOTPServlet", urlPatterns = {"/verifyotp"})
public class VerifyOTPServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputOTP = request.getParameter("otp");
        HttpSession session = request.getSession();
        String storedOTP = (String) session.getAttribute("otp");
        Long otpTimestamp = (Long) session.getAttribute("otpTimestamp");
        
        // Check if session is valid
        if (storedOTP == null || otpTimestamp == null) {
            request.setAttribute("message", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
            return;
        }
        
        // Check OTP expiration (5 minutes)
        if (System.currentTimeMillis() - otpTimestamp > 300000) {
            request.setAttribute("message", "Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
            return;
        }
        
        if (storedOTP.equals(inputOTP)) {
            session.setAttribute("otpVerified", true);
            response.sendRedirect(request.getContextPath() + "/newpassword.jsp");
        } else {
            request.setAttribute("message", "Mã OTP không chính xác.");
            request.setAttribute("messageType", "error");
            request.getRequestDispatcher("/enterotp.jsp").forward(request, response);
        }
    }
}