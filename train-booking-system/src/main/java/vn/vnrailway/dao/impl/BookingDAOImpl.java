package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.BookingDAO;
import vn.vnrailway.model.Booking;
// import vn.vnrailway.config.DBContext; // If DBContext is used for connections

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp; // For DATETIME2
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOImpl implements BookingDAO {

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingID(rs.getInt("BookingID"));
        booking.setBookingCode(rs.getString("BookingCode"));
        booking.setUserID(rs.getInt("UserID"));
        
        Timestamp bookingDateTimeTs = rs.getTimestamp("BookingDateTime");
        if (bookingDateTimeTs != null) {
            booking.setBookingDateTime(bookingDateTimeTs.toLocalDateTime());
        }
        
        booking.setTotalPrice(rs.getBigDecimal("TotalPrice"));
        booking.setBookingStatus(rs.getString("BookingStatus"));
        booking.setPaymentStatus(rs.getString("PaymentStatus"));
        booking.setSource(rs.getString("Source"));
        
        Timestamp expiredAtTs = rs.getTimestamp("ExpiredAt");
        if (expiredAtTs != null) {
            booking.setExpiredAt(expiredAtTs.toLocalDateTime());
        }
        return booking;
    }

    @Override
    public long insertBooking(Connection conn, Booking booking) throws SQLException {
        String sql = "INSERT INTO dbo.Bookings (BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, booking.getBookingCode());
            ps.setInt(2, booking.getUserID()); 
            ps.setTimestamp(3, booking.getBookingDateTime() != null ? Timestamp.valueOf(booking.getBookingDateTime()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setBigDecimal(4, booking.getTotalPrice());
            ps.setString(5, booking.getBookingStatus());
            ps.setString(6, booking.getPaymentStatus());
            ps.setString(7, booking.getSource());
            ps.setTimestamp(8, booking.getExpiredAt() != null ? Timestamp.valueOf(booking.getExpiredAt()) : null);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // Return BookingID
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Booking findByBookingCode(Connection conn, String bookingCode) throws SQLException {
        String sql = "SELECT * FROM dbo.Bookings WHERE BookingCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public Booking findById(Connection conn, long bookingId) throws SQLException {
        String sql = "SELECT * FROM dbo.Bookings WHERE BookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateBookingStatus(Connection conn, String bookingCode, String bookingStatus, String paymentStatus, String notes) throws SQLException {
        // 'notes' parameter is not directly used in this SQL as Bookings table doesn't have a generic notes field.
        // It could be logged or used if a notes field is added to the table later.
        String sql = "UPDATE dbo.Bookings SET BookingStatus = ?, PaymentStatus = ? WHERE BookingCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingStatus);
            ps.setString(2, paymentStatus);
            ps.setString(3, bookingCode);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean updateBooking(Connection conn, Booking booking) throws SQLException {
        String sql = "UPDATE dbo.Bookings SET UserID = ?, BookingDateTime = ?, TotalPrice = ?, BookingStatus = ?, PaymentStatus = ?, Source = ?, ExpiredAt = ? WHERE BookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserID());
            ps.setTimestamp(2, Timestamp.valueOf(booking.getBookingDateTime()));
            ps.setBigDecimal(3, booking.getTotalPrice());
            ps.setString(4, booking.getBookingStatus());
            ps.setString(5, booking.getPaymentStatus());
            ps.setString(6, booking.getSource());
            ps.setTimestamp(7, booking.getExpiredAt() != null ? Timestamp.valueOf(booking.getExpiredAt()) : null);
            ps.setInt(8, booking.getBookingID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
}
