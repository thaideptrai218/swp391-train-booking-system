<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Góp Ý</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feedback.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
  	
  	<div class="feedback">
    <div class="feedback-container">
        <div class="feedback-child">
            <div class="nhng-ni-dung">Những nội dung chúng tôi rất mong nhận được từ bạn</div>
            <ul class="feedback-list">
                <li>Góp ý về giao diện và trải nghiệm đặt vé</li>
                <li>Phản ánh sự cố kỹ thuật, lỗi thanh toán hoặc sai thông tin vé</li>
                <li>Đề xuất tính năng mới hoặc dịch vụ bổ sung</li>
                <li>Cảm nhận về dịch vụ khách hàng</li>
                <li>Mọi ý kiến khác liên quan đến website</li>
            </ul>
        </div>
        <div class="feedback-item">
            <div class="gp-v">GÓP Ý VÀ PHẢN HỒI CỦA QUÝ KHÁCH</div>
            <div class="cm-n-qu">Cảm ơn Quý khách đã sử dụng dịch vụ đặt vé tàu trực tuyến Cảm ơn Quý khách đã sử dụng dịch vụ đặt vé tàu trực tuyến tại VEXERE.COM. Sự hài lòng của Quý khách là động lực để chúng tôi không ngừng hoàn thiện và nâng cao chất lượng dịch vụ.</div>
            <form id="feedbackForm">
                <div class="form-group">
                    <div class="h-v-tn-container">Họ và tên <span class="span">*</span></div>
                    <div class="feedback-inner"><input type="text" name="fullName" required></div>
                </div>
                <div class="form-group">
                    <div class="email-lin-h-container">Email liên hệ <span class="span">*</span></div>
                    <div class="rectangle-div"><input type="email" name="email" required></div>
                </div>
                <div class="form-group">
                    <div class="ni-dung-gp">Nội dung góp ý <span class="span">*</span></div>
                    <div class="feedback-child2"><textarea name="feedbackContent" required></textarea></div>
                </div>
                <div class="form-group">
                    <div class="thng-tin">THÔNG TIN</div>
                    <div class="info-preview-1-icon"></div>
                    <div class="tn-phiu-container">Tên phiếu <span class="span">*</span></div>
                    <div class="feedback-child8"><input type="text" name="ticketName" required></div>
                    <div class="loi-phiu-container">Loại phiếu <span class="span">*</span></div>
                    <div class="feedback-child9">
                        <select name="ticketType" required>
                            <option value="" disabled selected>Chọn loại phiếu</option>
                            <option value="Góp ý">Góp ý</option>
                            <option value="Phản ánh">Phản ánh</option>
                            <option value="Đề xuất">Đề xuất</option>
                        </select>
                    </div>
                    <div class="m-t">Mô tả</div>
                    <div class="feedback-child10"><textarea name="description" required></textarea></div>
                </div>
                <div class="feedback-child1">
                    <div class="gi">Gửi</div>
                </div>
            </form>
        </div>
    </div>
</div>
  	
  	
  	
  	
</body>
</html>
