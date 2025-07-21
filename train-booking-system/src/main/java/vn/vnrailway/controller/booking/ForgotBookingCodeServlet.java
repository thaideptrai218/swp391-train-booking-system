package vn.vnrailway.controller.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

@WebServlet("/forgotbookingcode")
public class ForgotBookingCodeServlet extends HttpServlet {
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";
    private BookingRepository bookingRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        bookingRepository = new BookingRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/public/check-booking/forgot-booking-code.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        if (email != null) {
            email = email.trim();
        }

        try {
            // This method needs to be created in BookingRepository
            List<String> bookingCodes = bookingRepository.findBookingCodesByEmail(email);

            if (bookingCodes != null && !bookingCodes.isEmpty()) {
                sendBookingCodesEmail(email, bookingCodes);
                request.setAttribute("successMessage", "Mã đặt chỗ đã được gửi đến email của bạn.");
            } else {
                request.setAttribute("errorMessage", "Không tìm thấy thông tin đặt chỗ với email đã nhập.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/public/check-booking/forgot-booking-code.jsp").forward(request, response);
    }

    private void sendBookingCodesEmail(String email, List<String> bookingCodes) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session mailSession = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                }
            });

            Message mimeMessage = new MimeMessage(mailSession);
            mimeMessage.setFrom(new InternetAddress(EMAIL_FROM, "Vetaure", "UTF-8"));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
            mimeMessage.setSubject(MimeUtility.encodeText("Danh sách mã đặt chỗ của bạn - Vetaure", "UTF-8", "B"));

            StringBuilder bookingCodesHtml = new StringBuilder();
            for (String code : bookingCodes) {
                bookingCodesHtml.append("<li><strong>").append(code).append("</strong></li>");
            }

            String messageContent = String.format(
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2>Yêu cầu lấy lại mã đặt chỗ</h2>" +
                "<p>Xin chào,</p>" +
                "<p>Bạn đã yêu cầu lấy lại danh sách các mã đặt chỗ đã đăng ký với email này. Dưới đây là danh sách các mã của bạn:</p>" +
                "<ul>%s</ul>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>" +
                "<p>Trân trọng,<br>Đội ngũ Vetaure</p>" +
                "</body>" +
                "</html>",
                bookingCodesHtml.toString()
            );

            mimeMessage.setContent(messageContent, "text/html; charset=UTF-8");
            Transport.send(mimeMessage);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
