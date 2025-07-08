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
                                <li><a href="#">Kiểm tra vào/ra</a></li>
                                <li><a href="${pageContext.request.contextPath}/staff-message">Hỗ trợ khách hàng</a>
                                </li>
                                <li><a href="#">Báo cáo</a></li>
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
                                                <td colspan="8" style="text-align: center;">Không có tin nhắn nào từ
                                                    khách hàng.</td>
                                            </tr>
                                        </c:if>
                                        <c:forEach var="summary" items="${chatSummaries}">
                                            <tr>
                                                <td>${summary.fullName}</td>
                                                <td>${summary.email}</td>
                                                <td>${summary.lastMessage}</td>
                                                <td>
                                                    <form action="${pageContext.request.contextPath}/staff-message"
                                                        method="post" style="display: inline;">
                                                        <input type="hidden" name="userId" value="${summary.userId}" />
                                                        <button type="submit" class="chat-button">Chat</button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
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
                                    <jsp:include page="/WEB-INF/jsp/staff/chat-messages.jsp">
                                        <jsp:param name="userId" value="${param.userId}" />
                                    </jsp:include>
                                    <form action="${pageContext.request.contextPath}/staff-message" method="post">
                                        <input type="hidden" name="userId" value="${param.userId}" />
                                        <div class="chat-input">
                                            <textarea name="message" placeholder="Nhập tin nhắn..." rows="3"
                                                required></textarea>
                                            <button type="submit">Gửi</button>
                                        </div>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </main>
                </div>
            </body>

            </html>