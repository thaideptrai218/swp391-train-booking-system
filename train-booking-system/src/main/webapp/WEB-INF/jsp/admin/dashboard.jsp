<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
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
        .btn-toggle {
            padding: 5px 10px;
            border: 1px solid #ccc;
            background-color: #f0f0f0;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Bảng điều khiển</h1>
            </header>
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
            
            <div class="charts-grid">
                <div class="chart-card">
                    <h3>Xu Hướng Đăng Ký Tài Khoản</h3>
                    <canvas id="userRegistrationChart"></canvas>
                    <div class="filter-form">
                        <c:if test="${viewMode == 'overview'}">
                            <a href="?viewMode=details" class="btn-toggle">Chi tiết</a>
                            <a href="?viewMode=overview&year=${selectedYear - 1}" class="btn btn-secondary">< Năm trước</a>
                            <span>Năm ${selectedYear}</span>
                            <a href="?viewMode=overview&year=${selectedYear + 1}" class="btn btn-secondary">Năm sau ></a>
                        </c:if>
                        <c:if test="${viewMode == 'details'}">
                            <a href="?viewMode=overview" class="btn-toggle">Tổng quan</a>
                            <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" id="trendForm">
                                <input type="hidden" name="viewMode" value="details">
                                <select name="trendDays" onchange="this.form.submit()">
                                    <option value="7" ${selectedTrendDays == 7 ? 'selected' : ''}>7 ngày qua</option>
                                    <option value="14" ${selectedTrendDays == 14 ? 'selected' : ''}>2 tuần qua</option>
                                    <option value="21" ${selectedTrendDays == 21 ? 'selected' : ''}>3 tuần qua</option>
                                </select>
                            </form>
                        </c:if>
                    </div>
                </div>

                <div class="chart-card">
                    <h3>Phân Bổ Giới Tính Người Dùng</h3>
                    <canvas id="genderDistributionChart"></canvas>
                </div>

                <div class="chart-card">
                    <h3>Phân Bổ Độ Tuổi Người Dùng</h3>
                    <canvas id="ageGroupDistributionChart"></canvas>
                </div>

                <div class="chart-card">
                    <h3>Trạng Thái Tài Khoản</h3>
                    <canvas id="activeStatusChart"></canvas>
                </div>

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

    <div id="chartData" 
         data-user-chart='${userChart}'
         data-gender-distribution='${genderDistribution}'
         data-age-group-distribution='${ageGroupDistribution}'
         data-active-status='${activeStatusDistribution}'
         data-login-ratio='${loginRatio}'
         style="display: none;"></div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const chartDataElement = document.getElementById('chartData');

            const createChart = (ctx, type, data, options) => new Chart(ctx, { type, data, options });
            
            const getPieChartData = (jsonData, label, customColors = []) => {
                const parsedData = JSON.parse(jsonData);
                const defaultColors = [
                    'rgba(54, 162, 235, 0.8)', 'rgba(255, 99, 132, 0.8)', 'rgba(255, 206, 86, 0.8)',
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

            // 1. User Registration Chart
            const userChartCtx = document.getElementById('userRegistrationChart').getContext('2d');
            const userChartData = JSON.parse(chartDataElement.dataset.userChart);
            createChart(userChartCtx, 'line', {
                labels: Object.keys(userChartData),
                datasets: [{
                    label: 'Số lượng đăng ký',
                    data: Object.values(userChartData),
                    borderColor: 'rgba(75, 192, 192, 1)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    fill: true,
                    tension: 0.1
                }]
            }, { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } });

            // 2. Gender Distribution Chart
            const genderCtx = document.getElementById('genderDistributionChart').getContext('2d');
            const genderData = getPieChartData(chartDataElement.dataset.genderDistribution, 'Phân bổ giới tính');
            createChart(genderCtx, 'pie', genderData);

            // 3. Age Group Distribution Chart
            const ageGroupCtx = document.getElementById('ageGroupDistributionChart').getContext('2d');
            const ageGroupData = JSON.parse(chartDataElement.dataset.ageGroupDistribution);
            createChart(ageGroupCtx, 'bar', {
                labels: Object.keys(ageGroupData),
                datasets: [{
                    label: 'Số lượng người dùng',
                    data: Object.values(ageGroupData),
                    backgroundColor: 'rgba(153, 102, 255, 0.6)'
                }]
            }, { scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } } });

            // 4. Active Status Chart
            const activeStatusCtx = document.getElementById('activeStatusChart').getContext('2d');
            const activeStatusData = getPieChartData(chartDataElement.dataset.activeStatus, 'Trạng thái hoạt động');
            createChart(activeStatusCtx, 'pie', activeStatusData);

            // 5. Login Ratio Chart
            const loginRatioCtx = document.getElementById('loginRatioChart').getContext('2d');
            const loginRatioColors = ['rgba(75, 192, 192, 0.8)', 'rgba(255, 159, 64, 0.8)']; // Green, Orange
            const loginRatioData = getPieChartData(chartDataElement.dataset.loginRatio, 'Tỷ lệ đăng nhập', loginRatioColors);
            createChart(loginRatioCtx, 'pie', loginRatioData);
        });
    </script>
</body>
</html>
