<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Bảng điều khiển quản trị</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <a href="${pageContext.request.contextPath}/searchTrip" class="home-link">
                <i class="fa-solid fa-house fa-xl home-icon"></i>
            </a>
            <h2>Bảng điều khiển quản trị</h2>
            <nav>
                <ul>
                    <li><a href="#">Bảng điều khiển</a></li>
                    <li><a href="#">Quản lý người dùng</a></li>
                    <li><a href="#">Quản lý tàu</a></li>
                    <li><a href="#">Quản lý tuyến đường</a></li>
                    <li><a href="#">Xem đặt chỗ</a></li>
                    <li><a href="#">Báo cáo</a></li>
                    <li><a href="#">Cài đặt</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Chào mừng, Quản trị viên!</h1>
                <div class="user-info">
                    <span>Đăng nhập với tư cách: Người dùng quản trị</span>
                </div>
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
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
    <script>
        const ctx = document.getElementById('bookingChart').getContext('2d');
        // Parse JSON string thành object
        const trends = JSON.parse('${bookingTrends}');
        
        // Lấy 6 tháng gần nhất
        const sortedMonths = Object.keys(trends).sort().slice(-6);
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
</body>
</html>
