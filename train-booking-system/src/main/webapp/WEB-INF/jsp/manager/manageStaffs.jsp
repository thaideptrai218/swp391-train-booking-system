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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/staffs/manageStaffs.css" />
    <script src="${pageContext.request.contextPath}/js/manager/staffs/manageStaffs.js" defer></script>
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
          <div id="staffFormWarning" class="alert alert-warning" style="display:none;" role="alert">
            Vui lòng nhập đầy đủ Họ và tên, Email, Số điện thoại, và Số CCCD. (Giới tính và Địa chỉ có thể để trống)
          </div>
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

  </body>
</html>
