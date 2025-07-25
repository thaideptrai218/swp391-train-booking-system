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
import vn.vnrailway.model.Location;
import vn.vnrailway.model.FeaturedRoute;
import vn.vnrailway.dao.FeaturedRouteRepository;
import vn.vnrailway.dao.impl.FeaturedRouteRepositoryImpl;

@WebServlet(name = "LandingPageServlet", urlPatterns = { "/landing", "" })
public class LandingPageServlet extends HttpServlet {

    private StationRepository stationRepository;
    private LocationRepository locationRepository;
    private FeaturedRouteRepository featuredRouteRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAOs. Consider dependency injection for a more robust
        // application.
        stationRepository = new StationRepositoryImpl();
        locationRepository = new LocationRepositoryImpl();
        featuredRouteRepository = new FeaturedRouteRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        List<Station> stationList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        List<FeaturedRoute> featuredRouteList = new ArrayList<>();
        String errorMessage = null;
        String locationErrorMessage = null;
        String featuredRouteErrorMessage = null;

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
        } catch (Exception e) { // Catch exceptions from getAllLocations
            locationErrorMessage = "Error fetching location data: " + e.getMessage();
            System.err.println(locationErrorMessage);
            e.printStackTrace();
        }

        try {
            featuredRouteList = featuredRouteRepository.findAll();
        } catch (SQLException e) {
            featuredRouteErrorMessage = "Error fetching featured routes: " + e.getMessage();
            System.err.println(featuredRouteErrorMessage);
            e.printStackTrace();
        }

        request.setAttribute("stationList", stationList);
        request.setAttribute("locationList", locationList);
        request.setAttribute("featuredRouteList", featuredRouteList);

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        if (locationErrorMessage != null) {
            request.setAttribute("locationErrorMessage", locationErrorMessage);
        }
        if (featuredRouteErrorMessage != null) {
            request.setAttribute("featuredRouteErrorMessage", featuredRouteErrorMessage);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/public/landing/landing-page.jsp");
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
