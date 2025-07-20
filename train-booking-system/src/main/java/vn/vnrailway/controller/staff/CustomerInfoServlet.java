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

@WebServlet("/customer-info")
public class CustomerInfoServlet extends HttpServlet {

    private CustomerInfoDAO customerInfoDAO = new CustomerInfoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1)
                    page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        List<User> customers = customerInfoDAO.getAllCustomers(page);
        request.setAttribute("customers", customers);
        request.setAttribute("currentPage", page);
        int totalCustomers = new CustomerInfoImpl().getTotalCustomers();
        int totalPages = (int) Math.ceil((double) totalCustomers / 10);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/jsp/staff/customer-info.jsp").forward(request, response);
    }
}