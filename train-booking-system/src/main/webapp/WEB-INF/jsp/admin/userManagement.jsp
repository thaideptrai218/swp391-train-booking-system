<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý người dùng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Quản lý người dùng</h1>
            </header>
            <section class="user-table-container">
                <table class="user-table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ và tên</th>
                            <th>Email</th>
                            <th>Số điện thoại</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>${user.userID}</td>
                                <td>${user.fullName}</td>
                                <td>${user.email}</td>
                                <td>${user.phoneNumber}</td>
                                <td>${user.role}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${user.isActive()}">
                                            Hoạt động
                                        </c:when>
                                        <c:otherwise>
                                            Bị khóa
                                        </c:otherwise>
                                    </c:choose>
                                 </td>
                                 <td>
                                     <a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.userID}">Sửa</a>
                                     <c:if test="${user.role != 'Customer'}">
                                        <a href="${pageContext.request.contextPath}/admin/userManagement?action=delete&userId=${user.userID}" class="delete-user">Xóa</a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${user.isActive()}">
                                            <a href="${pageContext.request.contextPath}/admin/userManagement?action=lock&userId=${user.userID}">Khóa</a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/admin/userManagement?action=unlock&userId=${user.userID}">Mở khóa</a>
                                        </c:otherwise>
                                    </c:choose>
                                    <a href="${pageContext.request.contextPath}/admin/userManagement?action=hide&userId=${user.userID}">Ẩn</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <a href="${pageContext.request.contextPath}/admin/addUser" class="btn btn-primary">Thêm người dùng</a>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
