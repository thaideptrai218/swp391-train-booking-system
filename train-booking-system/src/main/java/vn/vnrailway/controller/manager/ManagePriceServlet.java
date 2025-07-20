package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.TrainTypeRepository;
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.impl.TrainTypeRepositoryImpl;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.TrainType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.PricingRuleRepository;
import vn.vnrailway.dao.impl.PricingRuleRepositoryImpl;
import vn.vnrailway.model.PricingRule;

@WebServlet("/managePrice")
public class ManagePriceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PricingRuleRepository pricingRuleRepository;
    private TrainTypeRepository trainTypeRepository;
    private RouteRepository routeRepository;

    public ManagePriceServlet() {
        super();
        this.pricingRuleRepository = new PricingRuleRepositoryImpl();
        this.trainTypeRepository = new TrainTypeRepositoryImpl();
        this.routeRepository = new RouteRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "insert":
                    insertPricingRule(request, response);
                    break;
                case "delete":
                    deletePricingRule(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updatePricingRule(request, response);
                    break;
                default: // list
                    listPricingRules(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listPricingRules(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        List<PricingRule> listPricingRules = pricingRuleRepository.findAll();
        request.setAttribute("listPricingRules", listPricingRules);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/managePrice.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<TrainType> trainTypes = trainTypeRepository.getAllTrainTypes();
            List<Route> routes = routeRepository.findAll();
            request.setAttribute("trainTypes", trainTypes);
            request.setAttribute("routes", routes);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error fetching data for new price rule form", e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/addPriceRule.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        PricingRule existingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new ServletException("Pricing Rule not found with ID: " + id));
        request.setAttribute("pricingRule", existingRule);
        try {
            List<TrainType> trainTypes = trainTypeRepository.getAllTrainTypes();
            List<Route> routes = routeRepository.findAll();
            request.setAttribute("trainTypes", trainTypes);
            request.setAttribute("routes", routes);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error fetching data for edit price rule form", e);
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/editPriceRule.jsp").forward(request, response);
    }

    private void insertPricingRule(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String ruleName = request.getParameter("ruleName");
        String description = request.getParameter("description");
        Integer trainTypeID = getNullableIntParameter(request, "trainTypeID");
        Integer routeID = getNullableIntParameter(request, "routeID");
        BigDecimal basePricePerKm = getNullableBigDecimalParameter(request, "basePricePerKm");
        boolean isForRoundTrip = "1".equals(request.getParameter("isForRoundTrip"));
        LocalDate applicableDateStart = getNullableLocalDateParameter(request, "applicableDateStart");
        LocalDate applicableDateEnd = getNullableLocalDateParameter(request, "applicableDateEnd");
        boolean isActive = "true".equals(request.getParameter("isActive"));

        if (ruleName == null || ruleName.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                basePricePerKm == null ||
                applicableDateStart == null || applicableDateEnd == null) {

            request.setAttribute("errorMessage", "Các trường không được để trống.");
            showNewForm(request, response);
            return;
        }

        if (applicableDateEnd.isBefore(applicableDateStart)) {
            request.setAttribute("errorMessage", "Ngày kết thúc không được trước ngày bắt đầu.");
            showNewForm(request, response);
            return;
        }

        if (basePricePerKm != null) {
            basePricePerKm = basePricePerKm.multiply(new BigDecimal(1000));
        }

        PricingRule newRule = new PricingRule(0, ruleName, description, trainTypeID, routeID, basePricePerKm,
                isForRoundTrip, applicableDateStart, applicableDateEnd, isActive);
        pricingRuleRepository.save(newRule);
        response.sendRedirect(request.getContextPath() + "/managePrice");
    }

    private void updatePricingRule(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int ruleID = Integer.parseInt(request.getParameter("ruleID"));
        String ruleName = request.getParameter("ruleName");
        String description = request.getParameter("description");
        Integer trainTypeID = getNullableIntParameter(request, "trainTypeID");
        Integer routeID = getNullableIntParameter(request, "routeID");
        BigDecimal basePricePerKm = getNullableBigDecimalParameter(request, "basePricePerKm");
        boolean isForRoundTrip = "1".equals(request.getParameter("isForRoundTrip"));
        LocalDate applicableDateStart = getNullableLocalDateParameter(request, "applicableDateStart");
        LocalDate applicableDateEnd = getNullableLocalDateParameter(request, "applicableDateEnd");
        boolean isActive = "true".equals(request.getParameter("isActive"));

        if (ruleName == null || ruleName.trim().isEmpty() ||
                description == null || description.trim().isEmpty() ||
                basePricePerKm == null ||
                applicableDateStart == null || applicableDateEnd == null) {

            request.setAttribute("errorMessage", "Các trường không được để trống.");
            showEditForm(request, response);
            return;
        }

        if (applicableDateEnd.isBefore(applicableDateStart)) {
            request.setAttribute("errorMessage", "Ngày kết thúc không được trước ngày bắt đầu.");
            showEditForm(request, response);
            return;
        }

        PricingRule existingRule = pricingRuleRepository.findById(ruleID)
                .orElseThrow(() -> new ServletException("Pricing Rule not found with ID: " + ruleID));

        if (basePricePerKm != null && (!basePricePerKm.equals(existingRule.getBasePricePerKm()))) {
            basePricePerKm = basePricePerKm.multiply(new BigDecimal(1000));
        } else {
            basePricePerKm = existingRule.getBasePricePerKm();
        }

        PricingRule ruleToUpdate = new PricingRule(ruleID, ruleName, description, trainTypeID, routeID,
                basePricePerKm, isForRoundTrip,
                applicableDateStart, applicableDateEnd, isActive);
        pricingRuleRepository.update(ruleToUpdate);
        response.sendRedirect(request.getContextPath() + "/managePrice");
    }

    private void deletePricingRule(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        pricingRuleRepository.deleteById(id);
        response.sendRedirect(request.getContextPath() + "/managePrice");
    }

    private Integer getNullableIntParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal getNullableBigDecimalParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getNullableLocalDateParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "updateRuleStatus":
                    updateRuleStatus(request, response);
                    break;
                default:
                    doGet(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void updateRuleStatus(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean isActive = Boolean.parseBoolean(request.getParameter("isActive"));
        pricingRuleRepository.updateRuleStatus(id, isActive);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
