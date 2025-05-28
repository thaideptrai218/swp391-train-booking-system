<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thay đổi mật khẩu</title>
    <link rel="stylesheet" href="css/changepassword.css">
    <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
        />
</head>
<body>
    <div class="main-container">
        <div class="background-image"></div>
        
        <header class="header">
            <div class="logo"></div>
        </header>

        <main class="main-content">
            <section class="change-password-section">
                <nav class="breadcrumb">
                    <button type="button" class="home-button" aria-label="Trang chủ">
                        <div class="home-icon"></div>
                    </button>
                </nav>
<i class="fa-solid fa-house fa-xl"></i>
                <article class="password-form-container">
                    <header class="form-header">
                        <h1 class="form-title">Thay đổi mật khẩu</h1>
                    </header>

                    <form id="changePasswordForm" class="password-form" novalidate>
                        <div class="form-group">
                            <label for="oldPassword" class="form-label">Mật khẩu cũ</label>
                            <div class="input-container">
                                <input 
                                    type="password" 
                                    id="oldPassword" 
                                    name="oldPassword" 
                                    class="form-input" 
                                    placeholder="Nhập mật khẩu cũ"
                                    required
                                    autocomplete="current-password"
                                >
                            </div>
                            <span class="error-message" id="oldPasswordError"></span>
                        </div>

                        <div class="form-group">
                            <label for="newPassword" class="form-label">Mật khẩu mới</label>
                            <div class="input-container">
                                <input 
                                    type="password" 
                                    id="newPassword" 
                                    name="newPassword" 
                                    class="form-input" 
                                    placeholder="Nhập mật khẩu mới"
                                    required
                                    autocomplete="new-password"
                                >
                            </div>
                            <span class="error-message" id="newPasswordError"></span>
                        </div>

                        <div class="form-group">
                            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                            <div class="input-container">
                                <input 
                                    type="password" 
                                    id="confirmPassword" 
                                    name="confirmPassword" 
                                    class="form-input" 
                                    placeholder="Nhập lại mật khẩu mới"
                                    required
                                    autocomplete="new-password"
                                >
                            </div>
                            <span class="error-message" id="confirmPasswordError"></span>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="submit-button">
                                <span class="button-text">Thay đổi</span>
                            </button>
                        </div>
                    </form>
                </article>
            </section>
        </main>

        <div class="loading-overlay" id="loadingOverlay" style="display: none;">
            <div class="loading-spinner"></div>
        </div>

        <div class="success-message" id="successMessage" style="display: none;">
            <p>Mật khẩu đã được thay đổi thành công!</p>
        </div>
    </div>

    <script src="changepassword.js"></script>
</body>
</html>
