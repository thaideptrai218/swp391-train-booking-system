package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.BestSellerLocationDTO;
import vn.vnrailway.dto.StationPopularityDTO;
import vn.vnrailway.dto.TripPopularityDTO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/managerDashboard")
public class ManagerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ManagerDashboardServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        TicketRepository ticketRepository = new TicketRepositoryImpl();
        TripRepository tripRepository = new TripRepositoryImpl();
        StationRepository stationRepository = new StationRepositoryImpl();
        int statsLimit = 5; // Configurable limit for top N statistics

        try {
            // Existing stats
            long totalTicketsSold = ticketRepository.getTotalTicketsSold();
            double totalRevenue = ticketRepository.getTotalRevenue();
            List<BestSellerLocationDTO> bestSellerLocations = tripRepository.getBestSellerLocations(statsLimit);

            request.setAttribute("totalTicketsSold", totalTicketsSold);
            request.setAttribute("totalRevenue", totalRevenue);
            request.setAttribute("bestSellerLocations", bestSellerLocations); // Keep for JSTL tables

            // New stats
            List<StationPopularityDTO> mostCommonOriginStations = stationRepository
                    .getMostCommonOriginStations(statsLimit);
            List<StationPopularityDTO> mostCommonDestinationStations = stationRepository
                    .getMostCommonDestinationStations(statsLimit);
            List<TripPopularityDTO> mostPopularTrips = tripRepository.getMostPopularTrips(statsLimit);

            request.setAttribute("mostCommonOriginStations", mostCommonOriginStations);
            request.setAttribute("mostCommonDestinationStations", mostCommonDestinationStations);
            request.setAttribute("mostPopularTrips", mostPopularTrips);

        } catch (SQLException e) {
            // Log the error and/or set an error message for the JSP
            e.printStackTrace(); // Consider a proper logging framework
            request.setAttribute("errorMessage", "Error fetching dashboard data: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/jsp/manager/manager-dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
