package vn.vnrailway.dao;

import vn.vnrailway.model.TripStation;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripStationRepository {
    Optional<TripStation> findById(int tripStationId) throws SQLException;

    List<TripStation> findByTripId(int tripId) throws SQLException; // Ordered by sequence number

    List<TripStation> findByStationId(int stationId) throws SQLException;

    Optional<TripStation> findByTripIdAndStationId(int tripId, int stationId) throws SQLException;

    TripStation save(TripStation tripStation) throws SQLException;

    boolean update(TripStation tripStation) throws SQLException; // For updating times, etc.

    boolean deleteById(int tripStationId) throws SQLException;

    boolean deleteByTripId(int tripId) throws SQLException; // For deleting all stations of a trip

    void updateScheduledArrival(int tripId, int stationId, LocalDateTime scheduledArrival) throws SQLException;

    void updateScheduledDeparture(int tripId, int stationId, LocalDateTime scheduledDeparture) throws SQLException;
}
