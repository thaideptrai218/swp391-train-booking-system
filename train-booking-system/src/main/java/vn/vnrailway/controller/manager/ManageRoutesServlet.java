package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList; // Added for stationsOrder list
import java.util.Map; // Added for creating response maps
import java.util.Optional; // Added for Optional type
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
import vn.vnrailway.dao.TripRepository; // Added
import vn.vnrailway.dao.impl.TripRepositoryImpl; // Added

@WebServlet("/manageRoutes")
public class ManageRoutesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RouteRepository routeRepository;
    private TripRepository tripRepository; // Added

    public ManageRoutesServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        this.routeRepository = new RouteRepositoryImpl();
        this.tripRepository = new TripRepositoryImpl(); // Added
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                case "editRoute": // This was for the modal on routeDetail, might need renaming or separate logic
                    showEditRouteForm(request, response); // This currently forwards to manageRoutes.jsp, but expects to
                                                          // be on routeDetail
                    break;
                case "showEditForm": // New action for showing edit form on manageRoutes.jsp
                    showEditFormOnManageRoutesPage(request, response);
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

    private void showEditFormOnManageRoutesPage(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            int routeId = Integer.parseInt(request.getParameter("routeId"));
            Route routeToEdit = routeRepository.findById(routeId)
                    .orElseThrow(() -> new ServletException(
                            "Route not found with ID: " + routeId + " for editing on manage routes page."));
            request.setAttribute("routeToEditOnPage", routeToEdit);

            // Fetch stations for this route to display start and end points
            List<RouteStationDetailDTO> stationsInRoute = routeRepository.findStationDetailsByRouteId(routeId);
            if (stationsInRoute != null && !stationsInRoute.isEmpty()) {
                // Assuming stations are ordered by sequence number
                request.setAttribute("departureStationIdForEdit", stationsInRoute.get(0).getStationID());
                request.setAttribute("arrivalStationIdForEdit",
                        stationsInRoute.get(stationsInRoute.size() - 1).getStationID());
                // Also pass names for display if needed, though dropdowns will handle it
                request.setAttribute("departureStationNameForEdit", stationsInRoute.get(0).getStationName());
                request.setAttribute("arrivalStationNameForEdit",
                        stationsInRoute.get(stationsInRoute.size() - 1).getStationName());
            } else {
                // This case should ideally not happen if routes are created with start/end
                // stations.
                // If it can, we might need a different way to determine original stations or
                // prevent editing.
                request.setAttribute("errorMessage", "Không thể xác định trạm đầu/cuối cho tuyến này để chỉnh sửa.");
            }
            // Ensure allStations is available for the dropdowns
            request.setAttribute("allStations", routeRepository.getAllStations());

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Route ID for editing.");
        } catch (ServletException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        // Still need to populate other lists for the page
        listRoutesAndStations(request, response);
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
            e.printStackTrace(); // Log the full stack trace
            String errorMessage;
            String rawSqlErrorMessage = e.getMessage();
            int sqlErrorCode = e.getErrorCode();

            // SQL Server error code for unique key violation is 2627.
            // SQL Server error code for primary key violation is 2601.
            if (sqlErrorCode == 2627 || sqlErrorCode == 2601) {
                if (rawSqlErrorMessage != null
                        && rawSqlErrorMessage.toLowerCase().contains("uq_routestations_routesequence")) {
                    // Specific message for sequence constraint, per user's request to map it to
                    // "station already in route"
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                } else if (rawSqlErrorMessage != null
                        && (rawSqlErrorMessage.toLowerCase().contains("uq_routestations_station")
                                || rawSqlErrorMessage.toLowerCase().contains("pk_routestations"))) {
                    // Specific message for station already in route (if these are the constraint
                    // names for RouteID, StationID)
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                } else {
                    // General unique key violation, use user's desired message
                    errorMessage = "Không thể thêm trạm đã có trong tuyến đường.";
                }
            } else {
                // Generic fallback for other SQL errors
                errorMessage = "Database operation failed: " + (rawSqlErrorMessage != null ? rawSqlErrorMessage
                        : "Unknown SQL error (" + sqlErrorCode + ")");
            }

            String routeIdForError = request.getParameter("routeId");
            if (routeIdForError == null) {
                routeIdForError = request.getParameter("routeIdForStation");
            }

            if (("updateRouteStation".equals(action) || "removeStationFromRoute".equals(action)
                    || "addStationToRoute".equals(action))
                    && routeIdForError != null && !routeIdForError.isEmpty()) {
                request.getSession().setAttribute("errorMessage", errorMessage);
                response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeIdForError);
            } else {
                request.setAttribute("errorMessage", errorMessage);
                try {
                    listRoutesAndStations(request, response);
                } catch (Exception ex) {
                    throw new ServletException("Error handling doPost after SQL exception", ex);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            String errorMessage = "Invalid number format: " + e.getMessage();
            String routeIdForError = request.getParameter("routeId");
            if (routeIdForError == null) {
                routeIdForError = request.getParameter("routeIdForStation");
            }

            if (("updateRouteStation".equals(action) || "removeStationFromRoute".equals(action)
                    || "addStationToRoute".equals(action))
                    && routeIdForError != null && !routeIdForError.isEmpty()) {
                request.getSession().setAttribute("errorMessage", errorMessage);
                response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeIdForError);
            } else {
                request.setAttribute("errorMessage", errorMessage);
                try {
                    listRoutesAndStations(request, response);
                } catch (Exception ex) {
                    throw new ServletException("Error handling doPost after NumberFormatException", ex);
                }
            }
        }
    }

    private void addRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int departureStationId = Integer.parseInt(request.getParameter("departureStationId"));
        int arrivalStationId = Integer.parseInt(request.getParameter("arrivalStationId"));
        String description = request.getParameter("description");

        if (departureStationId == arrivalStationId) {
            // Set an error message and redirect or forward back to the form
            request.setAttribute("errorMessage", "Điểm đi và điểm đến không được trùng nhau.");
            // Repopulate necessary data for the form
            listRoutesAndStations(request, response); // This forwards, so return after
            return;
        }

        // Fetch station names to create the route name
        // This requires a method in StationRepository or getting all stations and
        // filtering
        // For simplicity, assuming routeRepository can provide station details or we
        // fetch all and find
        List<Station> allStations = routeRepository.getAllStations(); // Already fetched in listRoutesAndStations
        String departureStationName = allStations.stream()
                .filter(s -> s.getStationID() == departureStationId)
                .findFirst()
                .map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Departure station not found"));
        String arrivalStationName = allStations.stream()
                .filter(s -> s.getStationID() == arrivalStationId)
                .findFirst()
                .map(Station::getStationName)
                .orElseThrow(() -> new ServletException("Arrival station not found"));

        String routeName = departureStationName + " - " + arrivalStationName;

        // Check if a route with this name already exists
        Optional<Route> existingRouteOpt = routeRepository.findByRouteName(routeName);
        if (existingRouteOpt.isPresent()) {
            request.setAttribute("errorMessage", "Tuyến đường '" + routeName + "' đã tồnS tại.");
            listRoutesAndStations(request, response);
            return;
        }

        Route newRoute = new Route(0, routeName, description); // ID will be auto-generated
        Route savedRoute = routeRepository.save(newRoute); // Get the saved route with its ID

        if (savedRoute.getRouteID() > 0) {
            // Add departure station (sequence 1)
            routeRepository.addStationToRoute(savedRoute.getRouteID(), departureStationId, 1, BigDecimal.ZERO, 0);
            // Add arrival station (sequence 2)
            // Distance for arrival station: for now, also 0, or could be a placeholder.
            // Actual distances are usually set when adding intermediate stations or
            // editing.
            routeRepository.addStationToRoute(savedRoute.getRouteID(), arrivalStationId, 2, BigDecimal.ZERO, 0); // Assuming
                                                                                                                 // distance
                                                                                                                 // 0
                                                                                                                 // for
                                                                                                                 // simplicity
                                                                                                                 // now
        } else {
            // Handle error: route not saved correctly
            throw new ServletException("Could not save the new route properly, ID not generated.");
        }

        request.getSession().setAttribute("successMessage", "Tuyến đường '" + routeName + "' đã được thêm thành công!");
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void updateRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        String description = request.getParameter("description");
        int newDepartureStationId = Integer.parseInt(request.getParameter("newDepartureStationId"));
        int newArrivalStationId = Integer.parseInt(request.getParameter("newArrivalStationId"));

        // Hidden fields for original station IDs might be needed if we only update if
        // changed.
        // int originalDepartureStationId =
        // Integer.parseInt(request.getParameter("originalDepartureStationId"));
        // int originalArrivalStationId =
        // Integer.parseInt(request.getParameter("originalArrivalStationId"));

        if (newDepartureStationId == newArrivalStationId) {
            request.getSession().setAttribute("errorMessage", "Điểm đi và điểm đến không được trùng nhau.");
            response.sendRedirect(request.getContextPath() + "/manageRoutes?action=showEditForm&routeId=" + routeId
                    + "#editRouteFormContainer");
            return;
        }

        Optional<Route> routeOpt = routeRepository.findById(routeId);
        if (!routeOpt.isPresent()) {
            request.getSession().setAttribute("errorMessage", "Không tìm thấy tuyến đường để cập nhật.");
            response.sendRedirect(request.getContextPath() + "/manageRoutes");
            return;
        }
        Route routeToUpdate = routeOpt.get();

        // Fetch station names for the new route name
        List<Station> allStations = routeRepository.getAllStations(); // Assuming this is efficient enough or cached
        String newDepartureStationName = allStations.stream()
                .filter(s -> s.getStationID() == newDepartureStationId)
                .findFirst().map(Station::getStationName)
                .orElseThrow(() -> new ServletException("New departure station not found"));
        String newArrivalStationName = allStations.stream()
                .filter(s -> s.getStationID() == newArrivalStationId)
                .findFirst().map(Station::getStationName)
                .orElseThrow(() -> new ServletException("New arrival station not found"));

        String newRouteName = newDepartureStationName + " - " + newArrivalStationName;

        // Check if the new route name conflicts with another existing route
        if (!newRouteName.equals(routeToUpdate.getRouteName())) {
            Optional<Route> existingRouteWithNewName = routeRepository.findByRouteName(newRouteName);
            if (existingRouteWithNewName.isPresent() && existingRouteWithNewName.get().getRouteID() != routeId) {
                request.getSession().setAttribute("errorMessage",
                        "Tuyến đường với tên '" + newRouteName + "' đã tồn tại.");
                response.sendRedirect(request.getContextPath() + "/manageRoutes?action=showEditForm&routeId=" + routeId
                        + "#editRouteFormContainer");
                return;
            }
        }

        routeToUpdate.setRouteName(newRouteName);
        routeToUpdate.setDescription(description);

        // --- Complex part: Updating RouteStations ---
        // This requires careful logic to remove old start/end stations if they changed,
        // add new start/end stations, and potentially re-sequence or adjust
        // intermediate stations.
        // This is a placeholder for the complex DB operations.
        // Ideally, this would be a single transactional method in a service/DAO layer.
        // For now, we'll update the route name/description and assume station updates
        // are more complex.

        // Simplified: Update Route table. Actual station replacement is complex.
        // To fully implement, we'd need:
        // 1. Get current first and last RouteStation entries.
        // 2. If newDepartureStationId is different from current first:
        // - Remove current first RouteStation.
        // - Add newDepartureStationId as RouteStation with sequence 1, distance 0.
        // 3. If newArrivalStationId is different from current last:
        // - Remove current last RouteStation.
        // - Add newArrivalStationId as RouteStation with new last sequence number.
        // Distance calculation is tricky.
        // This simplified version will only update the Route's name and description.
        // The actual stations in RouteStations table are NOT changed by this simplified
        // logic.
        // A full implementation would require more DAO methods and careful transaction
        // management.

        boolean routeUpdated = routeRepository.update(routeToUpdate); // Updates name and description

        if (routeUpdated) {
            // Placeholder for actual station replacement logic
            // Example: if (departureChanged) routeRepository.replaceFirstStation(routeId,
            // newDepartureStationId, ...);
            // Example: if (arrivalChanged) routeRepository.replaceLastStation(routeId,
            // newArrivalStationId, ...);
            request.getSession().setAttribute("successMessage",
                    "Thông tin tuyến đường '" + routeToUpdate.getRouteName() + "' đã được cập nhật (Mô tả và Tên). " +
                            "Thay đổi trạm đầu/cuối cần xử lý phức tạp hơn ở tầng DAO.");
        } else {
            request.getSession().setAttribute("errorMessage", "Không thể cập nhật thông tin tuyến đường.");
        }
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void deleteRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        String confirmedDelete = request.getParameter("confirmedDelete");

        int tripCount = tripRepository.countTripsByRouteId(routeId);

        if (tripCount > 0 && !"true".equals(confirmedDelete)) {
            // Trips exist, and not yet confirmed for deletion
            Optional<Route> routeOpt = routeRepository.findById(routeId);
            String routeName = routeOpt.isPresent() ? routeOpt.get().getRouteName() : "ID " + routeId;

            request.setAttribute("confirmDeleteRouteWithTrips", true);
            request.setAttribute("routeIdToDelete", routeId);
            request.setAttribute("routeNameToDelete", routeName);
            request.setAttribute("numberOfTrips", tripCount);

            // Forward back to the page to show confirmation
            listRoutesAndStations(request, response); // This re-populates lists and forwards
            return;
        }

        // Proceed with deletion (either no trips or confirmed)
        try {
            boolean deleted = routeRepository.deleteById(routeId); // This now also deletes trips via TripRepository
            if (deleted) {
                request.getSession().setAttribute("successMessage",
                        "Tuyến đường và các chuyến đi liên quan (nếu có) đã được xóa thành công.");
            } else {
                request.getSession().setAttribute("errorMessage",
                        "Không thể xóa tuyến đường. Có thể nó không tồn tại.");
            }
        } catch (SQLException e) {
            // The FK error should ideally be caught before this if trip deletion failed,
            // but this is a final catch-all.
            request.getSession().setAttribute("errorMessage",
                    "Lỗi cơ sở dữ liệu khi xóa tuyến đường: " + e.getMessage());
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/manageRoutes");
    }

    private void addStationToRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        int routeId = Integer.parseInt(request.getParameter("routeIdForStation")); // Name from JSP form
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        BigDecimal distanceFromStart = new BigDecimal(request.getParameter("distanceFromStart"));
        int defaultStopTime = Integer.parseInt(request.getParameter("defaultStopTime"));

        List<RouteStationDetailDTO> currentStations = routeRepository.findStationDetailsByRouteId(routeId);
        int targetSequenceNumber;

        if (currentStations.size() < 2) {
            // If 0 or 1 station, add the new station at the end
            targetSequenceNumber = routeRepository.getNextSequenceNumberForRoute(routeId);
            routeRepository.addStationToRoute(routeId, stationId, targetSequenceNumber, distanceFromStart,
                    defaultStopTime);
        } else {
            // If 2 or more stations, insert before the last one
            // The sequence number of the current last station will be the target for the
            // new station
            targetSequenceNumber = currentStations.get(currentStations.size() - 1).getSequenceNumber();

            // Increment sequence numbers for the current last station (and any beyond,
            // though there shouldn't be)
            routeRepository.incrementSequenceNumbersFrom(routeId, targetSequenceNumber);

            // Add the new station at the targetSequence (which is now free)
            routeRepository.addStationToRoute(routeId, stationId, targetSequenceNumber, distanceFromStart,
                    defaultStopTime);
        }

        request.getSession().setAttribute("successMessage", "Trạm đã được thêm vào tuyến thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
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
        // Redirect back to the route detail page
        request.getSession().setAttribute("successMessage", "Trạm trong tuyến đã được cập nhật thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
    }

    private void removeStationFromRoute(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int routeId = Integer.parseInt(request.getParameter("routeId"));
        int stationId = Integer.parseInt(request.getParameter("stationId"));
        routeRepository.removeStationFromRoute(routeId, stationId);
        // Redirect back to the route detail page
        request.getSession().setAttribute("successMessage", "Trạm đã được xóa khỏi tuyến thành công!");
        response.sendRedirect(request.getContextPath() + "/manager/routeDetail?routeId=" + routeId);
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
