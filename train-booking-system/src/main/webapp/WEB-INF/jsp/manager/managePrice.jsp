<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="vn.vnrailway.utils.DateUtils" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Quản lý quy tắc giá</title>
    <%-- Add your CSS links here --%>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/sidebar.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/managePrice.css"
    />
    <style>
      body {
        font-family: Arial, sans-serif;
        margin: 0;
        background-color: #f4f4f4;
        display: flex;
      }
      .main-content {
        flex-grow: 1;
        padding: 20px;
      }
      .container {
        background-color: #fff;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
      h1 {
        color: #333;
        margin-bottom: 20px;
      }
      .add-button {
        display: inline-block;
        padding: 10px 20px;
        background-color: #007bff;
        color: white;
        text-decoration: none;
        border-radius: 5px;
        margin-bottom: 20px;
        font-size: 16px;
      }
      .add-button:hover {
        background-color: #0056b3;
      }
      table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 20px;
      }
      th,
      td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: left;
        vertical-align: middle;
      }
      th {
        background-color: #007bff;
        color: white;
      }
      tr:nth-child(even) {
        background-color: #f9f9f9;
      }
      tr:hover {
        background-color: #f1f1f1;
      }
      .actions a {
        margin-right: 10px;
        text-decoration: none;
        padding: 5px 10px;
        border-radius: 3px;
        font-size: 14px;
      }
      .actions .edit {
        background-color: #ffc107;
        color: #333;
      }
      .actions .delete {
        background-color: #dc3545;
        color: white;
      }
      .actions .edit:hover {
        background-color: #e0a800;
      }
      .actions .delete:hover {
        background-color: #c82333;
      }
      .no-rules {
        text-align: center;
        font-size: 18px;
        color: #777;
        padding: 20px;
      }
    </style>
  </head>
  <body>
    <%@ include file="sidebar.jsp" %>
    <div class="main-content">
      <div class="container">
        <h1>Quản lý quy tắc giá</h1>
        <a
          href="${pageContext.request.contextPath}/managePrice?action=new"
          class="add-button"
          >Thêm quy tắc mới</a
        >

        <c:if test="${empty listPricingRules}">
          <p class="no-rules">Không tìm thấy quy tắc giá nào.</p>
        </c:if>

        <c:if test="${not empty listPricingRules}">
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Giá cơ bản/Km</th>
                <th>Hiệu lực từ</th>
                <th>Hiệu lực đến</th>
                <th>Hoạt động</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="rule" items="${listPricingRules}">
                <tr>
                  <td>${rule.ruleID}</td>
                  <td><c:out value="${rule.ruleName}" /></td>
                  <td>
                    <fmt:formatNumber
                      value="${rule.basePricePerKm}"
                      type="currency"
                      currencySymbol="₫"
                      maxFractionDigits="0"
                    />
                  </td>
                  <td>
                    <fmt:formatDate value="<%= DateUtils.toDate(
                    ((vn.vnrailway.model.PricingRule)
                    pageContext.findAttribute(\"rule\"))
                    .getEffectiveFromDate()) %>" pattern="dd-MM-yyyy" />
                  </td>
                  <td>
                    <fmt:formatDate value="<%= DateUtils.toDate(
                    ((vn.vnrailway.model.PricingRule)
                    pageContext.findAttribute(\"rule\")) .getEffectiveToDate())
                    %>" pattern="dd-MM-yyyy" />
                  </td>
                  <td>${rule.active ? 'Có' : 'Không'}</td>
                  <td class="actions">
                    <a
                      href="${pageContext.request.contextPath}/managePrice?action=edit&id=${rule.ruleID}"
                      class="edit"
                      >Sửa</a
                    >
                    <a
                      href="${pageContext.request.contextPath}/managePrice?action=delete&id=${rule.ruleID}"
                      class="delete"
                      onclick="return confirm('Bạn có chắc chắn muốn xóa quy tắc này không?');"
                      >Xóa</a
                    >
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </c:if>
      </div>
    </div>
  </body>
</html>
