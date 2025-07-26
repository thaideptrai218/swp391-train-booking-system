package vn.vnrailway.service.impl;

import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.CoachRepository;
import vn.vnrailway.dao.CoachTypeRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dao.impl.CoachRepositoryImpl;
import vn.vnrailway.dao.impl.CoachTypeRepositoryImpl;
import vn.vnrailway.dto.CoachInfoDTO;
import vn.vnrailway.dto.TripSearchResultDTO;
import vn.vnrailway.model.Coach;
import vn.vnrailway.model.CoachType;
import vn.vnrailway.service.TripService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class TripServiceImpl implements TripService {

    private TripRepository tripRepository;
    private CoachRepository coachRepository;
    private CoachTypeRepository coachTypeRepository;

    public TripServiceImpl() {
        this.tripRepository = new TripRepositoryImpl();
        this.coachRepository = new CoachRepositoryImpl();
        this.coachTypeRepository = new CoachTypeRepositoryImpl();
    }

    // Constructor for allowing injection
    public TripServiceImpl(TripRepository tripRepository, CoachRepository coachRepository,
            CoachTypeRepository coachTypeRepository) {
        this.tripRepository = tripRepository;
        this.coachRepository = coachRepository;
        this.coachTypeRepository = coachTypeRepository;
    }

    @Override
    public List<TripSearchResultDTO> searchAvailableTrips(int originStationId, int destinationStationId,
            LocalDate departureDate, int passengerCount) throws SQLException, Exception {
        try {
            List<TripSearchResultDTO> trips = tripRepository.searchAvailableTrips(originStationId, destinationStationId,
                    departureDate, passengerCount);

            if (trips == null) {
                return new ArrayList<>(); // Return empty list if null
            }

            for (TripSearchResultDTO tripDto : trips) {
                if (tripDto.getTrainId() > 0) { // Ensure trainId is valid
                    List<Coach> coaches = coachRepository.findByTrainIdOrderByPositionInTrainDesc(tripDto.getTrainId());
                    List<CoachInfoDTO> coachInfoList = new ArrayList<>();
                    for (Coach coach : coaches) {
                        Optional<CoachType> coachTypeOpt = coachTypeRepository.findById(coach.getCoachTypeID());
                        if (coachTypeOpt.isPresent()) {
                            CoachType coachType = coachTypeOpt.get();
                            CoachInfoDTO coachInfo = new CoachInfoDTO();
                            coachInfo.setCoachId(coach.getCoachID());
                            coachInfo.setPositionInTrain(coach.getPositionInTrain());
                            coachInfo.setCoachTypeName(coachType.getTypeName());
                            coachInfo.setCoachTypeDescription(coachType.getDescription());
                            coachInfo.setCapacity(coach.getCapacity());
                            coachInfo.setCompartmented(coachType.isCompartmented()); // Populate from CoachType model
                            coachInfo.setDefaultCompartmentCapacity(coachType.getDefaultCompartmentCapacity()); // Populate
                                                                                                                // from
                                                                                                                // CoachType
                                                                                                                // model
                            coachInfoList.add(coachInfo);
                        } else {
                            System.err.println("Warning: CoachType not found for ID: " + coach.getCoachTypeID()
                                    + " for CoachID: " + coach.getCoachID());
                        }
                    }
                    tripDto.setCoaches(coachInfoList);
                } else {
                    tripDto.setCoaches(new ArrayList<>()); // Set empty list if no valid trainId
                }
            }
            return trips;
        } catch (SQLException e) {
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
