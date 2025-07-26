<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Kiểm tra hoàn vé</title>
    
    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-message.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <!-- Internal Styles -->
    <style>
        .chat-container {
            background-color: #fff;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
            font-size: 15px;
        }

        th,
        td {
            padding: 12px 10px;
            border: 1px solid #ddd;
            vertical-align: top;
        }

        th {
            background-color: #f0f4fa;
            text-align: center;
        }

        tr:nth-child(even) {
            background-color: #f9fcff;
        }

        td:first-child {
            font-weight: bold;
        }

        .message {
            padding: 8px;
            background-color: #ffe5e5;
            border: 1px solid #ff8888;
            border-radius: 6px;
            margin-bottom: 12px;
        }

        button {
            padding: 6px 12px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: bold;
            color: white;
        }

        button[name="action"][value="approve"] {
            background-color: #28a745;
        }

        button[name="action"][value="reject"] {
            background-color: #dc3545;
        }

        input[type="text"] {
            border: 1px solid #ccc;
            padding: 6px;
            border-radius: 4px;
            width: 100%;
        }

        .section-header {
            font-size: 18px;
            font-weight: bold;
            color: #d50000;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>
    <div class="dashboard-container">
        
        <!-- Sidebar -->
        <jsp:include page="sidebar.jsp" />

        <!-- Main Content -->
        <main class="main-content">
            <header class="header">
                <h1>Kiểm tra hoàn vé</h1>
            </header>

                            <table>
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Mã vé</th>
                                        <th>Hành khách</th>
                                        <th>Thông tin</th>
                                        <th>Chính sách</th>
                                        <th>Chi phí</th>
                                        <th>Thời gian yêu cầu</th>
                                        <th>Người đặt vé</th>
                                        <th>Ghi chú STK</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${refundRequests}" varStatus="i">
                                        <tr>
                                            <td colspan="10" style="background: #eef5ff; font-weight: bold;">
                                                ${req.startStation} - ${req.endStation} |
                                                ${req.scheduledDeparture}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>${i.index + 1}</td>
                                            <td>${req.ticketCode}</td>
                                            <td>
                                                <b>${req.passengerFullName}</b><br />
                                                ${req.passengerType}<br />
                                                Số giấy tờ: ${req.passengerIDCard}
                                            </td>
                                            <td>
                                                ${req.trainName}<br />
                                                Toa: ${req.coachName}<br />
                                                Ghế số: ${req.seatNumber}<br />
                                                ${req.seatType}
                                            </td>
                                            <td style="text-align: center;">
                                                ${req.refundPolicy}
                                            </td>
                                            <td style="text-align: left;">
                                                Tiền vé:
                                                <fmt:formatNumber value="${req.originalPrice}" groupingUsed="true" />
                                                đ<br />
                                                Phí hoàn:
                                                <fmt:formatNumber value="${req.refundFee}" groupingUsed="true" />
                                                đ<br />
                                                Hoàn lại:
                                                <fmt:formatNumber value="${req.refundAmount}" groupingUsed="true" /> đ
                                            </td>
                                            <td style="text-align: center;">
                                                ${req.requestedAt}
                                            </td>
                                            <td style="text-align: left;">
                                                <b>${req.userFullName}</b><br />
                                                Email: ${req.email}<br />
                                                SĐT: ${req.phoneNumber}<br />
                                                CMND: ${req.userIDCard}
                                            </td>
                                            <td style="text-align: left;">
                                                <c:choose>
                                                    <c:when test="${not empty req.noteSTK}">
                                                        ${req.noteSTK}
                                                    </c:when>
                                                    <c:otherwise>
                                                        Không có ghi chú
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <form method="post" action="refundProcessing" enctype="multipart/form-data">
                                                    <input type="file" name="imageFile" accept="image/*" required />
                                                    <input type="hidden" name="ticketInfo"
                                                        value="${req.ticketID}|${req.policyID}|${req.originalPrice}|${req.refundFee}|${req.refundAmount}|${req.requestedAt}|${req.userID}|${sessionScope.loggedInUser.userID}|${req.bookingID}|${req.noteSTK}" />
                                                    <input type="hidden" name="email" value="${req.email}" />
                                                    <div style="margin-top: 6px;">
                                                        <button type="submit" name="action" value="approve">Chấp
                                                            nhận</button>
                                                        <button type="submit" name="action" value="reject">Từ
                                                            chối</button>
                                                    </div>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </main>
                </div>
            </body>

                <c:if test="${not empty message}">
                    <div class="message">${message}</div>
                </c:if>

                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Mã vé</th>
                            <th>Hành khách</th>
                            <th>Thông tin</th>
                            <th>Chính sách</th>
                            <th>Chi phí</th>
                            <th>Thời gian yêu cầu</th>
                            <th>Người đặt vé</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="req" items="${refundRequests}" varStatus="i">
                            <tr>
                                <td colspan="9" style="background: #eef5ff; font-weight: bold;">
                                    ${req.startStation} - ${req.endStation} |
                                    ${req.scheduledDeparture}
                                </td>
                            </tr>
                            <tr>
                                <td>${i.index + 1}</td>
                                <td>${req.ticketCode}</td>
                                <td>
                                    <b>${req.passengerFullName}</b><br />
                                    ${req.passengerType}<br />
                                    Số giấy tờ: ${req.passengerIDCard}
                                </td>
                                <td>
                                    ${req.trainName}<br />
                                    Toa: ${req.coachName}<br />
                                    Ghế số: ${req.seatNumber}<br />
                                    ${req.seatType}
                                </td>
                                <td style="text-align: center;">
                                    ${req.refundPolicy}
                                </td>
                                <td style="text-align: left;">
                                    Tiền vé:
                                    <fmt:formatNumber value="${req.originalPrice}" groupingUsed="true" />
                                    đ<br />
                                    Phí hoàn:
                                    <fmt:formatNumber value="${req.refundFee}" groupingUsed="true" />
                                    đ<br />
                                    Hoàn lại:
                                    <fmt:formatNumber value="${req.refundAmount}" groupingUsed="true" /> đ
                                </td>
                                <td style="text-align: center;">
                                    ${req.requestedAt}
                                </td>
                                <td style="text-align: left;">
                                    <b>${req.userFullName}</b><br />
                                    Email: ${req.email}<br />
                                    SĐT: ${req.phoneNumber}<br />
                                    CMND: ${req.userIDCard}
                                </td>
                                <td>
                                    <form method="post" action="refundProcessing">
                                        <input type="hidden" name="ticketInfo" value="${req.ticketID}|${req.policyID}|${req.originalPrice}|${req.refundFee}|${req.refundAmount}|${req.requestedAt}|${req.userID}|${sessionScope.loggedInUser.userID}|${req.bookingID}" />
                                        <input type="hidden" name="email" value="${req.email}" />
                                        <div style="margin-top: 6px;">
                                            <button type="submit" name="action" value="approve">Chấp
                                                nhận</button>
                                            <button type="submit" name="action" value="reject">Từ
                                                chối</button>
                                        </div>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
</body>

</html>
