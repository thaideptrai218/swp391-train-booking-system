<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
      <!DOCTYPE html>
      <html lang="en">

      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hồ sơ Người dùng</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/customer-profile.css">
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
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none"
                          stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                          class="feather feather-globe mr-2 icon-inline">
                          <circle cx="12" cy="12" r="10"></circle>
                          <line x1="2" y1="12" x2="22" y2="12"></line>
                          <path
                            d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z">
                          </path>
                        </svg>
                        Website
                      </h6>
                      <span class="text-secondary">https://vnrailway.vn</span>
                    </li>
                  </ul>
                  <a class="btn btn-secondary btn-sm" style="margin-left: 1rem;"
                    href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                </div>
              </div>
              <div class="col-md-8">
                <div class="card mb-3">
                  <div class="card-body">
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Họ và tên</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        ${user.fullName}
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Email</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        ${user.email}
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Số điện thoại</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        ${user.phoneNumber}
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Số CMND</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        ${user.idCardNumber}
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Địa chỉ</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        ${user.address}
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Giới tính</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        <c:out value="${user.gender}" />
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-3">
                        <h6 class="mb-0">Ngày sinh</h6>
                      </div>
                      <div class="col-sm-9 text-secondary">
                        <fmt:formatDate value="${dateOfBirth}" pattern="yyyy-MM-dd" />
                      </div>
                    </div>
                    <hr>
                    <div class="row">
                      <div class="col-sm-12 text-center">
                        <a class="btn btn-info" href="${pageContext.request.contextPath}/editprofile">Chỉnh sửa</a>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="card mt-3">
                  <div class="card-body">
                    <h6 class="d-flex align-items-center mb-3"><i class="material-icons text-info mr-2">Thẻ</i>VIP</h6>
                    <c:choose>
                      <c:when test="${not empty userVIPCard}">
                        <p><strong>Loại thẻ:</strong> ${userVIPCard.vipCardTypeID}</p>
                        <p><strong>Ngày hết hạn:</strong> <fmt:formatDate value="${userVIPCard.expiryDate}" pattern="dd-MM-yyyy" /></p>
                      </c:when>
                      <c:otherwise>
                        <p>Bạn chưa có thẻ VIP.</p>
                        <a href="${pageContext.request.contextPath}/vip-purchase" class="btn btn-primary">Mua thẻ VIP</a>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>
              </div>
            </div>
            <style type="text/css">
              body {
                margin-top: 20px;
                color: #1a202c;
                text-align: left;
                background-color: #e2e8f0;
              }

              .main-body {
                padding: 15px;
              }

              .card {
                box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .1), 0 1px 2px 0 rgba(0, 0, 0, .06);
              }

              .card {
                position: relative;
                display: flex;
                flex-direction: column;
                min-width: 0;
                word-wrap: break-word;
                background-color: #fff;
                background-clip: border-box;
                border: 0 solid rgba(0, 0, 0, .125);
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

              .gutters-sm>.col,
              .gutters-sm>[class*=col-] {
                padding-right: 8px;
                padding-left: 8px;
              }

              .mb-3,
              .my-3 {
                margin-bottom: 1rem !important;
              }

              .bg-gray-300 {
                background-color: #e2e8f0;
              }

              .h-100 {
                height: 100% !important;
              }

              .shadow-none {
                box-shadow: none !important;
              }
            </style>
            <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.5.0/dist/js/bootstrap.bundle.min.js"></script>
      </body>

      </html>