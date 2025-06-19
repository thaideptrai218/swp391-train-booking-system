package vn.vnrailway.controller.api.booking;

import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;
import vn.vnrailway.dto.ApiResponse;
import vn.vnrailway.dto.SeatToBookDTO;
import vn.vnrailway.model.TemporarySeatHold;
import vn.vnrailway.utils.DBContext;
import vn.vnrailway.utils.JsonUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/booking/initiateBooking")
public class InitiateBookingApiServlet extends HttpServlet {

    private TemporarySeatHoldDAO temporarySeatHoldDAO;

    public InitiateBookingApiServlet() {
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
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

            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            LocalDateTime bookingExpiryTime = LocalDateTime.now().plusMinutes(5);

            // Verify all temporary holds and update their expiry.
            // The holds are associated with the session, not a booking ID at this stage.
            for (SeatToBookDTO seatReq : seatsToBook) {
                TemporarySeatHold activeHold = temporarySeatHoldDAO.findActiveHoldBySessionAndLeg(
                        conn, sessionId, seatReq.getTripId(), seatReq.getSeatId(), seatReq.getCoachId(),
                        seatReq.getLegOriginStationId(), seatReq.getLegDestinationStationId());

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
            }
            conn.commit();
            String pendingBookingSessionIdentifier = UUID.randomUUID().toString();
            httpSession.setAttribute("pendingBookingSessionId", pendingBookingSessionIdentifier);
            httpSession.setAttribute("seatsForPendingBooking", new ArrayList<>(seatsToBook)); // Store a copy

            String redirectUrl = request.getContextPath() + "/ticketPayment";
            ApiResponse<Object> apiResponse = ApiResponse.success(
                    Map.of("redirectUrl", redirectUrl, "pendingBookingSessionId", pendingBookingSessionIdentifier),
                    "Seat holds confirmed. Please proceed to provide passenger details.");
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
