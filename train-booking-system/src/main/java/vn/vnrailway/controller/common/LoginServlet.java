/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package vn.vnrailway.controller.common;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.utils.HashPassword;

/**
 *
 * @author admin
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     * 
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/jsp/authentication/login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * 
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String identifier = request.getParameter("identifier");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe"); // "on" if checked, null otherwise

        UserRepository userRepository = new UserRepositoryImpl();
        Optional<User> userOptional = Optional.empty();
        String loginIdentifier = identifier; // To store the identifier used for login (email or phone)

        try {
            if (identifier != null && !identifier.trim().isEmpty()) {
                // Check if the identifier is an email or a phone number
                if (identifier.contains("@")) { // Simple check for email
                    userOptional = userRepository.findByEmail(identifier);
                } else { // Assume it's a phone number
                    userOptional = userRepository.findByPhone(identifier);
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error during login", e);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isActive()) {
                request.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa, xin vui lòng liên hệ với email admin@vnr.com để được hỗ trợ thêm!");
                request.getRequestDispatcher("/WEB-INF/jsp/authentication/login.jsp").forward(request, response);
                return;
            }
            if (HashPassword.checkPassword(password, user.getPasswordHash())) {
                try {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.update(user);
                } catch (SQLException e) {
                    // Log the exception, but allow the user to log in.
                    // In a production environment, a proper logging framework should be used.
                    e.printStackTrace();
                }
                HttpSession session = request.getSession(true); // Ensure session is created if not existing
                session.setAttribute("loggedInUser", user);

                // Handle "Remember Me" functionality
                if ("on".equals(rememberMe)) {
                    // Set cookies for email/phone and password
                    Cookie identifierCookie = new Cookie("rememberedIdentifier", loginIdentifier);
                    identifierCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                    response.addCookie(identifierCookie);

                    Cookie passwordCookie = new Cookie("rememberedPassword", password); // Store plain password for
                                                                                        // client-side pre-fill
                    passwordCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                    response.addCookie(passwordCookie);

                    Cookie rememberMeCookie = new Cookie("rememberMeChecked", "true");
                    rememberMeCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                    response.addCookie(rememberMeCookie);

                } else {
                    // Remove "Remember Me" cookies if unchecked
                    Cookie identifierCookie = new Cookie("rememberedIdentifier", "");
                    identifierCookie.setMaxAge(0); // Delete cookie
                    response.addCookie(identifierCookie);

                    Cookie passwordCookie = new Cookie("rememberedPassword", "");
                    passwordCookie.setMaxAge(0); // Delete cookie
                    response.addCookie(passwordCookie);

                    Cookie rememberMeCookie = new Cookie("rememberMeChecked", "");
                    rememberMeCookie.setMaxAge(0); // Delete cookie
                    response.addCookie(rememberMeCookie);
                }

                String role = user.getRole();
                switch (role) {
                    case "Admin":
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                        break;
                    case "Staff":
                        response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                        break;
                    case "Manager":
                        response.sendRedirect(request.getContextPath() + "/managerDashboard");
                        break;
                    case "Customer":
                        response.sendRedirect(request.getContextPath() + "/landing");
                        break;
                    default:
                        response.sendRedirect(request.getContextPath() + "/WEB-INF/jsp/authentication/login.jsp");
                        break;
                }
            } else {
                request.setAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng, vui lòng nhập lại!");
                request.getRequestDispatcher("/WEB-INF/jsp/authentication/login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("errorMessage", "Tài khoản hoặc mật khẩu không đúng, vui lòng nhập lại!");
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/login.jsp").forward(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     * 
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
