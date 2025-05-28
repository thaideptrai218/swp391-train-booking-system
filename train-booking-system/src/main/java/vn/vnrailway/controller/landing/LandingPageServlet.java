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

@WebServlet(name = "LandingPageServlet", urlPatterns = { "/landing", "" })
public class LandingPageServlet extends HttpServlet {

    private StationRepository stationRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize the DAO. Consider dependency injection for a more robust application.
        stationRepository = new StationRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        List<Station> stationList = new ArrayList<>(); // Initialize to prevent null in JSP
        String errorMessage = null;

        try {
            stationList = stationRepository.findAll();
        } catch (SQLException e) {
            errorMessage = "Error fetching station data from the database: " + e.getMessage();
            // Log the error more formally in a real application
            System.err.println(errorMessage); 
            e.printStackTrace();
        }
        
        request.setAttribute("stationList", stationList);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
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
