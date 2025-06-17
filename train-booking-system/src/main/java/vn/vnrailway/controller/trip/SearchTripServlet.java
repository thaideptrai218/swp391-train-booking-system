package vn.vnrailway.controller.trip; // Corrected package

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import jakarta.servlet.http.HttpSession; // Added for session Added for map
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
            } catch (SQLException e) {
                System.err.println("SearchTripServlet: SQL error fetching station details: " + e.getMessage());
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
        DateTimeFormatter inputDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ. Vui lòng sử dụng dd/MM/yyyy.");
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
            return;
        }

        try {
            List<TripSearchResultDTO> outboundTrips = tripService.searchAvailableTrips(originStationId,
                    destinationStationId, departureLocalDate);
            request.setAttribute("outboundTrips", outboundTrips);
            request.setAttribute("outboundTripsFromServlet", true); // Flag to prevent mock data in JSP

            request.setAttribute("departureDateDisplay", departureLocalDate.format(displayDateFormatter));
            request.setAttribute("originStationDisplay", originStationName);
            request.setAttribute("destinationStationDisplay", destinationStationName);

            // Store individual search criteria in session for POST-back
            HttpSession session = request.getSession();
            session.setAttribute("lastQuery_originalStationId", originStationIdStr);
            session.setAttribute("lastQuery_destinationStationId", destinationStationIdStr);
            session.setAttribute("lastQuery_departureDate", departureDateStr);
            if (returnDateStr != null && !returnDateStr.trim().isEmpty()) {
                session.setAttribute("lastQuery_returnDate", returnDateStr);
            } else {
                session.removeAttribute("lastQuery_returnDate"); // Ensure it's cleared if not present
            }
            session.setAttribute("lastQuery_originalStationName", originStationName);
            session.setAttribute("lastQuery_destinationStationName", destinationStationName);

            if (returnLocalDate != null) {
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
