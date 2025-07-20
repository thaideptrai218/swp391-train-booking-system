package vn.vnrailway.dao;

import vn.vnrailway.dto.SeatStatusDTO;
import vn.vnrailway.model.Seat;
import java.sql.SQLException;
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
}
