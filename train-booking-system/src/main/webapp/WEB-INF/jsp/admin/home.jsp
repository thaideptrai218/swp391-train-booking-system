<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bảng điều khiển quản trị</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Chào mừng, Quản trị viên!</h1>
            </header>
            <section class="cards-container">
                <div class="card">
                    <h3>Tổng số người dùng</h3>
                    <p>${totalUsers}</p>
                </div>
                <div class="card">
                    <h3>Tổng số tàu</h3>
                    <p>${totalTrains}</p>
                </div>
                <div class="card">
                    <h3>Tổng số đặt chỗ</h3>
                    <p>${totalBookings}</p>
                </div>
                <div class="card">
                    <h3>Doanh thu</h3>
                    <p>${totalRevenue}</p>
                </div>
            </section>

            <section class="charts-container">
                <h3>Xu hướng đặt chỗ (6 tháng gần nhất)</h3>
                <div style="height: 400px;">
                    <canvas id="bookingChart"></canvas>
                </div>
            </section>
            <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
            <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
            <script>
                const ctx = document.getElementById('bookingChart').getContext('2d');
                const trends = JSON.parse('${bookingTrends}');
                const sortedMonths = Object.keys(trends).sort(); 
                const labels = sortedMonths.map(month => {
                    const [year, monthNum] = month.split('-');
                    return `Tháng ${monthNum}/${year}`;
                });
                const data = sortedMonths.map(month => trends[month]);
                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Số lượng đặt chỗ',
                            data: data,
                            borderColor: '#4CAF50',
                            backgroundColor: 'rgba(76, 175, 80, 0.1)',
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'top',
                            },
                            title: {
                                display: true,
                                text: 'Biểu đồ xu hướng đặt chỗ 6 tháng gần nhất',
                                font: {
                                    size: 16
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                min: 0,
                                ticks: {
                                    stepSize: 1,
                                    callback: function(value) {
                                        return value.toLocaleString('vi-VN');
                                    }
                                }
                            }
                        },
                        interaction: {
                            intersect: false,
                            mode: 'index'
                        }
                    }
                });
            </script>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
