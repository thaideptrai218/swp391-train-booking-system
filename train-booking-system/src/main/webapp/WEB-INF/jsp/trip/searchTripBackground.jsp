<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Trip Background</title>
    <meta name="description" content="Trang tìm kiếm chuyến đi với nền tùy chỉnh">
    <meta name="keywords" content="tàu hỏa, vé tàu, tìm kiếm, background">
    <meta name="author" content="Vetaure">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
    <%-- Giả định một file CSS mới cho trang này. Bạn có thể thay đổi nếu cần. --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/searchTripBackground.css">
</head>
<body>
    <!-- Header -->
    <header>
        <nav aria-label="Main navigation">
            <div class="logo">
                <a href="${pageContext.request.contextPath}/">
                    <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Vetaure Logo" width="245" height="105">
                </a>
            </div>
            <div class="menu">
                <a href="${pageContext.request.contextPath}/trip/search">Tìm vé</a>
                <a href="${pageContext.request.contextPath}/booking/info">Thông tin đặt chỗ</a>
                <a href="${pageContext.request.contextPath}/ticket/check">Kiểm tra vé</a>
                <a href="${pageContext.request.contextPath}/ticket/refund">Trả vé</a>
                <a href="${pageContext.request.contextPath}/contact">Hotline</a>
                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn-register">Đăng kí</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/profile">${sessionScope.user.fullName}</a>
                        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </nav>
    </header>

    <!-- Nội dung chính -->
    <main>
        <section class="hero-section">
            <img src="${pageContext.request.contextPath}/assets/images/searchTripBackground.png" alt="Background Image" class="hero-background-image">
            <%-- You can add text or other elements over the background image here if needed --%>
        </section>

        <section class="why-vetaure content-wrapper">
            <h2>Tại sao nên mua vé tàu lửa qua Vetaure.com?</h2>
            <div class="features">
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-checkmark.png" alt="Chắc chắn có vé Icon">
                    <h3>Chắc chắn có vé</h3>
                    <p>Tích hợp trực tiếp với Tổng công ty Đường sắt Việt Nam</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-payment.png" alt="Đa dạng hình thức thanh toán Icon">
                    <h3>Đa dạng hình thức thanh toán</h3>
                    <p>Thanh toán đa dạng và an toàn với ví điện tử (Momo, Zalopay...), thẻ ATM nội địa, thẻ Visa/Master.</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-gift.png" alt="Ưu đãi Icon">
                    <h3>Đi càng nhiều ưu đãi càng lớn</h3>
                    <p>Vetaure.com luôn có chương trình ưu đãi đối với ngày lễ</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-support.png" alt="Hỗ trợ Icon">
                    <h3>Hỗ trợ tận tình và nhanh chóng</h3>
                    <p>Vetaure.com luôn thấu hiểu khách hàng và hỗ trợ bạn mọi lúc mọi nơi cho hành trình trọn vẹn.</p>
                </div>
            </div>
        </section>

        <section class="price-board content-wrapper">
            <h2>Bảng giá vé tàu Đường sắt Việt Nam các tuyến phổ biến</h2>
            <p class="price-board-intro">Dưới đây là thông tin chi tiết về <strong>giá vé tàu hỏa</strong> cho các hành trình phổ biến, giúp bạn dễ dàng chọn lựa:</p>
            <ul>
                <li><span class="bullet">🔹</span> <strong>Huế - Đà Nẵng:</strong> Chỉ từ <strong>84.000đ</strong> cho ghế mềm điều hòa đến <strong>149.000đ</strong> cho vé giường nằm khoang 5.</li>
                <li><span class="bullet">🔹</span> <strong>Sài Gòn - Nha Trang:</strong> Chỉ từ <strong>265.000đ</strong> đến <strong>472.000đ</strong>, phù hợp cho các chuyến đi ngắn.</li>
                <li><span class="bullet">🔹</span> <strong>Hà Nội - Hải Phòng:</strong> Giá vé từ <strong>98.000đ</strong>, hành trình ngắn nên chỉ có ghế ngồi mềm</li>
                <li><span class="bullet">🔹</span> <strong>Sài Gòn - Hà Nội:</strong> Giá vé dao động từ <strong>919.000đ</strong> (ghế ngồi mềm điều hòa) đến <strong>1.518.000đ</strong> (giường nằm khoang 4).</li>
            </ul>
        </section>

        <section class="train-schedule content-wrapper">
            <h2>Bảng giờ tàu mới nhất từ Đường Sắt Việt Nam (Cập nhật tháng 3/2025)</h2>
            <p>Từ ngày <strong>18/3</strong>, Đường Sắt Việt Nam (DSVN) sẽ tăng cường thêm tàu <strong>SE11, SE12</strong> trên tuyến <strong>Hà Nội – TP. Hồ Chí Minh</strong>, giúp hành khách có thêm lựa chọn khi di chuyển giữa hai thành phố lớn.</p>
            <p>Hiện tại, các tuyến tàu chạy hàng ngày như sau:</p>
            <ul>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – TP. Hồ Chí Minh:</strong> SE1/SE2, SE3/SE4, SE5/SE6, SE7/SE8, SE11/SE12</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Đà Nẵng:</strong> SE21/SE22</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Quy Nhơn:</strong> SE30 (thứ 6 hàng tuần), SE29 (Chủ Nhật hàng tuần)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Nha Trang:</strong> SNT1/SNT2 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Phan Thiết:</strong> SPT1/SPT2 (thứ 6, 7, Chủ Nhật)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – Đà Nẵng:</strong> SE19/SE20 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – Vinh:</strong> NA1/NA2 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Đà Nẵng – Huế:</strong> HĐ1/HĐ2, HĐ3/HĐ4 (hàng ngày)</li>
            </ul>
        </section>

        <section class="railway-map content-wrapper">
            <h2>Bản Đồ Tuyến Đường Sắt Việt Nam - Ga Tàu & Các Tuyến Tàu Hỏa Chính</h2>
            <p>Khám phá bản đồ đường sắt Việt Nam với đầy đủ thông tin các tuyến tàu <strong>Bắc – Nam, Hà Nội – Hải Phòng, Hà Nội - Sapa, Đà Nẵng, Huế, Nha Trang, Phan Thiết</strong> và nhiều ga lớn trên cả nước. Hình ảnh trực quan giúp bạn dễ dàng tra cứu lộ trình, lựa chọn điểm đến và đặt vé tàu phù hợp.</p>
            <img src="${pageContext.request.contextPath}/assets/images/map.png" alt="Bản đồ tuyến đường sắt Việt Nam" class="map-image">
        </section>
    </main>

    <footer>
        <div class="footer-content">
            <div class="footer-nav">
                <a href="${pageContext.request.contextPath}/news">Tin tức</a>
                <a href="${pageContext.request.contextPath}/support">Hỗ trợ</a>
                <a href="${pageContext.request.contextPath}/faq">FAQ</a>
                <a href="${pageContext.request.contextPath}/contact">Liên hệ</a>
            </div>
            <div class="social-links">
                <h4>Kết nối với chúng tôi thông qua mạng xã hội</h4>
                <div class="social-icons">
                    <a href="#" aria-label="Facebook"><img src="${pageContext.request.contextPath}/assets/icons/facebook.png" alt="Facebook"></a>
                    <a href="#" aria-label="Twitter"><img src="${pageContext.request.contextPath}/assets/icons/twitter.png" alt="Twitter"></a>
                    <a href="#" aria-label="Instagram"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png" alt="Instagram"></a>
                    <a href="#" aria-label="YouTube"><img src="${pageContext.request.contextPath}/assets/icons/youtube.png" alt="YouTube"></a>
                </div>
            </div>
            <div class="footer-logo">
                <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Vetaure Logo">
            </div>
            <div class="footer-motto">
                Sự thoải mái của bạn là niềm vui của chúng tôi
            </div>
            <div class="copyright">
                <%= java.time.Year.now().getValue() %>. Copyright and All rights reserved.
            </div>
        </div>
    </footer>
</body>
</html>
