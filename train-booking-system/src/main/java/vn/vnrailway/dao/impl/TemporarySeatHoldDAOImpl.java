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

import vn.vnrailway.dto.payment.OriginalCartItemDataDto; // Added import
import java.sql.Statement; // Added import
import java.util.List; // Added import

public class TemporarySeatHoldDAOImpl implements TemporarySeatHoldDAO {

    @Override
    public TemporarySeatHold findActiveHoldBySessionAndLeg(Connection conn, String sessionId, int tripId, int seatId, int coachId, int legOriginStationId, int legDestinationStationId) throws SQLException {
        TemporarySeatHold hold = null;
        String sql = "SELECT HoldID, TripID, SeatID, CoachID, LegOriginStationID, LegDestinationStationID, SessionID, UserID, ExpiresAt, CreatedAt " +
                     "FROM dbo.TemporarySeatHolds " +
                     "WHERE SessionID = ? AND TripID = ? AND SeatID = ? AND CoachID = ? AND LegOriginStationID = ? AND LegDestinationStationID = ? AND ExpiresAt > GETDATE()";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            ps.setInt(2, tripId);
            ps.setInt(3, seatId);
            ps.setInt(4, coachId);
            ps.setInt(5, legOriginStationId);
            ps.setInt(6, legDestinationStationId);
            
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
    public long addHold(Connection conn, TemporarySeatHold holdData) throws SQLException {
        String sql = "INSERT INTO dbo.TemporarySeatHolds " +
                     "(TripID, SeatID, CoachID, LegOriginStationID, LegDestinationStationID, SessionID, UserID, ExpiresAt) " + // CreatedAt has DEFAULT
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // Return generated HoldID
                    }
                }
            }
            return -1; // Indicate failure
        }
    }

    @Override
    public boolean releaseHold(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException {
        // This method might need to include coachId if holds are specific to coach for the same seat number on different coaches of a trip segment.
        // Assuming seatId is unique across the trip for this method's current signature.
        // If coachId is essential for uniqueness here, the signature and query need an update.
        // For now, matching the existing implementation.
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
        String sql = "DELETE FROM dbo.TemporarySeatHolds WHERE SessionID = ? AND ExpiresAt > GETDATE()"; // Only release active holds
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

    @Override
    public int deleteHoldsForCartItems(Connection conn, String sessionId, List<OriginalCartItemDataDto> itemsToClear) throws SQLException {
        if (itemsToClear == null || itemsToClear.isEmpty()) {
            return 0;
        }
        // It's generally safer to delete by specific criteria rather than just HoldID
        // if the HoldID isn't directly available or to ensure atomicity for a set of items.
        // This implementation assumes we want to delete based on the detailed item properties.
        String sql = "DELETE FROM dbo.TemporarySeatHolds WHERE SessionID = ? AND TripID = ? AND SeatID = ? AND CoachID = ? AND LegOriginStationID = ? AND LegDestinationStationID = ?";
        int totalDeletedCount = 0;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OriginalCartItemDataDto item : itemsToClear) {
                try {
                    ps.setString(1, sessionId);
                    ps.setInt(2, Integer.parseInt(item.getTripId())); 
                    ps.setInt(3, item.getSeatID());
                    ps.setInt(4, Integer.parseInt(item.getCoachId())); 
                    ps.setInt(5, Integer.parseInt(item.getLegOriginStationId()));
                    ps.setInt(6, Integer.parseInt(item.getLegDestinationStationId()));
                    
                    totalDeletedCount += ps.executeUpdate();
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing ID for cart item during hold deletion: " + item + " - " + e.getMessage());
                    // Optionally continue or throw, depending on desired error handling
                }
            }
        }
        return totalDeletedCount;
    }

    @Override
    public boolean deleteHoldById(Connection conn, int holdId) throws SQLException {
        String sql = "DELETE FROM dbo.TemporarySeatHolds WHERE HoldID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, holdId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}
