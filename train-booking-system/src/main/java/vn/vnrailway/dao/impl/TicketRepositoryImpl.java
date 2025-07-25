package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TicketRepository;
import vn.vnrailway.dto.CheckBookingDTO;
import vn.vnrailway.dto.CheckInforRefundTicketDTO;
import vn.vnrailway.dto.ConfirmRefundRequestDTO;
import vn.vnrailway.dto.InfoPassengerDTO;
import vn.vnrailway.dto.RefundRequestDTO;
import vn.vnrailway.dto.RefundTicketDTO;
import vn.vnrailway.model.Ticket;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.tags.shaded.org.apache.xpath.SourceTree;

import java.math.BigDecimal; // Import BigDecimal
import java.util.HashSet;
import java.util.Set;

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
        ticket.setPassengerName(rs.getString("PassengerNameSnapshot"));
        ticket.setPassengerIDCardNumber(rs.getString("PassengerIDCardNumberSnapshot"));
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

    public List<String> getAllStationNames() throws SQLException {
        List<String> stationNames = new ArrayList<>();
        String sql = "SELECT StationName FROM Stations";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stationNames.add(rs.getString("StationName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stationNames;
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

    public List<InfoPassengerDTO> findListTicketBooking(String id) throws SQLException {
        String sql = "SELECT \r\n" + //
                "\tTK.TicketCode,\r\n" + //
                "    P.FullName AS PassengerFullName,\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,\r\n" + //
                "    PT.TypeName AS PassengerType,\r\n" + //
                "    ST.TypeName AS SeatTypeName,\r\n" + //
                "    S.SeatNumber AS SeatNumber,\r\n" + //
                "    C.CoachName AS CoachName,\r\n" + //
                "    T.TrainName AS TrainName,\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,\r\n" + //
                "    TK.TicketStatus,\r\n" + //
                "    TK.Price,\r\n" + //
                "    StartStation.StationName AS StartStationName,\r\n" + //
                "    EndStation.StationName AS EndStationName\r\n" + //
                "\r\n" + //
                "FROM Tickets TK\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN Users U ON P.UserID = U.UserID\r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID \r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TR ON TK.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TR.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID \r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID \r\n" + //
                "\r\n" + //
                "WHERE U.UserID = ?\r\n" + //
                "ORDER BY TS1.ScheduledDeparture DESC";

        String updateTicket = "UPDATE TK\r\n" + //
                "SET TicketStatus = 'Expired'\r\n" + //
                "FROM Tickets TK\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN Users U ON P.UserID = U.UserID\r\n" + //
                "JOIN Trips TR ON TK.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID\r\n" + //
                "WHERE DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < 0\r\n" + //
                "  AND TK.TicketStatus = 'Valid'\r\n" + //
                "  AND U.UserID = ?;";

        List<InfoPassengerDTO> passengerList = new ArrayList<>();
        try (Connection conn = DBContext.getConnection()) {
            try (PreparedStatement updateStmt = conn.prepareStatement(updateTicket)) {
                updateStmt.setString(1, id);
                updateStmt.executeUpdate(); // Không cần kết quả, chỉ cập nhật
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        String formattedDateTime = sdf.format(ts); // Kết quả: "10-06-2025 08:00"
                        passenger.setScheduledDepartureTime(formattedDateTime);
                        passenger.setTicketStatus(rs.getString("TicketStatus"));
                        passenger.setPrice(rs.getDouble("Price"));
                        passenger.setStartStationName(rs.getString("StartStationName"));
                        passenger.setEndStationName(rs.getString("EndStationName"));

                        passengerList.add(passenger);
                    }

                }
            }
        }
        return passengerList; // No ticket found with the given code
    }

    public InfoPassengerDTO findTicketByTicketCode(String ticketCode) throws SQLException {
        String sql = "SELECT \r\n" + //
                "    TK.TicketCode,\r\n" + //
                "    P.FullName AS PassengerFullName,\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,\r\n" + //
                "    PT.TypeName AS PassengerType,\r\n" + //
                "    ST.TypeName AS SeatTypeName,\r\n" + //
                "    S.SeatNumber AS SeatNumber,\r\n" + //
                "    C.CoachName AS CoachName,\r\n" + //
                "    T.TrainName AS TrainName,\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,\r\n" + //
                "    TK.TicketStatus,\r\n" + //
                "    TK.Price,\r\n" + //
                "    StartStation.StationName AS StartStationName,\r\n" + //
                "    EndStation.StationName AS EndStationName\r\n" + //
                "\r\n" + //
                "FROM Tickets TK\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID \r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TR ON TK.TripID = TR.TripID\r\n" + //
                "\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TR.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID \r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID \r\n" + //
                "\r\n" + //
                "WHERE TK.TicketCode = ?;";

        String updateSql = "UPDATE TK " +
                "SET TicketStatus = 'Expired' " +
                "FROM Tickets TK " +
                "JOIN Trips TR ON TK.TripID = TR.TripID " +
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID " +
                "WHERE DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < 0 " +
                "AND TK.TicketStatus = 'Valid' " +
                "AND TK.TicketCode = ?;";

        try (Connection conn = DBContext.getConnection()) {
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, ticketCode);
                updateStmt.executeUpdate(); // Không cần kết quả, chỉ cập nhật
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ticketCode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
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
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                        String formattedDateTime = sdf.format(ts); // Kết quả: "10-06-2025 08:00"
                        passenger.setScheduledDepartureTime(formattedDateTime);
                        passenger.setTicketStatus(rs.getString("TicketStatus"));
                        passenger.setPrice(rs.getDouble("Price"));
                        passenger.setStartStationName(rs.getString("StartStationName"));
                        passenger.setEndStationName(rs.getString("EndStationName"));
                        return passenger;
                    }

                }
            }
        }
        return null; // No ticket found with the given code
    }

    @Override
    public CheckInforRefundTicketDTO findInformationRefundTicketDetailsByCode(String bookingCode, String phoneNumber,
            String email) throws SQLException {
        String sql = "USE TrainTicketSystemDB_V2;\r\n" + //
                "\r\n" + //
                "SELECT \r\n" + //
                "    P.FullName AS PassengerFullName,\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,\r\n" + //
                "    TK.TicketCode,\r\n" + //
                "    TK.TicketID,\r\n" + //
                "    PT.TypeName AS PassengerType,\r\n" + //
                "    ST.TypeName AS SeatTypeName,\r\n" + //
                "    S.SeatNumber,\r\n" + //
                "    C.CoachName,\r\n" + //
                "    T.TrainName,\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,\r\n" + //
                "    TK.TicketStatus,\r\n" + //
                "    TK.Price,\r\n" + //
                "    StartStation.StationName AS StartStationName,\r\n" + //
                "    EndStation.StationName AS EndStationName,\r\n" + //
                "\r\n" + //
                "    CASE \r\n" + //
                "\t\tWHEN TK.IsCancelled = 1 THEN 1\r\n" + //
                "\t\tELSE CP.PolicyID\r\n" + //
                "\tEND AS PolicyID,\r\n" + //
                "    DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) AS HoursBeforeDeparture,\r\n" + //
                "\r\n" + //
                "    -- Thông tin hoàn vé\r\n" + //
                "    CASE \r\n" + //
                "\t\tWHEN TK.IsRefundable = 0 THEN 0\r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN 1\r\n" + //
                "        WHEN DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < 0 OR TK.IsRefundable = 0 THEN 0 \r\n"
                + //
                "        ELSE ISNULL(CP.IsRefundable, 0) \r\n" + //
                "    END AS IsRefundable,\r\n" + //
                "\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN N'Hoàn tiền 100% chuyến đi bị hủy'\r\n" + //
                "        WHEN DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < 0 THEN N'Không áp dụng' \r\n" + //
                "        ELSE ISNULL(CP.PolicyName, N'Không áp dụng')\r\n" + //
                "    END AS RefundPolicy,\r\n" + //
                "\r\n" + //
                "    -- Tính phí hoàn vé\r\n" + //
                "    CASE\r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN 0\r\n" + //
                "        WHEN CP.IsRefundable = 1 THEN \r\n" + //
                "            CASE \r\n" + //
                "                WHEN (TK.Price * CP.FeePercentage / 100.0) > CP.FixedFeeAmount THEN (TK.Price * CP.FeePercentage / 100.0)\r\n"
                + //
                "                ELSE CP.FixedFeeAmount\r\n" + //
                "            END\r\n" + //
                "        ELSE 0\r\n" + //
                "    END AS RefundFee,\r\n" + //
                "\r\n" + //
                "    -- Tính số tiền được hoàn lại\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN TK.Price\r\n" + //
                "        WHEN CP.IsRefundable = 1 THEN \r\n" + //
                "            CASE \r\n" + //
                "                WHEN (TK.Price * CP.FeePercentage / 100.0) > CP.FixedFeeAmount THEN TK.Price - (TK.Price * CP.FeePercentage / 100.0)\r\n"
                + //
                "                ELSE TK.Price - CP.FixedFeeAmount\r\n" + //
                "            END\r\n" + //
                "        ELSE 0\r\n" + //
                "    END AS RefundAmount,\r\n" + //
                "\r\n" + //
                "    -- Trạng thái hoàn vé\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.TicketStatus IN ('Used', 'Expired') THEN N'Không thể hoàn vé (vé đã sử dụng hoặc hết hạn)'\r\n"
                + //
                "        WHEN TK.TicketStatus = 'Refunded' THEN N'Yêu cầu được chấp nhận'\r\n" + //
                "        WHEN TK.TicketStatus = 'RejectedRefund' THEN N'Yêu cầu bị từ chối'\r\n" + //
                "        WHEN TK.TicketStatus = 'Processing' THEN N'Đang trong quá trình xử lý hoàn vé'\r\n" + //
                "\t\tWHEN TK.IsCancelled = 1 THEN N'Được hoàn vé (do chuyến đi bị hủy)'\r\n" + //
                "        WHEN DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < 0 THEN N'Tàu đã đi không trả vé'\r\n"
                + //
                "        WHEN CP.PolicyName IS NULL THEN N'Không có chính sách hoàn vé'\r\n" + //
                "        WHEN CP.IsRefundable = 0 THEN N'Không đủ điều kiện hoàn vé'\r\n" + //
                "        ELSE N'Được hoàn vé'\r\n" + //
                "    END AS RefundStatus,\r\n" + //
                "\r\n" + //
                "    -- Thông tin người đặt\r\n" + //
                "    u.FullName AS UserFullName,\r\n" + //
                "    u.Email,\r\n" + //
                "    u.IDCardNumber,\r\n" + //
                "    u.PhoneNumber\r\n" + //
                "\r\n" + //
                "FROM Tickets TK\r\n" + //
                "JOIN Bookings B ON TK.BookingID = B.BookingID\r\n" + //
                "JOIN Users u ON B.UserID = u.UserID\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID \r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TR ON TK.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TR.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TR.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID\r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID\r\n" + //
                "\r\n" + //
                "-- JOIN chính sách hoàn vé, có kiểm tra thời gian hợp lệ\r\n" + //
                "LEFT JOIN CancellationPolicies CP ON\r\n" + //
                "\tTK.IsCancelled = 0 AND\r\n" + //
                "    DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) >= 0 AND\r\n" + //
                "    DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) >= CP.HoursBeforeDeparture_Min AND\r\n" + //
                "    (\r\n" + //
                "        CP.HoursBeforeDeparture_Max IS NULL\r\n" + //
                "        OR DATEDIFF(HOUR, GETDATE(), TS1.ScheduledDeparture) < CP.HoursBeforeDeparture_Max\r\n" + //
                "    )\r\n" + //
                "    AND CP.IsActive = 1\r\n" + //
                "\r\n" + //
                "WHERE \r\n" + //
                "   ((B.BookingCode = ? AND u.PhoneNumber = ?) OR (B.BookingCode =  ? AND u.Email = ?));\r\n" + //
                "";

        CheckInforRefundTicketDTO checkInforRefundTicketDTO = null;
        List<RefundTicketDTO> refundTicketDTOs = new ArrayList<>();

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bookingCode);
            ps.setString(2, phoneNumber);
            ps.setString(3, bookingCode);
            ps.setString(4, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (checkInforRefundTicketDTO == null) {
                        checkInforRefundTicketDTO = new CheckInforRefundTicketDTO();
                        checkInforRefundTicketDTO.setUserFullName(rs.getString("UserFullName"));
                        checkInforRefundTicketDTO.setUserEmail(rs.getString("Email"));
                        checkInforRefundTicketDTO.setUserIDCardNumber(rs.getString("IDCardNumber"));
                        checkInforRefundTicketDTO.setUserPhoneNumber(rs.getString("PhoneNumber"));
                    }

                    RefundTicketDTO refundTicketDTO = new RefundTicketDTO();
                    refundTicketDTO.setPassengerFullName(rs.getString("PassengerFullName"));
                    refundTicketDTO.setPassengerIDCard(rs.getString("PassengerIDCard"));
                    refundTicketDTO.setTicketCode(rs.getString("TicketCode"));
                    refundTicketDTO.setTicketID(rs.getInt("TicketID"));
                    refundTicketDTO.setTicketID(rs.getInt("TicketID"));
                    refundTicketDTO.setPassengerType(rs.getString("PassengerType"));
                    refundTicketDTO.setSeatTypeName(rs.getString("SeatTypeName"));
                    refundTicketDTO.setSeatNumber(rs.getString("SeatNumber"));
                    refundTicketDTO.setCoachName(rs.getString("CoachName"));
                    refundTicketDTO.setTrainName(rs.getString("TrainName"));

                    Timestamp ts = rs.getTimestamp("ScheduledDepartureTime");
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    String formattedDateTime = sdf.format(ts); // Kết quả: "10-06-2025 08:00"
                    refundTicketDTO.setScheduledDepartureTime(formattedDateTime);
                    refundTicketDTO.setTicketStatus(rs.getString("TicketStatus"));
                    refundTicketDTO.setPrice(rs.getDouble("Price"));
                    refundTicketDTO.setStartStationName(rs.getString("StartStationName"));
                    refundTicketDTO.setEndStationName(rs.getString("EndStationName"));
                    refundTicketDTO.setPolicyID(rs.getInt("PolicyID"));
                    refundTicketDTO.setHoursBeforeDeparture(rs.getInt("HoursBeforeDeparture"));
                    refundTicketDTO.setRefundable(rs.getBoolean("IsRefundable"));
                    refundTicketDTO.setRefundPolicy(rs.getString("RefundPolicy"));
                    refundTicketDTO.setRefundFee(rs.getDouble("RefundFee"));
                    refundTicketDTO.setRefundAmount(rs.getDouble("RefundAmount"));
                    refundTicketDTO.setRefundStatus(rs.getString("RefundStatus"));

                    refundTicketDTOs.add(refundTicketDTO);
                }
            }
        }

        if (!refundTicketDTOs.isEmpty() && checkInforRefundTicketDTO != null) {
            checkInforRefundTicketDTO.setRefundTicketDTOs(refundTicketDTOs);
            return checkInforRefundTicketDTO;
        }
        return null;

    }

    public void insertTempRefundRequests(String ticketInfo, String noteSTK) {
        String insertSQL = "INSERT INTO TempRefundRequests (TicketID, AppliedPolicyID, FeeAmount, ActualRefundAmount, NoteSTK)\r\n"
                + //
                "VALUES (?, ?, ?, ?, ?);";
        String updateTicketStatusSQL = "UPDATE Tickets SET TicketStatus = 'Processing', IsRefundable = 0 WHERE TicketID = ?;";

        String[] parts = ticketInfo.split("\\|");
        String ticketCode = parts[0];
        int ticketID = Integer.parseInt(parts[1]);
        int policyID = Integer.parseInt(parts[2]);
        double feeAmount = Double.parseDouble(parts[3]);
        double actualRefundAmount = Double.parseDouble(parts[4]);

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false); // dùng transaction

            // Chèn vào bảng TempRefundRequests
            try (PreparedStatement psInsert = conn.prepareStatement(insertSQL)) {
                psInsert.setInt(1, ticketID);
                psInsert.setInt(2, policyID);
                psInsert.setDouble(3, feeAmount);
                psInsert.setDouble(4, actualRefundAmount);
                psInsert.setString(5, noteSTK); // NoteSTK
                psInsert.executeUpdate();
            }

            // Cập nhật TicketStatus thành 'Processing'
            try (PreparedStatement psUpdate = conn.prepareStatement(updateTicketStatusSQL)) {
                psUpdate.setInt(1, ticketID);
                psUpdate.executeUpdate();
            }

            conn.commit(); // nếu mọi thứ OK thì commit
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<RefundRequestDTO> getAllPendingRefundRequests() throws SQLException {
        List<RefundRequestDTO> list = new ArrayList<>();

        String sql = "SELECT \r\n" + //
                "    TR.RefundID,\r\n" + //
                "    TR.RequestedAt,\r\n" + //
                "    TR.TicketID,\r\n" + //
                "    TR.AppliedPolicyID,\r\n" + //
                "    U.UserID,\r\n" + //
                "    B.BookingID,\r\n" + //
                "\r\n" + //
                "    -- Thông tin vé\r\n" + //
                "    TK.TicketCode,\r\n" + //
                "    TK.TicketStatus,\r\n" + //
                "    TK.Price AS OriginalTicketPrice,\r\n" + //
                "\r\n" + //
                "    -- Thông tin hành khách\r\n" + //
                "    P.FullName AS PassengerFullName,\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,\r\n" + //
                "    PT.TypeName AS PassengerType,\r\n" + //
                "\r\n" + //
                "    -- Chỗ ngồi\r\n" + //
                "    ST.TypeName AS SeatTypeName,\r\n" + //
                "    S.SeatNumber,\r\n" + //
                "    C.CoachName,\r\n" + //
                "\r\n" + //
                "    -- Tàu và chuyến\r\n" + //
                "    T.TrainName,\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,\r\n" + //
                "\r\n" + //
                "    -- Thông tin tuyến\r\n" + //
                "    StartStation.StationName AS StartStationName,\r\n" + //
                "    EndStation.StationName AS EndStationName,\r\n" + //
                "\r\n" + //
                "    -- Thông tin người đặt\r\n" + //
                "    U.FullName AS UserFullName,\r\n" + //
                "    U.Email,\r\n" + //
                "    U.IDCardNumber AS UserIDCard,\r\n" + //
                "    U.PhoneNumber,\r\n" + //
                "    TR.NoteSTK,\r\n" + //
                "\r\n" + //
                "    -- Chính sách hoàn vé\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN 1\r\n" + //
                "        ELSE ISNULL(CP.IsRefundable, 0)\r\n" + //
                "    END AS IsRefundable,\r\n" + //
                "\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN N'Hoàn tiền 100% do chuyến đi bị hủy'\r\n" + //
                "        ELSE ISNULL(CP.PolicyName, N'Không áp dụng')\r\n" + //
                "    END AS RefundPolicy,\r\n" + //
                "\r\n" + //
                "    -- Tính phí hoàn vé\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN 0\r\n" + //
                "        ELSE TR.FeeAmount\r\n" + //
                "    END AS RefundFee,\r\n" + //
                "\r\n" + //
                "    -- Tính số tiền được hoàn lại\r\n" + //
                "    CASE \r\n" + //
                "        WHEN TK.IsCancelled = 1 THEN TK.Price\r\n" + //
                "        ELSE TR.ActualRefundAmount\r\n" + //
                "    END AS RefundAmount\r\n" + //
                "\r\n" + //
                "FROM TempRefundRequests TR\r\n" + //
                "JOIN Tickets TK ON TR.TicketID = TK.TicketID\r\n" + //
                "JOIN Bookings B ON TK.BookingID = B.BookingID\r\n" + //
                "JOIN Users U ON B.UserID = U.UserID\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID \r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TRIP ON TK.TripID = TRIP.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TRIP.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TRIP.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID \r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID \r\n" + //
                "\r\n" + //
                "-- JOIN chính sách hoàn vé theo thời gian còn lại\r\n" + //
                "LEFT JOIN CancellationPolicies CP ON\r\n" + //
                "    DATEDIFF(HOUR, TR.RequestedAt, TS1.ScheduledDeparture) >= 0 AND\r\n" + //
                "    DATEDIFF(HOUR, TR.RequestedAt, TS1.ScheduledDeparture) >= CP.HoursBeforeDeparture_Min AND\r\n" + //
                "    (\r\n" + //
                "        CP.HoursBeforeDeparture_Max IS NULL OR\r\n" + //
                "        DATEDIFF(HOUR, TR.RequestedAt, TS1.ScheduledDeparture) < CP.HoursBeforeDeparture_Max\r\n" + //
                "    )\r\n" + //
                "    AND CP.IsActive = 1\r\n" + //
                "\r\n" + //
                "ORDER BY TR.RequestedAt DESC;\r\n" + //
                "";

        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RefundRequestDTO dto = new RefundRequestDTO();
                dto.setRefundID(rs.getInt("RefundID"));
                dto.setRequestedAt(rs.getTimestamp("RequestedAt").toLocalDateTime());
                dto.setTicketCode(rs.getString("TicketCode"));
                dto.setTicketID(rs.getInt("TicketID"));
                dto.setTicketStatus(rs.getString("TicketStatus"));
                dto.setOriginalPrice(rs.getDouble("OriginalTicketPrice"));
                dto.setPassengerFullName(rs.getString("PassengerFullName"));
                dto.setPassengerIDCard(rs.getString("PassengerIDCard"));
                dto.setPassengerType(rs.getString("PassengerType"));
                dto.setSeatType(rs.getString("SeatTypeName"));
                dto.setSeatNumber(rs.getString("SeatNumber"));
                dto.setCoachName(rs.getString("CoachName"));
                dto.setTrainName(rs.getString("TrainName"));
                dto.setScheduledDeparture(rs.getTimestamp("ScheduledDepartureTime").toLocalDateTime());
                dto.setStartStation(rs.getString("StartStationName"));
                dto.setEndStation(rs.getString("EndStationName"));
                dto.setUserFullName(rs.getString("UserFullName"));
                dto.setEmail(rs.getString("Email"));
                dto.setUserIDCard(rs.getString("UserIDCard"));
                dto.setPhoneNumber(rs.getString("PhoneNumber"));
                dto.setRefundPolicy(rs.getString("RefundPolicy"));
                dto.setRefundFee(rs.getDouble("RefundFee"));
                dto.setRefundAmount(rs.getDouble("RefundAmount"));
                dto.setPolicyID(rs.getInt("AppliedPolicyID"));
                dto.setUserID(rs.getInt("UserID"));
                dto.setBookingID(rs.getInt("BookingID"));
                dto.setNoteSTK(rs.getString("NoteSTK"));

                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;

    }

    public void rejectRefundTicket(String ticketInfo, String imageFileName) throws SQLException {
        String updateTicketSql = "UPDATE Tickets SET TicketStatus = 'RejectedRefund' WHERE TicketID = ?;";
        String updateRefunds = "INSERT INTO Refunds (\r\n" + //
                "    TicketID,\r\n" + //
                "    BookingID,\r\n" + //
                "    AppliedPolicyID,\r\n" + //
                "    OriginalTicketPrice,\r\n" + //
                "    FeeAmount,\r\n" + //
                "    ActualRefundAmount,\r\n" + //
                "    RequestedAt,\r\n" + //
                "    Status,\r\n" + //
                "    RefundMethod,\r\n" + //
                "    Notes,\r\n" + //
                "    RequestedByUserID,\r\n" + //
                "    ProcessedByUserID,\r\n" + //
                "    Images,\r\n" + //
                "    isConfirmed\r\n" + //
                ")\r\n" + //
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String deleteTempRefundSql = "DELETE FROM TempRefundRequests WHERE TicketID =  ?";

        String[] parts = ticketInfo.split("\\|");
        int ticketID = Integer.parseInt(parts[0]);
        int policyID = Integer.parseInt(parts[1]);
        double originalPrice = Double.parseDouble(parts[2]);
        double refundFee = Double.parseDouble(parts[3]);
        double refundAmount = Double.parseDouble(parts[4]);
        String requestedAt = parts[5];
        int userIDByRequest = Integer.parseInt(parts[6]);
        int userIDByProcessing = Integer.parseInt(parts[7]);
        int bookingID = Integer.parseInt(parts[8]);
        String noteSTK = parts[9]; // Thông tin ghi chú STK

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false); // dùng transaction

            try (PreparedStatement ps = conn.prepareStatement(updateTicketSql)) {
                ps.setInt(1, ticketID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateRefunds)) {
                ps.setInt(1, ticketID);
                ps.setInt(2, bookingID);
                ps.setInt(3, policyID);
                ps.setDouble(4, originalPrice);
                ps.setDouble(5, refundFee);
                ps.setDouble(6, refundAmount);
                ps.setTimestamp(7, Timestamp.valueOf(requestedAt));
                ps.setString(8, "Rejected");
                ps.setString(9, "Bank Transfer"); // Hoặc phương thức hoàn tiền khác
                ps.setString(10, noteSTK);
                ps.setInt(11, userIDByRequest);
                ps.setInt(12, userIDByProcessing);
                ps.setString(13, imageFileName); // Tên file hình ảnh
                ps.setBoolean(14, true); // isConfirmed
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteTempRefundSql)) {
                ps.setInt(1, ticketID);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Ném lại ngoại lệ để caller có thể xử lý
        }
    }

    public void approveRefundTicket(String ticketInfo, String imageFileName) throws SQLException {
        String updateTicketSql = "UPDATE Tickets SET TicketStatus = 'Refunded' WHERE TicketID = ?;";
        String updateRefunds = "INSERT INTO Refunds (\r\n" + //
                "    TicketID,\r\n" + //
                "    BookingID,\r\n" + //
                "    AppliedPolicyID,\r\n" + //
                "    OriginalTicketPrice,\r\n" + //
                "    FeeAmount,\r\n" + //
                "    ActualRefundAmount,\r\n" + //
                "    RequestedAt,\r\n" + //
                "    Status,\r\n" + //
                "    RefundMethod,\r\n" + //
                "    Notes,\r\n" + //
                "    RequestedByUserID,\r\n" + //
                "    ProcessedByUserID,\r\n" + //
                "    Images,\r\n" + //
                "    isConfirmed\r\n" + //

                ")\r\n" + //
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        String deleteTempRefundSql = "DELETE FROM TempRefundRequests WHERE TicketID =  ?";

        String[] parts = ticketInfo.split("\\|");
        int ticketID = Integer.parseInt(parts[0]);
        int policyID = Integer.parseInt(parts[1]);
        double originalPrice = Double.parseDouble(parts[2]);
        double refundFee = Double.parseDouble(parts[3]);
        double refundAmount = Double.parseDouble(parts[4]);
        String requestedAt = parts[5];
        int userIDByRequest = Integer.parseInt(parts[6]);
        int userIDByProcessing = Integer.parseInt(parts[7]);
        int bookingID = Integer.parseInt(parts[8]);
        String noteSTK = parts[9];

        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false); // dùng transaction

            try (PreparedStatement ps = conn.prepareStatement(updateTicketSql)) {
                ps.setInt(1, ticketID);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateRefunds)) {
                ps.setInt(1, ticketID);
                ps.setInt(2, bookingID);
                ps.setInt(3, policyID);
                ps.setDouble(4, originalPrice);
                ps.setDouble(5, refundFee);
                ps.setDouble(6, refundAmount);
                ps.setString(7, requestedAt);
                ps.setString(8, "Approved");
                ps.setString(9, "Bank Transfer"); // Hoặc phương thức hoàn tiền khác
                ps.setString(10, noteSTK);
                ps.setInt(11, userIDByRequest);
                ps.setInt(12, userIDByProcessing);
                ps.setString(13, imageFileName);
                ps.setBoolean(14, true); // isConfirmed
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(deleteTempRefundSql)) {
                ps.setInt(1, ticketID);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Ném lại ngoại lệ để caller có thể xử lý
        }
    }

    @Override
    public void refundAllTicketsForTrip(int tripId) throws SQLException {
        String updateTicketsSql = "UPDATE Tickets SET IsRefundable = 0 WHERE TripID = ?;";
        String selectTicketsSql = "SELECT * FROM Tickets WHERE TripID = ?;";
        String selectRefundedTicketIdsSql = "SELECT TicketID FROM Refunds WHERE TicketID IN (SELECT TicketID FROM Tickets WHERE TripID = ?)";
        String insertRefundSql = "INSERT INTO Refunds (TicketID, BookingID, AppliedPolicyID, OriginalTicketPrice, FeeAmount, ActualRefundAmount, RequestedAt, Status, RefundMethod, Notes, RequestedByUserID, ProcessedByUserID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        vn.vnrailway.dao.TripStationRepository tripStationRepository = new vn.vnrailway.dao.impl.TripStationRepositoryImpl();
        java.util.List<vn.vnrailway.model.TripStation> tripStations = tripStationRepository.findByTripId(tripId);
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime lastScheduledDeparture = null;
        if (!tripStations.isEmpty()) {
            lastScheduledDeparture = tripStations.get(tripStations.size() - 1).getScheduledDeparture();
        }
        try (Connection conn = DBContext.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Update all tickets IsRefundable = 0
            try (PreparedStatement ps = conn.prepareStatement(updateTicketsSql)) {
                ps.setInt(1, tripId);
                ps.executeUpdate();
            }

            // 2. Select all tickets for the trip
            List<Ticket> tickets = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(selectTicketsSql)) {
                ps.setInt(1, tripId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Ticket t = mapResultSetToTicket(rs);
                        tickets.add(t);
                    }
                }
            }

            // 3. Lấy danh sách TicketID đã có Refunds
            Set<Integer> refundedTicketIds = new HashSet<>();
            try (PreparedStatement ps = conn.prepareStatement(selectRefundedTicketIdsSql)) {
                ps.setInt(1, tripId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        refundedTicketIds.add(rs.getInt(1));
                    }
                }
            }

            // 4. Batch insert Refunds cho các vé hợp lệ
            try (PreparedStatement ps = conn.prepareStatement(insertRefundSql)) {
                for (Ticket t : tickets) {
                    if (t.getBookingID() == 0)
                        continue; // Không có người đặt
                    if (lastScheduledDeparture != null && lastScheduledDeparture.isBefore(now))
                        continue; // Đã qua giờ khởi hành cuối
                    if (refundedTicketIds.contains(t.getTicketID()))
                        continue; // Đã có refund

                    ps.setInt(1, t.getTicketID());
                    ps.setInt(2, t.getBookingID());
                    ps.setNull(3, java.sql.Types.INTEGER); // AppliedPolicyID (null)
                    ps.setBigDecimal(4, t.getPrice() != null ? t.getPrice() : java.math.BigDecimal.ZERO); // OriginalTicketPrice
                    ps.setBigDecimal(5, java.math.BigDecimal.ZERO); // FeeAmount = 0
                    ps.setBigDecimal(6, t.getPrice() != null ? t.getPrice() : java.math.BigDecimal.ZERO); // ActualRefundAmount
                                                                                                          // = full
                                                                                                          // price
                    ps.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis())); // RequestedAt = now
                    ps.setString(8, "Approved"); // Status
                    ps.setString(9, "Bank Transfer"); // RefundMethod
                    ps.setString(10, "Auto refund due to trip cancellation"); // Notes
                    ps.setNull(11, java.sql.Types.INTEGER); // RequestedByUserID (null)
                    ps.setInt(12, 15); // ProcessedByUserID = 15
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<ConfirmRefundRequestDTO> getAllConfirmedRefundRequests() throws SQLException {
        List<ConfirmRefundRequestDTO> list = new ArrayList<>();

        String sql = "use TrainTicketSystemDB_V2\r\n" + //
                "SELECT \r\n" + //
                "\tTK.TicketCode,\r\n" + //
                "\t-- Chính sách hoàn vé\r\n" + //
                "    CP.PolicyName,\r\n" + //
                "\tCP.PolicyID,\r\n" + //
                "\tRF.TicketID,\r\n" + //
                "\tRF.RefundID,\r\n" + //
                "\tRF.Status,\r\n" + //
                "\tRF.ProcessedByUserID,\r\n" + //
                "\tRF.RequestedByUserID,\r\n" + //
                "\r\n" + //
                "\r\n" + //
                "\t-- Thông tin hành khách\r\n" + //
                "    P.FullName AS PassengerFullName,--\r\n" + //
                "    P.IDCardNumber AS PassengerIDCard,--\r\n" + //
                "    PT.TypeName AS PassengerType,--\r\n" + //
                "\r\n" + //
                "\t    -- Tính phí hoàn vé\r\n" + //
                "    RF.FeeAmount AS RefundFee,--\r\n" + //
                "\r\n" + //
                "    -- Tính số tiền được hoàn lại\r\n" + //
                "    RF.ActualRefundAmount AS RefundAmount,\r\n" + //
                "\tRF.OriginalTicketPrice,\r\n" + //
                "    RF.RequestedAt,--\r\n" + //
                "\tRF.ProcessedAt,\r\n" + //
                "\tB.BookingID,\r\n" + //
                "\r\n" + //
                "    -- Thông tin vé\r\n" + //
                "    TK.TicketStatus,--\r\n" + //
                "\r\n" + //
                "    -- Chỗ ngồi\r\n" + //
                "    ST.TypeName AS SeatTypeName,--\r\n" + //
                "    S.SeatNumber,--\r\n" + //
                "    C.CoachName,--\r\n" + //
                "\r\n" + //
                "    -- Tàu và chuyến\r\n" + //
                "    T.TrainName,--\r\n" + //
                "    TS1.ScheduledDeparture AS ScheduledDepartureTime,--\r\n" + //
                "\r\n" + //
                "    -- Thông tin tuyến\r\n" + //
                "    StartStation.StationName AS StartStationName,--\r\n" + //
                "    EndStation.StationName AS EndStationName,--\r\n" + //
                "\r\n" + //
                "    -- Thông tin người đặt\r\n" + //
                "    U.FullName AS UserFullName,\r\n" + //
                "U.Email AS UserEmail,\r\n" + //
                "U.PhoneNumber AS UserPhoneNumber,\r\n" + //
                "U.IDCardNumber AS UserIDCard," +
                "\r\n" + //
                "\t--Thông tin nhân viên hủy vé\r\n" + //
                "\tU2.FullName AS StaffFullName,\r\n" + //
                "U2.Email AS StaffEmail,\r\n" + //
                "U2.PhoneNumber AS StaffPhoneNumber,\r\n" + //
                "U2.IDCardNumber AS StaffIDCard, " +
                "\r\n" + //
                "\tRF.Notes,\r\n" + //
                "\tRF.Images\r\n" + //
                "\r\n" + //
                "FROM Refunds RF\r\n" + //
                "JOIN Tickets TK ON RF.TicketID = TK.TicketID\r\n" + //
                "JOIN Bookings B ON TK.BookingID = B.BookingID\r\n" + //
                "JOIN Users U ON B.UserID = U.UserID\r\n" + //
                "JOIN Users U2 ON RF.ProcessedByUserID = U2.UserID\r\n" + //
                "JOIN Passengers P ON TK.PassengerID = P.PassengerID \r\n" + //
                "JOIN PassengerTypes PT ON P.PassengerTypeID = PT.PassengerTypeID \r\n" + //
                "JOIN Seats S ON TK.SeatID = S.SeatID\r\n" + //
                "JOIN SeatTypes ST ON ST.SeatTypeID = S.SeatTypeID\r\n" + //
                "JOIN Coaches C ON S.CoachID = C.CoachID\r\n" + //
                "JOIN Trains T ON C.TrainID = T.TrainID\r\n" + //
                "JOIN Trips TRIP ON TK.TripID = TRIP.TripID\r\n" + //
                "JOIN TripStations TS1 ON TS1.StationID = TK.StartStationID AND TS1.TripID = TRIP.TripID\r\n" + //
                "JOIN TripStations TS2 ON TS2.StationID = TK.EndStationID AND TS2.TripID = TRIP.TripID\r\n" + //
                "JOIN Stations StartStation ON StartStation.StationID = TS1.StationID \r\n" + //
                "JOIN Stations EndStation ON EndStation.StationID = TS2.StationID \r\n" + //
                "\r\n" + //
                "-- JOIN chính sách hoàn vé theo thời gian còn lại\r\n" + //
                "LEFT JOIN CancellationPolicies CP ON RF.AppliedPolicyID = CP.PolicyID\r\n" + //
                "    \r\n" + //
                "WHERE RF.isConfirmed = 1\r\n" + //
                "ORDER BY RF.ProcessedAt DESC;";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ConfirmRefundRequestDTO dto = new ConfirmRefundRequestDTO();

                dto.setRefundID(rs.getInt("RefundID"));
                dto.setTicketCode(rs.getString("TicketCode"));
                dto.setTicketID(rs.getInt("TicketID")); // nếu cần, thêm TK.TicketID trong SELECT
                dto.setUserID(rs.getInt("RequestedByUserID"));
                dto.setBookingID(rs.getInt("BookingID"));

                dto.setPassengerFullName(rs.getString("PassengerFullName"));
                dto.setPassengerIDCard(rs.getString("PassengerIDCard"));
                dto.setPassengerType(rs.getString("PassengerType"));

                dto.setSeatType(rs.getString("SeatTypeName"));
                dto.setSeatNumber(rs.getString("SeatNumber"));
                dto.setCoachName(rs.getString("CoachName"));
                dto.setTrainName(rs.getString("TrainName"));

                dto.setStartStation(rs.getString("StartStationName"));
                dto.setEndStation(rs.getString("EndStationName"));
                dto.setPolicyID(rs.getInt("PolicyID"));
                dto.setRefundPolicy(rs.getString("PolicyName"));

                dto.setScheduledDeparture(rs.getTimestamp("ScheduledDepartureTime").toString());
                dto.setOriginalPrice(rs.getDouble("OriginalTicketPrice"));
                dto.setRefundFee(rs.getDouble("RefundFee"));
                dto.setRefundAmount(rs.getDouble("RefundAmount"));

                dto.setTicketStatus(rs.getString("TicketStatus"));
                dto.setRefundStatus(rs.getString("Status")); // cần SELECT thêm RF.Status

                dto.setRequestedAt(rs.getTimestamp("RequestedAt").toString());
                dto.setProcessedAt(rs.getTimestamp("ProcessedAt").toString());

                dto.setUserFullName(rs.getString("UserFullName"));
                dto.setUserEmail(rs.getString("UserEmail"));
                dto.setUserPhoneNumber(rs.getString("UserPhoneNumber"));
                dto.setUserIDCard(rs.getString("UserIDCard"));

                dto.setStaffFullName(rs.getString("StaffFullName"));
                dto.setStaffEmail(rs.getString("StaffEmail"));
                dto.setStaffPhoneNumber(rs.getString("StaffPhoneNumber"));
                dto.setStaffIDCard(rs.getString("StaffIDCard"));
                dto.setNotes(rs.getString("Notes"));
                dto.setImages(rs.getString("Images"));
                list.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        TicketRepository ticketRepository = new TicketRepositoryImpl();
        List<ConfirmRefundRequestDTO> confirmedRequests;
        try {
            confirmedRequests = ticketRepository.getAllConfirmedRefundRequests();
            for (ConfirmRefundRequestDTO request : confirmedRequests) {
                System.out.println(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
