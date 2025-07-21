<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tìm kiếm lại mã đặt chỗ</title>
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
                <label class="et-main-label">TÌM KIẾM LẠI TẤT CẢ MÃ ĐẶT CHỖ</label>
                <p>Để lấy lại mã đặt chỗ, quý khách vui lòng nhập email đã từng đặt chỗ.</p>
                <form id="returnForm" method="post" action="forgotbookingcode">
                    <table cellspacing="10">
                        <tbody>
                            <tr>
                                <td><strong>Email</strong> <span style="color: red">*</span></td>
                                <td>
                                    <input type="email" placeholder="Email" name="email" value="${email}" required /><br />
                                </td>
                            </tr>
                            <tr>
                                <td><button type="submit"><strong>Xác Nhận</strong></button></td>
                            </tr>
                        </tbody>
                    </table>
                </form>
                <c:if test="${not empty errorMessage}">
                    <p style="color: red; margin-top: 10px;">${errorMessage}</p>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <p style="color: green; margin-top: 10px;">${successMessage}</p>
                </c:if>
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
