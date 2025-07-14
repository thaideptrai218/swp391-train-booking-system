package vn.vnrailway.dao;

import vn.vnrailway.model.PricingRule;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PricingRuleRepository {
    Optional<PricingRule> findById(int ruleId) throws SQLException;

    List<PricingRule> findAll() throws SQLException;

    List<PricingRule> findActiveRules(LocalDate forDate) throws SQLException; // Find rules active on a specific date
    // More specific finders can be added based on criteria like TrainType,
    // CoachType, etc.
    // e.g., List<PricingRule> findByTrainTypeId(int trainTypeId, LocalDate forDate)
    // throws SQLException;

    PricingRule save(PricingRule pricingRule) throws SQLException;

    boolean update(PricingRule pricingRule) throws SQLException;

    boolean deleteById(int ruleId) throws SQLException; // Or mark as inactive

    void updateRuleStatus(int id, boolean isActive);
}
