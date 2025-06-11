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
          <a href="${pageContext.request.contextPath}/landing">
            <img src="${pageContext.request.contextPath}/assets/images/logo.png" class="logo" alt="Logo" />
          </a>

          <nav>
            <a href="${pageContext.request.contextPath}/searchTrip">Tìm vé</a>
            <a href="${pageContext.request.contextPath}/checkBooking">Thông tin đặt chỗ</a>
            <a href="#">Kiểm tra vé</a>
            <a href="#">Trả vé</a>
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
                    <td style="padding-right: 40px;"><input type="text" name="ticketCode" placeholder="Nhập mã vé" required /></td>
                    <td style="padding-right: 20px;"><strong>Mác tàu</strong></strong></td>
                    <td><input type="text" name="trainCode" placeholder="Nhập mác tàu" required /></td>
                  </tr>
                  <tr>
                    <td style="padding-right: 20px;"><strong>Ga đi</strong></strong></td>
                    <td style="padding-right: 40px;"><input type="text" name="departureStation" placeholder="Nhập ga đi" required /></td>
                    <td style="padding-right: 20px;"><strong>Ga đến</strong></strong></td>
                    <td><input type="text" name="arrivalStation" placeholder="Nhập ga đến" required /></td>
                  </tr>
                  <tr>
                    <td style="padding-right: 20px;"><strong>Ngày đi</strong></td>
                    <td style="padding-right: 40px;"><input type="date" name="departureDate" required /></td>
                    <td style="padding-right: 20px;"><strong>Số giấy tờ</strong></strong></td>
                    <td><input type="text" name="idNumber" placeholder="Nhập số giấy tờ" required /></td>
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
        </section>
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
            <a href="#"><img src="${pageContext.request.contextPath}/assets/icons/instagram.png" alt="Instagram" /></a>
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