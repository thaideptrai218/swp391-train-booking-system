<%@ page pageEncoding="UTF-8" %> <%@ taglib uri="jakarta.tags.core" prefix="c"
%>

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
                    <a href="${pageContext.request.contextPath}/tripInfomation"
                        >Thông tin chuyến tàu</a
                    >
                </li>
                <li>
                  <a href="${pageContext.request.contextPath}/checkBooking">Thông tin đặt chỗ</a>
                </li>
                <li><a href="${pageContext.request.contextPath}/checkTicket">Kiểm tra vé</a></li>
                <li><a href="${pageContext.request.contextPath}/refundTicket">Trả vé</a></li>
                <li><a href="#" onclick="copyHotline(event)">Hotline</a></li>
<c:choose>
                  <c:when
                    test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'Customer'}">
                    <li class="profile-dropdown-container">
                      <a href="javascript:void(0);" id="profileIcon" class="nav-profile-icon"
                        title="Trang cá nhân">👤</a>
                      <div class="profile-dropdown-menu" id="profileDropdown">
                        <div class="dropdown-header">
                          <span class="dropdown-username">${sessionScope.loggedInUser.fullName}</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/customerprofile" class="dropdown-item">
                          <!-- <img width="24" height="24" src="https://img.icons8.com/fluency-systems-regular/24/about-us-male.png" alt="about-us-male"/> -->
                          </i> Tài Khoản</a>

                        <a href="${pageContext.request.contextPath}/feedback" class="dropdown-item">
                          <!-- <img width="24" height="24" src="https://img.icons8.com/fluency-systems-regular/24/users-settings.png" alt="users-settings"/> -->
                          </i> Góp ý</a>

                        <a href="${pageContext.request.contextPath}/customer-support" class="dropdown-item">
                          <!-- <img width="24" height="24" src="https://img.icons8.com/fluency-systems-regular/24/online-support.png" alt="online-support"/> -->
                          </i> Trợ giúp và hỗ trợ</a>

                        <a href="${pageContext.request.contextPath}/vip/purchase" class="dropdown-item">
                          <i class="fas fa-crown"></i> Mua thẻ VIP</a>

                        <a href="${pageContext.request.contextPath}/changepassword" class="dropdown-item">
                          </i> Thay đổi Mật khẩu</a>

                        <c:if test="${not empty sessionScope.loggedInUser}">
                          <a href="${pageContext.request.contextPath}/listTicketBooking?id=${sessionScope.loggedInUser.userID}"
                            class="dropdown-item">
                            Tất cả vé đã đặt
                          </a>
                        </c:if>


                        <a href="${pageContext.request.contextPath}/logout" class="dropdown-item logout-item"><img
                            width="24" height="24" src="https://img.icons8.com/fluency-systems-regular/24/exit--v1.png"
                            alt="exit--v1" />
                          </i> Đăng xuất</a>
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
<!-- Script to toggle profile dropdown and hotline functionality -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const profileIcon = document.getElementById("profileIcon");
        const profileDropdown = document.getElementById("profileDropdown");

        if (profileIcon && profileDropdown) {
            profileIcon.addEventListener("click", function (event) {
                event.preventDefault();
                profileDropdown.style.display =
                    profileDropdown.style.display === "block"
                        ? "none"
                        : "block";
            });

            document.addEventListener("click", function (event) {
                if (
                    profileDropdown &&
                    profileIcon &&
                    !profileIcon.contains(event.target) &&
                    !profileDropdown.contains(event.target)
                ) {
                    profileDropdown.style.display = "none";
                }
            });
        }
    });

    // Hotline copy functionality
    function copyHotline(event) {
        event.preventDefault();
        const hotlineNumber = "0983868888";

        // Try to use the modern clipboard API
        if (navigator.clipboard && window.isSecureContext) {
            navigator.clipboard
                .writeText(hotlineNumber)
                .then(function () {
                    showToast("Đã copy hotline: " + hotlineNumber);
                })
                .catch(function (err) {
                    console.error("Could not copy text: ", err);
                    fallbackCopyTextToClipboard(hotlineNumber);
                });
        } else {
            // Fallback for older browsers
            fallbackCopyTextToClipboard(hotlineNumber);
        }
    }

    function fallbackCopyTextToClipboard(text) {
        const textArea = document.createElement("textarea");
        textArea.value = text;
        textArea.style.top = "0";
        textArea.style.left = "0";
        textArea.style.position = "fixed";
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();

        try {
            const successful = document.execCommand("copy");
            if (successful) {
                showToast("Đã copy hotline: " + text);
            } else {
                showToast("Không thể copy hotline. Vui lòng thử lại.");
            }
        } catch (err) {
            console.error("Fallback: Oops, unable to copy", err);
            showToast("Hotline: " + text);
        }

        document.body.removeChild(textArea);
    }

    function showToast(message) {
        // Create toast element
        const toast = document.createElement("div");
        toast.textContent = message;
        toast.style.cssText = `
      position: fixed;
      top: 20px;
      right: 20px;
      background: #1659b8;
      color: white;
      padding: 12px 24px;
      border-radius: 8px;
      z-index: 10000;
      font-family: Arial, sans-serif;
      box-shadow: 0 4px 12px rgba(0,0,0,0.3);
      transition: opacity 0.3s ease;
    `;

        document.body.appendChild(toast);

        // Remove toast after 3 seconds
        setTimeout(() => {
            toast.style.opacity = "0";
            setTimeout(() => {
                if (document.body.contains(toast)) {
                    document.body.removeChild(toast);
                }
            }, 300);
        }, 3000);
    }
</script>
