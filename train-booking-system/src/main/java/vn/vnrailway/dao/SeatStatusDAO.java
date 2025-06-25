package vn.vnrailway.dao;

import vn.vnrailway.dto.SeatUIDetailDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public interface SeatStatusDAO {

    /**
     * Calls the dbo.CheckSingleSeatAvailability stored procedure to get the status of a single seat.
     *
     * @param conn The database connection.
     * @param tripId The ID of the trip.
     * @param seatId The ID of the seat.
     * @param legOriginStationId The origin station ID of the leg for which availability is checked.
     * @param legDestinationStationId The destination station ID of the leg.
     * @return A string representing the seat status (e.g., "Available", "Occupied", "Disabled", or an error string).
     * @throws SQLException If a database access error occurs.
     */
    String checkSingleSeatAvailability(Connection conn, int tripId, int seatId, int legOriginStationId, int legDestinationStationId) throws SQLException;

    /**
     * Calls the dbo.GetCoachSeatsWithAvailabilityAndPrice stored procedure to fetch all seat details for a coach.
     *
     * @param conn The database connection.
     * @param tripId The ID of the trip.
     * @param coachId The ID of the coach.
     * @param legOriginStationId The origin station ID of the leg.
     * @param legDestinationStationId The destination station ID of the leg.
     * @param bookingDateTime The current date and time of booking, for pricing rules.
     * @param isRoundTrip Flag indicating if this is for a round trip, for pricing rules.
     * @return A list of SeatUIDetailDTO objects representing the seats in the coach.
     * @throws SQLException If a database access error occurs.
     */
    List<SeatUIDetailDTO> getCoachSeatsWithAvailabilityAndPrice(
            Connection conn, 
            int tripId, 
            int coachId, 
            int legOriginStationId, 
            int legDestinationStationId, 
            Timestamp bookingDateTime, 
            boolean isRoundTrip) throws SQLException;
}
