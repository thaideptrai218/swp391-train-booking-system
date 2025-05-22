<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tìm kiếm vé tàu</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search-trip.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <div class="search-trip-wrapper">
        <%-- <h1>Tìm vé tàu</h1> --%> <%-- Title can be removed if the search bar is self-explanatory --%>
        <div class="search-panel">
            <form action="${pageContext.request.contextPath}/searchTrip" method="POST" id="searchTripForm" class="search-form-flex">
                
                <div class="search-field-group origin-field">
                    <img src="${pageContext.request.contextPath}/assets/icons/origin_marker_icon.png" alt="Origin Icon" class="field-icon">
                    <div class="input-wrapper">
                        <input type="text" id="origin" name="originStationCode" required>
                        <label for="origin">Nơi xuất phát</label>
                        <%-- Secondary text like "Ga Hà Nội" could be added here dynamically if needed --%>
                    </div>
                </div>

                <button type="button" id="swapStationsBtn" class="swap-stations-btn" title="Đổi chiều">&#8646;</button>

                <div class="search-field-group destination-field">
                    <img src="${pageContext.request.contextPath}/assets/icons/destination_marker_icon.png" alt="Destination Icon" class="field-icon">
                    <div class="input-wrapper">
                        <input type="text" id="destination" name="destinationStationCode" required>
                        <label for="destination">Nơi đến</label>
                         <%-- Secondary text like "Ga Đà Nẵng" could be added here dynamically if needed --%>
                    </div>
                </div>

                <div class="separator"></div>

                <div class="search-field-group date-field">
                    <img src="${pageContext.request.contextPath}/assets/icons/calendar_icon.png" alt="Calendar Icon" class="field-icon">
                    <div class="input-wrapper">
                        <input type="text" id="departureDate" name="departureDate" placeholder="Chọn ngày đi" required onfocus="(this.type='date')" onblur="(this.type='text')">
                        <label for="departureDate">Ngày đi</label>
                    </div>
                </div>
                
                <div class="separator"></div>

                <div class="search-field-group return-date-field">
                     <a href="#" id="addReturnDateLink" class="add-return-link">+ Thêm ngày về</a>
                    <div class="input-wrapper" id="returnDateInputWrapper" style="display:none;">
                        <%-- Icon can be added here if needed when visible --%>
                        <%-- <img src="${pageContext.request.contextPath}/assets/icons/calendar_icon.png" alt="Calendar Icon" class="field-icon"> --%>
                        <input type="text" id="returnDate" name="returnDate" placeholder="Chọn ngày về" onfocus="(this.type='date')" onblur="(this.type='text')">
                        <label for="returnDate">Ngày về</label>
                    </div>
                </div>
                
                <button type="submit" class="search-action-btn">Tìm kiếm</button>
            </form>
        </div>
        
        <%-- Placeholder for displaying search results or error messages --%>
        <%-- Example:
        <c:if test="${not empty searchResults}">
            <h2>Kết quả tìm kiếm:</h2>
            <table>
                <thead>
                    <tr>
                        <th>Tàu</th>
                        <th>Ga đi</th>
                        <th>Ga đến</th>
                        <th>Giờ đi</th>
                        <th>Giờ đến</th>
                        <th>Giá vé</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="trip" items="${searchResults}">
                        <tr>
                            <td>${trip.trainName}</td>
                            <td>${trip.originStation}</td>
                            <td>${trip.destinationStation}</td>
                            <td>${trip.departureTime}</td>
                            <td>${trip.arrivalTime}</td>
                            <td>${trip.price}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p style="color: red;">${errorMessage}</p>
        </c:if>
        --%>
    </div>

    <script src="${pageContext.request.contextPath}/js/search-trip.js"></script>
</body>
</html>
