<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thành công - Vetaure</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css"> <%-- Assuming a common CSS --%>
    <style>
        body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; margin: 0; }
        .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }
        .icon-success { font-size: 50px; color: #28a745; margin-bottom: 20px; }
        h1 { color: #333; }
        p { color: #555; line-height: 1.6; }
        .booking-code { font-weight: bold; color: #007bff; }
        .actions a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px; }
        .actions a:hover { background-color: #0056b3; }
        .status-message { font-style: italic; color: #17a2b8; }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="icon-success"><i class="fas fa-check-circle"></i></div>
        <h1>Thanh toán thành công!</h1>
        <p>Cảm ơn bạn đã đặt vé tàu qua Vetaure. Giao dịch của bạn đã được xử lý thành công.</p>
        
        <c:if test="${not empty param.bookingCode}">
            <p>Mã đặt vé của bạn là: <span class="booking-code">${param.bookingCode}</span></p>
        </c:if>
        
        <c:if test="${param.status == 'ALREADY_CONFIRMED'}">
            <p class="status-message">Trạng thái: Đơn hàng này đã được xác nhận thanh toán trước đó.</p>
        </c:if>

        <p>Một email xác nhận cùng với vé điện tử sẽ được gửi đến địa chỉ email của bạn trong vài phút tới.</p>
        <p>Vui lòng kiểm tra hộp thư đến (và cả mục thư rác/spam).</p>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/">Về trang chủ</a>
            <a href="${pageContext.request.contextPath}/checkBooking">Kiểm tra đặt vé</a>
        </div>
    </div>
</body>
</html>
