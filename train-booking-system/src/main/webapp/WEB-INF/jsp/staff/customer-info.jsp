<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html>

            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                <title>Thông tin khách hàng</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
                <link rel="stylesheet"
                    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
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
                                <li><a href="#">Kiểm tra vào/ra</a></li>
                                <li><a href="#">Hỗ trợ khách hàng</a></li>
                                <li><a href="#">Báo cáo</a></li>
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
                            <h1>Thông tin khách hàng</h1>
                            <div class="user-info">
                                <span>Đăng nhập với tư cách: Người dùng nhân viên</span>
                            </div>
                        </header>
                        <section class="customer-list">
                            <h3>Danh sách người dùng</h3>
                            <table class="table table-bordered">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Họ và tên</th>
                                        <th>Email</th>
                                        <th>Số điện thoại</th>
                                        <th>Ngày sinh</th>
                                        <th>Giới tính</th>
                                        <th>Địa chỉ</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty customers}">
                                        <tr>
                                            <td colspan="8" style="text-align: center;">Không có khách hàng nào</td>
                                        </tr>
                                    </c:if>
                                    <c:forEach var="customer" items="${customers}">
                                        <tr>
                                            <td>${customer.userID}</td>
                                            <td>${customer.fullName}</td>
                                            <td>${customer.email}</td>
                                            <td>${customer.phoneNumber}</td>
                                            <td>
                                                <fmt:formatDate value="${customer.dateOfBirthAsDate}"
                                                    pattern="dd/MM/yyyy" />
                                            </td>
                                            <td>${customer.gender}</td>
                                            <td>${customer.address}</td>
                                            <td>${customer.isActive ? 'Active' : 'Inactive'}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <!-- Phân trang -->
                            <div class="pagination">
                                <c:set var="totalPages" value="${totalPages}" />
                                <c:if test="${currentPage > 1}">
                                    <a
                                        href="${pageContext.request.contextPath}/customer-info?page=${currentPage - 1}">Previous</a>
                                </c:if>
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <a href="${pageContext.request.contextPath}/customer-info?page=${i}"
                                        class="${i == currentPage ? 'active' : ''}">${i}</a>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <a
                                        href="${pageContext.request.contextPath}/customer-info?page=${currentPage + 1}">Next</a>
                                </c:if>
                            </div>
                        </section>
                    </main>
                </div>
                <script src="${pageContext.request.contextPath}/js/staff-dashboard.js"></script>
                <style>
                    .pagination {
                        margin-top: 20px;
                        text-align: center;
                    }

                    .pagination a {
                        display: inline-block;
                        padding: 8px 12px;
                        margin: 0 4px;
                        text-decoration: none;
                        color: #007bff;
                        border: 1px solid #007bff;
                        border-radius: 4px;
                    }

                    .pagination a.active {
                        background-color: #007bff;
                        color: white;
                    }

                    .pagination a:hover:not(.active) {
                        background-color: #f8f9fa;
                    }
                </style>
            </body>

            </html>