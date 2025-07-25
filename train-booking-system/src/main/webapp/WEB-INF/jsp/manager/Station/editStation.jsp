<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sửa ga</title>
    <link rel="stylesheet" href="<c:url value='/css/common.css'/>" />
    <link
      rel="stylesheet"
      href="<c:url value='/css/manager/manager-dashboard.css'/>"
    />
    <link rel="stylesheet" href="<c:url value='/css/manager/sidebar.css'/>" />
    <link
      rel="stylesheet"
      href="<c:url value='/css/manager/stations/manageStations.css'/>"
    />
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />

    <div class="dashboard-container">
      <div class="main-content">
        <header class="dashboard-header">
          <h1>Sửa ga</h1>
        </header>
        <section class="content-section">
          <form id="stationForm" action="manageStations" method="post">
            <input
              type="hidden"
              id="stationID"
              name="stationID"
              value="${station.stationID}"
            />
            <div class="form-group">
              <label for="stationName">Tên ga: <span class="required-asterisk">*</span></label>
              <input type="text" id="stationName" name="stationName" value="${station.stationName}" required class="form-control" />
            </div>
            <div class="form-group">
              <label for="address">Địa chỉ:</label>
              <input type="text" id="address" name="address" value="${station.address}" class="form-control" />
            </div>
            <div class="form-group">
              <label for="city">Thành phố:</label>
              <input type="text" id="city" name="city" value="${station.city}" class="form-control" />
            </div>
            <div class="form-group">
              <label for="region">Khu vực:</label>
              <input type="text" id="region" name="region" value="${station.region}" class="form-control" />
            </div>
            <div class="form-group">
              <label for="phoneNumber">Số điện thoại:</label>
              <input type="text" id="phoneNumber" name="phoneNumber" value="${station.phoneNumber}" class="form-control" />
            </div>
            <div class="form-group">
              <label for="isActive">Hoạt động:</label>
              <input type="checkbox" id="isActive" name="isActive" value="true" ${station.active ? 'checked' : ''} />
            </div>
            <div class="form-group">
              <button type="submit" name="command" value="edit" class="btn btn-primary">Cập nhật ga</button>
              <a href="manageStations" class="cancel-button">Hủy</a>
            </div>
          </form>
        </section>
      </div>
    </div>
  </body>
</html>
