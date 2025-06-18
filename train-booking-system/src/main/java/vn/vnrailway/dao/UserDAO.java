package vn.vnrailway.dao;

import vn.vnrailway.model.User;
import vn.vnrailway.dto.payment.CustomerDetailsDto; // For creating/updating user from DTO
import java.sql.Connection;
import java.sql.SQLException;

public interface UserDAO {

    /**
     * Finds a user by their email address.
     *
     * @param conn the database connection
     * @param email The email address to search for.
     * @return The User object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    User findByEmail(Connection conn, String email) throws SQLException;

    /**
     * Creates a new user in the database.
     *
     * @param conn the database connection
     * @param user The User object to create.
     * @return The generated UserID of the newly created user, or -1 if creation failed.
     * @throws SQLException If a database access error occurs.
     */
    long insertUser(Connection conn, User user) throws SQLException;
    
    /**
     * Updates an existing user's details.
     *
     * @param conn the database connection
     * @param user The User object with updated details.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateUser(Connection conn, User user) throws SQLException;

    /**
     * Finds a user by ID.
     * @param conn the database connection
     * @param userId The ID of the user.
     * @return User object if found, null otherwise.
     * @throws SQLException
     */
    User findById(Connection conn, long userId) throws SQLException;

    /**
     * Helper to find or create a user based on customer details from payment payload.
     * This method might encapsulate findByEmail, insertUser, and potentially updateUser.
     *
     * @param conn the database connection
     * @param customerDetails The customer details from the payment payload.
     * @return The User object (either existing or newly created).
     * @throws SQLException If a database access error occurs.
     */
    User findOrCreateUser(Connection conn, CustomerDetailsDto customerDetails) throws SQLException;

    /**
     * Checks if an email already exists in the database.
     * @param conn the database connection
     * @param email The email to check.
     * @return true if the email exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean checkEmailExists(Connection conn, String email) throws SQLException;

    /**
     * Updates the password for a user identified by email.
     * @param conn the database connection
     * @param email The email of the user whose password is to be updated.
     * @param newPasswordHash The new hashed password.
     * @return true if the password was updated successfully, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updatePassword(Connection conn, String email, String newPasswordHash) throws SQLException;
}
