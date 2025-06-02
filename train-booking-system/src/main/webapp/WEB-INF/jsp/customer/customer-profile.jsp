<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ khách hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customer-profile.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
</head>
<body>
    <div class="profile-container">
        <div class="profile-header">
            <a href="${pageContext.request.contextPath}/searchTrip" class="home-link">
                <i class="fa-solid fa-house fa-xl home-icon"></i>
            </a>
            <h1>Hồ sơ người dùng</h1>
        </div>
        <div class="profile-details">
            <div class="detail-item">
                <span class="label">Tên người dùng:</span>
                <span class="value">${user.userName}</span>
            </div>
            <div class="detail-item">
                <span class="label">Email:</span>
                <span class="value">${user.email}</span>
            </div>
            <div class="detail-item">
                <span class="label">Họ và tên:</span>
                <span class="value">${user.fullName}</span>
            </div>
            <div class="detail-item">
                <span class="label">Số điện thoại:</span>
                <span class="value">${user.phoneNumber}</span>
            </div>
            <div class="detail-item">
                <span class="label">Địa chỉ:</span>
                <span class="value">${user.address}</span>
            </div>
            <div class="detail-item">
                <span class="label">Vai trò:</span>
                <span class="value">${user.role}</span>
            </div>
            <!-- Add more user details as needed -->
        </div>
        <div class="profile-actions">
            <a href="${pageContext.request.contextPath}/changepassword" class="btn btn-primary">Đổi mật khẩu</a>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary">Đăng xuất</a>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/customerprofile.js"></script>
</body>
</html>
