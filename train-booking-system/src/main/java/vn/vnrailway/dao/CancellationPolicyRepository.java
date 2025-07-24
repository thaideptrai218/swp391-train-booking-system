package vn.vnrailway.dao;

import vn.vnrailway.model.CancellationPolicy;
import vn.vnrailway.model.Coach;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CancellationPolicyRepository {
    List<CancellationPolicy> getAll() throws SQLException;
    void insert(CancellationPolicy policy) throws SQLException ;
    CancellationPolicy findById(int id) throws SQLException;
    void update(CancellationPolicy policy) throws SQLException;
    List<CancellationPolicy> searchByPolicyName(String policyName) throws SQLException;
    
}
