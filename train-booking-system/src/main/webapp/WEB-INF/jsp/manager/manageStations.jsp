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

          <!-- Form for adding/editing stations -->
          <div
            id="stationFormContainer"
            class="form-section"
            style="display: none"
          >
            <h2 id="formTitle"></h2>
            <form id="stationForm" action="manageStations" method="post">
              <input type="hidden" id="stationID" name="stationID" value="" />
              <div class="form-group">
                <label for="stationCode"
                  >Mã ga: <span class="required-asterisk">*</span></label
                >
                <input
                  type="text"
                  id="stationCode"
                  name="stationCode"
                  required
                />
              </div>
              <div class="form-group">
                <label for="stationName"
                  >Tên ga: <span class="required-asterisk">*</span></label
                >
                <input
                  type="text"
                  id="stationName"
                  name="stationName"
                  required
                />
              </div>
              <div class="form-group">
                <label for="address">Địa chỉ:</label>
                <input type="text" id="address" name="address" />
              </div>
              <div class="form-group">
                <label for="city">Thành phố:</label>
                <input type="text" id="city" name="city" />
              </div>
              <div class="form-group">
                <label for="region">Khu vực:</label>
                <input type="text" id="region" name="region" />
              </div>
              <div class="form-group">
                <label for="phoneNumber">Số điện thoại:</label>
                <input type="text" id="phoneNumber" name="phoneNumber" />
              </div>
              <div class="form-group">
                <button type="submit" name="command" id="submitButton"></button>
                <button type="button" id="cancelButton">Hủy</button>
              </div>
            </form>
          </div>

          <div class="controls-container">
            <%-- Controls container for search and add button --%>
            <div class="search-container">
              <input
                type="text"
                id="searchInput"
                placeholder="Tìm kiếm theo ID, Mã, hoặc Tên..."
              />
              <button id="searchBtn" class="action-search">
                <i class="fas fa-search"></i> Tìm kiếm
              </button>
            </div>
            <button id="showAddFormBtn" class="action-add">
              <%-- Changed class --%> <i class="fas fa-plus"></i> Thêm ga mới
            </button>
          </div>

          <div class="table-container">
            <%-- Changed class and removed h2 --%>
            <table>
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
                      <button
                        class="edit-btn"
                        data-id="${station.stationID}"
                        data-code="${station.stationCode}"
                        data-name="${station.stationName}"
                        data-address="${station.address}"
                        data-city="${station.city}"
                        data-region="${station.region}"
                        data-phone="${station.phoneNumber}"
                      >
                        <i class="fas fa-edit"></i> Sửa
                      </button>
                      <button class="delete-btn" data-id="${station.stationID}">
                        <i class="fas fa-trash"></i> Xóa
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
        </section>
        <%-- Closed content section --%>
      </div>
    </div>

    <script>
      function removeDiacritics(str) {
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
      }
      document.addEventListener("DOMContentLoaded", function () {
        console.log("DOM Content Loaded: manageStations.jsp script started.");

        const searchInput = document.getElementById("searchInput");
        const searchBtn = document.getElementById("searchBtn");
        const stationTableRows = document.querySelectorAll(
          ".table-container tbody tr"
        );

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

        document.querySelectorAll(".edit-btn").forEach((button) => {
          button.addEventListener("click", function () {
            console.log("Edit button clicked. Dataset:", this.dataset);

            stationIDInput.value = this.dataset.id;
            stationCodeInput.value = this.dataset.code;
            stationNameInput.value = this.dataset.name;
            addressInput.value =
              this.dataset.address === "null" ? "" : this.dataset.address;
            cityInput.value =
              this.dataset.city === "null" ? "" : this.dataset.city;
            regionInput.value =
              this.dataset.region === "null" ? "" : this.dataset.region;
            phoneNumberInput.value =
              this.dataset.phone === "null" ? "" : this.dataset.phone;

            formTitle.textContent = "Sửa ga";
            submitButton.value = "edit";
            submitButton.textContent = "Cập nhật ga";

            stationFormContainer.style.display = "block";
            stationFormContainer.scrollIntoView({ behavior: "smooth" });
            console.log("Form populated for editing.");
          });
        });

        showAddFormBtn.addEventListener("click", function () {
          console.log("Add New Station button clicked.");
          stationForm.reset();
          stationIDInput.value = "";

          formTitle.textContent = "Thêm ga mới";
          submitButton.value = "add";
          submitButton.textContent = "Thêm ga";

          stationFormContainer.style.display = "block";
          stationFormContainer.scrollIntoView({ behavior: "smooth" });
          console.log("Form reset for new station.");
        });

        cancelButton.addEventListener("click", function () {
          console.log("Cancel button clicked.");
          stationFormContainer.style.display = "none";
          stationForm.reset();
          console.log("Form hidden and reset.");
        });

        document.querySelectorAll(".delete-btn").forEach((button) => {
          button.addEventListener("click", function () {
            console.log("Delete button clicked. Station ID:", this.dataset.id);
            if (confirm("Bạn có chắc chắn muốn xóa ga này không?")) {
              const stationId = this.dataset.id;
              const form = document.createElement("form");
              form.method = "post";
              form.action = "manageStations";

              const idInput = document.createElement("input");
              idInput.type = "hidden";
              idInput.name = "stationID";
              idInput.value = stationId;
              form.appendChild(idInput);

              const commandInput = document.createElement("input");
              commandInput.type = "hidden";
              commandInput.name = "command";
              commandInput.value = "delete";
              form.appendChild(commandInput);

              document.body.appendChild(form);
              form.submit();
              console.log("Delete form submitted.");
            } else {
              console.log("Delete cancelled.");
            }
          });
        });

        const tableBody = document.querySelector(".table-container tbody");
        const allRows = Array.from(tableBody.querySelectorAll("tr"));
        const noResultsRowHTML =
          '<tr><td colspan="8" style="text-align: center;">Không tìm thấy ga nào khớp.</td></tr>';

        function filterTable() {
          const searchTerm = removeDiacritics(
            searchInput.value.toLowerCase().trim().replace(/\s+/g, " ")
          );
          let visibleRows = 0;

          while (tableBody.firstChild) {
            tableBody.removeChild(tableBody.firstChild);
          }

          allRows.forEach((row) => {
            if (row.querySelector('td[colspan="8"]')) {
              return;
            }

            const idCell = removeDiacritics(
              row.cells[0].textContent.toLowerCase()
            );
            const codeCell = removeDiacritics(
              row.cells[1].textContent.toLowerCase()
            );
            const nameCell = removeDiacritics(
              row.cells[2].textContent.toLowerCase()
            );

            if (
              idCell.includes(searchTerm) ||
              codeCell.includes(searchTerm) ||
              nameCell.includes(searchTerm)
            ) {
              tableBody.appendChild(row);
              visibleRows++;
            }
          });

          if (visibleRows === 0) {
            tableBody.insertAdjacentHTML("beforeend", noResultsRowHTML);
          }
        }

        searchBtn.addEventListener("click", filterTable);
        searchInput.addEventListener("keyup", function (event) {
          filterTable();
        });
      });
    </script>
  </body>
</html>
