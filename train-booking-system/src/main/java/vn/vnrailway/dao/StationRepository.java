package vn.vnrailway.dao;

import vn.vnrailway.model.Station;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Optional<Station> findById(int stationId) throws SQLException;

    Optional<Station> findByStationName(String stationCode) throws SQLException;

    List<Station> findAll() throws SQLException;

    Station save(Station station) throws SQLException;

    boolean update(Station station) throws SQLException;

    boolean updateStationLocked(int stationId, boolean isLocked) throws SQLException;
}
