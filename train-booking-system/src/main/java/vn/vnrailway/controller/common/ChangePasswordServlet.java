/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package vn.vnrailway.controller.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException; // Added import
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Added import
import vn.vnrailway.dao.UserRepository; // Added import
import vn.vnrailway.dao.impl.UserRepositoryImpl; // Added import
import vn.vnrailway.model.User; // Added import


/**
 *
 * @author admin
 */

@WebServlet("/changepassword")

public class ChangePasswordServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChangePasswordServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ChangePasswordServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

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
        request.getRequestDispatcher("/changepassword.jsp").forward(request, response);
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
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            // User is not logged in, redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmNewPassword = request.getParameter("confirmNewPassword");

        String errorMessage = null;
        String successMessage = null;

        if (!newPassword.equals(confirmNewPassword)) {
            errorMessage = "Mật khẩu mới và xác nhận mật khẩu mới không khớp.";
        } else {
            UserRepository userRepository = new UserRepositoryImpl();
            try {
                // Verify current password (plain-text comparison)
                if (currentPassword.equals(loggedInUser.getPasswordHash())) {
                    // Update user's password in the database (plain-text)
                    loggedInUser.setPasswordHash(newPassword);
                    userRepository.update(loggedInUser); // Assuming an update method exists in UserRepository

                    successMessage = "Mật khẩu đã được thay đổi thành công.";
                    // Update the session user object as well
                    session.setAttribute("loggedInUser", loggedInUser);
                } else {
                    errorMessage = "Mật khẩu hiện tại không đúng.";
                }
            } catch (SQLException e) {
                errorMessage = "Đã xảy ra lỗi khi thay đổi mật khẩu. Vui lòng thử lại.";
                e.printStackTrace(); // Log the exception for debugging
            }
        }

        request.setAttribute("errorMessage", errorMessage);
        request.setAttribute("successMessage", successMessage);
        request.getRequestDispatcher("/changepassword.jsp").forward(request, response);
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
