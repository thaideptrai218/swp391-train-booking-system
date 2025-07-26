package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.PricingRuleRepository;
import vn.vnrailway.model.PricingRule;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PricingRuleRepositoryImpl implements PricingRuleRepository {

    private PricingRule mapResultSetToPricingRule(ResultSet rs) throws SQLException {
        PricingRule rule = new PricingRule();
        rule.setRuleID(rs.getInt("RuleID"));
        rule.setRuleName(rs.getString("RuleName"));
        rule.setDescription(rs.getString("Description"));
        rule.setTrainTypeID(rs.getObject("TrainTypeID", Integer.class));
        rule.setRouteID(rs.getObject("RouteID", Integer.class));
        rule.setBasePricePerKm(rs.getBigDecimal("BasePricePerKm"));
        rule.setForRoundTrip(rs.getBoolean("IsForRoundTrip"));
        rule.setApplicableDateStart(rs.getObject("ApplicableDateStart", LocalDate.class));
        rule.setApplicableDateEnd(rs.getObject("ApplicableDateEnd", LocalDate.class));
        rule.setActive(rs.getBoolean("IsActive"));
        rule.setDefaultRule(rs.getBoolean("IsDefault"));
        rule.setPriority(rs.getObject("Priority", Integer.class));
        return rule;
    }

    @Override
    public Optional<PricingRule> findById(int ruleId) throws SQLException {
        String sql = "SELECT * FROM PricingRules WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ruleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPricingRule(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<PricingRule> findAll() throws SQLException {
        List<PricingRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM PricingRules";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rules.add(mapResultSetToPricingRule(rs));
            }
        }
        return rules;
    }

    @Override
    public List<PricingRule> findActiveRules(LocalDate forDate) throws SQLException {
        List<PricingRule> rules = new ArrayList<>();
        String sql = "SELECT * FROM PricingRules WHERE IsActive = 1 AND ApplicableDateStart <= ? AND (ApplicableDateEnd IS NULL OR ApplicableDateEnd >= ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(forDate));
            ps.setDate(2, Date.valueOf(forDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rules.add(mapResultSetToPricingRule(rs));
                }
            }
        }
        return rules;
    }

    @Override
    public PricingRule save(PricingRule pricingRule) throws SQLException {
        String sql = "INSERT INTO PricingRules (RuleName, Description, TrainTypeID, RouteID, BasePricePerKm, IsForRoundTrip, ApplicableDateStart, ApplicableDateEnd, IsActive, Priority) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, pricingRule.getRuleName());
            ps.setString(2, pricingRule.getDescription());
            ps.setObject(3, pricingRule.getTrainTypeID(), Types.INTEGER);
            ps.setObject(4, pricingRule.getRouteID(), Types.INTEGER);
            ps.setBigDecimal(5, pricingRule.getBasePricePerKm());
            ps.setBoolean(6, pricingRule.isForRoundTrip());
            ps.setObject(7, pricingRule.getApplicableDateStart());
            ps.setObject(8, pricingRule.getApplicableDateEnd());
            ps.setBoolean(9, pricingRule.isActive());
            ps.setObject(10, pricingRule.getPriority(), Types.INTEGER);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating pricing rule failed, no rows affected.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pricingRule.setRuleID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating pricing rule failed, no ID obtained.");
                }
            }
        }
        return pricingRule;
    }

    @Override
    public boolean update(PricingRule pricingRule) throws SQLException {
        // Chặn sửa rule mặc định
        if (pricingRule.isDefaultRule()) {
            throw new SQLException("Không thể sửa quy tắc giá mặc định!");
        }
        String sql = "UPDATE PricingRules SET RuleName = ?, Description = ?, TrainTypeID = ?, RouteID = ?, BasePricePerKm = ?, IsForRoundTrip = ?, ApplicableDateStart = ?, ApplicableDateEnd = ?, IsActive = ?, Priority = ? WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pricingRule.getRuleName());
            ps.setString(2, pricingRule.getDescription());
            ps.setObject(3, pricingRule.getTrainTypeID(), Types.INTEGER);
            ps.setObject(4, pricingRule.getRouteID(), Types.INTEGER);
            ps.setBigDecimal(5, pricingRule.getBasePricePerKm());
            ps.setBoolean(6, pricingRule.isForRoundTrip());
            ps.setObject(7, pricingRule.getApplicableDateStart());
            ps.setObject(8, pricingRule.getApplicableDateEnd());
            ps.setBoolean(9, pricingRule.isActive());
            ps.setObject(10, pricingRule.getPriority(), Types.INTEGER);
            ps.setInt(11, pricingRule.getRuleID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int ruleId) throws SQLException {
        // Chặn xóa rule mặc định
        String checkSql = "SELECT IsDefault FROM PricingRules WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, ruleId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getBoolean("IsDefault")) {
                    throw new SQLException("Không thể xóa quy tắc giá mặc định!");
                }
            }
        }
        String sql = "DELETE FROM PricingRules WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ruleId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        PricingRuleRepository prRepository = new PricingRuleRepositoryImpl();
        try {
            System.out.println("Testing PricingRuleRepository...");
            List<PricingRule> rules = prRepository.findActiveRules(LocalDate.now());
            rules.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRuleStatus(int id, boolean isActive) {
        // Không cho phép update trạng thái rule default
        String checkSql = "SELECT IsDefault FROM PricingRules WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setInt(1, id);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getBoolean("IsDefault")) {
                    throw new SQLException("Không thể thay đổi trạng thái hoạt động của giá mặc định!");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String sql = "UPDATE PricingRules SET IsActive = ? WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
