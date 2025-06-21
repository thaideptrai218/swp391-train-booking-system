<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Bảng điều khiển nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"/>
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
                    <li><a href="#">Bảng điều khiển</a></li>
                    <li><a href="#">Quản lý đặt chỗ</a></li>
                    <li><a href="#">Kiểm tra vào/ra</a></li>
                    <li><a href="${pageContext.request.contextPath}/checkRefundTicket">Kiểm tra trả vé</a></li>
                    <li><a href="#">Hỗ trợ khách hàng</a></li>
                    <li><a href="#">Báo cáo</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Chào mừng, Nhân viên!</h1>
                <div class="user-info">
                    <span>Đăng nhập với tư cách: Người dùng nhân viên</span>
                </div>
            </header>

            <section class="cards-container">
                <div class="card">
                    <h3>Đặt chỗ đang chờ xử lý</h3>
                    <p>${pendingBookings}</p>
                </div>
                <div class="card">
                    <h3>Các chuyến khởi hành hôm nay</h3>
                    <p>${todayDepartures}</p>
                </div>
                <div class="card">
                    <h3>Vé đang hoạt động</h3>
                    <p>${activeTickets}</p>
                </div>
                <div class="card">
                    <h3>Yêu cầu gần đây</h3>
                    <p>${recentRequests}</p>
                </div>
            </section>

            <section class="charts-container">
                <h3>Tổng quan hoạt động hàng ngày (Chỗ giữ)</h3>
                <div style="height: 300px; background-color: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #666;">
                    Biểu đồ dữ liệu hoạt động sẽ ở đây
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/staff-dashboard.js"></script>
</body>
</html>
