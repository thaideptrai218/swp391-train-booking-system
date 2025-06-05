<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Xác Nhận OTP - Vetaure</title>
    <link rel="stylesheet" href="css/login.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <style>
        .error-message-server {
            color: #ff0000;
            font-weight: bold;
            text-align: center;
            margin: 10px 0;
            padding: 10px;
            border-radius: 5px;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <%-- Server-side message display --%>
            <%
                String pageMessage = (String) request.getAttribute("message");
                String pageMessageType = (String) request.getAttribute("messageType");
                if (pageMessage != null && !pageMessage.isEmpty()) {
                    String messageClass = "server-message ";
                    if ("error".equals(pageMessageType)) {
                        messageClass += "error-message-server";
                    }
            %>
                <div class="<%= messageClass %>"><%= pageMessage %></div>
            <%
                }
            %>

            <div class="login-header">
                <a href="${pageContext.request.contextPath}/landing" class="home-link">
                    <i class="fa-solid fa-house fa-xl home-icon"></i>
                </a>
                <h2>Xác Nhận OTP</h2>
            </div>

            <form id="otpForm" action="${pageContext.request.contextPath}/enterotp" method="post" class="login-form">
                <p class="form-description">
                    Vui lòng nhập mã OTP đã được gửi đến email của bạn.<br>
                    Mã sẽ hết hạn sau 5 phút.
                </p>
                <div class="form-group">
                    <label for="otp">Mã OTP</label>
                    <div class="input-wrapper">
                        <i class="fa-solid fa-key icon"></i>
                        <input type="text" id="otp" name="otp" placeholder="Nhập mã OTP 6 số" required maxlength="6" pattern="[0-9]{6}">
                    </div>
                    <span class="error-message" id="otpError"></span>
                </div>
                <div class="form-actions" style="justify-content: center;">
                    <button type="submit" class="login-button">Xác Nhận</button>
                </div>
            </form>
            
            <div class="register-section">
                <p>Chưa nhận được mã? <a href="${pageContext.request.contextPath}/forgotpassword?action=resend" class="register-link">Gửi lại mã</a></p>
            </div>
        </div>
    </div>
    <script src="js/enterotp.js"></script>
</body>
</html>
