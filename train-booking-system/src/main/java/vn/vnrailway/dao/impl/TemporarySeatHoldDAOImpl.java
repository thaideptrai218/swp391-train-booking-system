package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.TemporarySeatHoldDAO;
import vn.vnrailway.model.TemporarySeatHold;
import vn.vnrailway.utils.DBContext; // Assuming this is your DB connection utility

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class TemporarySeatHoldDAOImpl implements TemporarySeatHoldDAO {

    @Override
    public TemporarySeatHold findActiveHoldBySessionAndLeg(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException {
        TemporarySeatHold hold = null;
        String sql = "SELECT HoldID, TripID, SeatID, CoachID, LegOriginStationID, LegDestinationStationID, SessionID, UserID, ExpiresAt, CreatedAt " +
                     "FROM dbo.TemporarySeatHolds " +
                     "WHERE TripID = ? AND SeatID = ? AND LegOriginStationID = ? AND LegDestinationStationID = ? AND SessionID = ? AND ExpiresAt > GETDATE()";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, seatId);
            ps.setInt(3, legOriginStationId);
            ps.setInt(4, legDestinationStationId);
            ps.setString(5, sessionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hold = TemporarySeatHold.builder()
                            .holdId(rs.getInt("HoldID"))
                            .tripId(rs.getInt("TripID"))
                            .seatId(rs.getInt("SeatID"))
                            .coachId(rs.getInt("CoachID"))
                            .legOriginStationId(rs.getInt("LegOriginStationID"))
                            .legDestinationStationId(rs.getInt("LegDestinationStationID"))
                            .sessionId(rs.getString("SessionID"))
                            .userId(rs.getObject("UserID", Integer.class)) // Handles NULL UserID
                            .expiresAt(rs.getTimestamp("ExpiresAt"))
                            .createdAt(rs.getTimestamp("CreatedAt"))
                            .build();
                }
            }
        }
        return hold;
    }

    @Override
    public boolean refreshHoldExpiry(Connection conn, int holdId, Date newExpiresAt) throws SQLException {
        String sql = "UPDATE dbo.TemporarySeatHolds SET ExpiresAt = ? WHERE HoldID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(newExpiresAt.getTime()));
            ps.setInt(2, holdId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean addHold(Connection conn, TemporarySeatHold holdData) throws SQLException {
        String sql = "INSERT INTO dbo.TemporarySeatHolds " +
                     "(TripID, SeatID, CoachID, LegOriginStationID, LegDestinationStationID, SessionID, UserID, ExpiresAt) " + // CreatedAt has DEFAULT
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, holdData.getTripId());
            ps.setInt(2, holdData.getSeatId());
            ps.setInt(3, holdData.getCoachId());
            ps.setInt(4, holdData.getLegOriginStationId());
            ps.setInt(5, holdData.getLegDestinationStationId());
            ps.setString(6, holdData.getSessionId());
            if (holdData.getUserId() != null) {
                ps.setInt(7, holdData.getUserId());
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(8, new Timestamp(holdData.getExpiresAt().getTime()));
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean releaseHold(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException {
        String sql = "DELETE FROM dbo.TemporarySeatHolds " +
                     "WHERE TripID = ? AND SeatID = ? AND LegOriginStationID = ? AND LegDestinationStationID = ? AND SessionID = ? AND ExpiresAt > GETDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, seatId);
            ps.setInt(3, legOriginStationId);
            ps.setInt(4, legDestinationStationId);
            ps.setString(5, sessionId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public int releaseAllHoldsForSession(Connection conn, String sessionId) throws SQLException {
        String sql = "DELETE FROM dbo.TemporarySeatHolds WHERE SessionID = ? AND ExpiresAt > GETDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            return ps.executeUpdate();
        }
    }
    
    @Override
    public int releaseExpiredHolds(Connection conn) throws SQLException {
        String sql = "DELETE FROM dbo.TemporarySeatHolds WHERE ExpiresAt <= GETDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }
}
