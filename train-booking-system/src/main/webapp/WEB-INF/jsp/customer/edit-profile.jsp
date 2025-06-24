<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Profile</title>
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
              <li class="breadcrumb-item"><a href="javascript:void(0)">Tài Khoản</a></li>
              <li class="breadcrumb-item active" aria-current="page">Thông tin Tài Khoản</li>
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
                <a class="btn btn-secondary btn-sm" style="margin-left: 1rem;" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
              </div>
            </div>

            <div class="col-md-8">
                <div class="profile-container">
                    <h2>Chỉnh sửa Hồ Sơ</h2>
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="success-message">${successMessage}</div>
        </c:if>

        <form action="<c:url value="/editprofile"/>" method="post">
            <div class="form-group">
                <label for="fullName">Họ và tên:</label>
                <input type="text" id="fullName" name="fullName" value="${user.fullName}" required>
            </div>
            <div class="form-group">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" value="${user.email}" readonly>
            </div>
            <div class="form-group">
                <label for="phoneNumber">Số điện thoại:</label>
                <input type="tel" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
            </div>
            <div class="form-group">
                <label for="idCardNumber">Số CMND/CCCD:</label>
                <input type="text" id="idCardNumber" name="idCardNumber" value="${user.idCardNumber}">
            </div>
            <div class="form-group">
                <label for="dateOfBirth">Ngày sinh:</label>
                <input type="date" id="dateOfBirth" name="dateOfBirth" value="${user.dateOfBirth}">
            </div>
            <div class="form-group">
                <label for="gender">Giới tính:</label>
                <select id="gender" name="gender">
                    <option value="">Chọn Giới Tính</option>
                    <option value="Male" ${user.gender == 'Male' ? 'selected' : ''}>Nam</option>
                    <option value="Female" ${user.gender == 'Female' ? 'selected' : ''}>Nữ</option>
                    <option value="Other" ${user.gender == 'Other' ? 'selected' : ''}>Khác</option>
                </select>
            </div>
            <div class="form-group">
                <label for="address">Địa chỉ:</label>
                <input type="text" id="address" name="address" value="${user.address}">
            </div>
            <button type="submit" class="btn btn-primary">Lưu Thay Đổi</button>
        </form>
    </div>

    <c:import url="/WEB-INF/jsp/common/footer.jsp" />
<style type="text/css">
body{
    margin-top:20px;
    color: #1a202c;
    text-align: left;
    background-color: #e2e8f0;    
}
.main-body {
    padding: 15px;
}
.card {
    box-shadow: 0 1px 3px 0 rgba(0,0,0,.1), 0 1px 2px 0 rgba(0,0,0,.06);
}

.card {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    border: 0 solid rgba(0,0,0,.125);
    border-radius: .25rem;
}

.card-body {
    flex: 1 1 auto;
    min-height: 1px;
    padding: 1rem;
}

.gutters-sm {
    margin-right: -8px;
    margin-left: -8px;
}

.gutters-sm>.col, .gutters-sm>[class*=col-] {
    padding-right: 8px;
    padding-left: 8px;
}
.mb-3, .my-3 {
    margin-bottom: 1rem!important;
}

.bg-gray-300 {
    background-color: #e2e8f0;
}
.h-100 {
    height: 100%!important;
}
.shadow-none {
    box-shadow: none!important;
}
</style>
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
