<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Manager Home</title>
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
  </head>
  <body>
    <div class="dashboard-container">
      <%@ include file="sidebar.jsp" %>

      <div class="main-content">
        <header class="dashboard-header">
          <h1>Chào Mừng Đến Với Trang Quản Lý!</h1>
        </header>

        <section class="statistics-section">
          <h2>Thống Kê Doanh Thu</h2>

          <c:if test="${not empty errorMessage}">
            <div class="error-message">
              <p><c:out value="${errorMessage}" /></p>
            </div>
          </c:if>

          <div class="stats-cards">
            <div class="stat-card">
              <h3>Tổng Vé Đã Bán</h3>
              <p><c:out value="${totalTicketsSold}" default="0" /> vé</p>
            </div>
            <div class="stat-card">
              <h3>Tổng Doanh Thu</h3>
              <p>
                <fmt:formatNumber
                  value="${totalRevenue}"
                  type="currency"
                  currencyCode="VND"
                  minFractionDigits="0"
                />
              </p>
            </div>
          </div>

          <div class="best-sellers">
            <h3>Tuyến Đường Bán Chạy Nhất</h3>
            <c:choose>
              <c:when test="${not empty bestSellerLocations}">
                <div
                  class="chart-container"
                  style="position: relative; height: 40vh; width: 80vw"
                >
                  <canvas
                    id="bestSellersChart"
                    data-labels='[<c:forEach var="location" items="${bestSellerLocations}" varStatus="loop">"${fn:escapeXml(location.locationName)}"<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                    data-values='[<c:forEach var="location" items="${bestSellerLocations}" varStatus="loop">${location.ticketsSold}<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                  ></canvas>
                </div>
              </c:when>
              <c:otherwise>
                <p>Không có dữ liệu về tuyến đường bán chạy.</p>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="popular-stations-origin">
            <h3>Ga Khởi Hành Phổ Biến Nhất</h3>
            <c:choose>
              <c:when test="${not empty mostCommonOriginStations}">
                <div
                  class="chart-container"
                  style="position: relative; height: 40vh; width: 80vw"
                >
                  <canvas
                    id="popularOriginStationsChart"
                    data-labels='[<c:forEach var="station" items="${mostCommonOriginStations}" varStatus="loop">"${fn:escapeXml(station.stationName)}"<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                    data-values='[<c:forEach var="station" items="${mostCommonOriginStations}" varStatus="loop">${station.count}<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                  ></canvas>
                </div>
              </c:when>
              <c:otherwise>
                <p>Không có dữ liệu về ga khởi hành phổ biến.</p>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="popular-stations-destination">
            <h3>Ga Đến Phổ Biến Nhất</h3>
            <c:choose>
              <c:when test="${not empty mostCommonDestinationStations}">
                <div
                  class="chart-container"
                  style="position: relative; height: 40vh; width: 80vw"
                >
                  <canvas
                    id="popularDestinationStationsChart"
                    data-labels='[<c:forEach var="station" items="${mostCommonDestinationStations}" varStatus="loop">"${fn:escapeXml(station.stationName)}"<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                    data-values='[<c:forEach var="station" items="${mostCommonDestinationStations}" varStatus="loop">${station.count}<c:if test="${not loop.last}">, </c:if></c:forEach>]'
                  ></canvas>
                </div>
              </c:when>
              <c:otherwise>
                <p>Không có dữ liệu về ga đến phổ biến.</p>
              </c:otherwise>
            </c:choose>
          </div>

          <div class="popular-trips">
            <h3>Chuyến Đi Phổ Biến Nhất</h3>
            <c:choose>
              <c:when test="${not empty mostPopularTrips}">
                <div class="table-responsive-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Mã Chuyến</th>
                        <th>Tuyến Đường</th>
                        <th>Tên Tàu</th>
                        <th>Thời Gian Khởi Hành</th>
                        <th>Số Lượt Đặt</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="trip" items="${mostPopularTrips}">
                        <tr>
                          <td><c:out value="${trip.tripId}" /></td>
                          <td><c:out value="${trip.routeName}" /></td>
                          <td><c:out value="${trip.trainName}" /></td>
                          <td>
                            <fmt:formatDate
                              value="${trip.departureDateTimeAsDate}"
                              pattern="HH:mm dd/MM/yyyy"
                            />
                          </td>
                          <td><c:out value="${trip.bookingCount}" /></td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
              </c:when>
              <c:otherwise>
                <p>Không có dữ liệu về chuyến đi phổ biến.</p>
              </c:otherwise>
            </c:choose>
          </div>
        </section>
      </div>
    </div>
    <script src="${pageContext.request.contextPath}/js/manager/manager-dashboard.js"></script>
  </body>
</html>
