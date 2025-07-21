<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Staff Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .charts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .chart-card {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .chart-card h3 {
            margin-top: 0;
            text-align: center;
        }
        .filter-form {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 10px;
        }
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
                    <li><a href="${pageContext.request.contextPath}/checkRefundTicket">Kiểm tra hoàn vé</a></li>
                    <li><a href="${pageContext.request.contextPath}/staff-message">Hỗ trợ khách hàng</a></li>
                    <li><a href="#">Báo cáo</a></li>
                    <li><a href="${pageContext.request.contextPath}/staff/feedback">Góp ý của khách hàng</a></li>
                    <li><a href="${pageContext.request.contextPath}/customer-info">Thông tin khách hàng</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Bảng điều khiển</h1>
            </header>
            
            <div class="charts-grid">
                <div class="chart-card">
                    <h3>Trạng Thái Yêu Cầu Hoàn Vé</h3>
                    <canvas id="refundRequestStatusChart"></canvas>
                </div>

                <div class="chart-card">
                    <h3>Xu Hướng Yêu Cầu Hoàn Vé Theo Thời Gian</h3>
                    <canvas id="refundRequestsOverTimeChart"></canvas>
                    <div class="filter-form">
                        <form action="${pageContext.request.contextPath}/staff/dashboard" method="get">
                            <select name="refundTrendDays" onchange="this.form.submit()">
                                <option value="7" ${selectedRefundTrendDays == 7 ? 'selected' : ''}>7 ngày qua</option>
                                <option value="30" ${selectedRefundTrendDays == 30 ? 'selected' : ''}>1 tháng qua</option>
                                <option value="180" ${selectedRefundTrendDays == 180 ? 'selected' : ''}>6 tháng qua</option>
                                <option value="365" ${selectedRefundTrendDays == 365 ? 'selected' : ''}>12 tháng qua</option>
                            </select>
                        </form>
                    </div>
                </div>

                <div class="chart-card">
                    <h3>Trạng Thái Góp Ý Khách Hàng</h3>
                    <canvas id="feedbackStatusChart"></canvas>
                </div>

                <div class="chart-card">
                    <h3>Góp Ý Theo Chủ Đề</h3>
                    <canvas id="feedbackByTopicChart"></canvas>
                </div>
            </div>
        </main>
    </div>

    <div id="chartData" 
         data-refund-request-status='${refundRequestStatus}'
         data-refund-requests-over-time='${refundRequestsOverTime}'
         data-feedback-status='${feedbackStatus}'
         data-feedback-by-topic='${feedbackByTopic}'
         style="display: none;"></div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const chartDataElement = document.getElementById('chartData');

            const createChart = (ctx, type, data, options) => new Chart(ctx, { type, data, options });
            
            const getPieChartData = (jsonData, label, customColors = []) => {
                const parsedData = JSON.parse(jsonData);
                const defaultColors = [
                    'rgba(255, 206, 86, 0.8)', 'rgba(54, 162, 235, 0.8)', 'rgba(255, 99, 132, 0.8)',
                    'rgba(75, 192, 192, 0.8)', 'rgba(153, 102, 255, 0.8)', 'rgba(255, 159, 64, 0.8)'
                ];
                return {
                    labels: Object.keys(parsedData),
                    datasets: [{
                        label: label,
                        data: Object.values(parsedData),
                        backgroundColor: customColors.length > 0 ? customColors : defaultColors
                    }]
                };
            };

            // 1. Refund Request Status Chart
            const refundRequestStatusCtx = document.getElementById('refundRequestStatusChart').getContext('2d');
            const refundRequestStatusData = getPieChartData(chartDataElement.dataset.refundRequestStatus, 'Trạng thái yêu cầu hoàn vé', ['rgba(255, 206, 86, 0.8)', 'rgba(75, 192, 192, 0.8)', 'rgba(255, 99, 132, 0.8)']);
            createChart(refundRequestStatusCtx, 'pie', refundRequestStatusData);

            // 2. Refund Requests Over Time Chart
            const refundRequestsOverTimeCtx = document.getElementById('refundRequestsOverTimeChart').getContext('2d');
            const refundRequestsOverTimeData = JSON.parse(chartDataElement.dataset.refundRequestsOverTime);
            createChart(refundRequestsOverTimeCtx, 'line', {
                labels: Object.keys(refundRequestsOverTimeData),
                datasets: [{
                    label: 'Số lượng yêu cầu',
                    data: Object.values(refundRequestsOverTimeData),
                    borderColor: 'rgba(255, 99, 132, 1)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    fill: true,
                    tension: 0.1
                }]
            }, { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } });

            // 3. Feedback Status Chart
            const feedbackStatusCtx = document.getElementById('feedbackStatusChart').getContext('2d');
            const feedbackStatusData = getPieChartData(chartDataElement.dataset.feedbackStatus, 'Trạng thái góp ý', ['rgba(255, 206, 86, 0.8)', 'rgba(75, 192, 192, 0.8)']);
            createChart(feedbackStatusCtx, 'pie', feedbackStatusData);

            // 4. Feedback by Topic Chart
            const feedbackByTopicCtx = document.getElementById('feedbackByTopicChart').getContext('2d');
            const feedbackByTopicData = JSON.parse(chartDataElement.dataset.feedbackByTopic);
            createChart(feedbackByTopicCtx, 'bar', {
                labels: Object.keys(feedbackByTopicData),
                datasets: [{
                    label: 'Số lượng góp ý',
                    data: Object.values(feedbackByTopicData),
                    backgroundColor: 'rgba(153, 102, 255, 0.6)'
                }]
            }, { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } });
        });
    </script>
</body>
</html>
