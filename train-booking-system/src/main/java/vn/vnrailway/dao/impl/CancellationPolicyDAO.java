package vn.vnrailway.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.CancellationPolicyRepository;
import vn.vnrailway.dao.CustomerInfoDAO;
import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.User;

public class CancellationPolicyDAO implements CancellationPolicyRepository {

    @Override
    public List<CancellationPolicy> getAll() {
        List<CancellationPolicy> list = new ArrayList<>();
        String sql = "SELECT * FROM CancellationPolicies";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CancellationPolicy policy = new CancellationPolicy();
                policy.setPolicyID(rs.getInt("PolicyID"));
                policy.setPolicyName(rs.getString("PolicyName"));
                policy.setHoursBeforeDepartureMin(rs.getInt("HoursBeforeDeparture_Min"));
                policy.setHoursBeforeDepartureMax(
                        rs.getObject("HoursBeforeDeparture_Max") != null ? rs.getInt("HoursBeforeDeparture_Max")
                                : null);
                policy.setFeePercentage(rs.getBigDecimal("FeePercentage"));
                policy.setFixedFeeAmount(rs.getBigDecimal("FixedFeeAmount"));
                policy.setRefundable(rs.getBoolean("IsRefundable"));
                policy.setDescription(rs.getString("Description"));
                policy.setActive(rs.getBoolean("IsActive"));
                policy.setEffectiveFromDate(rs.getDate("EffectiveFromDate"));
                policy.setEffectiveToDate(rs.getDate("EffectiveToDate"));
                list.add(policy);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(CancellationPolicy policy) {
        String sql = "INSERT INTO CancellationPolicies " +
                "(PolicyName, HoursBeforeDeparture_Min, HoursBeforeDeparture_Max, FeePercentage, FixedFeeAmount, IsRefundable, Description, IsActive, EffectiveFromDate, EffectiveToDate) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, policy.getPolicyName());
            stmt.setInt(2, policy.getHoursBeforeDepartureMin());
            if (policy.getHoursBeforeDepartureMax() != null)
                stmt.setInt(3, policy.getHoursBeforeDepartureMax());
            else
                stmt.setNull(3, Types.INTEGER);
            stmt.setBigDecimal(4, policy.getFeePercentage());
            stmt.setBigDecimal(5, policy.getFixedFeeAmount());
            stmt.setBoolean(6, policy.isRefundable());
            stmt.setString(7, policy.getDescription());
            stmt.setBoolean(8, policy.isActive());
            stmt.setDate(9, new java.sql.Date(policy.getEffectiveFromDate().getTime()));
            if (policy.getEffectiveToDate() != null)
                stmt.setDate(10, new java.sql.Date(policy.getEffectiveToDate().getTime()));
            else
                stmt.setNull(10, Types.DATE);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CancellationPolicy findById(int id) throws SQLException {
        String sql = "SELECT * FROM CancellationPolicies WHERE PolicyID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CancellationPolicy policy = new CancellationPolicy();
                policy.setPolicyID(rs.getInt("PolicyID"));
                policy.setPolicyName(rs.getString("PolicyName"));
                policy.setHoursBeforeDepartureMin(rs.getInt("HoursBeforeDeparture_Min"));
                policy.setHoursBeforeDepartureMax(
                        rs.getObject("HoursBeforeDeparture_Max") != null ? rs.getInt("HoursBeforeDeparture_Max")
                                : null);
                policy.setFeePercentage(rs.getBigDecimal("FeePercentage"));
                policy.setFixedFeeAmount(rs.getBigDecimal("FixedFeeAmount"));
                policy.setRefundable(rs.getBoolean("IsRefundable"));
                policy.setDescription(rs.getString("Description"));
                policy.setActive(rs.getBoolean("IsActive"));
                policy.setEffectiveFromDate(rs.getDate("EffectiveFromDate"));
                policy.setEffectiveToDate(rs.getDate("EffectiveToDate"));
                return policy;
            }
        }
        return null;
    }

    public void update(CancellationPolicy policy) throws SQLException {
        String sql = "UPDATE CancellationPolicies SET " +
                "PolicyName = ?, " +
                "HoursBeforeDeparture_Min = ?, " +
                "HoursBeforeDeparture_Max = ?, " +
                "FeePercentage = ?, " +
                "FixedFeeAmount = ?, " +
                "IsRefundable = ?, " +
                "Description = ?, " +
                "IsActive = ?, " +
                "EffectiveFromDate = ?, " +
                "EffectiveToDate = ? " +
                "WHERE PolicyID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, policy.getPolicyName());
            stmt.setInt(2, policy.getHoursBeforeDepartureMin());

            if (policy.getHoursBeforeDepartureMax() != null)
                stmt.setInt(3, policy.getHoursBeforeDepartureMax());
            else
                stmt.setNull(3, Types.INTEGER);

            stmt.setBigDecimal(4, policy.getFeePercentage());
            stmt.setBigDecimal(5, policy.getFixedFeeAmount());
            stmt.setBoolean(6, policy.isRefundable());
            stmt.setString(7, policy.getDescription());
            stmt.setBoolean(8, policy.isActive());
            stmt.setDate(9, new java.sql.Date(policy.getEffectiveFromDate().getTime()));

            if (policy.getEffectiveToDate() != null)
                stmt.setDate(10, new java.sql.Date(policy.getEffectiveToDate().getTime()));
            else
                stmt.setNull(10, Types.DATE);

            stmt.setInt(11, policy.getPolicyID());

            stmt.executeUpdate();
        }
    }

}
