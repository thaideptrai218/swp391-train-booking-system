<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Landing Page</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing-page.css" />
  </head>
  <body>
    <section class="hero">
      <header class="navbar">
        <div class="container">
          <div class="logo-block">
            <img src="${pageContext.request.contextPath}/assets/icons/landing/logo.svg" alt="Logo" class="logo" />
          </div>
          <nav>
            <ul class="nav-list">
              <li><a href="#">Tìm vé</a></li>
              <li><a href="#">Thông tin đặt chỗ</a></li>
              <li><a href="#">Kiểm tra vé</a></li>
              <li><a href="#">Trả vé</a></li>
              <li><a href="#">Hotline</a></li>
              <li><a href="#">Đăng nhập</a></li>
              <li class="btn"><a href="#">Đăng kí</a></li>
            </ul>
          </nav>
        </div>
      </header>
      <div class="container hero-content-wrapper">
        <div class="hero-content">
          <!-- Renamed class for clarity -->
          <h1 class="hero-title">Đến với chúng tôi</h1>
          <p class="hero-subtitle">Trải nghiệm dịch vụ chất lượng</p>

          <div class="actions">
            <button class="btn btn-primary">Tìm hiểu thêm</button>
            <button class="btn btn-secondary">
              <img
                src="${pageContext.request.contextPath}/assets/icons/landing/phone.png"
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
        <!-- Added container for overall padding and max-width -->
        <div class="main-content-area">
          <div class="left-column">
            <h2 class="column-title">Hành trình tàu</h2>
            <h3 class="sub-heading">Danh sách tuyến đường sắt và Ga</h3>
            <div class="location-list">
              <c:if test="${not empty errorMessage}">
                <p style="color: red;"><c:out value="${errorMessage}" /></p>
              </c:if>
              <c:if test="${empty stationList and empty errorMessage}">
                <p>Không có thông tin ga tàu nào để hiển thị.</p>
              </c:if>
              <c:forEach var="station" items="${stationList}">
                <div class="location-item">
                  <img
                    src="${pageContext.request.contextPath}/assets/icons/landing/stations/station${station.stationID}.jpg"
                    alt="Ga <c:out value='${station.stationName}'/>"
                    onerror="this.onerror=null; this.src='https://via.placeholder.com/150x100?text=Image+Not+Found';" <%-- Fallback image --%>
                    style="width: 150px; height: 100px; object-fit: cover;" <%-- Basic styling for consistency --%>
                  />
                  <div class="location-info">
                    <h4><c:out value="${station.stationName}" /></h4>
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
              <img src="${pageContext.request.contextPath}/assets/icons/landing/map.jpg" alt="Bản đồ hành trình" />
            </div>
          </div>
        </div> <!-- End of main-content-area -->

        <div class="introduce">
          <!-- This section is outside the two-column layout -->
          <h1 class="section-main-title">Đến với chúng tôi</h1>
          <p>
            Để khám phá hết vẻ đẹp của dải đất hình chữ S và trải nghiệm cuộc sống
            thi vị, có lẽ không gì tuyệt vời hơn một chuyến tàu dọc theo chiều dài
            đất nước. Khi đoàn tàu lăn bánh cũng là lúc hành khách được thư giãn
            ngắm nhìn Việt Nam với khung cảnh thiên nhiên và cuộc sống thường ngày
            bình dị qua ô cửa con tàu
          </p>
          <img class="img1" src="${pageContext.request.contextPath}/assets/icons/landing/img1.jpeg" />
        </div>

        <div class="hot-locations">
          <div class="section-header">
            <h1 class="section-main-title">Địa điểm nổi bật</h1>
            <div class="carousel-navigation">
              <a href="#" class="view-all-link"
                >VIEW ALL <span class="arrow">&rarr;</span></a
              >
              <button class="nav-arrow prev-arrow"><</button>
              <button class="nav-arrow next-arrow">></button>
            </div>
          </div>
          <div class="carousel-container">
            <div class="carousel-track">
              <c:if test="${not empty locationErrorMessage}">
                <p style="color: red;"><c:out value="${locationErrorMessage}" /></p>
              </c:if>
              <c:if test="${empty locationList and empty locationErrorMessage}">
                <p>Không có địa điểm nổi bật nào để hiển thị.</p>
              </c:if>
              <c:forEach var="location" items="${locationList}">
                <div class="location-card">
                  <img
                    src="${pageContext.request.contextPath}/assets/icons/landing/locations/location${location.locationID}.jpg"
                    alt="<c:out value='${location.locationName}'/>"
                    class="card-image"
                    onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=Image+Not+Found';" <%-- Fallback image --%>
                  />
                  <div class="card-content">
                    <h4 class="card-title"><c:out value="${location.locationName}" /></h4>
                    <button class="btn btn-card">Xem Chi Tiết</button>
                  </div>
                </div>
              </c:forEach>
            </div>
          </div>
        </div>
      </div> <!-- This is the closing div of the container that now holds introduce and hot-locations -->
    </div> <!-- This is the closing div of class="body" -->

    <!-- New Site Info / Footer Section -->
    <section class="site-info-footer">
      <div class="booking-prompt">
        <div class="container">
          <h2>Đặt vé ngay tại đây</h2>
          <p>Tận hưởng trải nghiệm dịch vụ tốt nhất và đến nơi mà bạn mơ ước</p>
          <p>Liên hệ ngay: 0963868888</p>
          <button class="btn btn-primary btn-book-now-footer">Đặt vé</button>
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
              <li><a href="#">Câu hỏi thường gặp</a></li>
              <li><a href="#">Tra cứu đơn hàng</a></li>
            </ul>
          </div>
          <div class="info-column payment-partners">
            <h3>Đối tác thanh toán</h3>
            <div class="logos-grid">
              <img
                src="https://via.placeholder.com/80x40?text=TCB"
                alt="Techcombank"
              />
              <img
                src="https://via.placeholder.com/80x40?text=MB"
                alt="MB Bank"
              />
              <img
                src="https://via.placeholder.com/80x40?text=VCB"
                alt="Vietcombank"
              />
              <img
                src="https://via.placeholder.com/80x40?text=Momo"
                alt="Momo"
              />
              <img
                src="https://via.placeholder.com/80x40?text=Visa"
                alt="Visa"
              />
              <img
                src="https://via.placeholder.com/80x40?text=Mastercard"
                alt="Mastercard"
              />
              <img
                src="https://via.placeholder.com/80x40?text=SPay"
                alt="S Pay"
              />
              <img
                src="https://via.placeholder.com/80x40?text=VTPB"
                alt="VTPank"
              />
            </div>
          </div>
          <div class="info-column certifications">
            <h3>Chứng nhận</h3>
            <div class="logos-list">
              <img
                src="https://via.placeholder.com/150x50?text=VNExpress"
                alt="VNExpress"
              />
              <img
                src="https://via.placeholder.com/100x40?text=PayPal"
                alt="PayPal"
              />
              <img
                src="https://via.placeholder.com/100x40?text=VerifiedByVisa"
                alt="Verified by Visa"
              />
              <img
                src="https://via.placeholder.com/100x40?text=MasterSecure"
                alt="Mastercard SecureCode"
              />
            </div>
          </div>
          <div class="info-column app-download">
            <h3>Tải ứng dụng Vetaure</h3>
            <div class="app-buttons">
              <a href="#"
                ><img
                  src="https://via.placeholder.com/150x50?text=App+Store"
                  alt="Download on the App Store"
              /></a>
              <a href="#"
                ><img
                  src="https://via.placeholder.com/150x50?text=Google+Play"
                  alt="Get it on Google Play"
              /></a>
            </div>
          </div>
        </div>
      </div>

      <footer class="main-footer">
        <div class="footer-top">
          <div class="container">
            <a href="#">Tin tức</a>
            <a href="#">Hỗ trợ</a>
            <a href="#">FAQ</a>
            <a href="#">Liên hệ</a>
          </div>
        </div>
        <div class="footer-middle">
          <div class="container">
            <span>Kết nối với chúng tôi thông qua mạng xã hội</span>
            <div class="social-icons">
              <a href="#"
                ><img
                  src="https://via.placeholder.com/32x32/3B5998/FFFFFF?text=f"
                  alt="Facebook"
              /></a>
              <a href="#"
                ><img
                  src="https://via.placeholder.com/32x32/55ACEE/FFFFFF?text=t"
                  alt="Twitter"
              /></a>
              <a href="#"
                ><img
                  src="https://via.placeholder.com/32x32/E4405F/FFFFFF?text=ig"
                  alt="Instagram"
              /></a>
              <a href="#"
                ><img
                  src="https://via.placeholder.com/32x32/0088CC/FFFFFF?text=tg"
                  alt="Telegram"
              /></a>
              <a href="#"
                ><img
                  src="https://via.placeholder.com/32x32/FF0000/FFFFFF?text=yt"
                  alt="YouTube"
              /></a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <div class="container">
            <img
              src="https://via.placeholder.com/120x40?text=Vetaure.com"
              alt="Vetaure.com Logo"
              class="footer-logo"
            />
            <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
            <p class="copyright">
              &copy; 2025. Copyright and All rights reserved.
            </p>
          </div>
        </div>
      </footer>
    </section>

    <script src="${pageContext.request.contextPath}/js/landing/landing-page.js"></script>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
  </body>
</html>
