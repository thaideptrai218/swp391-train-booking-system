/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package vn.vnrailway.controller.common;

import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;


/**
 *
 * @author admin
 */
public class RegisterServlet extends HttpServlet {
   
        
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    // Remove processRequest as it's not needed for MVC flow
    // protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    // throws ServletException, IOException {
    //     response.setContentType("text/html;charset=UTF-8");
    //     try (PrintWriter out = response.getWriter()) {
    //         out.println("<!DOCTYPE html>");
    //         out.println("<html>");
    //         out.println("<head>");
    //         out.println("<title>Servlet RegisterServlet</title>");  
    //         out.println("</head>");
    //         out.println("<body>");
    //         out.println("<h1>Servlet RegisterServlet at " + request.getContextPath () + "</h1>");
    //         out.println("</body>");
    //         out.println("</html>");
    //     }
    // } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        request.getRequestDispatcher("/register.jsp").forward(request, response);
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
        String fullName = request.getParameter("fullname");
        String phoneNumber = request.getParameter("phone");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String idCardNumber = request.getParameter("idCardNumber"); // Assuming this parameter exists in register.jsp

        String errorMessage = null;

        if (!password.equals(confirmPassword)) {
            errorMessage = "Mật khẩu xác nhận không khớp.";
        } else {
            UserRepository userRepository = new UserRepositoryImpl();
            try {
                // Check if email already exists
                if (userRepository.findByEmail(email).isPresent()) {
                    errorMessage = "Email đã được đăng ký.";
                } else {
                    // Assuming 'fullName' is used as 'userName' for login
                    // In a real application, you might want a separate username field or generate one.
                    // Added an empty string for 'address' as it's a new field in User model
                    User newUser = new User(fullName, password, fullName, email, phoneNumber, idCardNumber, "", "Customer"); // Default role "Customer"

                    userRepository.save(newUser);
                    response.sendRedirect(request.getContextPath() + "/login.jsp?registrationSuccess=true");
                    return; // Important to return after redirect
                }
            } catch (SQLException e) {
                errorMessage = "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại.";
                e.printStackTrace(); // Log the exception for debugging
            }
        }

        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
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
