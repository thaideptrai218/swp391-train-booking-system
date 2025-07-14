package vn.vnrailway.dao.impl;

import vn.vnrailway.config.DBContext;
import vn.vnrailway.dao.HolidayPriceRepository;
import vn.vnrailway.model.HolidayPrice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HolidayPriceRepositoryImpl implements HolidayPriceRepository {
    private Connection connection;

    public HolidayPriceRepositoryImpl() throws Exception {
        this.connection = new DBContext().getConnection();
    }

    @Override
    public List<HolidayPrice> getAllHolidayPrices() {
        List<HolidayPrice> holidayPrices = new ArrayList<>();
        String sql = "SELECT * FROM HolidayPrices";
        System.out.println("Executing query: " + sql);
        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                System.out.println("Found a holiday price in the database.");
                HolidayPrice holidayPrice = new HolidayPrice();
                holidayPrice.setId(rs.getInt("HolidayID"));
                holidayPrice.setHolidayName(rs.getString("HolidayName"));
                holidayPrice.setDescription(rs.getString("Description"));
                holidayPrice.setStartDate(rs.getDate("StartDate"));
                holidayPrice.setEndDate(rs.getDate("EndDate"));
                holidayPrice.setDiscountPercentage(rs.getFloat("DiscountPercentage"));
                holidayPrice.setActive(rs.getBoolean("IsActive"));
                holidayPrices.add(holidayPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Returning " + holidayPrices.size() + " holiday prices.");
        return holidayPrices;
    }

    @Override
    public void updateHolidayStatus(int id, boolean isActive) {
        String sql = "UPDATE HolidayPrices SET IsActive = ? WHERE HolidayID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
