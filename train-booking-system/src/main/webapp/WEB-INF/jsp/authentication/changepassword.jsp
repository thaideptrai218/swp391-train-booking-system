<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thay đổi mật khẩu</title>
    <link rel="stylesheet" href="css/changepassword.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>
<body>
    <div class="change-password-container">
        <div class="change-password-card">
                <a href="${pageContext.request.contextPath}/landing" class="home-link">
                    <i class="fa-solid fa-house fa-xl home-icon"></i>
                </a>
            <div class="change-password-header">
                <h2>Thay đổi mật khẩu</h2>
            </div>

            <form id="changePasswordForm" class="password-form" action="${pageContext.request.contextPath}/changepassword" method="post" novalidate>
                <%-- Display error message if present --%>
                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="alert-message error-message-box">
                        <p><%= errorMessage %></p>
                    </div>
                <% } %>

                <%-- Display success message if present --%>
                <% String successMessage = (String) request.getAttribute("successMessage"); %>
                <% if (successMessage != null && !successMessage.isEmpty()) { %>
                    <div class="alert-message success-message-box">
                        <p><%= successMessage %></p>
                    </div>
                <% } %>

                <div class="form-group">
                    <label for="emailOrPhone">Email hoặc Số điện thoại</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-user icon"></i>
                        <input 
                            type="text" 
                            id="emailOrPhone" 
                            name="emailOrPhone" 
                            placeholder="Nhập email hoặc số điện thoại"
                            required
                        >
                    </div>
                    <span class="error-message" id="emailOrPhoneError"></span>
                </div>

                <div class="form-group">
                    <label for="currentPassword">Mật khẩu cũ</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input 
                            type="password" 
                            id="currentPassword" 
                            name="currentPassword" 
                            placeholder="Nhập mật khẩu cũ"
                            required
                            autocomplete="current-password"
                        >
                        <i class="fa-solid fa-eye-slash toggle-password" id="toggleCurrentPassword"></i>
                    </div>
                    <span class="error-message" id="currentPasswordError"></span>
                </div>

                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input 
                            type="password" 
                            id="newPassword" 
                            name="newPassword" 
                            placeholder="Nhập mật khẩu mới"
                            required
                            autocomplete="new-password"
                        >
                        <i class="fa-solid fa-eye-slash toggle-password" id="toggleNewPassword"></i>
                    </div>
                    <span class="error-message" id="newPasswordError"></span>
                </div>

                <div class="form-group">
                    <label for="confirmNewPassword">Xác nhận mật khẩu mới</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input 
                            type="password" 
                            id="confirmNewPassword" 
                            name="confirmNewPassword" 
                            placeholder="Nhập lại mật khẩu mới"
                            required
                            autocomplete="new-password"
                        >
                        <i class="fa-solid fa-eye-slash toggle-password" id="toggleConfirmNewPassword"></i>
                    </div>
                    <span class="error-message" id="confirmNewPasswordError"></span>
                </div>

                <div class="form-actions">
                    <button type="submit" class="submit-button">Thay đổi</button>
                </div>
            </form>
        </div>
    </div>

    <div class="loading-overlay" id="loadingOverlay" style="display: none;">
        <div class="loading-spinner"></div>
    </div>

    <%-- The success message div is now controlled by the servlet's attribute --%>
    <%-- <div class="success-message" id="successMessage" style="display: none;">
        <p>Mật khẩu đã được thay đổi thành công!</p>
    </div> --%>

    <script src="js/changepassword.js"></script>
</body>
</html>
