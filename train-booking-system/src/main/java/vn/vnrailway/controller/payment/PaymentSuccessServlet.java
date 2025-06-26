package vn.vnrailway.controller.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.BookingRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.User;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import vn.vnrailway.dto.CheckBookingDTO;
import vn.vnrailway.dto.InfoPassengerDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@WebServlet("/payment/success")
public class PaymentSuccessServlet extends HttpServlet {

    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        bookingRepository = new BookingRepositoryImpl();
        userRepository = new UserRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String bookingCode = request.getParameter("bookingCode");
        if (bookingCode == null || bookingCode.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            Booking booking = bookingRepository.findByBookingCode(bookingCode).orElse(null);
            if (booking != null && "Confirmed".equals(booking.getBookingStatus()) && "Paid".equals(booking.getPaymentStatus())) {
                User user = userRepository.getUserByBookingCode(bookingCode).orElse(null);
                List<String> ticketCodes = bookingRepository.findTicketCodesByBookingCode(bookingCode);
                String emailContent = generateTicketEmail(user, booking, ticketCodes);
                sendEmail(user.getEmail(), "Thông Báo Mua Vé Thành Công - VeTaure", emailContent);
                request.setAttribute("bookingCode", bookingCode);
                request.getRequestDispatcher("/payment/paymentSuccess.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/payment/paymentFailure.jsp");
        }
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException, IOException {
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
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
        mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
        mimeMessage.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        mimeMessage.setContent(content, "text/html; charset=UTF-8");

        Transport.send(mimeMessage);
    }

    private String generateTicketEmail(User user, Booking booking, List<String> ticketCodes) {
        String customerName = (user != null) ? user.getFullName() : "Quý khách";
        String ticketCodeList = String.join(", ", ticketCodes);

        return "<html><body>" +
                "<p>Xin chào <strong>" + customerName + "</strong>,</p>" +
                "<p>VeTauRe xác nhận bạn đã đặt vé tàu lửa thành công</p>" +
                "<p>Mã đặt chỗ: <strong>" + booking.getBookingCode() + "</strong></p>" +
                "<p>Mã vé: <strong>" + ticketCodeList + "</strong></p>" +
                "<p><strong>Chú ý:</strong></p>" +
                "<ul>" +
                "<li>Để xem chi tiết vé quý khách vui lòng vào trang web VeTaure -> Thông tin đặt chỗ với mã đặt chỗ ở trên.</li>" +
                "<li>Khi in thẻ lên tàu tại cửa vé, quý khách vui lòng mang theo giấy tờ tùy thân đã được sử dụng để mua vé cùng với mã đặt chỗ mà quý khách nhận được trong email này.</li>" +
                "<li>Để đảm bảo quyền lợi của mình, quý khách vui lòng mang theo thẻ lên tàu và giấy tờ tùy thân ghi trên thẻ lên tàu trong suốt hành trình và xuất trình cho nhân viên đường sắt khi có yêu cầu.</li>" +
                "<li>Đây là email gửi tự động. Xin vui lòng không trả lời email này.</li>" +
                "<li>Quý khách có thể liên hệ với trung tâm hỗ trợ khách hàng staff01@vnr.com để được trợ giúp.</li>" +
                "</ul>" +
                "<p>Trân trọng,</p>" +
                "<p><strong>Đội ngũ Vetaure</strong></p>" +
                "</body></html>";
    }
}
