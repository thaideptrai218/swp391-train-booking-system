package vn.vnrailway.dao;

import vn.vnrailway.model.TemporarySeatHold;
import vn.vnrailway.dto.payment.OriginalCartItemDataDto; // For matching criteria

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TemporarySeatHoldDAO {

    /**
     * Inserts a new temporary seat hold into the database.
     * (This might be handled by a different part of the application, not directly by payment servlets)
     * @param conn The database connection.
     * @param hold The TemporarySeatHold object to insert.
     * @return The generated HoldID or -1 if failed.
     * @throws SQLException If a database access error occurs.
     */
    long addHold(Connection conn, TemporarySeatHold hold) throws SQLException;

    /**
     * Finds an active temporary seat hold based on session, seat, and leg details.
     *
     * @param conn The database connection.
     * @param sessionId The HTTP session ID.
     * @param tripId The ID of the trip.
     * @param seatId The ID of the seat.
     * @param coachId The ID of the coach.
     * @param legOriginStationId The origin station ID for the leg.
     * @param legDestinationStationId The destination station ID for the leg.
     * @return The TemporarySeatHold object if a valid, non-expired hold exists, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    TemporarySeatHold findActiveHoldBySessionAndLeg(Connection conn, String sessionId, int tripId, int seatId, int coachId, int legOriginStationId, int legDestinationStationId) throws SQLException;
    
    /**
     * Deletes temporary seat holds associated with a list of cart items for a given session.
     * Typically called after successful booking or if payment fails/user cancels.
     *
     * @param conn The database connection.
     * @param sessionId The HTTP session ID.
     * @param itemsToClear List of OriginalCartItemDataDto representing seats to release.
     * @return The number of holds successfully deleted.
     * @throws SQLException If a database access error occurs.
     */
    int deleteHoldsForCartItems(Connection conn, String sessionId, List<OriginalCartItemDataDto> itemsToClear) throws SQLException;

    /**
     * Deletes all expired temporary seat holds from the database.
     * Useful for a background cleanup job.
     *
     * @param conn The database connection.
     * @return The number of expired holds deleted.
     * @throws SQLException If a database access error occurs.
     */
    int releaseExpiredHolds(Connection conn) throws SQLException; // Renamed from deleteExpiredHolds
    
    /**
     * Deletes a specific hold by its ID.
     * @param conn The database connection.
     * @param holdId The ID of the hold to delete.
     * @return true if deletion was successful, false otherwise.
     * @throws SQLException
     */
    boolean deleteHoldById(Connection conn, int holdId) throws SQLException; // New method from original interface plan

    /**
     * Refreshes the expiry time of an existing hold.
     * @param conn The database connection.
     * @param holdId The ID of the hold to refresh.
     * @param newExpiresAt The new expiry timestamp.
     * @return true if successful, false otherwise.
     * @throws SQLException
     */
    boolean refreshHoldExpiry(Connection conn, int holdId, java.util.Date newExpiresAt) throws SQLException; // From Impl

    /**
     * Releases/deletes a specific hold.
     * @param conn The database connection.
     * @param tripId Trip ID.
     * @param seatId Seat ID.
     * @param legOriginStationId Origin station ID of the leg.
     * @param legDestinationStationId Destination station ID of the leg.
     * @param sessionId Session ID.
     * @return true if successful, false otherwise.
     * @throws SQLException
     */
    boolean releaseHold(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException; // From Impl

    /**
     * Releases/deletes all active holds for a given session.
     * @param conn The database connection.
     * @param sessionId Session ID.
     * @return The number of holds released.
     * @throws SQLException
     */
    int releaseAllHoldsForSession(Connection conn, String sessionId) throws SQLException; // From Impl

    // Potentially:
    // List<TemporarySeatHold> findHoldsBySessionId(Connection conn, String sessionId) throws SQLException;
}
