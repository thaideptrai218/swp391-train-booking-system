<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Đăng Ký</title>
            <link rel="stylesheet" href="css/register.css">
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
        </head>

        <body>
            <div class="register-container">
                <div class="register-card">
                    <div class="register-header">
                        <a href="${pageContext.request.contextPath}/landing" class="home-link">
                            <i class="fa-solid fa-house fa-xl home-icon"></i>
                        </a>
                        <h2>Đăng Ký</h2>
                    </div>
                    <c:if test="${not empty errorMessage}">
                        <div class="error-box">
                            <p style="color: red;">${errorMessage}</p>
                        </div>
                    </c:if>
                    <form action="register" method="post" class="register-form">
                        <div class="form-group">
                            <label for="FullName">Họ và tên</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-user icon"></i>
                                <input type="text" id="FullName" name="FullName" placeholder="Nhập họ và tên của bạn"
                                    required>
                            </div>
                            <span class="error-message" id="fullNameError"></span>
                        </div>
                        <div class="form-group">
                            <label for="phone">Số điện thoại</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-phone icon"></i>
                                <input type="tel" id="phone" name="phone" placeholder="Nhập số điện thoại của bạn"
                                    required>
                            </div>
                            <span class="error-message" id="phoneError"></span>
                        </div>
                        <div class="form-group">
                            <label for="email">Email</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-envelope icon"></i>
                                <input type="email" id="email" name="email" placeholder="Nhập email của bạn" required>
                            </div>
                            <span class="error-message" id="emailError"></span>
                        </div>
                        <div class="form-group">
                            <label for="idCardNumber">Số CMND/CCCD</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-id-card icon"></i> <!-- Assuming a suitable icon for ID card -->
                                <input type="text" id="idCardNumber" name="idCardNumber"
                                    placeholder="Nhập số CMND/CCCD của bạn" required>
                            </div>
                            <span class="error-message" id="idCardNumberError"></span>
                        </div>
                        <div class="form-group">
                            <label for="password">Mật khẩu</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-lock icon"></i>
                                <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn"
                                    required>
                                <i class="fa-solid fa-eye-slash toggle-password" id="togglePassword"></i>
                            </div>
                            <span class="error-message" id="passwordError"></span>
                        </div>
                        <div class="form-group">
                            <label for="confirmPassword">Xác nhận mật khẩu</label>
                            <div class="input-wrapper">
                                <i class="fa-solid fa-lock icon"></i>
                                <input type="password" id="confirmPassword" name="confirmPassword"
                                    placeholder="Nhập lại mật khẩu của bạn" required>
                                <i class="fa-solid fa-eye-slash toggle-password" id="toggleConfirmPassword"></i>
                            </div>
                            <span class="error-message" id="confirmPasswordError"></span>
                        </div>
                        <button type="submit" class="register-button">Xác Nhận</button>
                    </form>
                    <div class="login-section">
                        <p>Bạn đã có tài khoản?</p>
                        <a href="${pageContext.request.contextPath}/login" class="login-link">Đăng nhập</a>
                    </div>
                </div>
            </div>
            <script src="js/register.js"></script>
        </body>

        </html>