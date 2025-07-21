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
            // Refund Request Status
            Map<String, Integer> refundRequestStatus = dashboardDAO.getRefundRequestStatus();
            request.setAttribute("refundRequestStatus", new Gson().toJson(refundRequestStatus));

            // Refund Requests Over Time
            String refundTrendDaysParam = request.getParameter("refundTrendDays");
            int refundTrendDays = (refundTrendDaysParam != null) ? Integer.parseInt(refundTrendDaysParam) : 7;
            Map<String, Integer> refundRequestsOverTime = dashboardDAO.getRefundRequestsOverTime(refundTrendDays);
            request.setAttribute("refundRequestsOverTime", new Gson().toJson(refundRequestsOverTime));
            request.setAttribute("selectedRefundTrendDays", refundTrendDays);

            // Feedback Status
            Map<String, Integer> feedbackStatus = dashboardDAO.getFeedbackStatus();
            request.setAttribute("feedbackStatus", new Gson().toJson(feedbackStatus));

            // Feedback by Topic
            Map<String, Integer> feedbackByTopic = dashboardDAO.getFeedbackByTopic();
            request.setAttribute("feedbackByTopic", new Gson().toJson(feedbackByTopic));

        } catch (SQLException e) {
            throw new ServletException("Database error in StaffDashboardServlet", e);
        }

        request.getRequestDispatcher("/WEB-INF/jsp/staff/dashboard.jsp").forward(request, response);
    }
}
