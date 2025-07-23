package vn.vnrailway.controller.trip;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Added for session management

import vn.vnrailway.dao.PassengerTypeRepository; // Assuming this DAO exists
import vn.vnrailway.dao.impl.PassengerTypeRepositoryImpl;
import vn.vnrailway.model.PassengerType; // Assuming this Model exists
import vn.vnrailway.model.User; // Added User import
import vn.vnrailway.utils.JsonUtils; // Added JsonUtils import

@WebServlet("/ticketPayment") // Path matches the JSP's expectation for contextPath + /ticketPayment
public class TicketPaymentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private PassengerTypeRepository passengerTypeRepository;

    @Override
    public void init() throws ServletException {
        try {
            passengerTypeRepository = new PassengerTypeRepositoryImpl();
        } catch (Exception e) {
            throw new ServletException("Failed to initialize PassengerTypeRepository", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<PassengerType> passengerTypes = passengerTypeRepository.findAll();
            String passengerTypesJson = JsonUtils.toJsonString(passengerTypes);
            request.setAttribute("passengerTypesJson", passengerTypesJson);

            // Retrieve last search criteria from session
            HttpSession session = request.getSession(false); // Don't create new session if not exists
            if (session != null) {
                request.setAttribute("lastQuery_originalStationId",
                        session.getAttribute("lastQuery_originalStationId"));
                request.setAttribute("lastQuery_destinationStationId",
                        session.getAttribute("lastQuery_destinationStationId"));
                request.setAttribute("lastQuery_departureDate", session.getAttribute("lastQuery_departureDate"));
                request.setAttribute("lastQuery_returnDate", session.getAttribute("lastQuery_returnDate"));
                request.setAttribute("lastQuery_originalStationName",
                        session.getAttribute("lastQuery_originalStationName"));
                request.setAttribute("lastQuery_destinationStationName",
                        session.getAttribute("lastQuery_destinationStationName"));
                
                // Get logged-in user information for auto-populating customer info
                User loggedInUser = (User) session.getAttribute("loggedInUser");
                if (loggedInUser != null) {
                    request.setAttribute("loggedInUser", loggedInUser);
                }
            }

            request.getRequestDispatcher("/WEB-INF/jsp/public/trip/ticketPayment.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu loại hành khách: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Không thể tải dữ liệu cần thiết cho trang thanh toán.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String jsonResponse = "{\"status\":\"success\", \"message\":\"Thanh toán đang được xử lý (placeholder).\", \"bookingCode\":\"BK12345\"}";
        response.getWriter().write(jsonResponse);
    }
}
