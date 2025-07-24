package vn.vnrailway.dao;

import vn.vnrailway.model.UserVIPCard;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User VIP Card data access operations.
 */
public interface UserVIPCardRepository {
    
    /**
     * Create a new user VIP card
     * @param userVIPCard the user VIP card to create
     * @return the created user VIP card with generated ID
     * @throws SQLException if database error occurs
     */
    UserVIPCard create(UserVIPCard userVIPCard) throws SQLException;
    
    /**
     * Find user VIP card by ID
     * @param userVIPCardID the user VIP card ID
     * @return Optional containing the user VIP card if found
     * @throws SQLException if database error occurs
     */
    Optional<UserVIPCard> findById(int userVIPCardID) throws SQLException;
    
    /**
     * Find active VIP card for a user
     * @param userID the user ID
     * @return Optional containing the active VIP card if found
     * @throws SQLException if database error occurs
     */
    Optional<UserVIPCard> findActiveByUserId(int userID) throws SQLException;
    
    /**
     * Find all VIP cards for a user (active and inactive)
     * @param userID the user ID
     * @return List of user VIP cards
     * @throws SQLException if database error occurs
     */
    List<UserVIPCard> findAllByUserId(int userID) throws SQLException;
    
    /**
     * Find user VIP card by transaction reference
     * @param transactionReference the transaction reference
     * @return Optional containing the user VIP card if found
     * @throws SQLException if database error occurs
     */
    Optional<UserVIPCard> findByTransactionReference(String transactionReference) throws SQLException;
    
    /**
     * Update VIP card status (active/inactive)
     * @param userVIPCardID the user VIP card ID
     * @param isActive the new active status
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean updateStatus(int userVIPCardID, boolean isActive) throws SQLException;
    
    /**
     * Deactivate all VIP cards for a user (before creating new one)
     * @param userID the user ID
     * @return number of cards deactivated
     * @throws SQLException if database error occurs
     */
    int deactivateAllForUser(int userID) throws SQLException;
    
    /**
     * Find all expired VIP cards
     * @return List of expired VIP cards
     * @throws SQLException if database error occurs
     */
    List<UserVIPCard> findExpired() throws SQLException;
    
    /**
     * Deactivate expired VIP cards
     * @return number of cards deactivated
     * @throws SQLException if database error occurs
     */
    int deactivateExpired() throws SQLException;
    
    /**
     * Check if user has an active VIP card
     * @param userID the user ID
     * @return true if user has active VIP card, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean hasActiveVIPCard(int userID) throws SQLException;
    
    /**
     * Get VIP discount percentage for a user
     * @param userID the user ID
     * @return discount percentage (0 if no active VIP card)
     * @throws SQLException if database error occurs
     */
    double getVIPDiscountPercentage(int userID) throws SQLException;
    
    /**
     * Find VIP cards expiring within specified days
     * @param daysFromNow number of days from now
     * @return List of VIP cards expiring soon
     * @throws SQLException if database error occurs
     */
    List<UserVIPCard> findExpiringSoon(int daysFromNow) throws SQLException;
}