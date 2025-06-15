<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán vé tàu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/trip/ticket-payment.css"> <%-- New CSS file --%>
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        /* Basic styling for layout - will be moved to ticket-payment.css */
        .payment-container {
            max-width: 900px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-family: Arial, sans-serif;
        }
        .payment-timer {
            text-align: center;
            font-size: 1.2em;
            color: red;
            margin-bottom: 20px;
        }
        .passenger-info-section table,
        .customer-info-section,
        .payment-summary,
        .payment-methods,
        .terms-agreement {
            margin-bottom: 20px;
        }
        .passenger-info-section table {
            width: 100%;
            border-collapse: collapse;
        }
        .passenger-info-section th,
        .passenger-info-section td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        .passenger-info-section th {
            background-color: #f2f2f2;
        }
        .customer-info-section div {
            margin-bottom: 10px;
        }
        .customer-info-section label {
            display: inline-block;
            width: 150px;
        }
        .customer-info-section input[type="text"],
        .customer-info-section input[type="email"],
        .customer-info-section input[type="tel"],
        .passenger-info-section input[type="text"],
        .passenger-info-section input[type="date"],
        .passenger-info-section select {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .button.primary-button {
            background-color: #007bff;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
        }
        .button.primary-button:hover {
            background-color: #0056b3;
        }
        .go-back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #007bff;
            text-decoration: none;
        }
        .go-back-link:hover {
            text-decoration: underline;
        }
        .error-message {
            color: red;
            font-size: 0.9em;
        }
    </style>
</head>
<body>
    <div class="payment-container">
        <a href="${pageContext.request.contextPath}/tripResult.jsp" class="go-back-link">< Quay lại chọn thêm vé</a>

        <h1>Thông tin thanh toán vé</h1>

        <div class="payment-timer">
            Thời gian còn lại để hoàn tất thanh toán: <span id="payment-countdown">05:00</span>
        </div>
        <div id="general-error-message" class="error-message" style="text-align:center; margin-bottom:15px;"></div>


        <form id="paymentForm" novalidate> <%-- Action and method will be handled by JS --%>
            
            <h2>Thông tin hành khách (<span id="passenger-count">0</span>/10)</h2>
            <div class="passenger-info-section">
                <table>
                    <thead>
                        <tr>
                            <th>Họ tên & Đối tượng</th>
                            <th>Thông tin chỗ</th>
                            <th>Giá vé (VNĐ)</th>
                            <th>Giảm đối tượng (VNĐ)</th>
                            <th>Thành tiền (VNĐ)</th>
                        </tr>
                    </thead>
                    <tbody id="passenger-details-body">
                        <%-- Passenger rows will be dynamically inserted here by JavaScript --%>
                        <tr id="no-passengers-row" style="display: none;">
                            <td colspan="5" style="text-align:center;">Vui lòng quay lại và chọn vé.</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <h2>Thông tin người đặt vé (người liên hệ)</h2>
            <div class="customer-info-section">
                <div>
                    <label for="customerFullName">Họ tên:</label>
                    <input type="text" id="customerFullName" name="customerFullName" required>
                    <span class="error-message" id="customerFullNameError"></span>
                </div>
                <div>
                    <label for="customerEmail">Email:</label>
                    <input type="email" id="customerEmail" name="customerEmail" required>
                    <span class="error-message" id="customerEmailError"></span>
                </div>
                <div>
                    <label for="customerEmailConfirm">Nhập lại Email:</label>
                    <input type="email" id="customerEmailConfirm" name="customerEmailConfirm" required>
                    <span class="error-message" id="customerEmailConfirmError"></span>
                </div>
                <div>
                    <label for="customerPhone">Số điện thoại:</label>
                    <input type="tel" id="customerPhone" name="customerPhone" required pattern="[0-9]{10,11}">
                    <span class="error-message" id="customerPhoneError"></span>
                </div>
                <div>
                    <label for="customerIDCard">Số CMND/CCCD (nếu có):</label>
                    <input type="text" id="customerIDCard" name="customerIDCard" pattern="[0-9]{9,12}">
                    <span class="error-message" id="customerIDCardError"></span>
                </div>
            </div>

            <div class="payment-summary">
                <h3>Tổng cộng: <span id="total-payment-amount">0</span> VNĐ</h3>
            </div>
            
            <div class="payment-methods">
                <h2>Chọn hình thức thanh toán</h2>
                <%-- TODO: Payment gateway options here --%>
                <p>(Khu vực lựa chọn hình thức thanh toán - sẽ được triển khai)</p>
                 <select id="paymentMethod" name="paymentMethod">
                    <option value="VNPAY">VNPAY</option>
                    <option value="MOMO">MOMO</option>
                    <option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>
                    <%-- Add other payment methods as needed --%>
                </select>
            </div>

            <div class="terms-agreement">
                <input type="checkbox" id="agreeTerms" name="agreeTerms" required>
                <label for="agreeTerms">Tôi đã đọc, hiểu rõ và đồng ý với các <a href="${pageContext.request.contextPath}/termsAndConditions.jsp" target="_blank">điều khoản và điều kiện</a> mua vé trực tuyến của Đường sắt Việt Nam.</label>
                <div class="error-message" id="agreeTermsError"></div>
            </div>

            <button type="submit" id="submitPaymentButton" class="button primary-button">Thanh toán</button>
        </form>
    </div>

    <%-- Hidden template for passenger row --%>
    <template id="passenger-row-template">
        <tr>
            <td class="passenger-name-type-cell">
                <input type="text" name="fullName" placeholder="Họ tên hành khách" class="passenger-fullName" required>
                <div class="error-message fullNameError"></div>
                <select name="passengerType" class="passenger-type-selector" required>
                    <option value="">-- Chọn đối tượng --</option>
                    <%-- Options will be populated by JS --%>
                </select>
                <div class="error-message passengerTypeError"></div>
                <input type="text" name="idCardNumber" class="passenger-idCardNumber" placeholder="Số CMND/CCCD/Giấy tờ" style="display:none; margin-top: 5px;">
                <div class="error-message idCardNumberError"></div>
                <input type="date" name="dateOfBirth" class="passenger-dateOfBirth" style="display:none; margin-top: 5px;">
                <div class="error-message dateOfBirthError"></div>
            </td>
            <td class="seat-info-cell">
                <%-- Seat details will be populated by JS --%>
                <div class="seat-train-route"></div>
                <div class="seat-departure-datetime"></div>
                <div class="seat-coach-seat"></div>
                <div class="seat-description"></div>
                <div class="seat-hold-timer" style="font-size:0.9em; color: #555;"></div>
            </td>
            <td class="original-price-cell"></td>
            <td class="discount-amount-cell">0</td>
            <td class="final-price-cell"></td>
        </tr>
    </template>
    
    <script>
        // Global contextPath for JavaScript files
        var contextPath = "${pageContext.request.contextPath}";
        // Passenger types data (to be populated by the servlet)
        var passengerTypesData = []; 
        // Example: passengerTypesData = [{"passengerTypeID":1,"typeName":"Người lớn","discountPercentage":0.00,"requiresDocument":true,"isChild":false}, ...];
        <c:if test="${not empty passengerTypesJson}">
            try {
                passengerTypesData = JSON.parse('${passengerTypesJson}');
            } catch (e) {
                console.error("Error parsing passengerTypesJson:", e);
            }
        </c:if>
    </script>
    <script src="${pageContext.request.contextPath}/js/trip/ticketPayment.js"></script>
</body>
</html>
