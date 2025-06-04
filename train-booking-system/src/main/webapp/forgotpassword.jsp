<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - Vietnam Railway</title>
    <link rel="stylesheet" href="css/login.css"> <%-- Sử dụng lại CSS của login --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <a href="${pageContext.request.contextPath}/landing" class="home-link">
                    <i class="fa-solid fa-house fa-xl home-icon"></i>
                </a>
                <h2>Quên Mật Khẩu</h2>
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

            <form id="forgotPasswordForm" action="${pageContext.request.contextPath}/forgot-password" method="post" class="login-form">
                <p class="form-description">
                    Nhập địa chỉ email của bạn và chúng tôi sẽ gửi cho bạn một liên kết để đặt lại mật khẩu.
                </p>
                <div class="form-group">
                    <label for="email">Email</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-envelope icon"></i>
                        <input type="email" id="email" name="email" placeholder="Nhập email của bạn" required>
                    </div>
                    <span class="error-message" id="emailError"></span>
                </div>                <div class="form-actions" style="justify-content: center;">
                    <button type="submit" class="login-button">Gửi Liên Kết Đặt Lại</button>
                </div>
            </form>
            
            <div class="register-section">
                <p>Nhớ mật khẩu? <a href="${pageContext.request.contextPath}/login" class="register-link">Đăng nhập tại đây</a></p>
                <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register" class="register-link">Đăng ký ngay</a></p>
            </div>
        </div>
    </div>
    <script src="js/forgotpassword.js"></script>
</body>
</html>
