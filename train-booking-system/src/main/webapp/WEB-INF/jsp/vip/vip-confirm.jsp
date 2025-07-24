<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Xác nhận mua thẻ VIP - VeTauRe</title>
        <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/vip-confirm.css"
        />
        <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            rel="stylesheet"
        />
        <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
        />
    </head>

    <body>
        <div class="container my-5">
            <!-- Page Header -->
            <div class="text-center mb-5">
                <h1 class="display-5 fw-bold text-primary">
                    <i class="fas fa-check-circle me-3"></i>Xác nhận mua thẻ VIP
                </h1>
                <p class="lead text-muted">
                    Vui lòng kiểm tra thông tin trước khi thanh toán
                </p>
            </div>

            <!-- Error Messages -->
            <c:if test="${param.error == 'database_error'}">
                <div class="alert alert-danger" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Có lỗi xảy ra. Vui lòng thử lại sau.
                </div>
            </c:if>
            <c:if test="${param.error == 'invalid_action'}">
                <div class="alert alert-danger" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Hành động không hợp lệ. Vui lòng thử lại.
                </div>
            </c:if>

            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <!-- Confirmation Card -->
                    <div class="confirmation-card">
                        <div
                            class="card-header ${selectedVIPCard.cardStyleClass}"
                        >
                            <div
                                class="d-flex align-items-center justify-content-center"
                            >
                                <span class="vip-icon-large"
                                    >${selectedVIPCard.VIPIcon}</span
                                >
                                <div class="ms-3">
                                    <h3 class="mb-0 text-white">
                                        ${selectedVIPCard.typeName}
                                    </h3>
                                    <p class="mb-0 text-white-50">
                                        Thẻ VIP
                                        ${selectedVIPCard.durationDescription}
                                    </p>
                                </div>
                            </div>
                        </div>

                        <div class="card-body" style="padding: 40px">
                            <!-- Customer Information -->
                            <div class="section-header">
                                <h5>
                                    <i class="fas fa-user me-2"></i>Thông tin
                                    khách hàng
                                </h5>
                            </div>
                            <div class="customer-info">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <label>Họ và tên:</label>
                                            <span>${user.fullName}</span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <label>Email:</label>
                                            <span>${user.email}</span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <label>Số điện thoại:</label>
                                            <span>${user.phoneNumber}</span>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="info-item">
                                            <label>Số CCCD/CMND:</label>
                                            <span
                                                >${not empty user.idCardNumber ?
                                                user.idCardNumber : 'Chưa cập
                                                nhật'}</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <hr class="my-4" />

                            <!-- VIP Card Details -->
                            <div class="section-header">
                                <h5>
                                    <i class="fas fa-crown me-2"></i>Chi tiết
                                    thẻ VIP
                                </h5>
                            </div>
                            <div class="vip-details">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="detail-item">
                                            <label>Loại thẻ:</label>
                                            <span class="fw-bold"
                                                >${selectedVIPCard.typeName}</span
                                            >
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="detail-item">
                                            <label>Thời hạn:</label>
                                            <span
                                                >${selectedVIPCard.durationDescription}</span
                                            >
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="detail-item">
                                            <label>Mức giảm giá:</label>
                                            <span class="text-success fw-bold"
                                                >${selectedVIPCard.formattedDiscount}</span
                                            >
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="detail-item">
                                            <label>Ngày hết hạn dự kiến:</label>
                                            <span>${expiryDate}</span>
                                        </div>
                                    </div>
                                </div>

                                <!-- VIP Benefits -->
                                <div class="vip-benefits mt-4">
                                    <h6>
                                        <i class="fas fa-star me-2"></i>Quyền
                                        lợi bao gồm:
                                    </h6>
                                    <p class="text-muted">
                                        ${selectedVIPCard.description}
                                    </p>
                                </div>
                            </div>

                            <hr class="my-4" />

                            <!-- Payment Summary -->
                            <div class="section-header">
                                <h5>
                                    <i class="fas fa-receipt me-2"></i>Thông tin
                                    thanh toán
                                </h5>
                            </div>
                            <div class="payment-summary">
                                <div
                                    class="d-flex justify-content-between align-items-center mb-3"
                                >
                                    <span
                                        >Giá thẻ
                                        ${selectedVIPCard.typeName}:</span
                                    >
                                    <span class="price-amount"
                                        >${selectedVIPCard.formattedPrice}</span
                                    >
                                </div>
                                <div
                                    class="d-flex justify-content-between align-items-center mb-3"
                                >
                                    <span>Phí xử lý:</span>
                                    <span class="text-success">Miễn phí</span>
                                </div>
                                <hr />
                                <div
                                    class="d-flex justify-content-between align-items-center"
                                >
                                    <span class="fw-bold fs-5"
                                        >Tổng thanh toán:</span
                                    >
                                    <span class="fw-bold fs-4 text-primary"
                                        >${selectedVIPCard.formattedPrice}</span
                                    >
                                </div>
                            </div>

                            <!-- Payment Method Notice -->
                            <div class="payment-notice mt-4">
                                <div class="alert alert-info">
                                    <i class="fas fa-info-circle me-2"></i>
                                    <strong>Thông tin thanh toán:</strong>
                                    <ul class="mb-0 mt-2">
                                        <li>
                                            Thanh toán qua cổng VNPay an toàn và
                                            bảo mật
                                        </li>
                                        <li>
                                            Hỗ trợ thanh toán bằng thẻ ATM,
                                            Visa, MasterCard
                                        </li>
                                        <li>
                                            Thẻ VIP sẽ được kích hoạt ngay sau
                                            khi thanh toán thành công
                                        </li>
                                        <li>
                                            Bạn sẽ nhận được email xác nhận sau
                                            khi hoàn tất
                                        </li>
                                    </ul>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="action-buttons mt-5">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <form
                                            method="post"
                                            action="${pageContext.request.contextPath}/vip/confirm"
                                        >
                                            <input
                                                type="hidden"
                                                name="action"
                                                value="cancel"
                                            />
                                            <button
                                                type="submit"
                                                class="btn btn-outline-secondary btn-lg w-100"
                                            >
                                                <i
                                                    class="fas fa-arrow-left me-2"
                                                ></i
                                                >Quay lại
                                            </button>
                                        </form>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <form
                                            method="post"
                                            action="${pageContext.request.contextPath}/vip/confirm"
                                        >
                                            <input
                                                type="hidden"
                                                name="action"
                                                value="confirm"
                                            />
                                            <button
                                                type="submit"
                                                class="btn btn-success btn-lg w-100 confirm-btn"
                                            >
                                                <i
                                                    class="fas fa-credit-card me-2"
                                                ></i
                                                >Xác nhận thanh toán
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Terms and Conditions -->
                            <div class="terms-section mt-4">
                                <div class="form-check">
                                    <input
                                        class="form-check-input"
                                        type="checkbox"
                                        id="acceptTerms"
                                        required
                                    />
                                    <label
                                        class="form-check-label text-muted"
                                        for="acceptTerms"
                                    >
                                        Tôi đồng ý với
                                        <a
                                            href="${pageContext.request.contextPath}/terms"
                                            target="_blank"
                                            >Điều khoản sử dụng</a
                                        >
                                        và
                                        <a
                                            href="${pageContext.request.contextPath}/privacy"
                                            target="_blank"
                                            >Chính sách bảo mật</a
                                        >
                                        của VeTauRe
                                    </label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Disable confirm button until terms are accepted
            document.addEventListener("DOMContentLoaded", function () {
                const acceptTerms = document.getElementById("acceptTerms");
                const confirmBtn = document.querySelector(".confirm-btn");

                acceptTerms.addEventListener("change", function () {
                    confirmBtn.disabled = !this.checked;
                    if (this.checked) {
                        confirmBtn.classList.remove("disabled");
                    } else {
                        confirmBtn.classList.add("disabled");
                    }
                });

                // Initially disable the button
                confirmBtn.disabled = true;
                confirmBtn.classList.add("disabled");
            });

            // Add confirmation dialog
            document
                .querySelector(".confirm-btn")
                .addEventListener("click", function (e) {
                    if (!document.getElementById("acceptTerms").checked) {
                        e.preventDefault();
                        alert(
                            "Vui lòng đồng ý với Điều khoản sử dụng để tiếp tục."
                        );
                        return;
                    }

                    if (
                        !confirm(
                            "Bạn có chắc chắn muốn mua thẻ VIP này? Sau khi thanh toán thành công, bạn không thể hủy hoặc hoàn tiền."
                        )
                    ) {
                        e.preventDefault();
                    }
                });
        </script>
    </body>
</html>
