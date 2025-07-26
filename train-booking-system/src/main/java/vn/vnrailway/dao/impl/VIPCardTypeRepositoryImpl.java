package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.model.VIPCardType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VIPCardTypeRepositoryImpl implements VIPCardTypeRepository {

    @Override
    public Optional<VIPCardType> findById(int id) throws SQLException {
        String sql = "SELECT * FROM VIPCardTypes WHERE VIPCardTypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToVIPCardType(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<VIPCardType> findAll() throws SQLException {
        List<VIPCardType> vipCardTypes = new ArrayList<>();
        String sql = "SELECT * FROM VIPCardTypes";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                vipCardTypes.add(mapRowToVIPCardType(rs));
            }
        }
        return vipCardTypes;
    }

    @Override
    public void save(VIPCardType vipCardType) throws SQLException {
        String sql = "INSERT INTO VIPCardTypes (TypeName, Price, DiscountPercentage, DurationMonths, Description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vipCardType.getTypeName());
            ps.setBigDecimal(2, vipCardType.getPrice());
            ps.setBigDecimal(3, vipCardType.getDiscountPercentage());
            ps.setInt(4, vipCardType.getDurationMonths());
            ps.setString(5, vipCardType.getDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(VIPCardType vipCardType) throws SQLException {
        String sql = "UPDATE VIPCardTypes SET TypeName = ?, Price = ?, DiscountPercentage = ?, DurationMonths = ?, Description = ? WHERE VIPCardTypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vipCardType.getTypeName());
            ps.setBigDecimal(2, vipCardType.getPrice());
            ps.setBigDecimal(3, vipCardType.getDiscountPercentage());
            ps.setInt(4, vipCardType.getDurationMonths());
            ps.setString(5, vipCardType.getDescription());
            ps.setInt(6, vipCardType.getVipCardTypeID());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM VIPCardTypes WHERE VIPCardTypeID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private VIPCardType mapRowToVIPCardType(ResultSet rs) throws SQLException {
        return new VIPCardType(
                rs.getInt("VIPCardTypeID"),
                rs.getString("TypeName"),
                rs.getBigDecimal("Price"),
                rs.getBigDecimal("DiscountPercentage"),
                rs.getInt("DurationMonths"),
                rs.getString("Description")
        );
    }
}
