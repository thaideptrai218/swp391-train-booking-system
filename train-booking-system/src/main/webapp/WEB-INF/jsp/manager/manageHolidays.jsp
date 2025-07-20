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
      .pagination-container {
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 20px;
      }
      .pagination-container .page-link {
        padding: 8px 12px;
        margin: 0 4px;
        border: 1px solid #ddd;
        background-color: #fff;
        color: #007bff;
        cursor: pointer;
        text-decoration: none;
        border-radius: 4px;
        transition: background-color 0.3s, color 0.3s;
      }
      .pagination-container .page-link.active,
      .pagination-container .page-link:hover {
        background-color: #007bff;
        color: #fff;
        border-color: #007bff;
      }
      .pagination-container .page-link.disabled {
        color: #ccc;
        cursor: not-allowed;
        background-color: #f9f9f9;
        border-color: #ddd;
      }
    </style>
  </head>
  <body>
    <%@ include file="sidebar.jsp" %>
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
    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const checkboxes = document.querySelectorAll(".status-checkbox");
        checkboxes.forEach((checkbox) => {
          checkbox.addEventListener("change", function () {
            const id = this.dataset.id;
            const isActive = this.checked;

            fetch("${pageContext.request.contextPath}/manageHolidays", {
              method: "POST",
              headers: {
                "Content-Type": "application/x-www-form-urlencoded",
              },
              body:
                "action=updateHolidayStatus&id=" + id + "&isActive=" + isActive,
            })
              .then((response) => {
                if (!response.ok) {
                  alert("Error updating status");
                  this.checked = !isActive; // Revert the checkbox on error
                }
              })
              .catch(() => {
                alert("Error updating status");
                this.checked = !isActive; // Revert the checkbox on error
              });
          });
        });

        function setupPaginationForTable(tableId, containerId) {
          const table = document.getElementById(tableId);
          if (!table) return;

          const allRows = Array.from(table.querySelectorAll("tbody tr"));
          const noDataRow = allRows.find((row) =>
            row.querySelector("td[colspan]")
          );
          const dataRows = allRows.filter((row) => row !== noDataRow);
          const paginationContainer = document.getElementById(containerId);
          const rowsPerPage = 5;
          let currentPage = 1;

          function displayRows(page) {
            currentPage = page;
            const start = (page - 1) * rowsPerPage;
            const end = start + rowsPerPage;

            dataRows.forEach((row) => (row.style.display = "none"));
            const rowsToShow = dataRows.slice(start, end);
            rowsToShow.forEach((row) => (row.style.display = ""));

            if (noDataRow) {
              noDataRow.style.display = dataRows.length === 0 ? "" : "none";
            }
          }

          function setupPagination() {
            paginationContainer.innerHTML = "";
            const pageCount = Math.ceil(dataRows.length / rowsPerPage);

            if (pageCount <= 1) return;

            const prevLink = document.createElement("a");
            prevLink.href = "#";
            prevLink.innerHTML = "&laquo;";
            prevLink.classList.add("page-link");
            if (currentPage === 1) {
              prevLink.classList.add("disabled");
            }
            prevLink.addEventListener("click", (e) => {
              e.preventDefault();
              if (currentPage > 1) {
                displayRows(currentPage - 1);
                setupPagination();
              }
            });
            paginationContainer.appendChild(prevLink);

            for (let i = 1; i <= pageCount; i++) {
              const pageLink = document.createElement("a");
              pageLink.href = "#";
              pageLink.innerText = i;
              pageLink.classList.add("page-link");
              if (i === currentPage) {
                pageLink.classList.add("active");
              }
              pageLink.addEventListener("click", (e) => {
                e.preventDefault();
                displayRows(i);
                setupPagination();
              });
              paginationContainer.appendChild(pageLink);
            }

            const nextLink = document.createElement("a");
            nextLink.href = "#";
            nextLink.innerHTML = "&raquo;";
            nextLink.classList.add("page-link");
            if (currentPage === pageCount) {
              nextLink.classList.add("disabled");
            }
            nextLink.addEventListener("click", (e) => {
              e.preventDefault();
              if (currentPage < pageCount) {
                displayRows(currentPage + 1);
                setupPagination();
              }
            });
            paginationContainer.appendChild(nextLink);
          }

          displayRows(1);
          setupPagination();
        }

        setupPaginationForTable(
          "holidaysTable",
          "holidays-pagination-container"
        );
      });
    </script>
  </body>
</html>
