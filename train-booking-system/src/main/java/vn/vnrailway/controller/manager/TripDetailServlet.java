package vn.vnrailway.controller.manager;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.TripStationInfoDTO;
import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal; // Import BigDecimal

@WebServlet("/tripDetail")
public class TripDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TripRepository tripRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        tripRepository = new TripRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tripIdStr = request.getParameter("tripId");

        if (tripIdStr != null && !tripIdStr.trim().isEmpty()) {
            try {
                int tripId = Integer.parseInt(tripIdStr);
                List<TripStationInfoDTO> tripStationDetails = tripRepository.findTripStationDetailsByTripId(tripId);

                if (tripStationDetails != null && !tripStationDetails.isEmpty()) {
                    request.setAttribute("tripStationDetails", tripStationDetails);
                    // Optionally, set the tripId itself if needed in the JSP for a header or title
                    request.setAttribute("tripIdForDetail", tripId);
                } else {
                    request.setAttribute("message", "No station details found for Trip ID: " + tripId);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Trip ID format: " + tripIdStr);
            } catch (SQLException e) {
                request.setAttribute("errorMessage", "Database error retrieving trip station details.");
                // Log the exception for server-side debugging
                e.printStackTrace();
            }
        } else {
            request.setAttribute("errorMessage", "Trip ID was not provided.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/manager/tripDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tripIdStr = request.getParameter("tripId");
        String stationIdStr = request.getParameter("stationId");
        String dateStr = request.getParameter("scheduledDepartureDate");
        String timeStr = request.getParameter("scheduledDepartureTime");
        String originalTripIdForRedirect = request.getParameter("originalTripId"); // To redirect back to the correct
                                                                                   // trip detail

        if (originalTripIdForRedirect == null || originalTripIdForRedirect.trim().isEmpty()) {
            originalTripIdForRedirect = tripIdStr; // Fallback if not passed separately
        }

        try {
            int tripId = Integer.parseInt(tripIdStr);
            // int stationId = Integer.parseInt(stationIdStr); // stationId from request is
            // for the first station, not needed for loop logic directly
            java.time.LocalDate firstStationDepartureDate = java.time.LocalDate.parse(dateStr);
            java.time.LocalTime firstStationDepartureTime = java.time.LocalTime.parse(timeStr);
            java.time.LocalDateTime firstStationActualDeparture = java.time.LocalDateTime.of(firstStationDepartureDate,
                    firstStationDepartureTime);

            List<TripStationInfoDTO> stationDetails = tripRepository.findTripStationDetailsByTripId(tripId);

            if (stationDetails == null || stationDetails.isEmpty()) {
                request.getSession().setAttribute("errorMessage",
                        "No station details found to update for Trip ID: " + tripId);
                response.sendRedirect(request.getContextPath() + "/tripDetail?tripId=" + originalTripIdForRedirect);
                return;
            }

            java.time.LocalDateTime previousStationDepartureCalc = firstStationActualDeparture;
            // Estimate time for the first station is from the absolute start of the route.
            // If the first station in the list IS the start of the route, its estimateTime
            // might be 0 or null.
            // The travel time to the first station is effectively 0 if it's the origin.
            java.math.BigDecimal previousEstimateTimeHours = java.math.BigDecimal.ZERO;

            // For the very first station in the list
            TripStationInfoDTO firstStationDto = stationDetails.get(0);
            java.time.LocalDateTime firstScheduledArrival = firstStationActualDeparture; // Arrives when it departs,
                                                                                         // effectively
            Integer firstStopTimeMinutes = firstStationDto.getDefaultStopTime();
            if (firstStopTimeMinutes == null)
                firstStopTimeMinutes = 0; // Handle null stop time
            java.time.LocalDateTime firstScheduledDeparture = firstScheduledArrival.plusMinutes(firstStopTimeMinutes);

            tripRepository.updateTripStationTimes(firstStationDto.getTripID(), firstStationDto.getStationId(),
                    firstScheduledArrival, firstScheduledDeparture);

            // Update for next iteration, using the *actual* departure of the first station
            // as the basis.
            previousStationDepartureCalc = firstScheduledDeparture;
            if (firstStationDto.getEstimateTime() != null) {
                previousEstimateTimeHours = firstStationDto.getEstimateTime();
            }

            for (int i = 1; i < stationDetails.size(); i++) {
                TripStationInfoDTO currentStationDto = stationDetails.get(i);

                java.math.BigDecimal currentEstimateTimeHours = currentStationDto.getEstimateTime();
                Integer currentStopTimeMinutes = currentStationDto.getDefaultStopTime();

                if (currentEstimateTimeHours == null) { // Should not happen if data is clean
                    System.err.println("Warning: EstimateTime is null for station " + currentStationDto.getStationName()
                            + ". Skipping its update.");
                    continue;
                }
                if (currentStopTimeMinutes == null)
                    currentStopTimeMinutes = 0;

                // Travel time from previous station to current station
                java.math.BigDecimal travelTimeHoursDecimal = currentEstimateTimeHours
                        .subtract(previousEstimateTimeHours);
                long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);

                java.time.LocalDateTime currentScheduledArrival = previousStationDepartureCalc
                        .plusSeconds(travelTimeSeconds);
                java.time.LocalDateTime currentScheduledDeparture = currentScheduledArrival
                        .plusMinutes(currentStopTimeMinutes);

                tripRepository.updateTripStationTimes(currentStationDto.getTripID(), currentStationDto.getStationId(),
                        currentScheduledArrival, currentScheduledDeparture);

                previousStationDepartureCalc = currentScheduledDeparture;
                previousEstimateTimeHours = currentEstimateTimeHours;
            }

            request.getSession().setAttribute("successMessage",
                    "Scheduled times updated successfully for all stations in trip " + tripId + ".");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("errorMessage", "Invalid ID or date/time format for update.");
            e.printStackTrace();
        } catch (java.time.format.DateTimeParseException e) {
            request.getSession().setAttribute("errorMessage", "Invalid date or time string format for update.");
            e.printStackTrace();
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Database error during update: " + e.getMessage());
            e.printStackTrace();
        }
        // Redirect back to the trip detail page using GET to refresh data and show
        // message
        response.sendRedirect(request.getContextPath() + "/tripDetail?tripId=" + originalTripIdForRedirect);
    }
}
