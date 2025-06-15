package vn.vnrailway.controller.manager;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.TripRepository;
import vn.vnrailway.dao.impl.TripRepositoryImpl;
import vn.vnrailway.dto.ManageTripViewDTO;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/manageTrips")
public class ManageTripsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TripRepository tripRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        tripRepository = new TripRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Default action
        }

        try {
            switch (action) {
                // Add cases for "add", "edit", "delete" later
                // case "showAddForm":
                // showAddForm(request, response);
                // break;
                // case "insert":
                // insertTrip(request, response);
                // break;
                // case "delete":
                // deleteTrip(request, response);
                // break;
                // case "showEditForm":
                // showEditForm(request, response);
                // break;
                // case "update":
                // updateTrip(request, response);
                // break;
                default: // list
                    listTrips(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException("Database access error in ManageTripsServlet", ex);
        }
    }

    private void listTrips(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        String searchTerm = request.getParameter("searchTerm");
        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        // Set defaults if parameters are null or empty for initial load or invalid
        // submissions
        if (sortField == null || sortField.isEmpty()) {
            sortField = "departureDateTime"; // Default sort field
        }
        if (sortOrder == null || sortOrder.isEmpty()
                || (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder))) {
            sortOrder = "DESC"; // Default sort order
        }
        if (searchTerm == null) {
            searchTerm = ""; // Default to empty search string
        }

        // TODO: Update TripRepository's findAllForManagerView to accept these
        // parameters
        // For now, it will ignore them, but we set attributes for the JSP
        List<ManageTripViewDTO> listTrips = tripRepository.findAllForManagerView(searchTerm, sortField, sortOrder);

        request.setAttribute("listTrips", listTrips);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("sortField", sortField);
        request.setAttribute("sortOrder", sortOrder);

        request.getRequestDispatcher("/WEB-INF/jsp/manager/manageTrips.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle any POST requests if needed for trip management (e.g., adding,
        // editing, deleting trips)
        // For now, can just forward to doGet or handle separately.
        doGet(request, response);
    }
}
