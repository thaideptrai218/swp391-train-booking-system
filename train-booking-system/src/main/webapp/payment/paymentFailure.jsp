<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thất bại - Vetaure</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; margin: 0; }
        .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }
        .icon-failure { font-size: 50px; color: #dc3545; margin-bottom: 20px; }
        h1 { color: #333; }
        p { color: #555; line-height: 1.6; }
        .error-details { margin-top: 15px; padding: 10px; background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; border-radius: 4px; }
        .booking-code { font-weight: bold; }
        .actions a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px; }
        .actions a:hover { background-color: #0056b3; }
        .actions a.retry { background-color: #ffc107; color: #212529; }
        .actions a.retry:hover { background-color: #e0a800; }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="icon-failure"><i class="fas fa-times-circle"></i></div>
        <h1>Thanh toán không thành công</h1>
        <p>Rất tiếc, giao dịch thanh toán cho đơn hàng của bạn đã không thành công.</p>

        <c:set var="errorMessage" value="Đã có lỗi xảy ra trong quá trình xử lý thanh toán." />
        <c:if test="${param.errorCode == 'INVALID_SIGNATURE'}">
            <c:set var="errorMessage" value="Lỗi xác thực giao dịch. Vui lòng thử lại hoặc liên hệ hỗ trợ." />
        </c:if>
        <c:if test="${param.errorCode == 'INVALID_PARAMS'}">
            <c:set var="errorMessage" value="Thông số giao dịch không hợp lệ. Vui lòng thử lại." />
        </c:if>
        <c:if test="${not empty param.vnpayCode && param.vnpayCode != '00'}">
            <%-- You might want a mapping from vnpayCode to a user-friendly message here --%>
            <c:set var="errorMessage" value="Giao dịch bị từ chối bởi VNPAY (Mã lỗi: ${param.vnpayCode})." />
        </c:if>
        
        <div class="error-details">
            <p>${errorMessage}</p>
            <c:if test="${not empty param.bookingCode}">
                <p>Mã đơn hàng tham chiếu: <span class="booking-code">${param.bookingCode}</span></p>
            </c:if>
        </div>

        <p>Vui lòng thử lại hoặc chọn phương thức thanh toán khác. Nếu bạn cần hỗ trợ, xin liên hệ với chúng tôi.</p>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/">Về trang chủ</a>
            <%-- Link to retry payment might go back to the cart or a payment selection page --%>
            <%-- For simplicity, linking to search page. Ideally, it should repopulate the cart if possible. --%>
            <a href="${pageContext.request.contextPath}/searchTrip" class="retry">Thử lại với đơn hàng khác</a> 
        </div>
    </div>
</body>
</html>
