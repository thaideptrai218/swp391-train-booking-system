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
import vn.vnrailway.dao.impl.UserDAOImpl; // Import UserDAOImpl
import vn.vnrailway.config.DBContext; // Import DBContext
import java.sql.Connection; // Import Connection
import java.sql.SQLException; // Import SQLException

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = { "/forgotpassword" })
public class ForgotPasswordServlet extends HttpServlet {
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    // Replace with your 16-character App Password
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("resend".equals(action)) {
            // Handle Resend OTP
            HttpSession session = request.getSession();
            String email = (String) session.getAttribute("email");
            String message = "";
            String messageType = "";

            // Rate limiting for resend OTP
            Long lastOTPSendTime = (Long) session.getAttribute("lastOTPSendTime");
            long currentTime = System.currentTimeMillis();
            long cooldownPeriod = 60 * 1000; // 60 seconds in milliseconds

            if (lastOTPSendTime != null && (currentTime - lastOTPSendTime < cooldownPeriod)) {
                long timeLeftInSeconds = (cooldownPeriod - (currentTime - lastOTPSendTime)) / 1000;
                message = "Vui lòng đợi " + timeLeftInSeconds + " giây nữa trước khi gửi lại OTP.";
                messageType = "error";
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                request.setAttribute("timeLeft", timeLeftInSeconds); // Pass timeLeft to JSP
                request.getRequestDispatcher("/WEB-INF/jsp/authentication/enterotp.jsp").forward(request, response);
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                message = "Không tìm thấy thông tin email để gửi lại OTP. Vui lòng thử lại từ bước quên mật khẩu.";
                messageType = "error";
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                // Redirect to forgot password page as session might be lost or invalid
                response.sendRedirect(request.getContextPath() + "/forgotpassword");
                return;
            }

            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Random rand = new Random();
                String otp = String.format("%06d", rand.nextInt(999999));

                System.out.println("Resending OTP: " + otp + " to email: " + email);

                Session mailSession = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                    }
                });
                mailSession.setDebug(true);

                Message mimeMessage = new MimeMessage(mailSession);
                mimeMessage.setFrom(new InternetAddress(EMAIL_FROM, "Vetaure", "UTF-8"));
                mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));

                mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
                mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
                mimeMessage.setSubject(MimeUtility.encodeText("Mã OTP Mới - Vetaure", "UTF-8", "B"));

                String messageContent = String.format(
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<head>" +
                                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
                                "</head>" +
                                "<body style='font-family: Arial, sans-serif;'>" +
                                "<h2 style='color: #333;'>Xin chào,</h2>" +
                                "<p>Đây là mã OTP mới của bạn theo yêu cầu gửi lại từ VeTauRe.</p>" +
                                "<p>Mã OTP mới của bạn là: <span style='color: red; font-size: 24px; font-weight: bold;'>%s</span></p>"
                                +
                                "<br/>" +
                                "<p>Nếu bạn không yêu cầu gửi lại mã OTP, vui lòng bỏ qua email này.</p>" +
                                "<p>Mã OTP này sẽ hết hạn sau 5 phút.</p>" +
                                "<br/>" +
                                "<h2 style='color: #333;'>Cảm ơn bạn đã sử dụng VeTauRe!</h2>" +
                                "<h2 style='color: #333;'>Đội ngũ VeTauRe.</h2>" +
                                "</body>" +
                                "</html>",
                        otp);

                mimeMessage.setContent(messageContent, "text/html; charset=UTF-8");
                Transport.send(mimeMessage);

                session.setAttribute("otp", otp);
                session.setAttribute("otpTimestamp", System.currentTimeMillis());
                session.setAttribute("lastOTPSendTime", System.currentTimeMillis()); // Update last send time

                message = "Mã OTP mới đã được gửi đến email của bạn.";
                messageType = "success";
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                request.getRequestDispatcher("/WEB-INF/jsp/authentication/enterotp.jsp").forward(request, response);

            } catch (MessagingException | java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("Email resend error: " + e.getMessage());

                message = "Lỗi khi gửi lại email OTP. Vui lòng thử lại sau.";
                messageType = "error";
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                request.getRequestDispatcher("/WEB-INF/jsp/authentication/enterotp.jsp").forward(request, response);
            }
        } else {
            // Default GET request: show forgot password page
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/forgotpassword.jsp").forward(request, response);
        }
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
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/forgotpassword.jsp").forward(request, response);
            return;
        }

        // Kiểm tra email trong database
        UserDAO userDAO = new UserDAOImpl();
        try (Connection conn = DBContext.getConnection()) { // Get connection
            if (!userDAO.checkEmailExists(conn, email)) { // Pass connection
                message = "Email này chưa được đăng ký trong hệ thống";
                messageType = "error";
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/forgotpassword.jsp").forward(request, response);
            return;
        }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Lỗi kết nối cơ sở dữ liệu. Vui lòng thử lại sau.";
            messageType = "error";
            request.setAttribute("message", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/forgotpassword.jsp").forward(request, response);
            return;
        }
        // Continue with email sending logic if email exists
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
                            "<p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn trên VeTauRe.</p>"
                            +
                            "<p>Để đặt lại mật khẩu, vui lòng sử dụng mã OTP sau đâyđây:<span style='color: red; font-size: 24px; font-weight: bold;'>%s</span></p>"
                            +
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
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/forgotpassword.jsp").forward(request, response);
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
