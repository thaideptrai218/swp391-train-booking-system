/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package vn.vnrailway.controller.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UserRepository userRepository = new UserRepositoryImpl();
        Optional<User> userOptional = null;
        try {
            userOptional = userRepository.findByEmail(email);
        } catch (SQLException e) {
            throw new ServletException("Database error during login", e);
        }
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Use HashPassword.checkPassword for verification
            if (HashPassword.checkPassword(password, user.getPasswordHash())) {
                HttpSession session = request.getSession();
                session.setAttribute("loggedInUser", user);
                String role = user.getRole();

                switch (role) {
                    case "admin":
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                        break;
                    case "staff":
                        response.sendRedirect(request.getContextPath() + "/staff/dashboard");
                        break;
                    case "customer":
                        response.sendRedirect(request.getContextPath() + "/customerProfile");
                        break;
                    default:
                        // Handle unknown role or default to a common dashboard
                        response.sendRedirect(request.getContextPath() + "/landing");
                        break;
                }
            } else {
                // Password mismatch
                request.setAttribute("errorMessage", "Sai email hoặc mật khẩu.");
                request.getRequestDispatcher("/login").forward(request, response);
            }
        } else {
            // User not found
            request.setAttribute("errorMessage", "Sai email hoặc mật khẩu.");
            request.getRequestDispatcher("/login").forward(request, response);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
