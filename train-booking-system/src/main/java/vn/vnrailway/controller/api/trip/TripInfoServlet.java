package vn.vnrailway.controller.api.trip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.TripStationInfoDTO;
import vn.vnrailway.model.Trip;
import vn.vnrailway.utils.JsonUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * API endpoint for retrieving detailed trip information including
 * trip details and all stations along the route
 */
@WebServlet("/api/trip/info")
public class TripInfoServlet extends HttpServlet {
    
    private TripRepository tripRepository;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.tripRepository = new TripRepositoryImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Get trip ID from request parameters
            String tripIdParam = request.getParameter("tripId");
            if (tripIdParam == null || tripIdParam.trim().isEmpty()) {
                sendErrorResponse(response, "Trip ID is required", 400);
                return;
            }
            
            int tripId;
            try {
                tripId = Integer.parseInt(tripIdParam);
            } catch (NumberFormatException e) {
                sendErrorResponse(response, "Invalid trip ID format", 400);
                return;
            }
            
            // Get trip details
            Optional<Trip> tripOpt = tripRepository.findById(tripId);
            if (!tripOpt.isPresent()) {
                sendErrorResponse(response, "Trip not found", 404);
                return;
            }
            Trip trip = tripOpt.get();
            
            // Get detailed trip station information
            List<TripStationInfoDTO> tripStations = tripRepository.findTripStationDetailsByTripId(tripId);
            
            // Build response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("trip", trip);
            responseData.put("stations", tripStations);
            
            // Calculate trip summary information
            if (!tripStations.isEmpty()) {
                TripStationInfoDTO firstStation = tripStations.get(0);
                TripStationInfoDTO lastStation = tripStations.get(tripStations.size() - 1);
                
                Map<String, Object> summary = new HashMap<>();
                summary.put("originStation", Map.of(
                    "stationId", firstStation.getStationId(),
                    "stationName", firstStation.getStationName(),
                    "departureTime", firstStation.getScheduledDepartureDate() + "T" + firstStation.getScheduledDepartureTime()
                ));
                summary.put("destinationStation", Map.of(
                    "stationId", lastStation.getStationId(),
                    "stationName", lastStation.getStationName(),
                    "arrivalTime", lastStation.getScheduledDepartureDate() + "T" + lastStation.getScheduledDepartureTime()
                ));
                summary.put("totalDistance", lastStation.getDistanceFromStart());
                summary.put("totalDuration", lastStation.getEstimateTime());
                summary.put("trainName", "Train-" + trip.getTrainID()); // Construct train name
                summary.put("routeName", "Route-" + trip.getRouteID()); // Construct route name
                
                responseData.put("summary", summary);
            }
            
            String jsonResponse = JsonUtils.toJsonString(responseData);
            response.getWriter().write(jsonResponse);
            
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