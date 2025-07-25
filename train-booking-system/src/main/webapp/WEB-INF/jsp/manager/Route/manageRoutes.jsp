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
      href="${pageContext.request.contextPath}/css/manager/routes/manageRoutes.css"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.15.0/Sortable.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/manager/routes/manageRoutes.js" defer></script>
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />

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

      <!-- Toolbar -->
      <div class="container" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
          <form id="filterForm" method="get" style="margin-right: 20px;">
              <label for="activeFilter">Trạng thái:</label>
              <select name="activeFilter" id="activeFilter" onchange="document.getElementById('filterForm').submit();">
                <option value="active" ${activeFilter == 'active' ? 'selected' : ''}>Hoạt động</option>
                <option value="inactive" ${activeFilter == 'inactive' ? 'selected' : ''}>Không hoạt động</option>
                <option value="all" ${activeFilter == 'all' ? 'selected' : ''}>Tất cả</option>
              </select>
          </form>
          <div class="search-container" style="flex-grow: 1; margin-right: 20px;">
              <input type="text" id="routeSearchInput" placeholder="Tìm kiếm theo ID hoặc Tên Tuyến Đường..." style="width: 100%; padding: 8px; border-radius: 4px; border: 1px solid #ccc;">
          </div>
          <a href="${pageContext.request.contextPath}/manager/addRoute" class="btn btn-primary">
              <i class="fas fa-plus-circle"></i> Thêm Tuyến Đường Mới
          </a>
      </div>

      <!-- List of Routes -->
      <h2><i class="fas fa-list-ul"></i> Danh Sách Tuyến Đường Hiện Có</h2>
      <c:choose>
        <c:when test="${empty allRoutes}">
          <p>Chưa có tuyến đường nào được tạo.</p>
        </c:when>
        <c:otherwise>
          <table class="routes-table" id="routesTable">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên Tuyến Đường</th>
                <th>Hoạt động</th>
                <th>Hành Động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${allRoutes}" var="route">
                <tr>
                  <td><c:out value="${route.routeID}" /></td>
                  <td><c:out value="${route.routeName}" /></td>
                  <td>
                    <input type="checkbox" class="route-active-checkbox" data-id="${route.routeID}" ${route.active ? 'checked' : ''} />
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
                    <!-- Đã loại bỏ button Xóa ở đây -->
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <div class="pagination-container" id="pagination-container"></div>
        </c:otherwise>
      </c:choose>

      <!-- Edit Route Form (conditionally shown or populated) -->
      <c:set var="editFormContainerClass" value=""/>
      <c:if test="${empty routeToEditOnPage}">
          <c:set var="editFormContainerClass" value="edit-form-hidden"/>
      </c:if>
      <div class="container<c:if test='${not empty editFormContainerClass}'> ${editFormContainerClass}</c:if>" id="editRouteFormContainer" style="margin-top: 30px;">
        <h2><i class="fas fa-edit"></i> Chỉnh Sửa Tuyến Đường</h2>
        <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
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

  </body>
</html>
