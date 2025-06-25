<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Góp ý của khách hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <style>
        .feedback-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .feedback-table th, .feedback-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        .feedback-table th {
            background-color: #f2f2f2;
        }
        .feedback-table tr:nth-child(even){background-color: #f9f9f9;}
        .feedback-table tr:hover {background-color: #ddd;}
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
                    <li><a href="${pageContext.request.contextPath}/staff/dashboard">Bảng điều khiển</a></li>
                    <li><a href="#">Quản lý đặt chỗ</a></li>
                    <li><a href="#">Kiểm tra vào/ra</a></li>
                    <li><a href="#">Hỗ trợ khách hàng</a></li>
                    <li><a href="#">Báo cáo</a></li>
                    <li><a href="${pageContext.request.contextPath}/staff/feedback">Góp ý của khách hàng</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Góp ý của khách hàng</h1>
                <div class="user-info">
                    <span>Đăng nhập với tư cách: Người dùng nhân viên</span>
                </div>
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
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="feedback" items="${feedbacks}">
                            <tr>
                                <td>${feedback.feedbackId}</td>
                                <td>${feedback.customerName}</td>
                                <td>${feedback.customerEmail}</td>
                                <td>${feedback.subject}</td>
                                <td>${feedback.message}</td>
                                <td>${feedback.submissionDate}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty feedbacks}">
                            <tr>
                                <td colspan="6">Không có góp ý nào.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/staff-dashboard.js"></script>
</body>
</html>
