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
                                            <th>CMND/CCCD</th>
                                            <th>Loại hành khách</th>
                                            <th>Tàu & Ghế</th>
                                            <th>Tuyến đi</th>
                                            <th>Thời gian</th>
                                            <th>Mã vé</th>
                                            <th>Giá</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="info" items="${infoPassenger}" varStatus="i">
                                            <tr style="background-color: #f8fbfd;">
                                                <td style="text-align: center;">${fromIndex + i.index + 1}</td>
                                                <td>${info.passengerFullName}</td>
                                                <td>${info.passengerIDCard}</td>
                                                <td>${info.passengerType}</td>
                                                <td style="text-align: start;">
                                                    ${info.trainName}<br />
                                                    Toa: ${info.coachName} – Ghế: ${info.seatNumber}<br />
                                                    ${info.seatTypeName}
                                                </td>
                                                <td>${info.startStationName} - ${info.endStationName}</td>
                                                <td style="text-align: start;">Ngày đi:
                                                    ${info.scheduledDepartureTime.subSequence(0, 10)}<br />
                                                    Giờ khởi hành: ${info.scheduledDepartureTime.subSequence(11, 16)}
                                                </td>
                                                <td>${info.ticketCode}</td>
                                                <td style="text-align: right;">
                                                    <fmt:formatNumber value="${info.price}" type="number"
                                                        groupingUsed="true" />đ
                                                </td>
                                                <td style="text-align: center;">
                                                    <c:choose>
                                                        <c:when test="${info.ticketStatus == 'Valid'}">
                                                            <span style="color: #28a745; font-weight: bold;">Hợp
                                                                lệ</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Used'}">
                                                            <span style="color: #007bff; font-weight: bold;">Đã sử
                                                                dụng</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Processing'}">
                                                            <span style="color: #fd7e14; font-weight: bold;">Đang xử
                                                                lý</span>
                                                        </c:when>
                                                        <c:when
                                                            test="${info.ticketStatus == 'Refunded' || info.ticketStatus == 'RejectedRefund'}">
                                                            <span style="color: #dc3545; font-weight: bold;">Đã
                                                                hủy</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Expired'}">
                                                            <span style="color: #6c757d; font-weight: bold;">Đã hết
                                                                hạn</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="color: #343a40; font-weight: bold;">Không xác
                                                                định</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>


                                <div style="text-align: center; margin-top: 20px;">
                                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                        <c:choose>
                                            <c:when test="${pageNum == currentPage}">
                                                <span
                                                    style="padding: 5px 10px; margin: 2px; font-weight: bold; background-color: #007bff; color: white; border-radius: 5px;">
                                                    ${pageNum}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="listTicketBooking?page=${pageNum}"
                                                    style="padding: 5px 10px; margin: 2px; text-decoration: none; background-color: #e8f0fe; color: #007bff; border-radius: 5px;">
                                                    ${pageNum}
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>


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