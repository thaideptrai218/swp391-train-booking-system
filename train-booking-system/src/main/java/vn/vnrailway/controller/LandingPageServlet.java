package vn.vnrailway.controller;

import java.io.IOException;
// Removed unused ParseException and SimpleDateFormat
// Removed unused Date
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;
// Removed unused ArrayList and List as they are not used in this basic servlet

@WebServlet(name = "LandingPageServlet", urlPatterns = { "/landing", "" })
public class LandingPageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/landing/landing-page.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward POST requests to doGet, or handle them separately if needed
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet to display the landing page";
    }
}
