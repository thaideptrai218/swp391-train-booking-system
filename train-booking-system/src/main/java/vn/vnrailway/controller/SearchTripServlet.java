package vn.vnrailway.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date; // Keep for sdf parsing if needed, but prefer LocalDate
import java.sql.SQLException;

import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.service.TripService;
import vn.vnrailway.service.impl.TripServiceImpl; // Assuming this exists

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/searchTrip")
public class SearchTripServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private TripService tripService;

    @Override
    public void init() throws ServletException {
        super.init();
        tripService = new TripServiceImpl(); // Initialize TripService
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the search form if accessed via GET
        request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // Get parameters from the form in searchTrip.jsp
        String originStationName = request.getParameter("original-station-name");
        String destinationStationName = request.getParameter("destination-station-name");
        String originStationIdStr = request.getParameter("originalStationId");
        String destinationStationIdStr = request.getParameter("destinationStationId");
        String departureDateStr = request.getParameter("departure-date");
        String returnDateStr = request.getParameter("return-date"); // Optional

        int originStationId;
        int destinationStationId;
        LocalDate departureLocalDate;
        LocalDate returnLocalDate = null;
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Changed to accept dd/MM/yyyy
        DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            originStationId = Integer.parseInt(originStationIdStr);
            destinationStationId = Integer.parseInt(destinationStationIdStr);
            departureLocalDate = LocalDate.parse(departureDateStr, inputDateFormatter);

            if (returnDateStr != null && !returnDateStr.trim().isEmpty()) {
                returnLocalDate = LocalDate.parse(returnDateStr, inputDateFormatter);
                if (returnLocalDate.isBefore(departureLocalDate)) {
                    request.setAttribute("errorMessage", "Ngày về không thể trước ngày đi.");
                    request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID ga không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
            return;
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ. Vui lòng sử dụng dd/MM/yyyy."); // Updated error message
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
            return;
        }

        try {
            List<TripSearchResultDTO> outboundTrips = tripService.searchAvailableTrips(originStationId,
                    destinationStationId, departureLocalDate);
            request.setAttribute("outboundTrips", outboundTrips);
            request.setAttribute("outboundTripsFromServlet", true); // Flag to prevent mock data in JSP

            // Set display parameters for the JSP
            request.setAttribute("departureDateDisplay", departureLocalDate.format(displayDateFormatter));
            request.setAttribute("originStationDisplay", originStationName);
            request.setAttribute("destinationStationDisplay", destinationStationName);

            if (returnLocalDate != null) {
                // Search for return trips (destination becomes origin, origin becomes
                // destination)
                List<TripSearchResultDTO> returnTrips = tripService.searchAvailableTrips(destinationStationId,
                        originStationId, returnLocalDate);
                request.setAttribute("returnTrips", returnTrips);
                request.setAttribute("returnDateDisplay", returnLocalDate.format(displayDateFormatter));
            }

            request.getRequestDispatcher("/WEB-INF/jsp/trip/tripResult.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the full error for debugging
            // Provide a user-friendly error message
            request.setAttribute("errorMessage",
                    "Đã xảy ra lỗi khi tìm kiếm chuyến tàu. Vui lòng thử lại. Chi tiết: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // Catch any other unexpected errors
            request.setAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
        }
    }
}
