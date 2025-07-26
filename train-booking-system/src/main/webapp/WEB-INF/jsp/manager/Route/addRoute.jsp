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
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        line-height: 1.6;
        margin: 0;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-direction: column;
      }
      .main-content {
        max-width: 600px;
        width: 100%;
        background: #fff;
        border-radius: 16px;
        box-shadow: 0 4px 24px rgba(0,0,0,0.10);
        padding: 36px 48px 32px 48px;
        margin: 40px auto;
        transition: box-shadow 0.2s;
        text-align: center;
      }
      .main-content:hover {
        box-shadow: 0 8px 32px rgba(0,0,0,0.13);
      }
      h1 {
        color: #2d3a4a;
        margin-bottom: 28px;
        font-weight: 500;
        border-bottom: 1px solid #e9ecef;
        padding-bottom: 12px;
        font-size: 2em;
        letter-spacing: 0.5px;
        text-align: center;
      }
      .form-group {
        margin-bottom: 1.5rem;
        text-align: center;
      }
      .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 600;
        color: #374151;
        letter-spacing: 0.2px;
        text-align: center;
      }
      .form-group input[type="text"],
      .form-group select,
      .form-group textarea {
        width: 100%;
        max-width: 400px;
        margin: 0 auto;
        padding: 0.65rem 1rem;
        font-size: 1rem;
        color: #374151;
        background-color: #f9fafb;
        border: 1.5px solid #d1d5db;
        border-radius: 6px;
        transition: border-color 0.18s, box-shadow 0.18s;
        box-sizing: border-box;
        display: block;
      }
      .form-group input:focus,
      .form-group select:focus,
      .form-group textarea:focus {
        border-color: #2563eb;
        outline: 0;
        box-shadow: 0 0 0 2px rgba(37,99,235,0.10);
        background: #fff;
      }
      .form-group textarea {
        min-height: 90px;
        resize: vertical;
      }
      .btn-container {
        text-align: center;
        margin-top: 2rem;
      }
      .btn.btn-primary {
        background: linear-gradient(90deg, #2563eb 0%, #1d4ed8 100%);
        color: #fff;
        border: none;
        padding: 0.6rem 1.5rem;
        border-radius: 6px;
        font-size: 1.08rem;
        font-weight: 500;
        cursor: pointer;
        margin-right: 12px;
        box-shadow: 0 2px 8px rgba(37,99,235,0.08);
        transition: background 0.18s, box-shadow 0.18s;
        display: inline-block;
      }
      .btn.btn-primary:hover {
        background: linear-gradient(90deg, #1d4ed8 0%, #2563eb 100%);
        box-shadow: 0 4px 16px rgba(37,99,235,0.13);
      }
      .btn.btn-secondary {
        background-color: #f3f4f6;
        color: #374151;
        border: 1.5px solid #d1d5db;
        padding: 0.6rem 1.5rem;
        border-radius: 6px;
        font-size: 1.08rem;
        font-weight: 500;
        cursor: pointer;
        transition: background 0.18s, border 0.18s;
        display: inline-block;
        text-decoration: none;
      }
      .btn.btn-secondary:hover {
        background-color: #e5e7eb;
        border-color: #a1a1aa;
      }
      .error-message {
        color: #b91c1c;
        background: #fee2e2;
        border: 1.5px solid #fecaca;
        border-radius: 6px;
        padding: 12px 16px;
        margin-bottom: 22px;
        font-size: 1rem;
        text-align: center;
      }
      .success-message {
        background: #d1fae5;
        color: #065f46;
        border: 1.5px solid #6ee7b7;
        border-radius: 6px;
        padding: 12px 16px;
        margin-bottom: 22px;
        font-size: 1rem;
        text-align: center;
      }
      .container {
        text-align: center;
      }
      form {
        text-align: center;
      }
      @media (max-width: 700px) {
        .main-content {
          padding: 18px 24px;
          margin: 20px;
        }
        .form-group input[type="text"],
        .form-group select,
        .form-group textarea {
          max-width: 100%;
        }
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
          <div class="btn-container">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-save"></i> Lưu Tuyến Đường
            </button>
            <a
              href="${pageContext.request.contextPath}/manageRoutes"
              class="btn btn-secondary"
              >Quay Lại</a
            >
          </div>
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
