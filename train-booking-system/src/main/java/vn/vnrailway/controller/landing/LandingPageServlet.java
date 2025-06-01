package vn.vnrailway.controller.landing;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

import vn.vnrailway.dao.StationRepository;
import vn.vnrailway.dao.impl.StationRepositoryImpl;
import vn.vnrailway.model.Station;
import vn.vnrailway.dao.LocationRepository;
import vn.vnrailway.dao.impl.LocationRepositoryImpl;
import vn.vnrailway.dto.PopularRouteDTO;
import vn.vnrailway.model.Location;

@WebServlet(name = "LandingPageServlet", urlPatterns = { "/landing", "" })
public class LandingPageServlet extends HttpServlet {

    private StationRepository stationRepository;
    private LocationRepository locationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAOs. Consider dependency injection for a more robust
        // application.
        stationRepository = new StationRepositoryImpl();
        locationRepository = new LocationRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        List<Station> stationList = new ArrayList<>(); // Initialize to prevent null in JSP
        List<Location> locationList = new ArrayList<>(); // Initialize for locations
        List<PopularRouteDTO> popularRouteList = new ArrayList<>();
        String errorMessage = null;
        String locationErrorMessage = null;
        String popularRouteErrorMessage = null;
        Location hanoiLocation = null;

        try {
            stationList = stationRepository.findAll();
        } catch (SQLException e) {
            errorMessage = "Error fetching station data from the database: " + e.getMessage();
            // Log the error more formally in a real application
            System.err.println(errorMessage);
            e.printStackTrace();
        } catch (Exception e) { // Catch broader exceptions from DBContext if any
            errorMessage = "A general error occurred while fetching station data: " + e.getMessage();
            System.err.println(errorMessage);
            e.printStackTrace();
        }

        try {
            locationList = locationRepository.getAllLocations();
            // Find Hanoi (LocationID 1)
            for (Location loc : locationList) {
                if (loc.getLocationID() == 1) { // Assuming Ha Noi is LocationID 1
                    hanoiLocation = loc;
                    break;
                }
            }

            if (hanoiLocation != null) {
                for (Location originLoc : locationList) {
                    if (originLoc.getLocationID() != hanoiLocation.getLocationID()) {
                        // Create DTO for route from originLoc to Hanoi
                        PopularRouteDTO route = new PopularRouteDTO();
                        route.setOriginName(originLoc.getLocationName());
                        route.setDestinationName(hanoiLocation.getLocationName());
                        // Use origin location's image for the card background
                        route.setBackgroundImageUrl(request.getContextPath() + "/assets/images/landing/stations/station"
                                + originLoc.getLocationID() + ".jpg");
                        route.setOriginLocationId(String.valueOf(originLoc.getLocationID()));
                        // route.setDestinationLocationId(String.valueOf(hanoiLocation.getLocationID()));
                        // // Reverted

                        // Placeholder data for other fields - replace with actual data logic if
                        // available
                        route.setTripsPerDay(String.valueOf((int) (Math.random() * 5) + 2)); // Random 2-6 trips
                        route.setDistance(String.valueOf((int) (Math.random() * 1500) + 200) + " km"); // Random
                                                                                                       // distance
                        route.setPopularTrainNames(
                                "SE" + ((int) (Math.random() * 8) + 1) + ", TN" + ((int) (Math.random() * 4) + 1));

                        popularRouteList.add(route);
                        if (popularRouteList.size() >= 6)
                            break; // Limit to 6 popular routes for display
                    }
                }
            } else {
                popularRouteErrorMessage = "Hanoi location (ID 1) not found. Cannot generate popular routes to Hanoi.";
            }

        } catch (Exception e) { // Catch exceptions from getAllLocations or DTO creation
            locationErrorMessage = "Error fetching location data or preparing popular routes: " + e.getMessage();
            System.err.println(locationErrorMessage);
            e.printStackTrace();
        }

        request.setAttribute("stationList", stationList);
        request.setAttribute("locationList", locationList);
        request.setAttribute("popularRouteList", popularRouteList);

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        if (locationErrorMessage != null) {
            request.setAttribute("locationErrorMessage", locationErrorMessage);
        }
        if (popularRouteErrorMessage != null) {
            request.setAttribute("popularRouteErrorMessage", popularRouteErrorMessage);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/landing/landing-page.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward POST requests to doGet, or handle them separately if needed
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to display the landing page";
    }
}
