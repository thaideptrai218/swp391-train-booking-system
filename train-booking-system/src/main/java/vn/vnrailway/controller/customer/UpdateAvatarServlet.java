package vn.vnrailway.controller.customer;

import vn.vnrailway.dao.UserRepository;
import vn.vnrailway.dao.impl.UserRepositoryImpl;
import vn.vnrailway.model.User;
import jakarta.servlet.http.Part;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@WebServlet("/updateavatar")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
public class UpdateAvatarServlet extends HttpServlet {
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        userRepository = new UserRepositoryImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("loggedInUser");
        Part filePart = request.getPart("avatarFile");

        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("errorMessage", "Vui lòng chọn tệp ảnh.");
            request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);
            return;
        }

        String originalFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String newFileName = "avatar_user_" + user.getUserID() + "_" + System.currentTimeMillis() + "_"
                + originalFileName;

        String externalUploadDir = "D:/uploads";
        File uploadFolder = new File(externalUploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        File savedFile = new File(uploadFolder, newFileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, savedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Saved external file path: " + savedFile.getAbsolutePath());

        String avatarPath = "/uploads/" + newFileName;

        try {
            user.setAvatarPath(avatarPath);
            userRepository.updateAvatar(user.getUserID(), avatarPath);
            session.setAttribute("loggedInUser", user);
            response.sendRedirect(request.getContextPath() + "/customerprofile");
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi cập nhật ảnh: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/customer/customer-profile.jsp").forward(request, response);
        }
    }
}
