<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Staff Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
</head>
<body>
    <div class="dashboard-container">
        <aside class="sidebar">
            <h2>Staff Panel</h2>
            <nav>
                <ul>
                    <li><a href="#">Dashboard</a></li>
                    <li><a href="#">Manage Bookings</a></li>
                    <li><a href="#">Check-in/Check-out</a></li>
                    <li><a href="#">Customer Support</a></li>
                    <li><a href="#">Reports</a></li>
                    <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                </ul>
            </nav>
        </aside>
        <main class="main-content">
            <header class="header">
                <h1>Welcome, Staff!</h1>
                <div class="user-info">
                    <span>Logged in as: Staff User</span>
                </div>
            </header>

            <section class="cards-container">
                <div class="card">
                    <h3>Pending Bookings</h3>
                    <p>45</p>
                </div>
                <div class="card">
                    <h3>Today's Departures</h3>
                    <p>12</p>
                </div>
                <div class="card">
                    <h3>Active Tickets</h3>
                    <p>567</p>
                </div>
                <div class="card">
                    <h3>Recent Inquiries</h3>
                    <p>8</p>
                </div>
            </section>

            <section class="charts-container">
                <h3>Daily Operations Overview (Placeholder)</h3>
                <div style="height: 300px; background-color: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #666;">
                    Operational data chart will go here
                </div>
            </section>
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/staff-dashboard.js"></script>
</body>
</html>
