package vn.vnrailway.dao.impl;

import vn.vnrailway.dao.SeatStatusDAO;
import vn.vnrailway.dto.SeatUIDetailDTO;
import vn.vnrailway.utils.DBContext; // Assuming this is your DB connection utility

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class SeatStatusDAOImpl implements SeatStatusDAO {

    @Override
    public String checkSingleSeatAvailability(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId) throws SQLException {
        String status = null;
        // Note: Your SP dbo.CheckSingleSeatAvailability returns a result set with one row, one column.
        String sql = "{CALL dbo.CheckSingleSeatAvailability(?, ?, ?, ?)}";
        
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, tripId);
            cs.setInt(2, seatId);
            cs.setInt(3, legOriginStationId);
            cs.setInt(4, legDestinationStationId);
            
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    status = rs.getString("AvailabilityStatus");
                } else {
                    // This case should ideally be handled by RAISERROR in SP if seat/trip/leg is invalid
                    status = "Error: No status returned"; 
                }
            }
        }
        return status;
    }

    @Override
    public List<SeatUIDetailDTO> getCoachSeatsWithAvailabilityAndPrice(
            Connection conn, 
            int tripId, 
            int coachId, 
            int legOriginStationId, 
            int legDestinationStationId, 
            Timestamp bookingDateTime, 
            boolean isRoundTrip) throws SQLException {
        
        List<SeatUIDetailDTO> seatList = new ArrayList<>();
        String sql = "{CALL dbo.GetCoachSeatsWithAvailabilityAndPrice(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, tripId);
            cs.setInt(2, coachId);
            cs.setInt(3, legOriginStationId);
            cs.setInt(4, legDestinationStationId);
            cs.setTimestamp(5, bookingDateTime);
            cs.setBoolean(6, isRoundTrip);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    SeatUIDetailDTO seat = new SeatUIDetailDTO();
                    seat.setSeatID(rs.getInt("SeatID"));
                    seat.setSeatName(rs.getString("SeatName"));
                    seat.setSeatNumberInCoach(rs.getInt("SeatNumberInCoach"));
                    seat.setSeatTypeID(rs.getInt("SeatTypeID"));
                    seat.setSeatTypeName(rs.getString("SeatTypeName"));
                    seat.setBerthLevel(rs.getString("BerthLevel")); // Adjust if type is different
                    seat.setSeatPriceMultiplier(rs.getDouble("SeatPriceMultiplier"));
                    seat.setCoachPriceMultiplier(rs.getDouble("CoachPriceMultiplier"));
                    seat.setTripBasePriceMultiplier(rs.getDouble("TripBasePriceMultiplier"));
                    seat.setEnabled(rs.getBoolean("IsEnabled"));
                    seat.setAvailabilityStatus(rs.getString("AvailabilityStatus"));
                    
                    // Handle nullable Double for CalculatedPrice
                    double calculatedPriceDouble = rs.getDouble("CalculatedPrice");
                    if (rs.wasNull()) {
                        seat.setCalculatedPrice(null);
                    } else {
                        seat.setCalculatedPrice(calculatedPriceDouble);
                    }
                    seatList.add(seat);
                }
            }
        }
        return seatList;
    }
}
