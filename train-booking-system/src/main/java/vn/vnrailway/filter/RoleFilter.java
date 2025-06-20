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
                path.startsWith("/public/") || path.startsWith("/authentication/") || // Allows servlets mapped under
                                                                                      // /authentication/

                // Other public JSPs (if any, ensure they are not in WEB-INF if directly
                // accessed)
                path.equals("/index.jsp") || // Assuming index.jsp is at the root and public
                // JSPs for landing, trip, train-info, check-booking, etc., are now in
                // WEB-INF/jsp/public/
                // and accessed via their respective servlets.
                // Direct checks for these JSP root paths are removed.

                // Servlet/public endpoints
                path.equals("/login") || path.equals("/register") || path.equals("/logout") ||
                path.equals("/forgot-password") || path.equals("/verify-otp") || path.equals("/reset-password") ||
                path.equals("/forgotpassword") || path.equals("/newpassword") || path.equals("/enterotp") ||
                path.equals("/changepassword") || path.equals("/checkBooking") ||
                path.equals("/searchTrip") || path.equals("/searchTripBackground") ||
                path.equals("/storeRoute") || path.equals("/getCoachSeatsWithPrice") ||
                path.equals("/all-locations") || path.equals("/landing") || path.equals("/terms") ||
                path.equals("/api/stations/all") ||

                // Trip & train info related public paths
                path.startsWith("/trip/") || path.startsWith("/train-info/") || path.startsWith("/check-booking/") ||

                // Error or common
                path.equals("/WEB-INF/jsp/common/unauthorized.jsp")) {
            chain.doFilter(request, response);
            return;
        }

        User user = (session != null) ? (User) session.getAttribute("loggedInUser") : null; // Changed "user" to
                                                                                            // "loggedInUser"

        if (user == null) {
            // No user logged in, redirect to login page for any protected resource
            if (!isPublicPage(path, httpRequest.getContextPath())) { // Check if it's not already a public page attempt
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/login"); // Redirect to /login servlet
                return;
            }
            // If it was an attempt to access a public page but somehow missed the initial
            // check, let it pass (should not happen with current logic)
            chain.doFilter(request, response);
            return;
        }

        String role = user.getRole() != null ? user.getRole().toLowerCase() : null; // Convert role to uppercase

        if ("admin".equals(role)) {
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
        } else if ("manager".equals(role)) {
            // Manager specific access
            if (path.equals("/managerDashboard") || path.startsWith("/manager/") ||
                    path.equals("/manageTrips") ||
                    path.equals("/manageRoutes") ||
                    path.equals("/manageStations") ||
                    path.equals("/manageStaffs") ||
                    path.equals("/routeDetail") ||
                    path.equals("/tripDetail") ||
                    path.equals("/manage-trains-seats") ||
                    path.equals("/managePrice") ||
                    path.equals("/managerStaff")) {
                chain.doFilter(request, response);
            } else if (isCommonPage(path)) {
                chain.doFilter(request, response);
            } else {
                // Unauthorized access attempt by Manager
                request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
                return;
            }
        } else if ("staff".equals(role)) {
            // Manager specific access
            // Allow access if path is exactly /managerDashboard OR starts with /manager/
            // if (path.equals("/managerDashboard") || path.startsWith("/manager/")) {
            // chain.doFilter(request, response);
            // } else if (isCommonPage(path)) {
            // chain.doFilter(request, response);
            // } else {
            // // Unauthorized access attempt by Manager
            // request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request,
            // response);
            // return;
            // }
        } else if ("customer".equals(role)) {
            // Customer specific access
            if (path.startsWith("/customer/") || path.equals("/changepassword") // Check servlet path
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
        // Check for servlet paths that handle public functionality
        // JSPs like login.jsp, register.jsp are in WEB-INF and accessed via these
        // servlets
        return path.equals("/index.jsp") || // Assuming index.jsp is at the root and public
                path.equals(contextPath + "/login") ||
                path.equals(contextPath + "/register") ||
                path.equals(contextPath + "/logout") ||
                path.equals(contextPath + "/forgotpassword") || // Ensure these match servlet mappings
                path.equals(contextPath + "/enterotp") ||
                path.equals(contextPath + "/newpassword") ||
                path.equals(contextPath + "/changepassword") ||
                // Add other public servlet paths here, and paths for static resources if not
                // covered by startsWith
                path.startsWith(contextPath + "/css/") ||
                path.startsWith(contextPath + "/js/") ||
                path.startsWith(contextPath + "/assets/") ||
                path.startsWith(contextPath + "/public/") ||
                path.startsWith(contextPath + "/authentication/"); // Covers servlets like /authentication/someAction
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
