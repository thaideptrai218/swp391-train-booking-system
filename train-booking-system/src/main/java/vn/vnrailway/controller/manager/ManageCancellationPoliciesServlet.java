package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.CancellationPolicyRepository;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.CancellationPolicyDAO;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Added this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ManageCancellationPoliciesServlet", urlPatterns = { "/manageCancellationPolicies" })
public class ManageCancellationPoliciesServlet extends HttpServlet {
    private CancellationPolicyRepository cancellationPolicyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        cancellationPolicyRepository = new CancellationPolicyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get search parameter
            String searchQuery = request.getParameter("search");
            List<CancellationPolicy> policies;

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                searchQuery = normalizeString(searchQuery);

                // Validate search query length and characters
                if (searchQuery.length() > 100) {
                    request.setAttribute("errorMessage", "Từ khóa tìm kiếm không được vượt quá 100 ký tự");
                    request.setAttribute("policies", new ArrayList<>()); 
                    request.getRequestDispatcher("/WEB-INF/jsp/manager/manageCancellationPolicies.jsp").forward(request,
                            response);
                    return;
                }


                policies = cancellationPolicyRepository.searchByPolicyName(searchQuery);

                System.out.println("Search query: '" + searchQuery + "' - Found: " + policies.size() + " results");


                request.setAttribute("searchQuery", searchQuery);


                if (policies.isEmpty()) {
                    request.setAttribute("searchMessage",
                            "Không tìm thấy chính sách nào phù hợp với từ khóa: \"" + searchQuery + "\"");
                } else {
                    request.setAttribute("searchMessage",
                            "Tìm thấy " + policies.size() + " kết quả cho từ khóa: \"" + searchQuery + "\"");
                }

            } else {
                // Get all policies
                policies = cancellationPolicyRepository.getAll();
                System.out.println("Loading all policies - Found: " + policies.size() + " total");
            }

            request.setAttribute("policies", policies);
            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageCancellationPolicies.jsp").forward(request,
                    response);

        } catch (SQLException e) {
            request.setAttribute("policies", new ArrayList<>());
            request.setAttribute("errorMessage", "Lỗi truy xuất dữ liệu: " + e.getMessage());

            // Preserve search query even on error
            String searchQuery = request.getParameter("search");
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                request.setAttribute("searchQuery", searchQuery.trim());
            }

            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageCancellationPolicies.jsp").forward(request,
                    response);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle form submissions for managing cancellation policies here
        // This could include adding, updating, or deleting policies
    }

    private String normalizeString(String input) {
        if (input == null)
            return null;

        // Trim leading/trailing spaces and replace multiple spaces with single space
        return input.trim().replaceAll("\\s+", " ");
    }
}
