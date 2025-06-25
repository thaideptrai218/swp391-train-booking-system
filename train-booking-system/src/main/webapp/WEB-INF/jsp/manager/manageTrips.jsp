<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Manage Trips</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/manageTrips.css"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"/>
    <style>
      body { font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; color: #333; line-height: 1.6; margin: 0; }
      .dashboard-container { display: flex; }
      .main-content { flex-grow: 1; padding: 25px; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05); margin: 20px; }
      .dashboard-header h1 { color: #343a40; margin-bottom: 25px; font-weight: 400; border-bottom: 1px solid #e9ecef; padding-bottom: 15px; font-size: 1.8em; }
      .message { padding: 12px 18px; margin-bottom: 20px; border-radius: 5px; font-size: 0.95em; }
      .error-message { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
      .success-message { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
      .info-message { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }
      .table-container table { width: 100%; border-collapse: collapse; margin-top: 20px; background-color: #fff; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); }
      .table-container th, .table-container td { padding: 12px 15px; text-align: left; border: 1px solid #ddd; }
      .table-container th { background-color: #f0f2f5; color: #333; font-weight: 600; font-size: 0.9em; }
      .table-container tbody tr:nth-child(even) { background-color: #f9f9f9; }
      .table-container tbody tr:hover { background-color: #e9ecef; }
      .table-actions a, .table-actions button { display: inline-flex; align-items: center; padding: 6px 10px; color: white; border-radius: 4px; text-decoration: none; font-size: 0.85em; transition: background-color 0.2s ease; border: none; cursor: pointer; margin-right: 5px; }
      .table-actions a.action-view-more { background-color: #17a2b8; }
      .table-actions a.action-view-more:hover { background-color: #138496; }
      .table-actions .action-delete { background-color: #dc3545; }
      .table-actions .action-delete:hover { background-color: #c82333; }
      .table-actions .fas { margin-right: 5px; }
      td form { margin: 0; padding: 0; display: inline-flex; align-items: center; gap: 5px;}
      .form-control-sm { padding: .25rem .5rem; font-size: .875rem; line-height: 1.5; border-radius: .2rem; border: 1px solid #ced4da; }
      .sort-indicator { margin-left: 5px; font-size: 0.8em; }
      th a { text-decoration: none; color: inherit; }
      th a:hover { text-decoration: underline; }
      .btn-update-multiplier {
        padding: .25rem .5rem; 
        font-size: .80rem; 
        line-height: 1.2; 
        border-radius: .2rem; 
        color: #fff;
        background-color: #007bff; 
        border-color: #007bff; 
        border: 1px solid transparent; 
        cursor: pointer;
        display: inline-flex; 
        align-items: center; 
        margin-left: 5px;
      }
      .btn-update-multiplier:hover { 
        background-color: #0056b3; 
        border-color: #0052cc;
      }
      .btn-update-multiplier .fas { 
        margin-right: 3px;
      }
    </style>
    <script>
      function submitSort(sortField, currentSortOrder) {
        const form = document.getElementById("sortForm");
        form.elements["sortField"].value = sortField;
        if (form.elements["currentSortField"].value === sortField && currentSortOrder === "ASC") {
          form.elements["sortOrder"].value = "DESC";
        } else {
          form.elements["sortOrder"].value = "ASC";
        }
        form.elements["currentSortField"].value = sortField;
        form.submit();
      }

      function handleHolidayChange(selectElement, tripId) {
        const multiplierDisplayInput = document.getElementById('basePriceMultiplierDisplay_' + tripId);
        const saveMultiplierButton = document.getElementById('saveMultiplierBtn_' + tripId);
        const holidayForm = selectElement.form; // The form containing the isHolidayTrip select

        if (selectElement.value === 'true') {
          multiplierDisplayInput.readOnly = false;
          if(saveMultiplierButton) saveMultiplierButton.style.display = 'inline-flex';
        } else {
          multiplierDisplayInput.readOnly = true;
          multiplierDisplayInput.value = '1.00'; 
          if(saveMultiplierButton) saveMultiplierButton.style.display = 'none';
        }
        // This form should ONLY submit the holiday status.
        // The multiplier is handled by its own save button.
        // If changing to "Không" (false) should auto-reset multiplier in DB, that logic is in servlet.
        holidayForm.elements['action'].value = 'updateHolidayStatus';
        holidayForm.submit();
      }
      
      function submitMultiplierForm(tripId) {
        const holidaySelect = document.getElementById('isHolidayTrip_' + tripId);
        const multiplierDisplay = document.getElementById('basePriceMultiplierDisplay_' + tripId);
        
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/manageTrips';

        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'updateBasePriceMultiplier';
        form.appendChild(actionInput);

        const tripIdInput = document.createElement('input');
        tripIdInput.type = 'hidden';
        tripIdInput.name = 'tripId';
        tripIdInput.value = tripId;
        form.appendChild(tripIdInput);

        const multiplierInput = document.createElement('input');
        multiplierInput.type = 'hidden';
        multiplierInput.name = 'basePriceMultiplier';
        multiplierInput.value = multiplierDisplay.value;
        form.appendChild(multiplierInput);
        
        // Include isHolidayTrip status as it might be needed by servlet logic
        const isHolidayInput = document.createElement('input');
        isHolidayInput.type = 'hidden';
        isHolidayInput.name = 'isHolidayTripHidden'; // Use a different name to avoid conflict if needed
        isHolidayInput.value = holidaySelect.value;
        form.appendChild(isHolidayInput);

        document.body.appendChild(form);
        form.submit();
      }
    </script>
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
      <%@ include file="sidebar.jsp" %>
      <div class="main-content">
        <header class="dashboard-header"><h1>Manage Trips</h1></header>
        <section class="content-section">
          <div class="controls-container" style="margin-bottom: 20px; text-align: right;">
            <a href="${pageContext.request.contextPath}/manageTrips?action=showAddForm" class="btn btn-primary" style="padding: 8px 15px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px;">
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
            <table>
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
                            <select name="isHolidayTrip" id="isHolidayTrip_${trip.tripID}" class="form-control-sm" onchange="handleHolidayChange(this, '${trip.tripID}')">
                              <option value="true" ${trip.holidayTrip ? 'selected' : ''}>Có</option>
                              <option value="false" ${!trip.holidayTrip ? 'selected' : ''}>Không</option>
                            </select>
                          </form>
                        </td>
                        <td>
                           <input type="number" id="basePriceMultiplierDisplay_${trip.tripID}" class="form-control-sm" 
                                  value="<fmt:formatNumber value="${trip.basePriceMultiplier}" pattern="#0.00" minFractionDigits="2" maxFractionDigits="2"/>" 
                                  step="0.01" min="0" 
                                  ${!trip.holidayTrip ? 'readonly' : ''} style="width: 80px;"/>
                           <button type="button" id="saveMultiplierBtn_${trip.tripID}" class="btn-update-multiplier" 
                                   onclick="submitMultiplierForm('${trip.tripID}')" 
                                   style="display: ${trip.holidayTrip ? 'inline-flex' : 'none'};">
                               <i class="fas fa-save"></i> Lưu
                           </button>
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
        </section>
      </div>
    </div>
  </body>
</html>
