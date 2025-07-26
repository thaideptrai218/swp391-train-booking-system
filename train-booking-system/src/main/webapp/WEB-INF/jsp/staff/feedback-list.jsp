<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Góp ý của khách hàng</title>
    
    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <!-- Internal Styles -->
    <style>
        .feedback-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .feedback-table th,
        .feedback-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        .feedback-table th {
            background-color: #f2f2f2;
        }

        .feedback-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .feedback-table tr:hover {
            background-color: #ddd;
        }

        .response-form {
            margin-top: 10px;
            padding: 10px;
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .response-form textarea {
            width: 100%;
            height: 80px;
            margin-bottom: 10px;
            padding: 5px;
            box-sizing: border-box;
        }

        .response-form button {
            background-color: #0082c4;
            color: white;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
        }

        .response-form button:hover {
            background-color: #006bb3;
        }

        .pagination {
            margin-top: 20px;
            text-align: center;
        }

        .pagination a {
            padding: 8px 12px;
            margin: 0 5px;
            text-decoration: none;
            color: #0082c4;
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .pagination a:hover {
            background-color: #ddd;
        }

        .pagination .active {
            background-color: #0082c4;
            color: white;
        }
    </style>
</head>

<body>
    <div class="dashboard-container">
        
        <!-- Sidebar -->
        <jsp:include page="sidebar.jsp" />

        <!-- Main Content -->
        <main class="main-content">
            <header class="header">
                <h1>Góp ý của khách hàng</h1>
            </header>

            <section class="feedback-list-container">
                <table class="feedback-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên khách hàng</th>
                            <th>Email</th>
                            <th>Tiêu đề</th>
                            <th>Nội dung</th>
                            <th>Ngày gửi</th>
                            <th>Trạng thái</th>
                            <th>Phản hồi</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="feedback" items="${feedbacks}">
                            <tr>
                                <td>${feedback.feedbackId}</td>
                                <td>${feedback.fullName}</td>
                                <td>${feedback.email}</td>
                                <td>${feedback.ticketName}</td>
                                <td>${feedback.feedbackContent}</td>
                                <td>
                                    <fmt:formatDate value="${feedback.submittedAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                                </td>
                                <td>${feedback.status}</td>
                                <td>${feedback.response != null ? feedback.response : 'Chưa phản hồi'}</td>
                                <td>
                                    <div id="response-form-${feedback.feedbackId}" class="response-form">
                                        <form action="${pageContext.request.contextPath}/staff/feedback" method="post">
                                            <input type="hidden" name="feedbackId" value="${feedback.feedbackId}">
                                            <textarea name="response" placeholder="Nhập phản hồi của bạn...">${feedback.response != null ? feedback.response : ''}</textarea>
                                            <button type="submit">Gửi phản hồi</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty feedbacks}">
                            <tr>
                                <td colspan="9">Không có góp ý nào.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <!-- Pagination -->
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/staff/feedback?page=${currentPage - 1}">Previous</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="page">
                        <c:choose>
                            <c:when test="${page == currentPage}">
                                <a href="${pageContext.request.contextPath}/staff/feedback?page=${page}" class="active">${page}</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/staff/feedback?page=${page}">${page}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/staff/feedback?page=${currentPage + 1}">Next</a>
                    </c:if>
                </div>
            </section>
        </main>
    </div>
</body>

</html>
