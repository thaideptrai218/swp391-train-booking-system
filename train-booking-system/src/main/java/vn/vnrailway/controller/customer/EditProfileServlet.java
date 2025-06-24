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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            request.setAttribute("errorMessage", "Lỗi khi truy xuất hồ sơ người dùng: " + e.getMessage());
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

                String errorMessage = null; // Initialize errorMessage variable

                // Update user object
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setIdCardNumber(idCardNumber);
                user.setGender(gender);
                user.setAddress(address);

        if (errorMessage == null) {
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
            Pattern emailPattern = Pattern.compile(emailRegex);
            Matcher emailMatcher = emailPattern.matcher(email);
            if (!emailMatcher.matches()) {
                errorMessage = "Email không đúng định dạng.";
            }
        }

        // 3. Phone number validation (starts with 0 and contains only digits)
        if (errorMessage == null) {
            if (!phoneNumber.startsWith("0") || !phoneNumber.matches("\\d+")) {
                errorMessage = "Số điện thoại phải bắt đầu bằng 0 và chỉ chứa các chữ số.";
            }
        }

        // 4. CCCD length and digit validation (exactly 12 digits)
        if (errorMessage == null) {
            if (idCardNumber.length() != 12 || !idCardNumber.matches("\\d+")) {
                errorMessage = "CCCD phải có đúng 12 chữ số.";
            }
        }

        // 5. Date of birth validation (must be a valid date)
        if (errorMessage == null) {
            try {
                LocalDate.parse(dateOfBirthStr);
            } catch (DateTimeParseException e) {
                errorMessage = "Ngày sinh không hợp lệ. Hãy nhập theo định dạng: YYYY-MM-DD.";
            }
        }

                if (dateOfBirthStr != null && !dateOfBirthStr.isEmpty()) {
                    user.setDateOfBirth(LocalDate.parse(dateOfBirthStr));
                } else {
                    user.setDateOfBirth(null); // Allow clearing date of birth
                }

                userRepository.update(user);
                session.setAttribute("loggedInUser", user); // Update user in session
                request.setAttribute("successMessage", "Cập nhật thành công!");
                request.setAttribute("user", user); // Set user attribute for JSP
                request.getRequestDispatcher("/WEB-INF/jsp/customer/edit-profile.jsp").forward(request, response);
            } else {
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=userNotFound");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cập nhật: " + e.getMessage());
            // Re-fetch user to populate form fields correctly in case of error
            User loggedInUserAfterError = (User) session.getAttribute("loggedInUser");
            try {
                Optional<User> userOptional = userRepository.findByEmail(loggedInUserAfterError.getEmail());
                userOptional.ifPresent(user -> request.setAttribute("user", user));
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log this secondary error
            }
            request.getRequestDispatcher("/WEB-INF/jsp/customer/edit-profile.jsp").forward(request, response);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Nhập không đúng định dạng. Hãy nhập theo định dạng: YYYY-MM-DD.");
            // Re-fetch user to populate form fields correctly in case of error
            User loggedInUserAfterError = (User) session.getAttribute("loggedInUser");
            try {
                Optional<User> userOptional = userRepository.findByEmail(loggedInUserAfterError.getEmail());
                userOptional.ifPresent(user -> request.setAttribute("user", user));
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log this secondary error
            }
            request.getRequestDispatcher("/WEB-INF/jsp/customer/edit-profile.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn: " + e.getMessage());
            // Re-fetch user to populate form fields correctly in case of error
            User loggedInUserAfterError = (User) session.getAttribute("loggedInUser");
            try {
                Optional<User> userOptional = userRepository.findByEmail(loggedInUserAfterError.getEmail());
                userOptional.ifPresent(user -> request.setAttribute("user", user));
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log this secondary error
            }
            request.getRequestDispatcher("/WEB-INF/jsp/customer/edit-profile.jsp").forward(request, response);
        }
    }
}
