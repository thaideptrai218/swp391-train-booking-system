<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- For mock data --%>
<jsp:useBean id="mockOutboundTrips" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="mockReturnTrips" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="now" class="java.util.Date" />

<%--
    NOTE: The following 'c:set' blocks are for MOCK DATA for layout demonstration.
    This should be REMOVED when the SearchTripServlet provides actual 'outboundTrips', 
    'returnTrips', 'departureDateDisplay', 'originStationDisplay', 
    and 'destinationStationDisplay' attributes.
--%>
<c:if test="${empty outboundTrips and empty requestScope.outboundTripsFromServlet}">
    <c:set var="departureDateDisplay" value="2024-12-25" scope="request"/>
    <c:set var="originStationDisplay" value="Ga Hà Nội" scope="request"/>
    <c:set var="destinationStationDisplay" value="Ga Sài Gòn" scope="request"/>
    <c:set var="returnDateDisplay" value="2024-12-30" scope="request"/>

    <%-- Mock Outbound Trip 1 --%>
    <jsp:useBean id="mockTrip1" class="vn.vnrailway.dto.TripSearchResultDTO"/>
    <c:set target="${mockTrip1}" property="legType" value="Outbound"/>
    <c:set target="${mockTrip1}" property="tripId" value="101"/>
    <c:set target="${mockTrip1}" property="trainName" value="SE1"/>
    <c:set target="${mockTrip1}" property="routeName" value="Hà Nội - Sài Gòn"/>
    <c:set target="${mockTrip1}" property="originStationName" value="Ga Hà Nội"/>
    <c:set target="${mockTrip1}" property="scheduledDeparture" value="${now}"/>
    <c:set target="${mockTrip1}" property="destinationStationName" value="Ga Sài Gòn"/>
    <c:set target="${mockTrip1}" property="scheduledArrival" value="${now}"/>
    <c:set target="${mockTrip1}" property="durationMinutes" value="1200"/>
    <c:set target="${mockTrip1}" property="tripOverallDepartureTime" value="${now}"/>
    <c:set target="${mockTrip1}" property="tripOverallArrivalTime" value="${now}"/>
    <c:set target="${mockTrip1}" property="tripStatus" value="Scheduled"/>
    <c:set target="${mockTrip1}" property="trainId" value="1"/>
    <c:set target="${mockTrip1}" property="routeId" value="1"/>
    <c:set target="${mockTrip1}" property="originStationId" value="1"/>
    <c:set target="${mockTrip1}" property="destinationStationId" value="2"/>
    <c:set target="${mockOutboundTrips}" property="add" value="${mockTrip1}"/>

    <%-- Mock Outbound Trip 2 --%>
    <jsp:useBean id="mockTrip2" class="vn.vnrailway.dto.TripSearchResultDTO"/>
    <c:set target="${mockTrip2}" property="legType" value="Outbound"/>
    <c:set target="${mockTrip2}" property="tripId" value="102"/>
    <c:set target="${mockTrip2}" property="trainName" value="SE3"/>
    <c:set target="${mockTrip2}" property="routeName" value="Hà Nội - Sài Gòn"/>
    <c:set target="${mockTrip2}" property="originStationName" value="Ga Hà Nội"/>
    <c:set target="${mockTrip2}" property="scheduledDeparture" value="${now}"/>
    <c:set target="${mockTrip2}" property="destinationStationName" value="Ga Sài Gòn"/>
    <c:set target="${mockTrip2}" property="scheduledArrival" value="${now}"/>
    <c:set target="${mockTrip2}" property="durationMinutes" value="1250"/>
    <c:set target="${mockTrip2}" property="tripStatus" value="Scheduled"/>
    <c:set target="${mockOutboundTrips}" property="add" value="${mockTrip2}"/>
    <c:set var="outboundTrips" value="${mockOutboundTrips}" scope="request"/>

    <%-- Mock Return Trip 1 --%>
    <jsp:useBean id="mockReturnTrip1" class="vn.vnrailway.dto.TripSearchResultDTO"/>
    <c:set target="${mockReturnTrip1}" property="legType" value="Return"/>
    <c:set target="${mockReturnTrip1}" property="tripId" value="201"/>
    <c:set target="${mockReturnTrip1}" property="trainName" value="SE2"/>
    <c:set target="${mockReturnTrip1}" property="routeName" value="Sài Gòn - Hà Nội"/>
    <c:set target="${mockReturnTrip1}" property="originStationName" value="Ga Sài Gòn"/>
    <c:set target="${mockReturnTrip1}" property="scheduledDeparture" value="${now}"/>
    <c:set target="${mockReturnTrip1}" property="destinationStationName" value="Ga Hà Nội"/>
    <c:set target="${mockReturnTrip1}" property="scheduledArrival" value="${now}"/>
    <c:set target="${mockReturnTrip1}" property="durationMinutes" value="1220"/>
    <c:set target="${mockReturnTrip1}" property="tripStatus" value="Scheduled"/>
    <c:set target="${mockReturnTrips}" property="add" value="${mockReturnTrip1}"/>
    <c:set var="returnTrips" value="${mockReturnTrips}" scope="request"/>
