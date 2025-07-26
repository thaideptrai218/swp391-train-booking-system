package vn.vnrailway.controller.staff;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.vnrailway.dao.FeedbackDAO;
import vn.vnrailway.dao.impl.FeedbackDAOImpl;
import vn.vnrailway.model.Feedback;
import vn.vnrailway.model.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@WebServlet("/image/*")
public class ImageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String filename = request.getPathInfo().substring(1); // lấy phần sau /image/
        File file = new File("C:/uploads/", filename);
        
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(file.getName());
        response.setContentType(mime);
        Files.copy(file.toPath(), response.getOutputStream());
    }
}
