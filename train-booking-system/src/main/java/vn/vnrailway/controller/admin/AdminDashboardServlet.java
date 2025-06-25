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

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        DashboardDAO dashboardDAO = new DashboardDAO();

        // Lấy các thống kê
        int totalUsers = dashboardDAO.getTotalUsers();
        int totalTrains = dashboardDAO.getTotalTrains();
        int totalBookings = dashboardDAO.getTotalBookings();
        double totalRevenue = dashboardDAO.getTotalRevenue();

        // Format doanh thu theo tiền tệ VND
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedRevenue = currencyFormat.format(totalRevenue);

        // Set attributes
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalTrains", totalTrains);
        request.setAttribute("totalBookings", totalBookings);
        request.setAttribute("totalRevenue", formattedRevenue);

        // Lấy dữ liệu cho biểu đồ
        BookingTrendDAO trendDAO = new BookingTrendDAO();
        Map<String, Integer> trends = trendDAO.getBookingTrends();

        // Chuyển Map thành JSON string
        Gson gson = new Gson();
        String trendDataJson = gson.toJson(trends);
        request.setAttribute("bookingTrends", trendDataJson);

        // Forward to dashboard
        request.getRequestDispatcher("/WEB-INF/jsp/admin/home.jsp")
                .forward(request, response);
    }
}
