package vn.vnrailway.controller.api.seat;

import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.dao.impl.SeatRepositoryImpl;
import vn.vnrailway.dto.SeatStatusDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Import HttpSession
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet(name = "GetCoachSeatsServlet", urlPatterns = { "/api/seats/getSeat" }) // Updated URL Pattern
public class GetCoachSeatsServlet extends HttpServlet {

    private SeatRepository seatRepository;
    private ObjectMapper objectMapper; // Instantiate ObjectMapper once

    @Override
    public void init() throws ServletException {
        super.init();
        this.seatRepository = new SeatRepositoryImpl();
        this.objectMapper = new ObjectMapper();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String errorMessage = null;

        try {
            String tripIdParam = request.getParameter("tripId");
            String coachIdParam = request.getParameter("coachId");
            String legOriginStationIdParam = request.getParameter("legOriginStationId");
            String legDestinationStationIdParam = request.getParameter("legDestinationStationId");
            String bookingDateTimeParam = request.getParameter("bookingDateTime");
            String isRoundTripParam = request.getParameter("isRoundTrip");

            if (tripIdParam == null || coachIdParam == null || legOriginStationIdParam == null
                    || legDestinationStationIdParam == null || bookingDateTimeParam == null
                    || isRoundTripParam == null) {
                throw new IllegalArgumentException(
                        "Missing required parameters: tripId, coachId, legOriginStationId, legDestinationStationId, bookingDateTime, isRoundTrip");
            }

            int tripId = Integer.parseInt(tripIdParam);
            int coachId = Integer.parseInt(coachIdParam);
            int legOriginStationId = Integer.parseInt(legOriginStationIdParam);
            int legDestinationStationId = Integer.parseInt(legDestinationStationIdParam);
            boolean isRoundTrip = Boolean.parseBoolean(isRoundTripParam);

            Timestamp bookingTimestamp;
            try {
                bookingTimestamp = Timestamp.valueOf(LocalDateTime.parse(bookingDateTimeParam));
            } catch (DateTimeParseException e) {
                System.err.println("Invalid bookingDateTime format: " + bookingDateTimeParam
                        + ". Falling back to current time. Error: " + e.getMessage());
                bookingTimestamp = new Timestamp(System.currentTimeMillis());
                // Optionally, you could re-throw or set a specific error for the client
            }

            // Get current user's session ID
            HttpSession session = request.getSession(false); // Get existing session, don't create if not present
            String currentUserSessionId = (session != null) ? session.getId() : null;

            List<SeatStatusDTO> seatStatusList = seatRepository.getCoachSeatsWithAvailabilityAndPrice(
                    tripId, coachId, legOriginStationId, legDestinationStationId, bookingTimestamp, isRoundTrip,
                    currentUserSessionId);

            objectMapper.writeValue(out, seatStatusList);

        } catch (NumberFormatException e) {
            errorMessage = "Invalid number format for one of the ID parameters.";
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.err.println("Error in GetCoachSeatsServlet (NumberFormat): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            errorMessage = e.getMessage();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            System.err.println("Error in GetCoachSeatsServlet (IllegalArgument): " + e.getMessage());
        } catch (SQLException e) {
            errorMessage = "Database error occurred while fetching seat information.";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("Error in GetCoachSeatsServlet (SQLException): " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage = "An unexpected error occurred.";
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.err.println("Error in GetCoachSeatsServlet (Exception): " + e.getMessage());
            e.printStackTrace();
        }

        if (errorMessage != null) {
            // Construct a simple error JSON object
            String errorJson = String.format("{\"error\": \"%s\"}",
                    errorMessage.replace("\\", "\\\\").replace("\"", "\\\""));
            response.getWriter().write(errorJson);
        }
    }
}