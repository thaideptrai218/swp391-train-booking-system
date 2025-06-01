package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.LocationRepository;
import vn.vnrailway.model.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationRepositoryImpl implements LocationRepository {

    @Override
    public List<Location> getAllLocations() throws Exception {
        List<Location> locations = new ArrayList<>();
        String sql = "SELECT LocationID, LocationName, City, Region, Link FROM Locations ORDER BY LocationID"; // Assuming
                                                                                                               // a
        // table named
        // 'Locations'
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getInt("LocationID"));
                location.setLocationName(rs.getString("LocationName"));
                location.setCity(rs.getString("City"));
                location.setRegion(rs.getString("Region"));
                location.setLink(rs.getString("Link"));
                locations.add(location);
            }
        } catch (SQLException e) {
            // Log the exception or handle it more gracefully
            System.err.println("Error fetching locations: " + e.getMessage());
            throw new Exception("Database error while fetching locations.", e);
        } catch (Exception e) {
            System.err.println("Error establishing database connection for locations: " + e.getMessage());
            throw new Exception("Connection error while fetching locations.", e);
        }
        return locations;
    }
}
