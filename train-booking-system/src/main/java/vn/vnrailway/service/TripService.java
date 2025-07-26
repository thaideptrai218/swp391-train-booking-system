package vn.vnrailway.service;

import vn.vnrailway.dto.TripSearchResultDTO;
import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;

public interface TripService {
    /**
     * Searches for available trips based on origin, destination, and departure date.
     *
     * @param originStationId The ID of the origin station.
     * @param destinationStationId The ID of the destination station.
     * @param departureDate The date of departure.
     * @return A list of TripSearchResultDTO objects representing available trips.
     * @throws SQLException if a database access error occurs.
     * @throws Exception for any other errors during the search process.
     */
    List<TripSearchResultDTO> searchAvailableTrips(int originStationId, int destinationStationId, LocalDate departureDate, int passengerCount) throws SQLException, Exception;
}
