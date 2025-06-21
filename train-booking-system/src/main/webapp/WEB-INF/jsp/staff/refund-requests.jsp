<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>

        <head>
            <title>Yêu cầu hoàn vé</title>
            <style>
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 10px;
                }

                th,
                td {
                    padding: 8px;
                    border: 1px solid #ccc;
                }

                form {
                    display: inline;
                }
            </style>
        </head>

        <body>
            <h2>Danh sách yêu cầu hoàn vé</h2>

            <c:if test="${not empty message}">
                <div class="message" style="color: red; font-weight: bold;">
                    ${message}
                </div>
            </c:if>
            <table>
                <thead>
                    <tr>
                        <th>Mã vé</th>
                        <th>Hành khách</th>
                        <th>Tàu</th>
                        <th>Ghế</th>
                        <th>Khởi hành</th>
                        <th>Chính sách</th>
                        <th>Hoàn lại</th>
                        <th>Thời gian gửi</th>
                        <th>Người đặt</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="req" items="${refundRequests}">
                        <tr>
                            <td>${req.ticketCode}</td>
                            <td>${req.passengerFullName} - ${req.passengerIDCard}</td>
                            <td>${req.trainName}</td>
                            <td>${req.coachName} - ${req.seatNumber}</td>
                            <td>${req.scheduledDeparture}</td>
                            <td>${req.refundPolicy}</td>
                            <td>${req.refundAmount} VND</td>
                            <td>${req.requestedAt}</td>
                            <td>${req.userFullName} (${req.phoneNumber})</td>
                            <td>
                                <!-- chỉnh sửa ở chuyển tiền, sau đó xóa dữ liệu trong bảng mới là TempRefundRequests, lưu vào bảng Refunds -->
                                <form method="post">
                                    <input type="hidden" name="refundId" value="${req.refundID}" />
                                    <button type="submit" name="action" value="approve">Chấp nhận</button>
                                    <button type="submit" name="action" value="reject">Từ chối</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

        </body>

        </html>