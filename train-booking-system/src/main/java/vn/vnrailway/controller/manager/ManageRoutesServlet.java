package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; // Added for stationsOrder list
import java.util.Map; // Added for creating response maps
import com.fasterxml.jackson.core.type.TypeReference; // Added for Jackson
import com.fasterxml.jackson.databind.ObjectMapper; // Added for Jackson

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;

@WebServlet("/manageRoutes")
public class ManageRoutesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RouteRepository routeRepository;

    public ManageRoutesServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.routeRepository = new RouteRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "editRoute":
                    showEditRouteForm(request, response);
                    break;
                case "editRouteStation":
                    showEditRouteStationForm(request, response);
                    break;
                default: // "list" or any other
                    listRoutesAndStations(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error in ManageRoutesServlet doGet", e);
        }
    }

    private void listRoutesAndStations(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<RouteStationDetailDTO> routeDetails = routeRepository.getAllRouteStationDetails();
        List<Station> allStations = routeRepository.getAllStations();
        List<Route> allRoutes = routeRepository.findAll(); // For adding stations to existing routes

        request.setAttribute("routeDetails", routeDetails);
        request.setAttribute("allStations", allStations);
        request.setAttribute("allRoutes", allRoutes);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageRoutes.jsp").forward(request, response);
    }

    private void showEditRouteForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ServletException("Route not found with ID: " + routeId));
        request.setAttribute("routeToEdit", route);
        listRoutesAndStations(request, response); // To repopulate lists for the main page view
    }

    private void showEditRouteStationForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));

        // Find the specific RouteStationDetailDTO to edit
        RouteStationDetailDTO routeStationToEdit = routeRepository.getAllRouteStationDetails().stream()
                .filter(rsd -> rsd.getRouteID() == routeId && rsd.getStationID() == stationId)
                .findFirst()
                .orElseThrow(() -> new ServletException(
                        "RouteStation not found for Route ID: " + routeId + " and Station ID: " + stationId));

        request.setAttribute("routeStationToEdit", routeStationToEdit);
        listRoutesAndStations(request, response); // To repopulate lists for the main page view
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manageRoutes");
            return;
        }

        try {
            switch (action) {
                case "addRoute":
                    addRoute(request, response);
                    break;
                case "updateRoute":
                    updateRoute(request, response);
                    break;
                case "deleteRoute":
                    deleteRoute(request, response);
                    break;
                case "addStationToRoute":
                    addStationToRoute(request, response);
                    break;
                case "updateRouteStation":
                    updateRouteStation(request, response);
                    break;
                case "removeStationFromRoute":
                    removeStationFromRoute(request, response);
                    break;
                case "updateStationOrder":
                    updateStationOrder(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/manageRoutes");
                    break;
            }
        } catch (SQLException e) {
            // Consider setting an error message attribute and forwarding to an error page
            // or back to the form
            request.setAttribute("errorMessage", "Database operation failed: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace
            try {
                listRoutesAndStations(request, response); // Attempt to show the page again with an error
            } catch (SQLException | ServletException | IOException ex) {
                throw new ServletException("Error handling doPost after SQL exception", ex);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid number format: " + e.getMessage());
            try {
                listRoutesAndStations(request, response);
            } catch (SQLException | ServletException | IOException ex) {
                throw new ServletException("Error handling doPost after NumberFormatException", ex);
            }
        }
    }

    private void addRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String routeName = request.getParameter("routeName");
        String description = request.getParameter("description");
        Route newRoute = new Route(0, routeName, description); // ID will be auto-generated
        routeRepository.save(newRoute);
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void updateRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        String routeName = request.getParameter("routeName");
        String description = request.getParameter("description");
        Route routeToUpdate = new Route(routeId, routeName, description);
        routeRepository.update(routeToUpdate);
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void deleteRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        routeRepository.deleteById(routeId);
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void addStationToRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeIdForStation")); // Name from JSP form
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        // Sequence number is now determined automatically
        int sequenceNumber = routeRepository.getNextSequenceNumberForRoute(routeId);
        BigDecimal distanceFromStart = new BigDecimal(request.getParameter("distanceFromStart"));
        int defaultStopTime = Integer.parseInt(request.getParameter("defaultStopTime"));

        routeRepository.addStationToRoute(routeId, stationId, sequenceNumber, distanceFromStart, defaultStopTime);
        response.sendRedirect(request.getContextPath() + "/manageRoutes?action=list&highlightRoute=" + routeId);
    }

    private void updateRouteStation(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException { // Added ServletException
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int originalStationId = Integer.parseInt(request.getParameter("originalStationId")); // Key to find the record
        int newStationId = Integer.parseInt(request.getParameter("stationId")); // Potentially new station if allowed
        // Sequence number is no longer submitted from the form, fetch existing
        RouteStationDetailDTO existingRouteStation = routeRepository.getAllRouteStationDetails().stream()
                .filter(rsd -> rsd.getRouteID() == routeId && rsd.getStationID() == originalStationId)
                .findFirst()
                .orElseThrow(() -> new ServletException(
                        "Original RouteStation not found for Route ID: " + routeId + " and Station ID: "
                                + originalStationId));
        int sequenceNumber = existingRouteStation.getSequenceNumber(); // Use existing sequence

        BigDecimal distanceFromStart = new BigDecimal(request.getParameter("distanceFromStart"));
        int defaultStopTime = Integer.parseInt(request.getParameter("defaultStopTime"));

        // If stationId can change, it's more complex: delete old, add new.
        // Assuming for now stationId within a route sequence doesn't change, only its
        // attributes.
        // If stationId itself can be changed for an existing entry, the PK of
        // RouteStations (RouteID, StationID) is an issue.
        // For simplicity, if newStationId is different from originalStationId, we might
        // need to remove old and add new.
        // Current DAO updateRouteStation updates based on routeId and stationId.
        // If stationId is part of what's being edited (i.e. changing which station is
        // at a sequence):
        if (originalStationId != newStationId) {
            // This implies changing the station at a certain point in the route.
            // This is more like removing the old station and adding a new one.
            // The current updateRouteStation in DAO updates based on (routeId, stationId)
            // as PK.
            // So, if stationId changes, we must remove the old and add the new.
            // The sequence number for the new station will be the same as the old one's.
            routeRepository.removeStationFromRoute(routeId, originalStationId);
            routeRepository.addStationToRoute(routeId, newStationId, sequenceNumber, distanceFromStart, // sequenceNumber
                                                                                                        // is from
                                                                                                        // existing
                    defaultStopTime);
        } else {
            // Station ID hasn't changed, just update its other attributes (distance,
            // stopTime)
            // Sequence number remains the same as it's managed by drag-drop.
            routeRepository.updateRouteStation(routeId, originalStationId, sequenceNumber, distanceFromStart,
                    defaultStopTime);
        }
        response.sendRedirect(request.getContextPath() + "/manageRoutes?action=list&highlightRoute=" + routeId);
    }

    private void removeStationFromRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        routeRepository.removeStationFromRoute(routeId, stationId);
        response.sendRedirect(request.getContextPath() + "/manageRoutes?action=list&highlightRoute=" + routeId);
    }

    private void updateStationOrder(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse;

        try {
            int routeId = Integer.parseInt(request.getParameter("routeId"));
            String stationsOrderJson = request.getParameter("stationsOrder");

            if (stationsOrderJson == null || stationsOrderJson.isEmpty()) {
                throw new IllegalArgumentException("Stations order data is missing.");
            }

            // Define a type for Jackson to deserialize the list of station orders
            TypeReference<List<StationOrderUpdateDTO>> typeRef = new TypeReference<List<StationOrderUpdateDTO>>() {
            };
            List<StationOrderUpdateDTO> stationsOrder = objectMapper.readValue(stationsOrderJson, typeRef);

            if (stationsOrder.isEmpty()) {
                // It's possible to drag all stations out, then back in. If the list is empty,
                // it might mean all stations were removed from the route, which is usually
                // handled by removeStationFromRoute.
                // Or it could be an erroneous client-side state.
                // For now, we'll assume an empty list means no changes or an invalid state for
                // *reordering*.
                // If the intention is to remove all, that should be a different action.
                // We could also choose to do nothing and return success if the list is empty.
                // Let's treat it as a successful "no-op" for now or a sign that the client
                // handled it.
                // However, the DAO method will likely expect items.
                // For safety, let's consider it an issue if it's empty but an update was
                // triggered.
                // Or, more practically, the DAO should handle an empty list gracefully if it
                // means "no stations in route now".
                // Given the current DAO structure, it's likely expecting to update existing
                // route_station entries.
                // Let's assume the DAO's `updateRouteStationOrder` will handle this.
            }

            // Call repository method to update the order
            // This method needs to be created in RouteRepository and its implementation
            boolean success = routeRepository.updateRouteStationOrder(routeId, stationsOrder);

            if (success) {
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("success", true, "message", "Thứ tự trạm đã được cập nhật thành công."));
            } else {
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("success", false, "message",
                                "Không thể cập nhật thứ tự trạm trong cơ sở dữ liệu. Một hoặc nhiều bản ghi không thành công."));
                // Consider SC_INTERNAL_SERVER_ERROR if the DAO signals a definite failure
                response.setStatus(HttpServletResponse.SC_OK); // Still an OK HTTP response, but logical failure
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi định dạng ID tuyến đường: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Dữ liệu không hợp lệ: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (IOException e) { // Catch Jackson's JsonProcessingException
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi xử lý dữ liệu yêu cầu (JSON): " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            e.printStackTrace();
            jsonResponse = objectMapper.writeValueAsString(
                    Map.of("success", false, "message", "Lỗi cơ sở dữ liệu: " + e.getMessage()));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        response.getWriter().write(jsonResponse);
    }

    // Inner DTO class for parsing station order updates
    // Needs to be static if it's an inner class used by Jackson, or a separate
    // public class.
    // For simplicity here, making it a static inner class.
    public static class StationOrderUpdateDTO {
        private int stationId;
        private int sequenceNumber;

        // Default constructor for Jackson
        public StationOrderUpdateDTO() {
        }

        public StationOrderUpdateDTO(int stationId, int sequenceNumber) {
            this.stationId = stationId;
            this.sequenceNumber = sequenceNumber;
        }

        public int getStationId() {
            return stationId;
        }

        public void setStationId(int stationId) {
            this.stationId = stationId;
        }

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }
    }
}
