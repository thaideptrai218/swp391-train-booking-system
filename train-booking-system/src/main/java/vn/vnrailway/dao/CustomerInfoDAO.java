package vn.vnrailway.dao;

import java.util.List;

import vn.vnrailway.model.User;

public interface CustomerInfoDAO {
    List<User> getAllCustomers(int page);

    int getTotalCustomers();
}