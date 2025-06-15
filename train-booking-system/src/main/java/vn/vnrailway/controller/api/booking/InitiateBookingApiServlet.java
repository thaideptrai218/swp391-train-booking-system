package vn.vnrailway.controller.api.booking;

import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.TicketRepository; // Changed from TicketDAO
import vn.vnrailway.dao.BookingRepository; // Assuming you have a BookingRepository
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;
// import vn.vnrailway.dao.impl.TicketRepositoryImpl; // Not used at this stage
import vn.vnrailway.dao.impl.BookingRepositoryImpl; // Assuming implementation
import vn.vnrailway.dao.UserRepository; // Added UserRepository
import vn.vnrailway.dao.impl.UserRepositoryImpl; // Added UserRepositoryImpl
import vn.vnrailway.dto.ApiResponse;
// BookingInitiationRequestDTO might not be needed if client sends List<SeatToBookDTO> directly
// import vn.vnrailway.dto.BookingInitiationRequestDTO; 
import vn.vnrailway.dto.SeatToBookDTO;
import vn.vnrailway.model.TemporarySeatHold;
// import vn.vnrailway.model.Ticket; // Not used at this stage
import vn.vnrailway.model.Booking; // Assuming you have a Booking model
import vn.vnrailway.model.User; // Added User model
import vn.vnrailway.utils.DBContext;
import vn.vnrailway.utils.JsonUtils;
import vn.vnrailway.utils.CodeGenerator; // Added CodeGenerator import

import com.fasterxml.jackson.core.type.TypeReference; // Changed for Jackson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal; // Added
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp; // Keep if DBContext or DAOs use it, otherwise can remove if all LocalDateTime
import java.time.LocalDateTime; // Added
import java.util.ArrayList;
// import java.util.Date; // Replaced by LocalDateTime where appropriate
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/booking/initiate")
public class InitiateBookingApiServlet extends HttpServlet {

    private TemporarySeatHoldDAO temporarySeatHoldDAO;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    // private TicketRepository ticketRepository; // Tickets are not created at this
    // stage

