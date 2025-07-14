package vn.vnrailway.dao;

import vn.vnrailway.model.Station;
import vn.vnrailway.dto.StationPopularityDTO;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface StationRepository {
    Optional<Station> findById(int stationId) throws SQLException;

    Optional<Station> findByStationCode(String stationCode) throws SQLException;

    List<Station> findAll() throws SQLException;

    Station save(Station station) throws SQLException; // Returns the saved station, possibly with generated ID

    boolean update(Station station) throws SQLException;

    List<StationPopularityDTO> getMostCommonOriginStations(int limit) throws SQLException;

    List<StationPopularityDTO> getMostCommonDestinationStations(int limit) throws SQLException;
}
