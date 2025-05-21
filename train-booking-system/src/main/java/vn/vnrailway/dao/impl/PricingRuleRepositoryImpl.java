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
        
        rule.setTrainTypeID(rs.getObject("TrainTypeID", Integer.class));
        rule.setCoachTypeID(rs.getObject("CoachTypeID", Integer.class));
        rule.setSeatTypeID(rs.getObject("SeatTypeID", Integer.class));
        rule.setPassengerTypeID(rs.getObject("PassengerTypeID", Integer.class));
        rule.setRouteID(rs.getObject("RouteID", Integer.class));
        rule.setTripID(rs.getObject("TripID", Integer.class));
        
        rule.setBasePricePerKm(rs.getBigDecimal("BasePricePerKm"));
        rule.setFixedPrice(rs.getBigDecimal("FixedPrice"));
        rule.setHolidaySurchargePercentage(rs.getBigDecimal("HolidaySurchargePercentage"));
        
        rule.setBookingTimeWindowHoursBeforeDepartureMin(rs.getObject("BookingTimeWindowHoursBeforeDepartureMin", Integer.class));
        rule.setBookingTimeWindowHoursBeforeDepartureMax(rs.getObject("BookingTimeWindowHoursBeforeDepartureMax", Integer.class));
        
        rule.setForRoundTrip(rs.getBoolean("IsForRoundTrip"));
        rule.setPriority(rs.getInt("Priority"));
        
        Date effectiveFrom = rs.getDate("EffectiveFromDate");
        if (effectiveFrom != null) {
            rule.setEffectiveFromDate(effectiveFrom.toLocalDate());
        }
        Date effectiveTo = rs.getDate("EffectiveToDate");
        if (effectiveTo != null) {
            rule.setEffectiveToDate(effectiveTo.toLocalDate());
        }
        rule.setActive(rs.getBoolean("IsActive"));
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
        String sql = "SELECT * FROM PricingRules ORDER BY Priority ASC"; // Order by priority
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
        String sql = "SELECT * FROM PricingRules WHERE IsActive = 1 AND EffectiveFromDate <= ? AND (EffectiveToDate IS NULL OR EffectiveToDate >= ?) ORDER BY Priority ASC";
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
        String sql = "INSERT INTO PricingRules (RuleName, TrainTypeID, CoachTypeID, SeatTypeID, PassengerTypeID, RouteID, TripID, BasePricePerKm, FixedPrice, HolidaySurchargePercentage, BookingTimeWindowHoursBeforeDepartureMin, BookingTimeWindowHoursBeforeDepartureMax, IsForRoundTrip, Priority, EffectiveFromDate, EffectiveToDate, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, pricingRule.getRuleName());
            ps.setObject(2, pricingRule.getTrainTypeID(), Types.INTEGER);
            ps.setObject(3, pricingRule.getCoachTypeID(), Types.INTEGER);
            ps.setObject(4, pricingRule.getSeatTypeID(), Types.INTEGER);
            ps.setObject(5, pricingRule.getPassengerTypeID(), Types.INTEGER);
            ps.setObject(6, pricingRule.getRouteID(), Types.INTEGER);
            ps.setObject(7, pricingRule.getTripID(), Types.INTEGER);
            ps.setBigDecimal(8, pricingRule.getBasePricePerKm());
            ps.setBigDecimal(9, pricingRule.getFixedPrice());
            ps.setBigDecimal(10, pricingRule.getHolidaySurchargePercentage());
            ps.setObject(11, pricingRule.getBookingTimeWindowHoursBeforeDepartureMin(), Types.INTEGER);
            ps.setObject(12, pricingRule.getBookingTimeWindowHoursBeforeDepartureMax(), Types.INTEGER);
            ps.setBoolean(13, pricingRule.isForRoundTrip());
            ps.setInt(14, pricingRule.getPriority());
            ps.setDate(15, Date.valueOf(pricingRule.getEffectiveFromDate()));
            ps.setDate(16, pricingRule.getEffectiveToDate() != null ? Date.valueOf(pricingRule.getEffectiveToDate()) : null);
            ps.setBoolean(17, pricingRule.isActive());

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
        String sql = "UPDATE PricingRules SET RuleName = ?, TrainTypeID = ?, CoachTypeID = ?, SeatTypeID = ?, PassengerTypeID = ?, RouteID = ?, TripID = ?, BasePricePerKm = ?, FixedPrice = ?, HolidaySurchargePercentage = ?, BookingTimeWindowHoursBeforeDepartureMin = ?, BookingTimeWindowHoursBeforeDepartureMax = ?, IsForRoundTrip = ?, Priority = ?, EffectiveFromDate = ?, EffectiveToDate = ?, IsActive = ? WHERE RuleID = ?";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pricingRule.getRuleName());
            ps.setObject(2, pricingRule.getTrainTypeID(), Types.INTEGER);
            ps.setObject(3, pricingRule.getCoachTypeID(), Types.INTEGER);
            ps.setObject(4, pricingRule.getSeatTypeID(), Types.INTEGER);
            ps.setObject(5, pricingRule.getPassengerTypeID(), Types.INTEGER);
            ps.setObject(6, pricingRule.getRouteID(), Types.INTEGER);
            ps.setObject(7, pricingRule.getTripID(), Types.INTEGER);
            ps.setBigDecimal(8, pricingRule.getBasePricePerKm());
            ps.setBigDecimal(9, pricingRule.getFixedPrice());
            ps.setBigDecimal(10, pricingRule.getHolidaySurchargePercentage());
            ps.setObject(11, pricingRule.getBookingTimeWindowHoursBeforeDepartureMin(), Types.INTEGER);
            ps.setObject(12, pricingRule.getBookingTimeWindowHoursBeforeDepartureMax(), Types.INTEGER);
            ps.setBoolean(13, pricingRule.isForRoundTrip());
            ps.setInt(14, pricingRule.getPriority());
            ps.setDate(15, Date.valueOf(pricingRule.getEffectiveFromDate()));
            ps.setDate(16, pricingRule.getEffectiveToDate() != null ? Date.valueOf(pricingRule.getEffectiveToDate()) : null);
            ps.setBoolean(17, pricingRule.isActive());
            ps.setInt(18, pricingRule.getRuleID());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deleteById(int ruleId) throws SQLException {
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
}
