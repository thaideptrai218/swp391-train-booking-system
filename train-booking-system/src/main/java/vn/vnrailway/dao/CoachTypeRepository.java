package vn.vnrailway.dao;

import vn.vnrailway.model.CoachType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CoachTypeRepository {
    Optional<CoachType> findById(int coachTypeId) throws SQLException;

    List<CoachType> getAllCoachTypes() throws SQLException;
}
