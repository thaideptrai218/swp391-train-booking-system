package vn.vnrailway.controller.customer;

import vn.vnrailway.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/customerProfile")
public class CustomerProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TEMPORARY: Bypass login and role check for testing purposes
        // In a real application, you would keep the authentication and authorization logic.

        User user = new User();
        user.setUserName("testcustomer"); // Corrected method name
        user.setEmail("test@example.com");
        user.setFullName("Test Customer");
        user.setPhoneNumber("123-456-7890");
        user.setAddress("123 Test Street, Test City"); // Now this method exists
        user.setRole("customer"); // Set role to customer for JSP display

        request.setAttribute("user", user);
        request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);
    }
}
