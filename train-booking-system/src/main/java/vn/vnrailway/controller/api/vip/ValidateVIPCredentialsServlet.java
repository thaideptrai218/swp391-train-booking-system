package vn.vnrailway.controller.api.vip;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.dao.impl.UserVIPCardRepositoryImpl;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.UserVIPCard;
import vn.vnrailway.model.VIPCardType;
import vn.vnrailway.utils.JsonUtils;

/**
 * API Servlet to validate VIP credentials for passenger type selection
 */
@WebServlet("/api/vip/validateCredentials")
public class ValidateVIPCredentialsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private UserRepository userRepository;
    private UserVIPCardRepository userVIPCardRepository;
    private VIPCardTypeRepository vipCardTypeRepository;

    @Override
    public void init() throws ServletException {
        try {
            userRepository = new UserRepositoryImpl();
            userVIPCardRepository = new UserVIPCardRepositoryImpl();
            vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize repositories", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String idCardNumber = request.getParameter("idCardNumber");
            
            if (idCardNumber == null || idCardNumber.trim().isEmpty()) {
                sendErrorResponse(response, "Số CMND/CCCD không được để trống", 400);
                return;
            }
            
            idCardNumber = idCardNumber.trim();
            
            // Validate ID card format
            if (!idCardNumber.matches("\\d{9,12}")) {
                sendErrorResponse(response, "Số CMND/CCCD không hợp lệ. Vui lòng nhập 9-12 chữ số.", 400);
                return;
            }
            
            // Find user by ID card number
            Optional<User> userOpt = userRepository.findByIdCardNumber(idCardNumber);
            
            if (!userOpt.isPresent()) {
                sendVIPValidationResponse(response, false, "Không tìm thấy thông tin thành viên với số CMND/CCCD này", 
                                        0.0, null, null, null);
                return;
            }
            
            User user = userOpt.get();
            
            // Check for active VIP card
            List<UserVIPCard> vipCards = userVIPCardRepository.findActiveByUserId(user.getUserID());
            
            if (vipCards.isEmpty()) {
                sendVIPValidationResponse(response, false, "Thành viên này không có thẻ VIP hoặc thẻ VIP đã hết hạn", 
                                        0.0, null, null, user.getFullName());
                return;
            }
            
            UserVIPCard vipCard = vipCards.get(0); // Get the most relevant active card
            
            // Validate VIP card is still valid (not expired)
            if (!vipCard.isValid()) {
                sendVIPValidationResponse(response, false, "Thẻ VIP đã hết hạn hoặc không còn hiệu lực", 
                                        0.0, null, null, user.getFullName());
                return;
            }
            
            // Get VIP discount percentage
            double discountPercentage = userVIPCardRepository.getVIPDiscountPercentage(user.getUserID());
            
            // Load VIP Card Type details
            Optional<VIPCardType> vipCardTypeOpt = vipCardTypeRepository.findById(vipCard.getVipCardTypeID());
            if (!vipCardTypeOpt.isPresent()) {
                sendErrorResponse(response, "Không tìm thấy thông tin loại thẻ VIP.", 500);
                return;
            }
            VIPCardType vipCardType = vipCardTypeOpt.get();
            
            // User has valid VIP membership
            sendVIPValidationResponse(response, true, "Xác thực thành công! Thẻ VIP hợp lệ.", 
                                    discountPercentage, 
                                    vipCardType.getBaseTypeName(), 
                                    vipCardType.getVIPIcon(),
                                    user.getFullName());
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "Lỗi hệ thống khi xác thực VIP: " + e.getMessage(), 500);
        }
    }

    /**
     * Send VIP validation response
     */
    private void sendVIPValidationResponse(HttpServletResponse response, boolean isValid, String message,
                                         double discountPercentage, String vipTypeName, String vipIcon, String fullName) throws IOException {
        
        VIPValidationResponse vipResponse = new VIPValidationResponse();
        vipResponse.success = true;
        vipResponse.isValid = isValid;
        vipResponse.message = message;
        vipResponse.discountPercentage = discountPercentage;
        vipResponse.vipTypeName = vipTypeName;
        vipResponse.vipIcon = vipIcon;
        vipResponse.fullName = fullName;
        
        String jsonResponse = JsonUtils.toJsonString(vipResponse);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Send error response
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.success = false;
        errorResponse.message = message;
        
        String jsonResponse = JsonUtils.toJsonString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    /**
     * VIP Validation Response DTO
     */
    public static class VIPValidationResponse {
        public boolean success;
        public boolean isValid;
        public String message;
        public double discountPercentage;
        public String vipTypeName;
        public String vipIcon;
        public String fullName;
    }

    /**
     * Error Response DTO
     */
    public static class ErrorResponse {
        public boolean success;
        public String message;
    }
}
