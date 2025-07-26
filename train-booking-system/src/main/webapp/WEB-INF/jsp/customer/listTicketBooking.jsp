<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <title>Lịch sử vé đã đặt</title>
                <link rel="stylesheet" type="text/css"
                    href="${pageContext.request.contextPath}/css/check-booking.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            </head>

            <body>
                
                    <jsp:include page="../common/header.jsp" />

                <main class="main-content">
                    <section class="check-info">
                        <div class="booking-summary">
                            <c:if test="${not empty errorMessage}">
                                <p style="color: red">${errorMessage}</p>
                            </c:if>

                            <c:if test="${not empty infoPassenger}">
                                <h3 style="color: red">Thông tin vé:</h3>



                                <table style="width: 100%; border-collapse: collapse; border: 1px solid #ccc;">
                                    <thead>
                                        <tr style="background-color: #e8f0fe; text-align: center;">
                                            <th>#</th>
                                            <th>Họ tên</th>
                                            <th>CMND/CCCD</th>
                                            <th>Loại hành khách</th>
                                            <th>Tàu & Ghế</th>
                                            <th>Tuyến đi</th>
                                            <th>Thời gian</th>
                                            <th>Mã vé</th>
                                            <th>Giá</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="info" items="${infoPassenger}" varStatus="i">
                                            <tr style="background-color: #f8fbfd;">
                                                <td style="text-align: center;">${fromIndex + i.index + 1}</td>
                                                <td>${info.passengerFullName}</td>
                                                <td>${info.passengerIDCard}</td>
                                                <td>${info.passengerType}</td>
                                                <td style="text-align: start;">
                                                    ${info.trainName}<br />
                                                    Toa: ${info.coachName} – Ghế: ${info.seatNumber}<br />
                                                    ${info.seatTypeName}
                                                </td>
                                                <td>${info.startStationName} - ${info.endStationName}</td>
                                                <td style="text-align: start;">Ngày đi:
                                                    ${info.scheduledDepartureTime.subSequence(0, 10)}<br />
                                                    Giờ khởi hành: ${info.scheduledDepartureTime.subSequence(11, 16)}
                                                </td>
                                                <td>${info.ticketCode}</td>
                                                <td style="text-align: right;">
                                                    <fmt:formatNumber value="${info.price}" type="number"
                                                        groupingUsed="true" />đ
                                                </td>
                                                <td style="text-align: center;">
                                                    <c:choose>
                                                        <c:when test="${info.ticketStatus == 'Valid'}">
                                                            <span style="color: #28a745; font-weight: bold;">Hợp
                                                                lệ</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Used'}">
                                                            <span style="color: #007bff; font-weight: bold;">Đã sử
                                                                dụng</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Processing'}">
                                                            <span style="color: #fd7e14; font-weight: bold;">Đang xử
                                                                lý</span>
                                                        </c:when>
                                                        <c:when
                                                            test="${info.ticketStatus == 'Refunded' || info.ticketStatus == 'RejectedRefund'}">
                                                            <span style="color: #dc3545; font-weight: bold;">Đã
                                                                hủy</span>
                                                        </c:when>
                                                        <c:when test="${info.ticketStatus == 'Expired'}">
                                                            <span style="color: #6c757d; font-weight: bold;">Đã hết
                                                                hạn</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="color: #343a40; font-weight: bold;">Không xác
                                                                định</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>


                                <div style="text-align: center; margin-top: 20px;">
                                    <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                        <c:choose>
                                            <c:when test="${pageNum == currentPage}">
                                                <span
                                                    style="padding: 5px 10px; margin: 2px; font-weight: bold; background-color: #007bff; color: white; border-radius: 5px;">
                                                    ${pageNum}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="listTicketBooking?page=${pageNum}"
                                                    style="padding: 5px 10px; margin: 2px; text-decoration: none; background-color: #e8f0fe; color: #007bff; border-radius: 5px;">
                                                    ${pageNum}
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>


                            </c:if>
                        </div>
                    </section>
                </main>

                <script src="${pageContext.request.contextPath}/js/check-ticket/check-ticket.js"></script>


                    <jsp:include page="../common/footer.jsp" />

            </body>

            </html>