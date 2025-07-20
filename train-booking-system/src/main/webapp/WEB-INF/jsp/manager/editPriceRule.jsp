<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="vn.vnrailway.model.PricingRule, vn.vnrailway.utils.DateUtils, java.util.Date" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Chỉnh sửa quy tắc giá</title>
    <%-- Add your CSS links here --%>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/managePrice.css"
    />
    <style>
      body {
        font-family: Arial, sans-serif;
        margin: 0;
        background-color: #f4f4f4;
        color: #333;
        display: flex;
      }
      .container {
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-width: 900px;
        margin: 20px auto;
      }
      h1 {
        color: #333;
      }
      form {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 25px;
      }
      label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
      }
      input[type="text"],
      input[type="number"],
      input[type="date"],
      select,
      textarea {
        width: 100%;
        padding: 10px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-sizing: border-box;
      }
      input[type="checkbox"] {
        margin-right: 5px;
      }
      .form-group {
        margin-bottom: 15px;
      }
      .full-width {
        grid-column: 1 / -1;
      }
      .button-group {
        grid-column: 1 / -1;
        text-align: right;
        margin-top: 20px;
      }
      .button-group button {
        padding: 10px 20px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 16px;
      }
      .button-group button[type="submit"] {
        background-color: #28a745;
        color: white;
      }
      .button-group button[type="button"] {
        background-color: #6c757d;
        color: white;
        margin-left: 10px;
      }
      .button-group button[type="button"]:hover {
        background-color: #5a6268;
      }
      .error-message {
        color: red;
        margin-bottom: 15px;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h1>Chỉnh sửa quy tắc giá</h1>

      <c:if test="${not empty param.error and param.error eq 'invalidInput'}">
        <p class="error-message">
          Lỗi: Đầu vào không hợp lệ. Vui lòng kiểm tra lại dữ liệu của bạn và thử lại.
        </p>
      </c:if>
      
      <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
      </c:if>


      <form
        action="${pageContext.request.contextPath}/managePrice?action=update"
        method="post"
      >
        <input type="hidden" name="ruleID" value="${pricingRule.ruleID}" />

        <div class="form-group">
          <label for="ruleName">Tên quy tắc:</label>
          <input
            type="text"
            id="ruleName"
            name="ruleName"
            value="${pricingRule.ruleName}"
            required
          />
        </div>

        <div class="form-group">
          <label for="description">Mô tả:</label>
          <textarea id="description" name="description">${pricingRule.description}</textarea>
        </div>

        <div class="form-group">
          <label for="trainTypeID">Loại tàu (Tùy chọn):</label>
          <select id="trainTypeID" name="trainTypeID">
            <option value="">Tất cả</option>
            <c:forEach var="trainType" items="${trainTypes}">
              <option value="${trainType.trainTypeID}" ${pricingRule.trainTypeID == trainType.trainTypeID ? 'selected' : ''}>${trainType.typeName}</option>
            </c:forEach>
          </select>
        </div>

        <div class="form-group">
          <label for="routeID">Tuyến (Tùy chọn):</label>
          <select id="routeID" name="routeID">
            <option value="">Tất cả</option>
            <c:forEach var="route" items="${routes}">
              <option value="${route.routeID}" ${pricingRule.routeID == route.routeID ? 'selected' : ''}>${route.routeName}</option>
            </c:forEach>
          </select>
        </div>

        <div class="form-group">
          <label for="basePricePerKm">Giá cơ bản mỗi Km (Tùy chọn):</label>
          <input
            type="number"
            step="0.01"
            id="basePricePerKm"
            name="basePricePerKm"
            value="${pricingRule.basePricePerKm / 1000}"
          />
        </div>

        <div class="form-group">
          <label for="isForRoundTrip">Cho chuyến khứ hồi:</label>
          <select id="isForRoundTrip" name="isForRoundTrip">
            <option value="0" ${!pricingRule.forRoundTrip ? 'selected' : ''}>Một chiều</option>
            <option value="1" ${pricingRule.forRoundTrip ? 'selected' : ''}>Khứ hồi</option>
          </select>
        </div>

        <%
          PricingRule pricingRule = (PricingRule) request.getAttribute("pricingRule");
          Date applicableDateStart = DateUtils.toDate(pricingRule.getEffectiveFromDate());
          Date applicableDateEnd = DateUtils.toDate(pricingRule.getEffectiveToDate());
        %>
        <div class="form-group full-width">
          <label for="applicableDateStart">Ngày bắt đầu áp dụng:</label>
          <input
            type="date"
            id="applicableDateStart"
            name="applicableDateStart"
            value="<fmt:formatDate value="<%= applicableDateStart %>" pattern="yyyy-MM-dd" />"
            required
          />
          <label for="applicableDateEnd" style="margin-left: 20px;">Ngày kết thúc áp dụng (Tùy chọn):</label>
          <input
            type="date"
            id="applicableDateEnd"
            name="applicableDateEnd"
            value="<fmt:formatDate value="<%= applicableDateEnd %>" pattern="yyyy-MM-dd" />"
          />
        </div>

        <div class="form-group full-width">
          <label for="isActive">Hoạt động:</label>
          <input type="checkbox" id="isActive" name="isActive" value="true"
          ${pricingRule.active ? 'checked' : ''}>
        </div>

        <div class="button-group">
          <button type="submit">Cập nhật quy tắc</button>
          <button
            type="button"
            onclick="window.location.href='${pageContext.request.contextPath}/managePrice'"
          >
            Hủy
          </button>
        </div>
      </form>
    </div>

    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const form = document.querySelector("form");
        const applicableDateStartInput = document.getElementById("applicableDateStart");
        const applicableDateEndInput = document.getElementById("applicableDateEnd");

        // Set min on page load if applicableDateStart has a value
        if (applicableDateStartInput.value) {
          applicableDateEndInput.min = applicableDateStartInput.value;
        }

        applicableDateStartInput.addEventListener("change", function () {
          applicableDateEndInput.min = this.value;
          if (applicableDateEndInput.value && applicableDateEndInput.value < this.value) {
            applicableDateEndInput.value = this.value;
          }
        });

        form.addEventListener("submit", function (event) {
          const ruleName = document.getElementById("ruleName").value.trim();
          const description = document
            .getElementById("description")
            .value.trim();
          const basePricePerKm = document
            .getElementById("basePricePerKm")
            .value.trim();
          const applicableDateStart = applicableDateStartInput.value;
          const applicableDateEnd = applicableDateEndInput.value;

          if (
            !ruleName ||
            !description ||
            !basePricePerKm ||
            !applicableDateStart ||
            !applicableDateEnd
          ) {
            alert("Các trường không được để trống.");
            event.preventDefault();
            return;
          }

          if (applicableDateEnd && applicableDateEnd < applicableDateStart) {
            alert("Ngày kết thúc áp dụng không thể sớm hơn Ngày bắt đầu áp dụng.");
            event.preventDefault();
            return;
          }
        });
      });
    </script>
  </body>
</html>
