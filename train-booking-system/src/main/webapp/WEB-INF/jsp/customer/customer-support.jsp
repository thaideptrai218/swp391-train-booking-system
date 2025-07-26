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
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

                <style>
                    .messages {
                        max-height: 400px;
                        overflow-y: auto;
                        position: relative;
                        background: #fff;
                    }

                    .message-container {
                        transition: none;
                    }

                    .message-bubble {
                        opacity: 1;
                        padding: 5px 10px;
                    }

                    .message-bubble p {
                        margin: 0;
                        color: #000;
                        display: block;
                    }

                    .message-bubble small {
                        color: #666;
                    }
                </style>
            </head>

            <body>
                    <jsp:include page="../common/header.jsp" />

                <div style="margin-bottom: 20px; background-color: #0E385A;">
                    <a href="${pageContext.request.contextPath}/landing" class="back-button" style="color: white;">Quay
                        lại trang chủ</a>
                </div>
                <div class="support-container">
                    <h1 style="color: #0E385A;">Trợ giúp và Hỗ trợ Khách hàng</h1>
                    <p>Chúng tôi luôn sẵn sàng hỗ trợ bạn với các vấn đề liên quan đến vé tàu và dịch vụ.</p>

                    <div class="support-layout">
                        <div class="support-info">
                            <section class="support-section">
                                <h3><i class="fas fa-info-circle"></i> Các quy định</h3>
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
                                <p><strong>Làm thế nào để đặt vé tàu online trên Vetaure?</strong></p>
                                <ul>
                                    <li>Bước 1: Truy cập Website <a href="#" target="_blank">Vetaure.com</a>.</li>
                                    <li>Bước 2: Nhập thông tin hành trình (điểm đi, điểm đến, ngày khởi hành).</li>
                                    <li>Bước 3: Chọn chuyến tàu, toa tàu, loại ghế phù hợp.</li>
                                    <li>Bước 4: Thanh toán trực tuyến bằng thẻ ngân hàng hoặc ví điện tử.</li>
                                    <li>Bước 5: Nhận mã vé điện tử qua email hoặc SMS.</li>
                                </ul>
                                <p style="font-size: 12px">Với Vetaure, bạn có thể đặt vé mọi lúc, mọi nơi, nhanh chóng
                                    và an toàn.</p>
                                <p>Làm sao để mua được vé tàu giá rẻ?</p>
                                <p>Cần chuẩn bị những gì khi đi tàu hỏa?</p>
                                <p>Quy định về hành lý khi đi tàu hỏa là gì?</p>
                                <p>Vé tàu có được đổi trả không?</p>
                                <p>Làm sao để kiểm tra thông tin vé tàu?</p>
                                <p>Trẻ em đi tàu hỏa có cần mua vé không?</p>
                                <p>Vé tàu Online có khác gì so với vé mua tại ga không?</p>
                                <p>Tôi cần làm gì nếu không nhận được mã vé sau khi thanh toán?</p>
                                <p>Cần chuẩn bị những gì để lên tàu khi mua vé online?</p>
                            </section>
                        </div>

                        <div class="chat-section">
                            <h2><i class="fas fa-comments"></i> Nhắn tin với Staff</h2>
                            <div class="chat-box" id="chatBox">
                                <div class="messages">
                                    <c:if test="${not empty chatMessages}">
                                        <c:forEach var="message" items="${chatMessages}">
                                            <div class="message-container ${message.senderType == 'Customer' ? 'sent' : 'received'}"
                                                data-message-id="${message.messageId}">
                                                <div class="message-bubble">
                                                    <p>${message.content}</p>
                                                    <small>
                                                        <fmt:formatDate value="${message.timestampAsDate}"
                                                            pattern="HH:mm" />
                                                    </small>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                    <c:if test="${empty chatMessages}">
                                        <p>Chưa có tin nhắn. Hãy bắt đầu cuộc trò chuyện!</p>
                                    </c:if>
                                </div>
                            </div>
                            <form id="chatForm" method="post">
                                <div class="chat-input">
                                    <textarea id="chatInput" name="message" placeholder="Nhập tin nhắn của bạn..."
                                        rows="3" required></textarea>
                                    <button type="submit">Gửi</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
                <script>
                    let lastMessageId = 0;

                    $(document).ready(function () {
                        const messageContainers = $('#chatBox .messages .message-container');
                        if (messageContainers.length > 0) {
                            const initialIds = messageContainers.map(function () {
                                return parseInt($(this).data('message-id'));
                            }).get();
                            lastMessageId = Math.max(...initialIds, 0);
                            fetchMessages();
                        }
                        const chatBox = $('#chatBox .messages');
                        chatBox.scrollTop(chatBox[0].scrollHeight);

                        $('#chatForm').on('submit', function (event) {
                            event.preventDefault();
                            const message = $('#chatInput').val().trim();
                            if (!message) return;

                            $.post('${pageContext.request.contextPath}/customer-support', { message: message }, function () {
                                $('#chatInput').val('');
                                fetchMessages();
                            }).fail(function (xhr) {
                                console.error("Lỗi khi gửi tin nhắn:", xhr.status, xhr.responseText);
                                $('#chatBox .messages').append('<p style="color: red;">Lỗi khi gửi tin nhắn. Vui lòng thử lại.</p>');
                            });
                        });
                    });
                    function fetchMessages() {
                        $.ajax({
                            url: '${pageContext.request.contextPath}/customer-messages',
                            method: 'GET',
                            data: { lastMessageId: lastMessageId },
                            dataType: 'json',
                            success: function (messages) {
                                const chatBox = $('#chatBox .messages');
                                if (!chatBox.length) return;

                                if (!messages || messages.length === 0) {
                                    console.log("No new messages");
                                    return;
                                }

                                messages.forEach(function (message) {
                                    if (!message || !message.messageId || !message.content) {
                                        return;
                                    }

                                    if (message.messageId > lastMessageId) {
                                        const isCustomer = message.senderType === 'Customer';
                                        const containerClass = isCustomer ? 'sent' : 'received';
                                        const timestamp = message.timestamp
                                            ? new Date(message.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true })
                                            : new Date(message.timestampAsDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });

                                        const safeContent = message.content;

                                        if (!timestamp || !safeContent) {
                                            console.log("Missing timestamp or content:", message);
                                            return;
                                        }

                                        const $messageContainer = $('<div>').addClass('message-container').addClass(containerClass).attr('data-message-id', message.messageId);
                                        const $messageBubble = $('<div>').addClass('message-bubble');
                                        $messageBubble.append($('<p>').text(safeContent));
                                        $messageBubble.append($('<small>').text(timestamp));
                                        $messageContainer.append($messageBubble);

                                        chatBox.append($messageContainer);
                                        lastMessageId = Math.max(lastMessageId, message.messageId);
                                    }
                                });

                                chatBox.scrollTop(chatBox[0].scrollHeight);
                            },
                            error: function (xhr) {
                                console.error("Lỗi khi lấy tin nhắn:", xhr.status, xhr.responseText);
                                $('#chatBox .messages').append('<p style="color: red;">Lỗi khi tải tin nhắn. Vui lòng thử lại sau.</p>');
                            }
                        });
                    }

                    // Tự động gọi fetchMessages mỗi 3 giây
                    setInterval(fetchMessages, 3000);
                </script>
                    <jsp:include page="../common/footer.jsp" />
            </body>
            </html>