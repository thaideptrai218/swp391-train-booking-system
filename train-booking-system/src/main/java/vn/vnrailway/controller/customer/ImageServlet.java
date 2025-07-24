package vn.vnrailway.controller.customer;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/uploads/*")
public class ImageServlet extends HttpServlet {

    private static final String UPLOAD_DIR = "D:/uploads"; // thư mục ngoài

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestedFile = request.getPathInfo();
        if (requestedFile == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tên file không hợp lệ");
            return;
        }

        File file = new File(UPLOAD_DIR, requestedFile);
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType = getServletContext().getMimeType(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setContentLengthLong(file.length());

        Files.copy(file.toPath(), response.getOutputStream());
    }
}
