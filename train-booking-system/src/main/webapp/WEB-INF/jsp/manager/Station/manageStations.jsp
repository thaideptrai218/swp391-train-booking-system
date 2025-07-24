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
    <link
      rel="stylesheet"
      href="<c:url value='/css/manager/stations/manageStations.css'/>"
    />
    <script src="<c:url value='/js/manager/stations/manageStations.js'/>" defer></script>
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css"
    />
  </head>
  <body>
    <jsp:include page="../sidebar.jsp" />

    <div class="dashboard-container">
      <div class="main-content">
        <header class="dashboard-header">
          <h1>Quản lý ga</h1>
        </header>
        <section class="content-section">
          <c:if test="${not empty message}">
            <p class="message ${message.contains('successfully') ? 'success-message' : 'error-message'}">
              ${message}
            </p>
          </c:if>
          <c:if test="${not empty errorMessage}">
            <p class="message error-message">${errorMessage}</p>
          </c:if>

          <div class="controls-container">
            <div class="search-container">
              <input
                type="text"
                id="searchInput"
                placeholder="Tìm kiếm theo ID hoặc Tên..."
              />
            </div>
            <button id="showAddFormBtn" class="action-add">
              <i class="fas fa-plus"></i> Thêm ga mới
            </button>
          </div>

          <div class="table-container">
            <table id="stationsTable">
              <thead>
                <tr>
                  <th>ID</th>
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
                  <tr class="${station.locked ? 'locked-row' : ''}">
                    <td>${station.stationID}</td>
                    <td>${station.stationName}</td>
                    <td>${station.address}</td>
                    <td>${station.city}</td>
                    <td>${station.region}</td>
                    <td>${station.phoneNumber}</td>
                    <td class="actions">
                      <a href="editStation?id=${station.stationID}" class="edit-btn${station.locked ? ' disabled-link' : ''}" ${station.locked ? 'tabindex="-1"' : ''}>
                        <i class="fas fa-edit"></i> Sửa
                      </a>
                      <form action="manageStations" method="POST" style="display:inline-block; margin-left:8px; pointer-events:auto; opacity:1;">
                        <input type="hidden" name="command" value="${station.locked ? 'unlockStation' : 'lockStation'}" />
                        <input type="hidden" name="stationID" value="${station.stationID}" />
                        <button type="submit" class="lock-btn${station.locked ? ' locked' : ''}" style="pointer-events:auto; opacity:1;">
                          <i class="fas ${station.locked ? 'fa-lock-open' : 'fa-lock'}"></i> <span>${station.locked ? 'Mở khóa' : 'Khóa'}</span>
                        </button>
                      </form>
                    </td>
                  </tr>
                </c:forEach>
                <c:if test="${empty stations}">
                  <tr>
                    <td colspan="7">Không tìm thấy ga nào.</td>
                  </tr>
                </c:if>
              </tbody>
            </table>
          </div>
          <div class="pagination-container" id="pagination-container"></div>
        </section>
      </div>
    </div>
  </body>
</html>
