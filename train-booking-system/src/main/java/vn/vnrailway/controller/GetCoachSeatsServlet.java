package vn.vnrailway.controller;

import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.dao.impl.SeatRepositoryImpl;
import vn.vnrailway.dto.SeatStatusDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Not strictly needed for SeatStatusDTO as it is

@WebServlet(name = "GetCoachSeatsServlet", urlPatterns = { "/getCoachSeats" })
public class GetCoachSeatsServlet extends HttpServlet {

    private SeatRepository seatRepository;
    private ObjectMapper objectMapper; // Instantiate ObjectMapper once

    @Override
    public void init() throws ServletException {
        super.init();
        this.seatRepository = new SeatRepositoryImpl();
        this.objectMapper = new ObjectMapper();
        // objectMapper.registerModule(new JavaTimeModule()); // If needed for other
        // DTOs with Java 8 time
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

            if (tripIdParam == null || coachIdParam == null || legOriginStationIdParam == null
                    || legDestinationStationIdParam == null) {
                throw new IllegalArgumentException(
                        "Missing required parameters: tripId, coachId, legOriginStationId, legDestinationStationId");
            }

            int tripId = Integer.parseInt(tripIdParam);
            int coachId = Integer.parseInt(coachIdParam);
            int legOriginStationId = Integer.parseInt(legOriginStationIdParam);
            int legDestinationStationId = Integer.parseInt(legDestinationStationIdParam);

            List<SeatStatusDTO> seatStatusList = seatRepository.getCoachSeatsWithAvailability(tripId, coachId,
                    legOriginStationId, legDestinationStationId);

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

        // out.flush(); // Not strictly necessary as objectMapper.writeValue might
        // flush, and getWriter().write() might too.
        // If issues arise, can be re-added.
    }

    @Override
    public String getServletInfo() {
        return "Servlet to fetch coach seat availability for a given trip leg using Jackson for JSON.";
    }
}
