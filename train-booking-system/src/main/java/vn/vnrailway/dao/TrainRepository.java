package vn.vnrailway.dao;

import vn.vnrailway.model.Train;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TrainRepository {
    Optional<Train> findById(int trainId) throws SQLException;
    Optional<Train> findByTrainName(String trainName) throws SQLException;
    List<Train> findAll() throws SQLException;
    List<Train> findByTrainTypeId(int trainTypeId) throws SQLException;
    List<Train> findByStatus(boolean isActive) throws SQLException;
    
    Train save(Train train) throws SQLException;
    boolean update(Train train) throws SQLException;
    boolean deleteById(int trainId) throws SQLException; // Or just mark as inactive
}
