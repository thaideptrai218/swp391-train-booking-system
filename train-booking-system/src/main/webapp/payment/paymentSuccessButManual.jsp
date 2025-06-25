<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thành công - Cần hỗ trợ - Vetaure</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <style>
        body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; min-height: 100vh; background-color: #f4f4f4; margin: 0; }
        .container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 15px rgba(0,0,0,0.1); text-align: center; max-width: 550px; }
        .icon-warning { font-size: 50px; color: #ffc107; margin-bottom: 20px; } /* Warning icon */
        h1 { color: #333; }
        p { color: #555; line-height: 1.6; }
        .booking-code { font-weight: bold; color: #007bff; }
        .support-info { margin-top: 15px; padding: 10px; background-color: #e9ecef; border-left: 4px solid #007bff; text-align: left; }
        .actions a { display: inline-block; margin-top: 20px; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; margin-right: 10px; }
        .actions a:hover { background-color: #0056b3; }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
    <div class="container">
        <div class="icon-warning"><i class="fas fa-exclamation-circle"></i></div>
        <h1>Thanh toán đã được ghi nhận</h1>
        <p>Cảm ơn bạn, thanh toán cho đơn hàng của bạn đã được VNPAY xác nhận.</p>
        
        <c:if test="${not empty param.bookingCode}">
            <p>Mã đơn hàng của bạn là: <span class="booking-code">${param.bookingCode}</span></p>
        </c:if>

        <div class="support-info">
            <p><strong>Thông báo quan trọng:</strong></p>
            <p>Do một sự cố kỹ thuật tạm thời (phiên làm việc có thể đã hết hạn), vé của bạn chưa được tự động tạo ngay. Tuy nhiên, thanh toán của bạn đã thành công.</p>
            <p>Nhân viên hỗ trợ của chúng tôi sẽ sớm liên hệ với bạn qua email hoặc số điện thoại đã đăng ký để hoàn tất việc xuất vé.</p>
            <p>Nếu bạn không nhận được phản hồi trong vòng 1 giờ, vui lòng liên hệ với chúng tôi qua hotline [Số Hotline Hỗ Trợ] hoặc email [Địa Chỉ Email Hỗ Trợ] và cung cấp mã đơn hàng của bạn.</p>
        </div>
        
        <p>Chúng tôi thành thật xin lỗi vì sự bất tiện này và sẽ nhanh chóng xử lý trường hợp của bạn.</p>
        
        <div class="actions">
            <a href="${pageContext.request.contextPath}/">Về trang chủ</a>
            <a href="${pageContext.request.contextPath}/checkBooking">Kiểm tra đặt vé (có thể chưa cập nhật ngay)</a>
        </div>
    </div>
</body>
</html>
