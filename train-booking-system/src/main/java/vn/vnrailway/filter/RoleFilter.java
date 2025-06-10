package vn.vnrailway.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.model.User;

@WebFilter("/*")
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Allow access to static resources and public pages
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/assets/") ||
                path.equals("/login.jsp") || path.equals("/register.jsp") || path.equals("/forgotpassword.jsp") ||
                path.equals("/enterotp.jsp") || path.equals("/newpassword.jsp") || path.equals("/index.jsp") ||
                path.equals("/login") || path.equals("/register") || path.equals("/logout") ||
                path.equals("/forgot-password") || path.equals("/verify-otp") || path.equals("/reset-password") ||
                path.startsWith("/trip/") || path.startsWith("/train-info/") || path.startsWith("/check-booking/") ||
                path.equals("/WEB-INF/jsp/common/unauthorized.jsp")) { // Added unauthorized.jsp to public paths for
                                                                       // forwarding
            chain.doFilter(request, response);
            return;
        }

        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null; // Changed "user" to
                                                                                            // "loggedInUser"

        if (user == null) {
            // No user logged in, redirect to login page for any protected resource
            if (!isPublicPage(path, httpRequest.getContextPath())) { // Check if it's not already a public page attempt
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp");
                return;
            }
            // If it was an attempt to access a public page but somehow missed the initial
            // check, let it pass (should not happen with current logic)
            chain.doFilter(request, response);
            return;
        }

        String role = user.getRole() != null ? user.getRole().toUpperCase() : null; // Convert role to uppercase

        if ("ADMIN".equals(role)) {
            // Admin has access to all pages, or specific admin pages
            if (path.startsWith("/admin/") || path.equals("/admin-dashboard")) { // Added /admin-dashboard
                chain.doFilter(request, response);
            } else if (isCommonPage(path)) {
                chain.doFilter(request, response);
            } else {
                // Unauthorized access attempt by Admin
                request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
                return;
            }
        } else if ("MANAGER".equals(role)) {
            // Manager specific access
            // Allow access if path is exactly /managerDashboard OR starts with /manager/
            if (path.equals("/managerDashboard") || path.startsWith("/manager/")) {
                chain.doFilter(request, response);
            } else if (isCommonPage(path)) {
                chain.doFilter(request, response);
            } else {
                // Unauthorized access attempt by Manager
                request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
                return;
            }
        } else if ("CUSTOMER".equals(role)) {
            // Customer specific access
            if (path.startsWith("/customer/") || path.equals("/changepassword.jsp")
                    || path.equals("/change-password") || path.equals("/landing")) { // Added /landing
                chain.doFilter(request, response);
            } else if (isCommonPage(path)) {
                chain.doFilter(request, response);
            } else {
                // Unauthorized access attempt by Customer
                request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
                return;
            }
        } else {
            // Unknown role or role not handled, redirect to login (or unauthorized)
            request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
            return;
        }
    }

    // Helper method to check if a path is one of the explicitly public-facing pages
    private boolean isPublicPage(String path, String contextPath) {
        return path.equals("/login.jsp") || path.equals("/register.jsp") || path.equals("/forgotpassword.jsp") ||
                path.equals("/enterotp.jsp") || path.equals("/newpassword.jsp") || path.equals("/index.jsp") ||
                path.equals(contextPath + "/login") || path.equals(contextPath + "/register")
                || path.equals(contextPath + "/logout") ||
                path.equals(contextPath + "/forgot-password") || path.equals(contextPath + "/verify-otp")
                || path.equals(contextPath + "/reset-password");
    }

    private boolean isCommonPage(String path) {
        // Define common pages accessible by multiple roles after login
        return path.equals("/profile") || path.equals("/update-profile");
        // Add other common paths here e.g. path.equals("/settings")
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}
