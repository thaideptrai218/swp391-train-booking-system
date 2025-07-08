package vn.vnrailway.controller.api.seat;

import vn.vnrailway.dao.SeatStatusDAO;
import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.dao.impl.SeatStatusDAOImpl;
import vn.vnrailway.dao.impl.TemporarySeatHoldDAOImpl;
import vn.vnrailway.dto.ApiResponse;
import vn.vnrailway.dto.HoldRequestDTO;
import vn.vnrailway.model.TemporarySeatHold;
import vn.vnrailway.utils.DBContext; // Your DB connection utility
import vn.vnrailway.utils.JsonUtils; // A conceptual utility for JSON parsing/serialization

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@WebServlet("/api/seats/hold")
public class HoldSeatApiServlet extends HttpServlet {

    private TemporarySeatHoldDAO temporarySeatHoldDAO;
    private SeatStatusDAO seatStatusDAO;
    // In a real application, use dependency injection or a service layer
    // For simplicity here, direct instantiation.
    public HoldSeatApiServlet() {
        this.temporarySeatHoldDAO = new TemporarySeatHoldDAOImpl();
        this.seatStatusDAO = new SeatStatusDAOImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Connection conn = null;
        try {
            HoldRequestDTO holdRequest = JsonUtils.parse(request.getReader(), HoldRequestDTO.class);
            if (holdRequest == null || holdRequest.getTripId() == 0 || holdRequest.getSeatId() == 0 || 
                holdRequest.getCoachId() == 0 || holdRequest.getLegOriginStationId() == 0 || 
                holdRequest.getLegDestinationStationId() == 0) {
                JsonUtils.toJson(ApiResponse.error("Invalid request payload. All fields are required."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            HttpSession httpSession = request.getSession();
            String sessionId = httpSession.getId();
            Integer userId = (Integer) httpSession.getAttribute("userId"); // Assuming userId is stored in session upon login

            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Check if current session already holds this specific seat for this leg
            TemporarySeatHold ownExistingHold = temporarySeatHoldDAO.findActiveHoldBySessionAndLeg(
                    conn, sessionId, holdRequest.getTripId(), holdRequest.getSeatId(),
                    holdRequest.getCoachId(), // Added coachId
                    holdRequest.getLegOriginStationId(), holdRequest.getLegDestinationStationId());

            if (ownExistingHold != null) {
                Date newExpiresAt = new Date(System.currentTimeMillis() + (5 * 60 * 1000)); // 5 mins
                temporarySeatHoldDAO.refreshHoldExpiry(conn, ownExistingHold.getHoldId(), newExpiresAt);
                conn.commit();
                sendSuccessResponse(response, "Hold refreshed successfully.", holdRequest.getSeatId(), holdRequest.getTripId(), newExpiresAt);
                return;
            }

            // Step 2: Call SP to get general status (Available, Occupied by other/permanent, Disabled)
            String currentSeatStatus = seatStatusDAO.checkSingleSeatAvailability(conn,
                    holdRequest.getTripId(), holdRequest.getSeatId(),
                    holdRequest.getLegOriginStationId(), holdRequest.getLegDestinationStationId());

            if (currentSeatStatus == null || currentSeatStatus.startsWith("Error:")) {
                conn.rollback();
                JsonUtils.toJson(ApiResponse.error("Error checking seat status: " + (currentSeatStatus != null ? currentSeatStatus : "Unknown error.")), response.getWriter());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            switch (currentSeatStatus.toUpperCase()) {
                case "DISABLED":
                    conn.rollback();
                    JsonUtils.toJson(ApiResponse.error("Seat is disabled and cannot be booked."), response.getWriter());
                    response.setStatus(HttpServletResponse.SC_GONE); // 410 Gone
                    return;
                case "OCCUPIED": // Covers permanent bookings or holds by other sessions
                    conn.rollback();
                    JsonUtils.toJson(ApiResponse.error("Seat is already occupied or held by another user."), response.getWriter());
                    response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                    return;
                case "AVAILABLE":
                    // Proceed to place the hold
                    break;
                default:
                    conn.rollback();
                    JsonUtils.toJson(ApiResponse.error("Unknown seat status received: " + currentSeatStatus), response.getWriter());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
            }

            // Step 3: Place the new hold
            Date expiresAt = new Date(System.currentTimeMillis() + (5 * 60 * 1000)); // 5 minutes
            TemporarySeatHold newHold = TemporarySeatHold.builder()
                    .tripId(holdRequest.getTripId())
                    .seatId(holdRequest.getSeatId())
                    .coachId(holdRequest.getCoachId())
                    .legOriginStationId(holdRequest.getLegOriginStationId())
                    .legDestinationStationId(holdRequest.getLegDestinationStationId())
                    .sessionId(sessionId)
                    .userId(userId)
                    .expiresAt(expiresAt)
                    // CreatedAt will be set by DB default
                    .build();

            long holdId = temporarySeatHoldDAO.addHold(conn, newHold); // Changed to long

            if (holdId != -1) { // Check if holdId is valid
                conn.commit();
                sendSuccessResponse(response, "Seat held successfully.", holdRequest.getSeatId(), holdRequest.getTripId(), expiresAt);
            } else {
                conn.rollback(); // Should ideally not happen if checks are correct, but for safety
                JsonUtils.toJson(ApiResponse.error("Failed to hold seat (concurrent update possible). Please try again."), response.getWriter());
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }

        } catch (SQLException e) {
            handleSQLException(conn, e, response);
        } catch (Exception e) { // Catch other exceptions like JSON parsing
            handleGenericException(conn, e, response);
        } finally {
            closeConnection(conn);
        }
    }

    private void sendSuccessResponse(HttpServletResponse response, String message, int seatId, int tripId, Date expiresAt) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expiresAtISO = sdf.format(expiresAt);

        ApiResponse<Object> apiResponse = new ApiResponse<>("success", message, 
            java.util.Map.of("seatId", seatId, "tripId", tripId, "holdExpiresAt", expiresAtISO));
        JsonUtils.toJson(apiResponse, response.getWriter());
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    private void handleSQLException(Connection conn, SQLException e, HttpServletResponse response) throws IOException {
        e.printStackTrace(); // Log appropriately
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log rollback failure
            }
        }
        JsonUtils.toJson(ApiResponse.error("Database error: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void handleGenericException(Connection conn, Exception e, HttpServletResponse response) throws IOException {
        e.printStackTrace(); // Log appropriately
        if (conn != null) { // If conn was opened before exception
            try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); } // Try to rollback if applicable
        }
        JsonUtils.toJson(ApiResponse.error("An unexpected error occurred: " + e.getMessage()), response.getWriter());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.getAutoCommit()) { // If we managed transactions
                    conn.setAutoCommit(true); // Reset before closing/returning to pool
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Log closing error
            }
        }
    }
}