</c:if>


<!DOCTYPE html>
<html>
<head>
    <title>Kết quả tìm kiếm chuyến tàu</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css" />
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/trip-result.css" />
    <%-- Consider adding Font Awesome CDN link if not globally available in your project --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

</head>
<body>
    <div class="page-wrapper container">
        <aside class="sidebar">
            <div class="shopping-cart-placeholder">
                <h3><i class="fas fa-shopping-cart"></i> Giỏ hàng</h3>
                <p>(Chưa có vé nào)</p>
            </div>
            <div class="mini-search-placeholder">
                <h3><i class="fas fa-search"></i> Tìm kiếm lại</h3>
                <p>(Form tìm kiếm thu gọn)</p>
                <%-- Placeholder for a mini search form --%>
            </div>
        </aside>

        <main class="main-results-content">
            <%-- Outbound Trips Section --%>
            <section class="trip-section outbound-section">
                <h2>Chiều đi: <c:out value="${departureDateDisplay}"/> từ <c:out value="${originStationDisplay}"/> đến <c:out value="${destinationStationDisplay}"/></h2>
                <c:choose>
                    <c:when test="${not empty outboundTrips}">
                        <div class="train-list">
                            <c:forEach var="trip" items="${outboundTrips}">
                                <div class="train-item" data-trip-id="${trip.tripId}" tabindex="0">
                                    <div class="train-item-collapsed-summary">
                                        <span class="train-name">${trip.trainName}</span>
                                        <span class="departure-info">
                                            <fmt:formatDate value="${trip.scheduledDepartureAsDate}" pattern="HH:mm" /> - ${trip.originStationName}
                                        </span>
                                        <span class="duration-arrow">
                                            <i class="fas fa-long-arrow-alt-right"></i> ${trip.durationMinutes} phút
                                        </span>
                                        <span class="arrival-info">
                                            <fmt:formatDate value="${trip.scheduledArrivalAsDate}" pattern="HH:mm" /> - ${trip.destinationStationName}
                                        </span>
                                        <span class="seats-info">
                                            Còn trống: -- <br/> Đã đặt: --
                                        </span>
                                    </div>
                                    <div class="expanded-details" style="display:none;">
                                        <div class="carriage-list">
                                            <c:forEach var="i" begin="1" end="15">
                                                <div class="carriage-item ${i == 1 ? 'active' : ''}" data-carriage-id="${i}" tabindex="0">
                                                    <i class="fas fa-train"></i> Toa ${i}
                                                </div>
                                            </c:forEach>
                                        </div>
                                        <div class="seat-details-block">
                                            <p>Sơ đồ ghế cho Toa 1.</p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="no-results">Không tìm thấy chuyến đi nào phù hợp.</p>
                    </c:otherwise>
                </c:choose>
            </section>

            <%-- Return Trips Section (Optional) --%>
            <c:if test="${not empty returnDateDisplay}">
                <section class="trip-section return-section">
                    <h2>Chiều về: <c:out value="${returnDateDisplay}"/> từ <c:out value="${destinationStationDisplay}"/> đến <c:out value="${originStationDisplay}"/></h2>
                    <c:choose>
                        <c:when test="${not empty returnTrips}">
                            <div class="train-list">
                                <c:forEach var="trip" items="${returnTrips}">
                                    <div class="train-item" data-trip-id="${trip.tripId}" tabindex="0">
                                    <div class="train-item-collapsed-summary">
                                        <span class="train-name">${trip.trainName}</span>
                                        <span class="departure-info">
                                            <fmt:formatDate value="${trip.scheduledDepartureAsDate}" pattern="HH:mm" /> - ${trip.originStationName}
                                        </span>
                                        <span class="duration-arrow">
                                            <i class="fas fa-long-arrow-alt-right"></i> ${trip.durationMinutes} phút
                                        </span>
                                        <span class="arrival-info">
                                            <fmt:formatDate value="${trip.scheduledArrivalAsDate}" pattern="HH:mm" /> - ${trip.destinationStationName}
                                        </span>
                                        <span class="seats-info">
                                                Còn trống: -- <br/> Đã đặt: --
                                            </span>
                                        </div>
                                        <div class="expanded-details" style="display:none;">
                                            <div class="carriage-list">
                                                <c:forEach var="i" begin="1" end="15">
                                                    <div class="carriage-item ${i == 1 ? 'active' : ''}" data-carriage-id="${i}" tabindex="0">
                                                        <i class="fas fa-train"></i> Toa ${i}
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <div class="seat-details-block">
                                                <p>Sơ đồ ghế cho Toa 1.</p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-results">Không tìm thấy chuyến về nào phù hợp.</p>
                        </c:otherwise>
                    </c:choose>
                </section>
            </c:if>
        </main>
    </div>

    <script src="${pageContext.request.contextPath}/js/trip/trip-result.js"></script>
</body>
</html>
