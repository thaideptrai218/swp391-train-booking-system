package vn.vnrailway.controller.landing;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.LocationRepository;
import vn.vnrailway.dao.impl.LocationRepositoryImpl;
import vn.vnrailway.model.Location;

@WebServlet("/all-locations")
public class AllLocationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private LocationRepository locationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        this.locationRepository = new LocationRepositoryImpl();
    }

    private static final int PAGE_SIZE = 9; // Number of locations per page

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pageParam = request.getParameter("page");
            int currentPage = 1;
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    currentPage = Integer.parseInt(pageParam);
                    if (currentPage < 1) {
                        currentPage = 1;
                    }
                } catch (NumberFormatException e) {
                    // Log or handle invalid page number format, default to 1
                    currentPage = 1;
                }
            }

            // Read filter and sort parameters
            String filterRegion = request.getParameter("filterRegion");
            String filterCity = request.getParameter("filterCity");
            String sortField = request.getParameter("sortField");
            String sortOrder = request.getParameter("sortOrder");

            // Set defaults if parameters are null or empty
            if (sortField == null || sortField.isEmpty()) {
                sortField = "locationName"; // Default sort field
            }
            if (sortOrder == null || sortOrder.isEmpty()) {
                sortOrder = "ASC"; // Default sort order
            }
            // Ensure sortOrder is either ASC or DESC
            if (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder)) {
                sortOrder = "ASC";
            }

            // Pass filter and sort parameters to repository
            // Note: The LocationRepository.getLocations and getTotalLocationCount methods
            // will need to be updated
            // to accept these new parameters. This will be done in the next step.
            List<Location> locationsOnPage = locationRepository.getLocations(currentPage, PAGE_SIZE, filterRegion,
                    filterCity, sortField, sortOrder);
            int totalLocations = locationRepository.getTotalLocationCount(filterRegion, filterCity);
            int totalPages = (int) Math.ceil((double) totalLocations / PAGE_SIZE);

            if (currentPage > totalPages && totalPages > 0) { // If requested page is out of bounds
                currentPage = totalPages;
                // Re-fetch with corrected page number, including filters and sort
                locationsOnPage = locationRepository.getLocations(currentPage, PAGE_SIZE, filterRegion, filterCity,
                        sortField, sortOrder);
            }

            request.setAttribute("allLocations", locationsOnPage);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("contextPath", request.getContextPath());

            // Pass filter and sort parameters back to JSP to maintain UI state
            request.setAttribute("filterRegion", filterRegion);
            request.setAttribute("filterCity", filterCity);
            request.setAttribute("sortField", sortField);
            request.setAttribute("sortOrder", sortOrder);

            request.getRequestDispatcher("/WEB-INF/jsp/landing/all-locations.jsp").forward(request, response);
        } catch (Exception e) {
            // Log the error and redirect to an error page or show an error message
            e.printStackTrace(); // For debugging, consider a more robust logging mechanism
            // response.sendRedirect(request.getContextPath() + "/error-page.jsp");
            throw new ServletException("Error retrieving locations for page with filters/sorting", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
