package vn.vnrailway.dao;

import vn.vnrailway.model.Ticket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TicketDAO {

    /**
     * Inserts a new ticket record into the database.
     *
     * @param conn The database connection.
     * @param ticket The Ticket object to insert.
     * @return The generated TicketID of the newly created ticket, or -1 if insertion failed.
     * @throws SQLException If a database access error occurs.
     */
    long insertTicket(Connection conn, Ticket ticket) throws SQLException;

    /**
     * Finds a ticket by its ID.
     *
     * @param conn The database connection.
     * @param ticketId The ID of the ticket.
     * @return The Ticket object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Ticket findById(Connection conn, int ticketId) throws SQLException;
    
    /**
     * Finds a ticket by its unique ticket code.
     *
     * @param conn The database connection.
     * @param ticketCode The unique code of the ticket.
     * @return The Ticket object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    Ticket findByTicketCode(Connection conn, String ticketCode) throws SQLException;

    /**
     * Retrieves all tickets associated with a specific booking ID.
     *
     * @param conn The database connection.
     * @param bookingId The ID of the booking.
     * @return A list of Ticket objects.
     * @throws SQLException If a database access error occurs.
     */
    List<Ticket> findByBookingId(Connection conn, int bookingId) throws SQLException;

    /**
     * Updates the status of an existing ticket.
     *
     * @param conn The database connection.
     * @param ticketId The ID of the ticket to update.
     * @param newStatus The new status (e.g., "Used", "Cancelled").
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    boolean updateTicketStatus(Connection conn, int ticketId, String newStatus) throws SQLException;
    
    /**
     * Updates an entire Ticket object.
     * @param conn The database connection.
     * @param ticket The Ticket object with updated information.
     * @return true if the update was successful, false otherwise.
     * @throws SQLException
     */
    boolean updateTicket(Connection conn, Ticket ticket) throws SQLException;

    // Potentially other methods like:
    // List<Ticket> findByPassengerId(Connection conn, int passengerId) throws SQLException;
}
