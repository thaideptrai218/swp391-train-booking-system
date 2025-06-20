<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%-- Core Tag
Library --%> <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Manage Staffs</title>
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

        <div class="d-flex justify-content-between align-items-center mb-3">
          <h1>Manage Staff Users</h1>
          <div class="d-flex align-items-center">
            <div class="search-container mr-2">
              <input
                type="text"
                id="staffSearchInput"
                class="form-control form-control-sm"
                placeholder="Search ID, Name, Email, Phone, ID Card..."
              />
            </div>
            <button class="btn btn-success btn-sm" onclick="openAddModal()">
              <i class="fas fa-plus"></i> Add New Staff
            </button>
          </div>
        </div>

        <div class="table-responsive">
          <table class="table table-striped table-bordered table-hover">
            <thead class="thead-dark">
              <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Phone Number</th>
                <th>ID Card Number</th>
                <th>Role</th>
                <th>Active</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="user" items="${staffUsers}">
                <tr>
                  <td><c:out value="${user.userID}"></c:out></td>
                  <td><c:out value="${user.fullName}"></c:out></td>
                  <td><c:out value="${user.email}"></c:out></td>
                  <td><c:out value="${user.phoneNumber}"></c:out></td>
                  <td><c:out value="${user.idCardNumber}"></c:out></td>
                  <td><c:out value="${user.role}"></c:out></td>
                  <td>
                    <c:choose>
                      <c:when test="${user.active}">
                        <span class="badge badge-success">Yes</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge badge-danger">No</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="table-actions">
                    <button
                      class="btn btn-sm btn-primary"
                      onclick="openEditModal('${user.userID}')"
                    >
                      <i class="fas fa-edit"></i> Edit
                    </button>
                    <button
                      class="btn btn-sm btn-danger"
                      onclick="deleteStaff('${user.userID}')"
                    >
                      <i class="fas fa-trash"></i> Delete
                    </button>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty staffUsers}">
                <tr>
                  <td colspan="8" class="text-center">No staff users found.</td>
                </tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Add/Edit Staff Modal -->
    <div
      class="modal fade"
      id="staffModal"
      tabindex="-1"
      role="dialog"
      aria-labelledby="staffModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-lg" role="document">
        <div class="modal-content">
          <form
            id="staffForm"
            method="post"
            action="${pageContext.request.contextPath}/manageStaffs"
          >
            <input type="hidden" name="action" id="formAction" />
            <input type="hidden" name="userID" id="userID" />
            <div class="modal-header">
              <h5 class="modal-title" id="staffModalLabel">Staff Details</h5>
              <button
                type="button"
                class="close"
                data-dismiss="modal"
                aria-label="Close"
              >
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <div class="form-row">
                <div class="form-group col-md-6">
                  <label for="fullName">Full Name:</label>
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
                  <label for="phoneNumber">Phone Number:</label>
                  <input
                    type="text"
                    class="form-control"
                    id="phoneNumber"
                    name="phoneNumber"
                    required
                    pattern="\d{10}"
                    title="Phone number must be 10 digits."
                  />
                </div>
                <div class="form-group col-md-6">
                  <label for="idCardNumber">ID Card Number:</label>
                  <input
                    type="text"
                    class="form-control"
                    id="idCardNumber"
                    name="idCardNumber"
                    pattern="\d{12}"
                    title="ID Card number must be 12 digits."
                  />
                </div>
              </div>
              <!-- Password field removed for edit mode by manager -->
              <!-- The JavaScript will need to handle showing this field only for 'add' mode -->
              <div class="form-group" id="passwordFieldContainer">
                <label for="password">Password:</label>
                <input
                  type="password"
                  class="form-control"
                  id="password"
                  name="password"
                  placeholder="Enter password for new staff"
                />
              </div>
              <div class="form-row">
                <div class="form-group col-md-6">
                  <label for="role">Role:</label>
                  <select id="role" name="role" class="form-control" required>
                    <option value="Staff">Staff</option>
                    <option value="Manager">Manager</option>
                  </select>
                </div>
                <div class="form-group col-md-6 d-flex align-items-center">
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
                      >Active</label
                    >
                  </div>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn btn-secondary"
                data-dismiss="modal"
              >
                Close
              </button>
              <button type="submit" class="btn btn-primary">Save Staff</button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Bootstrap JS and dependencies -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <!-- Custom JS for sidebar is already included in sidebar.jsp -->

    <script>
      var contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/js/manager/manageStaffs.js"></script>

    <script>
      document.addEventListener("DOMContentLoaded", function () {
        const searchInput = document.getElementById("staffSearchInput");
        const tableBody = document.querySelector(".table-responsive tbody");
        const allRows = Array.from(tableBody.querySelectorAll("tr"));
        const noStaffRowHTML =
          '<tr><td colspan="8" class="text-center">No staff users match your search.</td></tr>';
        const originalNoStaffRow = tableBody.querySelector('td[colspan="8"]');

        if (searchInput) {
          searchInput.addEventListener("keyup", function () {
            const searchTerm =
              searchInput.value.trim(); /* Removed .toLowerCase() */
            let visibleRows = 0;

            // Clear current rows except header
            // While loop to remove all child nodes (rows) from tableBody
            while (tableBody.firstChild) {
              tableBody.removeChild(tableBody.firstChild);
            }

            allRows.forEach((row) => {
              // Skip the original "No staff users found" row if it's in allRows
              if (row === originalNoStaffRow?.parentNode) {
                return;
              }

              const idCell =
                row.cells[0].textContent; /* Removed .toLowerCase() */
              const nameCell =
                row.cells[1].textContent; /* Removed .toLowerCase() */
              const emailCell =
                row.cells[2].textContent; /* Removed .toLowerCase() */
              const phoneCell =
                row.cells[3].textContent; /* Removed .toLowerCase() */
              const idCardCell =
                row.cells[4].textContent; /* Removed .toLowerCase() */

              if (
                idCell.includes(searchTerm) ||
                nameCell.includes(searchTerm) ||
                emailCell.includes(searchTerm) ||
                phoneCell.includes(searchTerm) ||
                idCardCell.includes(searchTerm)
              ) {
                // row.style.display = ""; // Re-append the row if it matches
                tableBody.appendChild(row);
                visibleRows++;
              } else {
                // row.style.display = "none"; // No need to do this, just don't append
              }
            });

            if (
              visibleRows === 0 &&
              allRows.length > 0 &&
              (allRows.length > 1 || !originalNoStaffRow)
            ) {
              // If no data rows are visible and there were originally rows (or no original "no staff" message)
              tableBody.insertAdjacentHTML("beforeend", noStaffRowHTML);
            } else if (
              visibleRows === 0 &&
              originalNoStaffRow &&
              searchTerm === ""
            ) {
              // If search is empty and the only row was the original "no staff" message
              tableBody.appendChild(originalNoStaffRow.parentNode);
            } else if (
              visibleRows > 0 &&
              tableBody.querySelector('td[colspan="8"]')
            ) {
              // If rows are visible and a "no match" message is there, remove it
              // This case is handled by clearing and re-adding.
            }
            if (
              searchTerm === "" &&
              originalNoStaffRow &&
              allRows.length === 1 &&
              allRows[0] === originalNoStaffRow.parentNode
            ) {
              // Special case: if the only "row" in allRows was the original "no staff" message
              if (!tableBody.contains(originalNoStaffRow.parentNode)) {
                tableBody.appendChild(originalNoStaffRow.parentNode);
              }
            }
          });
        }
      });
    </script>
  </body>
</html>
