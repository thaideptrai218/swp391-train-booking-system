package vn.vnrailway.dao;

import vn.vnrailway.model.Coach;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CoachRepository {
    Optional<Coach> findById(int coachId) throws SQLException;
    List<Coach> findAll() throws SQLException;
    List<Coach> findByTrainId(int trainId) throws SQLException;
    List<Coach> findByCoachTypeId(int coachTypeId) throws SQLException;
    Optional<Coach> findByTrainIdAndCoachNumber(int trainId, int coachNumber) throws SQLException;

    Coach save(Coach coach) throws SQLException;
    boolean update(Coach coach) throws SQLException;
    boolean deleteById(int coachId) throws SQLException;
}
