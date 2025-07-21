<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Lịch sử vé đã đặt</title>
                <link rel="stylesheet" type="text/css"
                    href="${pageContext.request.contextPath}/css/check-booking.css" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            </head>

            <body>
                <header>
                    <div class="navbar">
                        <a href="${pageContext.request.contextPath}/landing">
                            <img src="${pageContext.request.contextPath}/assets/images/logo.png" class="logo"
                                alt="Logo" />
                        </a>

                        <nav>
                            <a href="${pageContext.request.contextPath}/searchTrip">Tìm vé</a>
                            <a href="${pageContext.request.contextPath}/checkBooking">Thông tin đặt chỗ</a>
                            <a href="${pageContext.request.contextPath}/checkTicket">Kiểm tra vé</a>
                            <a href="${pageContext.request.contextPath}/refundTicket">Trả vé</a>
                        </nav>

                        <c:choose>
                            <c:when
                                test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'Customer'}">
                                <div class="user-info">
                                    <span class="username">${sessionScope.loggedInUser.fullName}</span>
                                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="auth">
                                    <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                                    <a class="register" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </header>

                <main class="main-content">
                    <section class="check-info">
                        <div class="booking-summary">
                            <c:if test="${not empty errorMessage}">
                                <p style="color: red">${errorMessage}</p>
                            </c:if>

                            <c:if test="${not empty infoPassenger}">
                                <h3 style="color: red">Thông tin vé:</h3>



                                <table style="width: 100%; border-collapse: collapse; border: 1px solid #ccc;">
                                    <thead>
                                        <tr style="background-color: #e8f0fe; text-align: center;">
                                            <th>#</th>
                                            <th>Họ tên</th>
                                            <th>Thông tin vé</th>
                                            <th>Thành tiền (VND)</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="info" items="${infoPassenger}"
                                            varStatus="i">
                                            <tr style="background-color: #f0f8ff;">
                                                <td colspan="9" style="font-weight: bold; padding: 8px;">
                                                    ${info.startStationName} - ${info.endStationName}
                                                    ${info.scheduledDepartureTime}
                                                </td>
                                            </tr>
                                            <tr style="border-top: 1px solid #ccc;">
                                                <td style="text-align: start;">${i.index + 1}</td>
                                                <td style="text-align: start;">
                                                    <b>${info.passengerFullName}</b><br />
                                                    ${info.passengerType}<br />
                                                    Số giấy tờ: ${info.passengerIDCard}
                                                </td>
                                                <td style="text-align: start;">
                                                    ${info.trainName}<br />
                                                    Toa: ${info.coachName} – Ghế số: ${info.seatNumber}<br />
                                                    ${info.seatTypeName}<br />
                                                    Mã vé: ${info.ticketCode}
                                                </td>
                                                <td style="text-align: right;">
                                                    <fmt:formatNumber value="${info.price}" type="number"
                                                        groupingUsed="true" />đ
                                                </td>
                                                
                                                <td style="text-align: center;">
                                                    <c:choose>
                                                        <c:when test="${info.ticketStatus == 'Valid'}">
                                                            <span style="color: #28a745; font-weight: bold;">Hợp
                                                                lệ</span> <!-- xanh lá đẹp -->
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Used'}">
                                                            <span style="color: #007bff; font-weight: bold;">Đã sử
                                                                dụng</span>
                                                            <!-- xanh dương tươi -->
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Processing'}">
                                                            <span style="color: #fd7e14; font-weight: bold;">Đang xử
                                                                lý</span> <!-- cam nổi bật -->
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Refunded'}">
                                                            <span style="color: #dc3545; font-weight: bold;">Đã
                                                                hủy</span> <!-- đỏ đậm -->
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'RejectedRefund'}">
                                                            <span style="color: #dc3545; font-weight: bold;">Đã
                                                                hủy</span> <!-- đỏ đậm -->
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Expired'}">
                                                            <span style="color: #6c757d; font-weight: bold;">Đã hết
                                                                hạn</span>
                                                            <!-- xám trung tính -->
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="color: #343a40; font-weight: bold;">Không xác
                                                                định</span> <!-- đen xám -->
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>  
                            </c:if>
                        </div>
                    </section>
                </main>

                <script src="${pageContext.request.contextPath}/js/check-ticket/check-ticket.js"></script>


                <footer class="footer">
                    <div class="footer-top">
                        <a href="#">Tin tức</a>
                        <a href="#">Hỗ trợ</a>
                        <a href="#">FAQ</a>
                        <a href="#">Liên hệ</a>
                    </div>

                    <div class="footer-social">
                        <p>Kết nối với chúng tôi thông qua mạng xã hội</p>
                        <div class="social-icons">
                            <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/facebook.png"
                                    alt="Facebook" /></a>
                            <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/twitter.png"
                                    alt="Twitter" /></a>
                            <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png"
                                    alt="Instagram" /></a>
                            <!-- <a href="#"><img src="icons/telegram.png" alt="Telegram"></a> -->
                            <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/youtube.png"
                                    alt="YouTube" /></a>
                        </div>
                    </div>

                    <div class="footer-bottom">
                        <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Logo"
                            class="footer-logo" />
                        <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
                        <hr />
                        <p class="copyright">2025. Copyright and All rights reserved.</p>
                    </div>
                </footer>
            </body>

            </html>