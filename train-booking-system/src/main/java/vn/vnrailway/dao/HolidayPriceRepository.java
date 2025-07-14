package vn.vnrailway.dao;

import vn.vnrailway.model.HolidayPrice;

import java.util.List;

public interface HolidayPriceRepository {
    List<HolidayPrice> getAllHolidayPrices();

    void updateHolidayStatus(int id, boolean isActive);
}
