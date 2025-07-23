package vn.vnrailway.dao;

import vn.vnrailway.model.TemporaryVIPPurchase;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Temporary VIP Purchase data access operations.
 */
public interface TemporaryVIPPurchaseRepository {
    
    /**
     * Create a new temporary VIP purchase
     * @param tempVIPPurchase the temporary VIP purchase to create
     * @return the created temporary VIP purchase with generated ID
     * @throws SQLException if database error occurs
     */
    TemporaryVIPPurchase create(TemporaryVIPPurchase tempVIPPurchase) throws SQLException;
    
    /**
     * Find temporary VIP purchase by ID
     * @param tempVIPPurchaseID the temporary VIP purchase ID
     * @return Optional containing the temporary VIP purchase if found
     * @throws SQLException if database error occurs
     */
    Optional<TemporaryVIPPurchase> findById(int tempVIPPurchaseID) throws SQLException;
    
    /**
     * Find temporary VIP purchase by session ID
     * @param sessionID the session ID
     * @return Optional containing the temporary VIP purchase if found
     * @throws SQLException if database error occurs
     */
    Optional<TemporaryVIPPurchase> findBySessionId(String sessionID) throws SQLException;
    
    /**
     * Find temporary VIP purchase by user ID (most recent)
     * @param userID the user ID
     * @return Optional containing the temporary VIP purchase if found
     * @throws SQLException if database error occurs
     */
    Optional<TemporaryVIPPurchase> findByUserId(int userID) throws SQLException;
    
    /**
     * Update expiry time of a temporary VIP purchase
     * @param tempVIPPurchaseID the temporary VIP purchase ID
     * @param newExpiryMinutes new expiry time in minutes from now
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean updateExpiry(int tempVIPPurchaseID, int newExpiryMinutes) throws SQLException;
    
    /**
     * Delete a temporary VIP purchase by ID
     * @param tempVIPPurchaseID the temporary VIP purchase ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean delete(int tempVIPPurchaseID) throws SQLException;
    
    /**
     * Delete temporary VIP purchase by session ID
     * @param sessionID the session ID
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean deleteBySessionId(String sessionID) throws SQLException;
    
    /**
     * Find all expired temporary VIP purchases
     * @return List of expired temporary VIP purchases
     * @throws SQLException if database error occurs
     */
    List<TemporaryVIPPurchase> findExpired() throws SQLException;
    
    /**
     * Clean up expired temporary VIP purchases
     * @return number of records cleaned up
     * @throws SQLException if database error occurs
     */
    int cleanupExpired() throws SQLException;
    
    /**
     * Check if a temporary VIP purchase exists and is valid for a session
     * @param sessionID the session ID
     * @return true if valid temporary VIP purchase exists, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean existsValidBySessionId(String sessionID) throws SQLException;
}