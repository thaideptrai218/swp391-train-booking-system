<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="vn.vnrailway.utils.DateUtils" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Quản lý ngày lễ</title>
    <%-- Add your CSS links here --%>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/sidebar.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/managePrice.css"
    />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/holidays/manageHolidays.css" />
    <script src="${pageContext.request.contextPath}/js/manager/holidays/manageHolidays.js" defer></script>
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />
    <div class="main-content">
      <div class="container">
        <h1>Quản lý ngày lễ</h1>

        <a
          href="${pageContext.request.contextPath}/manageHolidays?action=new"
          class="add-button"
          >Thêm ngày lễ mới</a
        >

        <c:if test="${empty listHolidays}">
          <p class="no-rules">Không tìm thấy ngày lễ nào.</p>
        </c:if>

        <c:if test="${not empty listHolidays}">
          <table id="holidaysTable">
            <thead>
              <tr>
                <th>ID</th>
                <th>Tên</th>
                <th>Ngày bắt đầu</th>
                <th>Ngày kết thúc</th>
                <th>Hệ số</th>
                <th>Hoạt động</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="holiday" items="${listHolidays}">
                <tr>
                  <td>${holiday.id}</td>
                  <td><c:out value="${holiday.holidayName}" /></td>
                  <td>
                    <fmt:formatDate value="<%=
                    DateUtils.toDate(((vn.vnrailway.model.HolidayPrice)
                    pageContext.findAttribute(\"holiday\")).getStartDate()) %>"
                    pattern="dd-MM-yyyy" />
                  </td>
                  <td>
                    <fmt:formatDate value="<%=
                    DateUtils.toDate(((vn.vnrailway.model.HolidayPrice)
                    pageContext.findAttribute(\"holiday\")).getEndDate()) %>"
                    pattern="dd-MM-yyyy" />
                  </td>
                  <td>
                    <fmt:formatNumber
                      value="${holiday.discountPercentage}"
                      maxFractionDigits="2"
                    />
                  </td>
                  <td>
                    <input type="checkbox" class="status-checkbox"
                    data-id="${holiday.id}" ${holiday.active ? 'checked' : ''}>
                  </td>
                  <td class="actions">
                    <a
                      href="${pageContext.request.contextPath}/manageHolidays?action=edit&id=${holiday.id}"
                      class="edit"
                      >Sửa</a
                    >
                    <a
                      href="${pageContext.request.contextPath}/manageHolidays?action=delete&id=${holiday.id}"
                      class="delete"
                      onclick="return confirm('Bạn có chắc chắn muốn xóa ngày lễ này không?');"
                      >Xóa</a
                    >
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
          <div
            class="pagination-container"
            id="holidays-pagination-container"
          ></div>
        </c:if>
      </div>
    </div>
  </body>
</html>
