package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.CancellationPolicyRepository;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.CancellationPolicyDAO;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.sql.SQLException;
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
            List<CancellationPolicy> policies = cancellationPolicyRepository.getAll();
            request.setAttribute("policies", policies);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error retrieving cancellation policies: " + e.getMessage());
            e.printStackTrace();
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageCancellationPolicies.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle form submissions for managing cancellation policies here
        // This could include adding, updating, or deleting policies
    }
}
