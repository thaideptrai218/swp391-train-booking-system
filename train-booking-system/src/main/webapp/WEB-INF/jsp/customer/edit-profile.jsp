<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa Hồ sơ Người dùng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/edit-profile.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <div class="main-body">
    
          <!-- Breadcrumb -->
          <nav aria-label="breadcrumb" class="main-breadcrumb">
            <ol class="breadcrumb">
              <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/landing">Trang chủ</a></li>
              <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/customerprofile">Tài Khoản</a></li>
              <li class="breadcrumb-item active" aria-current="page">Chỉnh sửa Hồ sơ</li>
            </ol>
          </nav>
          <!-- /Breadcrumb -->
    
          <div class="row gutters-sm">
            <div class="col-md-4 mb-3">
              <div class="card">
                <div class="card-body">
                  <div class="d-flex flex-column align-items-center text-center">
                    <img src="${avatarURL}" alt="Admin" class="rounded-circle" width="150">
                    <div class="mt-3">
                      <h4>${user.fullName}</h4>
                      <p class="text-secondary mb-1">Khách Hàng</p>
                      <p class="text-muted font-size-sm">Việt Nam</p>
                      <!-- <button class="btn btn-primary">Follow</button>
                      <button class="btn btn-outline-primary">Message</button> -->
                    </div>
                  </div>
                </div>
              </div>
              <div class="card mt-3">
                <ul class="list-group list-group-flush">
                  <li class="list-group-item d-flex justify-content-between align-items-center flex-wrap">
                    <h6 class="mb-0">
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-globe mr-2 icon-inline">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="2" y1="12" x2="22" y2="12"></line>
                        <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
                      </svg>
                      Website
                    </h6>
                    <span class="text-secondary">https://vnrailway.vn</span>
                  </li>
                </ul>
                <a class="btn btn-secondary btn-sm btn-logout-custom" style="margin-left: 1rem;text-align:center" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
              </div>
            </div>

            <div class="col-md-8">
              <div class="card mb-3">
                <div class="card-body">
                  <h2 class="mb-3 text-center">Chỉnh sửa Hồ Sơ</h2>
                  <c:if test="${not empty errorMessage}">
                      <div class="error-message text-center">${errorMessage}</div>

                  </c:if>
                  <c:if test="${not empty successMessage}">
                      <div class="success-message text-center">${successMessage}</div>
                  </c:if>

                  <form action="<c:url value="/editprofile"/>" method="post">
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Họ và tên</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="text" class="form-control" id="fullName" name="fullName" value="${user.fullName}" required>
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Email</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="email" class="form-control" id="email" name="email" value="${user.email}" readonly>
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Số điện thoại</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Số CMND/CCCD</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="text" class="form-control" id="idCardNumber" name="idCardNumber" value="${user.idCardNumber}">
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Ngày sinh</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" value="${user.dateOfBirth}">
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Giới tính</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <select id="gender" name="gender" class="form-control">
                                  <option value="">Chọn Giới Tính</option>
                                  <option value="Male" ${user.gender == 'Male' ? 'selected' : ''}>Nam</option>
                                  <option value="Female" ${user.gender == 'Female' ? 'selected' : ''}>Nữ</option>
                                  <option value="Other" ${user.gender == 'Other' ? 'selected' : ''}>Khác</option>
                              </select>
                          </div>
                      </div>
                      <hr>
                      <div class="row mb-3">
                          <div class="col-sm-3">
                              <h6 class="mb-0">Địa chỉ</h6>
                          </div>
                          <div class="col-sm-9 text-secondary">
                              <input type="text" class="form-control" id="address" name="address" value="${user.address}">
                          </div>
                      </div>
                      <hr>
                      <div class="row">
                          <div class="col-sm-12 text-center">
                              <button type="submit" class="btn btn-info">Lưu Thay Đổi</button>
                          </div>
                      </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
