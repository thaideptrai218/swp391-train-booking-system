package vn.vnrailway.controller.api.booking;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import vn.vnrailway.config.DBContext;
import vn.vnrailway.dto.payment.CustomerDetailsDto;
import vn.vnrailway.dto.payment.OriginalCartItemDataDto;
import vn.vnrailway.dto.payment.PassengerPayloadDto;
import vn.vnrailway.dto.payment.PassengerTicketCreationData;
import vn.vnrailway.dto.payment.PaymentPayloadDto;
import vn.vnrailway.model.User;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Passenger;
import vn.vnrailway.model.PaymentTransaction;
import vn.vnrailway.model.TemporarySeatHold;

import vn.vnrailway.dao.UserDAO;
import vn.vnrailway.dao.BookingDAO;
import vn.vnrailway.dao.PassengerDAO;
import vn.vnrailway.dao.PaymentTransactionDAO;
import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.UserDAOImpl;
import vn.vnrailway.dao.impl.BookingDAOImpl;
import vn.vnrailway.dao.impl.PassengerDAOImpl;
import vn.vnrailway.dao.impl.PaymentTransactionDAOImpl;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;

import com.vnpay.common.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

@WebServlet("/api/booking/initiatePayment")
public class InitiateBookingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final Gson gson = new Gson();
    private UserDAO userDAO;
    private TemporarySeatHoldDAO temporarySeatHoldDAO;
    private BookingDAO bookingDAO;
    private PassengerDAO passengerDAO;
    private PaymentTransactionDAO paymentTransactionDAO;

    public InitiateBookingServlet() {
        this.userDAO = new UserDAOImpl();
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
        this.bookingDAO = new BookingDAOImpl();
        this.passengerDAO = new PassengerDAOImpl();
        this.paymentTransactionDAO = new PaymentTransactionDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        PaymentPayloadDto paymentPayload;
        try {
            paymentPayload = gson.fromJson(requestBody, PaymentPayloadDto.class);
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"ERROR\", \"message\": \"Invalid JSON payload: " + e.getMessage() + "\"}");
            out.flush();
            return;
        }

        if (paymentPayload == null || !"VNPAY".equalsIgnoreCase(paymentPayload.getPaymentMethod())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"ERROR\", \"message\": \"Invalid payment payload or payment method not VNPAY.\"}");
            out.flush();
            return;
        }

        Connection conn = null;
        String bookingCode = "VNR" + System.currentTimeMillis();
        List<PassengerTicketCreationData> passengerDataForSession = new ArrayList<>();

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);
            HttpSession session = request.getSession(); // Define session earlier

            // 1. User Handling (Booker) - Find/Create User
            User booker = userDAO.findOrCreateUser(conn, paymentPayload.getCustomerDetails());
            if (booker == null) {
                throw new SQLException("Failed to find or create booker user.");
            }
            long bookerUserId = booker.getUserID();

            // 2. Verify Seat Holds
            // HttpSession httpSession = request.getSession(); // Removed, session is already defined
            String sessionId = session.getId(); // Use the existing 'session' variable
            for (PassengerPayloadDto passengerDto : paymentPayload.getPassengers()) {
               OriginalCartItemDataDto item = passengerDto.getOriginalCartItem();
               TemporarySeatHold hold = temporarySeatHoldDAO.findActiveHoldBySessionAndLeg(conn, sessionId, 
                                             Integer.parseInt(item.getTripId()), item.getSeatID(), 
                                             Integer.parseInt(item.getCoachId()), 
                                             Integer.parseInt(item.getLegOriginStationId()), 
                                             Integer.parseInt(item.getLegDestinationStationId()));
               if (hold == null) {
                   throw new SQLException("Seat hold not found or expired for seat: " + item.getSeatName() + " on trip " + item.getTripId());
               }
               // Optional: re-verify price if necessary, though hold should be authoritative
            }
            
            // 3. Create Booking Record
            Booking newBooking = new Booking();
            newBooking.setBookingCode(bookingCode);
            newBooking.setUserID((int) bookerUserId);
            newBooking.setBookingDateTime(LocalDateTime.now());
            newBooking.setTotalPrice(BigDecimal.valueOf(paymentPayload.getTotalAmount()));
            newBooking.setBookingStatus("Pending");
            newBooking.setPaymentStatus("Unpaid");
            newBooking.setSource("WebApp");
            // ExpiredAt for booking can be set based on VNPAY expiry or a fixed duration
            Calendar cldExpire = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            cldExpire.add(Calendar.MINUTE, 16); // Slightly after VNPAY link expiry
            newBooking.setExpiredAt(cldExpire.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());

            long currentBookingId = bookingDAO.insertBooking(conn, newBooking);
            if (currentBookingId == -1) {
                throw new SQLException("Failed to create booking record.");
            }

            // 4. Create Passenger Records & Prepare Session Data
            for (PassengerPayloadDto passengerDto : paymentPayload.getPassengers()) {
                Passenger passenger = new Passenger();
                passenger.setFullName(passengerDto.getFullName());
                passenger.setIdCardNumber(passengerDto.getIdCardNumber());
                passenger.setPassengerTypeID(Integer.parseInt(passengerDto.getPassengerTypeID()));
                if (passengerDto.getDateOfBirth() != null && !passengerDto.getDateOfBirth().isEmpty()) {
                    passenger.setDateOfBirth(LocalDate.parse(passengerDto.getDateOfBirth()));
                }
                passenger.setUserID(booker.getUserID()); // Link passenger to the booker user for now

                long passengerId = passengerDAO.insertPassenger(conn, passenger);
                if (passengerId == -1) {
                    throw new SQLException("Failed to create passenger record for: " + passengerDto.getFullName());
                }
                // Store for session
                OriginalCartItemDataDto cartItemDto = passengerDto.getOriginalCartItem();
                passengerDataForSession.add(new PassengerTicketCreationData((int) passengerId, cartItemDto));
            }

            // 5. Create PaymentTransaction Record
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setBookingID((int) currentBookingId);
            transaction.setPaymentGateway("VNPAY");
            transaction.setAmount(BigDecimal.valueOf(paymentPayload.getTotalAmount()));
            transaction.setTransactionDateTime(new Date()); // Current time
            transaction.setStatus("Pending");

            long transactionId = paymentTransactionDAO.insertTransaction(conn, transaction);
            if (transactionId == -1) {
                throw new SQLException("Failed to create payment transaction record.");
            }

            conn.commit();

            session.setAttribute(bookingCode, passengerDataForSession);

            // --- Prepare VNPAY URL ---
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_OrderInfo = "Thanh toan ve tau VNR don hang " + bookingCode;
            String orderType = "other";
            String vnp_TxnRef = bookingCode;
            String vnp_IpAddr = Config.getIpAddress(request);
            String vnp_TmnCode = Config.vnp_TmnCode;
            long amount = (long) paymentPayload.getTotalAmount() * 100;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath() + "/api/payment/vnpay/return";
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
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
            String queryUrl = query.toString();
            String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

            response.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"status\": \"VNPAY_REDIRECT\", \"paymentUrl\": \"" + paymentUrl + "\", \"bookingCode\": \""
                    + bookingCode + "\"}");

        } catch (SQLException e) {
            handleDbError(conn, response, out, "Database error: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            handleDbError(conn, response, out, "Invalid ID format in request data: " + e.getMessage(), e);
        } catch (Exception e) {
            handleDbError(conn, response, out, "Unexpected error: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit())
                        conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                }
            }
            if (out != null)
                out.flush();
        }
    }

    private void handleDbError(Connection conn, HttpServletResponse response, PrintWriter out, String logMessage,
            Exception e) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit())
                    conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        out.print("{\"status\": \"ERROR\", \"message\": \"" + logMessage.replace("\"", "'") + "\"}"); // Basic
                                                                                                      // sanitization
                                                                                                      // for JSON
        System.err.println("Error in InitiateBookingServlet: " + logMessage);
        e.printStackTrace();
    }
}
