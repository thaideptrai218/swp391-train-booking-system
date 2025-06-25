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
          <h2>Thống Kê Chung</h2>

          <c:if test="${not empty errorMessage}">
            <div class="error-message">
              <p><c:out value="${errorMessage}" /></p>
            </div>
          </c:if>

          <div class="stats-cards">
            <div class="stat-card">
              <h3>Tổng Lượt Đặt Vé</h3>
              <p><c:out value="${totalTicketsSold}" default="0" /> lượt</p>
            </div>
            <div class="stat-card">
              <h3>Tổng Số Đoàn Tàu</h3>
              <p><c:out value="${totalTrains}" default="0" /> tàu</p>
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

          <div
            class="charts-row"
            style="
              display: flex;
              gap: 20px;
              margin-bottom: 20px;
              width: 100%;
              box-sizing: border-box;
            "
          >
            <div class="sales-by-week" style="flex: 1; min-width: 0">
              <h3>Doanh Thu Theo Tuần</h3>
              <c:choose>
                <c:when test="${not empty salesByWeekData}">
                  <div
                    class="chart-container"
                    style="position: relative; height: 320px"
                  >
                    <canvas id="salesByWeekChart"></canvas>
                  </div>
                </c:when>
                <c:otherwise>
                  <p>Không có dữ liệu doanh thu theo tuần.</p>
                </c:otherwise>
              </c:choose>
            </div>

            <div class="sales-by-month-year" style="flex: 1; min-width: 0">
              <h3>Doanh Thu Theo Tháng</h3>
              <c:choose>
                <c:when test="${not empty salesByMonthYearData}">
                  <div
                    class="chart-container"
                    style="position: relative; height: 320px"
                  >
                    <canvas id="salesByMonthYearChart"></canvas>
                  </div>
                </c:when>
                <c:otherwise>
                  <p>Không có dữ liệu doanh thu theo tháng.</p>
                </c:otherwise>
              </c:choose>
            </div>
          </div>

          <div class="best-sellers">
            <h3>Tuyến Đường Bán Chạy Nhất</h3>
            <c:choose>
              <c:when test="${not empty bestSellerLocations}">
                <div
                  class="chart-container"
                  style="position: relative; height: 40vh; width: 100%"
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

          <div class="popular-stations-row">
            <div class="popular-stations-origin">
              <h3>Ga Khởi Hành Phổ Biến Nhất</h3>
              <c:choose>
                <c:when test="${not empty mostCommonOriginStations}">
                  <div
                    class="chart-container"
                    style="position: relative; height: 40vh; width: 100%"
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
                    style="position: relative; height: 40vh; width: 100%"
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
          <%-- All Tickets Table Section and Booking Trend Chart Section are
          removed --%>
        </section>
      </div>
    </div>

    <script type="text/javascript">
      var salesByWeekDataForChart;
      <c:choose>
        <c:when test="${not empty salesByWeekJson}">
          salesByWeekDataForChart = ${salesByWeekJson};
        </c:when>
        <c:otherwise>salesByWeekDataForChart = null;</c:otherwise>
      </c:choose>;

      var salesByMonthYearDataForChart;
      <c:choose>
        <c:when test="${not empty salesByMonthYearJson}">
          salesByMonthYearDataForChart = ${salesByMonthYearJson};
        </c:when>
        <c:otherwise>salesByMonthYearDataForChart = null;</c:otherwise>
      </c:choose>;
    </script>
    <script src="${pageContext.request.contextPath}/js/manager/manager-dashboard.js"></script>
  </body>
</html>
