<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<hj th
<head>
    <meta charset="UTF-8">
    <title>Sửa thông tin người dùng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Sửa thông tin người dùng</h1>
            </header>
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/editUser" method="post" class="add-user-form">
                    <input type="hidden" name="userId" value="${user.userID}">
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" value="${user.fullName}" required>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" value="${user.email}" required>
                    </div>
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}" required>
                    </div>
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin" ${user.role == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Staff" ${user.role == 'Staff' ? 'selected' : ''}>Staff</option>
                            <option value="Customer" ${user.role == 'Customer' ? 'selected' : ''}>Customer</option>
                            <option value="Manager" ${user.role == 'Manager' ? 'selected' : ''}>Manager</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="isActive">Trạng thái:</label>
                        <select id="isActive" name="isActive">
                            <option value="true" ${user.isActive() ? 'selected' : ''}>Hoạt động</option>
                            <option value="false" ${!user.isActive() ? 'selected' : ''}>Bị khóa</option>
                        </select>
                    </div>
                    <input type="submit" value="Cập nhật thông tin" class="btn btn-primary">
                </form>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
