package vn.vnrailway.controller.staff;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.CustomerInfoDAO;
import vn.vnrailway.dao.impl.CustomerInfoImpl;
import vn.vnrailway.model.User;

@WebServlet("/staff/customer-info")
public class CustomerInfoServlet extends HttpServlet {

    private CustomerInfoDAO customerInfoDAO = new CustomerInfoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Set character encoding to handle Vietnamese characters properly
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Retrieve search and filter parameters from the request
        String searchTerm = request.getParameter("searchTerm");
        String searchField = request.getParameter("searchField");
        String genderFilter = request.getParameter("genderFilter");

        // Sanitize and validate search term
        if (searchTerm != null) {
            searchTerm = searchTerm.trim().replaceAll("\\s+", " ");
        }

        // Default to 'all' if gender filter is not specified or empty
        if (genderFilter == null || genderFilter.isEmpty()) {
            genderFilter = "all";
        }
        
        // Default search field if not specified
        if (searchField == null || searchField.isEmpty()) {
            searchField = "fullName";
        }

        // Pagination logic
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // Log the error and default to page 1
                System.err.println("Invalid page number format: " + pageParam);
                page = 1;
            }
        }

        // Fetch the filtered and paginated list of customers from the DAO
        List<User> customers = customerInfoDAO.getFilteredCustomers(searchTerm, searchField, genderFilter, page);
        
        // Get the total count of filtered customers for pagination
        int totalCustomers = customerInfoDAO.getTotalFilteredCustomers(searchTerm, searchField, genderFilter);
        
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCustomers / 10); // Assuming 10 customers per page

        // Set attributes for the JSP to render
        request.setAttribute("customers", customers);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        
        // Pass back search and filter parameters to maintain state in the form
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("searchField", searchField);
        request.setAttribute("genderFilter", genderFilter);

        // Forward the request to the JSP page for rendering
        request.getRequestDispatcher("/WEB-INF/jsp/staff/customer-info.jsp").forward(request, response);
    }
}
