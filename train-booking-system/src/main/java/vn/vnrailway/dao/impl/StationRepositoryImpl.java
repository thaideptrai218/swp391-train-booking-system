package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.model.Station;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationRepositoryImpl implements StationRepository {

    private Station mapResultSetToStation(ResultSet rs) throws SQLException {
        Station station = new Station();
        station.setStationID(rs.getInt("StationID"));
        station.setStationName(rs.getString("StationName"));
        station.setAddress(rs.getString("Address"));
        station.setCity(rs.getString("City"));
        station.setRegion(rs.getString("Region"));
        station.setPhoneNumber(rs.getString("PhoneNumber"));
        try {
            station.setLocked(rs.getBoolean("IsLocked"));
        } catch (SQLException ignore) {}
        try {
            station.setActive(rs.getBoolean("IsActive"));
        } catch (SQLException ignore) {}
        try {
            station.setStationCode(rs.getString("StationCode"));
        } catch (SQLException ignore) {}
        return station;
    }

    @Override
    public Optional<Station> findById(int stationId) throws SQLException {
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region, PhoneNumber, IsLocked, IsActive FROM Stations WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Station> findByStationName(String stationName) throws SQLException {
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region, PhoneNumber, IsLocked, IsActive FROM Stations WHERE StationName COLLATE SQL_Latin1_General_CI_AI = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stationName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Station> findAll() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region, PhoneNumber, IsLocked, IsActive FROM Stations";
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
    public List<Station> findByActive(Boolean isActive) throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region, PhoneNumber, IsLocked, IsActive FROM Stations";
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
                    stations.add(mapResultSetToStation(rs));
                }
            }
        }
        return stations;
    }

    @Override
    public Station save(Station station) throws SQLException {
        String sql = "INSERT INTO Stations (StationName, Address, City, Region, PhoneNumber, IsLocked, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.setString(3, station.getCity());
            ps.setString(4, station.getRegion());
            ps.setString(5, station.getPhoneNumber());
            ps.setBoolean(6, station.isLocked());
            ps.setBoolean(7, station.isActive());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Lỗi khi tạo ga, không có ga nào được thêm.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    station.setStationID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Lỗi khi tạo ga, không thể lấy ID của ga mới.");
                }
            }
        }
        return station;
    }

    @Override
    public boolean update(Station station) throws SQLException {
        String sql = "UPDATE Stations SET StationName = ?, Address = ?, City = ?, Region = ?, PhoneNumber = ?, IsLocked = ?, IsActive = ? WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.setString(3, station.getCity());
            ps.setString(4, station.getRegion());
            ps.setString(5, station.getPhoneNumber());
            ps.setBoolean(6, station.isLocked());
            ps.setBoolean(7, station.isActive());
            ps.setInt(8, station.getStationID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateStationLocked(int stationId, boolean isLocked) throws SQLException {
        String sql = "UPDATE Stations SET IsLocked = ? WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isLocked);
            ps.setInt(2, stationId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean updateStationActive(int stationId, boolean isActive) throws SQLException {
        String sql = "UPDATE Stations SET IsActive = ? WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, stationId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int stationId) throws SQLException {
        String sql = "DELETE FROM Stations WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            return ps.executeUpdate() > 0;
        }
    }

    // Testing
    public static void main(String[] args) {
        StationRepository stationRepository = new StationRepositoryImpl();
        try {
            // findAll
            System.out.println("Testing findAll stations:");
            List<Station> stations = stationRepository.findAll();
            if (stations.isEmpty()) {
                System.out.println("No stations found.");
            } else {
                stations.forEach(s -> System.out.println(s));
            }

            // Test findById (assuming station with ID 1 exists, or change ID for testing)
            int testStationId = 1; // Ensure this ID exists in your DB for testing
            System.out.println("\nTesting findById for station ID: " + testStationId);
            Optional<Station> stationOpt = stationRepository.findById(testStationId);
            stationOpt.ifPresentOrElse(
                    s -> System.out.println("Found station: " + s),
                    () -> System.out.println("Station with ID " + testStationId + " not found."));

            // Example of saving a new station (uncomment and modify to test)
            /*
             * System.out.println("\nTesting save new station:");
             * Station newStation = new Station(0, "TESTCODE", "Test Station Name",
             * "123 Test Address", "Test City", "Test Region", "0123456789");
             * Station savedStation = stationRepository.save(newStation);
             * System.out.println("Saved station: " + savedStation);
             * 
             * // Example of updating the station (use the ID from savedStation)
             * if (savedStation.getStationID() > 0) {
             * System.out.println("\nTesting update station ID: " +
             * savedStation.getStationID());
             * savedStation.setStationName("Test Station Updated Name");
             * savedStation.setPhoneNumber("9876543210");
             * boolean updated = stationRepository.update(savedStation);
             * System.out.println("Update successful: " + updated);
             * 
             * Optional<Station> updatedStationOpt =
             * stationRepository.findById(savedStation.getStationID());
             * updatedStationOpt.ifPresent(s ->
             * System.out.println("Updated station details: " + s));
             * }
             */

        } catch (SQLException e) {
            System.err.println("Error testing StationRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
