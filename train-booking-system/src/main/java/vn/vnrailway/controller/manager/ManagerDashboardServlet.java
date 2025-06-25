package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList; // Keep for general list usage
import java.util.List;
import java.util.stream.Collectors; // Added for JSON serialization
// Removed: SimpleDateFormat, Calendar, Date, Map, Collectors

import jakarta.servlet.ServletException;
import vn.vnrailway.dao.DashboardDAO; // Added
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.dao.impl.TicketRepositoryImpl;
import vn.vnrailway.dao.impl.TrainRepositoryImpl;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.BestSellerLocationDTO;
// import vn.vnrailway.dto.SalesByMonthDTO; // Reverted
// import vn.vnrailway.dto.SalesByYearDTO; // Reverted
import vn.vnrailway.dto.SalesByMonthYearDTO;
import vn.vnrailway.dto.SalesByWeekDTO; // Added for weekly sales
// import vn.vnrailway.dto.BookingTrendDTO; // Already marked as removed
import vn.vnrailway.dto.StationPopularityDTO;
import vn.vnrailway.dto.TripPopularityDTO;
// import vn.vnrailway.model.Ticket; // Already marked as removed
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
                TrainRepository trainRepository = new TrainRepositoryImpl();
                DashboardDAO dashboardDAO = new DashboardDAO(); // Added
                int statsLimit = 5;

                try {
                        // Existing stats
                        long totalTicketsSold = ticketRepository.getTotalTicketsSold(); // Consider using
                                                                                        // dashboardDAO.getTotalBookings()
                        double totalRevenue = dashboardDAO.getTotalRevenue(); // Using dashboardDAO for revenue
                        List<BestSellerLocationDTO> bestSellerLocations = tripRepository
                                        .getBestSellerLocations(statsLimit);

                        request.setAttribute("totalTicketsSold", totalTicketsSold);
                        request.setAttribute("totalRevenue", totalRevenue);
                        request.setAttribute("bestSellerLocations", bestSellerLocations);

                        long totalTrains = trainRepository.findAll().size();
                        request.setAttribute("totalTrains", totalTrains);

                        List<StationPopularityDTO> mostCommonOriginStations = stationRepository
                                        .getMostCommonOriginStations(statsLimit);
                        List<StationPopularityDTO> mostCommonDestinationStations = stationRepository
                                        .getMostCommonDestinationStations(statsLimit);
                        List<TripPopularityDTO> mostPopularTrips = tripRepository.getMostPopularTrips(statsLimit);

                        request.setAttribute("mostCommonOriginStations", mostCommonOriginStations);
                        request.setAttribute("mostCommonDestinationStations", mostCommonDestinationStations);
                        request.setAttribute("mostPopularTrips", mostPopularTrips);

                        // Fetch sales by month and year
                        List<SalesByMonthYearDTO> salesByMonthYearData = dashboardDAO.getSalesByMonthYear();
                        request.setAttribute("salesByMonthYearData", salesByMonthYearData); // Keep for JSP check

                        // Serialize to JSON for JavaScript
                        String salesByMonthYearJson = salesByMonthYearData.stream()
                                        .map(dto -> {
                                                // Ensure monthName is set if not already
                                                if (dto.getMonthName() == null || dto.getMonthName().isEmpty()) {
                                                        dto.setMonthNameFromMonth();
                                                }
                                                return String.format(
                                                                "{\"label\":\"%s %d\",\"totalSales\":%.2f}",
                                                                dto.getMonthName(), dto.getYear(), dto.getTotalSales());
                                        })
                                        .collect(Collectors.joining(",", "[", "]"));
                        request.setAttribute("salesByMonthYearJson", salesByMonthYearJson);

                        // Fetch sales by week
                        List<SalesByWeekDTO> salesByWeekData = dashboardDAO.getSalesByWeek();
                        request.setAttribute("salesByWeekData", salesByWeekData); // Keep for JSP check

                        // Serialize weekly sales to JSON for JavaScript
                        String salesByWeekJson = salesByWeekData.stream()
                                        .map(dto -> String.format(
                                                        "{\"year\":%d,\"week\":%d,\"weekOfYear\":\"%s\",\"totalSales\":%.2f}",
                                                        dto.getYear(), dto.getWeek(), dto.getWeekOfYear(),
                                                        dto.getTotalSales()))
                                        .collect(Collectors.joining(",", "[", "]"));
                        request.setAttribute("salesByWeekJson", salesByWeekJson);

                        // Removed: Fetch all tickets for the new table
                        // List<Ticket> allTickets = ticketRepository.findAll();
                        // request.setAttribute("allTickets", allTickets);

                } catch (SQLException e) {
                        e.printStackTrace();
                        request.setAttribute("errorMessage", "Error fetching dashboard data: " + e.getMessage());
                }

                request.getRequestDispatcher("/WEB-INF/jsp/manager/manager-dashboard.jsp").forward(request, response);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
                doGet(request, response);
        }
        // Removed aggregateDailyDataToWeekly helper method
}
