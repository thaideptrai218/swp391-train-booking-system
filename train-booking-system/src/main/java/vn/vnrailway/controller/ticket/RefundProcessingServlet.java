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
import java.util.Properties;

@WebServlet(name = "RefundProcessingServlet", urlPatterns = { "/refundProcessing" })
public class RefundProcessingServlet extends HttpServlet {
    private TicketRepository ticketRepository;
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    // Replace with your 16-character App Password
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";

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
        response.sendRedirect(request.getContextPath() + "/checkRefundTicket");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle the booking check logic here
        // For now, just forward to the same JSP
        String action = request.getParameter("action");
        String ticketCode = request.getParameter("ticketCode");
        String email = request.getParameter("email");

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
            mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");

            // Set subject with UTF-8 encoding
            mimeMessage.setSubject(
                    MimeUtility.encodeText("Yêu cầu cung cấp lại số tài khoản hoàn tiền - Vetaure", "UTF-8", "B"));

            String messageContent = """
                        <html>
                        <body style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>
                            <div style='background-color: #ffffff; padding: 20px; border-radius: 10px; max-width: 600px; margin: auto;'>
                                <h2 style='color: #2c3e50;'>Thông báo từ Vetaure</h2>
                                <p>Xin chào,</p>
                                <p>Để tiếp tục xử lý yêu cầu hoàn tiền của bạn, vui lòng phản hồi email này kèm theo <strong>số tài khoản ngân hàng</strong> để chúng tôi chuyển tiền hoàn.</p>
                                <p>Xin cảm ơn!</p>
                                <br/>
                                <p>Trân trọng,</p>
                                <p><strong>Đội ngũ Vetaure</strong></p>
                            </div>
                        </body>
                        </html>
                    """;

            mimeMessage.setContent(messageContent, "text/html; charset=UTF-8");

            Transport.send(mimeMessage);

            if ("approve".equals(action)) {
                try {
                    ticketRepository.approveRefundTicket(ticketCode);
                    response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
                }
            } else if ("reject".equals(action)) {
                try {
                    ticketRepository.rejectRefundTicket(ticketCode);
                    response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "Không thể gửi email. Vui lòng thử lại sau.");
            response.sendRedirect(request.getContextPath() + "/checkRefundTicket");
        }
    }

    public static void main(String[] args) {

    }
}