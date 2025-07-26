package vn.vnrailway.controller.ticket;

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
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.UserDAO;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.dto.CheckBookingDTO;
import vn.vnrailway.dto.CheckInforRefundTicketDTO;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Ticket;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

@WebServlet(name = "ConfirmRefundTicketServlet", urlPatterns = { "/confirmRefundTicket" })
public class ConfirmRefundTicketServlet extends HttpServlet {
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    // Replace with your 16-character App Password
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";
    private TicketRepository ticketRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO. Consider dependency injection for a more robust
        // application.
        ticketRepository = new TicketRepositoryImpl();
    }

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
                request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/confirm-OTP.jsp").forward(request, response);
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
                                "<title>Xác nhận huỷ vé - VeTauRe</title>" +
                                "</head>" +
                                "<body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>"
                                +
                                "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>"
                                +

                                "<h2 style='color: #2c3e50;'>Xác nhận yêu cầu huỷ vé</h2>" +

                                "<p>Xin chào,</p>" +

                                "<p>Bạn vừa yêu cầu <strong>huỷ vé tàu</strong> trên hệ thống <strong>VeTauRe</strong>.</p>"
                                +
                                "<p>Vui lòng sử dụng mã OTP bên dưới để xác nhận thao tác:</p>" +

                                "<p style='font-size: 20px;'>Mã OTP của bạn là: <span style='color: red; font-size: 26px; font-weight: bold;'>%s</span></p>"
                                +

                                "<p style='color: #e74c3c;'>Lưu ý: Mã OTP có hiệu lực trong vòng 5 phút kể từ thời điểm nhận email.</p>"
                                +

                                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ bộ phận hỗ trợ.</p>"
                                +

                                "<br/>" +
                                "<p>Trân trọng,</p>" +
                                "<p><strong>Đội ngũ VeTauRe</strong></p>" +
                                "</div>" +
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
                request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/confirm-OTP.jsp").forward(request, response);

            } catch (MessagingException | java.io.UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("Email resend error: " + e.getMessage());

                message = "Lỗi khi gửi lại email OTP. Vui lòng thử lại sau.";
                messageType = "error";
                request.setAttribute("message", message);
                request.setAttribute("messageType", messageType);
                request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/confirm-OTP.jsp").forward(request, response);
            }
        } else {
            // Default GET request: show forgot password page
            request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String message = "";
        String messageType = "";
        String noteSTK = request.getParameter("bankAccountNumber");
            if (noteSTK == null || !noteSTK.trim().matches("^\\d{6,20}\\s-\\s[A-Za-zÀ-ỹ\\s]+$")) {
                request.setAttribute("errorMessage",
                        "Số tài khoản không đúng định dạng. Vui lòng nhập theo mẫu: 0123456789 - Vietcombank");
                request.getRequestDispatcher("/WEB-INF/jsp/refund-ticket/refund-ticket.jsp").forward(request, response);
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
            mimeMessage.setSubject(MimeUtility.encodeText("Mã OTP Xác Nhận Hủy Vé - Vetaure", "UTF-8", "B"));

            // Create HTML message content with proper encoding and styling
            String messageContent = String.format(
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
                            "<title>Xác nhận huỷ vé - VeTauRe</title>" +
                            "</head>" +
                            "<body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>" +
                            "<div style='max-width: 600px; margin: auto; background-color: #ffffff; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1);'>"
                            +

                            "<h2 style='color: #2c3e50;'>Xác nhận yêu cầu huỷ vé</h2>" +

                            "<p>Xin chào,</p>" +

                            "<p>Bạn vừa yêu cầu <strong>huỷ vé tàu</strong> trên hệ thống <strong>VeTauRe</strong>.</p>"
                            +
                            "<p>Vui lòng sử dụng mã OTP bên dưới để xác nhận thao tác:</p>" +

                            "<p style='font-size: 20px;'>Mã OTP của bạn là: <span style='color: red; font-size: 26px; font-weight: bold;'>%s</span></p>"
                            +

                            "<p style='color: #e74c3c;'>Lưu ý: Mã OTP có hiệu lực trong vòng 5 phút kể từ thời điểm nhận email.</p>"
                            +

                            "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này hoặc liên hệ bộ phận hỗ trợ.</p>"
                            +

                            "<br/>" +
                            "<p>Trân trọng,</p>" +
                            "<p><strong>Đội ngũ VeTauRe</strong></p>" +
                            "</div>" +
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

            String[] ticketInfos = request.getParameterValues("ticketInfo");
            session.setAttribute("ticketInfos", ticketInfos);
            session.setAttribute("noteSTK", noteSTK);

            request.setAttribute(messageContent, ticketInfos);
            // Chuyển hướng đến trang nhập OTP
            response.sendRedirect(request.getContextPath() + "/confirmOTP");

        } catch (MessagingException e) {
            // In chi tiết lỗi để debug
            e.printStackTrace();
            System.out.println("Email error: " + e.getMessage());

            message = "Lỗi khi gửi email. Vui lòng thử lại sau.";
            messageType = "error";
            request.setAttribute("errorMessage", message);
            request.setAttribute("messageType", messageType);
            request.getRequestDispatcher("/refund-ticket.jsp").forward(request, response);
        }
    }

    public static void main(String[] args) {

    }
}
