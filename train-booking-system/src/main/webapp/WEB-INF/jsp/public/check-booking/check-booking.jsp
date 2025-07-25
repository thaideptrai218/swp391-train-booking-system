<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <a href="${pageContext.request.contextPath}/landing">
              <img src="${pageContext.request.contextPath}/assets/images/logo.png" class="logo" alt="Logo" />
            </a>

            <nav>
              <a href="${pageContext.request.contextPath}/searchTrip">Tìm vé</a>
              <a href="${pageContext.request.contextPath}/checkBooking">Thông tin đặt chỗ</a>
              <a href="${pageContext.request.contextPath}/checkTicket">Kiểm tra vé</a>
              <a href="${pageContext.request.contextPath}/refundTicket">Trả vé</a>
            </nav>

            <c:choose>
              <c:when test="${not empty sessionScope.loggedInUser and sessionScope.loggedInUser.role == 'Customer'}">
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
              <label class="et-main-label">TRA CỨU THÔNG TIN ĐẶT CHỖ</label>
              <p>Để tra cứu thông tin, quý khách vui lòng thông tin bên dưới.</p>
              <form id="returnForm" method="get" action="checkBooking">
                <table cellspacing="10">
                  <tbody>
                    <tr>
                      <td><strong>Mã đặt chỗ</strong> <span style="color: red">*</span></td>
                      <td>
                        <input type="text" name="bookingCode" placeholder="Nhập mã đặt chỗ" value="${bookingCode}"
                          required />
                      </td>
                    </tr>
                    <tr>
                      <td><strong>Điện thoại</strong></td>
                      <td>
                        <input type="text" name="phoneNumber" placeholder="Nhập số điện thoại"
                          value="${checkBookingDTO.userPhoneNumber}" />
                      </td>
                    </tr>
                    <tr>
                      <td><strong>Email</strong></td>
                      <td>
                        <input type="email" placeholder="Email" name="email"
                          value="${checkBookingDTO.userEmail}" /><br />
                      </td>
                    </tr>
                    <tr>
                      <td><button type="submit"><strong>Xác Nhận</strong></button></td>
                      <td><a href="${pageContext.request.contextPath}/forgotbookingcode" style="margin-left: 10px;">Quên mã đặt chỗ?</a></td>
                    </tr>
                  </tbody>
                </table>
              </form>
            </div>

            <div class="booking-summary">
              <c:if test="${not empty errorMessage}">
                <p style="color: red">${errorMessage}</p>
              </c:if>

              <c:if test="${not empty checkBookingDTO}">
                <h3 style="color: red">Danh sách vé:</h3>
                <table border="1" cellpadding="5" cellspacing="0">
                  <thead>
                    <tr style="background-color: #5e5e5e50">
                      <th>Hành khách</th>
                      <th>CMND</th>
                      <th>Loại</th>
                      <th>Ghế</th>
                      <th>Loại ghế</th>
                      <th>Toa</th>
                      <th>Tàu</th>
                      <th>Giá</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>

                  <c:forEach var="p" items="${checkBookingDTO.infoPassengers}">
                    <tbody>
                      <tr>
                        <td colspan="9" style="font-weight: bold">
                          ${p.startStationName} - ${p.endStationName}
                          ${p.scheduledDepartureTime}
                        </td>
                      </tr>
                      <tr>
                        <td style="text-align: start;">${p.passengerFullName}<br>
                        Mã vé: ${p.ticketCode}</td>
                        <td>${p.passengerIDCard}</td>
                        <td>${p.passengerType}</td>
                        <td>${p.seatNumber}</td>
                        <td>${p.seatTypeName}</td>
                        <td>${p.coachName}</td>
                        <td>${p.trainName}</td>
                        <td>
                          <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true" />đ
                        </td>
                        <td>
                            <c:choose>
                              <c:when test="${p.ticketStatus == 'Valid'}">
                                <span style="color: #28a745; font-weight: bold;">Hợp lệ</span> <!-- xanh lá đẹp -->
                              </c:when>
                              <c:when test="${p.ticketStatus == 'Used'}">
                                <span style="color: #007bff; font-weight: bold;">Đã sử dụng</span>
                                <!-- xanh dương tươi -->
                              </c:when>
                              <c:when test="${p.ticketStatus == 'Processing'}">
                                <span style="color: #fd7e14; font-weight: bold;">Đang xử lý</span> <!-- cam nổi bật -->
                              </c:when>
                              <c:when test="${p.ticketStatus == 'Refunded'}">
                                <span style="color: #dc3545; font-weight: bold;">Đã hủy</span> <!-- đỏ đậm -->
                              </c:when>
                              <c:when test="${p.ticketStatus == 'RejectedRefund'}">
                                <span style="color: #dc3545; font-weight: bold;">Đã hủy</span> <!-- đỏ đậm -->
                              </c:when>
                              <c:when test="${p.ticketStatus == 'Expired'}">
                                <span style="color: #6c757d; font-weight: bold;">Đã hết hạn</span>
                                <!-- xám trung tính -->
                              </c:when>
                              <c:otherwise>
                                <span style="color: #343a40; font-weight: bold;">Không xác định</span> <!-- đen xám -->
                              </c:otherwise>
                            </c:choose>
                        </td>
                      </tr>
                    </tbody>
                  </c:forEach>
                </table>

                <h3 style="color: red">Thông tin người đặt vé</h3>
                <div class="booking-info-grid">
                  <div class="form-row">
                    <label>Họ và tên</label>
                    <input type="text" value="${checkBookingDTO.userFullName}" readonly />
                    <label>Email</label>
                    <input type="text" value="${checkBookingDTO.userEmail}" readonly />
                  </div>
                  <div class="form-row">
                    <label>Số CMND</label>
                    <input type="text" value="${checkBookingDTO.userIDCardNumber}" readonly />
                    <label>Số điện thoại</label>
                    <input type="text" value="${checkBookingDTO.userPhoneNumber}" readonly />
                  </div>
                </div>
              </c:if>
            </div>
          </section>

          <!-- <div>
                    <img src="${pageContext.request.contextPath}/assets/images/image-27-73.png" class="news" alt="news">
                </div> -->
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
              <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/facebook.png" alt="Facebook" /></a>
              <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/twitter.png" alt="Twitter" /></a>
              <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png"
                  alt="Instagram" /></a>
              <!-- <a href="#"><img src="icons/telegram.png" alt="Telegram"></a> -->
              <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/youtube.png" alt="YouTube" /></a>
            </div>
          </div>

          <div class="footer-bottom">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" alt="Logo" class="footer-logo" />
            <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
            <hr />
            <p class="copyright">2025. Copyright and All rights reserved.</p>
          </div>
        </footer>
      </body>

      </html>
