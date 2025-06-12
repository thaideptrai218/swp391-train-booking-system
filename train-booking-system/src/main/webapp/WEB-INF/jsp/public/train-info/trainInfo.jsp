<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chọn loại tàu phù hợp</title>
    <meta name="description" content="Thông tin chi tiết về các loại tàu tại Việt Nam">
    <meta name="keywords" content="tàu hỏa, đường sắt Việt Nam, vé tàu, SE, TN">
    <meta name="author" content="Vetaure">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/train-info/train-info.css">
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
        <section class="train-selection">
            <h2>Nên chọn loại tàu nào khi đặt vé?</h2>
            <ul class="train-options">
                <li>Bạn cần đi nhanh, tiện nghi tốt? → Chọn tàu SE1-SE6</li>
                <li>Bạn muốn tiết kiệm chi phí? → Chọn tàu TN</li>
                <li>Bạn đi chuyến trong khu vực gần? → Chọn tàu địa phương</li>
                <li>Bạn muốn trải nghiệm sang trọng? → Chọn tàu 5 sao hoặc tàu du lịch</li>
            </ul>
            
            <div class="info-container">
                <div class="info-text">
                    <p>Hiện nay, Đường sắt Việt Nam vận hành nhiều loại tàu với đặc điểm khác nhau nhằm đáp ứng nhu cầu đi chuyến của hành khách. Dưới đây là thông tin chi tiết về từng loại tàu, giúp bạn lựa chọn chuyến đi phù hợp.</p>
                </div>
                <div class="info-image">
                    <img src="${pageContext.request.contextPath}/assets/images/train-info/image1.png" alt="Nội thất tàu">
                </div>
            </div>
        </section>

        <section class="train-details">
            <h3>1. Tàu SE (Super Express) – Tàu nhanh Thống Nhất</h3>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image2.png" alt="Tàu SE">
            <ul>
                <li>Lộ trình: Hà Nội - TP.HCM</li>
                <li>Tốc độ: Nhanh, đứng ít ga hơn các tàu khác</li>
                <li>Tiện nghi: Có khoang VIP 2 giường, điều hòa, nội thất hiện đại</li>
            </ul>
            
            <h4>Các chuyến tàu SE phổ biến:</h4>
            <ul>
                <li>SE1/SE2: Chất lượng cao nhất, khoang giường VIP rộng rãi, dịch vụ tiện nghi.</li>
                <li>SE3/SE4: Chạy ban đêm, giúp hành khách nghỉ ngơi và đến nơi vào sáng hôm sau</li>
                <li>SE5/SE6: Hoạt động ban ngày, phù hợp cho những ai muốn ngắm cảnh dọc hành trình.</li>
                <li>SE7/SE8: Xuất phát vào sáng sớm, thích hợp với hành trình dài trong ngày.</li>
            </ul>
            <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn cần đi chuyến nhanh với tiện nghi tốt, tàu SE là lựa chọn hợp lý.</p>
            
            <h3>2. Tàu TN (Tàu Thống Nhất) – Giá vé tiết kiệm hơn</h3>
            <ul>
                <li>Lộ trình: Hà Nội - TP.HCM</li>
                <li>Tốc độ: Chậm hơn SE, dừng tại nhiều ga hơn</li>
                <li>Giá vé: Thấp hơn so với tàu SE</li>
                <li>Tàu TN phù hợp với hành khách muốn tiết kiệm chi phí hoặc cần lên/xuống tại các ga phụ dọc tuyến.</li>
            </ul>
            <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn không quá gấp về thời gian và muốn mức giá hợp lý hơn, tàu TN là phương án thích hợp.</p>
            
            <h3>3. Tàu địa phương – Kết nối các tuyến ngắn</h3>
            <ul>
                <li>Lộ trình: Các tuyến ngắn như Sài Gòn – Phan Thiết, Hà Nội – Hải Phòng, Đà Nẵng – Huế</li>
                <li>Tốc độ: Trung bình, dừng tại nhiều ga nhỏ</li>
                <li>Tiện ích: Đơn giản, phù hợp cho hành khách đi lại giữa các tỉnh lân cận</li>
            </ul>
            <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn chỉ đi chuyến trong khu vực gần, tàu địa phương là lựa chọn thuận tiện và tiết kiệm.</p>
            
            <h3>4. Tàu 5 sao – Trải nghiệm cao cấp</h3>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image3.png" alt="Tàu 5 sao">
            <ul>
                <li>Lộ trình: Sài Gòn – Nha Trang, Sài Gòn – Đà Nẵng, Hà Nội – Đà Nẵng</li>
                <li>Tiện nghi: Nội thất hiện đại, khoang VIP, nhà hàng trên tàu, ghế xoay 180 độ</li>
            </ul>

            <h4>Các tàu chất lượng cao trên tuyến đường sắt Việt Nam:</h4>
            <ul>
                <li>SNT2 (Sài Gòn – Nha Trang): Tàu đêm, giường nằm rộng rãi, khoang ghế mềm điều chỉnh độ ngả</li>
                <li>SE19/SE20 (Hà Nội – Đà Nẵng): Khoang 2 giường VIP, chống ồn, tiện nghi cao cấp</li>
                <li>Sjourney: Hành trình xuyên Việt 7 ngày, dịch vụ cao cấp, giá vé cao</li>
            </ul>
            <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn tìm kiếm trải nghiệm sang trọng, thoải mái như đi máy bay hạng thương gia, tàu 5 sao là lựa chọn lý tưởng.</p>

            <h3>Giới thiệu về Đường Sắt Việt Nam</h3>
            <p>Đường sắt Việt Nam là hệ thống vận tải đường sắt chính ở Việt Nam, do Tổng công ty Đường sắt Việt Nam (dsvn.vn) quản lý. Hệ thống này có tổng chiều dài 3.161 km, với tuyến đường sắt Bắc-Nam là tuyến đường sắt huyết mạch, nối liền thủ đô Hà Nội với thành phố Hồ Chí Minh và nhiều tỉnh thành khác trên cả nước.</p>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image4.png" alt="Đường sắt Việt Nam">
            <p>Tuyến đường sắt Bắc-Nam được khởi công xây dựng từ thời Pháp thuộc. Trải qua nhiều thăng trầm lịch sử, đường sắt Việt Nam đã đóng góp vai trò quan trọng trong sự phát triển kinh tế - xã hội và an ninh quốc phòng của đất nước.</p>
            <p>Ngày nay, cùng với sự phát triển của các loại hình vận tải khác, đường sắt Việt Nam vẫn giữ vai trò quan trọng trong việc vận chuyển hành khách và hàng hóa, đặc biệt là trên tuyến đường sắt Bắc-Nam.</p>
        </section>

        <table class="station-table" aria-label="Thông tin các ga tàu">
            <thead>
                <tr>
                    <th scope="col">Tên ga</th>
                    <th scope="col">Số điện thoại</th>
                    <th scope="col">Địa chỉ</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td>Ga Hà Nội</td>
                    <td>19000109</td>
                    <td>120 Lê Duẩn, Hoàn Kiếm, Hà Nội</td>
                </tr>
                <tr>
                    <td>Ga Vinh</td>
                    <td>(0238) 3853 426</td>
                    <td>Số 1 Đường Lê Ninh - Phường Quán Bàu - Thành phố Vinh - Tỉnh Nghệ An</td>
                </tr>
                <tr>
                    <td>Ga Huế</td>
                    <td>(0234) 3822 175</td>
                    <td>Số 02 Bùi Thị Xuân - Thành phố Huế - Tỉnh Thừa Thiên Huế</td>
                </tr>
                <tr>
                    <td>Ga Sài Gòn</td>
                    <td>1900 636 212</td>
                    <td>Số 01 Nguyễn Thông - Phường 9 - Quận 3 - Thành phố Hồ Chí Minh</td>
                </tr>
            </tbody>
        </table>
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
                    <a href="#" aria-label="Facebook"><img src="${pageContext.request.contextPath}/assets/icons/facebook.png" alt=""></a>
                    <a href="#" aria-label="Twitter"><img src="${pageContext.request.contextPath}/assets/icons/twitter.png" alt=""></a>
                    <a href="#" aria-label="Instagram"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png" alt=""></a>
                    <a href="#" aria-label="YouTube"><img src="${pageContext.request.contextPath}/assets/icons/youtube.png" alt=""></a>
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