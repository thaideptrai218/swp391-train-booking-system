<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${not empty vipCardType ? "Sửa" : "Thêm"} loại thẻ VIP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/managePrice.css" />
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
      textarea {
        width: 100%;
        padding: 10px;
        margin-bottom: 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
        box-sizing: border-box;
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
    </style>
</head>
<body>
    <div class="container">
        <h1>${not empty vipCardType ? "Sửa" : "Thêm"} loại thẻ VIP</h1>

        <form action="${pageContext.request.contextPath}/manageVIPCardTypes" method="post">
            <c:if test="${not empty vipCardType}">
                <input type="hidden" name="action" value="update" />
                <input type="hidden" name="id" value="${vipCardType.vipCardTypeID}" />
            </c:if>
            <c:if test="${empty vipCardType}">
                <input type="hidden" name="action" value="insert" />
            </c:if>

            <div class="form-group">
                <label for="typeName">Tên loại thẻ:</label>
                <input type="text" id="typeName" name="typeName" value="<c:out value='${vipCardType.typeName}' />" required />
            </div>

            <div class="form-group">
                <label for="price">Giá:</label>
                <input type="number" step="0.01" id="price" name="price" value="${vipCardType.price}" required />
            </div>

            <div class="form-group">
                <label for="discountPercentage">Giảm giá (%):</label>
                <input type="number" step="0.01" id="discountPercentage" name="discountPercentage" value="${vipCardType.discountPercentage}" required />
            </div>

            <div class="form-group">
                <label for="durationMonths">Thời hạn (tháng):</label>
                <input type="number" id="durationMonths" name="durationMonths" value="${vipCardType.durationMonths}" required />
            </div>

            <div class="form-group full-width">
                <label for="description">Mô tả:</label>
                <textarea id="description" name="description" rows="3"><c:out value='${vipCardType.description}' /></textarea>
            </div>

            <div class="button-group">
                <button type="submit">Lưu</button>
                <button type="button" onclick="window.location.href='${pageContext.request.contextPath}/manageVIPCardTypes'">Hủy</button>
            </div>
        </form>
    </div>
</body>
</html>
