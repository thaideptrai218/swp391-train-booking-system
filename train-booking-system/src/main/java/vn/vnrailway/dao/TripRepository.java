package vn.vnrailway.dao;

import vn.vnrailway.model.Trip;
import vn.vnrailway.dto.BestSellerLocationDTO;
import vn.vnrailway.dto.TripPopularityDTO;
import vn.vnrailway.dto.TripSearchResultDTO; // Assuming this DTO will be used for search results

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository {
        Optional<Trip> findById(int tripId) throws SQLException;

        List<Trip> findAll() throws SQLException; // Might be too broad, consider pagination or filtering

        List<Trip> findByTrainId(int trainId) throws SQLException;

        List<Trip> findByRouteId(int routeId) throws SQLException;

        // Basic CRUD
        Trip save(Trip trip) throws SQLException;

        boolean update(Trip trip) throws SQLException;

        boolean deleteById(int tripId) throws SQLException;

        // Advanced search method - this will be complex and likely involve joins
        // The DTO is used here because search results often combine data from multiple
        // tables
        List<TripSearchResultDTO> searchAvailableTrips(
                        int originStationId,
                        int destinationStationId,
                        LocalDate departureDate) throws SQLException;

        // You might also want methods to find trips by date range, status, etc.
        // List<Trip> findByDepartureDateRange(LocalDateTime start, LocalDateTime end)
        // throws SQLException;
        // List<Trip> findByStatus(String status) throws SQLException;

        List<BestSellerLocationDTO> getBestSellerLocations(int limit) throws SQLException;

        List<TripPopularityDTO> getMostPopularTrips(int limit) throws SQLException;

        List<vn.vnrailway.dto.ManageTripViewDTO> findAllForManagerView(String searchTerm, String sortField,
                        String sortOrder) throws SQLException;

        List<vn.vnrailway.dto.TripStationInfoDTO> findTripStationDetailsByTripId(int tripId) throws SQLException;

        boolean updateTripStationScheduledDeparture(int tripId, int stationId,
                        java.time.LocalDateTime newScheduledDeparture) throws SQLException;

        boolean updateTripStationTimes(int tripId, int stationId,
                        java.time.LocalDateTime newScheduledArrival,
                        java.time.LocalDateTime newScheduledDeparture) throws SQLException;

        boolean updateTripHolidayStatus(int tripId, boolean isHoliday) throws SQLException;

        boolean updateTripStatus(int tripId, String newStatus) throws SQLException;

        boolean updateTripBasePriceMultiplier(int tripId, java.math.BigDecimal newMultiplier) throws SQLException;

        // Method to count trips associated with a route
        int countTripsByRouteId(int routeId) throws SQLException;

        // Method to delete all trips associated with a route
        boolean deleteTripsByRouteId(int routeId) throws SQLException;
}
