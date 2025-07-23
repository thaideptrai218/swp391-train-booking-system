package vn.vnrailway.controller.manager;

import vn.vnrailway.dao.CancellationPolicyRepository;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.CancellationPolicyDAO;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.Station;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Added this import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UpdatePolicyServlet", urlPatterns = { "/updatePolicy" })
public class UpdatePolicyServlet extends HttpServlet {
    private CancellationPolicyRepository cancellationPolicyRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        cancellationPolicyRepository = new CancellationPolicyDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        CancellationPolicy policy;
        try {
            policy = cancellationPolicyRepository.findById(id);
            request.setAttribute("policy", policy);
            request.getRequestDispatcher("/WEB-INF/jsp/manager/editPolicy.jsp").forward(request, response);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CancellationPolicy policy = new CancellationPolicy();
        policy.setPolicyID(Integer.parseInt(request.getParameter("policyID")));
        policy.setPolicyName(request.getParameter("policyName"));
        policy.setHoursBeforeDepartureMin(Integer.parseInt(request.getParameter("hoursMin")));
        String hoursMax = request.getParameter("hoursMax");
        policy.setHoursBeforeDepartureMax(hoursMax.isEmpty() ? null : Integer.parseInt(hoursMax));
        policy.setFeePercentage(new BigDecimal(request.getParameter("feePercentage")));
        policy.setFixedFeeAmount(new BigDecimal(request.getParameter("fixedFeeAmount")));
        policy.setRefundable("1".equals(request.getParameter("isRefundable")));
        policy.setDescription(request.getParameter("description"));
        policy.setActive("1".equals(request.getParameter("isActive")));
        policy.setEffectiveFromDate(Date.valueOf(request.getParameter("effectiveFromDate")));

        try {
            cancellationPolicyRepository.update(policy);
            response.sendRedirect(request.getContextPath() + "/manageCancellationPolicies");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
