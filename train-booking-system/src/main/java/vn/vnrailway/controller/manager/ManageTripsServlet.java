package vn.vnrailway.controller.manager;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.dao.TrainTypeRepository; // Added
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.TripStationRepository; // Added
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.impl.TrainRepositoryImpl;
import vn.vnrailway.dao.impl.TrainTypeRepositoryImpl; // Added
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dao.impl.TripStationRepositoryImpl; // Added
import vn.vnrailway.dto.ManageTripViewDTO;
import vn.vnrailway.dto.RouteStationDetailDTO; // Added
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Train;
import vn.vnrailway.model.TrainType; // Added
import vn.vnrailway.model.Trip;
import vn.vnrailway.model.TripStation; // Added
import java.math.RoundingMode; // Added
import java.util.Comparator; // Added
import java.util.Optional; // Added

@WebServlet("/manageTrips")
public class ManageTripsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TripRepository tripRepository;
    private RouteRepository routeRepository;
    private TrainRepository trainRepository;
    private TripStationRepository tripStationRepository; // Added
    private TrainTypeRepository trainTypeRepository; // Added

    @Override
    public void init() throws ServletException {
        super.init();
        tripRepository = new TripRepositoryImpl();
        routeRepository = new RouteRepositoryImpl();
        trainRepository = new TrainRepositoryImpl();
        tripStationRepository = new TripStationRepositoryImpl(); // Added
        trainTypeRepository = new TrainTypeRepositoryImpl(); // Added
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "showAddForm":
                    showAddForm(request, response);
                    break;
                case "list":
                default:
                    listTrips(request, response);
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            request.setAttribute("errorMessage", "Database access error: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageTrips.jsp").forward(request, response);
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Train> allTrains = trainRepository.getAllTrains();
        List<Route> allRoutes = routeRepository.findAll();
        request.setAttribute("allTrains", allTrains);
        request.setAttribute("allRoutes", allRoutes);
        request.getRequestDispatcher("/WEB-INF/jsp/manager/addTrip.jsp").forward(request, response);
    }

    private void listTrips(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        String searchTerm = request.getParameter("searchTerm");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        if (sortField == null || sortField.isEmpty()) {
            sortField = "tripID";
            sortOrder = "ASC";
        } else if ("tripID".equalsIgnoreCase(sortField) && (sortOrder == null || sortOrder.isEmpty())) {
            sortOrder = "ASC";
        } else if (sortOrder == null || sortOrder.isEmpty()
                || (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder))) {
            sortOrder = "ASC";
        }

        if (searchTerm == null) {
            searchTerm = "";
        }

        List<ManageTripViewDTO> listTrips = tripRepository.findAllForManagerView(searchTerm, sortField, sortOrder);
        request.setAttribute("listTrips", listTrips);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("currentSortField", sortField);
        request.setAttribute("currentSortOrder", sortOrder);

        if (request.getSession().getAttribute("successMessage") != null) {
            request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
            request.getSession().removeAttribute("successMessage");
        }
        if (request.getSession().getAttribute("errorMessage") != null) {
            request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
            request.getSession().removeAttribute("errorMessage");
        }
        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageTrips.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("insertTrip".equals(action)) {
            try {
                int trainId = Integer.parseInt(request.getParameter("trainId"));
                int routeId = Integer.parseInt(request.getParameter("routeId"));
                LocalDateTime departureDateTime = LocalDateTime.parse(request.getParameter("departureDateTime"),
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME);

                if (departureDateTime.isBefore(LocalDateTime.now())) {
                    request.getSession().setAttribute("errorMessage",
                            "Không thể chọn ngày trong quá khứ. Vui lòng chọn một ngày trong tương lai.");
                    response.sendRedirect(request.getContextPath() + "/manageTrips?action=showAddForm");
                    return;
                }

                // LocalDateTime arrivalDateTime =
                // LocalDateTime.parse(request.getParameter("arrivalDateTime"),
                // DateTimeFormatter.ISO_LOCAL_DATE_TIME); // Removed
                boolean isHolidayTrip = Boolean.parseBoolean(request.getParameter("isHolidayTrip"));
                String tripStatus = request.getParameter("tripStatus");
                BigDecimal basePriceMultiplier = new BigDecimal(request.getParameter("basePriceMultiplier"));

                Trip newTrip = new Trip();
                newTrip.setTrainID(trainId);
                newTrip.setRouteID(routeId);
                newTrip.setDepartureDateTime(departureDateTime);

                // Calculate ArrivalDateTime for the Trip
                LocalDateTime calculatedArrivalDateTime = null;
                StringBuilder errorDetailsBuilder = new StringBuilder(
                        "Could not calculate trip arrival time. Issues found: ");
                boolean calculationPossible = true;

                Optional<Train> trainOpt = trainRepository.findById(trainId);
                if (trainOpt.isEmpty()) {
                    errorDetailsBuilder.append("Train with ID ").append(trainId).append(" not found. ");
                    calculationPossible = false;
                } else {
                    Train train = trainOpt.get();
                    Optional<TrainType> trainTypeOpt = trainTypeRepository.findById(train.getTrainTypeID());
                    if (trainTypeOpt.isEmpty()) {
                        errorDetailsBuilder.append("TrainType with ID ").append(train.getTrainTypeID())
                                .append(" for Train '").append(train.getTrainName()).append("' not found. ");
                        calculationPossible = false;
                    } else {
                        TrainType trainType = trainTypeOpt.get();
                        BigDecimal averageVelocity = trainType.getAverageVelocity();
                        if (averageVelocity == null || averageVelocity.compareTo(BigDecimal.ZERO) <= 0) {
                            errorDetailsBuilder.append("AverageVelocity for TrainType '")
                                    .append(trainType.getTypeName()).append("' is missing or not positive. ");
                            calculationPossible = false;
                        } else {
                            // Proceed if averageVelocity is valid
                            List<RouteStationDetailDTO> routeStations = routeRepository
                                    .findStationDetailsByRouteId(routeId);
                            if (routeStations == null || routeStations.isEmpty()) {
                                errorDetailsBuilder.append("No stations found for Route ID ").append(routeId)
                                        .append(". ");
                                calculationPossible = false;
                            } else {
                                RouteStationDetailDTO lastStation = routeStations.stream()
                                        .max(Comparator.comparing(RouteStationDetailDTO::getDistanceFromStart,
                                                Comparator.nullsLast(BigDecimal::compareTo)))
                                        .orElse(null);

                                if (lastStation == null || lastStation.getDistanceFromStart() == null) {
                                    errorDetailsBuilder.append(
                                            "Could not determine the last station or its distance for Route ID ")
                                            .append(routeId).append(". ");
                                    calculationPossible = false;
                                } else {
                                    BigDecimal distanceToLastStation = lastStation.getDistanceFromStart();
                                    BigDecimal travelTimeHoursDecimal = distanceToLastStation.divide(averageVelocity, 4,
                                            RoundingMode.HALF_UP);
                                    long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);
                                    calculatedArrivalDateTime = departureDateTime.plusSeconds(travelTimeSeconds);
                                }
                            }
                        }
                    }
                }

                if (calculatedArrivalDateTime == null) {
                    if (!calculationPossible) { // Append general message if specific errors were already added
                        throw new ServletException(errorDetailsBuilder.toString());
                    } else { // Should not happen if calculationPossible was true and no specific error was
                             // caught, but as a fallback
                        throw new ServletException(
                                "Could not calculate trip arrival time due to an unexpected issue. Please check data for Train ID "
                                        + trainId + " and Route ID " + routeId + ".");
                    }
                }
                newTrip.setArrivalDateTime(calculatedArrivalDateTime);
                newTrip.setHolidayTrip(isHolidayTrip);
                newTrip.setTripStatus(tripStatus);
                newTrip.setBasePriceMultiplier(basePriceMultiplier);

                Trip savedTrip = tripRepository.save(newTrip); // Save trip and get its ID

                if (savedTrip.getTripID() > 0) {
                    // Fetch stations for the route
                    List<RouteStationDetailDTO> routeStations = routeRepository
                            .findStationDetailsByRouteId(savedTrip.getRouteID());

                    RouteStationDetailDTO lastStation = routeStations.stream()
                            .max(Comparator.comparing(RouteStationDetailDTO::getSequenceNumber))
                            .orElse(null);

                    for (RouteStationDetailDTO rsDetail : routeStations) {
                        TripStation newTripStation = new TripStation();
                        newTripStation.setTripID(savedTrip.getTripID());
                        newTripStation.setStationID(rsDetail.getStationID());
                        newTripStation.setSequenceNumber(rsDetail.getSequenceNumber());

                        // Set scheduled times
                        if (rsDetail.getSequenceNumber() == 1) { // First station
                            newTripStation.setScheduledArrival(newTrip.getDepartureDateTime());
                            newTripStation.setScheduledDeparture(newTrip.getDepartureDateTime());
                        } else if (lastStation != null && rsDetail.getStationID() == lastStation.getStationID()) { // Last
                                                                                                                   // station
                            newTripStation.setScheduledArrival(null); // Or calculate based on distance
                            newTripStation.setScheduledDeparture(newTrip.getArrivalDateTime());
                        } else {
                            // For subsequent stations, these will be calculated/set later or remain null
                            // initially
                            newTripStation.setScheduledArrival(null);
                            newTripStation.setScheduledDeparture(null);
                        }

                        // Actual times will be null when creating
                        newTripStation.setActualArrival(null);
                        newTripStation.setActualDeparture(null);

                        tripStationRepository.save(newTripStation);
                    }
                    request.getSession().setAttribute("successMessage",
                            "Trip and default station schedule added successfully!");
                } else {
                    request.getSession().setAttribute("errorMessage",
                            "Trip added, but failed to get Trip ID to create station schedule.");
                }

            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error adding trip: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateHolidayStatus".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                boolean isHoliday = Boolean.parseBoolean(request.getParameter("isHolidayTrip"));

                tripRepository.updateTripHolidayStatus(tripId, isHoliday);
                String message = "Holiday status updated for Trip ID: " + tripId;

                if (!isHoliday) {
                    tripRepository.updateTripBasePriceMultiplier(tripId, new BigDecimal("1.0"));
                    message += " and Base Price Multiplier reset to 1.0.";
                }
                request.getSession().setAttribute("successMessage", message);
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error updating holiday status: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateBasePriceMultiplier".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                String multiplierStr = request.getParameter("basePriceMultiplier");
                boolean isCurrentlyHoliday = Boolean.parseBoolean(request.getParameter("isHolidayTripHidden"));

                if (!isCurrentlyHoliday) {
                    request.getSession().setAttribute("errorMessage",
                            "Base Price Multiplier can only be set if 'Ngày Lễ' is 'Có'.");
                } else if (multiplierStr == null || multiplierStr.trim().isEmpty()) {
                    tripRepository.updateTripBasePriceMultiplier(tripId, new BigDecimal("1.0"));
                    request.getSession().setAttribute("successMessage",
                            "Base Price Multiplier reset to 1.0 for Trip ID: " + tripId);
                } else {
                    BigDecimal multiplier = new BigDecimal(multiplierStr);
                    if (multiplier.compareTo(BigDecimal.ZERO) < 0) {
                        request.getSession().setAttribute("errorMessage", "Base Price Multiplier cannot be negative.");
                    } else {
                        tripRepository.updateTripBasePriceMultiplier(tripId, multiplier);
                        request.getSession().setAttribute("successMessage",
                                "Base Price Multiplier updated for Trip ID: " + tripId);
                    }
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errorMessage", "Invalid format for Trip ID or Multiplier.");
                e.printStackTrace();
            } catch (SQLException e) {
                request.getSession().setAttribute("errorMessage",
                        "Database error updating multiplier: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("updateTripStatus".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                String newStatus = request.getParameter("tripStatus");
                if (newStatus != null && !newStatus.trim().isEmpty()) {
                    tripRepository.updateTripStatus(tripId, newStatus);
                    request.getSession().setAttribute("successMessage", "Trip status updated for Trip ID: " + tripId);
                } else {
                    request.getSession().setAttribute("errorMessage", "Trip status cannot be empty.");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error updating trip status: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else if ("deleteTrip".equals(action)) {
            try {
                int tripId = Integer.parseInt(request.getParameter("tripId"));
                // Before deleting a trip, ensure related TripStations are deleted to avoid FK
                // constraints
                // The TripStationRepository might have a deleteByTripId method, or handle it
                // here.
                // For now, assuming TripRepository.deleteById handles or cascades, or DB
                // handles it.
                // If not, add: tripStationRepository.deleteByTripId(tripId);
                boolean deleted = tripRepository.deleteById(tripId);
                if (deleted) {
                    request.getSession().setAttribute("successMessage",
                            "Trip ID: " + tripId + " deleted successfully.");
                } else {
                    request.getSession().setAttribute("errorMessage", "Failed to delete Trip ID: " + tripId
                            + ". It might have associated records or does not exist.");
                }
            } catch (Exception e) {
                request.getSession().setAttribute("errorMessage", "Error deleting trip: " + e.getMessage());
                e.printStackTrace();
            }
            response.sendRedirect(request.getContextPath() + "/manageTrips");
        } else {
            doGet(request, response);
        }
    }
}
