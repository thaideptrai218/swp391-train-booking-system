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
                    System.err.println("Creating trip succeeded, but no ID was obtained. TripID might not be auto-generated or configured to be returned.");
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
    public List<TripSearchResultDTO> searchAvailableTrips(int originStationId, int destinationStationId, LocalDate departureDate) throws SQLException {
        List<TripSearchResultDTO> results = new ArrayList<>();
        String sql = "SELECT " +
                     "t.TripID, ts_orig.ScheduledDeparture, ts_dest.ScheduledArrival, t.TripStatus, t.IsHolidayTrip, " +
                     "tr.TrainID, tr.TrainName, ro.RouteID, ro.RouteName, " +
                     "s_orig.StationID AS OriginStationID, s_orig.StationName AS OriginStationName, s_orig.StationCode AS OriginStationCode, " +
                     "s_dest.StationID AS DestinationStationID, s_dest.StationName AS DestinationStationName, s_dest.StationCode AS DestinationStationCode, " +
                     "t.BasePriceMultiplier " +
                     "FROM Trips t " +
                     "JOIN Trains tr ON t.TrainID = tr.TrainID " +
                     "JOIN Routes ro ON t.RouteID = ro.RouteID " +
                     "JOIN TripStations ts_orig ON t.TripID = ts_orig.TripID " +
                     "JOIN Stations s_orig ON ts_orig.StationID = s_orig.StationID " +
                     "JOIN TripStations ts_dest ON t.TripID = ts_dest.TripID " +
                     "JOIN Stations s_dest ON ts_dest.StationID = s_dest.StationID " +
                     "WHERE ts_orig.StationID = ? " + // originStationId
                     "AND ts_dest.StationID = ? " +   // destinationStationId
                     "AND ts_orig.SequenceNumber < ts_dest.SequenceNumber " +
                     "AND CONVERT(date, ts_orig.ScheduledDeparture) = ? " + // departureDate
                     "AND t.TripStatus = 'Scheduled' " + // Assuming 'Scheduled' status for bookable trips
                     "ORDER BY ts_orig.ScheduledDeparture";

        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, originStationId);
            ps.setInt(2, destinationStationId);
            ps.setDate(3, Date.valueOf(departureDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TripSearchResultDTO dto = new TripSearchResultDTO();
                    dto.setTripID(rs.getInt("TripID"));

                    Timestamp depTimestamp = rs.getTimestamp("ScheduledDeparture");
                    if (depTimestamp != null) {
                        dto.setDepartureDateTime(depTimestamp.toLocalDateTime());
                        dto.setScheduledDepartureFromOrigin(depTimestamp.toLocalDateTime());
                    }
                    Timestamp arrTimestamp = rs.getTimestamp("ScheduledArrival");
                    if (arrTimestamp != null) {
                        dto.setArrivalDateTime(arrTimestamp.toLocalDateTime());
                        dto.setScheduledArrivalAtDestination(arrTimestamp.toLocalDateTime());
                    }

                    dto.setTripStatus(rs.getString("TripStatus"));
                    dto.setHolidayTrip(rs.getBoolean("IsHolidayTrip"));
                    dto.setTrainID(rs.getInt("TrainID"));
                    dto.setTrainName(rs.getString("TrainName"));
                    dto.setRouteID(rs.getInt("RouteID"));
                    dto.setRouteName(rs.getString("RouteName"));
                    dto.setOriginStationID(rs.getInt("OriginStationID"));
                    dto.setOriginStationName(rs.getString("OriginStationName"));
                    dto.setOriginStationCode(rs.getString("OriginStationCode"));
                    dto.setDestinationStationID(rs.getInt("DestinationStationID"));
                    dto.setDestinationStationName(rs.getString("DestinationStationName"));
                    dto.setDestinationStationCode(rs.getString("DestinationStationCode"));
                    
                    // Using BasePriceMultiplier as a placeholder for estimated price.
                    // Actual price calculation will likely be more complex and handled in the service layer.
                    dto.setEstimatedPrice(rs.getBigDecimal("BasePriceMultiplier")); 
                                        
                    results.add(dto);
                }
            }
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
                () -> System.out.println("Trip with ID " + testTripId + " not found.")
            );

            // Test searchAvailableTrips
            // IMPORTANT: You'll need valid station IDs and a date with expected trips in your DB
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
            System.out.println("\nTesting save new trip:");
            // Ensure TrainID 1 and RouteID 1 exist for this test
            Trip newTrip = new Trip();
            newTrip.setTrainID(1); 
            newTrip.setRouteID(1);
            newTrip.setDepartureDateTime(LocalDateTime.now().plusDays(5).withHour(10).withMinute(0));
            newTrip.setArrivalDateTime(LocalDateTime.now().plusDays(5).withHour(18).withMinute(0));
            newTrip.setHolidayTrip(false);
            newTrip.setTripStatus("Scheduled");
            newTrip.setBasePriceMultiplier(new BigDecimal("1.0"));
            
            Trip savedTrip = tripRepository.save(newTrip);
            System.out.println("Saved trip: " + savedTrip);

            if (savedTrip.getTripID() > 0) {
                // Example of updating the trip
                System.out.println("\nTesting update trip ID: " + savedTrip.getTripID());
                savedTrip.setTripStatus("Delayed");
                boolean updated = tripRepository.update(savedTrip);
                System.out.println("Update successful: " + updated);

                Optional<Trip> updatedTripOpt = tripRepository.findById(savedTrip.getTripID());
                updatedTripOpt.ifPresent(t -> System.out.println("Updated trip details: " + t));

                // Example of deleting the trip
                System.out.println("\nTesting delete trip ID: " + savedTrip.getTripID());
                boolean deleted = tripRepository.deleteById(savedTrip.getTripID());
                System.out.println("Delete successful: " + deleted);
            }
            */

        } catch (SQLException e) {
            System.err.println("Error testing TripRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
