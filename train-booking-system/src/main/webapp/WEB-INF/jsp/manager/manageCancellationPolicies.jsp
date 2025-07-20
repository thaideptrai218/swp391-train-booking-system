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
      </style>
    </head>

    <body>
      <jsp:include page="sidebar.jsp" />

      <div class="dashboard-container">
        <div class="main-content">
          <header class="dashboard-header">
            <h1><i class="fas fa-ticket-alt"></i> Chính sách hủy vé</h1>
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

            <div class="policy-header">
              <h2>Danh sách chính sách</h2>
              <a href="${pageContext.request.contextPath}/createPolicy" class="btn-add">
                <i class="fas fa-plus"></i> Thêm mới
              </a>
            </div>

            <div class="table-container">
              <table class="styled-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Tên chính sách</th>
                    <th>Hoàn tiền?</th>
                    <th>Phí (%)</th>
                    <th>Phí cố định</th>
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
    </body>

    </html>