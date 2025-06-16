<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán vé tàu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/trip/ticket-payment.css"> <%-- New CSS file --%>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <%-- Inline styles moved to css/trip/ticket-payment.css --%>
</head>
<body>
    <div class="payment-container">
        <a href="${pageContext.request.contextPath}/tripResult.jsp" class="go-back-link">
            <i class="fas fa-arrow-left"></i> Quay lại chọn thêm vé
        </a>

        <h1>Thông tin thanh toán vé</h1>

        <div class="payment-timer">
            <i class="fas fa-clock"></i> Thời gian còn lại để hoàn tất thanh toán: <span id="payment-countdown">05:00</span>
        </div>
        <div id="general-error-message" class="error-message" style="text-align:center; margin-bottom:15px;"></div>

        <form id="paymentForm" novalidate> <%-- Action and method will be handled by JavaScript --%>
            
            <h2><i class="fas fa-users"></i> Thông tin hành khách</h2>
            <section class="passenger-info-section">
                <table>
                    <thead>
                        <tr>
                            <th><i class="fas fa-user-tag"></i> Họ tên & Đối tượng</th>
                            <th><i class="fas fa-chair"></i> Thông tin chỗ</th>
                            <th><i class="fas fa-dollar-sign"></i> Giá vé (VNĐ)</th>
                            <th><i class="fas fa-percent"></i> Giảm đối tượng (VNĐ)</th>
                            <th><i class="fas fa-money-bill-wave"></i> Thành tiền (VNĐ)</th>
                            <th><i class="fas fa-trash-alt"></i> Xóa</th>
                        </tr>
                    </thead>
                    <tbody id="passenger-details-body">
                        <%-- Passenger rows will be dynamically inserted here by JavaScript --%>
                        <tr id="no-passengers-row" style="display: none;">
                            <td colspan="6" style="text-align:center;">Vui lòng quay lại và chọn vé.</td>
                        </tr>
                    </tbody>
                </table>
            </section>

            <h2><i class="fas fa-user-circle"></i> Thông tin người đặt vé (người liên hệ)</h2>
            <section class="customer-info-section">
                <div class="form-group">
                    <label for="customerFullName"><i class="fas fa-user"></i> Họ tên:</label>
                    <div class="input-wrapper">
                        <input type="text" id="customerFullName" name="customerFullName" required aria-describedby="customerFullNameError">
                        <span class="error-message" id="customerFullNameError"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="customerEmail"><i class="fas fa-envelope"></i> Email:</label>
                    <div class="input-wrapper">
                        <input type="email" id="customerEmail" name="customerEmail" required aria-describedby="customerEmailError">
                        <span class="error-message" id="customerEmailError"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="customerEmailConfirm"><i class="fas fa-envelope-open-text"></i> Nhập lại Email:</label>
                    <div class="input-wrapper">
                        <input type="email" id="customerEmailConfirm" name="customerEmailConfirm" required aria-describedby="customerEmailConfirmError">
                        <span class="error-message" id="customerEmailConfirmError"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="customerPhone"><i class="fas fa-phone"></i> Số điện thoại:</label>
                    <div class="input-wrapper">
                        <input type="tel" id="customerPhone" name="customerPhone" required pattern="[0-9]{10,11}" aria-describedby="customerPhoneError">
                        <span class="error-message" id="customerPhoneError"></span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="customerIDCard"><i class="fas fa-id-card"></i> Số CMND/CCCD (nếu có):</label>
                    <div class="input-wrapper">
                        <input type="text" id="customerIDCard" name="customerIDCard" pattern="[0-9]{9,12}" aria-describedby="customerIDCardError">
                        <span class="error-message" id="customerIDCardError"></span>
                    </div>
                </div>
            </section>

            <section class="payment-summary">
                <h3><i class="fas fa-file-invoice-dollar"></i> Tổng cộng: <span id="total-payment-amount">0</span> VNĐ</h3>
            </section>
            
            <section class="payment-methods">
                <h2><i class="fas fa-credit-card"></i> Chọn hình thức thanh toán</h2>
                <%-- TODO: Payment gateway options here --%>
                <p>(Khu vực lựa chọn hình thức thanh toán - sẽ được triển khai)</p>
                 <select id="paymentMethod" name="paymentMethod">
                    <option value="VNPAY">VNPAY</option>
                    <option value="MOMO">MOMO</option>
                    <option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>
                    <%-- Add other payment methods as needed --%>
                </select>
            </section>

            <section class="terms-agreement">
                <input type="checkbox" id="agreeTerms" name="agreeTerms" required aria-describedby="agreeTermsError">
                <label for="agreeTerms">
                    <i class="fas fa-check-square"></i> Tôi đã đọc, hiểu rõ và đồng ý với các 
                    <a href="${pageContext.request.contextPath}/termsAndConditions.jsp" target="_blank" rel="noopener noreferrer">điều khoản và điều kiện</a> 
                    mua vé trực tuyến của Đường sắt Việt Nam.
                </label>
                <div class="error-message" id="agreeTermsError"></div>
            </section>

            <button type="submit" id="submitPaymentButton" class="button primary-button">
                <i class="fas fa-lock"></i> Thanh toán
            </button>
        </form>
    </div>

    <%-- Hidden template for passenger row --%>
    <template id="passenger-row-template">
        <tr>
            <td class="passenger-name-type-cell">
                <label for="fullName-${index}" class="sr-only">Họ tên hành khách</label>
                <input type="text" name="fullName" id="fullName-${index}" placeholder="Họ tên hành khách" class="passenger-fullName" required aria-describedby="fullNameError-${index}">
                <div class="error-message fullNameError" id="fullNameError-${index}"></div>
                
                <label for="passengerType-${index}" class="sr-only">Đối tượng hành khách</label>
                <select name="passengerType" id="passengerType-${index}" class="passenger-type-selector" required aria-describedby="passengerTypeError-${index}">
                    <option value="">-- Chọn đối tượng --</option>
                    <%-- Options will be populated by JS --%>
                </select>
                <div class="error-message passengerTypeError" id="passengerTypeError-${index}"></div>

                <label for="idCardNumber-${index}" class="sr-only">Số CMND/CCCD/Giấy tờ</label>
                <input type="text" name="idCardNumber" id="idCardNumber-${index}" class="passenger-idCardNumber" placeholder="Số CMND/CCCD/Giấy tờ" style="display:none; margin-top: 5px;" aria-describedby="idCardNumberError-${index}">
                <div class="error-message idCardNumberError" id="idCardNumberError-${index}"></div>

                <label for="dateOfBirth-${index}" class="sr-only">Ngày sinh</label>
                <input type="date" name="dateOfBirth" id="dateOfBirth-${index}" class="passenger-dateOfBirth" style="display:none; margin-top: 5px;" aria-describedby="dateOfBirthError-${index}">
                <div class="error-message dateOfBirthError" id="dateOfBirthError-${index}"></div>
            </td>
            <td class="seat-info-cell">
                <%-- Seat details will be populated by JS --%>
                <div class="seat-train-route"></div>
                <div class="seat-departure-datetime"></div>
                <div class="seat-coach-seat"></div>
                <div class="seat-description"></div>
                <div class="seat-hold-timer" style="font-size:0.9em; color: #555;"></div>
            </td>
            <td class="original-price-cell" data-label="Giá vé gốc"></td>
            <td class="discount-amount-cell" data-label="Giảm giá">0</td>
            <td class="final-price-cell" data-label="Thành tiền"></td>
            <td class="action-cell" data-label="Xóa">
                <button type="button" class="button-icon delete-passenger-button" title="Xóa vé này">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        </tr>
    </template>
    
    <script>
        var contextPath = "${pageContext.request.contextPath}";
        var passengerTypesData = []; 
        <c:if test="${not empty passengerTypesJson}">
            try {
                // Sanitize JSON string before parsing
                var sanitizedJsonString = '${passengerTypesJson}'.replace(/\\'/g, "'").replace(/\\"/g, '"');
                passengerTypesData = JSON.parse(sanitizedJsonString);
            } catch (e) {
                console.error("Error parsing passengerTypesJson:", e, "Raw JSON: ", '${passengerTypesJson}');
            }
        </c:if>
    </script>
    <script src="${pageContext.request.contextPath}/js/trip/ticketPayment.js"></script>
</body>
</html>
