package vn.vnrailway.dao;

import vn.vnrailway.model.Location;
import java.util.List;

public interface LocationRepository {
    List<Location> getAllLocations() throws Exception;
    // Add other CRUD operations if needed in the future
}
