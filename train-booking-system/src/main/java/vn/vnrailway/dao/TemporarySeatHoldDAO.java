package vn.vnrailway.dao;

import vn.vnrailway.model.TemporarySeatHold;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface TemporarySeatHoldDAO {

    /**
     * Finds an active hold for a specific seat, leg, and session.
     *
     * @param conn The database connection.
     * @param tripId The ID of the trip.
     * @param seatId The ID of the seat.
     * @param legOriginStationId The origin station ID of the leg.
     * @param legDestinationStationId The destination station ID of the leg.
     * @param sessionId The session ID of the user.
     * @return TemporarySeatHold object if an active hold is found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    TemporarySeatHold findActiveHoldBySessionAndLeg(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException;

    /**
     * Refreshes the expiry time of an existing hold.
     *
     * @param conn The database connection.
     * @param holdId The ID of the hold to refresh.
     * @param newExpiresAt The new expiry timestamp.
     * @return true if the hold was successfully refreshed, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean refreshHoldExpiry(Connection conn, int holdId, Date newExpiresAt) throws SQLException;

    /**
     * Adds a new temporary seat hold to the database.
     *
     * @param conn The database connection.
     * @param holdData The TemporarySeatHold object containing data for the new hold.
     * @return true if the hold was successfully added, false otherwise.
     * @throws SQLException If a database access error occurs (e.g., unique constraint violation).
     */
    boolean addHold(Connection conn, TemporarySeatHold holdData) throws SQLException;

    /**
     * Releases/deletes a specific active hold for a given seat, leg, and session.
     *
     * @param conn The database connection.
     * @param tripId The ID of the trip.
     * @param seatId The ID of the seat.
     * @param legOriginStationId The origin station ID of the leg.
     * @param legDestinationStationId The destination station ID of the leg.
     * @param sessionId The session ID of the user whose hold is to be released.
     * @return true if a hold was successfully released, false otherwise (e.g., no such active hold for the session).
     * @throws SQLException If a database access error occurs.
     */
    boolean releaseHold(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId, String sessionId) throws SQLException;

    /**
     * Releases/deletes all holds associated with a specific session ID.
     * Useful for cleanup when a session ends or booking is completed/cancelled.
     *
     * @param conn The database connection.
     * @param sessionId The session ID for which to release all holds.
     * @return The number of holds released.
     * @throws SQLException If a database access error occurs.
     */
    int releaseAllHoldsForSession(Connection conn, String sessionId) throws SQLException;
    
    /**
     * Deletes all temporary seat holds that have expired.
     * Intended to be called by a background cleanup job.
     *
     * @param conn The database connection.
     * @return The number of expired holds deleted.
     * @throws SQLException If a database access error occurs.
     */
    int releaseExpiredHolds(Connection conn) throws SQLException;
}
