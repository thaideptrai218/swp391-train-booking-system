<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mua Thẻ VIP - VeTauRe</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vip-purchase.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>
    <!-- Include header if you have one -->
    <%-- <jsp:include page="../common/header.jsp" /> --%>
    
    <div class="container my-5">
        <!-- Page Header -->
        <div class="text-center mb-5">
            <h1 class="display-4 fw-bold text-primary">
                <i class="fas fa-crown me-3"></i>Thẻ VIP VeTauRe
            </h1>
            <p class="lead text-muted">Trải nghiệm dịch vụ tàu hỏa cao cấp với nhiều ưu đãi đặc biệt</p>
        </div>

        <!-- Error Messages -->
        <c:if test="${param.error == 'invalid_selection'}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Lựa chọn thẻ VIP không hợp lệ. Vui lòng thử lại.
            </div>
        </c:if>
        <c:if test="${param.error == 'invalid_card'}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Thẻ VIP không tồn tại. Vui lòng chọn thẻ khác.
            </div>
        </c:if>
        <c:if test="${param.error == 'database_error'}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Có lỗi xảy ra. Vui lòng thử lại sau.
            </div>
        </c:if>
                <c:if test="${param.error == 'already_has_vip'}">
            <div class="alert alert-danger" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>
                Ban da dang ky VIP roi!.
            </div>
        </c:if>

        <!-- VIP Cards Container -->
        <div class="vip-cards-container">
            <!-- Bronze Cards -->
            <c:if test="${not empty groupedVIPCards['Thẻ Đồng']}">
                <div class="vip-card vip-card-bronze">
                    <div class="vip-card-content">
                        <!-- VIP Icon -->
                        <div class="vip-icon">
                            <span class="icon-emoji">🥉</span>
                            <div class="card-tier">BRONZE</div>
                        </div>
                        
                        <!-- Type Name -->
                        <div class="vip-type-name">
                            <h3>Thẻ Đồng</h3>
                        </div>
                        
                        <!-- Duration Options -->
                        <div class="duration-options">
                            <c:forEach var="card" items="${groupedVIPCards['Thẻ Đồng']}">
                                <div class="duration-option ${card.durationMonths == 3 ? 'option-3month' : 'option-12month'}">
                                    <div class="price-discount">
                                        <span class="price">${card.formattedPrice}</span>
                                        <span class="discount">Giảm ${card.formattedDiscount}</span>
                                        <span class="duration">${card.durationButtonText}</span>
                                    </div>
                                    
                                    <!-- Buy VIP Button -->
                                    <div class="buy-button">
                                        <c:choose>
                                            <c:when test="${isLoggedIn}">
                                                <form method="post" action="${pageContext.request.contextPath}/vip/purchase" class="d-inline">
                                                    <input type="hidden" name="vipCardTypeId" value="${card.vipCardTypeID}">
                                                    <button type="submit" class="btn btn-vip btn-bronze">
                                                        <i class="fas fa-shopping-cart me-2"></i>Mua VIP
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/vip/purchase%3Fselected=${card.vipCardTypeID}" 
                                                   class="btn btn-vip btn-bronze">
                                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập để mua
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Description -->
                        <div class="vip-description">
                            <c:if test="${not empty groupedVIPCards['Thẻ Đồng'][0].description}">
                                <p>${groupedVIPCards['Thẻ Đồng'][0].description}</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Silver Cards -->
            <c:if test="${not empty groupedVIPCards['Thẻ Bạc']}">
                <div class="vip-card vip-card-silver">
                    <div class="vip-card-content">
                        <!-- VIP Icon -->
                        <div class="vip-icon">
                            <span class="icon-emoji">🥈</span>
                            <div class="card-tier">SILVER</div>
                        </div>
                        
                        <!-- Type Name -->
                        <div class="vip-type-name">
                            <h3>Thẻ Bạc</h3>
                        </div>
                        
                        <!-- Duration Options -->
                        <div class="duration-options">
                            <c:forEach var="card" items="${groupedVIPCards['Thẻ Bạc']}">
                                <div class="duration-option ${card.durationMonths == 3 ? 'option-3month' : 'option-12month'}">
                                    <div class="price-discount">
                                        <span class="price">${card.formattedPrice}</span>
                                        <span class="discount">Giảm ${card.formattedDiscount}</span>
                                        <span class="duration">${card.durationButtonText}</span>
                                    </div>
                                    
                                    <!-- Buy VIP Button -->
                                    <div class="buy-button">
                                        <c:choose>
                                            <c:when test="${isLoggedIn}">
                                                <form method="post" action="${pageContext.request.contextPath}/vip/purchase" class="d-inline">
                                                    <input type="hidden" name="vipCardTypeId" value="${card.vipCardTypeID}">
                                                    <button type="submit" class="btn btn-vip btn-silver">
                                                        <i class="fas fa-shopping-cart me-2"></i>Mua VIP
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/vip/purchase%3Fselected=${card.vipCardTypeID}" 
                                                   class="btn btn-vip btn-silver">
                                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập để mua
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Description -->
                        <div class="vip-description">
                            <c:if test="${not empty groupedVIPCards['Thẻ Bạc'][0].description}">
                                <p>${groupedVIPCards['Thẻ Bạc'][0].description}</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Gold Cards -->
            <c:if test="${not empty groupedVIPCards['Thẻ Vàng']}">
                <div class="vip-card vip-card-gold">
                    <div class="vip-card-content">
                        <!-- VIP Icon -->
                        <div class="vip-icon">
                            <span class="icon-emoji">🥇</span>
                            <div class="card-tier">GOLD</div>
                            <div class="popular-badge">PHỔ BIẾN</div>
                        </div>
                        
                        <!-- Type Name -->
                        <div class="vip-type-name">
                            <h3>Thẻ Vàng</h3>
                        </div>
                        
                        <!-- Duration Options -->
                        <div class="duration-options">
                            <c:forEach var="card" items="${groupedVIPCards['Thẻ Vàng']}">
                                <div class="duration-option ${card.durationMonths == 3 ? 'option-3month' : 'option-12month'}">
                                    <div class="price-discount">
                                        <span class="price">${card.formattedPrice}</span>
                                        <span class="discount">Giảm ${card.formattedDiscount}</span>
                                        <span class="duration">${card.durationButtonText}</span>
                                    </div>
                                    
                                    <!-- Buy VIP Button -->
                                    <div class="buy-button">
                                        <c:choose>
                                            <c:when test="${isLoggedIn}">
                                                <form method="post" action="${pageContext.request.contextPath}/vip/purchase" class="d-inline">
                                                    <input type="hidden" name="vipCardTypeId" value="${card.vipCardTypeID}">
                                                    <button type="submit" class="btn btn-vip btn-gold">
                                                        <i class="fas fa-shopping-cart me-2"></i>Mua VIP
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/vip/purchase%3Fselected=${card.vipCardTypeID}" 
                                                   class="btn btn-vip btn-gold">
                                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập để mua
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Description -->
                        <div class="vip-description">
                            <c:if test="${not empty groupedVIPCards['Thẻ Vàng'][0].description}">
                                <p>${groupedVIPCards['Thẻ Vàng'][0].description}</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Diamond Cards -->
            <c:if test="${not empty groupedVIPCards['Thẻ Kim Cương']}">
                <div class="vip-card vip-card-diamond">
                    <div class="vip-card-content">
                        <!-- VIP Icon -->
                        <div class="vip-icon">
                            <span class="icon-emoji">💎</span>
                            <div class="card-tier">DIAMOND</div>
                            <div class="premium-badge">CAO CẤP NHẤT</div>
                        </div>
                        
                        <!-- Type Name -->
                        <div class="vip-type-name">
                            <h3>Thẻ Kim Cương</h3>
                        </div>
                        
                        <!-- Duration Options -->
                        <div class="duration-options">
                            <c:forEach var="card" items="${groupedVIPCards['Thẻ Kim Cương']}">
                                <div class="duration-option ${card.durationMonths == 3 ? 'option-3month' : 'option-12month'}">
                                    <div class="price-discount">
                                        <span class="price">${card.formattedPrice}</span>
                                        <span class="discount">Giảm ${card.formattedDiscount}</span>
                                        <span class="duration">${card.durationButtonText}</span>
                                    </div>
                                    
                                    <!-- Buy VIP Button -->
                                    <div class="buy-button">
                                        <c:choose>
                                            <c:when test="${isLoggedIn}">
                                                <form method="post" action="${pageContext.request.contextPath}/vip/purchase" class="d-inline">
                                                    <input type="hidden" name="vipCardTypeId" value="${card.vipCardTypeID}">
                                                    <button type="submit" class="btn btn-vip btn-diamond">
                                                        <i class="fas fa-shopping-cart me-2"></i>Mua VIP
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/login?redirect=${pageContext.request.contextPath}/vip/purchase%3Fselected=${card.vipCardTypeID}" 
                                                   class="btn btn-vip btn-diamond">
                                                    <i class="fas fa-sign-in-alt me-2"></i>Đăng nhập để mua
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <!-- Description -->
                        <div class="vip-description">
                            <c:if test="${not empty groupedVIPCards['Thẻ Kim Cương'][0].description}">
                                <p>${groupedVIPCards['Thẻ Kim Cương'][0].description}</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>

        <!-- Benefits Section -->
        <div class="benefits-section mt-5">
            <div class="text-center mb-4">
                <h2 class="fw-bold">Quyền lợi thành viên VIP</h2>
                <p class="text-muted">Tận hưởng những đặc quyền độc quyền khi trở thành thành viên VIP</p>
            </div>
            
            <div class="row">
                <div class="col-md-3 text-center mb-4">
                    <div class="benefit-item">
                        <i class="fas fa-percent fa-3x text-primary mb-3"></i>
                        <h5>Giảm giá vé tàu</h5>
                        <p class="text-muted">Từ 5% đến 20% tùy loại thẻ</p>
                    </div>
                </div>
                <div class="col-md-3 text-center mb-4">
                    <div class="benefit-item">
                        <i class="fas fa-clock fa-3x text-success mb-3"></i>
                        <h5>Ưu tiên đặt chỗ</h5>
                        <p class="text-muted">Đặt vé trước 24-72 giờ</p>
                    </div>
                </div>
                <div class="col-md-3 text-center mb-4">
                    <div class="benefit-item">
                        <i class="fas fa-undo fa-3x text-warning mb-3"></i>
                        <h5>Miễn phí hủy vé</h5>
                        <p class="text-muted">Hủy vé linh hoạt theo gói</p>
                    </div>
                </div>
                <div class="col-md-3 text-center mb-4">
                    <div class="benefit-item">
                        <i class="fas fa-headset fa-3x text-info mb-3"></i>
                        <h5>Hỗ trợ 24/7</h5>
                        <p class="text-muted">Dành cho thẻ Kim Cương</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Back Button -->
        <div class="text-center mt-5">
            <a href="${pageContext.request.contextPath}/landing" class="btn btn-outline-secondary btn-lg">
                <i class="fas fa-arrow-left me-2"></i>Quay lại trang chủ
            </a>
        </div>
    </div>

    <!-- Include footer if you have one -->
    <%-- <jsp:include page="../common/footer.jsp" /> --%>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>