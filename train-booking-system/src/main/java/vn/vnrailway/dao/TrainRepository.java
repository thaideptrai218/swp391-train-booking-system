package vn.vnrailway.dao;

import vn.vnrailway.model.Train;
import java.sql.SQLException;
import java.util.List;

import java.util.Optional;

public interface TrainRepository {
    void addTrain(Train train) throws SQLException;

    boolean updateTrain(Train train) throws SQLException;

    boolean deleteTrain(String trainCode) throws SQLException;

    List<Train> getAllTrains() throws SQLException;

    Train getTrainByTrainCode(String trainCode) throws SQLException;

    Optional<Train> findById(int trainId) throws SQLException;

    boolean updateTrainLocked(int trainId, boolean isLocked) throws SQLException;
}
