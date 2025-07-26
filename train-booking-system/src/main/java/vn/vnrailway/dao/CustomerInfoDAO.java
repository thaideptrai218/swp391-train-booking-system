package vn.vnrailway.dao;

import java.util.List;

import vn.vnrailway.model.User;

public interface CustomerInfoDAO {
    List<User> getAllCustomers(int page);

    int getTotalCustomers();
    
    List<User> getFilteredCustomers(String searchTerm, String searchField, String genderFilter, int page);

    int getTotalFilteredCustomers(String searchTerm, String searchField, String genderFilter);
}
