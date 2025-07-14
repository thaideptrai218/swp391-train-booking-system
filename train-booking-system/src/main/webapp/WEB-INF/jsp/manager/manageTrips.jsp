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
      .pagination-container {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;
      }
      .pagination-container .page-link {
        padding: 8px 12px;
        margin: 0 4px;
        border: 1px solid #ddd;
        background-color: #fff;
        color: #007bff;
        cursor: pointer;
        text-decoration: none;
        border-radius: 4px;
        transition: background-color 0.3s, color 0.3s;
      }
      .pagination-container .page-link.active,
      .pagination-container .page-link:hover {
        background-color: #007bff;
        color: #fff;
        border-color: #007bff;
      }
      .pagination-container .page-link.disabled {
        color: #ccc;
        cursor: not-allowed;
        background-color: #f9f9f9;
        border-color: #ddd;
      }
    </style>
    <script type="text/javascript">
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
        
        const isHolidayInput = document.createElement('input');
        isHolidayInput.type = 'hidden';
        isHolidayInput.name = 'isHolidayTripHidden';
        isHolidayInput.value = holidaySelect.value;
        form.appendChild(isHolidayInput);

        document.body.appendChild(form);
        form.submit();
      }

      function removeDiacritics(str) {
          return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
      }

      document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("tripSearchInput");
        const table = document.getElementById("tripsTable");
        const allRows = Array.from(table.querySelectorAll("tbody tr"));
        const noTripsRow = allRows.find(row => row.querySelector("td[colspan='6']"));
        const dataRows = allRows.filter(row => row !== noTripsRow);
        const paginationContainer = document.getElementById("pagination-container");
        const rowsPerPage = 10;
        let currentPage = 1;
        let filteredRows = dataRows;

        function displayRows(page) {
            currentPage = page;
            const start = (page - 1) * rowsPerPage;
            const end = start + rowsPerPage;

            // Hide all data rows first
            dataRows.forEach(row => row.style.display = 'none');

            // Show only the rows for the current page from the filtered list
            const rowsToShow = filteredRows.slice(start, end);
            rowsToShow.forEach(row => row.style.display = '');

            // Handle the "No trips found" message
            if (noTripsRow) {
                noTripsRow.style.display = filteredRows.length === 0 ? '' : 'none';
            }
        }

        function setupPagination() {
            paginationContainer.innerHTML = "";
            const pageCount = Math.ceil(filteredRows.length / rowsPerPage);

            if (pageCount <= 1) return;

            // Previous button
            const prevLink = document.createElement('a');
            prevLink.href = '#';
            prevLink.innerHTML = '&laquo;';
            prevLink.classList.add('page-link');
            if (currentPage === 1) {
                prevLink.classList.add('disabled');
            }
            prevLink.addEventListener('click', (e) => {
                e.preventDefault();
                if (currentPage > 1) {
                    displayRows(currentPage - 1);
                    setupPagination();
                }
            });
            paginationContainer.appendChild(prevLink);

            // Page number links
            for (let i = 1; i <= pageCount; i++) {
                const pageLink = document.createElement('a');
                pageLink.href = '#';
                pageLink.innerText = i;
                pageLink.classList.add('page-link');
                if (i === currentPage) {
                    pageLink.classList.add('active');
                }
                pageLink.addEventListener('click', (e) => {
                    e.preventDefault();
                    displayRows(i);
                    setupPagination();
                });
                paginationContainer.appendChild(pageLink);
            }

            // Next button
            const nextLink = document.createElement('a');
            nextLink.href = '#';
            nextLink.innerHTML = '&raquo;';
            nextLink.classList.add('page-link');
            if (currentPage === pageCount) {
                nextLink.classList.add('disabled');
            }
            nextLink.addEventListener('click', (e) => {
                e.preventDefault();
                if (currentPage < pageCount) {
                    displayRows(currentPage + 1);
                    setupPagination();
                }
            });
            paginationContainer.appendChild(nextLink);
        }

        function filterAndPaginate() {
            const searchTerm = removeDiacritics(searchInput.value.toLowerCase().trim().replace(/\s+/g, " "));
            
            filteredRows = dataRows.filter(row => {
                const tripIdCell = removeDiacritics(row.cells[0].textContent.toLowerCase());
                const routeNameCell = removeDiacritics(row.cells[1].textContent.toLowerCase());
                return tripIdCell.includes(searchTerm) || routeNameCell.includes(searchTerm);
            });

            displayRows(1); // Reset to first page
            setupPagination();
        }

        if (searchInput) {
            searchInput.addEventListener("keyup", filterAndPaginate);
        }

        // Initial setup
        filterAndPaginate();
      });
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
                            <select name="isHolidayTrip" id="isHolidayTrip_${trip.tripID}" class="form-control-sm" onchange="handleHolidayChange(this, '${trip.tripID}')">
                              <option value="true" ${trip.holidayTrip ? 'selected' : ''}>Có</option>
                              <option value="false" ${!trip.holidayTrip ? 'selected' : ''}>Không</option>
                            </select>
                          </form>
                        </td>
                        <td>
                           <input type="number" id="basePriceMultiplierDisplay_${trip.tripID}" class="form-control-sm" 
                                  value="${trip.basePriceMultiplier}" 
                                  step="0.01" min="0" 
                                  ${!trip.holidayTrip ? 'readonly' : ''} style="width: 80px;"/>
                           <c:set var="buttonDisplayStyleValue" value="${trip.holidayTrip ? 'inline-flex' : 'none'}" />
                           <button type="button" id="saveMultiplierBtn_${trip.tripID}" class="btn-update-multiplier" 
                                   onclick="submitMultiplierForm('${trip.tripID}')" 
                                   style="display: buttonDisplayStyleValue;">
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
          <div class="pagination-container" id="pagination-container"></div>
        </section>
      </div>
    </div>
  </body>
</html>
