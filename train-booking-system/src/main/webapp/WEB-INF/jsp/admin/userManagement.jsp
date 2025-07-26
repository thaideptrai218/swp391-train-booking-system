<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--
  User Management Page
  This page allows administrators to view, filter, and manage user accounts.
-->

<!DOCTYPE html>
<html>

<head>
    <!-- Meta tags and Title -->
    <meta charset="UTF-8">
    <title>Quản lý người dùng</title>

    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Internal Styles -->
    <style>
        /* Styling for the filter container */
        .filter-container {
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
        }

        .filter-container form {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        /* Styling for form inputs */
        .filter-container input[type="text"],
        .filter-container select {
            padding: 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 14px;
        }

        /* Styling for the filter button */
        .filter-container button {
            padding: 8px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }

        .filter-container button:hover {
            background-color: #0056b3;
        }
    </style>
</head>

<body>
    <!-- Main container for the dashboard -->
    <div class="dashboard-container">
        
        <!-- Include admin layout (sidebar, etc.) -->
        <jsp:include page="adminLayout.jsp" />

        <!-- Main content area -->
        <main class="main-content">
            
            <!-- Header section -->
            <header class="header">
                <h1>Quản lý người dùng</h1>
            </header>

            <!-- User table and filters section -->
            <section class="user-table-container">
                
                <!-- Filter controls and Add User button -->
                <div class="filter-container" style="display: flex; justify-content: space-between; align-items: center;">
                    <form action="userManagement" method="get" style="display: flex; align-items: center; gap: 15px; flex-grow: 1;">
                        <input type="text" name="searchTerm" placeholder="Tìm kiếm theo tên, email, SĐT..." value="${searchTerm}">
                        <select name="role">
                            <option value="all" ${empty selectedRole or selectedRole == 'all' ? 'selected' : ''}>Tất cả vai trò</option>
                            <option value="Admin" ${selectedRole == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Staff" ${selectedRole == 'Staff' ? 'selected' : ''}>Staff</option>
                            <option value="Customer" ${selectedRole == 'Customer' ? 'selected' : ''}>Customer</option>
                            <option value="Manager" ${selectedRole == 'Manager' ? 'selected' : ''}>Manager</option>
                            <option value="Guest" ${selectedRole == 'Guest' ? 'selected' : ''}>Guest</option>
                        </select>
                        <select name="status">
                            <option value="all" ${empty selectedStatus or selectedStatus == 'all' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="active" ${selectedStatus == 'active' ? 'selected' : ''}>Hoạt động</option>
                            <option value="inactive" ${selectedStatus == 'inactive' ? 'selected' : ''}>Bị khóa</option>
                        </select>
                        <button type="submit">Lọc</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/admin/addUser" class="btn btn-primary" style="white-space: nowrap;">Thêm người dùng</a>
                </div>
                
                <!-- User Data Table -->
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
                                     <a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.userID}" class="btn btn-primary">Sửa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <!-- Pagination controls -->
                <div class="pagination">
                    <c:if test="${currentPage != 1}">
                        <a href="userManagement?page=${currentPage - 1}&searchTerm=${searchTerm}&role=${selectedRole}&status=${selectedStatus}">Trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${noOfPages}" var="i">
                        <c:choose>
                            <c:when test="${currentPage eq i}">
                                <span>${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="userManagement?page=${i}&searchTerm=${searchTerm}&role=${selectedRole}&status=${selectedStatus}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage lt noOfPages}">
                        <a href="userManagement?page=${currentPage + 1}&searchTerm=${searchTerm}&role=${selectedRole}&status=${selectedStatus}">Sau</a>
                    </c:if>
                </div>
            </section>
        </main>
    </div>

    <!-- External JavaScript -->
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
    
    <!-- Inline script to trim search term -->
    <script>
        document.querySelector('form[action="userManagement"]').addEventListener('submit', function(e) {
            const searchTermInput = e.target.querySelector('input[name="searchTerm"]');
            if (searchTermInput) {
                searchTermInput.value = searchTermInput.value.trim();
            }
        });
    </script>
</body>

</html>
