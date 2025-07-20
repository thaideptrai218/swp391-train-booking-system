<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%-- Core Tag
Library --%> <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý nhân viên</title>
    <!-- Bootstrap CSS -->
    <link
      href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <!-- Font Awesome -->
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <!-- Custom CSS -->
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/manager/sidebar.css"
    />
    <style>
      /* Styles for content area to work with the sidebar.js and sidebar.css */
      .main-content {
        /* padding: 20px; /* Padding is now handled by sidebar.css */
        transition: margin-left 0.3s ease; /* Matches sidebar.css */
        /* Default margin-left can be 0 or small for the button if sidebar is completely hidden */
        /* margin-left: 60px; /* Example: if toggle button area needs space */
      }

      /* .sidebar.collapsed + .content-container logic is now handled by sidebar.js and sidebar.css */
      /* using .main-content and .main-content.shifted */

      .table-actions button {
        margin-right: 5px;
      }
      .modal-header {
        background-color: #007bff;
        color: white;
      }
      .modal-header .close {
        color: white;
      }
      .search-container input[type="text"] {
        min-width: 250px; /* Give search input a decent minimum width */
      }
      /* Adjust alignment if needed for the group holding search and add button */
      .d-flex.justify-content-between.align-items-center
        > .d-flex.align-items-center {
        gap: 0.5rem; /* Add some gap between search input and add button if not using mr-2 effectively */
      }
      .form-section {
        background-color: #f8f9fa;
        padding: 20px;
        border-radius: 8px;
        border: 1px solid #dee2e6;
        margin-bottom: 20px;
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
    <jsp:include page="sidebar.jsp" />

    <div class="main-content">
      <!-- Changed class to main-content -->
      <div class="container-fluid">
        <!-- Flash Messages -->
        <c:if test="${not empty successMessage}">
          <div
            class="alert alert-success alert-dismissible fade show"
            role="alert"
          >
            ${successMessage}
            <button
              type="button"
              class="close"
              data-dismiss="alert"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </c:if>
        <c:if test="${not empty errorMessage}">
          <div
            class="alert alert-danger alert-dismissible fade show"
            role="alert"
          >
            ${errorMessage}
            <button
              type="button"
              class="close"
              data-dismiss="alert"
              aria-label="Close"
            >
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </c:if>

        <h1>Quản lý nhân viên</h1>

        <div id="staffFormContainer" class="form-section" style="display: none">
          <h2 id="formTitle"></h2>
          <form
            id="staffForm"
            method="post"
            action="${pageContext.request.contextPath}/manageStaffs"
          >
            <input type="hidden" name="action" id="formAction" />
            <input type="hidden" name="userID" id="userID" />
            <div class="form-row">
              <div class="form-group col-md-6">
                <label for="fullName">Họ và tên:</label>
                <input
                  type="text"
                  class="form-control"
                  id="fullName"
                  name="fullName"
                  required
                />
              </div>
              <div class="form-group col-md-6">
                <label for="email">Email:</label>
                <input
                  type="email"
                  class="form-control"
                  id="email"
                  name="email"
                  required
                />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group col-md-6">
                <label for="phoneNumber">Số điện thoại:</label>
                <input
                  type="text"
                  class="form-control"
                  id="phoneNumber"
                  name="phoneNumber"
                  required
                  pattern="\d{10}"
                  title="Số điện thoại phải có 10 chữ số."
                />
              </div>
              <div class="form-group col-md-6">
                <label for="idCardNumber">Số CCCD:</label>
                <input
                  type="text"
                  class="form-control"
                  id="idCardNumber"
                  name="idCardNumber"
                  pattern="\d{12}"
                  title="Số CCCD phải có 12 chữ số."
                />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group col-md-6">
                <label for="gender">Giới tính:</label>
                <select id="gender" name="gender" class="form-control">
                  <option value="">Chọn giới tính</option>
                  <option value="Nam">Nam</option>
                  <option value="Nữ">Nữ</option>
                  <option value="Khác">Khác</option>
                </select>
              </div>
              <div class="form-group col-md-6">
                <label for="address">Địa chỉ:</label>
                <input
                  type="text"
                  class="form-control"
                  id="address"
                  name="address"
                />
              </div>
            </div>
            <div
              class="form-group"
              id="passwordFieldContainer"
              style="display: none"
            >
              <label for="password">Mật khẩu:</label>
              <input
                type="password"
                class="form-control"
                id="password"
                name="password"
                placeholder="Nhập mật khẩu cho nhân viên mới"
              />
            </div>
            <div class="form-row">
              <div class="form-group col-md-12 d-flex align-items-center">
                <div class="form-check">
                  <input
                    type="checkbox"
                    class="form-check-input"
                    id="isActive"
                    name="isActive"
                    value="true"
                    checked
                  />
                  <label class="form-check-label" for="isActive"
                    >Hoạt động</label
                  >
                </div>
              </div>
            </div>
            <button type="submit" class="btn btn-primary">Lưu nhân viên</button>
            <button type="button" class="btn btn-secondary" id="cancelButton">
              Hủy
            </button>
          </form>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-3">
          <div class="search-container w-100">
            <input
              type="text"
              id="staffSearchInput"
              class="form-control"
              placeholder="Tìm kiếm ID, Tên, Email, Điện thoại, CCCD..."
            />
          </div>
          <button
            class="btn btn-success btn-sm ml-2"
            id="showAddFormBtn"
            style="white-space: nowrap"
          >
            <i class="fas fa-plus"></i> Thêm nhân viên mới
          </button>
        </div>

        <div class="table-responsive">
          <table
            class="table table-striped table-bordered table-hover"
            id="staffsTable"
          >
            <thead class="thead-dark">
              <tr>
                <th>ID</th>
                <th>Họ và tên</th>
                <th>Email</th>
                <th>Số điện thoại</th>
                <th>Số CCCD</th>
                <th>Gender</th>
                <th>Address</th>
                <th>Hành động</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="user" items="${staffUsers}">
                <tr
                  data-user-id="${user.userID}"
                  data-full-name="${user.fullName}"
                  data-email="${user.email}"
                  data-phone-number="${user.phoneNumber}"
                  data-id-card-number="${user.idCardNumber}"
                  data-gender="${user.gender}"
                  data-address="${user.address}"
                  data-is-active="${user.active}"
                >
                  <td><c:out value="${user.userID}"></c:out></td>
                  <td><c:out value="${user.fullName}"></c:out></td>
                  <td><c:out value="${user.email}"></c:out></td>
                  <td><c:out value="${user.phoneNumber}"></c:out></td>
                  <td><c:out value="${user.idCardNumber}"></c:out></td>
                  <td><c:out value="${user.gender}"></c:out></td>
                  <td><c:out value="${user.address}"></c:out></td>
                  <td class="table-actions">
                    <button class="btn btn-sm btn-primary edit-btn">
                      <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button
                      class="btn btn-sm ${user.active ? 'btn-warning' : 'btn-success'}"
                      onclick="toggleStaffStatus('${user.userID}', '${user.active}')"
                    >
                      <i
                        class="fas ${user.active ? 'fa-lock' : 'fa-lock-open'}"
                      ></i>
                      ${user.active ? 'Khóa' : 'Mở khóa'}
                    </button>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty staffUsers}">
                <tr>
                  <td colspan="8" class="text-center">
                    Không tìm thấy nhân viên nào.
                  </td>
                </tr>
              </c:if>
            </tbody>
          </table>
          <div class="pagination-container" id="pagination-container"></div>
        </div>
      </div>
    </div>

    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- Custom JS for sidebar is already included in sidebar.jsp -->

    <script>
      function toggleStaffStatus(userId, isActive) {
        const message = isActive
          ? "Bạn có chắc chắn muốn khóa nhân viên này không?"
          : "Bạn có chắc chắn muốn mở khóa nhân viên này không?";
        if (confirm(message)) {
          const form = document.createElement("form");
          form.method = "post";
          form.action = "${pageContext.request.contextPath}/manageStaffs";

          const actionInput = document.createElement("input");
          actionInput.type = "hidden";
          actionInput.name = "action";
          actionInput.value = "toggleStatus";
          form.appendChild(actionInput);

          const idInput = document.createElement("input");
          idInput.type = "hidden";
          idInput.name = "userID";
          idInput.value = userId;
          form.appendChild(idInput);

          document.body.appendChild(form);
          form.submit();
        }
      }

      function removeDiacritics(str) {
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
      }
      document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("staffSearchInput");
        const table = document.getElementById("staffsTable");
        const allRows = Array.from(table.querySelectorAll("tbody tr"));
        const noStaffRow = allRows.find((row) =>
          row.querySelector("td[colspan='8']")
        );
        const dataRows = allRows.filter((row) => row !== noStaffRow);
        const paginationContainer = document.getElementById(
          "pagination-container"
        );
        const rowsPerPage = 10;
        let currentPage = 1;
        let filteredRows = dataRows;

        const staffFormContainer =
          document.getElementById("staffFormContainer");
        const formTitle = document.getElementById("formTitle");
        const staffForm = document.getElementById("staffForm");
        const formAction = document.getElementById("formAction");
        const userID = document.getElementById("userID");
        const fullName = document.getElementById("fullName");
        const email = document.getElementById("email");
        const phoneNumber = document.getElementById("phoneNumber");
        const idCardNumber = document.getElementById("idCardNumber");
        const gender = document.getElementById("gender");
        const address = document.getElementById("address");
        const passwordFieldContainer = document.getElementById(
          "passwordFieldContainer"
        );
        const passwordInput = document.getElementById("password");
        const isActive = document.getElementById("isActive");
        const showAddFormBtn = document.getElementById("showAddFormBtn");
        const cancelButton = document.getElementById("cancelButton");

        showAddFormBtn.addEventListener("click", function () {
          staffForm.reset();
          formTitle.textContent = "Thêm nhân viên mới";
          formAction.value = "add";
          userID.value = "";
          passwordFieldContainer.style.display = "none";
          passwordInput.required = false;
          staffFormContainer.style.display = "block";
          staffFormContainer.scrollIntoView({ behavior: "smooth" });
        });

        document.querySelectorAll(".edit-btn").forEach((button) => {
          button.addEventListener("click", function () {
            const row = this.closest("tr");
            if (row.dataset.isActive === "false") {
              alert("Không thể sửa thông tin nhân viên đã bị khóa.");
              return;
            }
            formTitle.textContent = "Sửa thông tin nhân viên";
            formAction.value = "update";
            userID.value = row.dataset.userId;
            fullName.value = row.dataset.fullName;
            email.value = row.dataset.email;
            phoneNumber.value = row.dataset.phoneNumber;
            idCardNumber.value = row.dataset.idCardNumber;
            gender.value = row.dataset.gender;
            address.value = row.dataset.address;
            isActive.checked = row.dataset.isActive === "true";

            passwordFieldContainer.style.display = "none";
            passwordInput.required = false;

            staffFormContainer.style.display = "block";
            staffFormContainer.scrollIntoView({ behavior: "smooth" });
          });
        });

        cancelButton.addEventListener("click", function () {
          staffFormContainer.style.display = "none";
          staffForm.reset();
        });

        function displayRows(page) {
          currentPage = page;
          const start = (page - 1) * rowsPerPage;
          const end = start + rowsPerPage;

          dataRows.forEach((row) => (row.style.display = "none"));
          const rowsToShow = filteredRows.slice(start, end);
          rowsToShow.forEach((row) => (row.style.display = ""));

          if (noStaffRow) {
            noStaffRow.style.display = filteredRows.length === 0 ? "" : "none";
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
            searchInput.value.trim().toLowerCase().replace(/\s+/g, " ")
          );

          filteredRows = dataRows.filter((row) => {
            const idCell = removeDiacritics(
              row.cells[0].textContent.toLowerCase()
            );
            const nameCell = removeDiacritics(
              row.cells[1].textContent.toLowerCase()
            );
            const emailCell = removeDiacritics(
              row.cells[2].textContent.toLowerCase()
            );
            const phoneCell = removeDiacritics(
              row.cells[3].textContent.toLowerCase()
            );
            const idCardCell = removeDiacritics(
              row.cells[4].textContent.toLowerCase()
            );

            return (
              idCell.includes(searchTerm) ||
              nameCell.includes(searchTerm) ||
              emailCell.includes(searchTerm) ||
              phoneCell.includes(searchTerm) ||
              idCardCell.includes(searchTerm)
            );
          });

          displayRows(1);
          setupPagination();
        }

        if (searchInput) {
          searchInput.addEventListener("keyup", filterAndPaginate);
        }

        filterAndPaginate();
      });
    </script>
  </body>
</html>
