
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<header class="navbar">
  <div class="container">
    <a
      href="${pageContext.request.contextPath}/landing"
      class="logo-block-link"
    >
      <div class="logo-block">
        <img
          src="${pageContext.request.contextPath}/assets/images/landing/common/logo.svg"
          alt="Logo"
          class="logo"
        />
      </div>
    </a>
    <nav>
      <ul class="nav-list">
        <li>
          <a href="${pageContext.request.contextPath}/searchTrip">Tìm vé</a>
        </li>
        <li>
          <a href="${pageContext.request.contextPath}/checkBooking">Thông tin đặt chỗ</a>
        </li>
        <li><a href="#">Kiểm tra vé</a></li>
        <li><a href="#">Trả vé</a></li>
        <li><a href="#">Hotline</a></li>
        <c:choose>
          <c:when
            test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'Customer'}"
          >
            <li class="profile-dropdown-container">
              <a
                href="javascript:void(0);"
                id="profileIcon"
                class="nav-profile-icon"
                title="Trang cá nhân"
                >👤</a
              >
              <div class="profile-dropdown-menu" id="profileDropdown">
                <div class="dropdown-header">
                  <span class="dropdown-username"
                    >${sessionScope.loggedInUser.fullName}</span
                  >
                </div>
                <a
                  href="${pageContext.request.contextPath}/customer-profile"
                  class="dropdown-item"
                  >👥 Xem tất cả trang cá nhân</a
                >
                <a href="#" class="dropdown-item"
                  >⚙️ Cài đặt và quyền riêng tư
                  <span class="arrow-right">➡️</span></a
                >
                <a href="#" class="dropdown-item"
                  >❓ Trợ giúp và hỗ trợ
                  <span class="arrow-right">➡️</span></a
                >
                <a href="#" class="dropdown-item"
                  >🌙 Màn hình & trợ năng
                  <span class="arrow-right">➡️</span></a
                >
                <a href="#" class="dropdown-item">💬 Đóng góp ý kiến</a>
                <a
                  href="${pageContext.request.contextPath}/logout"
                  class="dropdown-item logout-item"
                  >🚪 Đăng xuất</a
                >
              </div>
            </li>
          </c:when>
          <c:otherwise>
            <li>
              <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </li>
            <li class="btn">
              <a href="${pageContext.request.contextPath}/register">Đăng kí</a>
            </li>
          </c:otherwise>
        </c:choose>
      </ul>
    </nav>
  </div>
</header>
<!-- Script to toggle profile dropdown -->
<script>
  document.addEventListener('DOMContentLoaded', function () {
    const profileIcon = document.getElementById('profileIcon');
    const profileDropdown = document.getElementById('profileDropdown');

    if (profileIcon && profileDropdown) {
      profileIcon.addEventListener('click', function (event) {
        event.preventDefault();
        profileDropdown.style.display = profileDropdown.style.display === 'block' ? 'none' : 'block';
      });

      document.addEventListener('click', function (event) {
        if (profileDropdown && profileIcon && !profileIcon.contains(event.target) && !profileDropdown.contains(event.target)) {
          profileDropdown.style.display = 'none';
        }
      });
    }
  });
</script>
