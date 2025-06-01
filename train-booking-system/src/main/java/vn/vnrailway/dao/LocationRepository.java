package vn.vnrailway.dao;

import vn.vnrailway.model.Location;
import java.util.List;

public interface LocationRepository {
    List<Location> getAllLocations() throws Exception;

    List<Location> getLocations(int pageNumber, int pageSize, String filterRegion, String filterCity, String sortField,
            String sortOrder) throws Exception;

    int getTotalLocationCount(String filterRegion, String filterCity) throws Exception;
    // Add other CRUD operations if needed in the future
}
