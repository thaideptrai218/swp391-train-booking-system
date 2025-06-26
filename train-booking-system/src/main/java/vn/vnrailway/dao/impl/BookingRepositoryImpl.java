package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.BookingRepository;
import vn.vnrailway.dto.CheckBookingDTO;
import vn.vnrailway.dto.InfoPassengerDTO;
import vn.vnrailway.model.Booking;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookingRepositoryImpl implements BookingRepository {

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingID(rs.getInt("BookingID"));
        booking.setBookingCode(rs.getString("BookingCode"));
        booking.setUserID(rs.getInt("UserID"));

        Timestamp bookingDateTimeTimestamp = rs.getTimestamp("BookingDateTime");
        if (bookingDateTimeTimestamp != null) {
            booking.setBookingDateTime(bookingDateTimeTimestamp.toLocalDateTime());
        }

        booking.setTotalPrice(rs.getBigDecimal("TotalPrice"));
        booking.setBookingStatus(rs.getString("BookingStatus"));
        booking.setPaymentStatus(rs.getString("PaymentStatus"));
        booking.setSource(rs.getString("Source"));

        Timestamp expiredAtTimestamp = rs.getTimestamp("ExpiredAt");
        if (expiredAtTimestamp != null) {
            booking.setExpiredAt(expiredAtTimestamp.toLocalDateTime());
        }
        return booking;
    }

    @Override
    public Optional<Booking> findById(int bookingId) throws SQLException {
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE BookingID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Booking> findByBookingCode(String bookingCode) throws SQLException {
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE BookingCode = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBooking(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE UserID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByStatus(String bookingStatus) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE BookingStatus = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByPaymentStatus(String paymentStatus) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE PaymentStatus = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    @Override
    public List<Booking> findByBookingDateRange(LocalDateTime startDate, LocalDateTime endDate) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT BookingID, BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt FROM Bookings WHERE BookingDateTime >= ? AND BookingDateTime <= ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        }
        return bookings;
    }

    @Override
    public Booking save(Booking booking) throws SQLException {
        String sql = "INSERT INTO Bookings (BookingCode, UserID, BookingDateTime, TotalPrice, BookingStatus, PaymentStatus, Source, ExpiredAt) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, booking.getBookingCode());
            ps.setInt(2, booking.getUserID());
            ps.setTimestamp(3, Timestamp.valueOf(
                    booking.getBookingDateTime() != null ? booking.getBookingDateTime() : LocalDateTime.now()));
            ps.setBigDecimal(4, booking.getTotalPrice());
            ps.setString(5, booking.getBookingStatus());
            ps.setString(6, booking.getPaymentStatus());
            ps.setString(7, booking.getSource());
            if (booking.getExpiredAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(booking.getExpiredAt()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating booking failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setBookingID(generatedKeys.getInt(1));
                } else {
                    System.err.println(
                            "Creating booking succeeded, but no ID was obtained. BookingID might not be auto-generated or configured to be returned.");
                }
            }
        }
        return booking;
    }

    @Override
    public boolean update(Booking booking) throws SQLException {
        String sql = "UPDATE Bookings SET BookingCode = ?, UserID = ?, BookingDateTime = ?, TotalPrice = ?, BookingStatus = ?, PaymentStatus = ?, Source = ?, ExpiredAt = ? WHERE BookingID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, booking.getBookingCode());
            ps.setInt(2, booking.getUserID());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getBookingDateTime()));
            ps.setBigDecimal(4, booking.getTotalPrice());
            ps.setString(5, booking.getBookingStatus());
            ps.setString(6, booking.getPaymentStatus());
            ps.setString(7, booking.getSource());
            if (booking.getExpiredAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(booking.getExpiredAt()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            ps.setInt(9, booking.getBookingID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int bookingId) throws SQLException {
        String sql = "DELETE FROM Bookings WHERE BookingID = ?";
        // Consider business rules: usually bookings are cancelled (status update)
        // rather than deleted.
        // If hard delete is required:
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookingId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public CheckBookingDTO findBookingDetailsByCode(String bookingCode, String phoneNumber, String email)
            throws SQLException {
        String sql = "SELECT \r\n" +
                "    TK.TicketCode,\r\n" + //
                "    P.FullName AS PassengerFullName,\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,\r\n" + //
                "    PT.TypeName AS PassengerType,\r\n" + //
                "\tST.TypeName AS SeatTypeName,\r\n" + //
                "    S.SeatNumber AS SeatNumber,\r\n" + //
                "    C.CoachName AS CoachName,\r\n" + //
                "    T.TrainName AS TrainName,\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,\r\n" + //
                "    TK.TicketStatus,\r\n" + //
                "    TK.Price,\r\n" + //
                "\tStartStation.StationName AS StartStationName,\r\n" + //
                "    EndStation.StationName AS EndStationName,\r\n" + //
                "\r\n" + //
                "\tu.FullName, u.Email, u.IDCardNumber, u.PhoneNumber\r\n" + //
                "\r\n" + //
                "FROM Tickets TK\r\n" + //
                "JOIN Bookings B ON TK.BookingID = B.BookingID\r\n" + //
                "JOIN Users u ON B.UserID = u.UserID\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID\r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID\r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TR ON TK.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TR.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID\r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID\r\n" + //
                "WHERE B.BookingCode = ? AND (u.PhoneNumber = ? OR u.Email = ?)";


        CheckBookingDTO checkBookingDTO = null;
        List<InfoPassengerDTO> passengers = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            ps.setString(2, phoneNumber);
            ps.setString(3, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (checkBookingDTO == null) {
                        checkBookingDTO = new CheckBookingDTO();
                        checkBookingDTO.setBookingCode(bookingCode);
                        checkBookingDTO.setUserFullName(rs.getString("FullName"));
                        checkBookingDTO.setUserEmail(rs.getString("Email"));
                        checkBookingDTO.setUserIDCardNumber(rs.getString("IDCardNumber"));
                        checkBookingDTO.setUserPhoneNumber(rs.getString("PhoneNumber"));
                    }

                    InfoPassengerDTO passenger = new InfoPassengerDTO();
                    passenger.setTicketCode(rs.getString("TicketCode"));
                    passenger.setPassengerFullName(rs.getString("PassengerFullName"));
                    passenger.setPassengerIDCard(rs.getString("PassengerIDCard"));
                    passenger.setPassengerType(rs.getString("PassengerType"));
                    passenger.setSeatTypeName(rs.getString("SeatTypeName"));
                    passenger.setSeatNumber(rs.getString("SeatNumber"));
                    passenger.setCoachName(rs.getString("CoachName"));
                    passenger.setTrainName(rs.getString("TrainName"));
                    Timestamp ts = rs.getTimestamp("ScheduledDepartureTime");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String formattedDateTime = sdf.format(ts); // Kết quả: "2025-06-10 08:00"
                    passenger.setScheduledDepartureTime(formattedDateTime);
                    passenger.setTicketStatus(rs.getString("TicketStatus"));
                    passenger.setPrice(rs.getDouble("Price"));
                    passenger.setStartStationName(rs.getString("StartStationName"));
                    passenger.setEndStationName(rs.getString("EndStationName"));

                    passengers.add(passenger);
                }
            }
        }

        if (checkBookingDTO != null) {
            checkBookingDTO.setInfoPassengers(passengers);
        }
        return checkBookingDTO;
    }

    @Override
    public List<String> findTicketCodesByBookingCode(String bookingCode) throws SQLException {
        List<String> ticketCodes = new ArrayList<>();
        String sql = "SELECT T.TicketCode FROM Tickets T JOIN Bookings B ON T.BookingID = B.BookingID WHERE B.BookingCode = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ticketCodes.add(rs.getString("TicketCode"));
                }
            }
        }
        return ticketCodes;
    }

    // Main method for testing
    public static void main(String[] args) {
        BookingRepository bookingRepository = new BookingRepositoryImpl();
        try {
            // // Test findAll
            // System.out.println("Testing findAll bookings:");
            // List<Booking> bookings = bookingRepository.findAll();
            // if (bookings.isEmpty()) {
            // System.out.println("No bookings found.");
            // } else {
            // bookings.forEach(b -> System.out.println(b));
            // }

            // // Test findById (assuming booking with ID 1 exists)
            // int testBookingId = 1; // Ensure this ID exists in your DB
            // System.out.println("\nTesting findById for booking ID: " + testBookingId);
            // Optional<Booking> bookingOpt = bookingRepository.findById(testBookingId);
            // bookingOpt.ifPresentOrElse(
            // b -> System.out.println("Found booking: " + b),
            // () -> System.out.println("Booking with ID " + testBookingId + " not
            // found."));

            CheckBookingDTO checkBookingDTO = bookingRepository.findBookingDetailsByCode(
                    "VNR1750245599712", "0987300269", "customer.an@example.com");
            if (checkBookingDTO != null) {
                System.out.println("Booking details found:");
                System.out.println("User Full Name: " + checkBookingDTO.getUserFullName());
                System.out.println("User Email: " + checkBookingDTO.getUserEmail());
                System.out.println("User ID Card Number: " + checkBookingDTO.getUserIDCardNumber());
                System.out.println("User Phone Number: " + checkBookingDTO.getUserPhoneNumber());
                System.out.println("Passengers Info:");
                for (InfoPassengerDTO passenger : checkBookingDTO.getInfoPassengers()) {
                    System.out.println(passenger);
                }
            } else {
                System.out.println("No booking found with the provided code.");
            }
            // Example of saving a new booking (uncomment and modify to test)
            /*
             * System.out.println("\nTesting save new booking:");
             * // Ensure UserID 1 exists for this test
             * Booking newBooking = new Booking();
             * newBooking.setBookingCode("TESTBK001_MAIN");
             * newBooking.setUserID(1);
             * newBooking.setBookingDateTime(LocalDateTime.now());
             * newBooking.setTotalPrice(new java.math.BigDecimal("250000.00"));
             * newBooking.setBookingStatus("Confirmed");
             * newBooking.setPaymentStatus("Paid");
             * newBooking.setSource("WebApp_MainTest");
             * // newBooking.setExpiredAt(LocalDateTime.now().plusHours(24)); // Optional
             * 
             * Booking savedBooking = bookingRepository.save(newBooking);
             * System.out.println("Saved booking: " + savedBooking);
             * 
             * if (savedBooking.getBookingID() > 0) {
             * // Example of updating the booking
             * System.out.println("\nTesting update booking ID: " +
             * savedBooking.getBookingID());
             * savedBooking.setPaymentStatus("Refunded");
             * boolean updated = bookingRepository.update(savedBooking);
             * System.out.println("Update successful: " + updated);
             * 
             * Optional<Booking> updatedBookingOpt =
             * bookingRepository.findById(savedBooking.getBookingID());
             * updatedBookingOpt.ifPresent(b ->
             * System.out.println("Updated booking details: " + b));
             * 
             * // Example of deleting the booking
             * System.out.println("\nTesting delete booking ID: " +
             * savedBooking.getBookingID());
             * boolean deleted = bookingRepository.deleteById(savedBooking.getBookingID());
             * System.out.println("Delete successful: " + deleted);
             * }
             */

        } catch (SQLException e) {
            System.err.println("Error testing BookingRepository: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
