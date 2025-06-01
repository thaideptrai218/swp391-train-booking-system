<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Bảng điều khiển quản trị</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
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
                    <p>1,234</p>
                </div>
                <div class="card">
                    <h3>Tổng số tàu</h3>
                    <p>56</p>
                </div>
                <div class="card">
                    <h3>Tổng số đặt chỗ</h3>
                    <p>7,890</p>
                </div>
                <div class="card">
                    <h3>Doanh thu</h3>
                    <p>$123,456</p>
                </div>
            </section>

            <section class="charts-container">
                <h3>Xu hướng đặt chỗ (Chỗ giữ)</h3>
                <div style="height: 300px; background-color: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #666;">
                    Biểu đồ sẽ ở đây (ví dụ: sử dụng Chart.js)
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
</body>
</html>
