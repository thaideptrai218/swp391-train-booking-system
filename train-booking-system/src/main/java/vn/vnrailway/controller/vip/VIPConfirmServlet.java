package vn.vnrailway.controller.vip;

import vn.vnrailway.dao.VIPCardTypeRepository;
import vn.vnrailway.dao.impl.VIPCardTypeRepositoryImpl;
import vn.vnrailway.model.User;
import vn.vnrailway.model.VIPCardType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/vip/confirm")
public class VIPConfirmServlet extends HttpServlet {
    
    private VIPCardTypeRepository vipCardTypeRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        vipCardTypeRepository = new VIPCardTypeRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        // Check if user is logged in
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + 
                java.net.URLEncoder.encode("/vip/purchase", "UTF-8"));
            return;
        }
        
        // Get selected VIP card type ID from session
        Integer selectedVIPCardTypeId = (Integer) session.getAttribute("selectedVIPCardTypeId");
        if (selectedVIPCardTypeId == null) {
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=no_selection");
            return;
        }
        
        try {
            // Fetch VIP card type details
            Optional<VIPCardType> vipCardTypeOpt = vipCardTypeRepository.findById(selectedVIPCardTypeId);
            if (!vipCardTypeOpt.isPresent()) {
                session.removeAttribute("selectedVIPCardTypeId");
                response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_card");
                return;
            }
            
            VIPCardType selectedVIPCard = vipCardTypeOpt.get();
            
            // Set attributes for JSP
            request.setAttribute("user", loggedInUser);
            request.setAttribute("selectedVIPCard", selectedVIPCard);
            request.setAttribute("expiryDate", calculateExpiryDate(selectedVIPCard.getDurationMonths()));
            
            // Forward to confirmation JSP
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-confirm.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi xử lý thông tin. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        // Check if user is logged in
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Get selected VIP card type ID from session
        Integer selectedVIPCardTypeId = (Integer) session.getAttribute("selectedVIPCardTypeId");
        if (selectedVIPCardTypeId == null) {
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=session_expired");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("confirm".equals(action)) {
            // User confirmed the purchase, redirect to payment
            try {
                // Validate VIP card type still exists
                if (!vipCardTypeRepository.existsById(selectedVIPCardTypeId)) {
                    session.removeAttribute("selectedVIPCardTypeId");
                    response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_card");
                    return;
                }
                
                // Redirect to VIP payment initiation API
                response.sendRedirect(request.getContextPath() + "/api/vip/payment/initiate");
                
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/vip/confirm?error=database_error");
            }
            
        } else if ("cancel".equals(action)) {
            // User cancelled, clear session and redirect back to VIP purchase
            session.removeAttribute("selectedVIPCardTypeId");
            response.sendRedirect(request.getContextPath() + "/vip/purchase");
            
        } else {
            // Invalid action
            response.sendRedirect(request.getContextPath() + "/vip/confirm?error=invalid_action");
        }
    }
    
    /**
     * Calculate VIP card expiry date based on duration in months
     */
    private String calculateExpiryDate(int durationMonths) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime expiry = now.plusMonths(durationMonths);
        
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        return expiry.format(formatter);
    }
}