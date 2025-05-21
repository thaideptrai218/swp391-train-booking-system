package vn.vnrailway.dao;

import vn.vnrailway.model.Route;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Optional<Route> findById(int routeId) throws SQLException;
    List<Route> findAll() throws SQLException;
    Route save(Route route) throws SQLException; // Returns the saved route, possibly with generated ID
    boolean update(Route route) throws SQLException;
    boolean deleteById(int routeId) throws SQLException;
    Optional<Route> findByRouteName(String routeName) throws SQLException; // Added for convenience
}
