package vn.vnrailway.dao;

import vn.vnrailway.model.CoachType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CoachTypeRepository {
    Optional<CoachType> findById(int coachTypeId) throws SQLException;
    List<CoachType> findAll() throws SQLException; // Optional, but good practice
    // Add other methods like save, update, delete if needed for full CRUD
}
