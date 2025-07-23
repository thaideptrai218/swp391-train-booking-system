<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Manage Trips</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/trips/manageTrips.css"/>
    <script src="${pageContext.request.contextPath}/js/manager/trips/manageTrips.js" defer></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
  </head>
  <body>
    <form id="sortForm" method="POST" action="${pageContext.request.contextPath}/manageTrips">
      <input type="hidden" name="action" value="list" />
      <input type="hidden" name="sortField" value="${requestScope.currentSortField}" />
      <input type="hidden" name="sortOrder" value="${requestScope.currentSortOrder}" />
      <input type="hidden" name="searchTerm" value="${requestScope.searchTerm}" />
      <input type="hidden" name="currentSortField" value="${requestScope.currentSortField}" />
    </form>
    <div class="dashboard-container">
      <jsp:include page="../sidebar.jsp" />
      <div class="main-content">
        <header class="dashboard-header"><h1>Manage Trips</h1></header>
        <section class="content-section">
          <div class="controls-container">
            <div class="search-container">
              <input type="text" id="tripSearchInput" placeholder="Tìm theo Mã chuyến đi, Tên tuyến...">
            </div>
            <a href="${pageContext.request.contextPath}/manageTrips?action=showAddForm" class="btn btn-primary action-add">
              <i class="fas fa-plus-circle"></i> Thêm Chuyến Đi Mới
            </a>
          </div>

          <c:if test="${not empty requestScope.successMessage || not empty sessionScope.successMessage}">
            <p class="message success-message"><c:out value="${requestScope.successMessage}${sessionScope.successMessage}"/></p>
            <c:remove var="successMessage" scope="session"/>
          </c:if>
          <c:if test="${not empty requestScope.errorMessage || not empty sessionScope.errorMessage}">
            <p class="message error-message"><c:out value="${requestScope.errorMessage}${sessionScope.errorMessage}"/></p>
            <c:remove var="errorMessage" scope="session"/>
          </c:if>

          <div class="table-container">
            <table id="tripsTable">
              <thead>
                <tr>
                  <th><a href="javascript:void(0);" onclick="submitSort('tripID', '${requestScope.currentSortOrder}')">Mã chuyến đi<c:if test="${requestScope.currentSortField == 'tripID'}"><span class="sort-indicator">${requestScope.currentSortOrder == 'ASC' ? '▲' : '▼'}</span></c:if></a></th>
                  <th><a href="javascript:void(0);" onclick="submitSort('routeName', '${requestScope.currentSortOrder}')">Tên tuyến<c:if test="${requestScope.currentSortField == 'routeName'}"><span class="sort-indicator">${requestScope.currentSortOrder == 'ASC' ? '▲' : '▼'}</span></c:if></a></th>
                  <th><a href="javascript:void(0);" onclick="submitSort('isHolidayTrip', '${requestScope.currentSortOrder}')">Ngày Lễ<c:if test="${requestScope.currentSortField == 'isHolidayTrip'}"><span class="sort-indicator">${requestScope.currentSortOrder == 'ASC' ? '▲' : '▼'}</span></c:if></a></th>
                  <th><a href="javascript:void(0);" onclick="submitSort('basePriceMultiplier', '${requestScope.currentSortOrder}')">Hệ Số Tiền<c:if test="${requestScope.currentSortField == 'basePriceMultiplier'}"><span class="sort-indicator">${requestScope.currentSortOrder == 'ASC' ? '▲' : '▼'}</span></c:if></a></th>
                  <th><a href="javascript:void(0);" onclick="submitSort('tripStatus', '${requestScope.currentSortOrder}')">Trạng thái<c:if test="${requestScope.currentSortField == 'tripStatus'}"><span class="sort-indicator">${requestScope.currentSortOrder == 'ASC' ? '▲' : '▼'}</span></c:if></a></th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${not empty listTrips}">
                    <c:forEach var="trip" items="${listTrips}">
                      <tr>
                        <td>${trip.tripID}</td>
                        <td><c:out value="${trip.routeName}" /></td>
                        <td>
                          <form action="${pageContext.request.contextPath}/manageTrips" method="POST" style="display: inline-flex; align-items: center; gap: 5px; margin: 0;">
                            <input type="hidden" name="action" value="updateHolidayStatus" />
                            <input type="hidden" name="tripId" value="${trip.tripID}" />
                            <input type="hidden" name="isHolidayTrip" id="isHolidayTrip_${trip.tripID}" value="${trip.holidayTrip ? 1 : 0}" />
                            <input type="hidden" name="basePriceMultiplier" id="basePriceMultiplier_${trip.tripID}" value="${trip.basePriceMultiplier}" />
                            <select id="holidayYesNo_${trip.tripID}" class="form-control-sm holiday-yesno-select" data-trip-id="${trip.tripID}">
                              <option value="0" ${!trip.holidayTrip ? 'selected' : ''}>Không</option>
                              <option value="1" ${trip.holidayTrip ? 'selected' : ''}>Có</option>
                            </select>
                            <select id="holidaySelect_${trip.tripID}" class="form-control-sm holiday-select" data-trip-id="${trip.tripID}" style="display: '${trip.holidayTrip ? 'inline-block' : 'none'};'">
                              <option value="">-- Chọn ngày lễ --</option>
                              <c:forEach var="holiday" items="${allHolidays}">
                                <option value="${holiday.discountPercentage}" ${trip.basePriceMultiplier == (holiday.discountPercentage / 100) ? 'selected' : ''}>${holiday.holidayName}</option>
                              </c:forEach>
                            </select>
                            <button type="submit" class="btn btn-primary btn-sm">Lưu</button>
                          </form>
                        </td>
                        <td>
                          <input type="number" id="basePriceMultiplierDisplay_${trip.tripID}" class="form-control-sm" value="${trip.basePriceMultiplier}" step="0.01" min="0" readonly style="width: 80px;"/>
                        </td>
                        <td>
                          <form action="${pageContext.request.contextPath}/manageTrips" method="POST" style="display: inline-flex; align-items: center; gap: 5px; margin: 0;">
                            <input type="hidden" name="action" value="updateTripStatus" />
                            <input type="hidden" name="tripId" value="${trip.tripID}" />
                            <select name="tripStatus" class="form-control-sm" onchange="this.form.submit()">
                              <option value="Scheduled" ${trip.tripStatus == 'Scheduled' ? 'selected' : ''}>Lên Lịch</option>
                              <option value="In Progress" ${trip.tripStatus == 'In Progress' ? 'selected' : ''}>Đang Diễn Ra</option>
                              <option value="Completed" ${trip.tripStatus == 'Completed' ? 'selected' : ''}>Đã Hoàn Thành</option>
                              <option value="Cancelled" ${trip.tripStatus == 'Cancelled' ? 'selected' : ''}>Hủy Chuyến</option>
                            </select>
                          </form>
                        </td>
                        <td class="table-actions">
                          <a href="${pageContext.request.contextPath}/tripDetail?tripId=${trip.tripID}" class="action-view-more" title="Xem chi tiết"><i class="fas fa-eye"></i> Xem thêm</a>
                          <form action="${pageContext.request.contextPath}/manageTrips" method="POST" style="display: inline-block; margin-left: 8px;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa chuyến đi này không?');">
                            <input type="hidden" name="action" value="deleteTrip" />
                            <input type="hidden" name="tripId" value="${trip.tripID}" />
                            <button type="submit" class="action-delete" title="Xóa chuyến đi"><i class="fas fa-trash-alt"></i> Xóa</button>
                          </form>
                        </td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr><td colspan="6">No trips found.</td></tr>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
          <div class="pagination-container" id="pagination-container"></div>
        </section>
      </div>
    </div>
  </body>
</html>
