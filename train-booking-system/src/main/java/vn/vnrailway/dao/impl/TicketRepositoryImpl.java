package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TicketRepository;
// import vn.vnrailway.dto.BookingTrendDTO; // Removed import
import vn.vnrailway.model.Ticket;

import java.sql.*;
// import java.util.Date; // Removed import
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class TicketRepositoryImpl implements TicketRepository {

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setTicketID(rs.getInt("TicketID"));
        ticket.setTicketCode(rs.getString("TicketCode"));
        ticket.setBookingID(rs.getInt("BookingID"));
        ticket.setTripID(rs.getInt("TripID"));
        ticket.setSeatID(rs.getInt("SeatID"));
        ticket.setPassengerID(rs.getInt("PassengerID"));
        ticket.setStartStationID(rs.getInt("StartStationID"));
        ticket.setEndStationID(rs.getInt("EndStationID"));
        ticket.setPrice(rs.getBigDecimal("Price"));
        ticket.setTicketStatus(rs.getString("TicketStatus"));
        ticket.setCoachNameSnapshot(rs.getString("CoachNameSnapshot"));
        ticket.setSeatNameSnapshot(rs.getString("SeatNameSnapshot"));
        ticket.setPassengerName(rs.getString("PassengerName"));
        ticket.setPassengerIDCardNumber(rs.getString("PassengerIDCardNumber"));
        ticket.setFareComponentDetails(rs.getString("FareComponentDetails"));

        int parentTicketIdVal = rs.getInt("ParentTicketID");
        if (rs.wasNull()) {
            ticket.setParentTicketID(null);
        } else {
            ticket.setParentTicketID(parentTicketIdVal);
        }
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(int ticketId) throws SQLException {
        String sql = "SELECT * FROM Tickets WHERE TicketID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTicket(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> findByTicketCode(String ticketCode) throws SQLException {
        String sql = "SELECT * FROM Tickets WHERE TicketCode = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTicket(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Ticket> findAll() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets ORDER BY TicketID DESC";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findByBookingId(int bookingId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE BookingID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findByTripId(int tripId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE TripID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findByPassengerId(int passengerId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE PassengerID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, passengerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    @Override
    public List<Ticket> findByTripIdAndSeatId(int tripId, int seatId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Tickets WHERE TripID = ? AND SeatID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ps.setInt(2, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapResultSetToTicket(rs));
                }
            }
        }
        return tickets;
    }

    @Override
    public Ticket save(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO Tickets (TicketCode, BookingID, TripID, SeatID, PassengerID, StartStationID, EndStationID, Price, TicketStatus, CoachNameSnapshot, SeatNameSnapshot, PassengerName, PassengerIDCardNumber, FareComponentDetails, ParentTicketID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, ticket.getTicketCode());
            ps.setInt(2, ticket.getBookingID());
            ps.setInt(3, ticket.getTripID());
            ps.setInt(4, ticket.getSeatID());
            ps.setInt(5, ticket.getPassengerID());
            ps.setInt(6, ticket.getStartStationID());
            ps.setInt(7, ticket.getEndStationID());
            ps.setBigDecimal(8, ticket.getPrice());
            ps.setString(9, ticket.getTicketStatus());
            ps.setString(10, ticket.getCoachNameSnapshot());
            ps.setString(11, ticket.getSeatNameSnapshot());
            ps.setString(12, ticket.getPassengerName());
            ps.setString(13, ticket.getPassengerIDCardNumber());
            ps.setString(14, ticket.getFareComponentDetails());
            if (ticket.getParentTicketID() != null) {
                ps.setInt(15, ticket.getParentTicketID());
            } else {
                ps.setNull(15, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating ticket failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ticket.setTicketID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating ticket failed, no ID obtained.");
                }
            }
        }
        return ticket;
    }

    @Override
    public boolean update(Ticket ticket) throws SQLException {
        String sql = "UPDATE Tickets SET TicketCode = ?, BookingID = ?, TripID = ?, SeatID = ?, PassengerID = ?, StartStationID = ?, EndStationID = ?, Price = ?, TicketStatus = ?, CoachNameSnapshot = ?, SeatNameSnapshot = ?, PassengerName = ?, PassengerIDCardNumber = ?, FareComponentDetails = ?, ParentTicketID = ? WHERE TicketID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticket.getTicketCode());
            ps.setInt(2, ticket.getBookingID());
            ps.setInt(3, ticket.getTripID());
            ps.setInt(4, ticket.getSeatID());
            ps.setInt(5, ticket.getPassengerID());
            ps.setInt(6, ticket.getStartStationID());
            ps.setInt(7, ticket.getEndStationID());
            ps.setBigDecimal(8, ticket.getPrice());
            ps.setString(9, ticket.getTicketStatus());
            ps.setString(10, ticket.getCoachNameSnapshot());
            ps.setString(11, ticket.getSeatNameSnapshot());
            ps.setString(12, ticket.getPassengerName());
            ps.setString(13, ticket.getPassengerIDCardNumber());
            ps.setString(14, ticket.getFareComponentDetails());
            if (ticket.getParentTicketID() != null) {
                ps.setInt(15, ticket.getParentTicketID());
            } else {
                ps.setNull(15, Types.INTEGER);
            }
            ps.setInt(16, ticket.getTicketID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int ticketId) throws SQLException {
        String sql = "DELETE FROM Tickets WHERE TicketID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public long getTotalTicketsSold() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Tickets WHERE TicketStatus NOT IN ('Cancelled', 'Void')";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    @Override
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(Price) FROM Tickets WHERE TicketStatus NOT IN ('Cancelled', 'Void')";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                BigDecimal totalRevenue = rs.getBigDecimal(1);
                return totalRevenue == null ? 0.0 : totalRevenue.doubleValue();
            }
        }
        return 0.0;
    }
    // Removed Trend Method Implementations
}
