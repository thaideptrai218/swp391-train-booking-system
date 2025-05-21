package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.model.Station;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StationRepositoryImpl implements StationRepository {

    @Override
    public Optional<Station> findById(int stationId) throws SQLException {
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region FROM Stations WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Station station = new Station();
                    station.setStationID(rs.getInt("StationID"));
                    station.setStationCode(rs.getString("StationCode"));
                    station.setStationName(rs.getString("StationName"));
                    station.setAddress(rs.getString("Address"));
                    station.setCity(rs.getString("City"));
                    station.setRegion(rs.getString("Region"));
                    return Optional.of(station);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Station> findByStationCode(String stationCode) throws SQLException {
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region FROM Stations WHERE StationCode = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, stationCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Station station = new Station();
                    station.setStationID(rs.getInt("StationID"));
                    station.setStationCode(rs.getString("StationCode"));
                    station.setStationName(rs.getString("StationName"));
                    station.setAddress(rs.getString("Address"));
                    station.setCity(rs.getString("City"));
                    station.setRegion(rs.getString("Region"));
                    return Optional.of(station);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Station> findAll() throws SQLException {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT StationID, StationCode, StationName, Address, City, Region FROM Stations";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Station station = new Station();
                station.setStationID(rs.getInt("StationID"));
                station.setStationCode(rs.getString("StationCode"));
                station.setStationName(rs.getString("StationName"));
                station.setAddress(rs.getString("Address"));
                station.setCity(rs.getString("City"));
                station.setRegion(rs.getString("Region"));
                stations.add(station);
            }
        }
        return stations;
    }

    @Override
    public Station save(Station station) throws SQLException {
        String sql = "INSERT INTO Stations (StationCode, StationName, Address, City, Region) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, station.getStationCode());
            ps.setString(2, station.getStationName());
            ps.setString(3, station.getAddress());
            ps.setString(4, station.getCity());
            ps.setString(5, station.getRegion());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating station failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    station.setStationID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating station failed, no ID obtained.");
                }
            }
        }
        return station;
    }

    @Override
    public boolean update(Station station) throws SQLException {
        String sql = "UPDATE Stations SET StationCode = ?, StationName = ?, Address = ?, City = ?, Region = ? WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, station.getStationCode());
            ps.setString(2, station.getStationName());
            ps.setString(3, station.getAddress());
            ps.setString(4, station.getCity());
            ps.setString(5, station.getRegion());
            ps.setInt(6, station.getStationID());
            
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
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        StationRepository stationRepository = new StationRepositoryImpl();
        try {
            // Test findAll
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
                () -> System.out.println("Station with ID " + testStationId + " not found.")
            );

            // Example of saving a new station (uncomment and modify to test)
            /*
            System.out.println("\nTesting save new station:");
            Station newStation = new Station(0, "TESTCODE", "Test Station Name", "123 Test Address", "Test City", "Test Region");
            Station savedStation = stationRepository.save(newStation);
            System.out.println("Saved station: " + savedStation);
            
            // Example of updating the station (use the ID from savedStation)
            if (savedStation.getStationID() > 0) {
                 System.out.println("\nTesting update station ID: " + savedStation.getStationID());
                 savedStation.setStationName("Test Station Updated Name");
                 boolean updated = stationRepository.update(savedStation);
                 System.out.println("Update successful: " + updated);
            
                 Optional<Station> updatedStationOpt = stationRepository.findById(savedStation.getStationID());
                 updatedStationOpt.ifPresent(s -> System.out.println("Updated station details: " + s));

                 // Example of deleting the station
                 System.out.println("\nTesting delete station ID: " + savedStation.getStationID());
                 boolean deleted = stationRepository.deleteById(savedStation.getStationID());
                 System.out.println("Delete successful: " + deleted);
            }
            */

        } catch (SQLException e) {
            System.err.println("Error testing StationRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
