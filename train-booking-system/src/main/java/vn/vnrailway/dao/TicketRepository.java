package vn.vnrailway.dao;

import vn.vnrailway.model.Ticket;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findById(int ticketId) throws SQLException;

    Optional<Ticket> findByTicketCode(String ticketCode) throws SQLException;

    List<Ticket> findAll() throws SQLException; // Use with caution, can be very large

    List<Ticket> findByBookingId(int bookingId) throws SQLException;

    List<Ticket> findByTripId(int tripId) throws SQLException;

    List<Ticket> findByPassengerId(int passengerId) throws SQLException;

    List<Ticket> findByTripIdAndSeatId(int tripId, int seatId) throws SQLException; // Check if a seat on a trip is
                                                                                    // booked

    Ticket save(Ticket ticket) throws SQLException;

    boolean update(Ticket ticket) throws SQLException; // Mainly for status changes

    boolean deleteById(int ticketId) throws SQLException; // Usually not deleted, but status changed (e.g., "Cancelled")

    long getTotalTicketsSold() throws SQLException;

    double getTotalRevenue() throws SQLException;
}