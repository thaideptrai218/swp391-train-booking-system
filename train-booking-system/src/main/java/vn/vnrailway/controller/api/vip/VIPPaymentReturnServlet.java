package vn.vnrailway.controller.api.vip;

import com.vnpay.common.Config;
import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.TemporaryVIPPurchaseRepository;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.dao.PaymentTransactionDAO;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.dao.impl.TemporaryVIPPurchaseRepositoryImpl;
import vn.vnrailway.dao.impl.UserVIPCardRepositoryImpl;
import vn.vnrailway.dao.impl.PaymentTransactionDAOImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.VIPCardType;
import vn.vnrailway.model.TemporaryVIPPurchase;
import vn.vnrailway.model.UserVIPCard;
import vn.vnrailway.model.PaymentTransaction;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/api/vip/payment/vnpay/return")
public class VIPPaymentReturnServlet extends HttpServlet {
    
    private VIPCardTypeRepository vipCardTypeRepository;
    private TemporaryVIPPurchaseRepository tempVIPPurchaseRepository;
    private UserVIPCardRepository userVIPCardRepository;
    private PaymentTransactionDAO paymentTransactionDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
        tempVIPPurchaseRepository = new TemporaryVIPPurchaseRepositoryImpl();
        userVIPCardRepository = new UserVIPCardRepositoryImpl();
        paymentTransactionDAO = new PaymentTransactionDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        
        // Extract VNPay parameters
        Map<String, String> vnp_Params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                vnp_Params.put(fieldName, fieldValue);
            }
        }
        
        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
        if (vnp_Params.containsKey("vnp_SecureHashType")) {
            vnp_Params.remove("vnp_SecureHashType");
        }
        
        // Verify VNPay signature
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            hashData.append(fieldName);
            hashData.append('=');
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            hashData.append('&');
        }
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }
        
        String calculatedSecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        String vipTransactionRef = request.getParameter("vnp_TxnRef");
        
        if (vnp_SecureHash == null || !vnp_SecureHash.equals(calculatedSecureHash)) {
            System.err.println("VNPAY SecureHash mismatch for VIP transaction: " + vipTransactionRef);
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
            return;
        }
        
        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            
            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            String vnp_PayDate = request.getParameter("vnp_PayDate");
            String vnp_BankCode = request.getParameter("vnp_BankCode");
            String vnp_Amount = request.getParameter("vnp_Amount");
            
            // Get temporary VIP purchase from session
            Integer tempVIPPurchaseID = null;
            if (session != null) {
                tempVIPPurchaseID = (Integer) session.getAttribute("tempVIPPurchaseID");
            }
            
            if (tempVIPPurchaseID == null) {
                conn.rollback();
                request.setAttribute("errorMessage", "Phiên làm việc đã hết hạn");
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
                return;
            }
            
            // Find temporary VIP purchase
            Optional<TemporaryVIPPurchase> tempVIPPurchaseOpt = tempVIPPurchaseRepository.findById(tempVIPPurchaseID);
            if (!tempVIPPurchaseOpt.isPresent()) {
                conn.rollback();
                request.setAttribute("errorMessage", "Không tìm thấy giao dịch");
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
                return;
            }
            
            TemporaryVIPPurchase tempVIPPurchase = tempVIPPurchaseOpt.get();
            
            // Verify transaction reference matches
            String sessionTransactionRef = null;
            if (session != null) {
                sessionTransactionRef = (String) session.getAttribute("vipTransactionRef");
            }
            
            if (!vipTransactionRef.equals(sessionTransactionRef)) {
                conn.rollback();
                request.setAttribute("errorMessage", "Mã giao dịch không khớp");
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
                return;
            }
            
            // Get VIP card type details
            Optional<VIPCardType> vipCardTypeOpt = vipCardTypeRepository.findById(tempVIPPurchase.getVipCardTypeID());
            if (!vipCardTypeOpt.isPresent()) {
                conn.rollback();
                request.setAttribute("errorMessage", "Loại thẻ VIP không hợp lệ");
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
                return;
            }
            
            VIPCardType vipCardType = vipCardTypeOpt.get();
            
            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful
                
                // Verify amount matches
                BigDecimal expectedAmount = vipCardType.getPrice().multiply(new BigDecimal("100")); // Convert to cents
                BigDecimal receivedAmount = new BigDecimal(vnp_Amount);
                
                if (expectedAmount.compareTo(receivedAmount) != 0) {
                    conn.rollback();
                    request.setAttribute("errorMessage", "Số tiền thanh toán không khớp");
                    request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
                    return;
                }
                
                // Deactivate any existing VIP cards for the user
                userVIPCardRepository.deactivateAllForUser(tempVIPPurchase.getUserID());
                
                // Create new VIP card
                LocalDateTime expiryDate = LocalDateTime.now().plusMonths(vipCardType.getDurationMonths());
                UserVIPCard userVIPCard = new UserVIPCard(
                    tempVIPPurchase.getUserID(),
                    vipCardType.getVipCardTypeID(),
                    expiryDate,
                    vipTransactionRef
                );
                
                userVIPCardRepository.create(userVIPCard);
                
                // Create payment transaction record
                PaymentTransaction paymentTransaction = new PaymentTransaction();
                paymentTransaction.setPaymentGatewayTransactionID(vnp_TransactionNo);
                paymentTransaction.setBookingID(0); // No booking for VIP purchases
                paymentTransaction.setAmount(vipCardType.getPrice());
                paymentTransaction.setPaymentGateway("VNPay");
                paymentTransaction.setStatus("Success");
                paymentTransaction.setTransactionDateTime(parseVNPayDateToDate(vnp_PayDate));
                paymentTransaction.setNotes("VIP Card Purchase: " + vipCardType.getTypeName() + " - Transaction Ref: " + vipTransactionRef + " - Bank: " + vnp_BankCode);
                
                // Note: You might need to modify PaymentTransaction model to support VIP purchases
                // paymentTransactionDAO.create(paymentTransaction);
                
                // Clean up temporary purchase
                tempVIPPurchaseRepository.delete(tempVIPPurchaseID);
                
                // Clear session attributes
                if (session != null) {
                    session.removeAttribute("selectedVIPCardTypeId");
                    session.removeAttribute("vipTransactionRef");
                    session.removeAttribute("tempVIPPurchaseID");
                }
                
                conn.commit();
                
                // Redirect to success page with transaction reference (following booking pattern)
                response.sendRedirect(request.getContextPath() + "/vip/payment/success?transactionRef=" + 
                    java.net.URLEncoder.encode(vipTransactionRef, "UTF-8"));
                
            } else {
                // Payment failed
                conn.rollback();
                
                // Log the failed transaction
                PaymentTransaction failedTransaction = new PaymentTransaction();
                failedTransaction.setPaymentGatewayTransactionID(vnp_TransactionNo);
                failedTransaction.setBookingID(0);
                failedTransaction.setAmount(vipCardType.getPrice());
                failedTransaction.setPaymentGateway("VNPay");
                failedTransaction.setStatus("Failed");
                failedTransaction.setTransactionDateTime(parseVNPayDateToDate(vnp_PayDate));
                failedTransaction.setNotes("VIP Card Purchase Failed: " + vipCardType.getTypeName() + " - Transaction Ref: " + vipTransactionRef + " - Bank: " + vnp_BankCode + " - Error Code: " + vnp_ResponseCode);
                
                // paymentTransactionDAO.create(failedTransaction);
                
                // Redirect to failure page
                request.setAttribute("errorCode", vnp_ResponseCode);
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu");
            try {
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            request.setAttribute("errorMessage", "Lỗi hệ thống");
            try {
                request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-payment-failure.jsp").forward(request, response);
            } catch (ServletException | IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Parse VNPay date format (yyyyMMddHHmmss) to java.util.Date
     */
    private java.util.Date parseVNPayDateToDate(String vnpayDate) {
        if (vnpayDate == null || vnpayDate.length() != 14) {
            return new java.util.Date();
        }
        
        try {
            int year = Integer.parseInt(vnpayDate.substring(0, 4));
            int month = Integer.parseInt(vnpayDate.substring(4, 6));
            int day = Integer.parseInt(vnpayDate.substring(6, 8));
            int hour = Integer.parseInt(vnpayDate.substring(8, 10));
            int minute = Integer.parseInt(vnpayDate.substring(10, 12));
            int second = Integer.parseInt(vnpayDate.substring(12, 14));
            
            LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
            return java.sql.Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            return new java.util.Date();
        }
    }
}
