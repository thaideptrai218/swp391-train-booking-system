package vn.vnrailway.dao;

import vn.vnrailway.model.VIPCardType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for VIP Card Type data access operations.
 */
public interface VIPCardTypeRepository {
    
    /**
     * Find all VIP card types
     * @return List of all VIP card types
     * @throws SQLException if database error occurs
     */
    List<VIPCardType> findAll() throws SQLException;
    
    /**
     * Find VIP card type by ID
     * @param vipCardTypeID the VIP card type ID
     * @return Optional containing the VIP card type if found
     * @throws SQLException if database error occurs
     */
    Optional<VIPCardType> findById(int vipCardTypeID) throws SQLException;
    
    /**
     * Find VIP card type by name
     * @param typeName the type name to search for
     * @return Optional containing the VIP card type if found
     * @throws SQLException if database error occurs
     */
    Optional<VIPCardType> findByTypeName(String typeName) throws SQLException;
    
    /**
     * Create a new VIP card type
     * @param vipCardType the VIP card type to create
     * @return the created VIP card type with generated ID
     * @throws SQLException if database error occurs
     */
    VIPCardType create(VIPCardType vipCardType) throws SQLException;
    
    /**
     * Update an existing VIP card type
     * @param vipCardType the VIP card type to update
     * @return true if update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean update(VIPCardType vipCardType) throws SQLException;
    
    /**
     * Delete a VIP card type by ID
     * @param vipCardTypeID the VIP card type ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean delete(int vipCardTypeID) throws SQLException;
    
    /**
     * Check if a VIP card type exists by ID
     * @param vipCardTypeID the VIP card type ID to check
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    boolean existsById(int vipCardTypeID) throws SQLException;
    
    /**
     * Find VIP card types within a price range
     * @param minPrice minimum price
     * @param maxPrice maximum price
     * @return List of VIP card types within the price range
     * @throws SQLException if database error occurs
     */
    List<VIPCardType> findByPriceRange(double minPrice, double maxPrice) throws SQLException;
    
    /**
     * Find VIP card types by duration
     * @param durationMonths the duration in months
     * @return List of VIP card types with the specified duration
     * @throws SQLException if database error occurs
     */
    List<VIPCardType> findByDuration(int durationMonths) throws SQLException;
}