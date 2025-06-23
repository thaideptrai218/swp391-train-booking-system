package vn.vnrailway.dao;

import vn.vnrailway.model.SeatType;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SeatTypeRepository {
    List<SeatType> findAll() throws SQLException;

    Optional<SeatType> findById(int id) throws SQLException;
    // Add other CRUD methods if needed in the future, e.g., save, update, delete
}
