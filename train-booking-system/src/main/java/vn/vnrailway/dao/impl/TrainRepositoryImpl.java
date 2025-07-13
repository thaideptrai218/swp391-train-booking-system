package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TrainRepository;
import vn.vnrailway.model.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainRepositoryImpl implements TrainRepository {

    private Train mapResultSetToTrain(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setTrainID(rs.getInt("TrainID"));
        train.setTrainName(rs.getString("TrainName"));
        train.setTrainTypeID(rs.getInt("TrainTypeID"));
        train.setActive(rs.getBoolean("IsActive"));
        return train;
    }

    @Override
    public void addTrain(Train train) throws SQLException {
        String sql = "INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setInt(2, train.getTrainTypeID());
            ps.setBoolean(3, train.isActive());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean updateTrain(Train train) throws SQLException {
        String sql = "UPDATE Trains SET TrainTypeID = ?, IsActive = ? WHERE TrainName = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, train.getTrainTypeID());
            ps.setBoolean(2, train.isActive());
            ps.setString(3, train.getTrainName());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteTrain(String trainCode) throws SQLException {
        String sql = "DELETE FROM Trains WHERE TrainName = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trainCode);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Train> getAllTrains() throws SQLException {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM Trains";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trains.add(mapResultSetToTrain(rs));
            }
        }
        return trains;
    }

    @Override
    public Train getTrainByTrainCode(String trainCode) throws SQLException {
        String sql = "SELECT * FROM Trains WHERE TrainName = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trainCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTrain(rs);
                }
            }
        }
        return null;
    }

    @Override
    public java.util.Optional<Train> findById(int trainId) throws SQLException {
        String sql = "SELECT * FROM Trains WHERE TrainID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return java.util.Optional.of(mapResultSetToTrain(rs));
                }
            }
        }
        return java.util.Optional.empty();
    }
}
