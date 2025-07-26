package vn.vnrailway.controller.staff;

import com.google.gson.Gson;
import vn.vnrailway.dao.DashboardDAO;
import vn.vnrailway.dao.impl.DashboardDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(name = "StaffDashboardServlet", urlPatterns = {"/staff/dashboard"})
public class StaffDashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DashboardDAO dashboardDAO;

    public void init() {
        dashboardDAO = new DashboardDAOImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int pendingRefundsCount = dashboardDAO.getPendingRefundsCount();
            int pendingFeedbacksCount = dashboardDAO.getPendingFeedbacksCount();
            int unansweredUsersCount = dashboardDAO.getUnansweredUsersCount();

            request.setAttribute("pendingRefundsCount", pendingRefundsCount);
            request.setAttribute("pendingFeedbacksCount", pendingFeedbacksCount);
            request.setAttribute("unansweredUsersCount", unansweredUsersCount);

        } catch (SQLException e) {
            throw new ServletException("Database error in StaffDashboardServlet", e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/staff/dashboard.jsp").forward(request, response);
    }
}
