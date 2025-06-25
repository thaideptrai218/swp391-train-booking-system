<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %> <%-- Added for LocalTime formatting --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Trip Station Details</title>
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
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <%-- You might want to create a specific CSS file for tripDetail.css --%>
    <%--
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/tripDetail.css"
    />
    --%>
    <style>
      /* General body and container styling (optional, adjust as needed) */
      body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6; 
        color: #333;
        line-height: 1.6;
        margin: 0; 
      }
      .dashboard-container {
        display: flex; 
      }
      .main-content {
        flex-grow: 1; 
        padding: 25px;
        background-color: #ffffff; 
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        margin: 20px;
      }
      .dashboard-header h1 {
        color: #343a40;
        margin-bottom: 25px;
        font-weight: 400;
        border-bottom: 1px solid #e9ecef;
        padding-bottom: 15px;
        font-size: 1.8em;
      }
      /* Message styling */
      .message {
        padding: 12px 18px;
        margin-bottom: 20px;
        border-radius: 5px;
        font-size: 0.95em;
      }
      .error-message {
        background-color: #f8d7da;
        color: #721c24;
        border: 1px solid #f5c6cb;
      }
      .info-message {
        background-color: #d1ecf1;
        color: #0c5460;
        border: 1px solid #bee5eb;
      }

      /* Table specific styling */
      .table-container {
        margin-top: 20px;
      }
      .table-container table {
        width: 100%;
        border-collapse: collapse; 
        margin-top: 20px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); 
      }
      .table-container th,
      .table-container td {
        padding: 12px 15px; 
        text-align: left;
        border: 1px solid #ddd; /* Clear borders for all cells */
      }
      .table-container th {
        background-color: #f0f2f5; 
        color: #333; 
        font-weight: 600;
        font-size: 0.9em;
      }
      .table-container tbody tr:nth-child(even) {
        background-color: #f9f9f9; /* Zebra striping */
      }
      .table-container tbody tr:hover {
        background-color: #e9ecef; 
      }
      .action-button {
        display: inline-flex; /* Align icon and text */
        align-items: center;
        padding: 8px 15px;
        margin-top: 25px;
        background-color: #6c757d; /* Secondary/Gray color for back button */
        color: white;
        border-radius: 4px;
        text-decoration: none;
        transition: background-color 0.2s ease;
        font-size: 0.9em;
        border: none;
      }
      .action-button:hover {
        background-color: #5a6268; /* Darker gray */
      }
      .action-button .fas {
        margin-right: 6px; /* Space between icon and text */
      }
      .form-control-sm { /* Basic styling for date/time inputs */
        padding: .25rem .5rem;
        font-size: .875rem;
        line-height: 1.5;
        border-radius: .2rem;
        border: 1px solid #ced4da;
        margin-right: 5px; /* Add some space if inputs are side-by-side in future */
      }
      .btn-update-time {
        padding: .25rem .5rem;
        font-size: .875rem;
        line-height: 1.5;
        border-radius: .2rem;
        color: #fff;
        background-color: #28a745; /* Success green */
        border-color: #28a745;
        border: 1px solid transparent;
        cursor: pointer;
        display: inline-flex;
        align-items: center;
      }
      .btn-update-time:hover {
        background-color: #218838;
        border-color: #1e7e34;
      }
      .btn-update-time .fas {
        margin-right: 4px;
      }
    </style>
  </head>
  <body>
  <body>
    <div class="dashboard-container">
      <%@ include file="sidebar.jsp" %>
      <div class="main-content">
        <header class="dashboard-header">
          <h1>
            Chi tiết trạm dừng cho Mã chuyến đi:
            <c:out value="${tripIdForDetail}" />
          </h1>
        </header>

        <section class="content-section">
          <%-- Display success/error messages from session --%>
          <c:if test="${not empty sessionScope.successMessage}">
            <p class="message success-message"><c:out value="${sessionScope.successMessage}"/></p>
            <c:remove var="successMessage" scope="session"/>
          </c:if>
          <c:if test="${not empty sessionScope.errorMessage}">
            <p class="message error-message"><c:out value="${sessionScope.errorMessage}"/></p>
            <c:remove var="errorMessage" scope="session"/>
          </c:if>
          
          <%-- Display messages from request (e.g., if no tripId provided on initial GET) --%>
          <c:if test="${not empty requestScope.errorMessage}">
            <p class="message error-message">
              <c:out value="${requestScope.errorMessage}" />
            </p>
          </c:if>
          <c:if test="${not empty requestScope.message && empty sessionScope.successMessage && empty sessionScope.errorMessage}">
            <p class="message info-message"><c:out value="${requestScope.message}" /></p>
          </c:if>

          <c:if test="${not empty tripStationDetails}">
            <div class="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Tên ga</th>
                    <th>Khoảng cách từ đầu (km)</th>
                    <th>Ngày khởi hành dự kiến</th>
                    <th>Giờ khởi hành dự kiến</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="stationDetail" items="${tripStationDetails}" varStatus="loop">
                    <tr 
                        data-station-id="${stationDetail.stationId}"
                        data-estimate-time="${stationDetail.estimateTime}" 
                        data-default-stop-time="${stationDetail.defaultStopTime}"
                        class="station-row">
                      <form action="${pageContext.request.contextPath}/tripDetail" method="POST">
                        <input type="hidden" name="tripId" value="${stationDetail.tripID}" />
                        <input type="hidden" name="stationId" value="${stationDetail.stationId}" />
                        <input type="hidden" name="originalTripId" value="${tripIdForDetail}" /> <%-- For redirect --%>

                        <td><c:out value="${stationDetail.stationName}" /></td>
                        <td>
                          <fmt:formatNumber
                            value="${stationDetail.distanceFromStart}"
                            pattern="#,##0.##"
                          />
                        </td>
                        <td>
                          <input type="date" name="scheduledDepartureDate" 
                                 id="departureDate-${loop.index}" 
                                 value="${stationDetail.scheduledDepartureDate}" required 
                                 class="form-control-sm departure-date" 
                                 <c:if test="${loop.index != 0}">disabled</c:if> />
                        </td>
                        <td>
                          <input type="time" name="scheduledDepartureTime" 
                                 id="departureTime-${loop.index}" 
                                 value="${stationDetail.scheduledDepartureTime != null ? stationDetail.scheduledDepartureTime.format(DateTimeFormatter.ofPattern('HH:mm')) : ''}" required 
                                 class="form-control-sm departure-time" step="60" <%-- Step 60 for minute precision --%>
                                 <c:if test="${loop.index != 0}">disabled</c:if> />
                        </td>
                        <td>
                          <c:if test="${loop.index == 0}">
                            <button type="submit" class="btn-update-time">
                              <i class="fas fa-save"></i> Lưu
                            </button>
                          </c:if>
                          <%-- For other rows, this cell will be empty --%>
                        </td>
                      </form>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:if>
          <a
            href="${pageContext.request.contextPath}/manageTrips"
            class="action-button"
            ><i class="fas fa-arrow-left"></i> Quay lại quản lý chuyến đi</a 
          >
        </section>
      </div>
    </div>

    <script>
      document.addEventListener('DOMContentLoaded', function () {
        const stationRows = document.querySelectorAll('.station-row');
        if (stationRows.length === 0) return;

        const firstDepartureDateInput = document.getElementById('departureDate-0');
        const firstDepartureTimeInput = document.getElementById('departureTime-0');

        function calculateScheduledTimes() {
          if (!firstDepartureDateInput.value || !firstDepartureTimeInput.value) {
            // Don't calculate if the first station's date or time is not set
            return;
          }

          let previousDepartureDateTime = new Date(firstDepartureDateInput.value + 'T' + firstDepartureTimeInput.value);
          let previousEstimateTimeHours = parseFloat(stationRows[0].dataset.estimateTime) || 0;

          for (let i = 1; i < stationRows.length; i++) {
            const currentRow = stationRows[i];
            const currentEstimateTimeHours = parseFloat(currentRow.dataset.estimateTime);
            const defaultStopTimeMinutes = parseInt(currentRow.dataset.defaultStopTime, 10) || 0;

            const dateInput = document.getElementById('departureDate-' + i);
            const timeInput = document.getElementById('departureTime-' + i);

            if (isNaN(currentEstimateTimeHours)) {
                console.warn('Invalid estimateTime for station index ' + i + '. Skipping calculation for this and subsequent stations.');
                // Clear subsequent fields if data is missing
                for (let j = i; j < stationRows.length; j++) {
                    document.getElementById('departureDate-' + j).value = '';
                    document.getElementById('departureTime-' + j).value = '';
                }
                return; 
            }
            
            // Calculate travel time from previous station to current station in milliseconds
            // estimateTime is total time from start of route
            const travelTimeHours = currentEstimateTimeHours - previousEstimateTimeHours;
            const travelTimeMilliseconds = travelTimeHours * 60 * 60 * 1000;

            // Calculate arrival time at current station
            let arrivalAtCurrentStation = new Date(previousDepartureDateTime.getTime() + travelTimeMilliseconds);

            // Calculate departure time from current station
            let departureFromCurrentStation = new Date(arrivalAtCurrentStation.getTime() + (defaultStopTimeMinutes * 60 * 1000));

            // Format and set the date and time for the current station
            dateInput.value = departureFromCurrentStation.toISOString().split('T')[0];
            timeInput.value = departureFromCurrentStation.toTimeString().split(' ')[0].substring(0,5); // HH:MM

            // Update for the next iteration
            previousDepartureDateTime = departureFromCurrentStation;
            previousEstimateTimeHours = currentEstimateTimeHours;
          }
        }

        if (firstDepartureDateInput && firstDepartureTimeInput) {
          firstDepartureDateInput.addEventListener('change', calculateScheduledTimes);
          firstDepartureTimeInput.addEventListener('change', calculateScheduledTimes);
          
          // Initial calculation if first station already has values (e.g., on page load with existing data)
          if (firstDepartureDateInput.value && firstDepartureTimeInput.value) {
            calculateScheduledTimes();
          }
        }
      });
    </script>
  </body>
</html>
