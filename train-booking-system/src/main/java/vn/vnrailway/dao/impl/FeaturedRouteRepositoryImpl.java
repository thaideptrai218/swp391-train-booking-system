package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.FeaturedRouteRepository;
import vn.vnrailway.model.FeaturedRoute;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
// import java.util.Optional; // Not strictly needed for findAll

public class FeaturedRouteRepositoryImpl implements FeaturedRouteRepository {

    private FeaturedRoute mapResultSetToFeaturedRoute(ResultSet rs) throws SQLException {
        FeaturedRoute route = new FeaturedRoute();
        route.setFeaturedRouteID(rs.getInt("FeaturedRouteID"));
        route.setRouteID(rs.getInt("RouteID"));
        route.setOriginStationID(rs.getInt("OriginStationID"));
        route.setDestinationStationID(rs.getInt("DestinationStationID"));
        route.setDisplayName(rs.getString("DisplayName"));
        // The fields backgroundImageUrl, tripsPerDay, distance, popularTrainNames are
        // not in the DB.
        return route;
    }

    @Override
    public List<FeaturedRoute> findAll() throws SQLException {
        List<FeaturedRoute> routes = new ArrayList<>();
        // SQL selects only the 5 columns present in the FeaturedRoutes table.
        String sql = "SELECT FeaturedRouteID, RouteID, OriginStationID, DestinationStationID, DisplayName FROM FeaturedRoutes";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                routes.add(mapResultSetToFeaturedRoute(rs));
            }
        }
        return routes;
    }

    // Optional: Implement save and findById if needed later
    /*
     * @Override
     * public FeaturedRoute save(FeaturedRoute featuredRoute) throws SQLException {
     * // Implementation for saving a featured route
     * return featuredRoute; // Placeholder
     * }
     * 
     * @Override
     * public Optional<FeaturedRoute> findById(int featuredRouteId) throws
     * SQLException {
     * // Implementation for finding a featured route by ID
     * return Optional.empty(); // Placeholder
     * }
     */
}
