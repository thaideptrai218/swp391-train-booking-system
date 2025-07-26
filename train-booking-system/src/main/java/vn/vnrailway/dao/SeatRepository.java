package vn.vnrailway.dao;

import vn.vnrailway.dto.SeatStatusDTO;
import vn.vnrailway.dto.SeatTypePricingDTO;
import vn.vnrailway.model.Seat;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface SeatRepository {
    void addSeat(Seat seat) throws SQLException;

    boolean updateSeat(Seat seat) throws SQLException;

    boolean deleteSeat(int id) throws SQLException;

    List<Seat> getAllSeats() throws SQLException;

    Seat getSeatById(int id) throws SQLException;

    List<SeatStatusDTO> getCoachSeatsWithAvailabilityAndPrice(
            int tripId,
            int coachId,
            int legOriginStationId,
            int legDestinationStationId,
            java.sql.Timestamp bookingDateTime,
            boolean isRoundTrip,
            String currentUserSessionId) throws SQLException;


    List<Seat> findByCoachId(int coachId) throws SQLException;
    List<SeatTypePricingDTO> getTripSeatTypePricing(
            int tripId,
            int legOriginStationId,
            int legDestinationStationId,
            Timestamp bookingDateTime,
            boolean isRoundTrip,
            String currentUserSessionId) throws SQLException;

}
