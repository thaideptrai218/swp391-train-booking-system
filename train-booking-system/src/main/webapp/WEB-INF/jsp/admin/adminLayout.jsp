<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--
  Admin Layout (Sidebar)
  This file defines the sidebar navigation for the admin panel.
-->

<!-- Font Awesome CDN for icons -->
<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
  integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ=="
  crossorigin="anonymous"
  referrerpolicy="no-referrer"
/>

<!-- Sidebar Toggle Button (for mobile view) -->
<button id="sidebarToggle" class="sidebar-toggle-btn">
  <i class="fas fa-bars"></i>
</button>

<!-- Sidebar -->
<aside class="sidebar" id="sidebar">
    
    <!-- Admin Information Section -->
    <div class="admin-info">
        <h3><i class="fas fa-user-shield"></i> Thông tin người quản lý</h3>
        <p><strong>Họ và Tên:</strong> ${sessionScope.loggedInUser.fullName}</p>
        <p><strong>Email:</strong> ${sessionScope.loggedInUser.email}</p>
        <p><strong>Vai Trò:</strong> ${sessionScope.loggedInUser.role}</p>
    </div>
    
    <!-- Main Navigation Section -->
    <nav class="main-nav">
        <h3><i class="fas fa-cogs"></i> Chức Năng</h3>
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-chart-line"></i> Xem Số Liệu Thống Kê
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/userManagement">
                    <i class="fas fa-users-cog"></i> Quản Lý Người Dùng
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin/auditLog">
                    <i class="fas fa-history"></i> Lịch sử sửa đổi
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/logout">
                    <i class="fas fa-sign-out-alt"></i> Đăng Xuất
                </a>
            </li>
        </ul>
    </nav>
</aside>

<!-- Script for sidebar functionality -->
<script src="${pageContext.request.contextPath}/js/admin.js"></script>
