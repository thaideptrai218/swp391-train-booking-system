package vn.vnrailway.controller.api.trip;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.utils.JsonUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API endpoint for searching trips based on origin, destination, and date
 */
@WebServlet("/api/trip/search")
public class TripSearchServlet extends HttpServlet {

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
            // Get search parameters
            String originStationIdParam = request.getParameter("originStationId");
            String destinationStationIdParam = request.getParameter("destinationStationId");
            String departureDateParam = request.getParameter("departureDate");

            // Validate required parameters
            if (originStationIdParam == null || destinationStationIdParam == null || departureDateParam == null) {
                sendErrorResponse(response,
                        "Missing required parameters: originStationId, destinationStationId, departureDate", 400);
                return;
            }

            // Parse parameters
            int originStationId;
            int destinationStationId;
            LocalDate departureDate;

            try {
                originStationId = Integer.parseInt(originStationIdParam);
                destinationStationId = Integer.parseInt(destinationStationIdParam);

                // Try YYYY-MM-DD format first (from date picker), then dd/MM/yyyy format
                try {
                    departureDate = LocalDate.parse(departureDateParam);
                } catch (DateTimeParseException e1) {
                    try {
                        DateTimeFormatter ddMMyyyy = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        departureDate = LocalDate.parse(departureDateParam, ddMMyyyy);
                    } catch (DateTimeParseException e2) {
                        sendErrorResponse(response, "Invalid date format. Use YYYY-MM-DD or dd/MM/yyyy", 400);
                        return;
                    }
                }
            } catch (NumberFormatException e) {
                sendErrorResponse(response, "Invalid station ID format", 400);
                return;
            }

            // Validate station IDs are different
            if (originStationId == destinationStationId) {
                sendErrorResponse(response, "Origin and destination stations must be different", 400);
                return;
            }

            // Search for trips
            List<TripSearchResultDTO> trips = tripRepository.searchAvailableTrips(
                    originStationId,
                    destinationStationId,
                    departureDate);

            // Build response
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("trips", trips);
            responseData.put("searchCriteria", Map.of(
                    "originStationId", originStationId,
                    "destinationStationId", destinationStationId,
                    "departureDate", departureDate.toString()));
            responseData.put("totalResults", trips.size());

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