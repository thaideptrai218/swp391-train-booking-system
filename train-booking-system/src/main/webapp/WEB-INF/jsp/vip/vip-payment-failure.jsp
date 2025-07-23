<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thất bại - VeTauRe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: linear-gradient(135deg, #ff6b6b 0%, #ffa500 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .failure-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            padding: 40px;
            text-align: center;
            max-width: 600px;
        }
        .failure-icon {
            font-size: 5rem;
            color: #dc3545;
            margin-bottom: 20px;
        }
    </style>
</head>

<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-6">
                <div class="failure-card">
                    <div class="failure-icon">
                        <i class="fas fa-times-circle"></i>
                    </div>
                    
                    <h1 class="display-5 fw-bold text-danger mb-4">
                        Thanh toán thất bại
                    </h1>
                    
                    <p class="lead text-muted mb-4">
                        Rất tiếc, giao dịch mua thẻ VIP của bạn không thành công.
                    </p>
                    
                    <div class="alert alert-warning" role="alert">
                        <h6><i class="fas fa-exclamation-triangle me-2"></i>Lý do có thể:</h6>
                        <ul class="list-unstyled mb-0">
                            <li>• Thông tin thẻ không chính xác</li>
                            <li>• Tài khoản không đủ số dư</li>
                            <li>• Giao dịch bị hủy bởi người dùng</li>
                            <li>• Lỗi kết nối với ngân hàng</li>
                        </ul>
                    </div>
                    
                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/vip/purchase" class="btn btn-primary btn-lg me-3">
                            <i class="fas fa-redo me-2"></i>Thử lại
                        </a>
                        <a href="${pageContext.request.contextPath}/landing" class="btn btn-outline-secondary btn-lg">
                            <i class="fas fa-home me-2"></i>Trang chủ
                        </a>
                    </div>
                    
                    <div class="mt-4 text-muted">
                        <small>
                            Nếu bạn cần hỗ trợ, vui lòng liên hệ: 
                            <a href="mailto:support@vetaure.com">support@vetaure.com</a>
                        </small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>

</html>