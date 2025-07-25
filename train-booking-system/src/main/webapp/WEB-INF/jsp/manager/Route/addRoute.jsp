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
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        line-height: 1.6;
        margin: 0;
      }
      .main-content {
        max-width: 700px;
        margin: 30px auto;
        background: #fff;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.07);
        padding: 30px 40px;
      }
      h1 {
        color: #343a40;
        margin-bottom: 25px;
        font-weight: 400;
        border-bottom: 1px solid #e9ecef;
        padding-bottom: 15px;
        font-size: 1.8em;
      }
      .form-group {
        margin-bottom: 1.25rem;
      }
      .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: #495057;
      }
      .form-group input[type="text"],
      .form-group select,
      .form-group textarea {
        width: 100%;
        padding: 0.5rem 0.75rem;
        font-size: 0.9rem;
        line-height: 1.5;
        color: #495057;
        background-color: #fff;
        border: 1px solid #ced4da;
        border-radius: 0.25rem;
        transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        box-sizing: border-box;
      }
      .form-group input:focus,
      .form-group select:focus,
      .form-group textarea:focus {
        border-color: #80bdff;
        outline: 0;
        box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.15);
      }
      .btn.btn-primary {
        background-color: #007bff;
        color: #fff;
        border: none;
        padding: 0.5rem 1.2rem;
        border-radius: 0.25rem;
        font-size: 1rem;
        cursor: pointer;
        margin-right: 10px;
      }
      .btn.btn-primary:hover {
        background-color: #0056b3;
      }
      .btn.btn-secondary {
        background-color: #f8f9fa;
        color: #212529;
        border: 1px solid #ced4da;
        padding: 0.5rem 1.2rem;
        border-radius: 0.25rem;
        font-size: 1rem;
        cursor: pointer;
      }
      .btn.btn-secondary:hover {
        background-color: #e2e6ea;
      }
      .error-message {
        color: #721c24;
        background: #f8d7da;
        border: 1px solid #f5c6cb;
        border-radius: 4px;
        padding: 10px;
        margin-bottom: 20px;
      }
    </style>
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />

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
              <c:forEach var="station" items="${allStations}">
                <option value="${station.stationID}">${station.stationName}</option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="arrivalStationId">Điểm Đến:</label>
            <select name="arrivalStationId" id="arrivalStationId" required>
              <option value="">-- Chọn Điểm Đến --</option>
              <c:forEach var="station" items="${allStations}">
                <option value="${station.stationID}">${station.stationName}</option>
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
