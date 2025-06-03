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
        // Added LocationCode to the SELECT statement
        String sql = "SELECT LocationID, LocationCode, LocationName, City, Region, Link FROM Locations ORDER BY LocationID";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Location location = new Location();
                location.setLocationID(rs.getInt("LocationID"));
                location.setLocationCode(rs.getString("LocationCode")); // Added
                location.setLocationName(rs.getString("LocationName"));
                location.setCity(rs.getString("City"));
                location.setRegion(rs.getString("Region"));
                location.setLink(rs.getString("Link"));
                locations.add(location);
            }
        } catch (SQLException e) {
            // Log the exception or handle it more gracefully
            System.err.println("Error fetching all locations: " + e.getMessage());
            throw new Exception("Database error while fetching all locations.", e);
        } catch (Exception e) {
            System.err.println("Error establishing database connection for all locations: " + e.getMessage());
            throw new Exception("Connection error while fetching all locations.", e);
        }
        return locations;
    }

    @Override
    public List<Location> getLocations(int pageNumber, int pageSize, String filterRegion, String filterCity,
            String sortField, String sortOrder) throws Exception {
        List<Location> locations = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        // Added LocationCode to the SELECT statement
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT LocationID, LocationCode, LocationName, City, Region, Link FROM Locations");

        // Filtering
        boolean hasFilter = false;
        if (filterRegion != null && !filterRegion.trim().isEmpty()) {
            sqlBuilder.append(" WHERE Region LIKE ?");
            params.add("%" + filterRegion.trim() + "%");
            hasFilter = true;
        }
        if (filterCity != null && !filterCity.trim().isEmpty()) {
            if (hasFilter) {
                sqlBuilder.append(" AND City LIKE ?");
            } else {
                sqlBuilder.append(" WHERE City LIKE ?");
            }
            params.add("%" + filterCity.trim() + "%");
        }

        // Sorting - Validate sortField to prevent SQL injection
        String validSortField = "LocationName"; // Default
        if ("LocationID".equalsIgnoreCase(sortField) || "City".equalsIgnoreCase(sortField)
                || "Region".equalsIgnoreCase(sortField)) {
            validSortField = sortField;
        }
        // sortOrder is already validated in servlet (ASC/DESC)
        sqlBuilder.append(" ORDER BY ").append(validSortField).append(" ").append(sortOrder);

        // Pagination
        sqlBuilder.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        int offset = (pageNumber - 1) * pageSize;
        params.add(offset);
        params.add(pageSize);

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Location location = new Location();
                    location.setLocationID(rs.getInt("LocationID"));
                    location.setLocationCode(rs.getString("LocationCode")); // Added
                    location.setLocationName(rs.getString("LocationName"));
                    location.setCity(rs.getString("City"));
                    location.setRegion(rs.getString("Region"));
                    location.setLink(rs.getString("Link"));
                    locations.add(location);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching paginated/filtered/sorted locations: " + e.getMessage());
            throw new Exception("Database error while fetching locations.", e);
        } catch (Exception e) {
            System.err.println("Error establishing database connection for locations: " + e.getMessage());
            throw new Exception("Connection error while fetching locations.", e);
        }
        return locations;
    }

    @Override
    public int getTotalLocationCount(String filterRegion, String filterCity) throws Exception {
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM Locations");
        List<Object> params = new ArrayList<>();
        boolean hasFilter = false;

        if (filterRegion != null && !filterRegion.trim().isEmpty()) {
            sqlBuilder.append(" WHERE Region LIKE ?");
            params.add("%" + filterRegion.trim() + "%");
            hasFilter = true;
        }
        if (filterCity != null && !filterCity.trim().isEmpty()) {
            if (hasFilter) {
                sqlBuilder.append(" AND City LIKE ?");
            } else {
                sqlBuilder.append(" WHERE City LIKE ?");
            }
            params.add("%" + filterCity.trim() + "%");
        }

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching total location count with filters: " + e.getMessage());
            throw new Exception("Database error while fetching total location count.", e);
        } catch (Exception e) {
            System.err.println("Error establishing database connection for location count: " + e.getMessage());
            throw new Exception("Connection error while fetching location count.", e);
        }
        return 0;
    }
}
