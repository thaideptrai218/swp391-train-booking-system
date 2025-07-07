<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <div class="messages" id="chatMessages">
            <c:if test="${empty chatMessages}">
                <p style="text-align: center; color: #666;">Không có tin nhắn nào trước đó.</p>
            </c:if>
            <c:forEach var="message" items="${chatMessages}">
                <div class="message-container ${message.senderType == 'Staff' ? 'sent' : 'received'}">
                    <div class="message-bubble">
                        <p>${message.content}</p>
                        <small>
                            <fmt:formatDate value="${message.timestampAsDate}" pattern="HH:mm" />
                        </small>
                    </div>
                </div>
            </c:forEach>
        </div>