<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử sửa đổi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
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
        .filter-container input[type="text"],
        .filter-container input[type="date"],
        .filter-container select {
            padding: 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 14px;
        }
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
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Lịch sử sửa đổi</h1>
            </header>
            <section class="user-table-container">
                <div class="filter-container" style="display: flex; justify-content: space-between; align-items: center;">
                    <form action="auditLog" method="get" style="display: flex; align-items: center; gap: 15px; flex-grow: 1;">
                        <input type="text" name="searchTerm" placeholder="Tìm theo Email đối tượng..." value="${searchTerm}">
                        <select name="action">
                            <option value="all" ${empty selectedAction or selectedAction == 'all' ? 'selected' : ''}>Tất cả chức năng</option>
                            <option value="Add" ${selectedAction == 'Add' ? 'selected' : ''}>Thêm</option>
                            <option value="Edit FullName" ${selectedAction == 'Edit FullName' ? 'selected' : ''}>Sửa họ tên</option>
                            <option value="Edit Email" ${selectedAction == 'Edit Email' ? 'selected' : ''}>Sửa email</option>
                            <option value="Edit PhoneNumber" ${selectedAction == 'Edit PhoneNumber' ? 'selected' : ''}>Sửa SĐT</option>
                            <option value="Edit Role" ${selectedAction == 'Edit Role' ? 'selected' : ''}>Sửa vai trò</option>
                            <option value="Edit IsActive" ${selectedAction == 'Edit IsActive' ? 'selected' : ''}>Sửa trạng thái</option>
                        </select>
                        <input type="date" name="startDate" value="${startDate}">
                        <input type="date" name="endDate" value="${endDate}">
                        <button type="submit">Lọc</button>
                    </form>
                </div>
                <table class="user-table table-bordered table-striped">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Email người sửa</th>
                            <th>Chức năng</th>
                            <th>Email đối tượng</th>
                            <th>Giá trị cũ</th>
                            <th>Giá trị mới</th>
                            <th>Thời gian</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="log" items="${auditLogs}">
                            <tr>
                                <td>${log[0]}</td>
                                <td>${log[1]}</td>
                                <td>${log[2]}</td>
                                <td>${log[3]}</td>
                                <td>${log[4]}</td>
                                <td>${log[5]}</td>
                                <td>${log[6]}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?page=${currentPage - 1}&searchTerm=${searchTerm}&action=${selectedAction}&startDate=${startDate}&endDate=${endDate}">Trang trước</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${currentPage eq i}">
                                <span>${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="?page=${i}&searchTerm=${searchTerm}&action=${selectedAction}&startDate=${startDate}&endDate=${endDate}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}&searchTerm=${searchTerm}&action=${selectedAction}&startDate=${startDate}&endDate=${endDate}">Trang sau</a>
                    </c:if>
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
    <script>
        document.querySelector('form[action="auditLog"]').addEventListener('submit', function(e) {
            const searchTermInput = e.target.querySelector('input[name="searchTerm"]');
            if (searchTermInput) {
                searchTermInput.value = searchTermInput.value.trim();
            }
        });
    </script>
</body>
</html>
