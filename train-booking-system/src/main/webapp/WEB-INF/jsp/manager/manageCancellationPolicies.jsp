<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <!DOCTYPE html>
    <html lang="vi">

    <head>
      <meta charset="UTF-8" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <title>Chính sách hủy vé</title>
      <link rel="stylesheet" href="<c:url value='/css/common.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/manager/manager-dashboard.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/manager/sidebar.css'/>" />
      <link rel="stylesheet" href="<c:url value='/css/manager/cancellationPolicies.css'/>" />
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
      <style>
        .content-section {
          padding: 20px;
          background-color: #fff;
          border-radius: 12px;
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
        }

        .policy-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
        }

        .policy-header h2 {
          font-size: 1.5rem;
          color: #333;
        }

        .btn-add {
          background-color: #28a745;
          color: #fff;
          padding: 8px 15px;
          border-radius: 8px;
          text-decoration: none;
          transition: background 0.3s ease;
        }

        .btn-add:hover {
          background-color: #218838;
        }

        .styled-table {
          width: 100%;
          border-collapse: collapse;
          border-radius: 8px;
          overflow: hidden;
          font-size: 0.95rem;
        }

        .styled-table thead {
          background-color: #007bff;
          color: #fff;
        }

        .styled-table th,
        .styled-table td {
          padding: 12px 16px;
          text-align: center;
          border-bottom: 1px solid #ddd;
        }

        .styled-table tbody tr:hover {
          background-color: #f1f1f1;
        }

        .status-active {
          color: green;
          font-weight: bold;
        }

        .status-inactive {
          color: #999;
          font-style: italic;
        }

        .btn-action {
          padding: 5px 10px;
          border-radius: 6px;
          font-size: 0.85rem;
          text-decoration: none;
          margin: 0 4px;
        }

        .btn-action.edit {
          background-color: #ffc107;
          color: #000;
        }

        .btn-action.edit:hover {
          background-color: #e0a800;
        }

        .success-message {
          color: green;
          margin-bottom: 10px;
          font-weight: bold;
        }

        .error-message {
          color: red;
          margin-bottom: 10px;
          font-weight: bold;
        }

        .alert {
          padding: 15px;
          margin: 20px 0;
          border: 1px solid transparent;
          border-radius: 6px;
        }

        .alert-danger {
          color: #721c24;
          background-color: #f8d7da;
          border-color: #f5c6cb;
        }

        .alert-success {
          color: #155724;
          background-color: #d4edda;
          border-color: #c3e6cb;
        }

        .alert ul {
          margin: 0;
          padding-left: 20px;
        }

        .search-container {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;
          gap: 15px;
        }

        .search-form {
          display: flex;
          align-items: center;
          gap: 10px;
          flex: 1;
          max-width: 500px;
        }

        .search-input {
          flex: 1;
          padding: 10px 15px;
          border: 2px solid #ddd;
          border-radius: 8px;
          font-size: 0.95rem;
          transition: border-color 0.3s ease;
        }

        .search-input:focus {
          outline: none;
          border-color: #007bff;
          box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
        }

        .btn-search {
          background-color: #007bff;
          color: white;
          padding: 10px 20px;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          font-size: 0.95rem;
          transition: background-color 0.3s ease;
          display: flex;
          align-items: center;
          gap: 5px;
        }

        .btn-search:hover {
          background-color: #0056b3;
        }

        .btn-clear {
          background-color: #6c757d;
          color: white;
          padding: 10px 15px;
          border: none;
          border-radius: 8px;
          cursor: pointer;
          font-size: 0.95rem;
          transition: background-color 0.3s ease;
          text-decoration: none;
          display: flex;
          align-items: center;
          gap: 5px;
        }

        .btn-clear:hover {
          background-color: #5a6268;
        }

        .search-results-info {
          margin-bottom: 15px;
          color: #666;
          font-style: italic;
        }

        .no-results {
          text-align: center;
          padding: 40px;
          color: #666;
        }

        .policy-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
        }
      </style>
    </head>

    <body>
      <jsp:include page="sidebar.jsp" />

      <div class="dashboard-container">
        <div class="main-content">
          <header class="dashboard-header">
            <h1><i class="fas fa-ticket-alt"></i> Chính sách hủy vé</h1>
          </header>

          <c:if test="${not empty sessionScope.successMessage}">
            <div class="alert alert-success">
              <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
            </div>
            <c:remove var="successMessage" scope="session" />
          </c:if>

          <section class="content-section">
            <c:if test="${not empty message}">
              <p class="message ${message.contains('successfully') ? 'success-message' : 'error-message'}">
                ${message}
              </p>
            </c:if>
            <c:if test="${not empty errorMessage}">
              <p class="message error-message">${errorMessage}</p>
            </c:if>

            <div class="policy-header">
              <h2>Danh sách chính sách</h2>
              <a href="${pageContext.request.contextPath}/createPolicy" class="btn-add">
                <i class="fas fa-plus"></i> Thêm mới
              </a>
            </div>

            <!-- Search Section -->
            <div class="search-container">
              <form method="GET" action="${pageContext.request.contextPath}/manageCancellationPolicies"
                class="search-form">
                <input type="text" name="search" value="${searchQuery}" placeholder="Tìm kiếm theo tên chính sách..."
                  class="search-input">
                <button type="submit" class="btn-search">
                  <i class="fas fa-search"></i> Tìm kiếm
                </button>
                <c:if test="${not empty param.search}">
                  <a href="${pageContext.request.contextPath}/manageCancellationPolicies" class="btn-clear">
                    <i class="fas fa-times"></i> Xóa
                  </a>
                </c:if>
              </form>
            </div>

            <!-- Search Results Info -->
            <c:if test="${not empty param.search}">
              <div class="search-results-info">
                <i class="fas fa-info-circle"></i>
                Tìm thấy <strong>${policies.size()}</strong> kết quả cho "<strong>${param.search}</strong>"
              </div>
            </c:if>

            <div class="table-container">
              <table class="styled-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Tên chính sách</th>
                    <th>Hoàn tiền?</th>
                    <th>Phí (%)</th>
                    <th>Phí cố định</th>
                    <th>Mô tả</th>
                    <th>Đang áp dụng</th>
                    <th>Hành động</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="p" items="${policies}">
                    <tr>
                      <td>${p.policyID}</td>
                      <td>${p.policyName}</td>
                      <td>${p.refundable ? 'Có' : 'Không'}</td>
                      <td>${p.feePercentage}%</td>
                      <td>${p.fixedFeeAmount}₫</td>
                      <td>${p.description}</td>
                      <td><span class="${p.active ? 'status-active' : 'status-inactive'}">
                          ${p.active ? 'Đang áp dụng' : 'Không áp dụng'}
                        </span>
                      </td>
                      <td>
                        <a href="${pageContext.request.contextPath}/updatePolicy?id=${p.policyID}"
                          class="btn-action edit">
                          <i class="fas fa-edit"></i> Sửa
                        </a>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </section>
        </div>
      </div>

      <script>
        document.addEventListener('DOMContentLoaded', function () {
          const searchInput = document.querySelector('.search-input');
          const searchForm = document.querySelector('.search-form');

          // Auto focus on search input if there's a search parameter
          if (searchInput.value) {
            searchInput.focus();
            searchInput.setSelectionRange(searchInput.value.length, searchInput.value.length);
          }

          // Enter key to submit search
          searchInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
              searchForm.submit();
            }
          });

          // Clear search with Escape key
          searchInput.addEventListener('keydown', function (e) {
            if (e.key === 'Escape') {
              this.value = '';
              window.location.href = '${pageContext.request.contextPath}/manageCancellationPolicies';
            }
          });
        });
      </script>
    </body>

    </html>