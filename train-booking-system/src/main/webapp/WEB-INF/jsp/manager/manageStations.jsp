<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý ga</title>
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>" />
    <link
      rel="stylesheet"
      href="<c:url value='/css/manager/manager-dashboard.css'/>"
    />
    <link rel="stylesheet" href="<c:url value='/css/manager/sidebar.css'/>" />
    <link
      rel="stylesheet"
      href="<c:url value='/css/manager/manageStations.css'/>"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <style>
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
      .lock-btn.locked {
        background-color: red;
        color: white;
      }
      .actions {
        min-width: 200px;
        display: flex;
        gap: 5px;
      }
      .edit-btn,
      .lock-btn {
        padding: 5px 10px;
        border-radius: 5px;
        text-decoration: none;
        color: white;
        display: inline-flex;
        align-items: center;
        gap: 5px;
      }
      .edit-btn {
        background-color: #ffc107;
      }
      .lock-btn {
        background-color: #dc3545;
      }
      .lock-btn.locked {
        background-color: #28a745;
      }
    </style>
  </head>
  <body>
    <jsp:include page="sidebar.jsp" />

    <div class="dashboard-container">
      <%-- Changed class --%>
      <div class="main-content">
        <header class="dashboard-header">
          <%-- Added header --%>
          <h1>Quản lý ga</h1>
        </header>
        <section class="content-section">
          <%-- Added content section wrapper --%>
          <c:if test="${not empty message}">
            <%-- Changed div to p and class names for messages --%>
            <p
              class="message ${message.contains('successfully') ? 'success-message' : 'error-message'}"
            >
              ${message}
            </p>
          </c:if>
          <c:if test="${not empty errorMessage}">
            <p class="message error-message">${errorMessage}</p>
          </c:if>

          <div class="controls-container">
            <%-- Controls container for search and add button --%>
            <div class="search-container">
              <input
                type="text"
                id="searchInput"
                placeholder="Tìm kiếm theo ID, Mã, hoặc Tên..."
              />
            </div>
            <button id="showAddFormBtn" class="action-add">
              <%-- Changed class --%> <i class="fas fa-plus"></i> Thêm ga mới
            </button>
          </div>

          <div class="table-container">
            <%-- Changed class and removed h2 --%>
            <table id="stationsTable">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Mã</th>
                  <th>Tên</th>
                  <th>Địa chỉ</th>
                  <th>Thành phố</th>
                  <th>Khu vực</th>
                  <th>Điện thoại</th>
                  <th>Hành động</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="station" items="${stations}">
                  <tr>
                    <td>${station.stationID}</td>
                    <td>${station.stationCode}</td>
                    <td>${station.stationName}</td>
                    <td>${station.address}</td>
                    <td>${station.city}</td>
                    <td>${station.region}</td>
                    <td>${station.phoneNumber}</td>
                    <td class="actions">
                      <a
                        href="editStation?id=${station.stationID}"
                        class="edit-btn"
                      >
                        <i class="fas fa-edit"></i> Sửa
                      </a>
                      <button class="lock-btn" data-id="${station.stationID}">
                        <i class="fas fa-lock"></i>
                        <span>Khóa</span>
                      </button>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty stations}">
                  <tr>
                    <td colspan="8">Không tìm thấy ga nào.</td>
                  </tr>
                </c:if>
              </tbody>
            </table>
          </div>
          <div class="pagination-container" id="pagination-container"></div>
        </section>
        <%-- Closed content section --%>
      </div>
    </div>

    <script>
      function removeDiacritics(str) {
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
      }
      document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("searchInput");
        const stationFormContainer = document.getElementById(
          "stationFormContainer"
        );
        const formTitle = document.getElementById("formTitle");
        const stationForm = document.getElementById("stationForm");
        const stationIDInput = document.getElementById("stationID");
        const stationCodeInput = document.getElementById("stationCode");
        const stationNameInput = document.getElementById("stationName");
        const addressInput = document.getElementById("address");
        const cityInput = document.getElementById("city");
        const regionInput = document.getElementById("region");
        const phoneNumberInput = document.getElementById("phoneNumber");
        const submitButton = document.getElementById("submitButton");
        const cancelButton = document.getElementById("cancelButton");
        const showAddFormBtn = document.getElementById("showAddFormBtn");
        const table = document.getElementById("stationsTable");
        const allRows = Array.from(table.querySelectorAll("tbody tr"));
        const noStationsRow = allRows.find((row) =>
          row.querySelector("td[colspan='8']")
        );
        const dataRows = allRows.filter((row) => row !== noStationsRow);
        const paginationContainer = document.getElementById(
          "pagination-container"
        );
        const rowsPerPage = 10;
        let currentPage = 1;
        let filteredRows = dataRows;

        function displayRows(page) {
          currentPage = page;
          const start = (page - 1) * rowsPerPage;
          const end = start + rowsPerPage;

          dataRows.forEach((row) => (row.style.display = "none"));

          const rowsToShow = filteredRows.slice(start, end);
          rowsToShow.forEach((row) => (row.style.display = ""));

          if (noStationsRow) {
            noStationsRow.style.display =
              filteredRows.length === 0 ? "" : "none";
          }
        }

        function setupPagination() {
          paginationContainer.innerHTML = "";
          const pageCount = Math.ceil(filteredRows.length / rowsPerPage);

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

        function filterAndPaginate() {
          const searchTerm = removeDiacritics(
            searchInput.value.toLowerCase().trim().replace(/\s+/g, " ")
          );

          filteredRows = dataRows.filter((row) => {
            const idCell = removeDiacritics(
              row.cells[0].textContent.toLowerCase()
            );
            const codeCell = removeDiacritics(
              row.cells[1].textContent.toLowerCase()
            );
            const nameCell = removeDiacritics(
              row.cells[2].textContent.toLowerCase()
            );
            return (
              idCell.includes(searchTerm) ||
              codeCell.includes(searchTerm) ||
              nameCell.includes(searchTerm)
            );
          });

          displayRows(1);
          setupPagination();
        }

        if (searchInput) {
          searchInput.addEventListener("keyup", filterAndPaginate);
        }

        showAddFormBtn.addEventListener("click", function () {
          // Redirect to an add station page or show a modal
          // For now, let's assume it redirects
          window.location.href = "addStation";
        });

        document.querySelectorAll(".lock-btn").forEach((button) => {
          button.addEventListener("click", function () {
            const row = this.closest("tr");
            const editLink = row.querySelector(".edit-btn");
            const isLocked = this.classList.toggle("locked");
            const icon = this.querySelector("i");
            const text = this.querySelector("span");

            if (isLocked) {
              editLink.style.pointerEvents = "none";
              editLink.style.opacity = "0.5";
              icon.className = "fas fa-lock-open";
              text.textContent = "Mở khóa";
            } else {
              editLink.style.pointerEvents = "auto";
              editLink.style.opacity = "1";
              icon.className = "fas fa-lock";
              text.textContent = "Khóa";
            }
          });
        });

        filterAndPaginate();
      });
    </script>
  </body>
</html>
