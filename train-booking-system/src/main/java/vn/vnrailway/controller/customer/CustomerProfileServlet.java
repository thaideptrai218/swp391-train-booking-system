package vn.vnrailway.controller.customer;

import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@WebServlet("/customerprofile")
public class CustomerProfileServlet extends HttpServlet {
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        userRepository = new UserRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Do not create a new session if one doesn't exist

        if (session == null || session.getAttribute("loggedInUser") == null) {
            // User is not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        try {
            // Fetch the full user details from the database using the email from the session user
            Optional<User> userOptional = userRepository.findByEmail(loggedInUser.getEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);
            } else {
                // User not found in DB, possibly an old session or data inconsistency
                session.invalidate(); // Invalidate session
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=userNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error, e.g., show an error page or redirect
            request.setAttribute("errorMessage", "Error retrieving user profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response); // Assuming an error.jsp
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        try {
            Optional<User> userOptional = userRepository.findByEmail(loggedInUser.getEmail());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Update user information from the request
                user.setFullName(request.getParameter("fullName"));
                user.setPhoneNumber(request.getParameter("phoneNumber"));
                user.setAddress(request.getParameter("address"));
                String gender = request.getParameter("gender");
                 user.setGender(gender);

                // Parse dateOfBirth
                String dateOfBirthStr = request.getParameter("dateOfBirth");
                LocalDate dateOfBirth = null;
                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    try {
                        dateOfBirth = LocalDate.parse(dateOfBirthStr);
                    } catch (DateTimeParseException e) {
                        // Handle date parsing error
                        request.setAttribute("errorMessage", "Invalid date format. Please use yyyy-MM-dd.");
                        request.setAttribute("user", user);
                        request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);
                        return;
                    }
                }
                user.setDateOfBirth(dateOfBirth);

                userRepository.update(user); // Assuming you have an update method in your UserRepository

                request.setAttribute("successMessage", "Profile updated successfully!");
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);

            } else {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=userNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
}
