package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
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

public class RouteRepositoryImpl implements RouteRepository {

    private Route mapResultSetToRoute(ResultSet rs) throws SQLException {
        Route route = new Route();
        route.setRouteID(rs.getInt("RouteID"));
        route.setRouteName(rs.getString("RouteName"));
        route.setDescription(rs.getString("Description"));
        return route;
    }

    private Station mapResultSetToStation(ResultSet rs) throws SQLException {
        Station station = new Station();
        station.setStationID(rs.getInt("StationID"));
        station.setStationName(rs.getString("StationName"));
        // Add other station properties if needed for the dropdown/display
        // e.g., station.setCity(rs.getString("City"));
        return station;
    }

    @Override
    public Optional<Route> findById(int routeId) throws SQLException {
        String sql = "SELECT RouteID, RouteName, Description FROM Routes WHERE RouteID = ?";
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
        String sql = "SELECT RouteID, RouteName, Description FROM Routes";
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
        String sql = "SELECT RouteID, RouteName, Description FROM Routes WHERE RouteName = ?";
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
        String sql = "INSERT INTO Routes (RouteName, Description) VALUES (?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, route.getRouteName());
            ps.setString(2, route.getDescription());

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
        String sql = "UPDATE Routes SET RouteName = ?, Description = ? WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, route.getRouteName());
            ps.setString(2, route.getDescription());
            ps.setInt(3, route.getRouteID());

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

            // Delete from RouteStations first
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
        String sql = "SELECT StationID, StationName FROM Stations ORDER BY StationName"; // Add other fields if needed
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
