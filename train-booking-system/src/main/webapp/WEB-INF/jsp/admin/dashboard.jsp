<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <h2>Admin Panel</h2>
            <nav>
                <ul>
                    <li><a href="#">Dashboard</a></li>
                    <li><a href="#">Manage Users</a></li>
                    <li><a href="#">Manage Trains</a></li>
                    <li><a href="#">Manage Routes</a></li>
                    <li><a href="#">View Bookings</a></li>
                    <li><a href="#">Reports</a></li>
                    <li><a href="#">Settings</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Welcome, Admin!</h1>
                <div class="user-info">
                    <span>Logged in as: Admin User</span>
                </div>
            </header>

            <section class="cards-container">
                <div class="card">
                    <h3>Total Users</h3>
                    <p>1,234</p>
                </div>
                <div class="card">
                    <h3>Total Trains</h3>
                    <p>56</p>
                </div>
                <div class="card">
                    <h3>Total Bookings</h3>
                    <p>7,890</p>
                </div>
                <div class="card">
                    <h3>Revenue</h3>
                    <p>$123,456</p>
                </div>
            </section>

            <section class="charts-container">
                <h3>Booking Trends (Placeholder)</h3>
                <div style="height: 300px; background-color: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #666;">
                    Chart will go here (e.g., using Chart.js)
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/admin-dashboard.js"></script>
</body>
</html>
