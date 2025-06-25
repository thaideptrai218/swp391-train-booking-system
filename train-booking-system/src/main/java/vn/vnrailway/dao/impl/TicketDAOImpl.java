package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.TicketDAO;
import vn.vnrailway.model.Ticket;
// import vn.vnrailway.config.DBContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types; // For setting NULL for Integer
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setTicketID(rs.getInt("TicketID"));
        t.setTicketCode(rs.getString("TicketCode"));
        t.setBookingID(rs.getInt("BookingID"));
        t.setTripID(rs.getInt("TripID"));
        t.setSeatID(rs.getInt("SeatID"));
        t.setPassengerID(rs.getInt("PassengerID"));
        t.setStartStationID(rs.getInt("StartStationID"));
        t.setEndStationID(rs.getInt("EndStationID"));
        t.setPrice(rs.getBigDecimal("Price"));
        t.setTicketStatus(rs.getString("TicketStatus"));
        t.setCoachNameSnapshot(rs.getString("CoachNameSnapshot"));
        t.setSeatNameSnapshot(rs.getString("SeatNameSnapshot"));
        t.setPassengerName(rs.getString("PassengerNameSnapshot")); // DB to Model
        t.setPassengerIDCardNumber(rs.getString("PassengerIDCardNumberSnapshot")); // DB to Model
        t.setFareComponentDetails(rs.getString("FareComponentDetails"));
        
        int parentTicketId = rs.getInt("ParentTicketID");
        if (rs.wasNull()) {
            t.setParentTicketID(null);
        } else {
            t.setParentTicketID(parentTicketId);
        }
        return t;
    }

    @Override
    public long insertTicket(Connection conn, Ticket ticket) throws SQLException {
        String sql = "INSERT INTO dbo.Tickets (TicketCode, BookingID, TripID, SeatID, PassengerID, StartStationID, EndStationID, Price, TicketStatus, CoachNameSnapshot, SeatNameSnapshot, PassengerNameSnapshot, PassengerIDCardNumberSnapshot, FareComponentDetails, ParentTicketID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            ps.setString(12, ticket.getPassengerName()); // Model to DB
            ps.setString(13, ticket.getPassengerIDCardNumber()); // Model to DB
            ps.setString(14, ticket.getFareComponentDetails());
            
            if (ticket.getParentTicketID() != null) {
                ps.setInt(15, ticket.getParentTicketID());
            } else {
                ps.setNull(15, Types.INTEGER);
            }
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1); // TicketID
                    }
                }
            }
        }
        return -1;
    }

    @Override
    public Ticket findById(Connection conn, int ticketId) throws SQLException {
        String sql = "SELECT * FROM dbo.Tickets WHERE TicketID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Ticket findByTicketCode(Connection conn, String ticketCode) throws SQLException {
        String sql = "SELECT * FROM dbo.Tickets WHERE TicketCode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Ticket> findByBookingId(Connection conn, int bookingId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM dbo.Tickets WHERE BookingID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
    public boolean updateTicketStatus(Connection conn, int ticketId, String newStatus) throws SQLException {
        String sql = "UPDATE dbo.Tickets SET TicketStatus = ? WHERE TicketID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, ticketId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean updateTicket(Connection conn, Ticket ticket) throws SQLException {
        String sql = "UPDATE dbo.Tickets SET TicketCode = ?, BookingID = ?, TripID = ?, SeatID = ?, PassengerID = ?, StartStationID = ?, EndStationID = ?, Price = ?, TicketStatus = ?, CoachNameSnapshot = ?, SeatNameSnapshot = ?, PassengerNameSnapshot = ?, PassengerIDCardNumberSnapshot = ?, FareComponentDetails = ?, ParentTicketID = ? WHERE TicketID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
            ps.setString(12, ticket.getPassengerName()); // Model to DB
            ps.setString(13, ticket.getPassengerIDCardNumber()); // Model to DB
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
}
