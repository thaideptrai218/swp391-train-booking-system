<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
                    <h3>Tổng số người dùng</h3>
                    <p>${totalUsers}</p>
                </div>
                <div class="card">
                    <h3>TB người dùng/tháng</h3>
                    <p>${avgUsersPerMonth}</p>
                </div>
                <div class="card">
                    <h3>TB người dùng/năm</h3>
                    <p>${avgUsersPerYear}</p>
                </div>
            </section>
            <section class="charts-container">
                <h3>Lượng người dùng đăng ký theo tháng</h3>
                <canvas id="userRegistrationChart"></canvas>
                <div class="year-selector">
                    <a href="?year=${selectedYear - 1}" class="btn btn-secondary">< Năm trước</a>
                    <span>Năm ${selectedYear}</span>
                    <a href="?year=${selectedYear + 1}" class="btn btn-secondary">Năm sau ></a>
                </div>
                <div id="chartData" data-user-chart='${userChart}' style="display: none;"></div>
            </section>
        </main>
    </div>
    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const chartDataElement = document.getElementById('chartData');
            const trendData = JSON.parse(chartDataElement.dataset.userChart);
            const labels = Object.keys(trendData);
            const data = Object.values(trendData);

            const ctx = document.getElementById('userRegistrationChart').getContext('2d');
            const userRegistrationChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Số lượng người dùng đăng ký',
                        data: data,
                        backgroundColor: 'rgba(54, 162, 235, 0.6)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1
                            }
                        }
                    }
                }
            });
        });
    </script>
</body>
</html>
