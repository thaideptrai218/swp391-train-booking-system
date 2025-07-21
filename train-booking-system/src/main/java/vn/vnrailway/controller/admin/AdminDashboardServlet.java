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

        DashboardDAO dashboardDAO = new vn.vnrailway.dao.impl.DashboardDAOImpl();

        // Lấy các thống kê
        int totalAccounts = dashboardDAO.getTotalUsers();
        int customerAccounts = dashboardDAO.getUserCountByRole("Customer");
        int guestAccounts = dashboardDAO.getUserCountByRole("Guest");

        // Set attributes
        request.setAttribute("totalAccounts", totalAccounts);
        request.setAttribute("customerAccounts", customerAccounts);
        request.setAttribute("guestAccounts", guestAccounts);

        // Lấy dữ liệu cho biểu đồ
        Map<String, Integer> genderDistribution = dashboardDAO.getGenderDistribution();
        Map<String, Integer> ageGroupDistribution = dashboardDAO.getAgeGroupDistribution();
        Map<String, Integer> activeStatusDistribution = dashboardDAO.getActiveStatusDistribution();
        
        String loginRatioDaysParam = request.getParameter("loginRatioDays");
        int loginRatioDays = (loginRatioDaysParam != null && !loginRatioDaysParam.isEmpty()) ? Integer.parseInt(loginRatioDaysParam) : 7; // Default to 7 days
        Map<String, Integer> loginRatio = dashboardDAO.getLoginRatio(loginRatioDays);
        
        String viewMode = request.getParameter("viewMode");
        if (viewMode == null || viewMode.isEmpty()) {
            viewMode = "overview"; // Default view
        }

        Map<String, Integer> trends;
        List<Integer> registrationYears = null;
        int selectedYear = 0;
        int selectedTrendDays = 0;

        if ("details".equals(viewMode)) {
            String trendDaysParam = request.getParameter("trendDays");
            selectedTrendDays = (trendDaysParam != null && !trendDaysParam.isEmpty()) ? Integer.parseInt(trendDaysParam) : 7;
            trends = dashboardDAO.getDailyUserRegistrations(selectedTrendDays);
        } else { // "overview"
            String yearParam = request.getParameter("year");
            selectedYear = (yearParam != null) ? Integer.parseInt(yearParam) : Calendar.getInstance().get(Calendar.YEAR);
            trends = dashboardDAO.getMonthlyUserRegistrations(selectedYear);
            registrationYears = dashboardDAO.getRegistrationYears();
        }

        // Chuyển Map thành JSON string
        Gson gson = new Gson();
        request.setAttribute("genderDistribution", gson.toJson(genderDistribution));
        request.setAttribute("ageGroupDistribution", gson.toJson(ageGroupDistribution));
        request.setAttribute("activeStatusDistribution", gson.toJson(activeStatusDistribution));
        request.setAttribute("loginRatio", gson.toJson(loginRatio));
        request.setAttribute("selectedLoginRatioDays", loginRatioDays);
        
        String trendDataJson = gson.toJson(trends);
        request.setAttribute("userChart", trendDataJson);
        request.setAttribute("viewMode", viewMode);
        if ("details".equals(viewMode)) {
            request.setAttribute("selectedTrendDays", selectedTrendDays);
        } else {
            request.setAttribute("selectedYear", selectedYear);
            request.setAttribute("registrationYears", registrationYears);
        }

        // Forward to dashboard
        request.getRequestDispatcher("/WEB-INF/jsp/admin/dashboard.jsp")
                .forward(request, response);
    }
}
