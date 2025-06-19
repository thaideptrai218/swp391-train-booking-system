package vn.vnrailway.controller.api.payment;

import com.vnpay.common.Config;
import vn.vnrailway.config.DBContext;
import vn.vnrailway.dto.payment.OriginalCartItemDataDto;
import vn.vnrailway.dto.payment.PassengerTicketCreationData;
import vn.vnrailway.model.Booking;
import vn.vnrailway.model.Passenger;
import vn.vnrailway.model.Ticket;
import vn.vnrailway.dao.BookingDAO;
import vn.vnrailway.dao.PassengerDAO;
import vn.vnrailway.dao.PaymentTransactionDAO;
import vn.vnrailway.dao.TicketDAO;
import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.BookingDAOImpl;
import vn.vnrailway.dao.impl.PassengerDAOImpl;
import vn.vnrailway.dao.impl.PaymentTransactionDAOImpl;
import vn.vnrailway.dao.impl.TicketDAOImpl;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder; // Added import
import java.nio.charset.StandardCharsets; // Added import
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/api/payment/vnpay/return")
public class VNPayReturnServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private BookingDAO bookingDAO;
    private PaymentTransactionDAO paymentTransactionDAO;
    private PassengerDAO passengerDAO;
    private TicketDAO ticketDAO;
    private TemporarySeatHoldDAO temporarySeatHoldDAO;

    public VNPayReturnServlet() {
        this.bookingDAO = new BookingDAOImpl();
        this.paymentTransactionDAO = new PaymentTransactionDAOImpl();
        this.passengerDAO = new PassengerDAOImpl();
        this.ticketDAO = new TicketDAOImpl();
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String> vnp_Params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                vnp_Params.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash"); // Remove before hashing
        if (vnp_Params.containsKey("vnp_SecureHashType")) { // Also remove if present
            vnp_Params.remove("vnp_SecureHashType");
        }

        // Create hashData from raw, VNPAY-provided parameters
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName); // Raw value
            // Ensure only vnp_ prefixed parameters are used for hashing as per some VNPAY
            // docs
            // However, typical IPN/Return URL hashing uses all received params (except hash
            // itself)
            // For now, using all received params as collected in vnp_Params
            hashData.append(fieldName);
            hashData.append('=');
            // Values in the hash string should be URL-encoded, similar to how the outgoing
            // hash is prepared.
            // request.getParameter() already decodes them, so we re-encode for hashing.
            hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            hashData.append('&');
        }
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1); // Remove last '&'
        }

        String calculatedSecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        String bookingCode = request.getParameter("vnp_TxnRef"); // Still get this for logging/session
        HttpSession session = request.getSession(false);

        if (vnp_SecureHash == null || !vnp_SecureHash.equals(calculatedSecureHash)) {
            System.err.println("VNPAY SecureHash mismatch for bookingCode: " + bookingCode + ". Received: "
                    + vnp_SecureHash + ", Calculated: " + calculatedSecureHash);
            System.err.println("Data used for hashing: " + hashData.toString());
            if (session != null && bookingCode != null) {
                session.removeAttribute(bookingCode);
            }
            response.sendRedirect(request.getContextPath()
                    + "/payment/paymentFailure.jsp?errorCode=INVALID_SIGNATURE&bookingCode=" + bookingCode);
            return;
        }

        List<PassengerTicketCreationData> passengerDataForTickets = null;
        if (session != null && bookingCode != null) {
            passengerDataForTickets = (List<PassengerTicketCreationData>) session.getAttribute(bookingCode);
        }

        Connection conn = null;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
            String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
            String vnp_PayDate = request.getParameter("vnp_PayDate");
            String vnp_BankCode = request.getParameter("vnp_BankCode");

            Booking booking = bookingDAO.findByBookingCode(conn, bookingCode);
            if (booking == null) {
                throw new ServletException("Booking not found for code: " + bookingCode
                        + ". VNPAY may have called back for a non-existent or already processed transaction.");
            }

            if ("Paid".equalsIgnoreCase(booking.getPaymentStatus())
                    && "Confirmed".equalsIgnoreCase(booking.getBookingStatus())) {
                if (session != null && bookingCode != null)
                    session.removeAttribute(bookingCode);
                response.sendRedirect(request.getContextPath() + "/payment/paymentSuccess.jsp?bookingCode="
                        + bookingCode + "&status=ALREADY_CONFIRMED");
                return;
            }

            if ("00".equals(vnp_ResponseCode)) {
                if (passengerDataForTickets == null) {
                    System.err.println(
                            "CRITICAL: Session data missing for successful payment. BookingCode: " + bookingCode);
                    bookingDAO.updateBookingStatus(conn, bookingCode, "Confirmed", "Paid",
                            "Session data missing, manual ticket creation needed.");
                    paymentTransactionDAO.updateTransactionStatus(conn, booking.getBookingID(), "VNPAY", "Success",
                            vnp_TransactionNo, "Session data missing for ticket creation. VNPAY PayDate: " + vnp_PayDate
                                    + ", BankCode: " + vnp_BankCode);
                    conn.commit();
                    response.sendRedirect(request.getContextPath() + "/payment/paymentSuccessButManual.jsp?bookingCode="
                            + bookingCode);
                    return;
                }

                bookingDAO.updateBookingStatus(conn, bookingCode, "Confirmed", "Paid", null);
                paymentTransactionDAO.updateTransactionStatus(conn, booking.getBookingID(), "VNPAY", "Success",
                        vnp_TransactionNo, "VNPAY PayDate: " + vnp_PayDate + ", BankCode: " + vnp_BankCode);

                for (PassengerTicketCreationData item : passengerDataForTickets) {
                    Passenger passengerDetails = passengerDAO.findById(conn, item.getPassengerId());
                    if (passengerDetails == null) {
                        throw new SQLException("Passenger not found for ID: " + item.getPassengerId()
                                + " during ticket creation for booking " + bookingCode);
                    }
                    OriginalCartItemDataDto cartItem = item.getOriginalCartItem();
                    Ticket ticket = new Ticket();
                    ticket.setTicketCode(UUID.randomUUID().toString().substring(0, 18).toUpperCase());
                    ticket.setBookingID(booking.getBookingID());
                    ticket.setTripID(Integer.parseInt(cartItem.getTripId()));
                    ticket.setSeatID(cartItem.getSeatID());
                    ticket.setPassengerID(item.getPassengerId());
                    ticket.setStartStationID(Integer.parseInt(cartItem.getLegOriginStationId()));
                    ticket.setEndStationID(Integer.parseInt(cartItem.getLegDestinationStationId()));
                    ticket.setPrice(BigDecimal.valueOf(cartItem.getCalculatedPrice()));
                    ticket.setTicketStatus("Valid");
                    ticket.setCoachNameSnapshot(cartItem.getCoachPosition());
                    ticket.setSeatNameSnapshot(cartItem.getSeatName());
                    ticket.setPassengerName(passengerDetails.getFullName());
                    ticket.setPassengerIDCardNumber(passengerDetails.getIdCardNumber());

                    ticketDAO.insertTicket(conn, ticket);
                }

                conn.commit();
                response.sendRedirect(
                        request.getContextPath() + "/payment/paymentSuccess.jsp?bookingCode=" + bookingCode);

            } else {
                String failureReason = "Payment failed/cancelled at VNPAY. Response Code: " + vnp_ResponseCode;
                bookingDAO.updateBookingStatus(conn, bookingCode, "Cancelled", "Unpaid", failureReason);
                paymentTransactionDAO.updateTransactionStatus(conn, booking.getBookingID(), "VNPAY", "Failed",
                        vnp_TransactionNo, failureReason);

                if (passengerDataForTickets != null) {
                    for (PassengerTicketCreationData item : passengerDataForTickets) {
                        passengerDAO.deletePassengerById(conn, item.getPassengerId());
                    }
                } else {
                    System.err.println(
                            "Warning: Session data missing for failed payment cleanup. BookingCode: " + bookingCode);
                }
                conn.commit();
                response.sendRedirect(request.getContextPath() + "/payment/paymentFailure.jsp?bookingCode="
                        + bookingCode + "&vnpayCode=" + vnp_ResponseCode);
            }

            temporarySeatHoldDAO.releaseAllHoldsForSession(conn, session.getId());

        } catch (SQLException | ServletException | NumberFormatException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed in VNPayReturnServlet: " + ex.getMessage());
                }
            }
            System.err.println("Error in VNPayReturnServlet for bookingCode " + bookingCode + ": " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/payment/paymentError.jsp?bookingCode=" + bookingCode
                    + "&error=" + e.getClass().getSimpleName());
        } finally {
            if (session != null && bookingCode != null) {

                session.removeAttribute(bookingCode);
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection in VNPayReturnServlet: " + e.getMessage());
                }
            }
        }
    }
}
