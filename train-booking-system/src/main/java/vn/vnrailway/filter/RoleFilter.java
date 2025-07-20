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

                path.equals("/index.jsp") || // Assuming index.jsp is at the root and public

                path.equals("/login") || path.equals("/register") || path.equals("/logout") ||
                path.equals("/forgot-password") || path.equals("/verify-otp") || path.equals("/reset-password") ||
                path.equals("/forgotpassword") || path.equals("/newpassword") || path.equals("/enterotp") ||
                path.equals("/changepassword") || path.equals("/checkBooking") || path.equals("/checkTicket")
                || path.equals("/refundTicket") || path.equals("/confirmRefundTicket") || path.equals("/confirmOTP")
                || path.equals("/refundProcessing") || path.equals("/checkRefundTicket") ||
                path.equals("/searchTrip") || path.equals("/searchTripBackground") ||
                path.equals("/storeRoute") || path.equals("/getCoachSeatsWithPrice") ||
                path.equals("/all-locations") || path.equals("/landing") || path.equals("/terms") ||
                path.equals("/api/stations/all") ||

                // Trip & train info related public paths
                path.startsWith("/trip/") || path.startsWith("/train-info/") || path.startsWith("/check-booking/")
                || path.startsWith("/api") || path.startsWith("/ticketPayment") || path.startsWith("/payment/") ||
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

        String role = user.getRole() != null ? user.getRole().toLowerCase() : ""; // Use empty string for null role

        boolean authorized = false;

        switch (role) {
            case "admin":
                if (path.startsWith("/admin/") ||
                        path.equals("/admin-dashboard") ||
                        path.equals("/addUser") ||
                        path.equals("/adminLayout") ||
                        path.equals("/auditLog") ||
                        path.equals("/dashboard") ||
                        path.equals("/editUser") ||
                        path.equals("/userManagement") ||
                        isCommonPage(path)) {
                    authorized = true;
                }
                break;
            case "manager":
                if (path.startsWith("/manager/") ||
                        path.equals("/managerDashboard") ||
                        path.equals("/addPriceRule") ||
                        path.equals("/addRoute") ||
                        path.equals("/addStation") ||
                        path.equals("/addTrip") ||
                        path.equals("/editPriceRule") ||
                        path.equals("/editStation") ||
                        path.equals("/managePrice") ||
                        path.equals("/manageRoutes") ||
                        path.equals("/manageStaffs") ||
                        path.equals("/manageStations") ||
                        path.equals("/manageTrainsSeats") ||
                        path.equals("/manageTrips") ||
                        path.equals("/routeDetail") ||
                        path.equals("/sidebar") ||
                        path.equals("/train_form") ||
                        path.equals("/tripDetail") ||
                        isCommonPage(path)) {
                    authorized = true;
                }
                break;
            case "staff":
                if (path.startsWith("/staff/") ||
                        path.equals("/staff-dashboard") ||
                        path.equals("/feedback-list") ||
                        path.equals("/refund-requests") ||
                        isCommonPage(path)) {
                    authorized = true;
                }
                break;
            case "customer":
                if (path.startsWith("/customer/") ||
                        path.equals("/customer-profile") ||
                        path.equals("/edit-profile") ||
                        path.equals("/feedback") ||
                        path.equals("/changepassword") ||
                        path.equals("/change-password") ||
                        isCommonPage(path)) {
                    authorized = true;
                }
                break;
            default:
                break;
        }

        if (authorized) {
            chain.doFilter(request, response);
        } else {
            // Unauthorized access attempt
            request.getRequestDispatcher("/WEB-INF/jsp/common/unauthorized.jsp").forward(request, response);
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
                path.startsWith(contextPath + "/css/") ||
                path.startsWith(contextPath + "/js/") ||
                path.startsWith(contextPath + "/assets/") ||
                path.startsWith(contextPath + "/public/") ||
                path.startsWith(contextPath + "/authentication/") || // Covers servlets like /authentication/someAction
                path.startsWith(contextPath + "/payment/") ||
                path.equals(contextPath + "/checkBooking") ||
                path.equals(contextPath + "/checkTicket") ||
                path.equals(contextPath + "/refundTicket") ||
                path.equals(contextPath + "/confirmRefundTicket") ||
                path.equals(contextPath + "/confirmOTP") ||
                path.equals(contextPath + "/refundProcessing") ||
                path.equals(contextPath + "/checkRefundTicket");
    }

    private boolean isCommonPage(String path) {
        return path.equals("/profile") || path.equals("/update-profile");
    }
}
