package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.RouteRepository;
import vn.vnrailway.model.Route;

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
                    System.err.println("Creating route succeeded, but no ID was obtained. RouteID might not be auto-generated or configured to be returned.");
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
        String sql = "DELETE FROM Routes WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, routeId);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
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
                () -> System.out.println("Route with ID " + testRouteId + " not found.")
            );

            // Test findByRouteName
            String testRouteName = "Hanoi - Saigon"; // Ensure this name exists or use a real one from your DB
            System.out.println("\nTesting findByRouteName for route name: " + testRouteName);
            Optional<Route> routeByNameOpt = routeRepository.findByRouteName(testRouteName);
            routeByNameOpt.ifPresentOrElse(
                r -> System.out.println("Found route by name: " + r),
                () -> System.out.println("Route with name '" + testRouteName + "' not found.")
            );
            
            // Example of saving a new route (uncomment and modify to test)
            /*
            System.out.println("\nTesting save new route:");
            Route newRoute = new Route(0, "TESTROUTE_MAIN", "Test Route Main Description");
            Route savedRoute = routeRepository.save(newRoute);
            System.out.println("Saved route: " + savedRoute);

            if (savedRoute.getRouteID() > 0) {
                // Example of updating the route
                System.out.println("\nTesting update route ID: " + savedRoute.getRouteID());
                savedRoute.setDescription("Updated Test Route Main Description");
                boolean updated = routeRepository.update(savedRoute);
                System.out.println("Update successful: " + updated);

                Optional<Route> updatedRouteOpt = routeRepository.findById(savedRoute.getRouteID());
                updatedRouteOpt.ifPresent(r -> System.out.println("Updated route details: " + r));

                // Example of deleting the route
                System.out.println("\nTesting delete route ID: " + savedRoute.getRouteID());
                boolean deleted = routeRepository.deleteById(savedRoute.getRouteID());
                System.out.println("Delete successful: " + deleted);
            }
            */

        } catch (SQLException e) {
            System.err.println("Error testing RouteRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
