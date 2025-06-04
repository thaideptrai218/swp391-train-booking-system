package vn.vnrailway.controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.StaffDashboardDAO;
import java.io.IOException;

@WebServlet("/staff/dashboard")
public class StaffDashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        StaffDashboardDAO dashboardDAO = new StaffDashboardDAO();
        
        // Lấy các thống kê
        request.setAttribute("pendingBookings", dashboardDAO.getPendingBookings());
        request.setAttribute("todayDepartures", dashboardDAO.getTodayDepartures());
        request.setAttribute("activeTickets", dashboardDAO.getActiveTickets());
        request.setAttribute("recentRequests", dashboardDAO.getRecentRequests());
        
        // Forward to dashboard
        request.getRequestDispatcher("/WEB-INF/jsp/staff/dashboard.jsp")
               .forward(request, response);
    }
}
