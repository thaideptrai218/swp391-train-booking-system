<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="java.util.List, vn.vnrailway.model.PassengerType" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Trip Background</title>
    <meta name="description" content="Trang tìm kiếm chuyến đi với nền tùy chỉnh">
    <meta name="keywords" content="tàu hỏa, vé tàu, tìm kiếm, background">
    <meta name="author" content="Vetaure">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&family=Sora:wght@400;600&display=swap" rel="stylesheet">
    <%-- Giả định một file CSS mới cho trang này. Bạn có thể thay đổi nếu cần. --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/searchTripBackground.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search-trip.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/themes/material_blue.css"/>
    <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
</head>
<body data-context-path="${pageContext.request.contextPath}">
    <%@include file="../../common/header.jsp" %>

    <!-- Nội dung chính -->
    <main>
        <section class="hero-section">
            <%-- Background image will be set via CSS --%>
            <div class="search-trip-container"> <%-- Added a container for positioning the search form over the background --%>
                <section class="search-trip-wrapper">
                    <div class="search-panel">
                        <form
                            action="<c:url value='/searchTrip'/>"
                            method="POST"
                            id="searchTripForm"
                            class="search-form-flex"
                        >
                            <div class="search-bar">
                                <div class="input-section">
                                    <div class="input-icon-group">
                                        <span class="input-icon"
                                            ><i class="fa-solid fa-train-subway"></i
                                        ></span>
                                        <div class="form-group">
                                            <input
                                                type="text"
                                                id="original-station"
                                                name="original-station-name"
                                                value="${prefill_originalStationName}"
                                                class="${not empty prefill_originalStationName ? 'has-value' : ''}"
                                                autocomplete="off"
                                                onfocus="this.classList.add('has-value')"
                                                onblur="if(!this.value) this.classList.remove('has-value')"
                                                required
                                            />
                                            <label for="original-station">Ga đi</label>
                                            <input type="hidden" id="original-station-id" name="originalStationId" value="${prefill_originalStationId}" />
                                            <div id="original-station-suggestions" class="autocomplete-suggestions"></div>
                                        </div>
                                    </div>
                                    <div class="input-icon-group">
                                        <span class="input-icon"
                                            ><i class="fa-solid fa-location-dot"></i
                                        ></span>
                                        <div class="form-group">
                                            <input
                                                type="text"
                                                id="destination-station"
                                                name="destination-station-name"
                                                value="${prefill_destinationStationName}"
                                                class="${not empty prefill_destinationStationName ? 'has-value' : ''}"
                                                autocomplete="off"
                                                onfocus="this.classList.add('has-value')"
                                                onblur="if(!this.value) this.classList.remove('has-value')"
                                                required
                                            />
                                            <label for="destination-station"
                                                >Ga đến</label
                                            >
                                            <input type="hidden" id="destination-station-id" name="destinationStationId" value="${prefill_destinationStationId}" />
                                            <div id="destination-station-suggestions" class="autocomplete-suggestions"></div>
                                        </div>
                                    </div>
                                    <div class="input-icon-group date-group">
                                        <span class="input-icon"
                                            ><i class="fa-solid fa-calendar-days"></i
                                        ></span>
                                        <div class="form-group">
                                            <input
                                                type="date"
                                                id="departure-date"
                                                name="departure-date"
                                                autocomplete="off"
                                                required
                                            />
                                            <label for="departure-date">Ngày đi</label>
                                        </div>
                                    </div>
                                    <div
                                        class="input-icon-group date-group"
                                        id="return-date-group"
                                    >
                                        <div class="add-return-date-prompt">
                                            <i class="fa-solid fa-plus"></i> Thêm ngày
                                            về
                                        </div>
                                        <span
                                            class="input-icon return-date-icon-element"
                                            style="display: none"
                                            ><i class="fa-solid fa-calendar-plus"></i
                                        ></span>
                                        <div
                                            class="form-group return-date-form-group-element"
                                            style="display: none"
                                        >
                                            <input
                                                type="date"
                                                id="return-date"
                                                name="return-date"
                                                autocomplete="off"
                                            />
                                            <label for="return-date">Ngày về</label>
                                            <button
                                                type="button"
                                                class="clear-return-date"
                                                aria-label="Clear return date"
                                            >
                                                <i class="fa-solid fa-times"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                                <button class="submit-button" type="submit">
                                    Tìm kiếm
                                </button>
                            </div>

                            
                            <div class="passengers-bar">
                                <div class="passenger-selector">
                                    <div class="passenger-summary" tabindex="0">
                                        <div class="passenger-count-display">
                                            <span class="passenger-icon"><i class="fa-solid fa-user-group"></i></span>
                                            <input type="hidden" class="passenger-total-number" name="passenger-total-number"></input>
                                            <span class="passenger-total-text"> 1 Hành khách</span>

                                        </div>
                                        <div class="passenger-container">
                                            <c:forEach var="pt" items="${passengerTypes}">
                                                <c:set var="passengerTypeCode" value="${fn:toLowerCase(fn:replace(pt.typeName, ' ', '_'))}" />
                                                <c:set var="defaultQuantity" value="${pt.typeName == 'Người lớn' ? 1 : 0}" />
                                                <div class="sub-container" data-type="${passengerTypeCode}" data-discount-info="-${pt.discountPercentage.stripTrailingZeros().toPlainString()}%">
                                                    <%-- Icon removed as per request --%>
                                                    <span class="sub-quantity">${defaultQuantity}</span>
                                                    <span class="sub-label">${pt.typeName}</span>
                                                </div>
                                            </c:forEach>
                                            <div class="passenger-details">
                                                <c:forEach var="pt" items="${passengerTypes}">
                                                    <c:set var="passengerTypeCode" value="${fn:toLowerCase(fn:replace(pt.typeName, ' ', '_'))}" />
                                                    <c:set var="defaultQuantity" value="${pt.typeName == 'Người lớn' ? 1 : 0}" />
                                                    <div class="passenger-group" data-type="${passengerTypeCode}" data-min="0" data-max="10">
                                                        <div class="passenger-info">
                                                            <p class="title">${pt.typeName} <span class="discount-detail-text">(GIẢM ${pt.discountPercentage.stripTrailingZeros().toPlainString()}%)</span></p>
                                                            <p class="desc">${pt.description}</p>
                                                        </div>
                                                        <div class="input-block">
                                                            <button type="button" class="decrease-btn" aria-label="Giảm số lượng ${pt.typeName}"><i class="fa-solid fa-minus"></i></button>
                                                            <input type="text" class="quantity-display" value="${defaultQuantity}" readonly aria-live="polite">
                                                            <input type="hidden" name="${passengerTypeCode}_quantity" value="${defaultQuantity}">
                                                            <button type="button" class="increase-btn" aria-label="Tăng số lượng ${pt.typeName}"><i class="fa-solid fa-plus"></i></button>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </section>
            </div>
        </section>

        <section class="why-vetaure content-wrapper">
            <h2>Tại sao nên mua vé tàu lửa qua Vetaure.com?</h2>
            <div class="features">
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-checkmark.png" alt="Chắc chắn có vé Icon">
                    <h3>Chắc chắn có vé</h3>
                    <p>Tích hợp trực tiếp với Tổng công ty Đường sắt Việt Nam</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-payment.png" alt="Đa dạng hình thức thanh toán Icon">
                    <h3>Đa dạng hình thức thanh toán</h3>
                    <p>Thanh toán đa dạng và an toàn với ví điện tử (Momo, Zalopay...), thẻ ATM nội địa, thẻ Visa/Master.</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-gift.png" alt="Ưu đãi Icon">
                    <h3>Đi càng nhiều ưu đãi càng lớn</h3>
                    <p>Vetaure.com luôn có chương trình ưu đãi đối với ngày lễ</p>
                </div>
                <div class="feature-item">
                    <img src="${pageContext.request.contextPath}/assets/images/feature-support.png" alt="Hỗ trợ Icon">
                    <h3>Hỗ trợ tận tình và nhanh chóng</h3>
                    <p>Vetaure.com luôn thấu hiểu khách hàng và hỗ trợ bạn mọi lúc mọi nơi cho hành trình trọn vẹn.</p>
                </div>
            </div>
        </section>

        <section class="price-board content-wrapper">
            <h2>Bảng giá vé tàu Đường sắt Việt Nam các tuyến phổ biến</h2>
            <p class="price-board-intro">Dưới đây là thông tin chi tiết về <strong>giá vé tàu hỏa</strong> cho các hành trình phổ biến, giúp bạn dễ dàng chọn lựa:</p>
            <ul>
                <li><span class="bullet">🔹</span> <strong>Huế - Đà Nẵng:</strong> Chỉ từ <strong>84.000đ</strong> cho ghế mềm điều hòa đến <strong>149.000đ</strong> cho vé giường nằm khoang 5.</li>
                <li><span class="bullet">🔹</span> <strong>Sài Gòn - Nha Trang:</strong> Chỉ từ <strong>265.000đ</strong> đến <strong>472.000đ</strong>, phù hợp cho các chuyến đi ngắn.</li>
                <li><span class="bullet">🔹</span> <strong>Hà Nội - Hải Phòng:</strong> Giá vé từ <strong>98.000đ</strong>, hành trình ngắn nên chỉ có ghế ngồi mềm</li>
                <li><span class="bullet">🔹</span> <strong>Sài Gòn - Hà Nội:</strong> Giá vé dao động từ <strong>919.000đ</strong> (ghế ngồi mềm điều hòa) đến <strong>1.518.000đ</strong> (giường nằm khoang 4).</li>
            </ul>
        </section>

        <section class="train-schedule content-wrapper">
            <h2>Bảng giờ tàu mới nhất từ Đường Sắt Việt Nam (Cập nhật tháng 3/2025)</h2>
            <p>Từ ngày <strong>18/3</strong>, Đường Sắt Việt Nam (DSVN) sẽ tăng cường thêm tàu <strong>SE11, SE12</strong> trên tuyến <strong>Hà Nội – TP. Hồ Chí Minh</strong>, giúp hành khách có thêm lựa chọn khi di chuyển giữa hai thành phố lớn.</p>
            <p>Hiện tại, các tuyến tàu chạy hàng ngày như sau:</p>
            <ul>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – TP. Hồ Chí Minh:</strong> SE1/SE2, SE3/SE4, SE5/SE6, SE7/SE8, SE11/SE12</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Đà Nẵng:</strong> SE21/SE22</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Quy Nhơn:</strong> SE30 (thứ 6 hàng tuần), SE29 (Chủ Nhật hàng tuần)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Nha Trang:</strong> SNT1/SNT2 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>TP. Hồ Chí Minh – Phan Thiết:</strong> SPT1/SPT2 (thứ 6, 7, Chủ Nhật)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – Đà Nẵng:</strong> SE19/SE20 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Hà Nội – Vinh:</strong> NA1/NA2 (hàng ngày)</li>
                <li><span class="bullet">🔹</span> Tuyến <strong>Đà Nẵng – Huế:</strong> HĐ1/HĐ2, HĐ3/HĐ4 (hàng ngày)</li>
            </ul>
        </section>

        <section class="railway-map content-wrapper">
            <h2>Bản Đồ Tuyến Đường Sắt Việt Nam - Ga Tàu & Các Tuyến Tàu Hỏa Chính</h2>
            <p>Khám phá bản đồ đường sắt Việt Nam với đầy đủ thông tin các tuyến tàu <strong>Bắc – Nam, Hà Nội – Hải Phòng, Hà Nội - Sapa, Đà Nẵng, Huế, Nha Trang, Phan Thiết</strong> và nhiều ga lớn trên cả nước. Hình ảnh trực quan giúp bạn dễ dàng tra cứu lộ trình, lựa chọn điểm đến và đặt vé tàu phù hợp.</p>
            <img src="${pageContext.request.contextPath}/assets/images/map.png" alt="Bản đồ tuyến đường sắt Việt Nam" class="map-image">
        </section>
    </main>

    <%@include file="../../common/footer.jsp" %>
    <script src="${pageContext.request.contextPath}/js/trip/search-trip.js"></script>
    <script src="${pageContext.request.contextPath}/js/trip/passenger-selector.js"></script>
    <script src="${pageContext.request.contextPath}/js/trip/station-autocomplete.js"></script>
</body>
</html>
