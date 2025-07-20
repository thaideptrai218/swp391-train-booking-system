<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Tra cứu thông tin đặt vé</title>
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
                    <!-- <div>
                    <img src="${pageContext.request.contextPath}/assets/images/image-26-72.png" class="news" alt="news">
                </div> -->

                    <section class="check-info">
                        <div class="form-checking">
                            <label class="et-main-label">TRẢ VÉ TRỰC TUYẾN</label>
                            <p>Để trả vé, quý khách vui lòng thông tin bên dưới.</p>
                            <form id="returnForm" method="get" action="refundTicket">
                                <table cellspacing="10">
                                    <tbody>
                                        <tr>
                                            <td><strong>Mã đặt chỗ</strong> <span style="color: red">*</span></td>
                                            <td>
                                                <input type="text" name="bookingCode" placeholder="Nhập mã đặt chỗ"
                                                    value="${bookingCode}" required />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Điện thoại</strong></td>
                                            <td>
                                                <input type="text" name="phoneNumber" placeholder="Nhập số điện thoại"
                                                    value="${checkInforRefundTicketDTO.userPhoneNumber}" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><strong>Email</strong></td>
                                            <td>
                                                <input type="email" placeholder="Email" name="email"
                                                    value="${checkInforRefundTicketDTO.userEmail}" /><br />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td><button type="submit"><strong>Xác Nhận</strong></button></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </form>
                        </div>

                        <div class="booking-summary">
                            <c:if test="${not empty errorMessage}">
                                <p style="color: red">${errorMessage}</p>
                            </c:if>

                            <c:if test="${not empty checkInforRefundTicketDTO}">
                                <form id="continueForm" method="post" action="confirmRefundTicket">
                                    <input type="hidden" name="bookingCode" value="${bookingCode}" />
                                    <input type="hidden" name="phoneNumber"
                                        value="${checkInforRefundTicketDTO.userPhoneNumber}" />
                                    <input type="hidden" name="email" value="${checkInforRefundTicketDTO.userEmail}" />

                                    <h3 style="color: red">Danh sách vé</h3>
                                    <table style="width: 100%; border-collapse: collapse; border: 1px solid #ccc;">
                                        <thead>
                                            <tr style="background-color: #e8f0fe; text-align: center;">
                                                <th>#</th>
                                                <th>Họ tên</th>
                                                <th>Thông tin vé</th>
                                                <th>Thành tiền (VND)</th>
                                                <th>Loại trả vé</th>
                                                <th>Lệ phí trả vé</th>
                                                <th>Tiền trả lại</th>
                                                <th>Thông tin vé trả</th>
                                                <th>Chọn vé trả</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="p" items="${checkInforRefundTicketDTO.refundTicketDTOs}"
                                                varStatus="i">
                                                <tr style="background-color: #f0f8ff;">
                                                    <td colspan="9" style="font-weight: bold; padding: 8px;">
                                                        ${p.startStationName} - ${p.endStationName}
                                                        ${p.scheduledDepartureTime}
                                                    </td>
                                                </tr>
                                                <tr style="border-top: 1px solid #ccc;">
                                                    <td style="text-align: start;">${i.index + 1}</td>
                                                    <td style="text-align: start;">
                                                        <b>${p.passengerFullName}</b><br />
                                                        ${p.passengerType}<br />
                                                        Số giấy tờ: ${p.passengerIDCard}
                                                    </td>
                                                    <td style="text-align: start;">
                                                        ${p.trainName}<br />
                                                        Toa: ${p.coachName} – Ghế số: ${p.seatNumber}<br />
                                                        ${p.seatTypeName}<br />
                                                        Mã vé: ${p.ticketCode}
                                                    </td>
                                                    <td style="text-align: right;">
                                                        <fmt:formatNumber value="${p.price}" type="number"
                                                            groupingUsed="true" />đ
                                                    </td>
                                                    <td style="text-align: center;">${p.refundPolicy}</td>
                                                    <td style="text-align: right;">
                                                        <fmt:formatNumber value="${p.refundFee}" type="number"
                                                            groupingUsed="true" />đ
                                                    </td>
                                                    <td style="text-align: right;">
                                                        <fmt:formatNumber value="${p.refundAmount}" type="number"
                                                            groupingUsed="true" />đ
                                                    </td>
                                                    <td style="text-align: center;">${p.refundStatus}</td>
                                                    <td style="text-align: center;">
                                                        <c:if test="${p.refundable}">
                                                            <input type="checkbox" name="ticketInfo"
                                                                value="${p.ticketCode}|${p.ticketID}|${p.policyID}|${p.refundFee}|${p.refundAmount}"/>
                                                        </c:if>
                                                        <c:if test="${not p.refundable}">
                                                            <span style="color: gray;">Không thể trả</span>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>

                                    <h3 style="color: red; margin-top: 20px;">Thông tin người đặt vé</h3>
                                    <div class="booking-info-grid">
                                        <div class="form-row">
                                            <label>Họ và tên</label>
                                            <input type="text" value="${checkInforRefundTicketDTO.userFullName}"
                                                readonly />
                                            <label>Email</label>
                                            <input type="text" value="${checkInforRefundTicketDTO.userEmail}"
                                                readonly />
                                        </div>
                                        <div class="form-row">
                                            <label>Số CMND</label>
                                            <input type="text" value="${checkInforRefundTicketDTO.userIDCardNumber}"
                                                readonly />
                                            <label>Số điện thoại</label>
                                            <input type="text" value="${checkInforRefundTicketDTO.userPhoneNumber}"
                                                readonly />
                                        </div>
                                    </div>

                                    <button id="continueBtn" type="submit" disabled
                                        style="width: 10%; margin-top: 15px; padding: 10px 20px; font-size: 16px; cursor: not-allowed;">Tiếp
                                        tục</button>
                                </form>
                            </c:if>
                        </div>
                    </section>

                    <!-- <div>
                    <img src="${pageContext.request.contextPath}/assets/images/image-27-73.png" class="news" alt="news">
                </div> -->
                </main>

                <script src="${pageContext.request.contextPath}/js/refund-ticket/refund-ticket.js"></script>

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