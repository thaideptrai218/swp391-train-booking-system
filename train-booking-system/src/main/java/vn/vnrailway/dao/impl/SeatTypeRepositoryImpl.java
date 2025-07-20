package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.SeatTypeRepository;
import vn.vnrailway.model.SeatType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatTypeRepositoryImpl implements SeatTypeRepository {

    @Override
    public List<SeatType> getAllSeatTypes() throws SQLException {
        List<SeatType> seatTypes = new ArrayList<>();
        // Using Java model field names as column names: seatTypeID, typeName
        String sql = "SELECT seatTypeID, typeName, description FROM SeatTypes";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SeatType seatType = new SeatType();
                seatType.setSeatTypeID(rs.getInt("seatTypeID"));
                seatType.setTypeName(rs.getString("typeName"));
                seatType.setDescription(rs.getString("description"));
                seatTypes.add(seatType);
            }
        }
        return seatTypes;
    }

    @Override
    public Optional<SeatType> findById(int id) throws SQLException {
        SeatType seatType = null;
        // Using Java model field names as column names: seatTypeID, typeName
        String sql = "SELECT seatTypeID, typeName, description FROM SeatTypes WHERE seatTypeID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    seatType = new SeatType();
                    seatType.setSeatTypeID(rs.getInt("seatTypeID"));
                    seatType.setTypeName(rs.getString("typeName"));
                    seatType.setDescription(rs.getString("description"));
                }
            }
        }
        return Optional.ofNullable(seatType);
    }
    // Implement other methods like save, update, delete if you add them to the
    // interface
}
