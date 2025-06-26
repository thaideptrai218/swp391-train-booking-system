package vn.vnrailway.controller.admin;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.DashboardDAO;
import vn.vnrailway.dao.BookingTrendDAO;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.model.User;

import java.util.Calendar;
import java.util.List;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            request.setAttribute("user", user);
        }

        DashboardDAO dashboardDAO = new DashboardDAO();

        // Lấy các thống kê
        int totalUsers = dashboardDAO.getTotalUsers();
        double avgUsersPerMonth = dashboardDAO.getAverageUsersPerMonth();
        double avgUsersPerYear = dashboardDAO.getAverageUsersPerYear();

        // Set attributes
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("avgUsersPerMonth", String.format("%.2f", avgUsersPerMonth));
        request.setAttribute("avgUsersPerYear", String.format("%.2f", avgUsersPerYear));

        // Lấy dữ liệu cho biểu đồ
        String yearParam = request.getParameter("year");
        int year = (yearParam != null) ? Integer.parseInt(yearParam) : Calendar.getInstance().get(Calendar.YEAR);

        Map<String, Integer> trends = dashboardDAO.getMonthlyUserRegistrations(year);
        List<Integer> registrationYears = dashboardDAO.getRegistrationYears();

        // Chuyển Map thành JSON string
        Gson gson = new Gson();
        String trendDataJson = gson.toJson(trends);
        request.setAttribute("userChart", trendDataJson);
        request.setAttribute("selectedYear", year);
        request.setAttribute("registrationYears", registrationYears);

        // Forward to dashboard
        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")
                .forward(request, response);
    }
}
