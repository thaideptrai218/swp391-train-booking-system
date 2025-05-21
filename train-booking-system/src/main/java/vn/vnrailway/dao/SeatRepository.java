package vn.vnrailway.dao;

import vn.vnrailway.model.Seat;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Optional<Seat> findById(int seatId) throws SQLException;
    List<Seat> findAll() throws SQLException; // Potentially large, consider if needed without filtering
    List<Seat> findByCoachId(int coachId) throws SQLException;
    List<Seat> findBySeatTypeId(int seatTypeId) throws SQLException;
    Optional<Seat> findByCoachIdAndSeatNumber(int coachId, int seatNumber) throws SQLException;
    List<Seat> findEnabledSeatsByCoachId(int coachId) throws SQLException;

    Seat save(Seat seat) throws SQLException;
    boolean update(Seat seat) throws SQLException; // e.g., to enable/disable a seat
    boolean deleteById(int seatId) throws SQLException; // Or mark as disabled
}
