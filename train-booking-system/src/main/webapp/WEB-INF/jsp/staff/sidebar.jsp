<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    <h2><i class="fas fa-user"></i> Thông tin nhân viên</h2>
    <p>
      <strong>Họ và Tên:</strong> <c:out value="${loggedInUser.fullName}" />
    </p>
    <p><strong>Email:</strong> <c:out value="${loggedInUser.email}" /></p>
    <p><strong>Vai Trò:</strong> <c:out value="${loggedInUser.role}" /></p>
  </c:if>
  <c:if test="${empty loggedInUser}">
    <p>Thông tin người dùng không có sẵn.</p>
  </c:if>

  <h3><i class="fas fa-tasks"></i> Chức Năng</h3>
  <ul>
    <li>
      <a href="${pageContext.request.contextPath}/staff/dashboard">
        <i class="fas fa-tachometer-alt"></i> Bảng điều khiển
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/checkRefundTicket">
        <i class="fas fa-ticket-alt"></i> Kiểm tra hoàn vé
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/staff-message">
        <i class="fas fa-headset"></i> Hỗ trợ khách hàng
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/checkConfirmRefundRequest?userID=${loggedInUser.userID}">
        <i class="fas fa-check-circle"></i> Danh sách các vé đã hoàn
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/staff/feedback">
        <i class="fas fa-comment-dots"></i> Góp ý của khách hàng
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/customer-info">
        <i class="fas fa-users"></i> Thông tin khách hàng
      </a>
    </li>
    <li>
      <a href="${pageContext.request.contextPath}/logout">
        <i class="fas fa-sign-out-alt"></i> Đăng xuất
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
