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
      href="${pageContext.request.contextPath}/css/manager/price/managePrice.css"
    />
    <script src="${pageContext.request.contextPath}/js/manager/price/managePrice.js" defer></script>
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />
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
          <table id="pricingRulesTable">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Mô tả</th>
                <th>Giá cơ bản/km</th>
                <th>Ngày bắt đầu áp dụng</th>
                <th>Ngày kết thúc áp dụng</th>
                <th>Hoạt động</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="rule" items="${listPricingRules}">
                <tr>
                  <td>${rule.ruleID}</td>
                  <td><c:out value="${rule.ruleName}" /></td>
                  <td>${rule.description}</td>
                  <td>
                    <fmt:formatNumber
                      value="${rule.basePricePerKm}"
                      type="currency"
                      currencySymbol="₫"
                      maxFractionDigits="0"
                    />
                  </td>
                  <td>
                    <% java.util.Date applicableDateStart = vn.vnrailway.utils.DateUtils.toDate(((vn.vnrailway.model.PricingRule)pageContext.findAttribute("rule")).getApplicableDateStart()); %>
                    <fmt:formatDate value="<%= applicableDateStart %>" pattern="dd-MM-yyyy" />
                  </td>
                  <td>
                    <% java.util.Date applicableDateEnd = vn.vnrailway.utils.DateUtils.toDate(((vn.vnrailway.model.PricingRule)pageContext.findAttribute("rule")).getApplicableDateEnd()); %>
                    <fmt:formatDate value="<%= applicableDateEnd %>" pattern="dd-MM-yyyy" />
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${rule.defaultRule}">
                        <input type="checkbox" class="status-checkbox" checked disabled />
                      </c:when>
                      <c:otherwise>
                        <input type="checkbox" class="status-checkbox"
                               data-type="rule" data-id="${rule.ruleID}" ${rule.active ? 'checked' : ''}>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="actions">
                    <c:if test="${!rule.defaultRule}">
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
                    </c:if>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <div
            class="pagination-container"
            id="rules-pagination-container"
          ></div>
        </c:if>
      </div>
    </div>
  </body>
</html>
