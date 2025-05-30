package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.model.Trip;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TripRepositoryImpl implements TripRepository {

    private Trip mapResultSetToTrip(ResultSet rs) throws SQLException {
        Trip trip = new Trip();
        trip.setTripID(rs.getInt("TripID"));
        trip.setTrainID(rs.getInt("TrainID"));
        trip.setRouteID(rs.getInt("RouteID"));

        Timestamp departureTimestamp = rs.getTimestamp("DepartureDateTime");
        if (departureTimestamp != null) {
            trip.setDepartureDateTime(departureTimestamp.toLocalDateTime());
        }

        Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalDateTime");
        if (arrivalTimestamp != null) {
            trip.setArrivalDateTime(arrivalTimestamp.toLocalDateTime());
        }

        trip.setHolidayTrip(rs.getBoolean("IsHolidayTrip"));
        trip.setTripStatus(rs.getString("TripStatus"));
        trip.setBasePriceMultiplier(rs.getBigDecimal("BasePriceMultiplier"));
        return trip;
    }

    // Helper method to get Station Code by ID
    private String getStationCodeById(int stationId, Connection conn) throws SQLException {
        String sql = "SELECT StationCode FROM dbo.Stations WHERE StationID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("StationCode");
                } else {
                    // Consider if a more specific exception or error handling is needed
                    throw new SQLException("Station code not found for ID: " + stationId
                            + ". This station may not exist or may not have a code.");
                }
            }
        }
    }

    @Override
    public Optional<Trip> findById(int tripId) throws SQLException {
        String sql = "SELECT TripID, TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier FROM Trips WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrip(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Trip> findAll() throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT TripID, TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier FROM Trips";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trips.add(mapResultSetToTrip(rs));
            }
        }
        return trips;
    }

    @Override
    public List<Trip> findByTrainId(int trainId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT TripID, TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier FROM Trips WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trips.add(mapResultSetToTrip(rs));
                }
            }
        }
        return trips;
    }

    @Override
    public List<Trip> findByRouteId(int routeId) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT TripID, TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier FROM Trips WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    trips.add(mapResultSetToTrip(rs));
                }
            }
        }
        return trips;
    }

    @Override
    public Trip save(Trip trip) throws SQLException {
        String sql = "INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, trip.getTrainID());
            ps.setInt(2, trip.getRouteID());
            ps.setTimestamp(3, Timestamp.valueOf(trip.getDepartureDateTime()));
            ps.setTimestamp(4, Timestamp.valueOf(trip.getArrivalDateTime()));
            ps.setBoolean(5, trip.isHolidayTrip());
            ps.setString(6, trip.getTripStatus());
            ps.setBigDecimal(7, trip.getBasePriceMultiplier());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating trip failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trip.setTripID(generatedKeys.getInt(1));
                } else {
                    System.err.println(
                            "Creating trip succeeded, but no ID was obtained. TripID might not be auto-generated or configured to be returned.");
                }
            }
        }
        return trip;
    }

    @Override
    public boolean update(Trip trip) throws SQLException {
        String sql = "UPDATE Trips SET TrainID = ?, RouteID = ?, DepartureDateTime = ?, ArrivalDateTime = ?, IsHolidayTrip = ?, TripStatus = ?, BasePriceMultiplier = ? WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trip.getTrainID());
            ps.setInt(2, trip.getRouteID());
            ps.setTimestamp(3, Timestamp.valueOf(trip.getDepartureDateTime()));
            ps.setTimestamp(4, Timestamp.valueOf(trip.getArrivalDateTime()));
            ps.setBoolean(5, trip.isHolidayTrip());
            ps.setString(6, trip.getTripStatus());
            ps.setBigDecimal(7, trip.getBasePriceMultiplier());
            ps.setInt(8, trip.getTripID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int tripId) throws SQLException {
        String sql = "DELETE FROM Trips WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<TripSearchResultDTO> searchAvailableTrips(int originStationId, int destinationStationId,
            LocalDate departureDate) throws SQLException {
        List<TripSearchResultDTO> results = new ArrayList<>();
        String originStationCode;
        String destinationStationCode;

        String callSP = "{CALL dbo.SearchTrips(?, ?, ?, ?)}"; // Params: OriginCode, DestCode, DepartureDate, ReturnDate
                                                              // (NULL for one-way)

        try (Connection conn = DBContext.getConnection()) {
            // Fetch station codes using the provided IDs
            originStationCode = getStationCodeById(originStationId, conn);
            destinationStationCode = getStationCodeById(destinationStationId, conn);

            try (CallableStatement cs = conn.prepareCall(callSP)) {
                cs.setString(1, originStationCode);
                cs.setString(2, destinationStationCode);
                cs.setDate(3, Date.valueOf(departureDate));
                cs.setNull(4, Types.DATE); // @ReturnDate = NULL for one-way search

                try (ResultSet rs = cs.executeQuery()) {
                    while (rs.next()) {
                        TripSearchResultDTO dto = new TripSearchResultDTO();

                        dto.setLegType(rs.getString("LegType"));
                        dto.setTripId(rs.getInt("TripID"));
                        dto.setTrainName(rs.getString("TrainName"));
                        dto.setRouteName(rs.getString("RouteName"));
                        dto.setOriginStationName(rs.getString("OriginStation"));
                        dto.setTrainId(rs.getInt("trainId"));

                        Timestamp depTimestamp = rs.getTimestamp("DepartureTime");
                        if (depTimestamp != null) {
                            dto.setScheduledDeparture(depTimestamp.toLocalDateTime());
                        }

                        dto.setDestinationStationName(rs.getString("DestinationStation"));
                        Timestamp arrTimestamp = rs.getTimestamp("ArrivalTime");
                        if (arrTimestamp != null) {
                            dto.setScheduledArrival(arrTimestamp.toLocalDateTime());
                        }

                        dto.setDurationMinutes(rs.getInt("DurationMinutes"));

                        Timestamp overallDepTimestamp = rs.getTimestamp("TripOverallDeparture");
                        if (overallDepTimestamp != null) {
                            dto.setTripOverallDepartureTime(overallDepTimestamp.toLocalDateTime());
                        }

                        Timestamp overallArrTimestamp = rs.getTimestamp("TripOverallArrival");
                        if (overallArrTimestamp != null) {
                            dto.setTripOverallArrivalTime(overallArrTimestamp.toLocalDateTime());
                        }

                        // Populate fields that are known or can be derived
                        dto.setTripStatus("Scheduled"); // The SP filters for 'Scheduled' trips
                        dto.setOriginStationId(originStationId); // From method parameter
                        dto.setDestinationStationId(destinationStationId); // From method parameter

                        // trainId and routeId are not directly returned by the SP's final SELECT.
                        // They will remain as default (0 for int) unless the SP is modified or
                        // they are populated in a service layer.
                        // For now, we are not setting them from the ResultSet here.

                        results.add(dto);
                    }
                }
            }
        } catch (SQLException e) {
            // It's good practice to log the error or wrap it in a custom application
            // exception
            System.err.println("SQL Error in searchAvailableTrips: " + e.getMessage() +
                    " (OriginID: " + originStationId + ", DestID: " + destinationStationId +
                    ", Date: " + departureDate + ")");
            // e.printStackTrace(); // For detailed debugging during development
            throw e; // Re-throw to allow higher layers to handle it
        }
        return results;
    }

    // Main method for testing
    public static void main(String[] args) {
        TripRepository tripRepository = new TripRepositoryImpl();
        try {
            // Test findAll (basic trips, not search DTO)
            System.out.println("Testing findAll trips:");
            List<Trip> trips = tripRepository.findAll();
            if (trips.isEmpty()) {
                System.out.println("No trips found.");
            } else {
                trips.forEach(t -> System.out.println(t));
            }

            // Test findById (assuming trip with ID 1 exists)
            int testTripId = 1; // Ensure this ID exists
            System.out.println("\nTesting findById for trip ID: " + testTripId);
            Optional<Trip> tripOpt = tripRepository.findById(testTripId);
            tripOpt.ifPresentOrElse(
                    t -> System.out.println("Found trip: " + t),
                    () -> System.out.println("Trip with ID " + testTripId + " not found."));

            // Test searchAvailableTrips
            // IMPORTANT: You'll need valid station IDs and a date with expected trips in
            // your DB
            int originStationIdForTest = 1; // Example: Hanoi
            int destinationStationIdForTest = 5; // Example: Da Nang
            LocalDate departureDateForTest = LocalDate.now().plusDays(1); // Example: tomorrow

            System.out.println("\nTesting searchAvailableTrips from Station " + originStationIdForTest +
                    " to Station " + destinationStationIdForTest + " on " + departureDateForTest + ":");
            List<TripSearchResultDTO> searchResults = tripRepository.searchAvailableTrips(
                    originStationIdForTest, destinationStationIdForTest, departureDateForTest);

            if (searchResults.isEmpty()) {
                System.out.println("No available trips found for the criteria.");
            } else {
                searchResults.forEach(dto -> System.out.println(dto));
            }

            // Example of saving a new trip (uncomment and modify to test)
            /*
             * System.out.println("\nTesting save new trip:");
             * // Ensure TrainID 1 and RouteID 1 exist for this test
             * Trip newTrip = new Trip();
             * newTrip.setTrainID(1);
             * newTrip.setRouteID(1);
             * newTrip.setDepartureDateTime(LocalDateTime.now().plusDays(5).withHour(10).
             * withMinute(0));
             * newTrip.setArrivalDateTime(LocalDateTime.now().plusDays(5).withHour(18).
             * withMinute(0));
             * newTrip.setHolidayTrip(false);
             * newTrip.setTripStatus("Scheduled");
             * newTrip.setBasePriceMultiplier(new BigDecimal("1.0"));
             * 
             * Trip savedTrip = tripRepository.save(newTrip);
             * System.out.println("Saved trip: " + savedTrip);
             * 
             * if (savedTrip.getTripID() > 0) {
             * // Example of updating the trip
             * System.out.println("\nTesting update trip ID: " + savedTrip.getTripID());
             * savedTrip.setTripStatus("Delayed");
             * boolean updated = tripRepository.update(savedTrip);
             * System.out.println("Update successful: " + updated);
             * 
             * Optional<Trip> updatedTripOpt =
             * tripRepository.findById(savedTrip.getTripID());
             * updatedTripOpt.ifPresent(t -> System.out.println("Updated trip details: " +
             * t));
             * 
             * // Example of deleting the trip
             * System.out.println("\nTesting delete trip ID: " + savedTrip.getTripID());
             * boolean deleted = tripRepository.deleteById(savedTrip.getTripID());
             * System.out.println("Delete successful: " + deleted);
             * }
             */

        } catch (SQLException e) {
            System.err.println("Error testing TripRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
