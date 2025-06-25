package vn.vnrailway.controller.manager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
// import com.google.gson.Gson; // Gson is no longer used for this action
import java.time.format.DateTimeFormatter;
import java.util.HashMap; // Keep HashMap and Map as they are used for userMap
import java.util.Map;
// We might need to add other imports if manual JSON string building requires them,
// but for simple cases, it might not.

@WebServlet(name = "ManageStaffsServlet", urlPatterns = { "/manageStaffs" })
public class ManageStaffsServlet extends HttpServlet {
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        userRepository = new UserRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("getStaff".equals(action)) {
            try {
                String userIdParam = request.getParameter("userID");
                if (userIdParam == null || userIdParam.isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("userID parameter is missing");
                    return;
                }
                int userID = Integer.parseInt(userIdParam);
                Optional<User> userOptional = userRepository.findById(userID);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userID", user.getUserID());
                    // It's good practice to not send password hashes in API responses
                    // userMap.put("passwordHash", user.getPasswordHash());
                    userMap.put("fullName", user.getFullName());
                    userMap.put("email", user.getEmail());
                    userMap.put("phoneNumber", user.getPhoneNumber());
                    userMap.put("idCardNumber", user.getIdCardNumber());
                    userMap.put("address", user.getAddress());
                    userMap.put("role", user.getRole());
                    userMap.put("isActive", user.isActive());

                    // Format LocalDateTime fields into ISO standard strings
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                    if (user.getCreatedAt() != null) {
                        userMap.put("createdAt", user.getCreatedAt().format(dateTimeFormatter));
                    } else {
                        userMap.put("createdAt", null);
                    }
                    // Set lastLogin to null as requested
                    userMap.put("lastLogin", null);

                    // Manual JSON string construction
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("{");
                    appendJsonField(jsonBuilder, "userID", userMap.get("userID"), false);
                    appendJsonField(jsonBuilder, "userName", userMap.get("userName"), false);
                    appendJsonField(jsonBuilder, "fullName", userMap.get("fullName"), false);
                    appendJsonField(jsonBuilder, "email", userMap.get("email"), false);
                    appendJsonField(jsonBuilder, "phoneNumber", userMap.get("phoneNumber"), false);
                    appendJsonField(jsonBuilder, "idCardNumber", userMap.get("idCardNumber"), false);
                    appendJsonField(jsonBuilder, "address", userMap.get("address"), false);
                    appendJsonField(jsonBuilder, "role", userMap.get("role"), false);
                    appendJsonField(jsonBuilder, "isActive", userMap.get("isActive"), false);
                    appendJsonField(jsonBuilder, "createdAt", userMap.get("createdAt"), false);
                    appendJsonField(jsonBuilder, "lastLogin", userMap.get("lastLogin"), true); // Last field
                    jsonBuilder.append("}");
                    String jsonOutput = jsonBuilder.toString();

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(jsonOutput);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Staff user not found");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Invalid userID format");
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Error retrieving staff user: " + e.getMessage());
            }
            return; // Ensure no further processing after handling getStaff
        }

        // Default GET behavior: display the list of staff users
        try {
            List<User> staffUsers = userRepository.findByRole("Staff"); // Or "Manager", "Staff" depending on your needs
            request.setAttribute("staffUsers", staffUsers);

            // Retrieve and clear flash messages from session
            String successMessage = (String) request.getSession().getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                request.getSession().removeAttribute("successMessage");
            }
            String errorMessage = (String) request.getSession().getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                request.getSession().removeAttribute("errorMessage");
            }

            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageStaffs.jsp").forward(request, response);
        } catch (SQLException e) {
            // Log the error and set an error message for the user
            e.printStackTrace(); // Consider using a logger
            request.setAttribute("errorMessage", "Error retrieving staff users: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageStaffs.jsp").forward(request, response);
        } catch (Exception e) {
            // Catch any other unexpected errors during forwarding or session handling
            e.printStackTrace(); // Consider using a logger
            request.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/manager/manageStaffs.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle add, edit, delete operations here
        String mainAction = request.getParameter("action");
        if (mainAction != null) {
            try {
                switch (mainAction) {
                    case "add":
                        String fullName = request.getParameter("fullName");
                        String email = request.getParameter("email");
                        String phoneNumber = request.getParameter("phoneNumber");
                        String idCardNumber = request.getParameter("idCardNumber");
                        String password = request.getParameter("password");
                        String role = request.getParameter("role"); // Read role
                        boolean isActive = request.getParameter("isActive") != null;

                        User newUser = new User();
                        newUser.setFullName(fullName);
                        newUser.setEmail(email);
                        newUser.setPhoneNumber(phoneNumber);
                        newUser.setIdCardNumber(idCardNumber);
                        newUser.setPasswordHash(password); // Hash the password before saving
                        newUser.setRole(role); // Set role from request
                        newUser.setActive(isActive);

                        userRepository.save(newUser);
                        request.getSession().setAttribute("successMessage", "Staff member added successfully.");
                        break;
                    case "edit":
                        int userIDToEdit = Integer.parseInt(request.getParameter("userID"));
                        String fullNameEdit = request.getParameter("fullName");
                        String emailEdit = request.getParameter("email");
                        String phoneNumberEdit = request.getParameter("phoneNumber");
                        String idCardNumberEdit = request.getParameter("idCardNumber");
                        // String passwordEdit = request.getParameter("password"); // Password editing
                        // removed for managers
                        String roleEdit = request.getParameter("role"); // Read role
                        boolean isActiveEdit = request.getParameter("isActive") != null;

                        Optional<User> userToUpdateOptional = userRepository.findById(userIDToEdit);
                        if (userToUpdateOptional.isPresent()) {
                            User userToUpdate = userToUpdateOptional.get();
                            userToUpdate.setFullName(fullNameEdit);
                            userToUpdate.setEmail(emailEdit);
                            userToUpdate.setPhoneNumber(phoneNumberEdit);
                            userToUpdate.setIdCardNumber(idCardNumberEdit);
                            // Password update logic removed for existing staff by manager
                            // if (passwordEdit != null && !passwordEdit.isEmpty()) {
                            // userToUpdate.setPasswordHash(passwordEdit); // Hash the password before
                            // saving
                            // }
                            userToUpdate.setRole(roleEdit); // Set role from request
                            userToUpdate.setActive(isActiveEdit);
                            boolean updated = userRepository.update(userToUpdate);
                            if (updated) {
                                request.getSession().setAttribute("successMessage",
                                        "Staff member updated successfully.");
                            } else {
                                request.getSession().setAttribute("errorMessage",
                                        "Failed to update staff member. User not found or data unchanged.");
                            }
                        } else {
                            request.getSession().setAttribute("errorMessage",
                                    "Failed to update staff member. User with ID " + userIDToEdit + " not found.");
                        }
                        break;
                    case "delete":
                        int userID = Integer.parseInt(request.getParameter("userID"));
                        userRepository.delete(userID);
                        request.getSession().setAttribute("successMessage", "Staff member deleted successfully.");
                        break;
                    default:
                        request.getSession().setAttribute("errorMessage", "Unknown action: " + mainAction);
                        break;
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Log the error
                request.getSession().setAttribute("errorMessage",
                        "Database error performing staff action: " + e.getMessage());
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Log the error
                request.getSession().setAttribute("errorMessage", "Invalid user ID format for action: " + mainAction);
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                request.getSession().setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            }
        }
        response.sendRedirect(request.getContextPath() + "/manageStaffs"); // Redirect to GET to display messages
    }

    // Helper method to append a field to the JSON string
    private void appendJsonField(StringBuilder builder, String key, Object value, boolean isLast) {
        builder.append("\"").append(key).append("\":");
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String) {
            // Escape special characters in string values if necessary, for simplicity,
            // basic escaping for quotes.
            String stringValue = (String) value;
            builder.append("\"").append(stringValue.replace("\"", "\\\"")).append("\"");
        } else if (value instanceof Number || value instanceof Boolean) {
            builder.append(value.toString());
        } else { // Fallback for other types, assuming their toString() is appropriate or they
                 // are already formatted strings
            builder.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
        }
        if (!isLast) {
            builder.append(",");
        }
    }
}
