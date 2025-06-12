/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package vn.vnrailway.controller.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.utils.HashPassword; // Import HashPassword for hashing the password

/**
 *
 * @author admin
 */

@WebServlet("/register")

public class RegisterServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * 
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    // Remove processRequest as it's not needed for MVC flow
    // protected void processRequest(HttpServletRequest request, HttpServletResponse
    // response)
    // throws ServletException, IOException {
    // response.setContentType("text/html;charset=UTF-8");
    // try (PrintWriter out = response.getWriter()) {
    // out.println("<!DOCTYPE html>");
    // out.println("<html>");
    // out.println("<head>");
    // out.println("<title>Servlet RegisterServlet</title>");
    // out.println("</head>");
    // out.println("<body>");
    // out.println("<h1>Servlet RegisterServlet at " + request.getContextPath () +
    // "</h1>");
    // out.println("</body>");
    // out.println("</html>");
    // }
    // }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
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
        request.getRequestDispatcher("/WEB-INF/jsp/authentication/register.jsp").forward(request, response);
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
        String fullName = request.getParameter("FullName");
        String phoneNumber = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String idCardNumber = request.getParameter("idCardNumber"); // Assuming this parameter exists in register.jsp

        String errorMessage = null;

        // 1. Password length validation
        if (password.length() < 8) {
            errorMessage = "Mật khẩu phải có ít nhất 8 ký tự.";
        } else if (!password.equals(confirmPassword)) {
            errorMessage = "Mật khẩu xác nhận không khớp.";
        }

        // 2. Email format validation
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

        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getRequestDispatcher("/WEB-INF/jsp/authentication/register.jsp").forward(request, response);
            return; // Stop further processing if there's an error
        }

        // If all validations pass, proceed with user registration
        UserRepository userRepository = new UserRepositoryImpl();
        try {
            // Check if email already exists
            if (userRepository.findByEmail(email).isPresent()) {
                errorMessage = "Email đã được đăng ký.";
            } else {
                // Hash the password before saving
                String hashedPassword = HashPassword.hashPassword(password);

                User newUser = new User(fullName, hashedPassword, fullName, email, phoneNumber, idCardNumber, "",
                        "Customer"); // Default role "Customer"

                userRepository.save(newUser);
                response.sendRedirect(request.getContextPath() + "/login?registrationSuccess=true"); // Redirect to
                                                                                                     // /login servlet
                return; // Important to return after redirect
            }
        } catch (SQLException e) {
            errorMessage = "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại.";
            e.printStackTrace(); // Log the exception for debugging
        }

        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/jsp/authentication/register.jsp").forward(request, response);
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
