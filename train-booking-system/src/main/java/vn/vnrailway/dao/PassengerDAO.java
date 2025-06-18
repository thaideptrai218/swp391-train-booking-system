package vn.vnrailway.dao;

import vn.vnrailway.model.Passenger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List; // For potential future methods

public interface PassengerDAO {

    /**
     * Inserts a new passenger record into the database.
     *
     * @param conn The database connection.
     * @param passenger The Passenger object to insert.
     * @return The generated PassengerID of the newly created passenger, or -1 if insertion failed.
     * @throws SQLException If a database access error occurs.
     */
    long insertPassenger(Connection conn, Passenger passenger) throws SQLException;

    /**
     * Finds a passenger by their ID.
     *
     * @param conn The database connection.
     * @param passengerId The ID of the passenger.
     * @return The Passenger object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Passenger findById(Connection conn, int passengerId) throws SQLException;

    /**
     * Deletes a passenger by their ID.
     *
     * @param conn The database connection.
     * @param passengerId The ID of the passenger to delete.
     * @return true if the deletion was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean deletePassengerById(Connection conn, int passengerId) throws SQLException;
    
    /**
     * Retrieves a list of passengers associated with a specific booking ID.
     * This might be useful if passengers are directly linked to bookings before tickets are made.
     * However, based on current plan, passengers are linked via tickets or session data.
     * This method is more for general purpose.
     *
     * @param conn The database connection.
     * @param bookingId The ID of the booking.
     * @return A list of Passenger objects.
     * @throws SQLException If a database access error occurs.
     */
    // List<Passenger> findByBookingId(Connection conn, long bookingId) throws SQLException; // If needed later

    // Potentially other methods like:
    // boolean updatePassenger(Connection conn, Passenger passenger) throws SQLException;
}
