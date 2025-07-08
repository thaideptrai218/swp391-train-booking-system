package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TripStationRepository;
import vn.vnrailway.model.TripStation;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TripStationRepositoryImpl implements TripStationRepository {

    private TripStation mapResultSetToTripStation(ResultSet rs) throws SQLException {
        TripStation ts = new TripStation();
        ts.setTripStationID(rs.getInt("TripStationID"));
        ts.setTripID(rs.getInt("TripID"));
        ts.setStationID(rs.getInt("StationID"));
        ts.setSequenceNumber(rs.getInt("SequenceNumber"));

        Timestamp scheduledArrival = rs.getTimestamp("ScheduledArrival");
        if (scheduledArrival != null) {
            ts.setScheduledArrival(scheduledArrival.toLocalDateTime());
        }
        Timestamp scheduledDeparture = rs.getTimestamp("ScheduledDeparture");
        if (scheduledDeparture != null) {
            ts.setScheduledDeparture(scheduledDeparture.toLocalDateTime());
        }
        Timestamp actualArrival = rs.getTimestamp("ActualArrival");
        if (actualArrival != null) {
            ts.setActualArrival(actualArrival.toLocalDateTime());
        }
        Timestamp actualDeparture = rs.getTimestamp("ActualDeparture");
        if (actualDeparture != null) {
            ts.setActualDeparture(actualDeparture.toLocalDateTime());
        }
        return ts;
    }

    @Override
    public Optional<TripStation> findById(int tripStationId) throws SQLException {
        String sql = "SELECT * FROM TripStations WHERE TripStationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripStationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTripStation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TripStation> findByTripId(int tripId) throws SQLException {
        List<TripStation> tripStations = new ArrayList<>();
        String sql = "SELECT * FROM TripStations WHERE TripID = ? ORDER BY SequenceNumber ASC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tripStations.add(mapResultSetToTripStation(rs));
                }
            }
        }
        return tripStations;
    }

    @Override
    public List<TripStation> findByStationId(int stationId) throws SQLException {
        List<TripStation> tripStations = new ArrayList<>();
        String sql = "SELECT * FROM TripStations WHERE StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tripStations.add(mapResultSetToTripStation(rs));
                }
            }
        }
        return tripStations;
    }

    @Override
    public Optional<TripStation> findByTripIdAndStationId(int tripId, int stationId) throws SQLException {
        String sql = "SELECT * FROM TripStations WHERE TripID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, stationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTripStation(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public TripStation save(TripStation tripStation) throws SQLException {
        String sql = "INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture, ActualArrival, ActualDeparture) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tripStation.getTripID());
            ps.setInt(2, tripStation.getStationID());
            ps.setInt(3, tripStation.getSequenceNumber());
            ps.setTimestamp(4,
                    tripStation.getScheduledArrival() != null ? Timestamp.valueOf(tripStation.getScheduledArrival())
                            : null);
            ps.setTimestamp(5,
                    tripStation.getScheduledDeparture() != null ? Timestamp.valueOf(tripStation.getScheduledDeparture())
                            : null);
            ps.setTimestamp(6,
                    tripStation.getActualArrival() != null ? Timestamp.valueOf(tripStation.getActualArrival()) : null);
            ps.setTimestamp(7,
                    tripStation.getActualDeparture() != null ? Timestamp.valueOf(tripStation.getActualDeparture())
                            : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating trip station failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tripStation.setTripStationID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating trip station failed, no ID obtained.");
                }
            }
        }
        return tripStation;
    }

    @Override
    public boolean update(TripStation tripStation) throws SQLException {
        String sql = "UPDATE TripStations SET TripID = ?, StationID = ?, SequenceNumber = ?, ScheduledArrival = ?, ScheduledDeparture = ?, ActualArrival = ?, ActualDeparture = ? WHERE TripStationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripStation.getTripID());
            ps.setInt(2, tripStation.getStationID());
            ps.setInt(3, tripStation.getSequenceNumber());
            ps.setTimestamp(4,
                    tripStation.getScheduledArrival() != null ? Timestamp.valueOf(tripStation.getScheduledArrival())
                            : null);
            ps.setTimestamp(5,
                    tripStation.getScheduledDeparture() != null ? Timestamp.valueOf(tripStation.getScheduledDeparture())
                            : null);
            ps.setTimestamp(6,
                    tripStation.getActualArrival() != null ? Timestamp.valueOf(tripStation.getActualArrival()) : null);
            ps.setTimestamp(7,
                    tripStation.getActualDeparture() != null ? Timestamp.valueOf(tripStation.getActualDeparture())
                            : null);
            ps.setInt(8, tripStation.getTripStationID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int tripStationId) throws SQLException {
        String sql = "DELETE FROM TripStations WHERE TripStationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripStationId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteByTripId(int tripId) throws SQLException {
        String sql = "DELETE FROM TripStations WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            // This could affect multiple rows, so we check if any rows were affected.
            // The number of affected rows might be useful to return or log.
            return ps.executeUpdate() >= 0; // Returns true if 0 or more rows deleted (no error)
        }
    }

    @Override
    public void updateScheduledArrival(int tripId, int stationId, LocalDateTime scheduledArrival) throws SQLException {
        String sql = "UPDATE TripStations SET ScheduledArrival = ? WHERE TripID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, scheduledArrival != null ? Timestamp.valueOf(scheduledArrival) : null);
            ps.setInt(2, tripId);
            ps.setInt(3, stationId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateScheduledDeparture(int tripId, int stationId, LocalDateTime scheduledDeparture)
            throws SQLException {
        String sql = "UPDATE TripStations SET ScheduledDeparture = ? WHERE TripID = ? AND StationID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, scheduledDeparture != null ? Timestamp.valueOf(scheduledDeparture) : null);
            ps.setInt(2, tripId);
            ps.setInt(3, stationId);
            ps.executeUpdate();
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        TripStationRepository tsRepository = new TripStationRepositoryImpl();
        try {
            System.out.println("Testing TripStationRepository...");
            List<TripStation> stationsForTrip1 = tsRepository.findByTripId(1);
            stationsForTrip1.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
