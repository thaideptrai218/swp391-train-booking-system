package vn.vnrailway.controller.vip;

import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserVIPCardRepositoryImpl;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.UserVIPCard;
import vn.vnrailway.model.VIPCardType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Properties;

@WebServlet("/vip/payment/success")
public class VIPPaymentSuccessServlet extends HttpServlet {
    
    private static final String EMAIL_FROM = "assasinhp619@gmail.com";
    private static final String EMAIL_PASSWORD = "slos bctt epxv osla";
    private UserVIPCardRepository userVIPCardRepository;
    private VIPCardTypeRepository vipCardTypeRepository;
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        userVIPCardRepository = new UserVIPCardRepositoryImpl();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
        userRepository = new UserRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String transactionRef = request.getParameter("transactionRef");
        if (transactionRef == null || transactionRef.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        try {
            // Find VIP card by transaction reference
            Optional<UserVIPCard> userVIPCardOpt = userVIPCardRepository.findByTransactionReference(transactionRef);
            if (!userVIPCardOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            UserVIPCard userVIPCard = userVIPCardOpt.get();
            
            // Verify VIP card is active
            if (!userVIPCard.isActive()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            // Get user details
            Optional<User> userOpt = userRepository.findById(userVIPCard.getUserID());
            if (!userOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            User user = userOpt.get();
            
            // Get VIP card type details
            Optional<VIPCardType> vipCardTypeOpt = vipCardTypeRepository.findById(userVIPCard.getVipCardTypeID());
            if (!vipCardTypeOpt.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }
            
            VIPCardType vipCardType = vipCardTypeOpt.get();
            
            // Send confirmation email
            String emailContent = generateVIPPurchaseEmail(user, userVIPCard, vipCardType);
            sendEmail(user.getEmail(), "Thông Báo Mua Thẻ VIP Thành Công - VeTauRe", emailContent);
            
            // Set attributes for JSP
            request.setAttribute("user", user);
            request.setAttribute("userVIPCard", userVIPCard);
            request.setAttribute("vipCardType", vipCardType);
            request.setAttribute("transactionRef", transactionRef);
            
            // Forward to success JSP
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-success.jsp").forward(request, response);
            
        } catch (SQLException | MessagingException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi xử lý");
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
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
        mimeMessage.setFrom(new InternetAddress(EMAIL_FROM, "VeTauRe", "UTF-8"));
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
        mimeMessage.setHeader("Content-Transfer-Encoding", "8bit");
        mimeMessage.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        mimeMessage.setContent(content, "text/html; charset=UTF-8");

        Transport.send(mimeMessage);
    }
    
    private String generateVIPPurchaseEmail(User user, UserVIPCard userVIPCard, VIPCardType vipCardType) {
        String customerName = (user != null) ? user.getFullName() : "Quý khách";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String purchaseDate = userVIPCard.getPurchaseDate().format(formatter);
        String expiryDate = userVIPCard.getExpiryDate().format(formatter);
        
        return "<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>" +
                
                "<div style='text-align: center; margin-bottom: 30px;'>" +
                "<img src='https://via.placeholder.com/150x60?text=VeTauRe' alt='VeTauRe Logo' style='margin-bottom: 20px;'>" +
                "<h1 style='color: #28a745; margin: 0;'>🎉 Chúc mừng bạn đã trở thành thành viên VIP!</h1>" +
                "</div>" +
                
                "<p>Xin chào <strong style='color: #007bff;'>" + customerName + "</strong>,</p>" +
                "<p>VeTauRe xác nhận bạn đã mua thẻ VIP thành công!</p>" +
                
                "<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin: 20px 0;'>" +
                "<h3 style='margin: 0 0 15px 0; text-align: center;'>" + vipCardType.getVIPIcon() + " " + vipCardType.getTypeName() + "</h3>" +
                "<div style='display: flex; justify-content: space-between; flex-wrap: wrap;'>" +
                "<div style='margin-bottom: 10px;'><strong>Mức giảm giá:</strong> " + vipCardType.getFormattedDiscount() + "</div>" +
                "<div style='margin-bottom: 10px;'><strong>Thời hạn:</strong> " + vipCardType.getDurationDescription() + "</div>" +
                "<div style='margin-bottom: 10px;'><strong>Ngày mua:</strong> " + purchaseDate + "</div>" +
                "<div style='margin-bottom: 10px;'><strong>Ngày hết hạn:</strong> " + expiryDate + "</div>" +
                "</div>" +
                "</div>" +
                
                "<div style='background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
                "<h4 style='color: #495057; margin-top: 0;'>🌟 Quyền lợi của bạn:</h4>" +
                "<p style='margin: 0;'>" + vipCardType.getDescription() + "</p>" +
                "</div>" +
                
                "<div style='background: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h4 style='color: #856404; margin-top: 0;'>📝 Lưu ý quan trọng:</h4>" +
                "<ul style='margin: 0; padding-left: 20px;'>" +
                "<li>Thẻ VIP đã được kích hoạt và có hiệu lực ngay lập tức</li>" +
                "<li>Mức giảm giá sẽ được áp dụng tự động khi bạn đặt vé</li>" +
                "<li>Thẻ VIP chỉ có thể sử dụng bởi chủ thẻ đã đăng ký</li>" +
                "<li>Để xem thông tin chi tiết thẻ VIP, vui lòng đăng nhập vào tài khoản của bạn</li>" +
                "<li>Đây là email gửi tự động. Xin vui lòng không trả lời email này</li>" +
                "</ul>" +
                "</div>" +
                
                "<div style='background: #d1ecf1; border: 1px solid #bee5eb; padding: 15px; border-radius: 8px; margin: 20px 0;'>" +
                "<h4 style='color: #0c5460; margin-top: 0;'>🎯 Bắt đầu sử dụng thẻ VIP:</h4>" +
                "<p style='margin: 0;'>Hãy đặt vé tàu ngay hôm nay để tận hưởng mức giảm giá " + vipCardType.getFormattedDiscount() + " dành cho thành viên VIP!</p>" +
                "<div style='text-align: center; margin-top: 15px;'>" +
                "<a href='#' style='background: #28a745; color: white; padding: 12px 24px; text-decoration: none; border-radius: 25px; display: inline-block;'>🚄 Đặt vé ngay</a>" +
                "</div>" +
                "</div>" +
                
                "<div style='border-top: 1px solid #ddd; padding-top: 20px; margin-top: 30px; text-align: center; color: #6c757d;'>" +
                "<p>Cảm ơn bạn đã tin tướng và sử dụng dịch vụ VeTauRe!</p>" +
                "<p><strong>Đội ngũ VeTauRe</strong></p>" +
                "<p style='font-size: 12px;'>Liên hệ hỗ trợ: support@vetaure.com | Hotline: 1900-xxxx</p>" +
                "</div>" +
                
                "</div>" +
                "</body></html>";
    }
}
