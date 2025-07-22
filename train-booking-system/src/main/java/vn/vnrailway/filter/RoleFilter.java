package vn.vnrailway.filter;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.model.User;

/**
 * Modern Role-Based Access Control Filter
 * 
 * Features:
 * - Pattern-based URL matching for better maintainability
 * - Exclusive role-based permissions (each role has specific access)
 * - Clear separation of public, authenticated, and role-specific resources
 * - Configurable access rules
 * - Better error handling and logging
 */
public class RoleFilter implements Filter {

    // Available roles in the system (for reference and validation)
    private static final Set<String> VALID_ROLES = Set.of(
            "CUSTOMER", "STAFF", "MANAGER", "ADMIN");

    // Public resources accessible without authentication
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
            "/", "/index.jsp", "/login", "/register", "/logout",
            "/forgot-password", "/verify-otp", "/reset-password",
            "/forgotpassword", "/newpassword", "/enterotp",
            "/checkBooking", "/checkTicket", "/refundTicket",
            "/confirmRefundTicket", "/confirmOTP", "/refundProcessing",
            "/checkRefundTicket", "/searchTrip", "/searchTripBackground",
            "/storeRoute", "/getCoachSeatsWithPrice", "/all-locations",
            "/landing", "/terms", "/ticketPayment", "/train-info", "/forgotbookingcode");

    // Public path patterns (using regex for flexibility)
    private static final Set<Pattern> PUBLIC_PATH_PATTERNS = Set.of(
            Pattern.compile("^/css/.*"),
            Pattern.compile("^/js/.*"),
            Pattern.compile("^/assets/.*"),
            Pattern.compile("^/images/.*"),
            Pattern.compile("^/public/.*"),
            Pattern.compile("^/authentication/.*"),
            Pattern.compile("^/api/stations/.*"),
            Pattern.compile("^/api/trip/.*"),
            Pattern.compile("^/api/seats/.*"),
            Pattern.compile("^/api/booking/initiate.*"),
            Pattern.compile("^/api/seat/getCoachSeats.*"),
            Pattern.compile("^/trip/.*"),
            Pattern.compile("^/train-info/.*"),
            Pattern.compile("^/check-booking/.*"),
            Pattern.compile("^/payment/.*"),
            Pattern.compile("^/api/payment/.*"));

    // Role-specific access patterns
    private static final Map<String, Set<Pattern>> ROLE_PATTERNS = Map.of(
            "ADMIN", Set.of(
                    Pattern.compile("^/admin/.*"),
                    Pattern.compile("^/admin-dashboard$"),
                    Pattern.compile("^/(addUser|editUser|userManagement|auditLog)$")),
            "MANAGER", Set.of(
                    Pattern.compile("^/manager/.*"),
                    Pattern.compile("^/managerDashboard$"),
                    Pattern.compile("^/(manageHolidays|addHoliday|editHoliday|holidayPriceSelector|)$"),
                    Pattern.compile("^/(addPriceRule|addRoute|addStation|addTrip)$"),
                    Pattern.compile("^/(editPriceRule|editStation|routeDetail|tripDetail)$"),
                    Pattern.compile("^/manage(Price|Routes|Staffs|Stations|TrainsSeats|Trips)$"),
                    Pattern.compile("^/(manageCancellationPolicies|createPolicy|updatePolicy)$"),
                    Pattern.compile("^/(sidebar|train_form)$")),
            "STAFF", Set.of(
                    Pattern.compile("^/staff/.*"),
                    Pattern.compile("^/staff-dashboard$"),
                    Pattern.compile("^/(feedback-list|refund-requests)$"),
                    Pattern.compile("^/(customer-info|staff-message)$"),
                    Pattern.compile("^/api/booking/.*"),
                    Pattern.compile("^/api/payment/.*")),
            "CUSTOMER", Set.of(
                    Pattern.compile("^/customer/.*"),
                    Pattern.compile("^/(customer-profile|edit-profile|feedback)$"),
                    Pattern.compile("^/(customerprofile|editprofile|customer-support)$"),
                    Pattern.compile("^/(listTicketBooking|submitFeedback)$"),
                    Pattern.compile("^/api/booking/initiate.*")));

    // Common authenticated user paths (accessible to all logged-in users)
    private static final Set<Pattern> AUTHENTICATED_PATTERNS = Set.of(
            Pattern.compile("^/(profile|update-profile|changepassword|change-password)$"),
            Pattern.compile("^/api/user/.*"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Log filter initialization
        System.out.println("RoleFilter initialized with " + VALID_ROLES.size() + " roles: " + VALID_ROLES);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestPath = getRequestPath(httpRequest);

        // Check if this is a public resource
        if (isPublicResource(requestPath)) {
            chain.doFilter(request, response);
            return;
        }

        // Get user from session
        User user = getUserFromSession(httpRequest);

        // Check if user authentication is required
        if (user == null) {
            handleUnauthenticatedAccess(httpRequest, httpResponse, requestPath);
            return;
        }

        // Check role-based authorization
        if (isAuthorized(user, requestPath)) {
            chain.doFilter(request, response);
        } else {
            handleUnauthorizedAccess(httpRequest, httpResponse, user, requestPath);
        }
    }

    /**
     * Extract the request path without context path
     */
    private String getRequestPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        return requestURI.substring(contextPath.length());
    }

    /**
     * Check if the resource is publicly accessible
     */
    private boolean isPublicResource(String path) {
        // Check exact matches first (most common case)
        if (PUBLIC_EXACT_PATHS.contains(path)) {
            return true;
        }

        // Check pattern matches
        return PUBLIC_PATH_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    /**
     * Get user from HTTP session
     */
    private User getUserFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("loggedInUser");
    }

    /**
     * Check if user is authorized to access the given path
     */
    private boolean isAuthorized(User user, String path) {
        String userRole = normalizeRole(user.getRole());

        // Check if it's a common authenticated path
        if (isAuthenticatedPath(path)) {
            return true;
        }

        // Check role-specific access with hierarchy
        return hasRoleAccess(userRole, path);
    }

    /**
     * Check if path is accessible to any authenticated user
     */
    private boolean isAuthenticatedPath(String path) {
        return AUTHENTICATED_PATTERNS.stream()
                .anyMatch(pattern -> pattern.matcher(path).matches());
    }

    /**
     * Check if user role has access to the path (exclusive role-based access)
     */
    private boolean hasRoleAccess(String userRole, String path) {
        // Check access for the user's specific role only (no hierarchy)
        Set<Pattern> patterns = ROLE_PATTERNS.get(userRole);
        if (patterns == null) {
            return false;
        }

        return patterns.stream().anyMatch(p -> p.matcher(path).matches());
    }

    /**
     * Normalize role string to uppercase for consistent comparison
     */
    private String normalizeRole(String role) {
        return (role != null) ? role.trim().toUpperCase() : "";
    }

    /**
     * Handle access attempt by unauthenticated user
     */
    private void handleUnauthenticatedAccess(HttpServletRequest request,
            HttpServletResponse response,
            String path) throws IOException {
        // Log the access attempt
        String clientIP = getClientIP(request);
        System.out.println("Unauthenticated access attempt to: " + path + " from IP: " + clientIP);

        // Store the originally requested URL for redirect after login
        HttpSession session = request.getSession(true);
        session.setAttribute("originalRequestURL", request.getRequestURL().toString());

        // Redirect to login page
        response.sendRedirect(request.getContextPath() + "/login");
    }

    /**
     * Handle unauthorized access by authenticated user
     */
    private void handleUnauthorizedAccess(HttpServletRequest request,
            HttpServletResponse response,
            User user,
            String path) throws ServletException, IOException {
        // Log the unauthorized access attempt
        String clientIP = getClientIP(request);
        System.out.println("Unauthorized access attempt by user: " + user.getEmail() +
                " (role: " + user.getRole() + ") to: " + path + " from IP: " + clientIP);

        // Set error attributes for the unauthorized page
        request.setAttribute("errorMessage", "You don't have permission to access this resource.");
        request.setAttribute("userRole", user.getRole());
        request.setAttribute("requestedPath", path);

        // Forward to unauthorized page
        request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp")
                .forward(request, response);
    }

    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    @Override
    public void destroy() {
        // Cleanup resources if needed
        System.out.println("RoleFilter destroyed");
    }

    /**
     * Utility method to add new public path pattern (for testing or dynamic
     * configuration)
     */
    public static boolean isPublicPath(String path) {
        RoleFilter filter = new RoleFilter();
        return filter.isPublicResource(path);
    }

    /**
     * Utility method to check role access (for testing)
     */
    public static boolean checkRoleAccess(String role, String path) {
        RoleFilter filter = new RoleFilter();
        return filter.hasRoleAccess(filter.normalizeRole(role), path);
    }
}
