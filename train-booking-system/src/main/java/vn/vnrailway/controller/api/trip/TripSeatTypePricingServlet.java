package vn.vnrailway.controller.api.trip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.dao.impl.SeatRepositoryImpl;
import vn.vnrailway.dto.SeatTypePricingDTO;
import vn.vnrailway.utils.JsonUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * API endpoint to get seat type pricing summary for a trip using dedicated stored procedure
 */
@WebServlet("/api/trip/seat-pricing")
public class TripSeatTypePricingServlet extends HttpServlet {
    
    private SeatRepository seatRepository;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.seatRepository = new SeatRepositoryImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Get parameters
            String tripIdParam = request.getParameter("tripId");
            String originStationIdParam = request.getParameter("originStationId");
            String destinationStationIdParam = request.getParameter("destinationStationId");
            String bookingDateTimeParam = request.getParameter("bookingDateTime");
            String isRoundTripParam = request.getParameter("isRoundTrip");
            
            // Validate required parameters
            if (tripIdParam == null || originStationIdParam == null || 
                destinationStationIdParam == null || bookingDateTimeParam == null || 
                isRoundTripParam == null) {
                sendErrorResponse(response, "Missing required parameters: tripId, originStationId, destinationStationId, bookingDateTime, isRoundTrip", 400);
                return;
            }
            
            // Parse parameters
            int tripId = Integer.parseInt(tripIdParam);
            int originStationId = Integer.parseInt(originStationIdParam);
            int destinationStationId = Integer.parseInt(destinationStationIdParam);
            boolean isRoundTrip = Boolean.parseBoolean(isRoundTripParam);
            
            Timestamp bookingTimestamp;
            try {
                bookingTimestamp = Timestamp.valueOf(LocalDateTime.parse(bookingDateTimeParam));
            } catch (DateTimeParseException e) {
                bookingTimestamp = new Timestamp(System.currentTimeMillis());
            }
            
            // Get current user's session ID
            HttpSession session = request.getSession(false);
            String currentUserSessionId = (session != null) ? session.getId() : null;
            
            // Get seat type pricing using dedicated stored procedure
            List<SeatTypePricingDTO> seatTypePricing = seatRepository.getTripSeatTypePricing(
                tripId, originStationId, destinationStationId, 
                bookingTimestamp, isRoundTrip, currentUserSessionId
            );
            
            // Build response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("seatTypePricing", seatTypePricing);
            responseData.put("tripId", tripId);
            responseData.put("totalSeatTypes", seatTypePricing.size());
            
            String jsonResponse = JsonUtils.toJsonString(responseData);
            response.getWriter().write(jsonResponse);
            
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid number format for parameters", 400);
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }
    }
    
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        response.getWriter().write(JsonUtils.toJsonString(errorResponse));
    }
}