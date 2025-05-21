package vn.vnrailway.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
// Potentially import DTOs and Services later
// import vn.vnrailway.dto.TripSearchResultDTO;
// import vn.vnrailway.service.TripService;
// import vn.vnrailway.service.impl.TripServiceImpl;

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

    // private TripService tripService; // Will be initialized later, possibly in init()

    @Override
    public void init() throws ServletException {
        super.init();
        // tripService = new TripServiceImpl(); // Example initialization
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Forward to the search form if accessed via GET
        request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String originStation = request.getParameter("originStation");
        String destinationStation = request.getParameter("destinationStation");
        String departureDateStr = request.getParameter("departureDate");
        String returnDateStr = request.getParameter("returnDate");

        int numAdults = parseIntOrDefault(request.getParameter("numAdults"), 1);
        int numChildren = parseIntOrDefault(request.getParameter("numChildren"), 0);
        int numStudents = parseIntOrDefault(request.getParameter("numStudents"), 0);
        int numElderly = parseIntOrDefault(request.getParameter("numElderly"), 0);
        int numGroup = parseIntOrDefault(request.getParameter("numGroup"), 0);

        // Basic validation (can be expanded)
        if (originStation == null || originStation.trim().isEmpty() ||
            destinationStation == null || destinationStation.trim().isEmpty() ||
            departureDateStr == null || departureDateStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập đầy đủ thông tin bắt buộc: Ga đi, Ga đến, Ngày đi.");
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date departureDate = null;
        Date returnDate = null;

        try {
            departureDate = sdf.parse(departureDateStr);
            if (returnDateStr != null && !returnDateStr.trim().isEmpty()) {
                returnDate = sdf.parse(returnDateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace(); // Log error
            request.setAttribute("errorMessage", "Định dạng ngày không hợp lệ.");
            request.getRequestDispatcher("/WEB-INF/jsp/trip/searchTrip.jsp").forward(request, response);
            return;
        }

        // TODO: Call TripService to search for trips
        // List<TripSearchResultDTO> tripResults = tripService.searchAvailableTrips(originStation, destinationStation, departureDate, returnDate, numAdults, numChildren, ...);
        
        // For now, let's use placeholder data
        // List<Object> tripResults = new ArrayList<>(); // Replace Object with TripSearchResultDTO
        // request.setAttribute("tripResults", tripResults);
        // request.setAttribute("searchParams", {originStation, destinationStation, departureDateStr, ...}); // Pass search params for display

        // Forward to results page (to be created)
        // request.getRequestDispatcher("/WEB-INF/jsp/trip/tripResults.jsp").forward(request, response);
        
        // Placeholder response until tripResults.jsp is created
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h1>Search Parameters Received:</h1>");
        response.getWriter().println("<p>Origin: " + originStation + "</p>");
        response.getWriter().println("<p>Destination: " + destinationStation + "</p>");
        response.getWriter().println("<p>Departure Date: " + departureDateStr + "</p>");
        if (returnDateStr != null && !returnDateStr.isEmpty()) {
            response.getWriter().println("<p>Return Date: " + returnDateStr + "</p>");
        }
        response.getWriter().println("<p>Adults: " + numAdults + "</p>");
        response.getWriter().println("<p>Children: " + numChildren + "</p>");
        response.getWriter().println("<p>Students: " + numStudents + "</p>");
        response.getWriter().println("<p>Elderly: " + numElderly + "</p>");
        response.getWriter().println("<p>Group: " + numGroup + "</p>");
        response.getWriter().println("<p><em>Trip results page (tripResults.jsp) will be implemented next.</em></p>");
        response.getWriter().println("</body></html>");
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
