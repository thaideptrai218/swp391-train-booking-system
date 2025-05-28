package vn.vnrailway.service.impl;

import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl; // Assuming this is the implementation
import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.service.TripService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList; // Import if needed for empty list return or error handling

public class TripServiceImpl implements TripService {

    private TripRepository tripRepository;

    public TripServiceImpl() {
        // Initialize the repository. In a real application, this might be injected
        // by a dependency injection framework (e.g., Spring).
        this.tripRepository = new TripRepositoryImpl();
    }

    // Constructor for allowing injection, useful for testing or DI frameworks
    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<TripSearchResultDTO> searchAvailableTrips(int originStationId, int destinationStationId, LocalDate departureDate) throws SQLException, Exception {
        // Basic validation removed as per feedback, assuming client-side validation
        // or other layers handle these checks if necessary.

        try {
            // Delegate the call to the repository layer
            return tripRepository.searchAvailableTrips(originStationId, destinationStationId, departureDate);
        } catch (SQLException e) {
            // Log the SQL error and rethrow it to be handled by the controller or a global error handler
            System.err.println("SQLException in TripServiceImpl during search: " + e.getMessage());
            e.printStackTrace(); // For dev logging
            throw e; // Rethrow to allow controller to handle UI message
        } catch (Exception e) {
            // Catch any other unexpected errors
            System.err.println("Unexpected error in TripServiceImpl during search: " + e.getMessage());
            e.printStackTrace(); // For dev logging
            throw new Exception("An unexpected error occurred while searching for trips.", e); // Wrap and rethrow
        }
    }
}
