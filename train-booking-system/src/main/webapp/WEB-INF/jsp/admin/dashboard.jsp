<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--
  Admin Dashboard Page
  This page displays statistics and charts related to user accounts.
-->

<html>

<head>
    <!-- Meta tags and Title -->
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>

    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- External Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <!-- Internal Styles -->
    <style>
        /* Grid layout for charts */
        .charts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        /* Styling for chart cards */
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

        /* Styling for filter forms */
        .filter-form {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
            margin-top: 10px;
        }

        /* Common button styles */
        .btn-toggle, .btn-secondary {
            padding: 8px 15px;
            border: none;
            background-color: #007bff;
            color: white !important;
            text-decoration: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
            font-size: 14px;
        }

        .btn-toggle:hover, .btn-secondary:hover {
            background-color: #0056b3;
            text-decoration: none;
        }

        /* Specific style for toggle button */
        .btn-toggle {
            background-color: #6c757d;
        }

        .btn-toggle:hover {
            background-color: #5a6268;
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
                <h1>Bảng điều khiển</h1>
            </header>

            <!-- Cards section for summary statistics -->
            <section class="cards-container">
                <div class="card">
                    <h3>Tổng số tài khoản</h3>
                    <p>${totalAccounts}</p>
                </div>
                <div class="card">
                    <h3>Khách hàng đã đăng ký tài khoản</h3>
                    <p>${customerAccounts}</p>
                </div>
                <div class="card">
                    <h3>Khách hàng chưa đăng ký tài khoản</h3>
                    <p>${guestAccounts}</p>
                </div>
            </section>
            
            <!-- Charts section -->
            <div class="charts-grid">
                
                <!-- User Registration Chart -->
                <div class="chart-card">
                    <h3>Xu Hướng Đăng Ký Tài Khoản</h3>
                    <canvas id="userRegistrationChart"></canvas>
                    <div class="filter-form">
                        <c:if test="${viewMode == 'overview'}">
                            <a href="?viewMode=details" class="btn-toggle">Chi tiết</a>
                            <a href="?viewMode=overview&year=${selectedYear - 1}" class="btn-secondary">< Năm trước</a>
                            <span>Năm ${selectedYear}</span>
                            <a href="?viewMode=overview&year=${selectedYear + 1}" class="btn-secondary">Năm sau ></a>
                        </c:if>
                        <c:if test="${viewMode == 'details'}">
                            <a href="?viewMode=overview" class="btn-toggle">Tổng quan</a>
                            <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" id="trendForm">
                                <input type="hidden" name="viewMode" value="details">
                                <select name="trendDays" onchange="this.form.submit()">
                                    <option value="7" ${selectedTrendDays == 7 ? 'selected' : ''}>7 ngày qua</option>
                                    <option value="30" ${selectedTrendDays == 30 ? 'selected' : ''}>30 ngày qua</option>
                                    <option value="180" ${selectedTrendDays == 180 ? 'selected' : ''}>Nửa năm qua</option>
                                </select>
                            </form>
                        </c:if>
                    </div>
                </div>

                <!-- Active Status Chart -->
                <div class="chart-card">
                    <h3>Trạng Thái Tài Khoản</h3>
                    <canvas id="activeStatusChart"></canvas>
                </div>

                <!-- Login Ratio Chart -->
                <div class="chart-card">
                    <h3>Tỷ Lệ Tài Khoản Có Đăng Nhập</h3>
                    <canvas id="loginRatioChart"></canvas>
                    <div class="filter-form">
                        <form action="${pageContext.request.contextPath}/admin/dashboard" method="get">
                            <select name="loginRatioDays" onchange="this.form.submit()">
                                <option value="7" ${selectedLoginRatioDays == 7 ? 'selected' : ''}>7 ngày qua</option>
                                <option value="30" ${selectedLoginRatioDays == 30 ? 'selected' : ''}>30 ngày qua</option>
                                <option value="60" ${selectedLoginRatioDays == 60 ? 'selected' : ''}>2 tháng qua</option>
                            </select>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- Hidden div to store chart data from backend -->
    <div id="chartData" 
         data-user-chart='${userChart}'
         data-active-status='${activeStatusDistribution}'
         data-login-ratio='${loginRatio}'
         style="display: none;">
    </div>

    <!-- JavaScript for creating charts -->
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            // Get the element containing chart data
            const chartDataElement = document.getElementById('chartData');

            // Helper function to create a new chart
            const createChart = (ctx, type, data, options) => {
                return new Chart(ctx, {
                    type: type,
                    data: data,
                    options: options
                });
            };
            
            // Helper function to prepare data for Pie charts
            const getPieChartData = (jsonData, label, customColors = []) => {
                const parsedData = JSON.parse(jsonData);
                const defaultColors = [
                    'rgba(54, 162, 235, 0.8)', 
                    'rgba(255, 99, 132, 0.8)', 
                    'rgba(255, 206, 86, 0.8)',
                    'rgba(75, 192, 192, 0.8)', 
                    'rgba(153, 102, 255, 0.8)', 
                    'rgba(255, 159, 64, 0.8)'
                ];
                
                const labels = Object.keys(parsedData);
                const data = Object.values(parsedData);
                const backgroundColor = customColors.length > 0 ? customColors : defaultColors;

                return {
                    labels: labels,
                    datasets: [{
                        label: label,
                        data: data,
                        backgroundColor: backgroundColor
                    }]
                };
            };

            // --- Chart Initialization ---

            // 1. User Registration Chart (Line Chart)
            const userChartCtx = document.getElementById('userRegistrationChart').getContext('2d');
            const userChartJsonData = chartDataElement.dataset.userChart;
            const userChartData = JSON.parse(userChartJsonData);
            const userChartLabels = Object.keys(userChartData);
            const userChartValues = Object.values(userChartData);

            createChart(userChartCtx, 'line', {
                labels: userChartLabels,
                datasets: [{
                    label: 'Số lượng đăng ký',
                    data: userChartValues,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    fill: true,
                    tension: 0.1
                }]
            }, { 
                scales: { 
                    y: { 
                        beginAtZero: true, 
                        ticks: { 
                            stepSize: 1 
                        } 
                    } 
                } 
            });

            // 2. Active Status Chart (Pie Chart)
            const activeStatusCtx = document.getElementById('activeStatusChart').getContext('2d');
            const activeStatusJsonData = chartDataElement.dataset.activeStatus;
            const activeStatusData = getPieChartData(activeStatusJsonData, 'Trạng thái hoạt động');
            createChart(activeStatusCtx, 'pie', activeStatusData);

            // 3. Login Ratio Chart (Pie Chart)
            const loginRatioCtx = document.getElementById('loginRatioChart').getContext('2d');
            const loginRatioJsonData = chartDataElement.dataset.loginRatio;
            const loginRatioColors = ['rgba(75, 192, 192, 0.8)', 'rgba(255, 159, 64, 0.8)']; // Green, Orange
            const loginRatioData = getPieChartData(loginRatioJsonData, 'Tỷ lệ đăng nhập', loginRatioColors);
            createChart(loginRatioCtx, 'pie', loginRatioData);
        });
    </script>
</body>

</html>
