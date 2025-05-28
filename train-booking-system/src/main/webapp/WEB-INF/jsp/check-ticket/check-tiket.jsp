<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>Tìm kiếm vé tàu</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/check-booking.css" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            
        </head>

        <body>
            <header>
                <div class="navbar">
                    <img src="images/logo.png" class="logo" alt="Logo">

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
                    <img src="images/image-26-72.png" class="news" alt="news">
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
                                        <td><input type="text" placeholder="Nhập mã đặt chỗ" required></td>
                                    </tr>
                                    <tr>
                                        <td>Điện thoại <span style="color:red">*</span></td>
                                        <td><input type="text" placeholder="Nhập số điện thoại" required></td>
                                    </tr>
                                    <tr>
                                        <td>Email</td>
                                        <td><input type="email" placeholder="Email"><br></td>
                                    </tr>
                                    <tr>
                                        <td><button type="submit">Xác Nhận</button></td>
                                    </tr>
                                </tbody>
                            </table>
                        </form>
                    </div>
                </section>

                <div>
                    <img src="images/image-27-73.png" class="news" alt="news">
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
                        <a href="#"><img src="icons/facebook.png" alt="Facebook"></a>
                        <a href="#"><img src="icons/twitter.png" alt="Twitter"></a>
                        <a href="#"><img src="icons/instagram.png" alt="Instagram"></a>
                        <!-- <a href="#"><img src="icons/telegram.png" alt="Telegram"></a> -->
                        <a href="#"><img src="icons/youtube.png" alt="YouTube"></a>
                    </div>
                </div>

                <div class="footer-bottom">
                    <img src="images/logo.png" alt="Logo" class="footer-logo">
                    <p>Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
                    <hr>
                    <p class="copyright">2025. Copyright and All rights reserved.</p>
                </div>
            </footer>
        </body>

        </html>