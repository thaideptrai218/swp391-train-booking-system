<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu - Vietnam Railway</title>
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
                <h2>Đặt Lại Mật Khẩu</h2>
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

            <form id="newPasswordForm" action="${pageContext.request.contextPath}/resetpassword" method="post" class="login-form">
                <div class="form-group">
                    <label for="newPassword">Mật khẩu mới</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input type="password" id="newPassword" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                        <i class="fa-solid fa-eye-slash toggle-password" onclick="togglePassword('newPassword', this)"></i>
                    </div>
                    <span class="error-message" id="newPasswordError"></span>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-lock icon"></i>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                        <i class="fa-solid fa-eye-slash toggle-password" onclick="togglePassword('confirmPassword', this)"></i>
                    </div>
                    <span class="error-message" id="confirmPasswordError"></span>
                </div>

                <div class="form-actions" style="justify-content: center;">
                    <button type="submit" class="login-button">Đặt Lại Mật Khẩu</button>
                </div>
            </form>
        </div>
    </div>
    <script src="js/newpassword.js"></script>
</body>
</html>