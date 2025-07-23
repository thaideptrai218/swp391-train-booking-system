<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Thêm Chuyến Đi Mới</title>
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
    <style>
      body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        line-height: 1.6;
        margin: 0;
      }
      .dashboard-container {
        display: flex;
      }
      .main-content {
        flex-grow: 1;
        padding: 25px;
        background-color: #ffffff;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        margin: 20px;
      }
      .dashboard-header h1 {
        color: #343a40;
        margin-bottom: 25px;
        font-weight: 400;
        border-bottom: 1px solid #e9ecef;
        padding-bottom: 15px;
        font-size: 1.8em;
      }
      .form-container {
        max-width: 700px;
        margin: 0 auto;
        padding: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 1px 5px rgba(0, 0, 0, 0.1);
      }
      .form-group {
        margin-bottom: 1.25rem;
      }
      .form-group label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: #495057;
      }
      .form-group input[type="text"],
      .form-group input[type="datetime-local"],
      .form-group input[type="number"],
      .form-group select,
      .form-group textarea {
        width: 100%;
        padding: 0.5rem 0.75rem;
        font-size: 0.9rem;
        line-height: 1.5;
        color: #495057;
        background-color: #fff;
        background-clip: padding-box;
        border: 1px solid #ced4da;
        border-radius: 0.25rem;
        transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
        box-sizing: border-box;
      }
      .form-group input[type="datetime-local"] {
        appearance: none;
        -webkit-appearance: none;
      } /* Basic reset for datetime-local */
      .form-group input:focus,
      .form-group select:focus,
      .form-group textarea:focus {
        border-color: #80bdff;
        outline: 0;
        box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
      }
      .btn-submit {
        display: inline-block;
        font-weight: 400;
        color: #fff;
        text-align: center;
        vertical-align: middle;
        cursor: pointer;
        background-color: #007bff;
        border: 1px solid #007bff;
        padding: 0.5rem 1rem;
        font-size: 1rem;
        line-height: 1.5;
        border-radius: 0.25rem;
        transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out,
          border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
      }
      .btn-submit:hover {
        background-color: #0056b3;
        border-color: #0056b3;
      }
      .btn-cancel {
        display: inline-block;
        font-weight: 400;
        color: #212529;
        text-align: center;
        vertical-align: middle;
        cursor: pointer;
        background-color: #f8f9fa;
        border: 1px solid #ced4da;
        padding: 0.5rem 1rem;
        font-size: 1rem;
        line-height: 1.5;
        border-radius: 0.25rem;
        text-decoration: none;
        margin-left: 10px;
      }
      .btn-cancel:hover {
        background-color: #e2e6ea;
        border-color: #dae0e5;
      }
      .form-actions {
        margin-top: 20px;
        text-align: right;
      }
      .form-group input[type="number"]#basePriceMultiplier:read-only {
        background-color: #e9ecef;
        cursor: not-allowed;
      }
    </style>
    <script>
      function toggleMultiplierEditability() {
        var isHolidaySelect = document.getElementById("isHolidayTrip");
        var multiplierInput = document.getElementById("basePriceMultiplier");
        if (isHolidaySelect.value === "true") {
          multiplierInput.readOnly = false;
        } else {
          multiplierInput.readOnly = true;
          multiplierInput.value = "1.00"; // Reset if not holiday
        }
      }

      function validateDateTime() {
        // No longer needed as arrival time is removed, but keep if other validations are added.
        // For now, it can be an empty function or removed if no other client-side validation.
        return true;
      }

      function toggleHolidayFields() {
        var isHoliday =
          document.getElementById("isHolidayTrip").value === "true";
        var holidaySelectGroup = document.getElementById("holidaySelectGroup");
        var holidayId = document.getElementById("holidayId");
        var multiplierInput = document.getElementById("basePriceMultiplier");
        if (isHoliday) {
          holidaySelectGroup.style.display = "";
          multiplierInput.readOnly = true;
          if (holidayId.value) {
            var selected = holidayId.options[holidayId.selectedIndex];
            var discount = selected.getAttribute("data-discount");
            if (discount && !isNaN(discount)) {
              multiplierInput.value = (parseFloat(discount) / 100).toFixed(2);
            }
          }
        } else {
          holidaySelectGroup.style.display = "none";
          multiplierInput.value = "1.00";
          multiplierInput.readOnly = true;
          holidayId.value = "";
        }
      }

      document.addEventListener("DOMContentLoaded", function () {
        const now = new Date();
        now.setSeconds(0, 0);
        now.setMilliseconds(0);

        // Format the date for the 'min' attribute of the datetime-local input
        const yyyy = now.getFullYear();
        const mm = String(now.getMonth() + 1).padStart(2, "0");
        const dd = String(now.getDate()).padStart(2, "0");
        const hh = String(now.getHours()).padStart(2, "0");
        const mins = String(now.getMinutes()).padStart(2, "0");
        const minAttributeValue = `${yyyy}-${mm}-${dd}T${hh}:${mins}`;

        const departureInput = document.getElementById("departureDateTime");

        if (departureInput) {
          departureInput.setAttribute("min", minAttributeValue);

          const validateDate = function () {
            const selectedDate = new Date(this.value);
            if (selectedDate < now) {
              this.value = minAttributeValue; // Reset to the earliest valid datetime
            }
          };

          // Validate on different events to ensure coverage
          departureInput.addEventListener("input", validateDate);
          departureInput.addEventListener("change", validateDate);
        }

        // Add a final check on form submission
        const form = document.querySelector('form[action*="insertTrip"]');
        if (form) {
          form.addEventListener("submit", function (event) {
            const selectedDate = new Date(departureInput.value);
            if (selectedDate < now) {
              event.preventDefault(); // Stop the form from submitting
            }
          });
        }

        document
          .getElementById("isHolidayTrip")
          .addEventListener("change", toggleHolidayFields);
        var holidayId = document.getElementById("holidayId");
        var multiplierInput = document.getElementById("basePriceMultiplier");
        if (holidayId) {
          holidayId.addEventListener("change", function () {
            var selected = this.options[this.selectedIndex];
            var discount = selected.getAttribute("data-discount");
            if (discount && !isNaN(discount)) {
              multiplierInput.value = (parseFloat(discount) / 100).toFixed(2);
            } else {
              multiplierInput.value = "1.00";
            }
          });
        }
      });
    </script>
  </head>
  <body>
    <div class="dashboard-container">
      <jsp:include page="../sidebar.jsp" />
      <div class="main-content">
        <header class="dashboard-header">
          <h1><i class="fas fa-plus-circle"></i> Thêm Chuyến Đi Mới</h1>
        </header>
        <section class="content-section">
          <div class="form-container">
            <form
              action="${pageContext.request.contextPath}/manageTrips"
              method="POST"
            >
              <input type="hidden" name="action" value="insertTrip" />

              <div class="form-group">
                <label for="trainId">Tên Tàu:</label>
                <select
                  name="trainId"
                  id="trainId"
                  class="form-control"
                  required
                >
                  <option value="">-- Chọn Tàu --</option>
                  <c:forEach var="train" items="${requestScope.allTrains}">
                    <option value="${train.trainID}">
                      ${train.trainName} (ID: ${train.trainID})
                    </option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-group">
                <label for="routeId">Tên Tuyến:</label>
                <select
                  name="routeId"
                  id="routeId"
                  class="form-control"
                  required
                >
                  <option value="">-- Chọn Tuyến --</option>
                  <c:forEach var="route" items="${requestScope.allRoutes}">
                    <option value="${route.routeID}">
                      ${route.routeName} (ID: ${route.routeID})
                    </option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-group">
                <label for="departureDateTime">Ngày Giờ Khởi Hành:</label>
                <input
                  type="datetime-local"
                  id="departureDateTime"
                  name="departureDateTime"
                  class="form-control"
                  required
                />
              </div>

              <%-- Arrival DateTime form group removed --%>

              <div class="form-group">
                <label for="isHolidayTrip">Chuyến Lễ:</label>
                <select
                  name="isHolidayTrip"
                  id="isHolidayTrip"
                  class="form-control"
                  onchange="toggleHolidayFields()"
                  required
                >
                  <option value="false">Không</option>
                  <option value="true">Có</option>
                </select>
              </div>

              <div
                class="form-group"
                id="holidaySelectGroup"
                style="display: none"
              >
                <label for="holidayId">Chọn ngày lễ:</label>
                <select name="holidayId" id="holidayId" class="form-control">
                  <option value="">-- Chọn ngày lễ --</option>
                  <c:forEach var="holiday" items="${requestScope.allHolidays}">
                    <option
                      value="${holiday.id}"
                      data-discount="${holiday.discountPercentage}"
                    >
                      ${holiday.holidayName}
                    </option>
                  </c:forEach>
                </select>
              </div>

              <div class="form-group">
                <label for="basePriceMultiplier">Hệ Số Tiền:</label>
                <input
                  type="number"
                  id="basePriceMultiplier"
                  name="basePriceMultiplier"
                  class="form-control"
                  value="1.00"
                  step="0.01"
                  min="0"
                  readonly
                  required
                />
              </div>

              <div class="form-group">
                <label for="tripStatus">Trạng Thái Chuyến Đi:</label>
                <select
                  name="tripStatus"
                  id="tripStatus"
                  class="form-control"
                  required
                >
                  <option value="Scheduled" selected>Lên Lịch</option>
                  <option value="In Progress">Đang Diễn Ra</option>
                  <option value="Completed">Đã Hoàn Thành</option>
                  <option value="Cancelled">Hủy Chuyến</option>
                </select>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn-submit">
                  <i class="fas fa-save"></i> Lưu Chuyến Đi
                </button>
                <a
                  href="${pageContext.request.contextPath}/manageTrips"
                  class="btn-cancel"
                  >Hủy</a
                >
              </div>
            </form>
          </div>
        </section>
      </div>
    </div>
  </body>
</html>
