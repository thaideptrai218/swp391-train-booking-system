<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tìm kiếm chuyến đi</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/search-trip.css">
</head>
<body>
    <div class="booking-container">
        <h1>Đặt vé tàu trực tuyến</h1>
        <div class="booking-form-block">
            <form method="POST" action="${pageContext.request.contextPath}/searchTrip" id="searchTripForm">
                <div class="form-input-bar">
                    <div class="station-inputs">
                        <div class="input-group origin-station-group">
                            <img src="${pageContext.request.contextPath}/assets/icons/origin_marker_icon.png" alt="Origin Icon" class="input-icon">
                            <div class="input-field-wrapper">
                                <label for="originStation">Nơi xuất phát</label>
                                <input type="text" id="originStation" name="originStation" placeholder="Chọn nơi xuất phát">
                            </div>
                        </div>
                        <button type="button" id="switchStationsBtn" class="switch-stations-btn">
                            <!-- SVG for switch arrows will be added via CSS or inline later -->
                            <svg width="18" height="12" viewBox="0 0 18 12" fill="none" xmlns="http://www.w3.org/2000/svg">
                                <path d="M1 6H17M17 6L12 1M17 6L12 11" stroke="#10375C" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                                <path d="M17 6H1M1 6L6 1M1 6L6 11" stroke="#10375C" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" transform="rotate(180 9 6)"/>
                            </svg>
                        </button>
                        <div class="input-group destination-station-group">
                            <img src="${pageContext.request.contextPath}/assets/icons/destination_marker_icon.png" alt="Destination Icon" class="input-icon">
                            <div class="input-field-wrapper">
                                <label for="destinationStation">Nơi đến</label>
                                <input type="text" id="destinationStation" name="destinationStation" placeholder="Chọn nơi đến">
                            </div>
                        </div>
                    </div>
                    <div class="date-inputs">
                        <div class="input-group departure-date-group">
                            <img src="${pageContext.request.contextPath}/assets/icons/calendar_icon.png" alt="Calendar Icon" class="input-icon">
                            <div class="input-field-wrapper">
                                <label for="departureDate">Ngày đi</label>
                                <input type="date" id="departureDate" name="departureDate">
                            </div>
                        </div>
                        <div class="input-group return-date-group">
                            <button type="button" id="addReturnDateBtn">+ Thêm ngày về</button>
                            <div class="input-field-wrapper" id="returnDateFieldWrapper" style="display:none;">
                                <img src="${pageContext.request.contextPath}/assets/icons/calendar_icon.png" alt="Calendar Icon" class="input-icon">
                                <label for="returnDate">Ngày về</label>
                                <input type="date" id="returnDate" name="returnDate">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="passenger-selection-bar">
                    <div class="total-passenger-display" id="totalPassengerDisplay">
                        <img src="${pageContext.request.contextPath}/assets/icons/people_icon.png" alt="People Icon">
                        <span id="totalPassengerText">1 Hành khách</span>
                    </div>
                    <div class="passenger-dropdown" id="passengerDropdown" style="display:none;">
                        <div class="passenger-type">
                            <img src="${pageContext.request.contextPath}/assets/icons/adult_icon.png" alt="Adult Icon">
                            <label for="numAdults">Người lớn</label>
                            <input type="number" id="numAdults" name="numAdults" value="1" min="1">
                        </div>
                        <div class="passenger-type">
                            <img src="${pageContext.request.contextPath}/assets/icons/child_icon.png" alt="Child Icon">
                            <label for="numChildren">Trẻ em (dưới 10 tuổi)</label>
                            <input type="number" id="numChildren" name="numChildren" value="0" min="0">
                            <span class="discount-badge">-25%</span>
                        </div>
                        <div class="passenger-type">
                            <img src="${pageContext.request.contextPath}/assets/icons/student_icon.png" alt="Student Icon">
                            <label for="numStudents">Sinh viên</label>
                            <input type="number" id="numStudents" name="numStudents" value="0" min="0">
                            <span class="discount-badge">-10%</span>
                        </div>
                        <div class="passenger-type">
                            <img src="${pageContext.request.contextPath}/assets/icons/elderly_icon.png" alt="Elderly Icon">
                            <label for="numElderly">Người cao tuổi (trên 60)</label>
                            <input type="number" id="numElderly" name="numElderly" value="0" min="0">
                            <span class="discount-badge">-15%</span>
                        </div>
                        <div class="passenger-type">
                            <img src="${pageContext.request.contextPath}/assets/icons/group_icon.png" alt="Group Icon">
                            <label for="numGroup">Đoàn viên công đoàn (ĐVCĐ)</label>
                            <input type="number" id="numGroup" name="numGroup" value="0" min="0">
                            <span class="discount-badge">-5%</span>
                        </div>
                    </div>
                </div>

                <div class="submit-bar">
                    <button type="submit" class="search-button">Tìm kiếm</button>
                </div>
            </form>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/search-trip.js"></script>
</body>
</html>
