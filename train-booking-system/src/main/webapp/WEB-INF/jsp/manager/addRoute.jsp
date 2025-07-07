<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Thêm Tuyến Đường Mới</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/manageRoutes.css"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
  </head>
  <body>
    <jsp:include page="sidebar.jsp" />

    <div class="main-content" id="mainContent">
      <h1><i class="fas fa-plus-circle"></i> Thêm Tuyến Đường Mới</h1>

      <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
      </c:if>
      <c:if test="${not empty sessionScope.successMessage}">
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
          ${sessionScope.successMessage}
        </div>
        <c:remove var="successMessage" scope="session" />
      </c:if>

      <div class="container">
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
          <a
            href="${pageContext.request.contextPath}/manageRoutes"
            class="btn btn-secondary"
            >Quay Lại</a
          >
        </form>
      </div>
    </div>

    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const addDepartureSelect =
          document.getElementById("departureStationId");
        const addArrivalSelect = document.getElementById("arrivalStationId");

        function updateStationOptions(sourceSelect, targetSelect) {
          const selectedValue = sourceSelect.value;
          for (let i = 0; i < targetSelect.options.length; i++) {
            const option = targetSelect.options[i];
            if (option.value === selectedValue && option.value !== "") {
              option.style.display = "none";
            } else {
              option.style.display = "";
            }
          }
        }

        if (addDepartureSelect && addArrivalSelect) {
          addDepartureSelect.addEventListener("change", function () {
            updateStationOptions(this, addArrivalSelect);
          });
          addArrivalSelect.addEventListener("change", function () {
            updateStationOptions(this, addDepartureSelect);
          });
        }
      });
    </script>
  </body>
</html>
