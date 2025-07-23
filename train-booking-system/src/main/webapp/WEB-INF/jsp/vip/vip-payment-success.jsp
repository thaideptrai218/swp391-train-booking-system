<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Mua thẻ VIP thành công - VeTauRe</title>
        <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/vip-success.css"
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
            <!-- Success Header -->
            <div class="text-center mb-5">
                <div class="success-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <h1 class="display-4 fw-bold text-success mt-4">
                    Mua thẻ VIP thành công!
                </h1>
                <p class="lead text-muted">
                    Chúc mừng bạn đã trở thành thành viên VIP của VeTauRe
                </p>
            </div>

            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <!-- VIP Card Display -->
                    <div class="vip-card-display ${vipCardType.cardStyleClass}">
                        <div class="vip-card-header">
                            <div
                                class="d-flex align-items-center justify-content-between"
                            >
                                <div class="d-flex align-items-center">
                                    <span class="vip-icon-large"
                                        >${vipCardType.VIPIcon}</span
                                    >
                                    <div class="ms-3">
                                        <h3 class="mb-0 text-white">
                                            ${vipCardType.typeName}
                                        </h3>
                                        <p class="mb-0 text-white-50">
                                            Thành viên VIP
                                            ${vipCardType.durationDescription}
                                        </p>
                                    </div>
                                </div>
                                <div class="vip-status">
                                    <span class="badge bg-success"
                                        >HOẠT ĐỘNG</span
                                    >
                                </div>
                            </div>
                        </div>

                        <div class="vip-card-body">
                            <!-- VIP Card Information -->
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="info-section">
                                        <h6>
                                            <i class="fas fa-user me-2"></i
                                            >Thông tin thẻ
                                        </h6>
                                        <div class="info-item">
                                            <label>Chủ thẻ:</label>
                                            <span>${user.fullName}</span>
                                        </div>
                                        <div class="info-item">
                                            <label>Loại thẻ:</label>
                                            <span class="fw-bold"
                                                >${vipCardType.typeName}</span
                                            >
                                        </div>
                                        <div class="info-item">
                                            <label>Mức giảm giá:</label>
                                            <span class="text-warning fw-bold"
                                                >${vipCardType.formattedDiscount}</span
                                            >
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="info-section">
                                        <h6>
                                            <i class="fas fa-calendar me-2"></i
                                            >Thời hạn sử dụng
                                        </h6>
                                        <div class="info-item">
                                            <label>Ngày kích hoạt:</label>
                                            <span>
                                                <fmt:formatDate
                                                    value="${userVIPCard.toDatePurchaseDate()}"
                                                    pattern="dd/MM/yyyy HH:mm"
                                                />
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <label>Ngày hết hạn:</label>
                                            <span class="text-danger fw-bold">
                                                <fmt:formatDate
                                                    value="${userVIPCard.toDateExpiryDate()}"
                                                    pattern="dd/MM/yyyy HH:mm"
                                                />
                                            </span>
                                        </div>
                                        <div class="info-item">
                                            <label>Thời gian còn lại:</label>
                                            <span class="text-primary fw-bold"
                                                >${userVIPCard.remainingDays}
                                                ngày</span
                                            >
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- VIP Benefits -->
                            <div class="vip-benefits mt-4">
                                <h6>
                                    <i class="fas fa-star me-2"></i>Quyền lợi
                                    của bạn
                                </h6>
                                <p>${vipCardType.description}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Important Notes -->
                    <div class="important-notes mt-4">
                        <div class="alert alert-info">
                            <h6>
                                <i class="fas fa-info-circle me-2"></i>Thông tin
                                quan trọng
                            </h6>
                            <ul class="mb-0">
                                <li>
                                    Thẻ VIP đã được kích hoạt và có hiệu lực
                                    ngay lập tức
                                </li>
                                <li>
                                    Mức giảm giá sẽ được áp dụng tự động khi bạn
                                    đặt vé
                                </li>
                                <li>
                                    Bạn sẽ nhận được email xác nhận trong vòng 5
                                    phút
                                </li>
                                <li>
                                    Thẻ VIP chỉ có thể sử dụng bởi chủ thẻ đã
                                    đăng ký
                                </li>
                                <li>
                                    Vui lòng liên hệ hỗ trợ khách hàng nếu có
                                    thắc mắc
                                </li>
                            </ul>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="action-buttons mt-5">
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <a
                                    href="${pageContext.request.contextPath}/landing"
                                    class="btn btn-outline-primary btn-lg w-100"
                                >
                                    <i class="fas fa-home me-2"></i>Về trang chủ
                                </a>
                            </div>
                            <div class="col-md-4 mb-3">
                                <a
                                    href="${pageContext.request.contextPath}/trip/search"
                                    class="btn btn-success btn-lg w-100"
                                >
                                    <i class="fas fa-search me-2"></i>Đặt vé
                                    ngay
                                </a>
                            </div>
                            <div class="col-md-4 mb-3">
                                <a
                                    href="${pageContext.request.contextPath}/customerprofile"
                                    class="btn btn-outline-secondary btn-lg w-100"
                                >
                                    <i class="fas fa-user me-2"></i>Hồ sơ của
                                    tôi
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Social Share -->
                    <div class="social-share mt-4 text-center">
                        <h6>Chia sẻ với bạn bè</h6>
                        <div class="social-buttons">
                            <a
                                href="#"
                                class="btn btn-primary me-2"
                                onclick="shareOnFacebook()"
                            >
                                <i class="fab fa-facebook-f"></i> Facebook
                            </a>
                            <a
                                href="#"
                                class="btn btn-info me-2"
                                onclick="shareOnTwitter()"
                            >
                                <i class="fab fa-twitter"></i> Twitter
                            </a>
                            <a
                                href="#"
                                class="btn btn-success"
                                onclick="shareOnWhatsApp()"
                            >
                                <i class="fab fa-whatsapp"></i> WhatsApp
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Confetti animation on page load
            document.addEventListener("DOMContentLoaded", function () {
                // Simple celebration animation
                const successIcon = document.querySelector(".success-icon");
                successIcon.style.animation = "bounce 1.5s ease-in-out";

                // Auto-scroll to center
                window.scrollTo(0, 0);
            });

            // Social sharing functions
            function shareOnFacebook() {
                const url = encodeURIComponent(window.location.href);
                const text = encodeURIComponent(
                    "Tôi vừa trở thành thành viên VIP của VeTauRe! 🎉"
                );
                window.open(
                    `https://www.facebook.com/sharer/sharer.php?u=${url}&quote=${text}`,
                    "_blank"
                );
            }

            function shareOnTwitter() {
                const url = encodeURIComponent(window.location.href);
                const text = encodeURIComponent(
                    "Tôi vừa trở thành thành viên VIP của VeTauRe! 🎉 #VeTauRe #VIP"
                );
                window.open(
                    `https://twitter.com/intent/tweet?url=${url}&text=${text}`,
                    "_blank"
                );
            }

            function shareOnWhatsApp() {
                const text = encodeURIComponent(
                    "Tôi vừa trở thành thành viên VIP của VeTauRe! 🎉 Xem thêm tại: " +
                        window.location.href
                );
                window.open(`https://wa.me/?text=${text}`, "_blank");
            }

            // Add some celebration effects
            setTimeout(() => {
                document.body.style.background =
                    "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
                document.body.style.backgroundAttachment = "fixed";
            }, 1000);
        </script>
    </body>
</html>
