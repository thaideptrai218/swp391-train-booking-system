<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lỗi xử lý thanh toán - Vetaure</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; margin: 0; }
        .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); text-align: center; max-width: 500px; }
        .icon-error { font-size: 50px; color: #ffc107; margin-bottom: 20px; } /* Warning color for general error */
        h1 { color: #333; }
        p { color: #555; line-height: 1.6; }
        .error-details { margin-top: 15px; padding: 10px; background-color: #fff3cd; color: #856404; border: 1px solid #ffeeba; border-radius: 4px; }
        .booking-code { font-weight: bold; }
        .actions a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px; }
        .actions a:hover { background-color: #0056b3; }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="icon-error"><i class="fas fa-exclamation-triangle"></i></div>
        <h1>Lỗi xử lý thanh toán</h1>
        <p>Đã có lỗi không mong muốn xảy ra trong quá trình xử lý thanh toán của bạn.</p>
        
        <div class="error-details">
            <p>Chúng tôi rất tiếc về sự cố này. Vui lòng thử lại sau hoặc liên hệ bộ phận hỗ trợ khách hàng để được giúp đỡ.</p>
            <c:if test="${not empty param.bookingCode}">
                <p>Mã đơn hàng tham chiếu (nếu có): <span class="booking-code">${param.bookingCode}</span></p>
            </c:if>
            <c:if test="${not empty param.error}">
                <p>Chi tiết lỗi (kỹ thuật): ${param.error}</p>
            </c:if>
            <%-- For security, don't display detailed exception messages directly from isErrorPage=true context in production. 
                 The param.error is safer if passed explicitly by the servlet.
            <c:if test="${pageContext.exception != null}">
                <p style="font-size:0.8em; color: #777;">Debug: ${pageContext.exception.message}</p>
            </c:if> 
            --%>
        </div>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/">Về trang chủ</a>
            <a href="${pageContext.request.contextPath}/searchTrip">Tìm chuyến tàu khác</a>
        </div>
    </div>
</body>
</html>
