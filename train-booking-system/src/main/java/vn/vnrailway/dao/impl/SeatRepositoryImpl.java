package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.model.Seat;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatRepositoryImpl implements SeatRepository {

    private Seat mapResultSetToSeat(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setSeatID(rs.getInt("SeatID"));
        seat.setCoachID(rs.getInt("CoachID"));
        seat.setSeatNumber(rs.getInt("SeatNumber"));
        seat.setSeatName(rs.getString("SeatName"));
        seat.setSeatTypeID(rs.getInt("SeatTypeID"));
        seat.setEnabled(rs.getBoolean("IsEnabled"));
        return seat;
    }

    @Override
    public void addSeat(Seat seat) throws SQLException {
        String sql = "INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seat.getCoachID());
            ps.setInt(2, seat.getSeatNumber());
            ps.setString(3, seat.getSeatName());
            ps.setInt(4, seat.getSeatTypeID());
            ps.setBoolean(5, seat.isEnabled());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean updateSeat(Seat seat) throws SQLException {
        String sql = "UPDATE Seats SET CoachID = ?, SeatNumber = ?, SeatName = ?, SeatTypeID = ?, IsEnabled = ? WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seat.getCoachID());
            ps.setInt(2, seat.getSeatNumber());
            ps.setString(3, seat.getSeatName());
            ps.setInt(4, seat.getSeatTypeID());
            ps.setBoolean(5, seat.isEnabled());
            ps.setInt(6, seat.getSeatID());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteSeat(int id) throws SQLException {
        String sql = "DELETE FROM Seats WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Seat> getAllSeats() throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seats";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                seats.add(mapResultSetToSeat(rs));
            }
        }
        return seats;
    }

    @Override
    public Seat getSeatById(int id) throws SQLException {
        String sql = "SELECT * FROM Seats WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeat(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<vn.vnrailway.dto.SeatStatusDTO> getCoachSeatsWithAvailabilityAndPrice(int tripId, int coachId,
            int legOriginStationId, int legDestinationStationId, java.sql.Timestamp bookingDateTime,
            boolean isRoundTrip, String currentUserSessionId) throws SQLException {
        List<vn.vnrailway.dto.SeatStatusDTO> seatStatusList = new ArrayList<>();
        String callSP = "{CALL dbo.GetCoachSeatsWithAvailabilityAndPrice(?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBContext.getConnection();
                CallableStatement cs = conn.prepareCall(callSP)) {

            cs.setInt(1, tripId);
            cs.setInt(2, coachId);
            cs.setInt(3, legOriginStationId);
            cs.setInt(4, legDestinationStationId);
            cs.setTimestamp(5, bookingDateTime);
            cs.setBoolean(6, isRoundTrip);
            if (currentUserSessionId != null) {
                cs.setString(7, currentUserSessionId);
            } else {
                cs.setNull(7, java.sql.Types.NVARCHAR);
            }

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    seatStatusList.add(mapResultSetToSeatStatusDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getCoachSeatsWithAvailabilityAndPrice: " + e.getMessage());
            throw e;
        }
        return seatStatusList;
    }

    private vn.vnrailway.dto.SeatStatusDTO mapResultSetToSeatStatusDTO(ResultSet rs) throws SQLException {
        vn.vnrailway.dto.SeatStatusDTO dto = new vn.vnrailway.dto.SeatStatusDTO();
        dto.setSeatID(rs.getInt("SeatID"));
        dto.setSeatName(rs.getString("SeatName"));
        dto.setSeatNumberInCoach(rs.getInt("SeatNumberInCoach"));
        dto.setSeatTypeID(rs.getInt("SeatTypeID"));
        dto.setSeatTypeName(rs.getString("SeatTypeName"));
        dto.setBerthLevel(rs.getString("BerthLevel"));
        dto.setSeatPriceMultiplier(rs.getBigDecimal("SeatPriceMultiplier"));
        dto.setCoachPriceMultiplier(rs.getBigDecimal("CoachPriceMultiplier"));
        dto.setTripBasePriceMultiplier(rs.getBigDecimal("TripBasePriceMultiplier"));
        dto.setEnabled(rs.getBoolean("IsEnabled"));
        dto.setAvailabilityStatus(rs.getString("AvailabilityStatus"));
        dto.setCalculatedPrice(rs.getBigDecimal("CalculatedPrice"));
        return dto;
    }

    @Override
    public List<vn.vnrailway.dto.SeatTypePricingDTO> getTripSeatTypePricing(
            int tripId,
            int legOriginStationId,
            int legDestinationStationId,
            Timestamp bookingDateTime,
            boolean isRoundTrip,
            String currentUserSessionId) throws SQLException {
        
        List<vn.vnrailway.dto.SeatTypePricingDTO> seatTypePricingList = new ArrayList<>();
        String callSP = "{CALL dbo.GetTripSeatTypePricing(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = DBContext.getConnection();
                CallableStatement cs = conn.prepareCall(callSP)) {

            cs.setInt(1, tripId);
            cs.setInt(2, legOriginStationId);
            cs.setInt(3, legDestinationStationId);
            cs.setTimestamp(4, bookingDateTime);
            cs.setBoolean(5, isRoundTrip);
            cs.setString(6, currentUserSessionId);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    seatTypePricingList.add(mapResultSetToSeatTypePricingDTO(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Error in getTripSeatTypePricing: " + e.getMessage());
            throw e;
        }
        return seatTypePricingList;
    }

    private vn.vnrailway.dto.SeatTypePricingDTO mapResultSetToSeatTypePricingDTO(ResultSet rs) throws SQLException {
        vn.vnrailway.dto.SeatTypePricingDTO dto = new vn.vnrailway.dto.SeatTypePricingDTO();
        dto.setSeatTypeID(rs.getInt("SeatTypeID"));
        dto.setSeatTypeName(rs.getString("SeatTypeName"));
        dto.setCoachTypeName(rs.getString("CoachTypeName"));
        dto.setDescription(rs.getString("DisplayDescription"));
        dto.setPricePerSeat(rs.getBigDecimal("CalculatedPrice"));
        dto.setTotalSeats(rs.getInt("TotalSeats"));
        dto.setAvailableSeats(rs.getInt("AvailableSeats"));
        dto.setHasAvailableSeats(rs.getInt("AvailableSeats") > 0);
        return dto;
    }
}
