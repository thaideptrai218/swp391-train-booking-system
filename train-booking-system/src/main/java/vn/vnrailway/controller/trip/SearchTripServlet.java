package vn.vnrailway.controller.trip; // Corrected package

import java.io.IOException;
// import java.text.ParseException; // No longer explicitly used
// import java.text.SimpleDateFormat; // No longer explicitly used
import java.time.LocalDate;
// import java.time.ZoneId; // No longer explicitly used
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
// import java.util.Date; // No longer explicitly used
import java.sql.SQLException;
import java.util.Optional; // Added for Optional

import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.service.TripService;
import vn.vnrailway.service.impl.TripServiceImpl;
import vn.vnrailway.dao.StationRepository; // Added
import vn.vnrailway.dao.impl.StationRepositoryImpl; // Added
import vn.vnrailway.model.Station; // Added

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
    private StationRepository stationRepository; // Added

    @Override
    public void init() throws ServletException {
        super.init();
        tripService = new TripServiceImpl();
        stationRepository = new StationRepositoryImpl(); // Added initialization
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String originStationIdParam = request.getParameter("originalStation"); // Matches landing-page.jsp link
        String destinationStationIdParam = request.getParameter("destinationID"); // Matches landing-page.jsp link

        if (originStationIdParam != null && !originStationIdParam.isEmpty() &&
                destinationStationIdParam != null && !destinationStationIdParam.isEmpty()) {
            try {
                int originId = Integer.parseInt(originStationIdParam);
                int destinationId = Integer.parseInt(destinationStationIdParam);

                Optional<Station> originStationOpt = stationRepository.findById(originId);
                Optional<Station> destinationStationOpt = stationRepository.findById(destinationId);

                if (originStationOpt.isPresent()) {
                    Station originStation = originStationOpt.get();
                    request.setAttribute("prefill_originalStationId", originStation.getStationID());
                    request.setAttribute("prefill_originalStationName", originStation.getStationName());
                } else {
                    // Log or handle missing origin station
                    System.err.println("SearchTripServlet: Origin station not found for ID: " + originId);
                }

                if (destinationStationOpt.isPresent()) {
                    Station destinationStation = destinationStationOpt.get();
                    request.setAttribute("prefill_destinationStationId", destinationStation.getStationID());
                    request.setAttribute("prefill_destinationStationName", destinationStation.getStationName());
                } else {
                    // Log or handle missing destination station
                    System.err.println("SearchTripServlet: Destination station not found for ID: " + destinationId);
                }

            } catch (NumberFormatException e) {
                System.err.println("SearchTripServlet: Invalid station ID format from parameters.");
                // Optionally set an error message for the user
                // request.setAttribute("errorMessage", "Lỗi ID ga không hợp lệ từ trang
                // trước.");
            } catch (SQLException e) {
                System.err.println("SearchTripServlet: SQL error fetching station details: " + e.getMessage());
                // Optionally set an error message for the user
                // request.setAttribute("errorMessage", "Lỗi truy vấn thông tin ga.");
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
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
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Changed to accept
                                                                                          // dd/MM/yyyy
        DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            originStationId = Integer.parseInt(originStationIdStr);
            destinationStationId = Integer.parseInt(destinationStationIdStr);
            departureLocalDate = LocalDate.parse(departureDateStr, inputDateFormatter);

            if (returnDateStr != null && !returnDateStr.trim().isEmpty()) {
                returnLocalDate = LocalDate.parse(returnDateStr, inputDateFormatter);
                if (returnLocalDate.isBefore(departureLocalDate)) {
                    request.setAttribute("errorMessage", "Ngày về không thể trước ngày đi.");
                    request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
                    return;
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID ga không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
            return;
        } catch (DateTimeParseException e) {
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ. Vui lòng sử dụng dd/MM/yyyy."); // Updated
                                                                                                               // error
                                                                                                               // message
            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
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

            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/tripResult.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace(); // Log the full error for debugging
            // Provide a user-friendly error message
            request.setAttribute("errorMessage",
                    "Đã xảy ra lỗi khi tìm kiếm chuyến tàu. Vui lòng thử lại. Chi tiết: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace(); // Catch any other unexpected errors
            request.setAttribute("errorMessage", "Đã có lỗi không mong muốn xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/searchTrip.jsp").forward(request, response);
        }
    }
}
