<!DOCTYPE html>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>

<head>
    <meta charset="UTF-8">
    <title>Staff Dashboard</title>
    
    <!-- External Stylesheets -->
    <link 
        rel="stylesheet" 
        href="${pageContext.request.contextPath}/css/staff-dashboard.css"
    >
    <link 
        rel="stylesheet" 
        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" 
        integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" 
        crossorigin="anonymous" 
        referrerpolicy="no-referrer" 
    />

    <!-- Internal Styles -->
    <style>
        .task-list {
            margin-top: 20px;
        }

        .task-item {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 15px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .task-item p {
            margin: 0;
            font-size: 1.1em;
        }

        .task-item .btn {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }

        .task-item .btn:hover {
            background-color: #0056b3;
        }
    </style>
</head>

<body>
    <!-- Main container for the dashboard -->
    <div class="dashboard-container">
        
        <!-- Sidebar navigation -->
        <jsp:include page="sidebar.jsp" />

        <!-- Main content area -->
        <main class="main-content">
            
            <!-- Header section -->
            <header class="header">
                <h1>Công việc cần xử lý</h1>
            </header>
            
            <!-- List of tasks -->
            <div class="task-list">
                
                <!-- Pending Refunds Task -->
                <c:if test="${pendingRefundsCount > 0}">
                    <div class="task-item">
                        <p>Có ${pendingRefundsCount} yêu cầu hoàn vé đang chờ xử lý.</p>
                        <a href="${pageContext.request.contextPath}/staff/refund-requests" class="btn">Xử lý ngay</a>
                    </div>
                </c:if>

                <!-- Pending Feedbacks Task -->
                <c:if test="${pendingFeedbacksCount > 0}">
                    <div class="task-item">
                        <p>Có ${pendingFeedbacksCount} góp ý của khách hàng đang chờ phản hồi.</p>
                        <a href="${pageContext.request.contextPath}/staff/feedback" class="btn">Xem góp ý</a>
                    </div>
                </c:if>

                <!-- Unanswered Messages Task -->
                <c:if test="${unansweredUsersCount > 0}">
                    <div class="task-item">
                        <p>Có ${unansweredUsersCount} khách hàng đang chờ được trả lời tin nhắn.</p>
                        <a href="${pageContext.request.contextPath}/staff/messages" class="btn">Trả lời ngay</a>
                    </div>
                </c:if>

                <!-- No Tasks Message -->
                <c:if test="${pendingRefundsCount == 0 && pendingFeedbacksCount == 0 && unansweredUsersCount == 0}">
                    <div class="task-item">
                        <p>Không có công việc nào cần xử lý.</p>
                    </div>
                </c:if>
            </div>
        </main>
    </div>
</body>

</html>
