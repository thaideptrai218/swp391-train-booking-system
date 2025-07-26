package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.dto.RouteStationDetailDTO;
import vn.vnrailway.model.Route;
import vn.vnrailway.model.Station;
import vn.vnrailway.controller.manager.ManageRoutesServlet.StationOrderUpdateDTO;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteRepositoryImpl implements RouteRepository {

    public RouteRepositoryImpl() {
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
                throw new SQLException("Tạo tuyến đường không thành công, không có dòng nào được chèn.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    route.setRouteID(generatedKeys.getInt(1));
                } else {
                    System.err.println(
                            "Tạo tuyến đường thành công nhưng không lấy được ID của tuyến đường mới.");
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
        // Kiểm tra IsActive của ga trước khi thêm
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
        // Nếu ga đang hoạt động, thêm vào RouteStations
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
        String sql = "UPDATE RouteStations SET SequenceNumber = ?, DistanceFromStart = ?, DefaultStopTime = ? " +
                "WHERE RouteID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sequenceNumber);
            ps.setBigDecimal(2, distanceFromStart);
            ps.setInt(3, defaultStopTime);
            ps.setInt(4, routeId);
            ps.setInt(5, stationId);

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
        String sqlPhase1 = "UPDATE RouteStations SET SequenceNumber = ? WHERE RouteID = ? AND StationID = ?";

        String sqlPhase2 = "UPDATE RouteStations SET SequenceNumber = ? WHERE RouteID = ? AND StationID = ?";

        Connection conn = null;
        PreparedStatement psPhase1 = null;
        PreparedStatement psPhase2 = null;
        boolean allSuccess = true;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            psPhase1 = conn.prepareStatement(sqlPhase1);
            for (StationOrderUpdateDTO stationOrder : stationsOrder) {
                psPhase1.setInt(1, -(stationOrder.getStationId()));
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

                if (result == 0) {

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

            psPhase2 = conn.prepareStatement(sqlPhase2);
            for (StationOrderUpdateDTO stationOrder : stationsOrder) {
                psPhase2.setInt(1, stationOrder.getSequenceNumber());
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
                }
            if (psPhase2 != null)
                try {
                    psPhase2.close();
                } catch (SQLException e) {
                }
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

                    return rs.getInt("MaxSequence") + 1;
                } else {
                    return 1;
                }
            }
        }
    }

    @Override
    public void incrementSequenceNumbersFrom(int routeId, int fromSequenceNumber) throws SQLException {
        List<Integer> stationIdsToUpdate = new ArrayList<>();
        String selectSql = "SELECT StationID FROM RouteStations WHERE RouteID = ? AND SequenceNumber >= ? ORDER BY SequenceNumber DESC";

        Connection conn = null;
        PreparedStatement psSelect = null;
        PreparedStatement psUpdate = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            conn.setAutoCommit(false);

            psSelect = conn.prepareStatement(selectSql);
            psSelect.setInt(1, routeId);
            psSelect.setInt(2, fromSequenceNumber);
            rs = psSelect.executeQuery();
            while (rs.next()) {
                stationIdsToUpdate.add(rs.getInt("StationID"));
            }
            rs.close();
            psSelect.close();
            String shiftSql = "UPDATE RouteStations SET SequenceNumber = SequenceNumber + 1 " +
                    "WHERE RouteID = ? AND SequenceNumber >= ?";
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

    public static void main(String[] args) {
        RouteRepository routeRepository = new RouteRepositoryImpl();
        try {
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
            String testRouteName = "Hanoi - Saigon";
            System.out.println("\nTesting findByRouteName for route name: " + testRouteName);
            Optional<Route> routeByNameOpt = routeRepository.findByRouteName(testRouteName);
            routeByNameOpt.ifPresentOrElse(
                    r -> System.out.println("Found route by name: " + r),
                    () -> System.out.println("Route with name '" + testRouteName + "' not found."));
        } catch (SQLException e) {
            System.err.println("Error testing RouteRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
