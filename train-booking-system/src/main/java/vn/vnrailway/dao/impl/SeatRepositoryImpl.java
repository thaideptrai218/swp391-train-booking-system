package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.SeatRepository;
import vn.vnrailway.dto.SeatStatusDTO; // Import DTO
import vn.vnrailway.model.Seat;

import java.sql.*;
import java.math.BigDecimal; // Import for price multipliers
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private SeatStatusDTO mapResultSetToSeatStatusDTO(ResultSet rs) throws SQLException {
        SeatStatusDTO dto = new SeatStatusDTO();
        dto.setSeatID(rs.getInt("SeatID"));
        dto.setSeatName(rs.getString("SeatName"));
        dto.setSeatNumberInCoach(rs.getInt("SeatNumberInCoach"));
        dto.setSeatTypeID(rs.getInt("SeatTypeID"));
        dto.setSeatTypeName(rs.getString("SeatTypeName"));
        dto.setBerthLevel(rs.getString("BerthLevel")); // Handle null if SP can return null for this
        dto.setSeatPriceMultiplier(rs.getBigDecimal("SeatPriceMultiplier"));
        dto.setCoachPriceMultiplier(rs.getBigDecimal("CoachPriceMultiplier"));
        dto.setTripBasePriceMultiplier(rs.getBigDecimal("TripBasePriceMultiplier"));
        dto.setEnabled(rs.getBoolean("IsEnabled"));
        dto.setAvailabilityStatus(rs.getString("AvailabilityStatus"));
        // Map the new CalculatedPrice field from the SP
        dto.setCalculatedPrice(rs.getBigDecimal("CalculatedPrice"));
        return dto;
    }

    @Override
    public List<SeatStatusDTO> getCoachSeatsWithAvailability(int tripId, int coachId, int legOriginStationId, int legDestinationStationId) throws SQLException {
        List<SeatStatusDTO> seatStatusList = new ArrayList<>();
        String callSP = "{CALL dbo.GetCoachSeatsWithAvailability(?, ?, ?, ?)}";

        try (Connection conn = DBContext.getConnection();
             CallableStatement cs = conn.prepareCall(callSP)) {
            
            cs.setInt(1, tripId);
            cs.setInt(2, coachId);
            cs.setInt(3, legOriginStationId);
            cs.setInt(4, legDestinationStationId);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    seatStatusList.add(mapResultSetToSeatStatusDTO(rs));
                }
            }
        } catch (SQLException e) {
            // Log or handle more gracefully
            System.err.println("SQL Error in getCoachSeatsWithAvailability: " + e.getMessage());
            throw e;
        }
        return seatStatusList;
    }

    @Override
    public List<SeatStatusDTO> getCoachSeatsWithAvailabilityAndPrice(
            int tripId,
            int coachId,
            int legOriginStationId,
            int legDestinationStationId,
            java.sql.Timestamp bookingDateTime,
            boolean isRoundTrip,
            String currentUserSessionId) throws SQLException { // Added currentUserSessionId
        List<SeatStatusDTO> seatStatusList = new ArrayList<>();
        // Updated to call the new stored procedure with 7 parameters
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

    @Override
    public Optional<Seat> findById(int seatId) throws SQLException {
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSeat(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Seat> findAll() throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats";
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
    public List<Seat> findByCoachId(int coachId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats WHERE CoachID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToSeat(rs));
                }
            }
        }
        return seats;
    }

    @Override
    public List<Seat> findBySeatTypeId(int seatTypeId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats WHERE SeatTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToSeat(rs));
                }
            }
        }
        return seats;
    }

    @Override
    public Optional<Seat> findByCoachIdAndSeatNumber(int coachId, int seatNumber) throws SQLException {
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats WHERE CoachID = ? AND SeatNumber = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            ps.setInt(2, seatNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSeat(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    @Override
    public List<Seat> findEnabledSeatsByCoachId(int coachId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT SeatID, CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled FROM Seats WHERE CoachID = ? AND IsEnabled = 1";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seats.add(mapResultSetToSeat(rs));
                }
            }
        }
        return seats;
    }

    @Override
    public Seat save(Seat seat) throws SQLException {
        String sql = "INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, seat.getCoachID());
            ps.setInt(2, seat.getSeatNumber());
            ps.setString(3, seat.getSeatName());
            ps.setInt(4, seat.getSeatTypeID());
            ps.setBoolean(5, seat.isEnabled());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating seat failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    seat.setSeatID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating seat failed, no ID obtained.");
                }
            }
        }
        return seat;
    }

    @Override
    public boolean update(Seat seat) throws SQLException {
        String sql = "UPDATE Seats SET CoachID = ?, SeatNumber = ?, SeatName = ?, SeatTypeID = ?, IsEnabled = ? WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seat.getCoachID());
            ps.setInt(2, seat.getSeatNumber());
            ps.setString(3, seat.getSeatName());
            ps.setInt(4, seat.getSeatTypeID());
            ps.setBoolean(5, seat.isEnabled());
            ps.setInt(6, seat.getSeatID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int seatId) throws SQLException {
        String sql = "DELETE FROM Seats WHERE SeatID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seatId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        SeatRepository seatRepository = new SeatRepositoryImpl();
        try {
            List<Seat> seatsInCoach1 = seatRepository.findByCoachId(2);
            seatsInCoach1.forEach(System.out::println);

            //Add more specific tests as needed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
