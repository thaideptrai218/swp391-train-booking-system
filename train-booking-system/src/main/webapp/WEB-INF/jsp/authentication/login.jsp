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
                <%
                    String rememberedIdentifier = "";
                    String rememberedPassword = "";
                    boolean rememberMeChecked = false;

                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("rememberedIdentifier".equals(cookie.getName())) {
                                rememberedIdentifier = cookie.getValue();
                            } else if ("rememberedPassword".equals(cookie.getName())) {
                                rememberedPassword = cookie.getValue();
                            } else if ("rememberMeChecked".equals(cookie.getName())) {
                                rememberMeChecked = Boolean.parseBoolean(cookie.getValue());
                            }
                        }
                    }
                %>
                <div class="form-group">
                    <label for="email">Email</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-user icon"></i> <!-- Changed icon to user for username -->
                        <input type="email" id="email" name="email" placeholder="Nhập email của bạn" value="<%= rememberedIdentifier.contains("@") ? rememberedIdentifier : "" %>">
                    </div>
                </div>
                <div class="form-group">
                    <label for="phone">Số điện thoại</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-phone icon"></i>
                        <input type="tel" id="phone" name="phone" placeholder="Nhập số điện thoại của bạn" pattern="0[0-9]{9}" title="Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số" value="<%= !rememberedIdentifier.contains("@") ? rememberedIdentifier : "" %>">
                    </div>
                </div>
                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input type="password" id="password" name="password" placeholder="Nhập mật khẩu của bạn" value="<%= rememberedPassword %>">
                        <i class="fa-solid fa-eye-slash toggle-password" id="togglePassword"></i>
                    </div>
                </div>
                <%-- Display error message if present --%>
                <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
                <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
                    <div class="error-message-server"><%= errorMessage %></div>
                <% } %>
                <div class="form-actions">
                    <div class="remember-me">
                        <input type="checkbox" id="rememberMe" name="rememberMe" <%= rememberMeChecked ? "checked" : "" %>>
                        <label for="rememberMe">Nhớ tài khoản</label>
                    </div>
                    <a href="${pageContext.request.contextPath}/forgotpassword" class="forgot-password">Quên mật khẩu?</a>
                </div>
                <div class="login-button-container">
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
    <%-- Server-side message display --%>
    <%
        String pageMessage = (String) request.getAttribute("message");
        String pageMessageType = (String) request.getAttribute("messageType");
        if (pageMessage != null && !pageMessage.isEmpty()) {
            String messageClass = "server-message ";
            if ("success".equals(pageMessageType)) {
                messageClass += "success-message";
            } else {
                messageClass += "error-message-server";
            }
    %>
        <div class="<%= messageClass %>"><%= pageMessage %></div>
    <%
        }
    %>
    <script src="js/login.js"></script>
</body>
</html>
