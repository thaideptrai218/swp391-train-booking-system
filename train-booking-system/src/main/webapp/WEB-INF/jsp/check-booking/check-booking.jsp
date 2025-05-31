<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>Tra cứu thông tin đặt vé</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/check-booking.css" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

        </head>

        <body>
            <header>
                <div class="navbar">
                    <img src="${pageContext.request.contextPath}/assets/images/logo.png" class="logo" alt="Logo">

                    <nav>
                        <a href="#">Tìm vé</a>
                        <a href="#">Thông tin đặt chỗ</a>
                        <a href="#">Kiểm tra vé</a>
                        <a href="#">Trả vé</a>
                    </nav>
                    <div class="auth">
                        <a href="#">Đăng nhập</a>
                        <a class="register" href="#">Đăng ký</a>
                    </div>
                </div>
            </header>

            <main class="main-content">
                <div>
                    <img src="${pageContext.request.contextPath}/assets/images/image-26-72.png" class="news" alt="news">
                </div>


                <section class="check-info">
                    <div>
                        <label class="et-main-label">TRA CỨU THÔNG TIN ĐẶT CHỖ</label>
                        <p>Để tra cứu thông tin, quý khách vui lòng thông tin bên dưới.</p>
                        <form id="returnForm">
                            <table cellspacing="10">
                                <tbody>
                                    <tr>
                                        <td>Mã đặt chỗ <span style="color:red">*</span></td>
                                        <td><input type="text" name="bookingCode" placeholder="Nhập mã đặt chỗ" value="${bookingCode}" required></td>
                                    </tr>
                                    <tr>
                                        <td>Điện thoại</td>
                                        <td><input type="text" placeholder="Nhập số điện thoại" value="${checkBookingDTO.userPhoneNumber}"></td>
                                    </tr>
                                    <tr>
                                        <td>Email</td>
                                        <td><input type="email" placeholder="Email" value="${checkBookingDTO.userEmail}"><br></td>
                                    </tr>
                                    <tr>
                                        <td><button type="submit">Xác Nhận</button></td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </div>

                    <div>
                        <c:if test="${not empty errorMessage}">
                            <p style="color: red">${errorMessage}</p>
                        </c:if>

                        <c:if test="${not empty checkBookingDTO}">
                            <h3>Thông tin người đặt:</h3>
                            <p>Họ tên: ${checkBookingDTO.userFullName}</p>
                            <p>Email: ${checkBookingDTO.userEmail}</p>
                            <p>CMND: ${checkBookingDTO.userIDCardNumber}</p>
                            <p>SĐT: ${checkBookingDTO.userPhoneNumber}</p>

                            <h3>Danh sách vé:</h3>
                            <table border="1" cellpadding="5" cellspacing="0">
                                <tr>
                                    <th>Hành khách</th>
                                    <th>CMND</th>
                                    <th>Loại</th>
                                    <th>Ghế</th>
                                    <th>Loại ghế</th>
                                    <th>Toa</th>
                                    <th>Tàu</th>
                                    <th>Ga đi</th>
                                    <th>Ga đến</th>
                                    <th>Giá</th>
                                    <th>Trạng thái</th>
                                </tr>
                                <c:forEach var="p" items="${checkBookingDTO.infoPassengers}">
                                    <tr>
                                        <td>${p.passengerFullName}</td>
                                        <td>${p.passengerIDCard}</td>
                                        <td>${p.passengerType}</td>
                                        <td>${p.seatNumber}</td>
                                        <td>${p.seatTypeName}</td>
                                        <td>${p.coachName}</td>
                                        <td>${p.trainName}</td>
                                        <td>${p.startStationName}</td>
                                        <td>${p.endStationName}</td>
                                        <td>${p.price}</td>
                                        <td>${p.ticketStatus}</td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </c:if>
                    </div>
                </section>

                <div>
                    <img src="${pageContext.request.contextPath}/assets/images/image-27-73.png" class="news" alt="news">
                </div>

            </main>

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
                                alt="Facebook"></a>
                        <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/twitter.png"
                                alt="Twitter"></a>
                        <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png"
                                alt="Instagram"></a>
                        <!-- <a href="#"><img src="icons/telegram.png" alt="Telegram"></a> -->
                        <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/youtube.png"
                                alt="YouTube"></a>
                    </div>
                </div>

                <div class="footer-bottom">
                    <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Logo" class="footer-logo">
                    <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
                    <hr>
                    <p class="copyright">2025. Copyright and All rights reserved.</p>
                </div>
            </footer>
        </body>

        </html>