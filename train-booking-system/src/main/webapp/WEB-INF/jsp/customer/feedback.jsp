<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">

            </head>

            <body>
                    <jsp:include page="../common/header.jsp" />

                <div class="feedback">
                    <div class="feedback-content">
                        <div style="margin-bottom: 150px;">
                            <a href="${pageContext.request.contextPath}/landing" class="back-button">Quay lại trang
                                chủ</a>
                        </div>
                        <form id="feedbackForm" action="${pageContext.request.contextPath}/feedback" method="post"
                            class="feedback-form">
                            <div class="rectangle-parent">
                                <div class="group-child"></div>
                                <div class="gp-v-phn-hi-ca-qu-khc-parent">
                                    <b class="gp-v">GÓP Ý VÀ PHẢN HỒI CỦA QUÝ KHÁCH</b>
                                    <div class="cm-n-qu">
                                        Cảm ơn Quý khách đã sử dụng dịch vụ đặt vé tàu trực tuyến tại Vetaure.
                                        Sự hài lòng của Quý khách là động lực để chúng tôi không ngừng hoàn thiện và
                                        nâng cao chất lượng dịch vụ.
                                    </div>
                                    <div class="h-v-tn-container">
                                        <span>Họ và tên </span><span class="span">*</span>
                                    </div>
                                    <input type="text" name="fullName" class="group-item" required>

                                    <div class="email-lin-h-container">
                                        <span>Email liên hệ </span><span class="span">*</span>
                                    </div>
                                    <input type="email" name="email" class="group-inner" required>

                                    <div class="ni-dung-gp">Nội dung góp ý</div>
                                    <textarea name="feedbackContent" class="group-child1" required></textarea>

                                    <button type="submit" class="rectangle-div">
                                        <div class="gi">Gửi</div>
                                    </button>
                                    <div id="feedbackMessage" style="display: none;"></div>
                                </div>
                            </div>
                            <div class="rectangle-group">
                                <div class="group-child2"></div>
                                <div class="nhng-ni-dung-chng-ti-rt-m-parent">
                                    <div class="nhng-ni-dung">Những nội dung chúng tôi rất mong nhận được từ bạn</div>
                                    <div class="phn-nh-s">Phản ánh sự cố kỹ thuật, lỗi thanh toán hoặc sai thông tin vé
                                    </div>
                                    <div class="xut-tnh-nng">Đề xuất tính năng mới hoặc dịch vụ bổ sung</div>
                                    <div class="cm-nhn-v">Cảm nhận về dịch vụ khách hàng</div>
                                    <div class="mi-kin">Mọi ý kiến khác liên quan đến website</div>
                                    <div class="line-div"></div>
                                    <div class="group-child3"></div>
                                    <div class="group-child4"></div>
                                    <div class="group-child5"></div>
                                    <div class="group-child6"></div>
                                    <div class="thng-tin">THÔNG TIN</div>

                                    <div class="tn-phiu-container">
                                        <span>Tên phiếu </span><span class="span">*</span>
                                    </div>
                                    <input type="text" name="ticketName" class="group-child7" required>

                                    <div class="loi-phiu-container">
                                        <span>Loại phiếu </span><span class="span">*</span>
                                    </div>
                                    <select name="ticketType" class="group-child8" required>
                                        <option value="">Chọn loại phiếu</option>
                                        <option value="1">Góp ý</option>
                                        <option value="2">Phản ánh</option>
                                        <option value="3">Đề xuất</option>
                                    </select>

                                    <div class="m-t">
                                        <span>Mô tả </span><span class="span">*</span>
                                    </div>
                                    <textarea name="description" class="group-child9" required></textarea>
                                </div>
                            </div>
                        </form>

                        <!-- Phần hiển thị danh sách feedback -->
                        <div class="feedback-list-container">
                            <h1>Danh sách góp ý</h1>
                            <c:if test="${not empty feedbacks}">
                                <div class="feedback-list">
                                    <h3>Lịch sử góp ý của bạn</h3>
                                    <table class="table table-bordered">
                                        <thead>
                                            <tr>
                                                <th>Tên phiếu</th>
                                                <th>Loại phiếu</th>
                                                <th>Nội dung</th>
                                                <th>Mô tả</th>
                                                <th>Ngày gửi</th>
                                                <th>Trạng thái</th>
                                                <th>Phản hồi từ staff</th>
                                                <th>Ngày phản hồi</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="feedback" items="${feedbacks}">
                                                <tr>
                                                    <td>${feedback.ticketName}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${feedback.feedbackTypeId == 1}">Góp ý
                                                            </c:when>
                                                            <c:when test="${feedback.feedbackTypeId == 2}">Phản ánh
                                                            </c:when>
                                                            <c:when test="${feedback.feedbackTypeId == 3}">Đề xuất
                                                            </c:when>
                                                            <c:otherwise>Không xác định</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${feedback.feedbackContent}</td>
                                                    <td>${feedback.description}</td>
                                                    <td>
                                                        <fmt:formatDate value="${feedback.submittedAt}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                                    </td>
                                                    <td>${feedback.status}</td>
                                                    <td>${feedback.response != null ? feedback.response : 'Chưa có phản
                                                        hồi'}</td>
                                                    <td>
                                                        <fmt:formatDate value="${feedback.respondedAt}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        const feedbackMessage = document.getElementById('feedbackMessage');
                        <c:if test="${not empty feedbackSuccess and feedbackSuccess}">
                            feedbackMessage.style.display = 'block';
                            feedbackMessage.style.color = 'green';
                            feedbackMessage.textContent = 'Góp ý của bạn đã được gửi thành công!';
                            document.getElementById('feedbackForm').reset();
                        </c:if>
                    });
                </script>
            </body>
                    <jsp:include page="../common/footer.jsp" />

            </html>