    public InitiateBookingApiServlet() {
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
        this.bookingRepository = new BookingRepositoryImpl();
        this.userRepository = new UserRepositoryImpl(); // Initialize UserRepository
        // this.ticketRepository = new TicketRepositoryImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
            // Use Jackson's TypeReference for parsing a list of objects
            TypeReference<ArrayList<SeatToBookDTO>> listTypeRef = new TypeReference<ArrayList<SeatToBookDTO>>() {
            };
            List<SeatToBookDTO> seatsToBook = JsonUtils.parse(request.getReader(), listTypeRef);

            if (seatsToBook == null || seatsToBook.isEmpty()) {
                JsonUtils.toJson(ApiResponse.error("No seats provided for booking initiation."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            HttpSession httpSession = request.getSession(false);
            if (httpSession == null) {
                JsonUtils.toJson(ApiResponse.error("No active session found. Please select seats again."),
                        response.getWriter());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String sessionId = httpSession.getId();
            Integer userId = (Integer) httpSession.getAttribute("userId");
            String bookingCodeForGuestHandling = CodeGenerator.generateBookingCode(); // Generate once for potential
                                                                                      // guest

            if (userId == null) {
                // Handle guest user: Create a temporary guest account
                User guestUser = new User();
                // Ensure unique username and email for guest, possibly using booking code or
                // timestamp
                String guestIdentifier = "guest_" + bookingCodeForGuestHandling;
                // guestUser.setUserName(guestIdentifier); // UserName field removed from User
                // model
                guestUser.setFullName("Khách vãng lai"); // "Guest User" in Vietnamese
                guestUser.setEmail(guestIdentifier + "@vnrailway.guest"); // Placeholder email
                guestUser.setPhoneNumber("0000000000"); // Placeholder phone
                guestUser.setRole("Guest"); // Ensure role matches DB constraint
                guestUser.setActive(true);
                guestUser.setGuestAccount(true); // Set IsGuestAccount flag
                guestUser.setCreatedAt(LocalDateTime.now());
                // PasswordHash can be null or a non-loginable placeholder if DB requires it
                // guestUser.setPasswordHash(null); // Or some default non-functional hash

                // Save guest user - Assuming userRepository.save(user) returns the saved user
                // with ID
                // This needs to be done outside the main booking transaction or handled
                // carefully if within.
                // For simplicity, let's assume it can be done before the main transaction
                // starts,
                // or the save method handles its own transaction or is safe to call before
                // conn.setAutoCommit(false).
                // If userRepository.save needs a Connection, it must be handled.
                // Let's assume for now userRepository.save can operate independently or get its
                // own connection.
                User savedGuestUser = userRepository.save(guestUser); // This save method needs to exist and return User
                                                                      // with ID
                if (savedGuestUser == null || savedGuestUser.getUserID() == 0) {
                    JsonUtils.toJson(ApiResponse.error("Failed to create a guest user account."), response.getWriter());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                userId = savedGuestUser.getUserID();
                // Optionally, put guest user ID in session if needed later for this guest's
                // flow
                // httpSession.setAttribute("userId", userId); // This would make them "logged
                // in" as guest
            }

            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Create a new Booking record
            Booking newBooking = new Booking();
            // Use the bookingCode generated earlier (consistent for guest email/username if
            // guest was created)
            newBooking.setBookingCode(bookingCodeForGuestHandling);

            // userId will now be set for both logged-in users and newly created guest users
            newBooking.setUserID(userId);

            newBooking.setBookingDateTime(LocalDateTime.now());
            newBooking.setBookingStatus("Pending"); // Set to PendingPayment as user proceeds to payment page
            newBooking.setPaymentStatus("Unpaid");
            LocalDateTime bookingExpiryTime = LocalDateTime.now().plusMinutes(15); // e.g., 15 minutes for next steps
            newBooking.setExpiredAt(bookingExpiryTime);
            // TotalPrice will be calculated and set when tickets are finalized.
            newBooking.setTotalPrice(BigDecimal.ZERO); // Initialize to zero or null if DB allows

            Booking savedBooking = bookingRepository.save(newBooking); // Assumes save returns persisted Booking with ID
            if (savedBooking == null || savedBooking.getBookingID() == 0) {
                conn.rollback();
                JsonUtils.toJson(ApiResponse.error("Failed to create a new booking record."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
            int generatedBookingID = savedBooking.getBookingID();

            // Step 2: Verify all temporary holds and update their expiry to match the
            // booking expiry
            // Also, associate them with the bookingID if your TemporarySeatHold model has a
            // bookingId field (optional but good)
            for (SeatToBookDTO seatReq : seatsToBook) {
                TemporarySeatHold activeHold = temporarySeatHoldDAO.findActiveHoldBySessionAndLeg(
                        conn, seatReq.getTripId(), seatReq.getSeatId(),
                        seatReq.getLegOriginStationId(), seatReq.getLegDestinationStationId(), sessionId);

                if (activeHold == null) {
                    conn.rollback();
                    JsonUtils.toJson(
                            ApiResponse.error("Hold for seat " + seatReq.getSeatId() + " on trip " + seatReq.getTripId()
                                    + " has expired or is invalid. Please re-select your seats."),
                            response.getWriter());
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    return;
                }
                // Extend the hold to match the booking's expiry time
                boolean refreshed = temporarySeatHoldDAO.refreshHoldExpiry(conn, activeHold.getHoldId(),
                        Timestamp.valueOf(bookingExpiryTime));
                if (!refreshed) {
                    conn.rollback();
                    JsonUtils.toJson(
                            ApiResponse.error(
                                    "Failed to extend hold for seat " + seatReq.getSeatId() + ". Please try again."),
                            response.getWriter());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                }
                // Optional: If TemporarySeatHold has a bookingId field:
                // activeHold.setBookingId(generatedBookingID);
                // temporarySeatHoldDAO.updateBookingIdForHold(conn, activeHold.getHoldId(),
                // generatedBookingID);
            }

            // Tickets are NOT created here. They will be created after passenger details
            // are submitted.
            // The Booking's TotalPrice will also be updated at that stage.

            conn.commit();

            // Store booking ID and the list of seats (SeatToBookDTO) in session for the
            // next page
            httpSession.setAttribute("pendingBookingID", generatedBookingID);
            httpSession.setAttribute("seatsForPendingBooking", new ArrayList<>(seatsToBook)); // Store a copy
            if (savedBooking.getBookingCode() != null) { // If Booking model has a user-friendly code
                httpSession.setAttribute("pendingBookingCode", savedBooking.getBookingCode());
            }

            String redirectUrl = request.getContextPath() + "/ticketPayment"; // Corrected to point to
                                                                              // TicketPaymentServlet
            ApiResponse<Object> apiResponse = ApiResponse.success(
                    Map.of("redirectUrl", redirectUrl, "bookingId", generatedBookingID),
                    "Booking process initiated. Please provide passenger details.");
            JsonUtils.toJson(apiResponse, response.getWriter());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            handleSQLException(conn, e, response);
        } catch (Exception e) {
            handleGenericException(conn, e, response);
        } finally {
            closeConnection(conn);
        }
    }

    private void handleSQLException(Connection conn, SQLException e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        JsonUtils.toJson(ApiResponse.error("Database error during booking initiation: " + e.getMessage()),
                response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void handleGenericException(Connection conn, Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace();
        if (conn != null) {
            try {
                conn.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        JsonUtils.toJson(ApiResponse.error("An unexpected error occurred during booking initiation: " + e.getMessage()),
                response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void closeConnection(Connection conn) {
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
