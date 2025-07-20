<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Trợ giúp và Hỗ trợ</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customer-support.css" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
            </head>

            <body>
                <div style="margin-bottom: 20px; background-color: #0E385A;">
                    <a href="${pageContext.request.contextPath}/landing" class="back-button" style="color: white;">Quay
                        lại trang
                        chủ</a>
                </div>
                <div class="support-container">
                    <h1 style="color: #0E385A;">Trợ giúp và Hỗ trợ Khách hàng</h1>
                    <p>Chúng tôi luôn sẵn sàng hỗ trợ bạn với các vấn đề liên quan đến vé tàu và dịch vụ.</p>

                    <div class="support-layout">
                        <div class="support-info">
                            <section class="support-section">
                                <h3><i class="fas fa-info-circle"></i>Các quy định</h3>
                                <h4><strong>Các Điều Kiện & Điều Khoản</strong></h4>
                                <p>1.1. Quy định vận chuyển.</p>
                                <p>1.2. Điều kiện sử dụng hệ thống mua vé trực tuyến</p>
                                <p>1.3. Điều khoản sử dụng website Vetaure.com</p>
                                <p><strong>Phương thức thanh toán</strong></p>
                                <p><strong>Chính sách hoàn trả vé</strong></p>
                                <p>3.1. Chính sách hoàn trả vé, đổi vé.</p>
                                <p>3.2. Quy định thời gian hoàn tiền</p>
                                <p><strong>Chính sách bảo mật thông tin</strong></p>
                            </section>

                            <section class="support-section">
                                <h3><i class="fas fa-question-circle"></i> Các câu hỏi thường gặp khi đi tàu hỏa?</h3>
                                <p><strong>Làm thế nào để đặt vé tàu online trên Vetaure ?</strong></p>
                                <ul>
                                    <li>Bước 1: Truy cập Website <a href="#" target="_blank">Vetaure.com</a>.</li>
                                    <li>Bước 2: Nhập thông tin hành trình (điểm đi, điểm đến, ngày khởi hành).</li>
                                    <li>Bước 3: Chọn chuyến tàu, toa tàu, loại ghế phù hợp.</li>
                                    <li>Bước 4: Thanh toán trực tuyến bằng thẻ ngân hàng hoặc ví điện tử.</li>
                                    <li>Bước 5: Nhận má tàu điện tử qua email hoặc SMS.</li>
                                </ul>
                                <p style="font-size: 12px">Với Vetaure, bạn có thể đặt vé mọi lúc, mọi nơi, nhanh chóng
                                    và an toàn.</p>
                                <p>Làm sao để mua được vé tàu giá rẻ ?</p>
                                <p>Cần chuẩn bị những gì khi đi tàu hoả ?</p>
                                <p>Quy định về hành lý khi đi tàu hỏa là gì ?</p>
                                <p>Vé tàu có được đổi trả không ?</p>
                                <p>Làm sao để kiểm tra thông tin vé tàu ?</p>
                                <p>Trẻ em đi tàu hỏa có cần mua vé không ?</p>
                                <p>Vé tùa Online có khác gì so với vé mua tại ga không ?</p>
                                <p>Tôi cần làm gì nếu không nhận được mã vé sau khi thành toán ?</p>
                                <p>Cần chuẩn bị những gì để lên tàu khi mua vé online ?</p>
                            </section>
                        </div>

                        <div class="chat-section">
                            <h2><i class="fas fa-comments"></i> Nhắn tin với Staff</h2>
                            <div class="chat-box" id="chatBox">
                                <c:if test="${not empty chatMessages}">
                                    <div class="messages">
                                        <c:forEach var="message" items="${chatMessages}">
                                            <div
                                                class="message-container ${message.senderType == 'Customer' ? 'sent' : 'received'}">
                                                <div class="message-bubble">
                                                    <p>${message.content}</p>
                                                    <small>
                                                        <fmt:formatDate value="${message.timestampAsDate}"
                                                            pattern="HH:mm" />
                                                    </small>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${empty chatMessages}">
                                    <p>Chưa có tin nhắn. Hãy bắt đầu cuộc trò chuyện!</p>
                                </c:if>
                            </div>
                            <form action="${pageContext.request.contextPath}/customer-support" method="post"
                                onsubmit="return submitForm(event)">
                                <div class="chat-input">
                                    <textarea id="chatInput" name="message" placeholder="Nhập tin nhắn của bạn..."
                                        rows="3" required></textarea>
                                    <button type="submit">Gửi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <script>
                    function submitForm(event) {
                        event.preventDefault();
                        const form = event.target;
                        form.submit();
                        return false;
                    }
                </script>
            </body>

            </html>