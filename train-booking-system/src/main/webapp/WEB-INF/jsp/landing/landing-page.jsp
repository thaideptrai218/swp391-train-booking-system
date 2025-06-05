<%@page contentType="text/html" pageEncoding="UTF-8"%> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Trang Chủ</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/landing-page.css"
    />
  </head>
  <body data-context-path="${pageContext.request.contextPath}">
    <section
      class="hero"
      style="
        background-image: url('${pageContext.request.contextPath}/assets/images/landing/top_BG.png');
        background-size: cover;
        background-position: center;
      "
    >
      <header class="navbar">
        <div class="container">
          <a
            href="${pageContext.request.contextPath}/landing"
            class="logo-block-link"
          >
            <div class="logo-block">
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/logo.svg"
                alt="Logo"
                class="logo"
              />
            </div>
          </a>
          <nav>
            <ul class="nav-list">
              <li><a href="#">Tìm vé</a></li>
              <li><a href="#">Thông tin đặt chỗ</a></li>
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
                    <a href="${pageContext.request.contextPath}/login"
                      >Đăng nhập</a
                    >
                  </li>
                  <li class="btn">
                    <a href="${pageContext.request.contextPath}/register"
                      >Đăng kí</a
                    >
                  </li>
                </c:otherwise>
              </c:choose>
            </ul>
          </nav>
        </div>
      </header>
      <div class="container hero-content-wrapper">
        <div class="hero-content">
          <h1 class="hero-title">Đến với chúng tôi</h1>
          <p class="hero-subtitle">Trải nghiệm dịch vụ chất lượng</p>

          <div class="actions">
            <a
              href="${pageContext.request.contextPath}/train-info"
              class="btn btn-primary"
              >Tìm hiểu thêm</a
            >
            <button class="btn btn-secondary">
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/phone.png"
                alt="Phone"
                class="phone-icon"
              />0983868888
            </button>
          </div>
        </div>
      </div>
    </section>

    <div class="body">
      <div class="container">
        <div class="main-content-area">
          <div class="left-column">
            <h2 class="column-title">Hành trình tàu</h2>
            <h3 class="sub-heading">Danh sách tuyến đường sắt và Ga</h3>
            <div class="location-list">
              <c:if test="${not empty errorMessage}">
                <p style="color: red"><c:out value="${errorMessage}" /></p>
              </c:if>
              <c:if test="${empty stationList and empty errorMessage}">
                <p>Không có thông tin ga tàu nào để hiển thị.</p>
              </c:if>
              <c:forEach var="station" items="${stationList}">
                <div class="location-item">
                  <img
                    src="${pageContext.request.contextPath}/assets/images/landing/stations/${station.stationCode}.jpg"
                    alt="<c:out value='${station.stationName}'/>"
                    onerror="this.onerror=null; this.src='https://via.placeholder.com/150x100?text=Image+Not+Found';"
                    style="width: 150px; height: 100px; object-fit: cover"
                  />
                  <div class="location-info">
                    <h4 id="station-name-${station.stationID}">
                      <a
                        href="#"
                        onclick="showStationPopup('${station.stationName}', '${station.address}', '${station.phoneNumber}', '${station.stationCode}'); return false;"
                        class="station-name-link"
                      >
                        <c:out value="${station.stationName}" />
                      </a>
                    </h4>
                    <p>Địa chỉ: <c:out value="${station.address}" /></p>
                    <p>Điện thoại: <c:out value="${station.phoneNumber}" /></p>
                  </div>
                </div>
              </c:forEach>
            </div>
          </div>
          <div class="right-column">
            <div class="search-title-wrapper">
              <h2 class="column-title">Tìm kiếm:</h2>
              <input
                type="text"
                placeholder="Nhập từ khóa tìm kiếm..."
                class="search-input-right"
                id="stationSearchInput"
              />
            </div>
            <h3 class="sub-heading">Bản đồ hành trình</h3>
            <div class="map-placeholder">
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/map.jpg"
                alt="Bản đồ hành trình"
              />
            </div>
          </div>
        </div>

        <div class="introduce">
          <h1 class="section-main-title">Đến với chúng tôi</h1>
          <p>
            Để khám phá hết vẻ đẹp của dải đất hình chữ S và trải nghiệm cuộc
            sống thi vị, có lẽ không gì tuyệt vời hơn một chuyến tàu dọc theo
            chiều dài đất nước. Khi đoàn tàu lăn bánh cũng là lúc hành khách
            được thư giãn ngắm nhìn Việt Nam với khung cảnh thiên nhiên và cuộc
            sống thường ngày bình dị qua ô cửa con tàu
          </p>
          <img
            class="img1"
            src="${pageContext.request.contextPath}/assets/images/landing/img1.jpeg"
          />
        </div>

        <div class="hot-locations">
          <div class="section-header">
            <h1 class="section-main-title">Địa điểm nổi bật</h1>
            <div class="carousel-navigation">
              <button class="nav-arrow prev-location"><</button>
              <button class="nav-arrow next-location">></button>
              <a
                href="${pageContext.request.contextPath}/all-locations"
                class="view-all-link"
                >Xem thêm <span class="arrow">&rarr;</span></a
              >
            </div>
          </div>
          <div class="carousel-container" id="hotLocationsCarousel">
            <div class="carousel-track">
              <c:if test="${not empty locationErrorMessage}">
                <p style="color: red">
                  <c:out value="${locationErrorMessage}" />
                </p>
              </c:if>
              <c:if test="${empty locationList and empty locationErrorMessage}">
                <p>Không có địa điểm nổi bật nào để hiển thị.</p>
              </c:if>
              <c:forEach
                var="location"
                items="${locationList}"
                varStatus="loop"
                begin="0"
                end="5"
              >
                <a
                  href="${location.link}"
                  class="location-card-link"
                  target="_blank"
                >
                  <div class="location-card">
                    <img
                      src="${pageContext.request.contextPath}/assets/images/landing/locations/${location.locationCode}.jpg"
                      alt="<c:out value='${location.locationName}'/>"
                      class="card-image"
                      onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=Image+Not+Found';"
                    />
                    <div class="card-content">
                      <h4 class="card-title">
                        <c:out value="${location.locationName}" />
                      </h4>
                      <p>Thành phố: <c:out value="${location.city}" /></p>
                      <p>Vùng: <c:out value="${location.region}" /></p>
                    </div>
                  </div>
                </a>
              </c:forEach>
            </div>
          </div>
        </div>

        <!-- Featured Routes Section -->
        <div class="featured-routes-section">
          <h1 class="section-main-title">Tuyến đường nổi bật</h1>
          <c:if test="${not empty featuredRouteErrorMessage}">
            <p style="color: red; text-align: center">
              <c:out value="${featuredRouteErrorMessage}" />
            </p>
          </c:if>
          <c:if
            test="${empty featuredRouteList and empty featuredRouteErrorMessage}"
          >
            <p style="text-align: center">
              Không có tuyến đường nổi bật nào để hiển thị.
            </p>
          </c:if>
          <div class="route-cards-grid">
            <c:forEach var="route" items="${featuredRouteList}">
              <div class="route-card">
                <img
                  src="${pageContext.request.contextPath}/assets/images/landing/stations/${route.destinationStationCode}.jpg"
                  alt="<c:out value='${route.displayName}'/>"
                  class="route-card-bg"
                  onerror="this.onerror=null; this.src='https://via.placeholder.com/400x220?text=Image+Not+Found';"
                />
                <div class="route-card-overlay">
                  <div class="route-card-content">
                    <h3 class="route-card-title">
                      <c:out value="${route.displayName}" />
                    </h3>
                    <div class="route-card-info">
                      <p>Tàu/Ngày: <c:out value="${route.tripsPerDay}" /></p>
                      <p>
                        Khoảng Cách:
                        <c:out
                          value="${String.format('%.1f', route.distance)}"
                        />
                        km
                      </p>
                      <p>
                        Tàu Nổi Bật:
                        <c:forEach
                          var="trainName"
                          items="${route.popularTrainNames}"
                          varStatus="loop"
                        >
                          <c:out value="${trainName}" /><c:if
                            test="${not loop.last}"
                            >,
                          </c:if>
                        </c:forEach>
                      </p>
                    </div>
                  </div>
                  <a
                    href="${pageContext.request.contextPath}/searchTrip?originalStation=${route.originStationID}&destinationID=${route.destinationStationID}"
                    class="route-card-button"
                    data-originid="${route.originStationID}"
                    data-destinationid="${route.destinationStationID}"
                  >
                    <span class="arrow-icon">&#x2197;</span>
                  </a>
                </div>
              </div>
            </c:forEach>
          </div>
        </div>
      </div>
    </div>

    <section class="site-info-footer">
      <div class="booking-prompt">
        <div class="container">
          <h2>Đặt vé ngay tại đây</h2>
          <p>Tận hưởng trải nghiệm dịch vụ tốt nhất và đến nơi mà bạn mơ ước</p>
          <p>Liên hệ ngay: 0963868888</p>
          <a
            href="${pageContext.request.contextPath}/searchTrip"
            class="btn btn-primary btn-book-now-footer"
            style="text-decoration: none"
            >đặt vé</a
          >
        </div>
      </div>

      <div class="info-grid">
        <div class="container">
          <div class="info-column support-links">
            <h3>Hỗ trợ</h3>
            <ul>
              <li><a href="#">Hướng dẫn thanh toán</a></li>
              <li><a href="#">Quy chế Vexere.com</a></li>
              <li><a href="#">Chính sách bảo mật thông tin</a></li>
              <li><a href="#">Chính sách bảo mật thanh toán</a></li>
              <li>
                <a href="#"
                  >Chính sách và quy trình giải quyết tranh chấp, khiếu nại</a
                >
              </li>
              <li>
                <a href="${pageContext.request.contextPath}/terms"
                  >Câu hỏi thường gặp</a
                >
              </li>
              <li><a href="#">Tra cứu đơn hàng</a></li>
            </ul>
          </div>
          <div class="info-column payment-partners">
            <h3>Đối tác thanh toán</h3>
            <div class="logos-grid">
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/techcombank.jpg"
                alt="Techcombank"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/mbbank.jpg"
                alt="MB Bank"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/vietcombank.jpg"
                alt="Vietcombank"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/momo.jpg"
                alt="Momo"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/visa.jpg"
                alt="Visa"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/mastercard.jpg"
                alt="Mastercard"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/spay.jpg"
                alt="S Pay"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/vpbank.jpg"
                alt="VPBank"
              />
            </div>
          </div>
          <div class="info-column certifications">
            <h3>Chứng nhận</h3>
            <div class="logos-list">
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/vnexpress.png"
                alt="VNExpress"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/paypal.jpg"
                alt="PayPal"
              />
              <img
                src="${pageContext.request.contextPath}/assets/images/landing/footer/visa.webp"
                alt="Verified by Visa"
              />
            </div>
          </div>
          <div class="info-column app-download">
            <h3>Tải ứng dụng Vetaure</h3>
            <div class="app-buttons">
              <a href="#"
                ><img
                  src="${pageContext.request.contextPath}/assets/images/landing/footer/appstore.jpg"
                  alt="Download on the App Store"
              /></a>
              <a href="#"
                ><img
                  src="${pageContext.request.contextPath}/assets/images/landing/footer/ggplay.webp"
                  alt="Get it on Google Play"
              /></a>
            </div>
          </div>
        </div>
      </div>

      <footer class="footer">
        <div class="footer-top">
          <a href="#">Tin tức</a>
          <a href="#">Hỗ trợ</a>
          <a href="${pageContext.request.contextPath}/terms">FAQ</a>
          <a href="#">Liên hệ</a>
        </div>

        <div class="footer-social">
          <p>Kết nối với chúng tôi thông qua mạng xã hội</p>
          <div class="social-icons">
            <a href="#"
              ><img
                src="${pageContext.request.contextPath}/assets/images/icons/facebook.jpg"
                alt="Facebook"
            /></a>
            <a href="#"
              ><img
                src="${pageContext.request.contextPath}/assets/images/icons/twitter.jpg"
                alt="Twitter"
            /></a>
            <a href="#"
              ><img
                src="${pageContext.request.contextPath}/assets/images/icons/instagram.png"
                alt="Instagram"
            /></a>
            <a href="#"
              ><img
                src="${pageContext.request.contextPath}/assets/images/icons/youtube.jpg"
                alt="YouTube"
            /></a>
          </div>
        </div>

        <div class="footer-bottom">
          <img
            src="${pageContext.request.contextPath}/assets/images/icons/logo.png"
            alt="Logo"
            class="footer-logo"
          />
          <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
          <hr />
          <p class="copyright">2025. Copyright and All rights reserved.</p>
        </div>
      </footer>
    </section>

    <script src="${pageContext.request.contextPath}/js/landing/landing-page.js"></script>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>

    <div id="stationModal" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 id="modalHeaderStationName"></h3>
          <span class="close-button" onclick="closeStationPopup()"
            >&times;</span
          >
        </div>
        <div class="modal-body">
          <div class="modal-body-left">
            <img
              id="modalStationImage"
              src=""
              alt="Station Image"
              class="modal-station-image"
            />
          </div>
          <div class="modal-body-right">
            <h4 id="modalBodyStationName"></h4>
            <p>
              <strong>Địa chỉ:</strong> <span id="modalStationAddress"></span>
            </p>
            <p>
              <strong>Điện thoại:</strong> <span id="modalStationPhone"></span>
            </p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-modal-close" onclick="closeStationPopup()">
            Close
          </button>
        </div>
      </div>
    </div>
  </body>
</html>
