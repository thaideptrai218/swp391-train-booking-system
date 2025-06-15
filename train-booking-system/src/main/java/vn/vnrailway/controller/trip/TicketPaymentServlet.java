package vn.vnrailway.controller.trip;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.PassengerTypeRepository; // Assuming this DAO exists
import vn.vnrailway.dao.impl.PassengerTypeRepositoryImpl;
import vn.vnrailway.model.PassengerType;     // Assuming this Model exists
import vn.vnrailway.utils.JsonUtils;         // Added JsonUtils import

@WebServlet("/ticketPayment") // Path matches the JSP's expectation for contextPath + /ticketPayment
public class TicketPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PassengerTypeRepository passengerTypeRepository;

    @Override
    public void init() throws ServletException {
        // super.init(); // Not strictly necessary if not overriding a specific behavior of parent init
        // Initialize your DAO here. This might be done via dependency injection
        // or a service locator in a real application.
        // For now, assuming a simple instantiation. If PassengerTypeRepository is an interface,
        // you'd instantiate a concrete implementation.
        try {
            passengerTypeRepository = new PassengerTypeRepositoryImpl(); 
        } catch (Exception e) {
            throw new ServletException("Failed to initialize PassengerTypeRepository", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<PassengerType> passengerTypes = passengerTypeRepository.findAll(); // Assuming findAll() is the correct method
            
            // Convert to JSON using JsonUtils (which uses Jackson).
            String passengerTypesJson = JsonUtils.toJsonString(passengerTypes);
            
            request.setAttribute("passengerTypesJson", passengerTypesJson);
            request.getRequestDispatcher("/WEB-INF/jsp/trip/ticketPayment.jsp").forward(request, response);

        } catch (Exception e) {
            // Log the error and potentially show an error page
            e.printStackTrace(); // For debugging
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu loại hành khách: " + e.getMessage());
            // You might want to forward to an error JSP page
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tải dữ liệu cần thiết cho trang thanh toán.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This is where the actual payment processing logic will go when the form is submitted.
        // For now, it's a placeholder.
        
        // Example: Read data from request (e.g., using request.getParameter or parsing JSON body)
        // Process payment with a payment gateway
        // Save booking, passenger, ticket details to database
        // Send confirmation email/SMS
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Placeholder response
        String jsonResponse = "{\"status\":\"success\", \"message\":\"Thanh toán đang được xử lý (placeholder).\", \"bookingCode\":\"BK12345\"}";
        // In a real scenario, bookingCode would be generated after successful DB operations.
        // And redirectUrl might be part of this response if client handles redirect.
        
        response.getWriter().write(jsonResponse);
    }
}
