package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.FeaturedRouteRepository;
import vn.vnrailway.model.FeaturedRoute;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        route.setDestinationStationCode(rs.getString("DestinationStationCode"));
        route.setDistance(rs.getDouble("Distance"));
        route.setTripsPerDay(rs.getInt("TripsPerDay"));
        // Placeholder for popular train names
        route.setPopularTrainNames(Arrays.asList("SE1", "TN5")); // Example popular trains
        return route;
    }

    @Override
    public List<FeaturedRoute> findAll() throws SQLException {
        List<FeaturedRoute> routes = new ArrayList<>();
        String sql = "SELECT " +
                "    fr.FeaturedRouteID, " +
                "    fr.RouteID, " +
                "    fr.OriginStationID, " +
                "    fr.DestinationStationID, " +
                "    s_dest.StationCode AS DestinationStationCode, " +
                "    fr.DisplayName, " +
                "    ABS(rs_dest.DistanceFromStart - rs_orig.DistanceFromStart) AS Distance, " +
                "    (SELECT COUNT(*) FROM Trips t WHERE t.RouteID = fr.RouteID) AS TripsPerDay " +
                "FROM " +
                "    FeaturedRoutes fr " +
                "JOIN " +
                "    Routes r ON fr.RouteID = r.RouteID " +
                "JOIN " +
                "    RouteStations rs_orig ON r.RouteID = rs_orig.RouteID AND fr.OriginStationID = rs_orig.StationID " +
                "JOIN " +
                "    RouteStations rs_dest ON r.RouteID = rs_dest.RouteID AND fr.DestinationStationID = rs_dest.StationID "
                +
                "JOIN " +
                "    Stations s_dest ON fr.DestinationStationID = s_dest.StationID " + // Join to get destination
                                                                                       // station code
                "GROUP BY " +
                "    fr.FeaturedRouteID, fr.RouteID, fr.OriginStationID, fr.DestinationStationID, s_dest.StationCode, fr.DisplayName, "
                +
                "    rs_orig.DistanceFromStart, rs_dest.DistanceFromStart";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                routes.add(mapResultSetToFeaturedRoute(rs));
            }
        }
        return routes;
    }

    @Override
    public void deleteByRouteId(int routeId) throws SQLException {
        String sql = "DELETE FROM FeaturedRoutes WHERE RouteID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            ps.executeUpdate();
        }
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
