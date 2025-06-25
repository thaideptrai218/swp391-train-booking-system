<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<aside class="sidebar">
    <div class="sidebar-header">
        <button class="menu-toggle"><i class="fas fa-bars"></i></button>
    </div>
    <div class="admin-info">
        <h3><i class="fas fa-user-shield"></i> Thông tin người quản lý</h3>
        <p><strong>Họ và Tên:</strong> ${sessionScope.loggedInUser.fullName}</p>
        <p><strong>Email:</strong> ${sessionScope.loggedInUser.email}</p>
        <p><strong>Vai Trò:</strong> ${sessionScope.loggedInUser.role}</p>
    </div>
    <nav class="main-nav">
        <h3><i class="fas fa-cogs"></i> Chức Năng</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-chart-line"></i> Xem Số Liệu Thống Kê</a></li>
            <li><a href="#"><i class="fas fa-route"></i> Quản Lý Tuyến Đường</a></li>
            <li><a href="#"><i class="fas fa-subway"></i> Quản Lý Chuyến Đi</a></li>
            <li><a href="#"><i class="fas fa-train"></i> Quản Lý Tàu và Ghế Ngồi</a></li>
            <li><a href="#"><i class="fas fa-dollar-sign"></i> Quản Lý Giá Cả</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/userManagement"><i class="fas fa-users-cog"></i> Quản Lý Nhân Viên</a></li>
            <li><a href="${pageContext.request.contextPath}/admin/auditLog"><i class="fas fa-history"></i> Lịch sử sửa đổi</a></li>
            <li><a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a></li>
        </ul>
    </nav>
</aside>
