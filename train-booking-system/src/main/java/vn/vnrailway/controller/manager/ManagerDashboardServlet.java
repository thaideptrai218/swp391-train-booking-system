package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.DashboardDAO;
import vn.vnrailway.dao.impl.DashboardDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@WebServlet("/managerDashboard")
public class ManagerDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ManagerDashboardServlet.class.getName());
    private DashboardDAO dashboardDAO;

    public void init() {
        dashboardDAO = new DashboardDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Lấy tham số bộ lọc
            String departureMonthsParam = request.getParameter("departureMonths");
            String arrivalMonthsParam = request.getParameter("arrivalMonths");

            int departureMonths = (departureMonthsParam != null && !departureMonthsParam.isEmpty()) ? Integer.parseInt(departureMonthsParam) : 12;
            int arrivalMonths = (arrivalMonthsParam != null && !arrivalMonthsParam.isEmpty()) ? Integer.parseInt(arrivalMonthsParam) : 12;

            // Dữ liệu thống kê chung
            int totalTrains = dashboardDAO.getTotalTrainsCount();
            double totalRefunds = dashboardDAO.getTotalRefunds();
            int pendingRefundsCount = dashboardDAO.getPendingRefundsCount();

            // Dữ liệu doanh thu theo từng giai đoạn
            double revenue1Month = dashboardDAO.getTotalRevenue(1);
            double revenue3Months = dashboardDAO.getTotalRevenue(3);
            double revenue6Months = dashboardDAO.getTotalRevenue(6);
            double revenue12Months = dashboardDAO.getTotalRevenue(12);
            double profit = revenue12Months - totalRefunds;

            // Đặt các thuộc tính cho JSP
            request.setAttribute("totalTrains", totalTrains);
            request.setAttribute("totalRefunds", totalRefunds);
            request.setAttribute("pendingRefundsCount", pendingRefundsCount);
            request.setAttribute("revenue1Month", revenue1Month);
            request.setAttribute("revenue3Months", revenue3Months);
            request.setAttribute("revenue6Months", revenue6Months);
            request.setAttribute("revenue12Months", revenue12Months);
            request.setAttribute("profit", profit);

            // Dữ liệu cho biểu đồ
            Map<String, Integer> popularDepartureStations = dashboardDAO.getPopularDepartureStations(departureMonths);
            Map<String, Integer> popularArrivalStations = dashboardDAO.getPopularArrivalStations(arrivalMonths);
            List<Map<String, Object>> popularTrips = dashboardDAO.getPopularTrips();

            Gson gson = new Gson();
            String popularDeparturesJson = gson.toJson(popularDepartureStations);
            String popularArrivalsJson = gson.toJson(popularArrivalStations);

            LOGGER.info("Popular Departures JSON: " + popularDeparturesJson);
            LOGGER.info("Popular Arrivals JSON: " + popularArrivalsJson);

            request.setAttribute("popularDepartureStations", popularDeparturesJson);
            request.setAttribute("popularArrivalStations", popularArrivalsJson);
            request.setAttribute("popularTrips", popularTrips);
            request.setAttribute("selectedDepartureMonths", departureMonths);
            request.setAttribute("selectedArrivalMonths", arrivalMonths);

        } catch (SQLException e) {
            throw new ServletException("Database error in ManagerDashboardServlet", e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/manager/manager-dashboard.jsp").forward(request, response);
    }
}