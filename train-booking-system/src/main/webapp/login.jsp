<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Nhập</title>
    <link rel="stylesheet" href="css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <a href="${pageContext.request.contextPath}/landing" class="home-link">
                    <i class="fa-solid fa-house fa-xl home-icon"></i>
                </a>
                <h2>Đăng Nhập</h2>
            </div>
            <form action="login" method="post" class="login-form">
                <div class="form-group">
                    <label for="email">Email</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-user icon"></i> <!-- Changed icon to user for username -->
                        <input type="email" id="email" name="email" placeholder="Nhập email của bạn" required>
                    </div>
                </div>
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn" required>
                        <i class="fa-solid fa-eye-slash toggle-password" id="togglePassword"></i>
                    </div>
                </div>
                <%-- Display error message if present --%>
                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                    <p style="color: #dc3545; text-align: center; margin-top: -10px;"><%= errorMessage %></p>
                <% } %>
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/forgotpassword" class="forgot-password">Quên mật khẩu?</a>
                    <button type="submit" class="login-button">Đăng nhập</button>
                </div>
            </form>
            <div class="social-login">
                <p>Hoặc đăng nhập với</p>
                <button class="google-login-button">
                    <i class="fa-brands fa-google"></i> Tài khoản Google
                </button>
            </div>
            <div class="register-section">
                <p>Bạn chưa có tài khoản?</p>
                <a href="register.jsp" class="register-link">Đăng ký ngay</a>
            </div>
        </div>
    </div>
    <script src="js/login.js"></script>
</body>
</html>
