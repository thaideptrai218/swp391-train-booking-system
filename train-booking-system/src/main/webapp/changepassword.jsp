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
                <a href="http://localhost:8080/train-booking-system/searchTrip" class="home-link">
                    <i class="fa-solid fa-house fa-xl home-icon"></i>
                </a>
            <div class="change-password-header">
                <h2>Thay đổi mật khẩu</h2>
            </div>

            <form id="changePasswordForm" class="password-form" novalidate>
                <div class="form-group">
                    <label for="oldPassword">Mật khẩu cũ</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input 
                            type="password" 
                            id="oldPassword" 
                            name="oldPassword" 
                            placeholder="Nhập mật khẩu cũ"
                            required
                            autocomplete="current-password"
                        >
                        <i class="fa-solid fa-eye-slash toggle-password" id="toggleOldPassword"></i>
                    </div>
                    <span class="error-message" id="oldPasswordError"></span>
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
                    <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input 
                            type="password" 
                            id="confirmPassword" 
                            name="confirmPassword" 
                            placeholder="Nhập lại mật khẩu mới"
                            required
                            autocomplete="new-password"
                        >
                        <i class="fa-solid fa-eye-slash toggle-password" id="toggleConfirmPassword"></i>
                    </div>
                    <span class="error-message" id="confirmPasswordError"></span>
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

    <div class="success-message" id="successMessage" style="display: none;">
        <p>Mật khẩu đã được thay đổi thành công!</p>
    </div>

    <script src="js/changepassword.js"></script>
</body>
</html>
