package vn.vnrailway.controller.api.vip;

import com.vnpay.common.Config;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.TemporaryVIPPurchaseRepository;
import vn.vnrailway.dao.UserVIPCardRepository;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.dao.impl.TemporaryVIPPurchaseRepositoryImpl;
import vn.vnrailway.dao.impl.UserVIPCardRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.VIPCardType;
import vn.vnrailway.model.UserVIPCard;
import vn.vnrailway.model.TemporaryVIPPurchase;

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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet("/api/vip/payment/initiate")
public class VIPPaymentInitiateServlet extends HttpServlet {

    private VIPCardTypeRepository vipCardTypeRepository;
    private TemporaryVIPPurchaseRepository tempVIPPurchaseRepository;
    private UserVIPCardRepository userVIPCardRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
        tempVIPPurchaseRepository = new TemporaryVIPPurchaseRepositoryImpl();
        userVIPCardRepository = new UserVIPCardRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Check if user is logged in
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" +
                    URLEncoder.encode("/vip/purchase", "UTF-8"));
            return;
        }

        // Get selected VIP card type ID from session
        Integer selectedVIPCardTypeId = (Integer) session.getAttribute("selectedVIPCardTypeId");
        if (selectedVIPCardTypeId == null) {
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=no_selection");
            return;
        }

        try {
            // Fetch VIP card type details first
            Optional<VIPCardType> vipCardTypeOpt = vipCardTypeRepository.findById(selectedVIPCardTypeId);
            if (!vipCardTypeOpt.isPresent()) {
                session.removeAttribute("selectedVIPCardTypeId");
                response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_card");
                return;
            }

            VIPCardType selectedVIPCard = vipCardTypeOpt.get();

            // Check if user already has an active VIP card
            List<UserVIPCard> existingVIPCards = userVIPCardRepository.findActiveByUserId(loggedInUser.getUserID());
            if (!existingVIPCards.isEmpty()) {
                UserVIPCard existingVIPCard = existingVIPCards.get(0); // Get the most relevant card
                
                // Check if trying to buy the same VIP card type
                if (existingVIPCard.getVipCardTypeID() == selectedVIPCard.getVipCardTypeID()) {
                    // Same card type - redirect to already_has_vip error
                    response.sendRedirect(request.getContextPath() + "/vip/purchase?error=already_has_vip");
                    return;
                }
                
                // Different card type - allow upgrade/downgrade by deactivating existing card
                // The existing card will be deactivated when the new purchase is successful
                System.out.println("User " + loggedInUser.getUserID() + " is upgrading/changing VIP card from type " 
                                 + existingVIPCard.getVipCardTypeID() + " to type " + selectedVIPCard.getVipCardTypeID());
            }

            // Clean up any existing temporary VIP purchases for this session
            tempVIPPurchaseRepository.deleteBySessionId(session.getId());

            // Create temporary VIP purchase record
            TemporaryVIPPurchase tempVIPPurchase = new TemporaryVIPPurchase(
                    session.getId(),
                    loggedInUser.getUserID(),
                    selectedVIPCard.getVipCardTypeID(),
                    selectedVIPCard.getDurationMonths(),
                    selectedVIPCard.getPrice(),
                    15 // 15 minutes expiry
            );

            tempVIPPurchase = tempVIPPurchaseRepository.create(tempVIPPurchase);

            // Generate unique transaction reference
            String vipTransactionRef = "VIP" + System.currentTimeMillis() + "_" + loggedInUser.getUserID();

            // Store transaction reference in session for return handling
            session.setAttribute("vipTransactionRef", vipTransactionRef);
            session.setAttribute("tempVIPPurchaseID", tempVIPPurchase.getTempVIPPurchaseID());

            // Build VNPay payment URL
            String vnpayUrl = buildVNPayURL(request, selectedVIPCard, vipTransactionRef, loggedInUser);

            // Redirect to VNPay
            response.sendRedirect(vnpayUrl);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/vip/confirm?error=database_error");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/vip/confirm?error=payment_init_error");
        }
    }

    /**
     * Build VNPay payment URL for VIP card purchase
     */
    private String buildVNPayURL(HttpServletRequest request, VIPCardType vipCard,
            String transactionRef, User user) throws Exception {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TmnCode = Config.vnp_TmnCode;
        String vnp_Amount = String.valueOf(vipCard.getPrice().multiply(new BigDecimal("100")).longValue()); // VNPay
                                                                                                            // requires
                                                                                                            // amount in
                                                                                                            // VND cents
        String vnp_CurrCode = "VND";
        String vnp_BankCode = "";
        String vnp_TxnRef = transactionRef;
        String vnp_OrderInfo = "Thanh toan the VIP " + vipCard.getTypeName() + " - " + user.getFullName();
        String vnp_OrderType = "other";
        String vnp_Locale = "vn";
        String vnp_ReturnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/api/vip/payment/vnpay/return";
        String vnp_IpAddr = getClientIpAddress(request);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_BankCode", vnp_BankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return Config.vnp_PayUrl + "?" + query.toString();
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
