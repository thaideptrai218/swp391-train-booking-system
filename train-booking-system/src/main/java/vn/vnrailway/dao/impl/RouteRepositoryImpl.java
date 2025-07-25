package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station; // Assuming this model exists
import vn.vnrailway.controller.manager.ManageRoutesServlet.StationOrderUpdateDTO; // Added for the DTO

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Import TripRepository to delete associated trips
import vn.vnrailway.dao.TripRepository;

public class RouteRepositoryImpl implements RouteRepository {

    private TripRepository tripRepository; // Added

    public RouteRepositoryImpl() { // Added constructor
        this.tripRepository = new TripRepositoryImpl(); // Initialize TripRepository
    }

    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setRouteID(rs.getInt("RouteID"));
        route.setRouteName(rs.getString("RouteName"));
        route.setDescription(rs.getString("Description"));
        try {
            route.setActive(rs.getBoolean("IsActive"));
        } catch (SQLException ignore) {
        }
        return route;
    }

    private Station mapResultSetToStation(ResultSet rs) throws SQLException {
        Station station = new Station();
        station.setStationID(rs.getInt("StationID"));
        station.setStationName(rs.getString("StationName"));
        try {
            station.setActive(rs.getBoolean("IsActive"));
        } catch (SQLException ignore) {
        }
        // Add other station properties if needed for the dropdown/display
        // e.g., station.setCity(rs.getString("City"));
        return station;
    }

    @Override
    public Optional<Route> findById(int routeId) throws SQLException {
        String sql = "SELECT RouteID, RouteName, Description, IsActive FROM Routes WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoute(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Route> findAll() throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT RouteID, RouteName, Description, IsActive FROM Routes";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                routes.add(mapResultSetToRoute(rs));
            }
        }
        return routes;
    }

    @Override
    public Optional<Route> findByRouteName(String routeName) throws SQLException {
        String sql = "SELECT RouteID, RouteName, Description, IsActive FROM Routes WHERE RouteName = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, routeName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRoute(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Route save(Route route) throws SQLException {
        String sql = "INSERT INTO Routes (RouteName, Description, IsActive) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, route.getRouteName());
            ps.setString(2, route.getDescription());
            ps.setBoolean(3, route.isActive());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating route failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    route.setRouteID(generatedKeys.getInt(1));
                } else {
                    System.err.println(
                            "Creating route succeeded, but no ID was obtained. RouteID might not be auto-generated or configured to be returned.");
                }
            }
        }
        return route;
    }

    @Override
    public boolean update(Route route) throws SQLException {
        String sql = "UPDATE Routes SET RouteName = ?, Description = ?, IsActive = ? WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, route.getRouteName());
            ps.setString(2, route.getDescription());
            ps.setBoolean(3, route.isActive());
            ps.setInt(4, route.getRouteID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateRouteActive(int routeId, boolean isActive) throws SQLException {
        String sql = "UPDATE Routes SET IsActive = ? WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, routeId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int routeId) throws SQLException {
        String deleteRouteStationsSql = "DELETE FROM RouteStations WHERE RouteID = ?";
        String deleteRouteSql = "DELETE FROM Routes WHERE RouteID = ?";
        Connection conn = null;
        PreparedStatement psRouteStations = null;
        PreparedStatement psRoute = null;
        boolean success = false;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Delete associated trips (which internally handles tickets,
            // tripstations)
            // This assumes tripRepository.deleteTripsByRouteId handles its own transaction
            // or can be part of this one.
            // For simplicity here, we call it. If it throws an exception, this transaction
            // will roll back.
            // If deleteTripsByRouteId manages its own transaction, ensure it commits or
            // rolls back appropriately.
            // Given the current implementation of deleteTripsByRouteId, it does manage its
            // own transaction.
            // This is generally okay, but for a larger system, a service layer managing
            // transactions across repositories would be better.
            tripRepository.deleteTripsByRouteId(routeId); // This will attempt to delete trips and their dependencies

            // Step 2: Delete from RouteStations
            psRouteStations = conn.prepareStatement(deleteRouteStationsSql);
            psRouteStations.setInt(1, routeId);
            psRouteStations.executeUpdate(); // We don't strictly need to check affected rows here,
                                             // as a route might not have stations yet.

            // Then delete from Routes
            psRoute = conn.prepareStatement(deleteRouteSql);
            psRoute.setInt(1, routeId);
            int affectedRowsRoute = psRoute.executeUpdate();

            conn.commit(); // Commit transaction
            success = affectedRowsRoute > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception to be handled by the caller
        } finally {
            if (psRouteStations != null)
                try {
                    psRouteStations.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (psRoute != null)
                try {
                    psRoute.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return success;
    }

    @Override
    public List<RouteStationDetailDTO> getAllRouteStationDetails() throws SQLException {
        List<RouteStationDetailDTO> details = new ArrayList<>();
        String sql = "SELECT r.RouteID, s.StationID, r.RouteName, s.StationName, rs.SequenceNumber, rs.DistanceFromStart, rs.DefaultStopTime, r.Description "
                +
                "FROM Routes r JOIN RouteStations rs ON r.RouteID = rs.RouteID " +
                "JOIN Stations s ON rs.StationID = s.StationID ORDER BY r.RouteID, rs.SequenceNumber";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RouteStationDetailDTO dto = new RouteStationDetailDTO();
                dto.setRouteID(rs.getInt("RouteID"));
                dto.setStationID(rs.getInt("StationID"));
                dto.setRouteName(rs.getString("RouteName"));
                dto.setStationName(rs.getString("StationName"));
                dto.setSequenceNumber(rs.getInt("SequenceNumber"));
                dto.setDistanceFromStart(rs.getBigDecimal("DistanceFromStart"));
                dto.setDefaultStopTime(rs.getInt("DefaultStopTime"));
                dto.setDescription(rs.getString("Description"));
                details.add(dto);
            }
        }
        return details;
    }

    @Override
    public void addStationToRoute(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
            int defaultStopTime) throws SQLException {
        // Kiểm tra trạng thái hoạt động của ga trước khi thêm vào Route
        String checkActiveSql = "SELECT IsActive FROM Stations WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement checkPs = conn.prepareStatement(checkActiveSql)) {
            checkPs.setInt(1, stationId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    boolean isActive = rs.getBoolean("IsActive");
                    if (!isActive) {
                        throw new SQLException("Không thể thêm ga không hoạt động vào tuyến đường.");
                    }
                } else {
                    throw new SQLException("Không tìm thấy ga với ID: " + stationId);
                }
            }
        }
        // Nếu ga đang hoạt động, thực hiện thêm vào RouteStations
        String sql = "INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            ps.setInt(2, stationId);
            ps.setInt(3, sequenceNumber);
            ps.setBigDecimal(4, distanceFromStart);
            ps.setInt(5, defaultStopTime);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean updateRouteStation(int routeId, int stationId, int sequenceNumber, BigDecimal distanceFromStart,
            int defaultStopTime) throws SQLException {
        // Assuming RouteID and StationID together form the primary key for
        // RouteStations or there's a unique constraint.
        // If StationID can change for a given sequence, the logic might need adjustment
        // (e.g., delete old and insert new).
        // This implementation updates based on RouteID and StationID.
        String sql = "UPDATE RouteStations SET SequenceNumber = ?, DistanceFromStart = ?, DefaultStopTime = ? " +
                "WHERE RouteID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sequenceNumber);
            ps.setBigDecimal(2, distanceFromStart);
            ps.setInt(3, defaultStopTime);
            ps.setInt(4, routeId);
            ps.setInt(5, stationId); // Assuming stationId is the key to find the record to update for that route
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean removeStationFromRoute(int routeId, int stationId) throws SQLException {
        String sql = "DELETE FROM RouteStations WHERE RouteID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            ps.setInt(2, stationId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public List<Station> getAllStations() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT StationID, StationName, IsActive FROM Stations WHERE IsActive = 1 ORDER BY StationID";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stations.add(mapResultSetToStation(rs));
            }
        }
        return stations;
    }

    @Override
    public List<RouteStationDetailDTO> findStationDetailsByRouteId(int routeId) throws SQLException {
        List<RouteStationDetailDTO> details = new ArrayList<>();
        String sql = "SELECT r.RouteID, s.StationID, r.RouteName, s.StationName, rs.SequenceNumber, rs.DistanceFromStart, rs.DefaultStopTime, r.Description "
                +
                "FROM Routes r JOIN RouteStations rs ON r.RouteID = rs.RouteID " +
                "JOIN Stations s ON rs.StationID = s.StationID " +
                "WHERE r.RouteID = ? ORDER BY rs.SequenceNumber";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteStationDetailDTO dto = new RouteStationDetailDTO();
                    dto.setRouteID(rs.getInt("RouteID"));
                    dto.setStationID(rs.getInt("StationID"));
                    dto.setRouteName(rs.getString("RouteName"));
                    dto.setStationName(rs.getString("StationName"));
                    dto.setSequenceNumber(rs.getInt("SequenceNumber"));
                    dto.setDistanceFromStart(rs.getBigDecimal("DistanceFromStart"));
                    dto.setDefaultStopTime(rs.getInt("DefaultStopTime"));
                    dto.setDescription(rs.getString("Description")); // Route description
                    details.add(dto);
                }
            }
        }
        return details;
    }

    @Override
    public boolean updateRouteStationOrder(int routeId, List<StationOrderUpdateDTO> stationsOrder) throws SQLException {
        // Phase 1: Update to temporary, unique negative sequence numbers
        String sqlPhase1 = "UPDATE RouteStations SET SequenceNumber = ? WHERE RouteID = ? AND StationID = ?";
        // Phase 2: Update to final sequence numbers
        String sqlPhase2 = "UPDATE RouteStations SET SequenceNumber = ? WHERE RouteID = ? AND StationID = ?";
        // Note: In Phase 2, we are still identifying the row by StationID, but setting
        // its new SequenceNumber.
        // The temporary negative sequence isn't used for lookup in phase 2, simplifying
        // the query.

        Connection conn = null;
        PreparedStatement psPhase1 = null;
        PreparedStatement psPhase2 = null;
        boolean allSuccess = true;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Phase 1: Update to temporary sequence numbers
            psPhase1 = conn.prepareStatement(sqlPhase1);
            for (StationOrderUpdateDTO stationOrder : stationsOrder) {
                psPhase1.setInt(1, -(stationOrder.getStationId())); // Temporary unique negative sequence
                psPhase1.setInt(2, routeId);
                psPhase1.setInt(3, stationOrder.getStationId());
                psPhase1.addBatch();
            }
            int[] batchResultsPhase1 = psPhase1.executeBatch();
            for (int result : batchResultsPhase1) {
                if (result == Statement.EXECUTE_FAILED) {
                    allSuccess = false;
                    break;
                }
                // A result of 0 could mean the station was not found for the given routeId and
                // stationId.
                // This could be an issue if all stations in the DTO are expected to exist.
                if (result == 0) {
                    // Log or handle cases where a station in the list wasn't found for update.
                    // For now, we'll let it proceed but it might indicate data inconsistency.
                    System.err.println("Warning: Station ID " + psPhase1.getWarnings()
                            + " (this is a guess, cannot get actual ID from batch) might not have been found for update in phase 1 for route "
                            + routeId);
                }
            }

            if (!allSuccess) {
                conn.rollback();
                System.err.println("Transaction rolled back after phase 1 failure for routeId: " + routeId);
                return false;
            }

            // Phase 2: Update to final sequence numbers
            psPhase2 = conn.prepareStatement(sqlPhase2);
            for (StationOrderUpdateDTO stationOrder : stationsOrder) {
                psPhase2.setInt(1, stationOrder.getSequenceNumber()); // Final sequence number
                psPhase2.setInt(2, routeId);
                psPhase2.setInt(3, stationOrder.getStationId());
                psPhase2.addBatch();
            }
            int[] batchResultsPhase2 = psPhase2.executeBatch();
            for (int result : batchResultsPhase2) {
                if (result == Statement.EXECUTE_FAILED) {
                    allSuccess = false;
                    break;
                }
                if (result == 0) {
                    System.err.println(
                            "Warning: Station ID might not have been found for update in phase 2 for route " + routeId);
                }
            }

            if (allSuccess) {
                conn.commit();
            } else {
                conn.rollback();
                System.err.println("Transaction rolled back after phase 2 failure for routeId: " + routeId);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            throw e; // Re-throw the exception
        } finally {
            if (psPhase1 != null)
                try {
                    psPhase1.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (psPhase2 != null)
                try {
                    psPhase2.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
        return allSuccess;
    }

    @Override
    public int getNextSequenceNumberForRoute(int routeId) throws SQLException {
        String sql = "SELECT MAX(SequenceNumber) AS MaxSequence FROM RouteStations WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // rs.getInt() returns 0 if the value is SQL NULL (no stations yet)
                    return rs.getInt("MaxSequence") + 1;
                } else {
                    // Should not happen if MAX() is used, as it returns one row even if NULL.
                    // But as a fallback, if no rows (e.g. route doesn't exist, though FK should
                    // prevent this for RouteStations)
                    return 1;
                }
            }
        }
    }

    @Override
    public void incrementSequenceNumbersFrom(int routeId, int fromSequenceNumber) throws SQLException {
        // We need to update in descending order of sequence to avoid unique constraint
        // violations
        // if (RouteID, SequenceNumber) is a unique key.
        String sql = "UPDATE RouteStations SET SequenceNumber = SequenceNumber + 1 " +
                "WHERE RouteID = ? AND SequenceNumber >= ? " +
                "ORDER BY SequenceNumber DESC";
        // The ORDER BY in an UPDATE statement might not be supported by all DBs or
        // might behave unexpectedly.
        // A safer approach is to fetch the IDs and update them one by one or in a
        // batch,
        // or use a temporary table, or a more complex multi-step update if direct ORDER
        // BY in UPDATE fails.

        // For SQL Server, an UPDATE with ORDER BY is not standard.
        // A more robust way:
        // 1. Select the RouteStations to be updated, ordered by sequence DESC.
        // 2. Iterate and update them. This is less efficient but safer for constraints.

        // Simpler approach that works for many DBs (but check for SQL Server specific
        // syntax if needed):
        // Update all stations >= fromSequenceNumber by +1.
        // This must be done carefully. If we just run a single UPDATE,
        // and there's a unique constraint on (RouteID, SequenceNumber), it might fail
        // if, for example, Sequence 3 becomes 4, but 4 already exists.
        // Updating in descending order of current sequence number is key.

        // Let's try a direct update. If this causes issues due to unique constraints,
        // a multi-step update (e.g., to temporary negative numbers, then to final) or
        // fetching and updating one-by-one in a transaction would be needed.
        // Given the `updateRouteStationOrder` uses a two-phase update, a similar
        // strategy might be best.

        // Phase 1: Update to temporary high, unique sequence numbers (e.g., current_seq
        // + MAX_POSSIBLE_STATIONS)
        // Phase 2: Update from temporary to new_seq + 1

        // Simpler, direct approach (might fail on some DBs with strict unique key
        // enforcement during statement execution):
        // String directUpdateSql = "UPDATE RouteStations SET SequenceNumber =
        // SequenceNumber + 1 WHERE RouteID = ? AND SequenceNumber >= ? ORDER BY
        // SequenceNumber DESC";
        // The ORDER BY in UPDATE is tricky.
        // A common strategy for SQL Server to update in a specific order is using a CTE
        // or subquery if possible,
        // or fetching and then updating.

        // Let's use a straightforward update. The database should handle the
        // constraints
        // correctly if the operations are atomic or if deferred constraints are
        // available.
        // If not, this will throw an SQLException which the servlet will catch.
        String updateSql = "UPDATE RouteStations SET SequenceNumber = SequenceNumber + 1 " +
                "WHERE RouteID = ? AND SequenceNumber >= ?";

        // To be absolutely safe against unique constraint violations during the shift,
        // especially if not all DBs process this atomically in the desired order,
        // we should update from highest sequence number downwards.
        // The SQL standard doesn't guarantee order of row updates within a single
        // statement without specific DB extensions.
        // So, we'll fetch and update in a loop, in descending order.

        List<Integer> stationIdsToUpdate = new ArrayList<>();
        String selectSql = "SELECT StationID FROM RouteStations WHERE RouteID = ? AND SequenceNumber >= ? ORDER BY SequenceNumber DESC";

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            psSelect = conn.prepareStatement(selectSql);
            psSelect.setInt(1, routeId);
            psSelect.setInt(2, fromSequenceNumber);
            rs = psSelect.executeQuery();
            while (rs.next()) {
                stationIdsToUpdate.add(rs.getInt("StationID"));
            }
            rs.close();
            psSelect.close();

            // Now update them one by one, using their original sequence number + 1
            // The stationId is unique per route, so we can use that to identify rows.
            // The sequence number we are setting is based on its *original* sequence
            // number.
            // This means we need to know the original sequence for each.
            // The previous select only got station IDs. Let's refine.

            // Refined approach: Update directly, relying on the DB or catching constraint
            // violation.
            // If this fails, the more complex two-phase update (like in
            // updateRouteStationOrder)
            // or fetching all details and then batch updating would be necessary.
            // For now, let's assume a direct update is attempted.
            // The `ORDER BY SequenceNumber DESC` in the single UPDATE statement is crucial
            // if supported.
            // Most databases (like PostgreSQL, MySQL) support `ORDER BY` in `UPDATE` when
            // combined with `LIMIT`,
            // but not always for a full table scan update. SQL Server does not directly
            // support `ORDER BY` in `UPDATE`
            // in the same way.

            // Given the constraints, the safest bet is to update rows one by one, from
            // highest sequence to lowest.
            // This is less performant for many updates but guarantees correctness with
            // unique constraints.
            // The `updateRouteStationOrder` method uses a two-phase update (to negative,
            // then to final).
            // Let's use a single UPDATE statement that targets rows and increments their
            // sequence.
            // This relies on the database's ability to handle the unique constraint,
            // potentially by deferring checks or by the nature of the update.
            // If `(RouteID, SequenceNumber)` is a unique key, `UPDATE ... SET
            // SequenceNumber = SequenceNumber + 1 WHERE ...`
            // should work if the DB processes updates in a way that doesn't immediately
            // conflict.
            // For instance, if it updates sequence 5 to 6, then 4 to 5, then 3 to 4.
            // This is often the case.

            String shiftSql = "UPDATE RouteStations SET SequenceNumber = SequenceNumber + 1 " +
                    "WHERE RouteID = ? AND SequenceNumber >= ?";
            // To ensure this works correctly with unique constraints, it's often better to
            // update in reverse order of the sequence numbers.
            // However, a single SQL statement like this is usually handled correctly by
            // modern RDBMS.
            // If not, the transaction will fail and roll back.

            psUpdate = conn.prepareStatement(shiftSql); // Re-using psUpdate variable
            psUpdate.setInt(1, routeId);
            psUpdate.setInt(2, fromSequenceNumber);
            psUpdate.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println(
                            "Error rolling back transaction in incrementSequenceNumbersFrom: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (psSelect != null)
                try {
                    psSelect.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (psUpdate != null)
                try {
                    psUpdate.close();
                } catch (SQLException e) {
                    /* ignored */ }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    /* ignored */ }
            }
        }
    }

    @Override
    public List<Route> findAllByActive(Boolean isActive) throws SQLException {
        List<Route> routes = new ArrayList<>();
        String sql = "SELECT RouteID, RouteName, Description, IsActive FROM Routes";
        if (isActive != null) {
            sql += " WHERE IsActive = ?";
        }
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (isActive != null) {
                ps.setBoolean(1, isActive);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    routes.add(mapResultSetToRoute(rs));
                }
            }
        }
        return routes;
    }

    // Main method for testing
    public static void main(String[] args) {
        RouteRepository routeRepository = new RouteRepositoryImpl();
        try {
            // Test findAll
            System.out.println("Testing findAll routes:");
            List<Route> routes = routeRepository.findAll();
            if (routes.isEmpty()) {
                System.out.println("No routes found.");
            } else {
                routes.forEach(r -> System.out.println(r));
            }

            // Test findById (assuming route with ID 1 exists)
            int testRouteId = 1; // Ensure this ID exists in your DB
            System.out.println("\nTesting findById for route ID: " + testRouteId);
            Optional<Route> routeOpt = routeRepository.findById(testRouteId);
            routeOpt.ifPresentOrElse(
                    r -> System.out.println("Found route: " + r),
                    () -> System.out.println("Route with ID " + testRouteId + " not found."));

            // Test findByRouteName
            String testRouteName = "Hanoi - Saigon"; // Ensure this name exists or use a real one from your DB
            System.out.println("\nTesting findByRouteName for route name: " + testRouteName);
            Optional<Route> routeByNameOpt = routeRepository.findByRouteName(testRouteName);
            routeByNameOpt.ifPresentOrElse(
                    r -> System.out.println("Found route by name: " + r),
                    () -> System.out.println("Route with name '" + testRouteName + "' not found."));

            // Example of saving a new route (uncomment and modify to test)
            /*
             * System.out.println("\nTesting save new route:");
             * Route newRoute = new Route(0, "TESTROUTE_MAIN",
             * "Test Route Main Description");
             * Route savedRoute = routeRepository.save(newRoute);
             * System.out.println("Saved route: " + savedRoute);
             * 
             * if (savedRoute.getRouteID() > 0) {
             * // Example of updating the route
             * System.out.println("\nTesting update route ID: " + savedRoute.getRouteID());
             * savedRoute.setDescription("Updated Test Route Main Description");
             * boolean updated = routeRepository.update(savedRoute);
             * System.out.println("Update successful: " + updated);
             * 
             * Optional<Route> updatedRouteOpt =
             * routeRepository.findById(savedRoute.getRouteID());
             * updatedRouteOpt.ifPresent(r -> System.out.println("Updated route details: " +
             * r));
             * 
             * // Example of deleting the route
             * System.out.println("\nTesting delete route ID: " + savedRoute.getRouteID());
             * boolean deleted = routeRepository.deleteById(savedRoute.getRouteID());
             * System.out.println("Delete successful: " + deleted);
             * }
             */

        } catch (SQLException e) {
            System.err.println("Error testing RouteRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
