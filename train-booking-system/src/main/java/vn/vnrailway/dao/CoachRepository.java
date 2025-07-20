package vn.vnrailway.dao;

import vn.vnrailway.model.Coach;
import java.sql.SQLException;
import java.util.List;

public interface CoachRepository {
    void addCoach(Coach coach) throws SQLException;

    boolean updateCoach(Coach coach) throws SQLException;

    boolean deleteCoach(int id) throws SQLException;

    List<Coach> getAllCoaches() throws SQLException;

    Coach getCoachById(int id) throws SQLException;

    List<Coach> findByTrainIdOrderByPositionInTrainDesc(int trainId) throws SQLException;
}
