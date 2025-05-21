package vn.vnrailway.dao;

import vn.vnrailway.model.Booking;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Optional<Booking> findById(int bookingId) throws SQLException;
    Optional<Booking> findByBookingCode(String bookingCode) throws SQLException;
    List<Booking> findAll() throws SQLException; // Consider pagination for admin views
    List<Booking> findByUserId(int userId) throws SQLException;
    List<Booking> findByStatus(String bookingStatus) throws SQLException;
    List<Booking> findByPaymentStatus(String paymentStatus) throws SQLException;
    List<Booking> findByBookingDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    Booking save(Booking booking) throws SQLException;
    boolean update(Booking booking) throws SQLException; // Typically for status updates
    boolean deleteById(int bookingId) throws SQLException; // Use with caution, often bookings are cancelled not deleted
}
