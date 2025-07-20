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
          <section class="check-info">
            <div class="form-checking">
              <label class="et-main-label">KIỂM TRA VÉ</label>
              <p>Để tra cứu thông tin, quý khách vui lòng nhập đầy đủ thông tin bên dưới.</p>
              <form id="ticketLookupForm" method="get" action="checkTicket">
                <table cellspacing="10">
                  <tbody>
                    <tr>
                      <td style="padding-right: 20px;"><strong>Mã vé</strong></td>
                      <td style="padding-right: 40px;"><input type="text" name="ticketCode" placeholder="Nhập mã vé"
                          value="${ticketCode}" /></td>
                      <td style="padding-right: 20px;"><strong>Mác tàu</strong></strong></td>
                      <td><input type="text" name="trainCode" placeholder="Nhập mác tàu" value="${trainCode}" /></td>
                    </tr>
                    <tr>
                      <td style="padding-right: 20px;"><strong>Ga đi</strong></strong></td>
                      <td style="padding-right: 40px;"><input type="text" name="departureStation" id="departureStation"
                          placeholder="Nhập ga đi" list="departureSuggestions" value="${departureStation}" />
                        <datalist id="departureSuggestions"></datalist>
                      </td>
                      <td style="padding-right: 20px;"><strong>Ga đến</strong></strong></td>
                      <td><input type="text" name="arrivalStation" id="arrivalStation" placeholder="Nhập ga đến"
                          list="arrivalSuggestions" autocomplete="off" value="${arrivalStation}" />
                        <datalist id="arrivalSuggestions"></datalist>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding-right: 20px;"><strong>Ngày đi</strong></td>
                      <td style="padding-right: 40px;"><input type="date" name="departureDate"
                          value="${departureDate}" />
                      </td>
                      <td style="padding-right: 20px;"><strong>Số giấy tờ</strong></strong></td>
                      <td><input type="text" name="idNumber" placeholder="Nhập số giấy tờ" value="${idNumber}" /></td>
                    </tr>
                    <tr>
                      <td>
                        <button type="submit"><strong>Kiểm tra vé</strong></button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </form>
            </div>

            <div class="booking-summary">
              <c:if test="${not empty errorMessage}">
                <p style="color: red">${errorMessage}</p>
              </c:if>

              <c:if test="${not empty infoPassenger}">
                <h3 style="color: red">Thông tin vé:</h3>
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

                  <tbody>
                    <tr>
                      <td colspan="9" style="font-weight: bold">
                        ${infoPassenger.startStationName} - ${infoPassenger.endStationName}
                        ${infoPassenger.scheduledDepartureTime}
                      </td>
                    </tr>
                    <tr>
                      <td style="text-align: start;">${infoPassenger.passengerFullName}<br>
                        Mã vé: ${infoPassenger.ticketCode}</td>
                      <td>${infoPassenger.passengerIDCard}</td>
                      <td>${infoPassenger.passengerType}</td>
                      <td>${infoPassenger.seatNumber}</td>
                      <td>${infoPassenger.seatTypeName}</td>
                      <td>${infoPassenger.coachName}</td>
                      <td>${infoPassenger.trainName}</td>
                      <td>
                        <fmt:formatNumber value="${infoPassenger.price}" type="number" groupingUsed="true" />đ
                      </td>
                      <td>
                        <c:choose>
                              <c:when test="${infoPassenger.ticketStatus == 'Valid'}">
                                <span style="color: #28a745; font-weight: bold;">Hợp lệ</span> <!-- xanh lá đẹp -->
                              </c:when>
                              <c:when test="${infoPassenger.ticketStatus == 'Used'}">
                                <span style="color: #007bff; font-weight: bold;">Đã sử dụng</span>
                                <!-- xanh dương tươi -->
                              </c:when>
                              <c:when test="${infoPassenger.ticketStatus == 'Processing'}">
                                <span style="color: #fd7e14; font-weight: bold;">Đang xử lý</span> <!-- cam nổi bật -->
                              </c:when>
                              <c:when test="${infoPassenger.ticketStatus == 'Refunded'}">
                                <span style="color: #dc3545; font-weight: bold;">Đã hủy</span> <!-- đỏ đậm -->
                              </c:when>
                              <c:when test="${infoPassenger.ticketStatus == 'RejectedRefund'}">
                                <span style="color: #dc3545; font-weight: bold;">Đã hủy</span> <!-- đỏ đậm -->
                              </c:when>
                              <c:when test="${infoPassenger.ticketStatus == 'Expired'}">
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