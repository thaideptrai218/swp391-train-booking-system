<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Hỗ trợ Khách hàng</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-message.css" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                <style>
                    .pagination {
                        margin-top: 20px;
                        text-align: center;
                    }

                    .pagination a {
                        padding: 8px 12px;
                        margin: 0 5px;
                        text-decoration: none;
                        color: #0082c4;
                        background-color: #f9f9f9;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                    }

                    .pagination a:hover {
                        background-color: #ddd;
                    }

                    .pagination .active {
                        background-color: #0082c4;
                        color: white;
                    }
                </style>
            </head>

            <body>
                <div class="dashboard-container">
                    <aside class="sidebar">
                        <a href="${pageContext.request.contextPath}/searchTrip" class="home-link">
                            <i class="fa-solid fa-house fa-xl home-icon"></i>
                        </a>
                        <h2>Bảng điều khiển nhân viên</h2>
                        <nav>
                            <ul>
                                <li><a href="${pageContext.request.contextPath}/staff/dashboard">Bảng điều khiển</a>
                                </li>
                                <li><a href="#">Quản lý đặt chỗ</a></li>
                                <li><a href="${pageContext.request.contextPath}/checkRefundTicket">Kiểm tra hoàn vé</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/staff-message">Hỗ trợ khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/checkConfirmRefundRequest?userID=${loggedInUser.userID}">Danh sách các
                                        vé đã hoàn</a></li>
                                <li><a href="${pageContext.request.contextPath}/staff/feedback">Góp ý của khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/customer-info">Thông tin khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                            </ul>
                        </nav>
                    </aside>
                    <main class="main-content">
                        <header class="header">
                            <h1>Hỗ trợ Khách hàng</h1>
                            <div class="user-info">
                                <span>Đăng nhập với tư cách: Người dùng nhân viên</span>
                            </div>
                        </header>
                        <div class="chat-container">
                            <div class="chat-list">
                                <h3>Danh sách tin nhắn</h3>
                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Họ và tên</th>
                                            <th>Email</th>
                                            <th>Tin nhắn mới nhất</th>
                                            <th>Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:if test="${empty chatSummaries}">
                                            <tr>
                                                <td colspan="4" style="text-align: center;">Không có tin nhắn nào từ
                                                    khách hàng.</td>
                                            </tr>
                                        </c:if>
                                        <c:forEach var="summary" items="${chatSummaries}" varStatus="loop">
                                            <tr>
                                                <td>${summary.fullName}</td>
                                                <td>${summary.email}</td>
                                                <td>${summary.lastMessage}</td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/staff-message"
                                                        method="get" style="display: inline;">
                                                        <input type="hidden" name="userId" value="${summary.userId}" />
                                                        <input type="hidden" name="page" value="${currentPage}" />
                                                        <button type="submit" class="chat-button">Chat</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <div class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a
                                            href="${pageContext.request.contextPath}/staff-message?page=${currentPage - 1}">Previous</a>
                                    </c:if>
                                    <c:forEach begin="1" end="${totalPages}" var="page">
                                        <c:choose>
                                            <c:when test="${page == currentPage}">
                                                <a href="${pageContext.request.contextPath}/staff-message?page=${page}"
                                                    class="active">${page}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a
                                                    href="${pageContext.request.contextPath}/staff-message?page=${page}">${page}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                    <c:if test="${currentPage < totalPages}">
                                        <a
                                            href="${pageContext.request.contextPath}/staff-message?page=${currentPage + 1}">Next</a>
                                    </c:if>
                                </div>
                            </div>
                            <c:if test="${not empty param.userId}">
                                <c:set var="selectedSummary" value="${chatSummaries[0]}" />
                                <c:forEach var="summary" items="${chatSummaries}">
                                    <c:if test="${summary.userId == param.userId}">
                                        <c:set var="selectedSummary" value="${summary}" />
                                    </c:if>
                                </c:forEach>
                                <div class="chat-box" id="chatBox">
                                    <h2>Chat với ${selectedSummary.fullName}</h2>
                                    <div class="messages" id="chatMessages">
                                        <jsp:include page="/WEB-INF/jsp/staff/chat-messages.jsp">
                                            <jsp:param name="userId" value="${param.userId}" />
                                        </jsp:include>
                                    </div>
                                    <form action="${pageContext.request.contextPath}/staff-message" method="post"
                                        id="chatForm">
                                        <input type="hidden" name="userId" value="${param.userId}" />
                                        <div class="chat-input">
                                            <textarea name="message" id="chatInput" placeholder="Nhập tin nhắn..."
                                                rows="3" required></textarea>
                                            <button type="submit">Gửi</button>
                                        </div>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </main>
                </div>
                <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
                <c:if test="${not empty param.userId}">
                    <script>
                        const userId = parseInt('${param.userId}');
                        const contextPath = '${pageContext.request.contextPath}';
                        let lastMessageId = 0;

                        $(document).ready(function () {
                            const messageContainers = $('#chatMessages .message-container');
                            if (messageContainers.length > 0) {
                                const initialIds = messageContainers.map(function () {
                                    return parseInt($(this).data('message-id'));
                                }).get();
                                lastMessageId = Math.max(...initialIds, 0);
                            }
                            console.log('Initial lastMessageId:', lastMessageId);
                            const chatMessages = $('#chatMessages');
                            chatMessages.scrollTop(chatMessages[0].scrollHeight);

                            $('#chatForm').on('submit', function (event) {
                                event.preventDefault();
                                const message = $('#chatInput').val().trim();
                                if (!message) return;

                                $.post('${pageContext.request.contextPath}/staff-message',
                                    { userId: userId, message: message, page: '${currentPage}' },
                                    function () {
                                        $('#chatInput').val('');
                                        fetchNewMessages();
                                    }).fail(function (xhr) {
                                        console.error('Lỗi khi gửi tin nhắn:', xhr.status, xhr.responseText);
                                        $('#chatMessages').append('<p style="color: red;">Lỗi khi gửi tin nhắn. Vui lòng thử lại.</p>');
                                    });
                            });

                            fetchNewMessages(); // Gọi lần đầu
                            setInterval(fetchNewMessages, 3000); // Polling mỗi 3 giây
                        });

                        function fetchNewMessages() {
                            $.ajax({
                                url: contextPath + '/fetch-messages?userId=' + userId + '&lastMessageId=' + lastMessageId + '&t=' + new Date().getTime(),
                                method: 'GET',
                                dataType: 'json',
                                success: function (messages) {
                                    console.log('Fetched messages:', messages);
                                    const chatMessages = $('#chatMessages');
                                    if (!chatMessages.length) {
                                        console.warn('Chat messages container not found');
                                        return;
                                    }

                                    if (!messages || messages.length === 0) {
                                        console.log('No new messages');
                                        return;
                                    }

                                    messages.forEach(function (message) {
                                        if (!message || !message.messageId || !message.content) {
                                            console.warn('Invalid message data:', message);
                                            return;
                                        }

                                        const existingIds = new Set(chatMessages.find('[data-message-id]').map(function () {
                                            return parseInt($(this).data('message-id'));
                                        }).get());

                                        if (!existingIds.has(message.messageId)) {
                                            const isStaff = message.senderType === 'Staff';
                                            const containerClass = isStaff ? 'sent' : 'received';
                                            let timestamp = 'Unknown time';
                                            try {
                                                if (message.timestamp) {
                                                    timestamp = new Date(message.timestamp).toLocaleTimeString([], {
                                                        hour: '2-digit',
                                                        minute: '2-digit',
                                                        hour12: true
                                                    });
                                                } else if (message.timestampAsDate) {
                                                    timestamp = new Date(message.timestampAsDate).toLocaleTimeString([], {
                                                        hour: '2-digit',
                                                        minute: '2-digit',
                                                        hour12: true
                                                    });
                                                }
                                                if (timestamp === 'Invalid Date') {
                                                    console.warn('Invalid timestamp:', message.timestamp || message.timestampAsDate);
                                                    timestamp = 'Unknown time';
                                                }
                                            } catch (error) {
                                                console.error('Error parsing timestamp:', error, message);
                                                timestamp = 'Unknown time';
                                            }

                                            const safeContent = message.content;
                                            const $messageContainer = $('<div>').addClass('message-container').addClass(containerClass).attr('data-message-id', message.messageId);
                                            const $messageBubble = $('<div>').addClass('message-bubble');
                                            $messageBubble.append($('<p>').text(safeContent));
                                            $messageBubble.append($('<small>').text(timestamp));
                                            $messageContainer.append($messageBubble);

                                            chatMessages.append($messageContainer);
                                            console.log('Appended message:', message.messageId);
                                            lastMessageId = Math.max(lastMessageId, message.messageId);
                                        }
                                    });

                                    chatMessages.scrollTop(chatMessages[0].scrollHeight);
                                    console.log('Updated lastMessageId:', lastMessageId);
                                },
                                error: function (xhr) {
                                    console.error('Fetch error:', xhr.status, xhr.responseText);
                                    $('#chatMessages').append('<p style="color: red;">Lỗi khi tải tin nhắn. Vui lòng thử lại sau.</p>');
                                }
                            });
                        }
                    </script>
                </c:if>
            </body>

            </html>