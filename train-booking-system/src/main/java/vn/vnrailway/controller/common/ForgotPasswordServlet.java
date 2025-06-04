package vn.vnrailway.controller.common;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.UserDAO;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgotpassword"})
public class ForgotPasswordServlet extends HttpServlet {
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    // Replace with your 16-character App Password
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String message = "";
        String messageType = "";

        // Thêm logging để debug
        System.out.println("Processing email: " + email);

        if (email == null || email.trim().isEmpty() || !isValidEmail(email.trim())) {
            message = "Email không hợp lệ";
            messageType = "error";
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email trong database
        UserDAO userDAO = new UserDAO();
        if (!userDAO.checkEmailExists(email)) {
            message = "Email này chưa được đăng ký trong hệ thống";
            messageType = "error";
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
            return;
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            // Generate OTP - Move this before using it
            Random rand = new Random();
            String otp = String.format("%06d", rand.nextInt(999999));
            
            // Add debug log for OTP
            System.out.println("Generated OTP: " + otp);
            
            Session mailSession = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            // Thêm debug mode
            mailSession.setDebug(true);

            // Thêm logging
            System.out.println("Creating message...");

            Message mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setFrom(new InternetAddress(EMAIL_FROM, "Vetaure", "UTF-8"));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            
            // Set UTF-8 encoding for both subject and content
            mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
            mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
            
            // Set subject with UTF-8 encoding
            mimeMessage.setSubject(MimeUtility.encodeText("Mã OTP Đặt Lại Mật Khẩu - Vetaure", "UTF-8", "B"));
            
            // Create HTML message content with proper encoding and styling
            String messageContent = String.format(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #333;'>Xin chào,</h2>" +
                "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên VeTauRe.</p>" +
                "<p>Để đặt lại mật khẩu, vui lòng sử dụng mã OTP sau đâyđây:<span style='color: red; font-size: 24px; font-weight: bold;'>%s</span></p>" +
                "<br/>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "<p>Đây là mã OTP duy nhất và chỉ sử dụng một lần và sẽ hết hạn sau 5 phút..</p>" +
                "<br/>" +          
                "<h2 style='color: #333;'>Cảm ơn bạn đã sử dụng VeTauRe!</h2>" +
                "<h2 style='color: #333;'>Đội ngũ VeTauRe.</h2>" +
                "</body>" +
                "</html>", 
                otp);

            // Set content with explicit charset
            mimeMessage.setContent(messageContent, "text/html; charset=UTF-8");

            // Thêm logging
            System.out.println("Sending message...");

            Transport.send(mimeMessage);

            // Lưu OTP vào session
            HttpSession session = request.getSession();
            session.setAttribute("otp", otp);
            session.setAttribute("email", email);
            session.setAttribute("otpTimestamp", System.currentTimeMillis());

            // Chuyển hướng đến trang nhập OTP
            response.sendRedirect(request.getContextPath() + "/enterotp");

        } catch (MessagingException e) {
            // In chi tiết lỗi để debug
            e.printStackTrace();
            System.out.println("Email error: " + e.getMessage());
            
            message = "Lỗi khi gửi email. Vui lòng thử lại sau.";
            messageType = "error";
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/forgotpassword.jsp").forward(request, response);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public String getServletInfo() {
        return "Handles forgot password requests by sending OTP to user's email.";
    }
}
