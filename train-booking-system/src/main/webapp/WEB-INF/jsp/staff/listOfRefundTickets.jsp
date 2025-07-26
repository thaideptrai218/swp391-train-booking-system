<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <fmt:setLocale value="vi_VN" />
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Kiểm tra hoàn vé</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-message.css" />
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                <style>
                    .chat-container {
                        background-color: #fff;
                        padding: 20px;
                        border-radius: 12px;
                        box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }

                    .table-wrapper {
                        width: 100%;
                        overflow-x: auto;
                        overflow-y: visible;
                        border-radius: 8px;
                        margin-top: 15px;
                    }

                    .table-wrapper::-webkit-scrollbar {
                        height: 8px;
                    }

                    .table-wrapper::-webkit-scrollbar-track {
                        background: #f1f1f1;
                        border-radius: 4px;
                    }

                    .table-wrapper::-webkit-scrollbar-thumb {
                        background: #667eea;
                        border-radius: 4px;
                    }

                    .table-wrapper::-webkit-scrollbar-thumb:hover {
                        background: #5a6fd8;
                    }

                    table {
                        width: 100%;
                        min-width: 1600px;
                        border-collapse: collapse;
                        font-size: 13px;
                        background: white;
                    }

                    th,
                    td {
                        padding: 10px 8px;
                        border: 1px solid #ddd;
                        vertical-align: top;
                        text-align: left;
                    }

                    th:nth-child(1),
                    td:nth-child(1) {
                        width: 40px;
                        min-width: 40px;
                        text-align: center;
                    }

                    th:nth-child(2),
                    td:nth-child(2) {
                        width: 100px;
                        min-width: 100px;
                    }

                    th:nth-child(3),
                    td:nth-child(3) {
                        width: 140px;
                        min-width: 140px;
                    }

                    th:nth-child(4),
                    td:nth-child(4) {
                        width: 130px;
                        min-width: 130px;
                    }

                    th:nth-child(5),
                    td:nth-child(5) {
                        width: 100px;
                        min-width: 100px;
                        text-align: center;
                    }

                    th:nth-child(6),
                    td:nth-child(6) {
                        width: 160px;
                        min-width: 160px;
                    }

                    th:nth-child(7),
                    td:nth-child(7) {
                        width: 120px;
                        min-width: 120px;
                        text-align: center;
                    }

                    th:nth-child(8),
                    td:nth-child(8) {
                        width: 120px;
                        min-width: 120px;
                        text-align: center;
                    }

                    th:nth-child(9),
                    td:nth-child(9) {
                        width: 150px;
                        min-width: 150px;
                    }

                    th:nth-child(10),
                    td:nth-child(10) {
                        width: 150px;
                        min-width: 150px;
                    }

                    th:nth-child(11),
                    td:nth-child(11) {
                        width: 180px;
                        min-width: 180px;
                    }

                    th:nth-child(12),
                    td:nth-child(12) {
                        width: 140px;
                        min-width: 140px;
                    }

                    th:nth-child(13),
                    td:nth-child(13) {
                        width: 80px;
                        min-width: 80px;
                        text-align: center;
                    }

                    th {
                        background-color: #f0f4fa;
                        text-align: center;
                        font-weight: bold;
                        font-size: 12px;
                        position: sticky;
                        top: 0;
                        z-index: 10;
                    }

                    tr:nth-child(even) {
                        background-color: #f9fcff;
                    }

                    td[colspan="9"] {
                        background: #eef5ff !important;
                        font-weight: bold;
                        text-align: center;
                        padding: 12px;
                        color: #1565c0;
                    }

                    td:first-child {
                        font-weight: bold;
                    }

                    input[type="text"] {
                        border: 1px solid #ccc;
                        padding: 6px 8px;
                        border-radius: 4px;
                        width: 100%;
                        font-size: 12px;
                        box-sizing: border-box;
                    }

                    input[type="file"] {
                        width: 100%;
                        padding: 4px;
                        border: 1px dashed #ccc;
                        border-radius: 4px;
                        font-size: 11px;
                        background-color: #f8f9fa;
                    }

                    button {
                        padding: 6px 12px;
                        border: none;
                        border-radius: 6px;
                        cursor: pointer;
                        font-weight: bold;
                        color: white;
                        font-size: 12px;
                        min-width: 60px;
                    }

                    button[name="action"][value="approve"] {
                        background-color: #28a745;
                    }

                    button[name="action"][value="reject"] {
                        background-color: #dc3545;
                    }

                    button[name="saveBtn"] {
                        background-color: #007bff;
                    }

                    button:hover {
                        opacity: 0.9;
                        transform: translateY(-1px);
                        transition: all 0.2s ease;
                    }

                    .message {
                        padding: 8px;
                        background-color: #ffe5e5;
                        border: 1px solid #ff8888;
                        border-radius: 6px;
                        margin-bottom: 12px;
                    }

                    .section-header {
                        font-size: 18px;
                        font-weight: bold;
                        color: #d50000;
                        margin-bottom: 10px;
                    }

                    /* Pagination Styles */
                    .pagination-container {
                        margin-top: 20px;
                        padding: 15px 0;
                        border-top: 1px solid #e0e0e0;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        flex-wrap: wrap;
                        gap: 10px;
                    }

                    .pagination-info {
                        color: #666;
                        font-size: 14px;
                    }

                    .pagination {
                        display: flex;
                        align-items: center;
                        gap: 5px;
                    }

                    .pagination a,
                    .pagination span {
                        padding: 8px 12px;
                        text-decoration: none;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                        color: #333;
                        font-size: 14px;
                        min-width: 40px;
                        text-align: center;
                        display: inline-block;
                    }

                    .pagination a:hover {
                        background-color: #f5f5f5;
                        color: #667eea;
                    }

                    .pagination .current {
                        background-color: #667eea;
                        color: white;
                        border-color: #667eea;
                    }

                    .pagination .disabled {
                        background-color: #f8f9fa;
                        color: #6c757d;
                        cursor: not-allowed;
                        border-color: #dee2e6;
                    }

                    .page-size-selector {
                        display: flex;
                        align-items: center;
                        gap: 10px;
                        font-size: 14px;
                    }

                    .page-size-selector select {
                        padding: 5px 8px;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                        font-size: 14px;
                    }

                    @media (max-width: 768px) {
                        table {
                            min-width: 1400px;
                            font-size: 11px;
                        }

                        th,
                        td {
                            padding: 8px 6px;
                        }

                        input[type="text"],
                        input[type="file"],
                        button {
                            font-size: 10px;
                            padding: 4px 6px;
                        }
                    }
                </style>
            </head>

            <body>
                <div class="dashboard-container">
                    <aside class="sidebar">
                        <a href="${pageContext.request.contextPath}/searchTrip" class="home-link">
                            <i class="fa-solid fa-house fa-xl home-icon"></i>
                        </a>
                        <h2>Bảng điều khiển nhân viên</h2>
                        <nav>
                            <ul>
                                <li><a href="${pageContext.request.contextPath}/staff/dashboard">Bảng điều khiển</a>
                                </li>
                                <li><a href="#">Quản lý đặt chỗ</a></li>
                                <li><a href="${pageContext.request.contextPath}/checkRefundTicket">Kiểm tra hoàn vé</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/staff-message">Hỗ trợ khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/checkConfirmRefundRequest">Danh sách các
                                        vé đã hoàn</a></li>
                                <li><a href="${pageContext.request.contextPath}/staff/feedback">Góp ý của khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/customer-info">Thông tin khách hàng</a>
                                </li>
                                <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                            </ul>
                        </nav>
                    </aside>

                    <main class="main-content">
                        <header class="header">
                            <h1>Kiểm tra hoàn vé</h1>
                            <div class="user-info">
                                <span>Đăng nhập với tư cách: Người dùng nhân viên</span>
                            </div>
                        </header>

                        <div class="chat-container">
                            <div class="section-header">Danh sách yêu cầu trả vé</div>

                            <c:if test="${not empty message}">
                                <div class="message">${message}</div>
                            </c:if>

                            <!-- ✅ ADD TABLE WRAPPER -->
                            <div class="table-wrapper">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>#</th>
                                            <th>Mã vé</th>
                                            <th>Hành khách</th>
                                            <th>Thông tin</th>
                                            <th>Chính sách</th>
                                            <th>Chi phí</th>
                                            <th>Thời gian yêu cầu</th>
                                            <th>Thời gian xử lý</th>
                                            <th>Người đặt vé</th>
                                            <th>Người xử lý</th>
                                            <th>Ghi chú</th>
                                            <th>Ảnh chuyển khoản</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="req" items="${confirmRefundRequests}" varStatus="i">
                                            <tr>
                                                <td colspan="13"
                                                    style="background: #eef5ff !important; font-weight: bold; text-align: center; padding: 12px; color: #1565c0;">
                                                    ${req.startStation} - ${req.endStation} | ${req.scheduledDeparture}
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>${i.index + 1}</td>
                                                <td><strong>${req.ticketCode}</strong></td>
                                                <td>
                                                    <b>${req.passengerFullName}</b><br />
                                                    <small>${req.passengerType}</small><br />
                                                    <small>Số giấy tờ: ${req.passengerIDCard}</small>
                                                </td>
                                                <td>
                                                    <strong>${req.trainName}</strong><br />
                                                    <small>Toa: ${req.coachName}</small><br />
                                                    <small>Ghế số: ${req.seatNumber}</small><br />
                                                    <small>${req.seatType}</small>
                                                </td>
                                                <td>
                                                    <small>${req.refundPolicy}</small>
                                                </td>
                                                <td>
                                                    <strong>Tiền vé:</strong>
                                                    <fmt:formatNumber value="${req.originalPrice}"
                                                        groupingUsed="true" />đ<br />
                                                    <strong>Phí hoàn:</strong>
                                                    <fmt:formatNumber value="${req.refundFee}" groupingUsed="true" />
                                                    đ<br />
                                                    <strong style="color: #28a745;">Hoàn lại:</strong>
                                                    <fmt:formatNumber value="${req.refundAmount}" groupingUsed="true" />
                                                    đ
                                                </td>
                                                <td>
                                                    <small>${req.requestedAt}</small>
                                                </td>
                                                <td>
                                                    <small>${req.processedAt}</small>
                                                </td>
                                                <td>
                                                    <strong>${req.userFullName}</strong><br />
                                                    <small>Email: ${req.userEmail}</small><br />
                                                    <small>SĐT: ${req.userPhoneNumber}</small><br />
                                                    <small>CMND: ${req.userIDCard}</small>
                                                </td>
                                                <td>
                                                    <strong>${req.staffFullName}</strong><br />
                                                    <small>Email: ${req.staffEmail}</small><br />
                                                    <small>SĐT: ${req.staffPhoneNumber}</small><br />
                                                    <small>CMND: ${req.staffIDCard}</small>
                                                </td>
                                                <td>
                                                    ${req.notes}
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty req.images}">
                                                            <img src="${pageContext.request.contextPath}/image/${req.images}"
                                                                width="200" alt="Ảnh chuyển khoản"
                                                                style="border: 1px solid #ddd; border-radius: 4px;" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="color: #666; font-style: italic;">Chưa có ảnh</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <!-- ✅ END TABLE WRAPPER -->

                            <!-- Pagination Section -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination-container">
                                    <div class="pagination-info">
                                        Hiển thị ${startRecord} - ${endRecord} trong tổng số ${totalRecords} kết quả
                                    </div>
                                    
                                    <div class="pagination">
                                        <!-- First Page -->
                                        <c:choose>
                                            <c:when test="${currentPage == 1}">
                                                <span class="disabled">❮❮</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?page=1&pageSize=${pageSize}">❮❮</a>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Previous Page -->
                                        <c:choose>
                                            <c:when test="${currentPage == 1}">
                                                <span class="disabled">❮</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?page=${currentPage - 1}&pageSize=${pageSize}">❮</a>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Page Numbers -->
                                        <c:forEach var="i" begin="${currentPage - 2 < 1 ? 1 : currentPage - 2}" 
                                                 end="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}">
                                            <c:choose>
                                                <c:when test="${i == currentPage}">
                                                    <span class="current">${i}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="?page=${i}&pageSize=${pageSize}">${i}</a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>

                                        <!-- Next Page -->
                                        <c:choose>
                                            <c:when test="${currentPage == totalPages}">
                                                <span class="disabled">❯</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?page=${currentPage + 1}&pageSize=${pageSize}">❯</a>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Last Page -->
                                        <c:choose>
                                            <c:when test="${currentPage == totalPages}">
                                                <span class="disabled">❯❯</span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="?page=${totalPages}&pageSize=${pageSize}">❯❯</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="page-size-selector">
                                        <label for="pageSize">Hiển thị:</label>
                                        <select id="pageSize" onchange="changePageSize(this.value)">
                                            <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                                            <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                            <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                            <option value="50" ${pageSize == 50 ? 'selected' : ''}>50</option>
                                        </select>
                                        <span>kết quả/trang</span>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </main>
                </div>

                <script>
                    function changePageSize(newPageSize) {
                        const currentUrl = new URL(window.location.href);
                        currentUrl.searchParams.set('pageSize', newPageSize);
                        currentUrl.searchParams.set('page', '1'); // Reset về trang đầu
                        window.location.href = currentUrl.toString();
                    }
                </script>
            </body>

            </html>