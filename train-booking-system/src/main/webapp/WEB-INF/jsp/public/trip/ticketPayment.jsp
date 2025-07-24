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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        /* Enhanced styles for improved UX */
        .expired-hold-row {
            background-color: #fff5f5;
            border-left: 4px solid #ff6b6b;
        }
        
        .expired-hold-row .seat-hold-timer.expired {
            color: #ff6b6b;
            font-weight: bold;
        }
        
        .info-notice.warning-notice {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
            padding: 12px;
            color: #856404;
        }
        
        .error-message {
            color: #dc3545;
            font-size: 0.85em;
            margin-top: 4px;
            display: none;
        }
        
        .form-group {
            position: relative;
            margin-bottom: 15px;
        }
        
        .input-wrapper {
            position: relative;
        }
        
        .input-wrapper input.error, 
        .input-wrapper select.error {
            border-color: #dc3545;
            box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
        }
        
        .input-wrapper input.valid, 
        .input-wrapper select.valid {
            border-color: #28a745;
            box-shadow: 0 0 0 0.2rem rgba(40, 167, 69, 0.25);
        }
        
        .passenger-name-type-cell input, 
        .passenger-name-type-cell select {
            margin-bottom: 8px;
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        
        .passenger-name-type-cell input.error, 
        .passenger-name-type-cell select.error {
            border-color: #dc3545;
        }
        
        .passenger-name-type-cell input.valid, 
        .passenger-name-type-cell select.valid {
            border-color: #28a745;
        }
        
        .delete-passenger-button {
            transition: all 0.3s ease;
        }
        
        .delete-passenger-button:hover {
            transform: scale(1.05);
        }
        
        @media (max-width: 768px) {
            .passenger-info-section table {
                font-size: 0.9em;
            }
            
            .passenger-name-type-cell {
                min-width: 200px;
            }
        }

        /* VIP Modal Styles */
        .modal {
            position: fixed;
            z-index: 1000;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            background: white;
            border-radius: 8px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
            max-width: 500px;
            width: 90%;
            max-height: 80vh;
            overflow-y: auto;
        }

        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 999;
        }

        .modal-header {
            padding: 20px;
            border-bottom: 1px solid #dee2e6;
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: linear-gradient(135deg, #FFD700, #FFA500);
            border-radius: 8px 8px 0 0;
        }

        .modal-header h3 {
            margin: 0;
            color: #8B4513;
            font-weight: bold;
        }

        .modal-header .close {
            font-size: 24px;
            font-weight: bold;
            color: #8B4513;
            cursor: pointer;
            background: none;
            border: none;
        }

        .modal-header .close:hover {
            color: #654321;
        }

        .modal-body {
            padding: 20px;
        }

        .modal-footer {
            padding: 15px 20px;
            border-top: 1px solid #dee2e6;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }

        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            transition: all 0.3s ease;
        }

        .btn-primary {
            background: #007bff;
            color: white;
        }

        .btn-primary:hover {
            background: #0056b3;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #1e7e34;
        }

        .form-control {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
            outline: none;
        }

        .validation-message {
            margin-top: 5px;
            font-size: 12px;
        }

        .validation-message.error {
            color: #dc3545;
        }

        .validation-message.success {
            color: #28a745;
        }

        .vip-result-card {
            background: linear-gradient(135deg, #FFD700, #FFA500);
            border: 2px solid #FFD700;
            border-radius: 8px;
            padding: 15px;
            margin: 10px 0;
            color: #8B4513;
            text-align: center;
        }

        .vip-result-card .vip-icon {
            font-size: 24px;
            margin-right: 10px;
        }

        .vip-result-card .vip-discount {
            font-size: 18px;
            font-weight: bold;
            color: #B8860B;
        }
    </style>
</head>
<body>
    <div class="payment-container">
        <form action="${pageContext.request.contextPath}/searchTrip" method="POST" class="go-back-form">
            <c:if test="${not empty lastQuery_originalStationId}">
                <input type="hidden" name="originalStationId" value="${lastQuery_originalStationId}">
            </c:if>
            <c:if test="${not empty lastQuery_destinationStationId}">
                <input type="hidden" name="destinationStationId" value="${lastQuery_destinationStationId}">
            </c:if>
            <c:if test="${not empty lastQuery_departureDate}">
                <input type="hidden" name="departure-date" value="${lastQuery_departureDate}">
            </c:if>
            <c:if test="${not empty lastQuery_returnDate}">
                <input type="hidden" name="return-date" value="${lastQuery_returnDate}">
            </c:if>
            <c:if test="${not empty lastQuery_originalStationName}">
                <input type="hidden" name="original-station-name" value="${lastQuery_originalStationName}">
            </c:if>
            <c:if test="${not empty lastQuery_destinationStationName}">
                <input type="hidden" name="destination-station-name" value="${lastQuery_destinationStationName}">
            </c:if>
            <button type="submit" class="go-back-button-as-link">
                <i class="fas fa-arrow-left"></i> Quay lại chọn thêm vé
            </button>
        </form>

        <h1>Thông tin thanh toán vé</h1>

        <div id="expired-holds-notice" class="info-notice warning-notice" style="display: none; margin-top: 15px; margin-bottom: 15px;">
            <i class="fas fa-exclamation-triangle"></i> Các vé có biểu tượng <i class="fas fa-clock icon-expired-hold"></i> là các vé bị hết thời gian tạm giữ. Xin vui lòng loại bỏ các vé này khỏi danh sách vé đặt mua trước khi thực hiện giao dịch thanh toán tiền.
        </div>

        <div class="payment-timer">
            <i class="fas fa-clock"></i> Thời gian còn lại để hoàn tất thanh toán: <span id="payment-countdown">05:00</span>
        </div>
        <div id="general-error-message" class="error-message" style="text-align:center; margin-bottom:15px;"></div>

        <form id="paymentForm" novalidate> <%-- Action and method will be handled by JavaScript --%>
            
            <div class="info-notice passenger-info-instructions">
                <i class="fas fa-info-circle"></i> Quý khách vui lòng điền đầy đủ, chính xác tất cả các thông tin về hành khách đi tàu bao gồm: Họ tên đầy đủ, số giấy tờ tùy thân (Số chứng minh nhân dân hoặc số hộ chiếu hoặc số giấy phép lái xe đường bộ được pháp luật Việt Nam công nhận hoặc ngày tháng năm sinh nếu là trẻ em hoặc thẻ sinh viên nếu là sinh viên). Để đảm bảo an toàn, minh bạch trong quá trình bán vé các thông tin này sẽ được nhân viên soát vé kiểm tra trước khi lên tàu theo đúng các quy định của Tổng công ty Đường sắt Việt Nam.
            </div>
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
                <div style="text-align: right; margin-top: 10px; margin-bottom: 20px;">
                    <button type="button" id="clearAllSeatsButton" class="button secondary-button">
                        <i class="fas fa-times-circle"></i> Hủy tất cả vé đã chọn
                    </button>
                </div>
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
                    Tôi đã đọc, hiểu rõ và đồng ý với các 
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

    <%-- VIP ID Validation Modal --%>
    <div id="vipValidationModal" class="modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3><i class="fas fa-crown"></i> Xác thực thành viên VIP</h3>
                <span class="close" onclick="closeVIPModal()">&times;</span>
            </div>
            <div class="modal-body">
                <p>Vui lòng nhập số CMND/CCCD để xác thực thẻ VIP của bạn:</p>
                <div class="form-group">
                    <label for="vipIdInput">Số CMND/CCCD:</label>
                    <input type="text" id="vipIdInput" class="form-control" placeholder="Nhập số CMND/CCCD" pattern="[0-9]{9,12}" maxlength="12">
                    <div id="vipValidationMessage" class="validation-message"></div>
                </div>
                <div id="vipLoadingIndicator" style="display: none; text-align: center; margin: 10px 0;">
                    <i class="fas fa-spinner fa-spin"></i> Đang xác thực...
                </div>
                <div id="vipValidationResult" style="display: none;"></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" onclick="closeVIPModal()">Hủy</button>
                <button type="button" id="validateVIPBtn" class="btn btn-primary" onclick="validateVIPCredentials()">
                    <i class="fas fa-check"></i> Xác thực
                </button>
                <button type="button" id="confirmVIPBtn" class="btn btn-success" style="display: none;" onclick="confirmVIPSelection()">
                    <i class="fas fa-crown"></i> Xác nhận VIP
                </button>
            </div>
        </div>
    </div>
    <div id="modalOverlay" class="modal-overlay" style="display: none;" onclick="closeVIPModal()"></div>
    
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
        
        // Auto-populate customer information for logged-in users
        <c:if test="${not empty loggedInUser}">
            var loggedInUser = {
                fullName: "${loggedInUser.fullName}",
                email: "${loggedInUser.email}",
                phoneNumber: "${loggedInUser.phoneNumber}",
                idCardNumber: "${loggedInUser.idCardNumber}"
            };
        </c:if>

        // VIP Modal variables - will be used by external JS
        window.currentVIPRow = null;
        window.vipValidationResult = null;
    </script>
    <script src="${pageContext.request.contextPath}/js/trip/ticketPayment.js"></script>
</body>
</html>
