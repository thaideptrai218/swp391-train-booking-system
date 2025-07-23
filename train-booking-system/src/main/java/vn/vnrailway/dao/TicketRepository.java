package vn.vnrailway.dao;

import vn.vnrailway.dto.CheckInforRefundTicketDTO;
import vn.vnrailway.dto.InfoPassengerDTO;
import vn.vnrailway.dto.RefundRequestDTO;
import vn.vnrailway.model.Ticket;
// import vn.vnrailway.dto.BookingTrendDTO; // Removed import
import java.sql.SQLException;
// import java.util.Date; // Removed import
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
    List<RefundRequestDTO> getAllPendingRefundRequests() throws SQLException;

    CheckInforRefundTicketDTO findInformationRefundTicketDetailsByCode(String bookingCode, String phoneNumber, String email) throws SQLException;

    Ticket save(Ticket ticket) throws SQLException;

    InfoPassengerDTO findTicketByTicketCode(String ticketCode) throws SQLException; // For checking ticket status

    boolean update(Ticket ticket) throws SQLException; // Mainly for status changes

    boolean deleteById(int ticketId) throws SQLException; // Usually not deleted, but status changed (e.g., "Cancelled")

    long getTotalTicketsSold() throws SQLException;

    double getTotalRevenue() throws SQLException;
    
    void insertTempRefundRequests(String ticketCode) throws SQLException; // Insert temporary refund requests

    void rejectRefundTicket(String ticketInfo) throws SQLException;

    void approveRefundTicket(String ticketInfo) throws SQLException;

    List<InfoPassengerDTO> findListTicketBooking(String id) throws SQLException;
}