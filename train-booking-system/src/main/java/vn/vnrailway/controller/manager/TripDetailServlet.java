package vn.vnrailway.controller.manager;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.TripStationInfoDTO;
import java.sql.SQLException;
import java.util.List;
import java.math.BigDecimal; // Import BigDecimal
import vn.vnrailway.model.Trip;
import java.util.Optional;
import vn.vnrailway.dao.TripStationRepository;
import vn.vnrailway.dao.impl.TripStationRepositoryImpl;
import java.time.LocalDateTime;
import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.dao.TrainTypeRepository;
import vn.vnrailway.dao.impl.RouteRepositoryImpl;
import vn.vnrailway.dao.impl.TrainRepositoryImpl;
import vn.vnrailway.dao.impl.TrainTypeRepositoryImpl;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.model.Train;
import vn.vnrailway.model.TrainType;
import java.math.RoundingMode;
import java.util.Comparator;

@WebServlet("/tripDetail")
public class TripDetailServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TripRepository tripRepository;
    private TripStationRepository tripStationRepository;
    private RouteRepository routeRepository;
    private TrainRepository trainRepository;
    private TrainTypeRepository trainTypeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        tripRepository = new TripRepositoryImpl();
        tripStationRepository = new TripStationRepositoryImpl();
        routeRepository = new RouteRepositoryImpl();
        trainRepository = new TrainRepositoryImpl();
        trainTypeRepository = new TrainTypeRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tripIdStr = request.getParameter("tripId");

        if (tripIdStr != null && !tripIdStr.trim().isEmpty()) {
            try {
                int tripId = Integer.parseInt(tripIdStr);
                List<TripStationInfoDTO> tripStationDetails = tripRepository.findTripStationDetailsByTripId(tripId);

                if (tripStationDetails != null && !tripStationDetails.isEmpty()) {

                    TripStationInfoDTO firstStation = tripStationDetails.get(0);
                    if (firstStation.getScheduledDepartureDate() != null
                            && firstStation.getScheduledDepartureTime() != null) {
                        LocalDateTime previousDepartureDateTime = LocalDateTime
                                .of(firstStation.getScheduledDepartureDate(), firstStation.getScheduledDepartureTime());
                        BigDecimal previousEstimateTimeHours = firstStation.getEstimateTime() != null
                                ? firstStation.getEstimateTime()
                                : BigDecimal.ZERO;

                        for (int i = 1; i < tripStationDetails.size(); i++) {
                            TripStationInfoDTO currentStation = tripStationDetails.get(i);
                            BigDecimal currentEstimateTimeHours = currentStation.getEstimateTime();
                            Integer defaultStopTimeMinutes = currentStation.getDefaultStopTime();
                            if (defaultStopTimeMinutes == null) {
                                defaultStopTimeMinutes = 0;
                            }

                            if (currentEstimateTimeHours != null) {
                                BigDecimal travelTimeHoursDecimal = currentEstimateTimeHours
                                        .subtract(previousEstimateTimeHours);
                                long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);

                                LocalDateTime arrivalAtCurrentStation = previousDepartureDateTime
                                        .plusSeconds(travelTimeSeconds);
                                LocalDateTime departureFromCurrentStation = arrivalAtCurrentStation
                                        .plusMinutes(defaultStopTimeMinutes);

                                currentStation.setScheduledDepartureDate(departureFromCurrentStation.toLocalDate());
                                currentStation.setScheduledDepartureTime(departureFromCurrentStation.toLocalTime());

                                previousDepartureDateTime = departureFromCurrentStation;
                                previousEstimateTimeHours = currentEstimateTimeHours;
                            }
                        }
                    }

                    request.setAttribute("tripStationDetails", tripStationDetails);
                    request.setAttribute("tripIdForDetail", tripId);
                } else {
                    request.setAttribute("message", "No station details found for Trip ID: " + tripId);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Trip ID format: " + tripIdStr);
            } catch (SQLException e) {
                request.setAttribute("errorMessage", "Database error retrieving trip station details.");
                e.printStackTrace();
            }
        } else {
            request.setAttribute("errorMessage", "Trip ID was not provided.");
        }

        request.getRequestDispatcher("/WEB-INF/jsp/manager/tripDetail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tripIdStr = request.getParameter("tripId");
        String dateStr = request.getParameter("scheduledDepartureDate");
        String timeStr = request.getParameter("scheduledDepartureTime");

        try {
            int tripId = Integer.parseInt(tripIdStr);
            LocalDateTime newDepartureDateTime = LocalDateTime.of(java.time.LocalDate.parse(dateStr),
                    java.time.LocalTime.parse(timeStr));

            Optional<Trip> tripOpt = tripRepository.findById(tripId);
            if (tripOpt.isEmpty()) {
                throw new ServletException("Trip not found with ID: " + tripId);
            }
            Trip trip = tripOpt.get();
            trip.setDepartureDateTime(newDepartureDateTime);

            // Recalculate arrival time
            Optional<Train> trainOpt = trainRepository.findById(trip.getTrainID());
            if (trainOpt.isEmpty()) {
                throw new ServletException("Train not found with ID: " + trip.getTrainID());
            }
            Train train = trainOpt.get();
            Optional<TrainType> trainTypeOpt = trainTypeRepository.findById(train.getTrainTypeID());
            if (trainTypeOpt.isEmpty()) {
                throw new ServletException("TrainType not found with ID: " + train.getTrainTypeID());
            }
            TrainType trainType = trainTypeOpt.get();
            BigDecimal averageVelocity = trainType.getAverageVelocity();
            if (averageVelocity == null || averageVelocity.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ServletException("Average velocity for train type is not valid.");
            }
            List<RouteStationDetailDTO> routeStations = routeRepository.findStationDetailsByRouteId(trip.getRouteID());
            if (routeStations == null || routeStations.isEmpty()) {
                throw new ServletException("No stations found for route.");
            }
            RouteStationDetailDTO lastRouteStation = routeStations.stream()
                    .max(Comparator.comparing(RouteStationDetailDTO::getDistanceFromStart,
                            Comparator.nullsLast(BigDecimal::compareTo)))
                    .orElse(null);
            if (lastRouteStation == null || lastRouteStation.getDistanceFromStart() == null) {
                throw new ServletException("Could not determine the last station or its distance.");
            }
            BigDecimal distanceToLastStation = lastRouteStation.getDistanceFromStart();
            BigDecimal travelTimeHoursDecimal = distanceToLastStation.divide(averageVelocity, 4, RoundingMode.HALF_UP);
            long travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);
            LocalDateTime newArrivalDateTime = newDepartureDateTime.plusSeconds(travelTimeSeconds);
            trip.setArrivalDateTime(newArrivalDateTime);

            tripRepository.update(trip);

            // Update trip stations
            List<TripStationInfoDTO> stationDetails = tripRepository.findTripStationDetailsByTripId(tripId);
            if (stationDetails != null && !stationDetails.isEmpty()) {
                LocalDateTime previousDepartureDateTime = newDepartureDateTime;
                BigDecimal previousEstimateTimeHours = BigDecimal.ZERO;

                for (int i = 0; i < stationDetails.size(); i++) {
                    TripStationInfoDTO currentStation = stationDetails.get(i);
                    BigDecimal currentEstimateTimeHours = currentStation.getEstimateTime();
                    if (currentEstimateTimeHours == null) {
                        currentEstimateTimeHours = BigDecimal.ZERO;
                    }
                    Integer defaultStopTimeMinutes = currentStation.getDefaultStopTime();
                    if (defaultStopTimeMinutes == null) {
                        defaultStopTimeMinutes = 0;
                    }

                    LocalDateTime arrivalAtCurrentStation;
                    LocalDateTime departureFromCurrentStation;

                    if (i == 0) {
                        arrivalAtCurrentStation = newDepartureDateTime;
                        departureFromCurrentStation = newDepartureDateTime;
                    } else {
                        travelTimeHoursDecimal = currentEstimateTimeHours
                                .subtract(previousEstimateTimeHours);
                        travelTimeSeconds = (long) (travelTimeHoursDecimal.doubleValue() * 3600);
                        arrivalAtCurrentStation = previousDepartureDateTime.plusSeconds(travelTimeSeconds);
                        departureFromCurrentStation = arrivalAtCurrentStation.plusMinutes(defaultStopTimeMinutes);
                    }

                    if (i == stationDetails.size() - 1) {
                        departureFromCurrentStation = arrivalAtCurrentStation;
                    }

                    tripRepository.updateTripStationTimes(tripId, currentStation.getStationId(),
                            arrivalAtCurrentStation, departureFromCurrentStation);

                    previousDepartureDateTime = departureFromCurrentStation;
                    previousEstimateTimeHours = currentEstimateTimeHours;
                }

                request.getSession().setAttribute("successMessage", "Trip schedule updated successfully.");
            }

        } catch (Exception e) {
            request.getSession().setAttribute("errorMessage", "Error updating trip schedule: " + e.getMessage());
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/tripDetail?tripId=" + tripIdStr);
    }
}
