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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@WebServlet(name = "EditProfileServlet", urlPatterns = {"/editprofile"})
public class EditProfileServlet extends HttpServlet {

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
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        try {
            // Fetch the full user details from the database using the email from the session user
            Optional<User> userOptional = userRepository.findByEmail(loggedInUser.getEmail());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                request.setAttribute("user", user);
                request.getRequestDispatcher("/WEB-INF/jsp/customer/edit-profile.jsp").forward(request, response);
            } else {
                // User not found in DB, possibly an old session or data inconsistency
                session.invalidate(); // Invalidate session
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=userNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error retrieving user profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response); // Assuming an error.jsp
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        try {
            Optional<User> userOptional = userRepository.findByEmail(loggedInUser.getEmail());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Get parameters from the form
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String phoneNumber = request.getParameter("phoneNumber");
                String idCardNumber = request.getParameter("idCardNumber");
                String dateOfBirthStr = request.getParameter("dateOfBirth");
                String gender = request.getParameter("gender");
                String address = request.getParameter("address");

                // Update user object
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setIdCardNumber(idCardNumber);
                user.setGender(gender);
                user.setAddress(address);

                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    user.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
                } else {
                    user.setDateOfBirth(null); // Allow clearing date of birth
                }

                userRepository.update(user);
                session.setAttribute("loggedInUser", user); // Update user in session
                request.setAttribute("successMessage", "Profile updated successfully!");
                response.sendRedirect(request.getContextPath() + "/customer/profile"); // Redirect to profile page
            } else {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=userNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error updating profile: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/customer/profile"); // Redirect to profile page even on error
        }
    }
}
