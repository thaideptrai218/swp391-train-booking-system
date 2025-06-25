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
            int stationId = Integer.parseInt(stationIdStr);
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.LocalTime time = java.time.LocalTime.parse(timeStr);
            java.time.LocalDateTime newScheduledDeparture = java.time.LocalDateTime.of(date, time);

            boolean success = tripRepository.updateTripStationScheduledDeparture(tripId, stationId,
                    newScheduledDeparture);

            if (success) {
                request.getSession().setAttribute("successMessage",
                        "Scheduled departure updated successfully for station " + stationId + " in trip " + tripId
                                + ".");
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to update scheduled departure.");
            }
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
