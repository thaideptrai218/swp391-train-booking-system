<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %> <%@ taglib prefix="fn"
uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản Lý Tuyến Đường</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/manageRoutes.css"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.15.0/Sortable.min.js"></script>
    <style>
      /* Basic Modal Styling */
      .modal {
        display: none; /* Hidden by default */
        position: fixed; /* Stay in place */
        z-index: 1000; /* Sit on top */
        left: 0;
        top: 0;
        width: 100%; /* Full width */
        height: 100%; /* Full height */
        overflow: auto; /* Enable scroll if needed */
        background-color: rgb(0, 0, 0); /* Fallback color */
        background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
        padding-top: 60px;
      }

      .modal-content {
        background-color: #fefefe;
        margin: 5% auto; /* 15% from the top and centered */
        padding: 20px;
        border: 1px solid #888;
        width: 80%; /* Could be more or less, depending on screen size */
        max-width: 500px;
        border-radius: 8px;
      }

      .close-btn {
        color: #aaa;
        float: right;
        font-size: 28px;
        font-weight: bold;
      }

      .close-btn:hover,
      .close-btn:focus {
        color: black;
        text-decoration: none;
        cursor: pointer;
      }

      /* Styles for the routes table */
      .routes-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
      }
      .routes-table th,
      .routes-table td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
      }
      .routes-table th {
        background-color: #f2f2f2;
        color: #333;
      }
      .routes-table tr:nth-child(even) {
        background-color: #f9f9f9;
      }
      .routes-table tr:hover {
        background-color: #e9ecef;
      }
      .routes-table .action-link {
        color: #007bff;
        text-decoration: none;
        font-weight: bold;
      }
      .routes-table .action-link:hover {
        text-decoration: underline;
      }
    </style>
  </head>
  <body>
    <jsp:include page="sidebar.jsp" />

    <div class="main-content" id="mainContent">
      <h1><i class="fas fa-route"></i> Quản Lý Tuyến Đường</h1>

      <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
      </c:if>
      <c:if test="${not empty sessionScope.successMessage}">
        <div class="success-message" style="background-color: #d4edda; color: #155724; padding: 10px; border: 1px solid #c3e6cb; border-radius: 4px; margin-bottom: 20px;">
            ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session"/>
      </c:if>
      <c:if test="${not empty sessionScope.errorMessage}">
          <div class="error-message" style="background-color: #f8d7da; color: #721c24; padding: 10px; border: 1px solid #f5c6cb; border-radius: 4px; margin-bottom: 20px;">
              ${sessionScope.errorMessage}
          </div>
          <c:remove var="errorMessage" scope="session"/>
      </c:if>

      <!-- Confirmation Dialog for Deleting Route with Trips -->
      <c:if test="${confirmDeleteRouteWithTrips}">
        <div class="confirmation-dialog" style="border: 1px solid #ffc107; background-color: #fff3cd; color: #856404; padding: 15px; margin-bottom: 20px; border-radius: 4px;">
            <h4>Xác Nhận Xóa Tuyến Đường</h4>
            <p>
                Tuyến đường "<strong><c:out value="${routeNameToDelete}"/></strong>" (ID: ${routeIdToDelete}) hiện có <strong>${numberOfTrips}</strong> chuyến đi liên quan.
                <br/>Bạn có chắc chắn muốn xóa tuyến đường này VÀ TẤT CẢ các chuyến đi liên quan không? Hành động này không thể hoàn tác.
            </p>
            <form action="${pageContext.request.contextPath}/manageRoutes" method="post" style="display: inline-block; margin-right: 10px;">
                <input type="hidden" name="action" value="deleteRoute"/>
                <input type="hidden" name="routeId" value="${routeIdToDelete}"/>
                <input type="hidden" name="confirmedDelete" value="true"/>
                <button type="submit" class="btn btn-danger"><i class="fas fa-trash"></i> Có, Xóa Tất Cả</button>
            </form>
            <a href="${pageContext.request.contextPath}/manageRoutes" class="btn btn-secondary"><i class="fas fa-times"></i> Không, Hủy Bỏ</a>
        </div>
      </c:if>

      <c:if test="${not empty successMessage}">
        <div
          class="success-message"
          style="
            background-color: #d4edda;
            color: #155724;
            padding: 10px;
            border: 1px solid #c3e6cb;
            border-radius: 4px;
            margin-bottom: 20px;
          "
        >
          ${successMessage}
        </div>
      </c:if>

      <!-- Add New Route Form -->
      <div class="container">
        <h2><i class="fas fa-plus-circle"></i> Thêm Tuyến Đường Mới</h2>
        <form
          action="${pageContext.request.contextPath}/manageRoutes"
          method="post"
        >
          <input type="hidden" name="action" value="addRoute" />
          <div class="form-group">
            <label for="departureStationId">Điểm Đi:</label>
            <select name="departureStationId" id="departureStationId" required>
              <option value="">-- Chọn Điểm Đi --</option>
              <c:forEach items="${allStations}" var="station">
                <option value="${station.stationID}">
                  ${station.stationName}
                </option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="arrivalStationId">Điểm Đến:</label>
            <select name="arrivalStationId" id="arrivalStationId" required>
              <option value="">-- Chọn Điểm Đến --</option>
              <c:forEach items="${allStations}" var="station">
                <option value="${station.stationID}">
                  ${station.stationName}
                </option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="description">Mô Tả:</label>
            <textarea id="description" name="description"></textarea>
          </div>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save"></i> Lưu Tuyến Đường
          </button>
        </form>
      </div>

      <!-- List of Routes -->
      <h2><i class="fas fa-list-ul"></i> Danh Sách Tuyến Đường Hiện Có</h2>
      <c:choose>
        <c:when test="${empty allRoutes}">
          <p>Chưa có tuyến đường nào được tạo.</p>
        </c:when>
        <c:otherwise>
          <table class="routes-table">
            <thead>
              <tr>
                <th>Tên Tuyến Đường (ID)</th>
                <th>Hành Động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${allRoutes}" var="route">
                <tr>
                  <td>
                    <c:out value="${route.routeName}" /> (ID: ${route.routeID})
                  </td>
                  <td>
                    <a
                      href="${pageContext.request.contextPath}/manager/routeDetail?routeId=${route.routeID}"
                      class="action-link"
                      style="margin-right: 10px;"
                    >
                      <i class="fas fa-eye"></i> Xem Chi Tiết
                    </a>
                    <a
                      href="${pageContext.request.contextPath}/manageRoutes?action=showEditForm&routeId=${route.routeID}#editRouteFormContainer"
                      class="action-link"
                      style="margin-right: 10px;"
                    >
                      <i class="fas fa-edit"></i> Sửa
                    </a>
                    <form
                      action="${pageContext.request.contextPath}/manageRoutes"
                      method="post"
                      style="display: inline"
                      onsubmit="return confirm('Bạn có chắc chắn muốn xóa tuyến đường này và tất cả các trạm của nó không?');"
                    >
                      <input type="hidden" name="action" value="deleteRoute" />
                      <input type="hidden" name="routeId" value="${route.routeID}" />
                      <button type="submit" class="btn-link action-link" style="border: none; background: none; padding: 0; cursor: pointer; color: #dc3545;">
                        <i class="fas fa-trash"></i> Xóa
                      </button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:otherwise>
      </c:choose>

      <!-- Edit Route Form (conditionally shown or populated) -->
      <div class="container" id="editRouteFormContainer" style="margin-top: 30px; <c:if test="${empty routeToEditOnPage}">display: none;</c:if>">
        <h2><i class="fas fa-edit"></i> Chỉnh Sửa Tuyến Đường</h2>
        <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
          <input type="hidden" name="action" value="updateRoute" />
          <input type="hidden" name="action" value="updateRoute" />
          <input type="hidden" name="routeId" value="${routeToEditOnPage.routeID}" />
          <input type="hidden" name="originalDepartureStationId" value="${departureStationIdForEdit}" /> <%-- Need original IDs --%>
          <input type="hidden" name="originalArrivalStationId" value="${arrivalStationIdForEdit}" />

          <div class="form-group">
            <label for="editDepartureStationId">Điểm Đi:</label>
            <select name="newDepartureStationId" id="editDepartureStationId" required>
              <c:forEach items="${allStations}" var="station">
                <option value="${station.stationID}" ${station.stationID == departureStationIdForEdit ? 'selected' : ''}>
                  <c:out value="${station.stationName}"/>
                </option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="editArrivalStationId">Điểm Đến:</label>
            <select name="newArrivalStationId" id="editArrivalStationId" required>
              <c:forEach items="${allStations}" var="station">
                <option value="${station.stationID}" ${station.stationID == arrivalStationIdForEdit ? 'selected' : ''}>
                  <c:out value="${station.stationName}"/>
                </option>
              </c:forEach>
            </select>
          </div>
          <%-- The Route Name will be implicitly updated based on selected stations in the servlet --%>
          <div class="form-group">
            <label for="editDescription">Mô Tả:</label>
            <textarea id="editDescription" name="description"><c:out value="${routeToEditOnPage.description}"/></textarea>
          </div>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save"></i> Cập Nhật Tuyến Đường
          </button>
          <a href="${pageContext.request.contextPath}/manageRoutes" class="btn btn-secondary">Hủy</a>
        </form>
      </div>

    </div>
    <!-- End Main Content -->

    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const addDepartureSelect = document.getElementById('departureStationId');
        const addArrivalSelect = document.getElementById('arrivalStationId');
        const editDepartureSelect = document.getElementById('editDepartureStationId');
        const editArrivalSelect = document.getElementById('editArrivalStationId');

        function updateStationOptions(sourceSelect, targetSelect) {
            const selectedValue = sourceSelect.value;
            for (let i = 0; i < targetSelect.options.length; i++) {
                const option = targetSelect.options[i];
                if (option.value === selectedValue && option.value !== "") { // Don't hide the placeholder
                    option.style.display = 'none';
                } else {
                    option.style.display = '';
                }
            }
        }

        if (addDepartureSelect && addArrivalSelect) {
            addDepartureSelect.addEventListener('change', function() {
                updateStationOptions(this, addArrivalSelect);
            });
            addArrivalSelect.addEventListener('change', function() {
                updateStationOptions(this, addDepartureSelect);
            });
            // Initial sync for add form (if needed, though usually starts empty)
            // updateStationOptions(addDepartureSelect, addArrivalSelect);
            // updateStationOptions(addArrivalSelect, addDepartureSelect);
        }

        if (editDepartureSelect && editArrivalSelect) {
            editDepartureSelect.addEventListener('change', function() {
                updateStationOptions(this, editArrivalSelect);
            });
            editArrivalSelect.addEventListener('change', function() {
                updateStationOptions(this, editDepartureSelect);
            });
            // Initial sync for edit form when page loads with pre-selected values
            updateStationOptions(editDepartureSelect, editArrivalSelect);
            updateStationOptions(editArrivalSelect, editDepartureSelect);
        }

        const urlParams = new URLSearchParams(window.location.search);
        const action = urlParams.get('action');
        const routeId = urlParams.get('routeId');

        if (action === 'showEditForm' && routeId) {
          const editFormContainer = document.getElementById('editRouteFormContainer');
          if (editFormContainer) {
            editFormContainer.style.display = 'block';
            // Scroll to the form if the anchor is present
            if (window.location.hash === '#editRouteFormContainer') {
                 editFormContainer.scrollIntoView({ behavior: 'smooth' });
            }
          }
        }

        // Logic for highlightRouteId (if needed in the future)
        // const highlightRouteId = urlParams.get("highlightRouteId"); 
        // if (highlightRouteId) {
          // Logic to find and highlight the route in the new list format
        // }
      });
    </script>
  </body>
</html>
