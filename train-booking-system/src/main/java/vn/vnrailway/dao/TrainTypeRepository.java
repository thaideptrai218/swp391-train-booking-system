package vn.vnrailway.dao;

import vn.vnrailway.model.TrainType;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TrainTypeRepository {
    Optional<TrainType> findById(int id) throws SQLException;

    List<TrainType> getAllTrainTypes() throws SQLException;
}
