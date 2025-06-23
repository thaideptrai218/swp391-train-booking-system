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

          <!-- The Modal -->
          <div id="addStationModal" class="modal">
            <!-- Modal content -->
            <div class="modal-content">
              <span class="close-button">&times;</span>
              <div class="form-section">
                <h2>Thêm/Sửa ga</h2>
                <form id="stationForm" action="manageStations" method="post">
                  <input
                    type="hidden"
                    id="stationID"
                    name="stationID"
                    value=""
                  />
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
                    <button
                      type="submit"
                      name="command"
                      id="submitButton"
                      value="add"
                    >
                      Thêm ga
                    </button>
                    <button
                      type="button"
                      id="cancelEditButton"
                      style="display: none"
                    >
                      Hủy chỉnh sửa
                    </button>
                  </div>
                </form>
              </div>
            </div>
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
      document.addEventListener("DOMContentLoaded", function () {
        console.log("DOM Content Loaded: manageStations.jsp script started.");

        const searchInput = document.getElementById("searchInput");
        const searchBtn = document.getElementById("searchBtn");
        const stationTableRows = document.querySelectorAll(
          ".table-container tbody tr"
        );
        const stationForm = document.getElementById("stationForm");
        const stationIDInput = document.getElementById("stationID");
        const stationCodeInput = document.getElementById("stationCode");
        const stationNameInput = document.getElementById("stationName");
        const addressInput = document.getElementById("address");
        const cityInput = document.getElementById("city");
        const regionInput = document.getElementById("region");
        const phoneNumberInput = document.getElementById("phoneNumber");
        const submitButton = document.getElementById("submitButton");
        const cancelEditButton = document.getElementById("cancelEditButton");

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

            submitButton.value = "edit";
            submitButton.textContent = "Cập nhật ga";
            cancelEditButton.style.display = "inline-block";
            addStationModal.classList.add("active"); // <-- ADD THIS LINE TO SHOW MODAL FOR EDIT
            console.log("Form populated for editing. Modal shown.");
          });
        });

        cancelEditButton.addEventListener("click", function () {
          console.log("Cancel Edit button clicked.");
          stationForm.reset();
          stationIDInput.value = "";
          submitButton.value = "add";
          submitButton.textContent = "Thêm ga";
          cancelEditButton.style.display = "none";
          // addStationModal.style.display = "none"; /* Hide modal on cancel */
          addStationModal.classList.remove("active"); /* Hide modal on cancel */
          console.log("Form reset.");
        });

        const showAddFormBtn = document.getElementById("showAddFormBtn");
        const addStationModal = document.getElementById("addStationModal");
        const closeButton = document.querySelector(".close-button");

        showAddFormBtn.addEventListener("click", function () {
          console.log("Add New Station button clicked.");
          // Reset form for adding a new station
          stationForm.reset();
          stationIDInput.value = ""; // Clear any existing station ID
          submitButton.value = "add";
          submitButton.textContent = "Thêm ga";
          cancelEditButton.style.display = "none";
          addStationModal.classList.add("active");
          console.log("Form reset for new station. Modal shown.");
        });

        closeButton.addEventListener("click", function () {
          // addStationModal.style.display = "none";
          addStationModal.classList.remove("active");
        });

        window.addEventListener("click", function (event) {
          if (event.target == addStationModal) {
            // addStationModal.style.display = "none";
            addStationModal.classList.remove("active");
          }
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

        function filterTable() {
          const searchTerm = searchInput.value.toLowerCase().trim();
          stationTableRows.forEach((row) => {
            // Check if this is a "No stations found" row
            if (row.querySelector('td[colspan="8"]')) {
              // If it's the "no stations" message, always show it if no other rows are visible,
              // or hide it if other rows will be visible. This logic is handled implicitly
              // by checking if any data rows are visible later.
              return;
            }

            const idCell = row.cells[0].textContent.toLowerCase();
            const codeCell = row.cells[1].textContent.toLowerCase();
            const nameCell = row.cells[2].textContent.toLowerCase();

            if (
              idCell.includes(searchTerm) ||
              codeCell.includes(searchTerm) ||
              nameCell.includes(searchTerm)
            ) {
              row.style.display = "";
            } else {
              row.style.display = "none";
            }
          });

          // Show "No stations found" if all data rows are hidden by search
          const noStationsRow = document.querySelector(
            ".table-container tbody tr td[colspan='8']"
          );
          if (noStationsRow) {
            let anyRowVisible = false;
            stationTableRows.forEach((row) => {
              if (
                row.style.display !== "none" &&
                !row.querySelector('td[colspan="8"]')
              ) {
                anyRowVisible = true;
              }
            });
            if (!anyRowVisible && searchTerm !== "") {
              // Only show if search term is active and no results
              // If the "no stations found" row was part of the original items, it might be hidden.
              // We might need to dynamically add/remove it or ensure it's correctly handled.
              // For simplicity, let's assume it's always there and we just control its display.
              // This part might need refinement based on how `empty stations` is handled.
              // A better approach might be to have a dedicated "no results from search" message.
            }
          }
        }

        searchBtn.addEventListener("click", filterTable);
        searchInput.addEventListener("keyup", function (event) {
          // Optional: Trigger search on Enter key as well
          // if (event.key === "Enter") {
          //   filterTable();
          // }
          // For live search on every key press:
          filterTable();
        });
      });
    </script>
  </body>
</html>
