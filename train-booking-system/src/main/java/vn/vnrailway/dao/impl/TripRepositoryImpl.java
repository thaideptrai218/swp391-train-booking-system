package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dto.BestSellerLocationDTO;
import vn.vnrailway.dto.TripDTO;
import vn.vnrailway.dto.TripPopularityDTO;
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
            if (trip.getArrivalDateTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(trip.getArrivalDateTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP); // This should not happen if DB column is NOT NULL
            }
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
            if (trip.getArrivalDateTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(trip.getArrivalDateTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP); // This should not happen if DB column is NOT NULL
            }
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
                        dto.setTrainId(rs.getInt("TrainID")); // Mapping TrainID
                        dto.setTrainName(rs.getString("TrainName"));
                        dto.setRouteName(rs.getString("RouteName"));
                        dto.setOriginStationName(rs.getString("OriginStation"));
                        // dto.setTrainId(rs.getInt("trainId")); // Already mapped above as TrainID,
                        // ensure SP output name consistency

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
                        dto.setDistanceTraveledKm(rs.getDouble("DistanceTraveledKm")); // Mapping DistanceTraveledKm

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

                        // routeId is not directly returned by the SP's final SELECT.
                        // It will remain as default (0 for int) unless the SP is modified or
                        // populated in a service layer.
                        // For now, we are not setting it from the ResultSet here.

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

    @Override
    public List<BestSellerLocationDTO> getBestSellerLocations(int limit) throws SQLException {
        List<BestSellerLocationDTO> bestSellers = new ArrayList<>();
        // Assuming SQL Server for TOP ?, adjust for other DBs (e.g., LIMIT ? for
        // MySQL/PostgreSQL)
        String sql = "SELECT TOP (?) r.RouteName, COUNT(t.TicketID) as TicketsSold " +
                "FROM Tickets t " +
                "JOIN Trips tr ON t.TripID = tr.TripID " +
                "JOIN Routes r ON tr.RouteID = r.RouteID " +
                "WHERE t.TicketStatus NOT IN ('Cancelled', 'Void') " +
                "GROUP BY r.RouteName " +
                "ORDER BY TicketsSold DESC";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String routeName = rs.getString("RouteName");
                    long ticketsSold = rs.getLong("TicketsSold");
                    bestSellers.add(new BestSellerLocationDTO(routeName, ticketsSold));
                }
            }
        }
        return bestSellers;
    }

    @Override
    public List<TripPopularityDTO> getMostPopularTrips(int limit) throws SQLException {
        List<TripPopularityDTO> popularTrips = new ArrayList<>();
        String sql = "SELECT TOP (?) " +
                "tck.TripID, r.RouteName, trn.TrainName, tr.DepartureDateTime, COUNT(tck.TicketID) as BookingCount " +
                "FROM Tickets tck " +
                "JOIN Trips tr ON tck.TripID = tr.TripID " +
                "JOIN Routes r ON tr.RouteID = r.RouteID " +
                "JOIN Trains trn ON tr.TrainID = trn.TrainID " + // Assuming Trains table is named 'Trains' and has
                                                                 // 'TrainName'
                "WHERE tck.TicketStatus NOT IN ('Cancelled', 'Void') " +
                "GROUP BY tck.TripID, r.RouteName, trn.TrainName, tr.DepartureDateTime " +
                "ORDER BY BookingCount DESC";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int tripId = rs.getInt("TripID");
                    String routeName = rs.getString("RouteName");
                    String trainName = rs.getString("TrainName");
                    Timestamp departureTimestamp = rs.getTimestamp("DepartureDateTime");
                    LocalDateTime departureDateTime = (departureTimestamp != null)
                            ? departureTimestamp.toLocalDateTime()
                            : null;
                    long bookingCount = rs.getLong("BookingCount");
                    popularTrips
                            .add(new TripPopularityDTO(tripId, routeName, trainName, departureDateTime, bookingCount));
                }
            }
        }
        return popularTrips;
    }

    @Override
    public List<vn.vnrailway.dto.ManageTripViewDTO> findAllForManagerView(String searchTerm, String sortField,
            String sortOrder) throws SQLException {
        List<vn.vnrailway.dto.ManageTripViewDTO> managerTrips = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT ");
        sqlBuilder.append("t.TripID, r.RouteName, t.IsHolidayTrip, t.TripStatus, t.RouteID, t.BasePriceMultiplier ");
        sqlBuilder.append("FROM Trips t ");
        sqlBuilder.append("JOIN Routes r ON t.RouteID = r.RouteID ");

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sqlBuilder.append(
                    "WHERE (LOWER(r.RouteName) LIKE LOWER(?) OR LOWER(t.TripStatus) LIKE LOWER(?)) ");
            String wildCardSearchTerm = "%" + searchTerm.trim() + "%";
            params.add(wildCardSearchTerm);
            params.add(wildCardSearchTerm);
        }

        // Validate sortField to prevent SQL injection and map to actual DB columns
        String validSortField = "t.TripID"; // Default sort
        if ("routeName".equalsIgnoreCase(sortField))
            validSortField = "r.RouteName";
        else if ("isHolidayTrip".equalsIgnoreCase(sortField))
            validSortField = "t.IsHolidayTrip";
        else if ("tripStatus".equalsIgnoreCase(sortField))
            validSortField = "t.TripStatus";
        // tripID is already the default if no other match

        // Ensure sortOrder is either ASC or DESC
        String SOrder = "DESC"; // Default sort order
        if ("ASC".equalsIgnoreCase(sortOrder)) {
            SOrder = "ASC";
        }
        sqlBuilder.append("ORDER BY ").append(validSortField).append(" ").append(SOrder);

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vn.vnrailway.dto.ManageTripViewDTO dto = new vn.vnrailway.dto.ManageTripViewDTO();
                    dto.setTripID(rs.getInt("TripID"));
                    dto.setRouteName(rs.getString("RouteName"));
                    dto.setHolidayTrip(rs.getBoolean("IsHolidayTrip"));
                    dto.setTripStatus(rs.getString("TripStatus"));
                    dto.setRouteID(rs.getInt("RouteID"));
                    dto.setBasePriceMultiplier(rs.getBigDecimal("BasePriceMultiplier"));
                    // trainName, departureDateTime, arrivalDateTime, trainID are no longer part of
                    // the DTO's active fields
                    // or are not selected by the current query for this view.
                    managerTrips.add(dto);
                }
            } // Closing brace for try (ResultSet rs = ps.executeQuery())
        } catch (SQLException e) {
            System.err.println("Error fetching trips for manager view: " + e.getMessage());
            throw e; // Re-throw to allow higher layers to handle it
        }
        return managerTrips;
    }

    @Override
    public List<vn.vnrailway.dto.TripStationInfoDTO> findTripStationDetailsByTripId(int tripId) throws SQLException {
        List<vn.vnrailway.dto.TripStationInfoDTO> tripStationInfos = new ArrayList<>();
        // Updated SQL to include EstimateTime and DefaultStopTime
        String sql = "SELECT t.TripID, s.StationID, s.StationName, rs.DistanceFromStart, " +
                "ts.ScheduledDeparture, ty.AverageVelocity, " +
                "rs.DistanceFromStart / COALESCE(NULLIF(ty.AverageVelocity, 0), 100) AS EstimateTime, " + // Calculate
                                                                                                          // EstimateTime,
                                                                                                          // handle
                                                                                                          // division by
                                                                                                          // zero,
                                                                                                          // default to
                                                                                                          // 100
                "rs.DefaultStopTime " + // Added DefaultStopTime
                "FROM Trips t " +
                "JOIN Routes r ON t.RouteID = r.RouteID " +
                "JOIN TripStations ts ON ts.TripID = t.TripID " +
                "JOIN Stations s ON s.StationID = ts.StationID " +
                "JOIN RouteStations rs ON rs.StationID = ts.StationID AND t.RouteID = rs.RouteID " +
                "JOIN Trains tr ON tr.TrainID = t.TrainID " + // Join with Trains
                "JOIN TrainTypes ty ON ty.TrainTypeID = tr.TrainTypeID " + // Join with TrainTypes
                "WHERE t.TripID = ? " +
                "order by ts.SequenceNumber";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, tripId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vn.vnrailway.dto.TripStationInfoDTO dto = new vn.vnrailway.dto.TripStationInfoDTO();
                    dto.setTripID(rs.getInt("TripID"));
                    dto.setStationId(rs.getInt("StationID"));
                    dto.setStationName(rs.getString("StationName"));
                    dto.setDistanceFromStart(rs.getBigDecimal("DistanceFromStart"));

                    Timestamp departureTimestamp = rs.getTimestamp("ScheduledDeparture");
                    if (departureTimestamp != null) {
                        LocalDateTime ldt = departureTimestamp.toLocalDateTime();
                        dto.setScheduledDepartureDate(ldt.toLocalDate());
                        dto.setScheduledDepartureTime(ldt.toLocalTime());
                    }

                    // Populate new fields
                    BigDecimal estimateTime = rs.getBigDecimal("EstimateTime");
                    if (rs.wasNull()) { // Check if EstimateTime was NULL (e.g., due to division by zero)
                        dto.setEstimateTime(null); // Or BigDecimal.ZERO, depending on desired handling
                    } else {
                        dto.setEstimateTime(estimateTime);
                    }

                    int defaultStopTime = rs.getInt("DefaultStopTime");
                    if (rs.wasNull()) {
                        dto.setDefaultStopTime(null); // Or 0, if that's a more appropriate default
                    } else {
                        dto.setDefaultStopTime(defaultStopTime);
                    }

                    tripStationInfos.add(dto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching trip station details for TripID " + tripId + ": " + e.getMessage());
            throw e;
        }
        return tripStationInfos;
    }

    @Override
    public boolean updateTripStationScheduledDeparture(int tripId, int stationId, LocalDateTime newScheduledDeparture)
            throws SQLException {
        String sql = "UPDATE TripStations SET ScheduledDeparture = ? WHERE TripID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (newScheduledDeparture == null) {
                ps.setNull(1, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(1, Timestamp.valueOf(newScheduledDeparture));
            }
            ps.setInt(2, tripId);
            ps.setInt(3, stationId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating scheduled departure for TripID " + tripId + ", StationID " + stationId
                    + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateTripStationTimes(int tripId, int stationId, LocalDateTime newScheduledArrival,
            LocalDateTime newScheduledDeparture) throws SQLException {
        String sql = "UPDATE TripStations SET ScheduledArrival = ?, ScheduledDeparture = ? WHERE TripID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, newScheduledArrival != null ? Timestamp.valueOf(newScheduledArrival) : null);
            ps.setTimestamp(2, newScheduledDeparture != null ? Timestamp.valueOf(newScheduledDeparture) : null);
            ps.setInt(3, tripId);
            ps.setInt(4, stationId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println(
                    "Error updating times for TripID " + tripId + ", StationID " + stationId + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateTripHolidayStatus(int tripId, boolean isHoliday) throws SQLException {
        String sql = "UPDATE Trips SET IsHolidayTrip = ? WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isHoliday);
            ps.setInt(2, tripId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating holiday status for TripID " + tripId + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateTripStatus(int tripId, String newStatus) throws SQLException {
        String sql = "UPDATE Trips SET TripStatus = ? WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, tripId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating trip status for TripID " + tripId + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean updateTripBasePriceMultiplier(int tripId, BigDecimal newMultiplier) throws SQLException {
        String sql = "UPDATE Trips SET BasePriceMultiplier = ? WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newMultiplier);
            ps.setInt(2, tripId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating base price multiplier for TripID " + tripId + ": " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int countTripsByRouteId(int routeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Trips WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0; // Should not happen if COUNT(*) is used, but as a fallback
    }

    @Override
    public boolean deleteTripsByRouteId(int routeId) throws SQLException {
        // Before deleting trips, we might need to delete related records in other
        // tables
        // that have a foreign key to TripID, e.g., Tickets, TripStations.
        // This example assumes that cascading deletes are set up in the DB
        // or that these related entities are handled elsewhere or don't exist.
        // If not, you'd need to delete from child tables first.

        // For example, if Tickets and TripStations depend on Trips:
        // String deleteTicketsSql = "DELETE FROM Tickets WHERE TripID IN (SELECT TripID
        // FROM Trips WHERE RouteID = ?)";
        // String deleteTripStationsSql = "DELETE FROM TripStations WHERE TripID IN
        // (SELECT TripID FROM Trips WHERE RouteID = ?)";

        // For simplicity, this example only deletes from Trips.
        // In a real application, ensure all dependencies are handled.
        // A common approach is to first delete from tables that reference TripID.
        // For instance, delete tickets associated with these trips, then trip stations,
        // then the trips themselves.
        // This example will only delete from the Trips table.
        // WARNING: This does not handle dependent records in Tickets, TripStations etc.
        // You MUST ensure those are handled, e.g. by database CASCADE DELETE or by
        // explicit DELETE statements here.

        // Let's assume for now we need to delete Tickets and TripStations first.
        // This requires a transaction.
        Connection conn = null;
        boolean success = false;
        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            // Step 1: Delete from Tickets (assuming Tickets table has TripID FK)
            // This is a placeholder; actual ticket deletion logic might be more complex
            // (e.g., refunds, notifications)
            String deleteTicketsSql = "DELETE FROM Tickets WHERE TripID IN (SELECT TripID FROM Trips WHERE RouteID = ?)";
            try (PreparedStatement psTickets = conn.prepareStatement(deleteTicketsSql)) {
                psTickets.setInt(1, routeId);
                psTickets.executeUpdate(); // Number of affected rows not critical here unless for logging
            }

            // Step 2: Delete from TripStations (assuming TripStations table has TripID FK)
            String deleteTripStationsSql = "DELETE FROM TripStations WHERE TripID IN (SELECT TripID FROM Trips WHERE RouteID = ?)";
            try (PreparedStatement psTripStations = conn.prepareStatement(deleteTripStationsSql)) {
                psTripStations.setInt(1, routeId);
                psTripStations.executeUpdate();
            }

            // Step 3: Delete from Trips
            String deleteTripsSql = "DELETE FROM Trips WHERE RouteID = ?";
            try (PreparedStatement psTrips = conn.prepareStatement(deleteTripsSql)) {
                psTrips.setInt(1, routeId);
                int affectedRows = psTrips.executeUpdate();
                // success = affectedRows > 0; // Success if at least one trip was deleted, or
                // if no trips existed.
                // For this method, returning true if the operation completes without error is
                // fine.
            }

            conn.commit();
            success = true; // If all operations complete without error

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction in deleteTripsByRouteId: " + ex.getMessage());
                }
            }
            System.err.println("Error deleting trips by RouteID " + routeId + ": " + e.getMessage());
            throw e; // Re-throw the exception
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return success;
    }

    @Override
    public List<TripDTO> findAllAsDTO() throws SQLException {
        List<TripDTO> tripDTOs = new ArrayList<>();
        String sql = "SELECT t.*, r.RouteName, tr.TrainName " +
                "FROM Trips t " +
                "JOIN Routes r ON t.RouteID = r.RouteID " +
                "JOIN Trains tr ON t.TrainID = tr.TrainID";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Trip trip = mapResultSetToTrip(rs);
                String tripName = rs.getString("TrainName") + ": " + rs.getString("RouteName");
                tripDTOs.add(new TripDTO(trip, tripName));
            }
        }
        return tripDTOs;
    }
}
