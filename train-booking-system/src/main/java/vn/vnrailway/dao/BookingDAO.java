package vn.vnrailway.dao;

import vn.vnrailway.model.Booking;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List; // For potential future methods like findBookingsByUser

public interface BookingDAO {

    /**
     * Inserts a new booking record into the database.
     *
     * @param conn The database connection.
     * @param booking The Booking object to insert.
     * @return The generated BookingID of the newly created booking, or -1 if insertion failed.
     * @throws SQLException If a database access error occurs.
     */
    long insertBooking(Connection conn, Booking booking) throws SQLException;

    /**
     * Finds a booking by its unique booking code.
     *
     * @param conn The database connection.
     * @param bookingCode The booking code to search for.
     * @return The Booking object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Booking findByBookingCode(Connection conn, String bookingCode) throws SQLException;
    
    /**
     * Finds a booking by its primary key ID.
     *
     * @param conn The database connection.
     * @param bookingId The ID of the booking.
     * @return The Booking object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Booking findById(Connection conn, long bookingId) throws SQLException;

    /**
     * Updates the status of an existing booking.
     * Could be expanded to update other fields as well.
     *
     * @param conn The database connection.
     * @param bookingCode The booking code of the booking to update.
     * @param bookingStatus The new booking status (e.g., "Confirmed", "Cancelled").
     * @param paymentStatus The new payment status (e.g., "Paid", "Failed").
     * @param notes Optional notes for the update.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateBookingStatus(Connection conn, String bookingCode, String bookingStatus, String paymentStatus, String notes) throws SQLException;
    
    /**
     * Updates an entire booking object.
     * @param conn The database connection.
     * @param booking The booking object with updated information.
     * @return true if update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateBooking(Connection conn, Booking booking) throws SQLException;

    // Potentially other methods like:
    // List<Booking> findByUserId(Connection conn, long userId) throws SQLException;
    // List<Booking> findPendingBookingsPastExpiry(Connection conn) throws SQLException;
}
