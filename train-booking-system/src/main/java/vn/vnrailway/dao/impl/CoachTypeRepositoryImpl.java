package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.CoachTypeRepository;
import vn.vnrailway.model.CoachType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoachTypeRepositoryImpl implements CoachTypeRepository {

    private CoachType mapResultSetToCoachType(ResultSet rs) throws SQLException {
        CoachType coachType = new CoachType();
        coachType.setCoachTypeID(rs.getInt("CoachTypeID"));
        coachType.setTypeName(rs.getString("TypeName"));
        coachType.setPriceMultiplier(rs.getBigDecimal("PriceMultiplier"));
        coachType.setDescription(rs.getString("Description"));
        return coachType;
    }

    @Override
    public Optional<CoachType> findById(int coachTypeId) throws SQLException {
        String sql = "SELECT CoachTypeID, TypeName, PriceMultiplier, Description FROM CoachTypes WHERE CoachTypeID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coachTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCoachType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CoachType> findAll() throws SQLException {
        List<CoachType> coachTypes = new ArrayList<>();
        String sql = "SELECT CoachTypeID, TypeName, PriceMultiplier, Description FROM CoachTypes";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                coachTypes.add(mapResultSetToCoachType(rs));
            }
        }
        return coachTypes;
    }
    
    // Implement save, update, delete methods if/when needed
}
