<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lịch sử sửa đổi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Lịch sử sửa đổi</h1>
            </header>
            <section class="user-table-container">
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
                        <a href="?page=${currentPage - 1}">Trang trước</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${currentPage eq i}">
                                <span>${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="?page=${i}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="?page=${currentPage + 1}">Trang sau</a>
                    </c:if>
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
