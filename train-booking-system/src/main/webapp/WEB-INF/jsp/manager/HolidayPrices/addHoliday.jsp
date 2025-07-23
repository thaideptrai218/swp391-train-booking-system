<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Thêm ngày lễ mới</title>
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
      <h1>Thêm ngày lễ mới</h1>

      <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
      </c:if>

      <form
        action="${pageContext.request.contextPath}/manageHolidays?action=insert"
        method="post"
      >
        <div class="form-group">
          <label for="name">Tên ngày lễ:</label>
          <input type="text" id="name" name="name" required />
        </div>

        <div class="form-group">
          <label for="coefficient">Hệ số:</label>
          <input
            type="number"
            step="0.01"
            id="coefficient"
            name="coefficient"
            required
          />
        </div>

        <div class="form-group full-width">
          <label for="startDate">Ngày bắt đầu:</label>
          <input type="date" id="startDate" name="startDate" required />
          <label for="endDate" style="margin-left: 20px">Ngày kết thúc:</label>
          <input type="date" id="endDate" name="endDate" required />
        </div>

        <div class="form-group full-width">
          <label for="isActive">Hoạt động:</label>
          <input
            type="checkbox"
            id="isActive"
            name="isActive"
            value="true"
            checked
          />
        </div>

        <div class="button-group">
          <button type="submit">Lưu</button>
          <button
            type="button"
            onclick="window.location.href='${pageContext.request.contextPath}/manageHolidays'"
          >
            Hủy
          </button>
        </div>
      </form>
    </div>

    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const form = document.querySelector("form");
        const startDateInput = document.getElementById("startDate");
        const endDateInput = document.getElementById("endDate");

        startDateInput.addEventListener("change", function () {
          endDateInput.min = this.value;
        });

        endDateInput.addEventListener("change", function () {
          if (new Date(this.value) < new Date(startDateInput.value)) {
            alert("Ngày kết thúc không thể sớm hơn ngày bắt đầu.");
            this.value = startDateInput.value;
          }
        });

        form.addEventListener("submit", function (event) {
          const name = document.getElementById("name").value.trim();
          const coefficient = document
            .getElementById("coefficient")
            .value.trim();
          const startDate = startDateInput.value;
          const endDate = endDateInput.value;

          if (!name || !coefficient || !startDate || !endDate) {
            alert("Các trường không được để trống.");
            event.preventDefault();
            return;
          }

          if (new Date(endDate) < new Date(startDate)) {
            alert("Ngày kết thúc không thể sớm hơn ngày bắt đầu.");
            event.preventDefault();
            return;
          }
        });
      });
    </script>
  </body>
</html>
