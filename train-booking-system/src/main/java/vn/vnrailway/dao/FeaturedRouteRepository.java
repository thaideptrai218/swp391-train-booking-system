package vn.vnrailway.dao;

import vn.vnrailway.model.FeaturedRoute;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FeaturedRouteRepository {
    // We primarily need a method to get all featured routes for display
    List<FeaturedRoute> findAll() throws SQLException;

    // Optional: Add other methods if direct manipulation from admin side is needed
    // later
    // FeaturedRoute save(FeaturedRoute featuredRoute) throws SQLException;
    // Optional<FeaturedRoute> findById(int featuredRouteId) throws SQLException;
}
