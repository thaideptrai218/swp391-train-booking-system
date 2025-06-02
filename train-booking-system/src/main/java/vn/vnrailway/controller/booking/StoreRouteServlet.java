package vn.vnrailway.controller.booking;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "StoreRouteServlet", urlPatterns = { "/storeRoute" })
public class StoreRouteServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String originIDStr = request.getParameter("originID");
        String destinationIDStr = request.getParameter("destinationID");

        HttpSession session = request.getSession();

        if (originIDStr != null && !originIDStr.isEmpty() &&
                destinationIDStr != null && !destinationIDStr.isEmpty()) {
            try {
                int originID = Integer.parseInt(originIDStr);
                int destinationID = Integer.parseInt(destinationIDStr);

                session.setAttribute("selectedOriginID", originID);
                session.setAttribute("selectedDestinationID", destinationID);

                // For now, redirect back to landing page.
                // Later, you might want to redirect to a booking page or another relevant page.
                response.sendRedirect(request.getContextPath() + "/");
            } catch (NumberFormatException e) {
                // Handle cases where IDs are not valid numbers
                session.setAttribute("routeSelectionError", "Invalid station IDs provided.");
                response.sendRedirect(request.getContextPath() + "/"); // Or an error page
            }
        } else {
            // Handle cases where IDs are missing
            session.setAttribute("routeSelectionError", "Origin or Destination ID is missing.");
            response.sendRedirect(request.getContextPath() + "/"); // Or an error page
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Stores selected origin and destination IDs in session";
    }
}
