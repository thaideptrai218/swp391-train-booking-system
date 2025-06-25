<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm người dùng mới</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Thêm người dùng mới</h1>
            </header>
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/addUser" method="post" class="add-user-form">
                    <div class="form-group">
                        <label for="username">Tên đăng nhập:</label>
                        <input type="text" id="username" name="username" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Mật khẩu:</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" required>
                    </div>
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" required>
                    </div>
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin">Admin</option>
                            <option value="Staff">Staff</option>
                            <option value="Customer">Customer</option>
                            <option value="Manager">Manager</option>
                        </select>
                    </div>
                    <input type="submit" value="Thêm người dùng" class="btn btn-primary">
                </form>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
