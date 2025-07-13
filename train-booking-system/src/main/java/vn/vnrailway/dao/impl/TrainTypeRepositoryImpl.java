package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.TrainTypeRepository;
import vn.vnrailway.model.TrainType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public class TrainTypeRepositoryImpl implements TrainTypeRepository {

    private TrainType mapResultSetToTrainType(ResultSet rs) throws SQLException {
        TrainType trainType = new TrainType();
        trainType.setTrainTypeID(rs.getInt("TrainTypeID"));
        trainType.setTypeName(rs.getString("TypeName"));
        trainType.setDescription(rs.getString("Description"));
        // Assuming AverageVelocity column exists in TrainTypes table
        BigDecimal averageVelocity = rs.getBigDecimal("AverageVelocity");
        if (rs.wasNull()) {
            trainType.setAverageVelocity(null);
        } else {
            trainType.setAverageVelocity(averageVelocity);
        }
        return trainType;
    }

    @Override
    public Optional<TrainType> findById(int id) throws SQLException {
        String sql = "SELECT TrainTypeID, TypeName, Description, AverageVelocity FROM TrainTypes WHERE TrainTypeID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrainType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<TrainType> getAllTrainTypes() throws SQLException {
        List<TrainType> trainTypes = new ArrayList<>();
        String sql = "SELECT TrainTypeID, TypeName, Description, AverageVelocity FROM TrainTypes";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                trainTypes.add(mapResultSetToTrainType(rs));
            }
        }
        return trainTypes;
    }

    // Implement save, update, delete if they become necessary
}
