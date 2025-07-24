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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/vip/purchase")
public class VIPPurchaseServlet extends HttpServlet {
    
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
        
        try {
            // Fetch all VIP card types from database
            List<VIPCardType> allVIPCards = vipCardTypeRepository.findAll();
            
            // Group VIP cards by base type (Đồng, Bạc, Vàng, Kim Cương)
            Map<String, List<VIPCardType>> groupedVIPCards = groupVIPCardsByType(allVIPCards);
            
            // Set attributes for JSP
            request.setAttribute("groupedVIPCards", groupedVIPCards);
            request.setAttribute("isLoggedIn", loggedInUser != null);
            request.setAttribute("user", loggedInUser);
            
            // Handle pre-selected VIP card (from login redirect)
            String selectedCardId = request.getParameter("selected");
            String selectedDuration = request.getParameter("duration");
            if (selectedCardId != null && selectedDuration != null) {
                request.setAttribute("preSelectedCardId", selectedCardId);
                request.setAttribute("preSelectedDuration", selectedDuration);
            }
            
            // Forward to JSP page
            request.getRequestDispatcher("/WEB-INF/jsp/vip/vip-purchase.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra khi tải thông tin thẻ VIP. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        
        // Get selected VIP card info from form
        String vipCardTypeIdStr = request.getParameter("vipCardTypeId");
        
        if (vipCardTypeIdStr == null || vipCardTypeIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_selection");
            return;
        }
        
        try {
            int vipCardTypeId = Integer.parseInt(vipCardTypeIdStr);
            
            // Check if user is logged in
            if (loggedInUser == null) {
                // Redirect to login with return URL
                String returnUrl = "/vip/purchase?selected=" + vipCardTypeId;
                response.sendRedirect(request.getContextPath() + "/login?redirect=" + 
                    java.net.URLEncoder.encode(returnUrl, "UTF-8"));
                return;
            }
            
            // Validate VIP card type exists
            if (!vipCardTypeRepository.existsById(vipCardTypeId)) {
                response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_card");
                return;
            }
            
            // Store selected VIP card in session for confirmation page
            session.setAttribute("selectedVIPCardTypeId", vipCardTypeId);
            
            // Redirect to confirmation page
            response.sendRedirect(request.getContextPath() + "/vip/confirm");
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=invalid_selection");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/vip/purchase?error=database_error");
        }
    }
    
    /**
     * Group VIP cards by their base type (without duration suffix)
     * This creates a structure like:
     * {
     *   "Thẻ Đồng": [3-month variant, 12-month variant],
     *   "Thẻ Bạc": [3-month variant, 12-month variant],
     *   ...
     * }
     */
    private Map<String, List<VIPCardType>> groupVIPCardsByType(List<VIPCardType> allVIPCards) {
        Map<String, List<VIPCardType>> grouped = new HashMap<>();
        
        for (VIPCardType vipCard : allVIPCards) {
            String baseType = vipCard.getBaseTypeName();
            
            grouped.computeIfAbsent(baseType, k -> new ArrayList<>()).add(vipCard);
        }
        
        // Sort each group by duration (3 months first, then 12 months)
        for (List<VIPCardType> cards : grouped.values()) {
            cards.sort((a, b) -> Integer.compare(a.getDurationMonths(), b.getDurationMonths()));
        }
        
        return grouped;
    }
}