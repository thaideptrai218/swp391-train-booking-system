package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.util.List;
import java.util.Optional; // For handling Optional<Route>
import java.util.stream.Collectors; // For filtering

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;
import vn.vnrailway.dto.RouteStationDetailDTO;

// Using concrete implementations
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.RouteRepository; // Keep interface for type

@WebServlet("/manager/routeDetail")
public class RouteDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RouteRepository routeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        routeRepository = new RouteRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String routeIdStr = request.getParameter("routeId");
        String action = request.getParameter("action");

        if (routeIdStr != null && !routeIdStr.isEmpty()) {
            try {
                int routeId = Integer.parseInt(routeIdStr);
                Optional<Route> currentRouteOpt = routeRepository.findById(routeId);

                if (currentRouteOpt.isPresent()) {
                    Route currentRoute = currentRouteOpt.get();
                    request.setAttribute("currentRoute", currentRoute);

                    // Fetch all route station details and filter for the current route
                    List<RouteStationDetailDTO> allRouteDetails = routeRepository.getAllRouteStationDetails();
                    List<RouteStationDetailDTO> routeDetailsForCurrentRoute = allRouteDetails.stream()
                            .filter(detail -> detail.getRouteID() == routeId)
                            .collect(Collectors.toList());
                    request.setAttribute("routeDetailsForCurrentRoute", routeDetailsForCurrentRoute);

                    // Fetch all stations for the "Add Station" modal dropdown
                    List<Station> allStations = routeRepository.getAllStations();
                    // request.setAttribute("allStations", allStations); // We'll set
                    // availableStationsToAdd instead

                    // Create a list of station IDs already in the current route
                    List<Integer> stationIdsInRoute = routeDetailsForCurrentRoute.stream()
                            .map(RouteStationDetailDTO::getStationID)
                            .collect(Collectors.toList());

                    // Filter allStations to get stations not yet in the current route
                    List<Station> availableStationsToAdd = allStations.stream()
                            .filter(station -> !stationIdsInRoute.contains(station.getStationID()))
                            .collect(Collectors.toList());
                    request.setAttribute("availableStationsToAdd", availableStationsToAdd);

                    if ("editRoute".equals(action)) {
                        request.setAttribute("routeToEdit", currentRoute);
                    }

                    String stationIdToEditStr = request.getParameter("stationId");
                    if ("editRouteStation".equals(action) && stationIdToEditStr != null) {
                        int stationIdToEdit = Integer.parseInt(stationIdToEditStr);
                        Optional<RouteStationDetailDTO> stationToEditOpt = routeDetailsForCurrentRoute.stream()
                                .filter(detail -> detail.getStationID() == stationIdToEdit)
                                .findFirst();
                        if (stationToEditOpt.isPresent()) {
                            request.setAttribute("routeStationToEdit", stationToEditOpt.get());
                        } else {
                            request.setAttribute("infoMessage", "Trạm cần sửa không tìm thấy trong tuyến này.");
                        }
                    }

                } else {
                    request.setAttribute("errorMessage", "Tuyến đường với ID " + routeId + " không tìm thấy.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID Tuyến đường không hợp lệ: " + routeIdStr);
                e.printStackTrace();
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi khi tải chi tiết tuyến đường: " + e.getMessage());
                e.printStackTrace(); // Important for debugging
            }
        } else if (action == null && routeIdStr == null) { // Only set error if no routeId is provided at all
            request.setAttribute("errorMessage", "Không có ID Tuyến đường nào được cung cấp.");
        }

        // Check for messages from session (e.g., after a redirect from POST)
        if (request.getSession().getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
            request.getSession().removeAttribute("successMessage");
        }
        if (request.getSession().getAttribute("errorMessage") != null) {
            request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/manager/routeDetail.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
