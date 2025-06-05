package vn.vnrailway.dao;

import vn.vnrailway.dto.SeatStatusDTO;
import vn.vnrailway.model.Seat; // Assuming a Seat model exists for other CRUD if needed

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    // Method to get seat status for a specific coach on a trip leg
    List<SeatStatusDTO> getCoachSeatsWithAvailability(int tripId, int coachId, int legOriginStationId, int legDestinationStationId) throws SQLException;

    // New method to get seat status with price
    List<SeatStatusDTO> getCoachSeatsWithAvailabilityAndPrice(
            int tripId,
            int coachId,
            int legOriginStationId,
            int legDestinationStationId,
            java.sql.Timestamp bookingDateTime,
            boolean isRoundTrip
    ) throws SQLException;

    // Standard CRUD methods from existing Impl
    Optional<Seat> findById(int seatId) throws SQLException;
    List<Seat> findAll() throws SQLException;
    List<Seat> findByCoachId(int coachId) throws SQLException;
    List<Seat> findBySeatTypeId(int seatTypeId) throws SQLException; // Was in Impl
    Optional<Seat> findByCoachIdAndSeatNumber(int coachId, int seatNumber) throws SQLException; // Was in Impl
    List<Seat> findEnabledSeatsByCoachId(int coachId) throws SQLException; // Was in Impl
    Seat save(Seat seat) throws SQLException;
    boolean update(Seat seat) throws SQLException;
    boolean deleteById(int seatId) throws SQLException;
}
