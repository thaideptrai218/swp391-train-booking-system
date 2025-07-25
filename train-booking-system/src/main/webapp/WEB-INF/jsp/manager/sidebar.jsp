<%@ page language="java" pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Font Awesome CDN -->
<link
  rel="stylesheet"
  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
  integrity="sha512-1ycn6IcaQQ40/MKBW2W4Rhis/DbILU74C1vSrLJxCq57o941Ym01SwNsOMqvEBFlcgUa6xLiPY/NS5R+E6ztJQ=="
  crossorigin="anonymous"
  referrerpolicy="no-referrer"
/>

<button id="sidebarToggle" class="sidebar-toggle-btn">
  <i class="fas fa-bars"></i>
</button>
<div class="sidebar" id="sidebar">
  <c:if test="${not empty loggedInUser}">
    <h2><i class="fas fa-user-shield"></i> Thông tin người quản lý</h2>
    <p>
      <strong>Họ và Tên:</strong> <c:out value="${loggedInUser.fullName}" />
    </p>
    <p><strong>Email:</strong> <c:out value="${loggedInUser.email}" /></p>
    <p><strong>Vai Trò:</strong> <c:out value="${loggedInUser.role}" /></p>
  </c:if>
  <c:if test="${empty loggedInUser}">
    <p>Thông tin người dùng không có sẵn.</p>
  </c:if>

  <!-- Add other sidebar links/content here as needed -->
  <h3><i class="fas fa-cogs"></i> Chức Năng</h3>
  <ul>
    <li>
      <a href="${pageContext.request.contextPath}/managerDashboard">
        <i class="fas fa-tachometer-alt"></i> Bảng Điều Khiển
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/manageStations">
        <i class="fas fa-map-signs"></i> Quản Lý Ga Tàu
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/manageRoutes">
        <i class="fas fa-route"></i> Quản Lý Tuyến Đường
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/manageTrips">
        <i class="fas fa-train"></i> Quản Lý Chuyến Đi
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/manager/manage-trains-seats">
        <i class="fas fa-building"></i> Quản Lý Tàu và Ghế
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/managePrice">
        <i class="fas fa-dollar-sign"></i> Quản Lý Giá cả
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/manageCancellationPolicies">
        <i class="fas fa-users-cog"></i> Quản Lý Chính Sách Hủy Vé
      </a>
    </li>

    <li>
      <a href="${pageContext.request.contextPath}/manageHolidays">
        <i class="fas fa-calendar-alt"></i> Quản Lý Ngày Lễ
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/logout">
        <i class="fas fa-sign-out-alt"></i> Đăng Xuất
      </a>
    </li>
  </ul>
</div>

<link
  rel="stylesheet"
  type="text/css"
  href="${pageContext.request.contextPath}/css/manager/sidebar.css"
/>
<script src="${pageContext.request.contextPath}/js/manager/sidebar.js"></script>
