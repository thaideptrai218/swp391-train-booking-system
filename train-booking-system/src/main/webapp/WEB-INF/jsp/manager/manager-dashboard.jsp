<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manager Dashboard</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/common.css"/>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        .stats-container, .charts-container, .table-container, .revenue-container {
            padding: 20px;
        }
        .stats-container, .revenue-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
        }
        .stat-card, .chart-card, .table-card {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }
        .stat-card h3, .chart-card h3, .table-card h3 {
            margin-top: 0;
            color: #333;
            font-size: 1.1em;
        }
        .stat-card p {
            font-size: 1.8em;
            font-weight: bold;
            margin: 10px 0 0;
            color: #0056b3;
        }
        .charts-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .chart-card {
             max-width: 400px;
             margin: auto;
        }
        .table-container {
            margin-top: 20px;
        }
        .table-container table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        .table-container th, .table-container td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        .table-container th {
            background-color: #f2f2f2;
        }
        .filter-form {
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <%@ include file="sidebar.jsp" %>

        <div class="main-content">
            <header class="dashboard-header">
                <h1>Bảng Điều Khiển Quản Lý</h1>
            </header>

            <div class="revenue-container">
                 <div class="stat-card">
                    <h3>Doanh thu (1 tháng)</h3>
                    <p><fmt:formatNumber value="${revenue1Month}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
                <div class="stat-card">
                    <h3>Doanh thu (3 tháng)</h3>
                    <p><fmt:formatNumber value="${revenue3Months}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
                <div class="stat-card">
                    <h3>Doanh thu (6 tháng)</h3>
                    <p><fmt:formatNumber value="${revenue6Months}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
                <div class="stat-card">
                    <h3>Doanh thu (1 năm)</h3>
                    <p><fmt:formatNumber value="${revenue12Months}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
            </div>

            <div class="stats-container">
                <div class="stat-card">
                    <h3>Yêu cầu hoàn tiền</h3>
                    <p>${pendingRefundsCount}</p>
                </div>
                <div class="stat-card">
                    <h3>Tổng tiền đã hoàn</h3>
                    <p><fmt:formatNumber value="${totalRefunds}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
                <div class="stat-card">
                    <h3>Lợi nhuận (1 năm)</h3>
                    <p><fmt:formatNumber value="${profit}" type="currency" currencySymbol="₫" maxFractionDigits="0"/></p>
                </div>
                 <div class="stat-card">
                    <h3>Tổng Số Đoàn Tàu</h3>
                    <p>${totalTrains}</p>
                </div>
            </div>

            <div class="charts-container">
                <div class="chart-card">
                    <h3>Ga Đi Phổ Biến</h3>
                    <canvas id="departureStationChart"></canvas>
                    <div class="filter-form">
                        <form action="managerDashboard" method="get" id="departureForm">
                            <select name="departureMonths" onchange="document.getElementById('departureForm').submit()">
                                <option value="1" ${selectedDepartureMonths == 1 ? 'selected' : ''}>1 tháng</option>
                                <option value="3" ${selectedDepartureMonths == 3 ? 'selected' : ''}>3 tháng</option>
                                <option value="6" ${selectedDepartureMonths == 6 ? 'selected' : ''}>6 tháng</option>
                                <option value="12" ${selectedDepartureMonths == 12 ? 'selected' : ''}>1 năm</option>
                            </select>
                             <input type="hidden" name="arrivalMonths" value="${selectedArrivalMonths}">
                        </form>
                    </div>
                </div>
                <div class="chart-card">
                    <h3>Ga Đến Phổ Biến</h3>
                    <canvas id="arrivalStationChart"></canvas>
                     <div class="filter-form">
                        <form action="managerDashboard" method="get" id="arrivalForm">
                            <select name="arrivalMonths" onchange="document.getElementById('arrivalForm').submit()">
                                <option value="1" ${selectedArrivalMonths == 1 ? 'selected' : ''}>1 tháng</option>
                                <option value="3" ${selectedArrivalMonths == 3 ? 'selected' : ''}>3 tháng</option>
                                <option value="6" ${selectedArrivalMonths == 6 ? 'selected' : ''}>6 tháng</option>
                                <option value="12" ${selectedArrivalMonths == 12 ? 'selected' : ''}>1 năm</option>
                            </select>
                            <input type="hidden" name="departureMonths" value="${selectedDepartureMonths}">
                        </form>
                    </div>
                </div>
            </div>

            <div class="table-container">
                <div class="table-card">
                    <h3>Chuyến Đi Phổ Biến Nhất</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Ga Đi</th>
                                <th>Ga Đến</th>
                                <th>Số Lượt Đặt</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="trip" items="${popularTrips}">
                                <tr>
                                    <td>${trip.departureStation}</td>
                                    <td>${trip.arrivalStation}</td>
                                    <td>${trip.bookingCount}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div id="chartData"
         data-popular-departures='${popularDepartureStations}'
         data-popular-arrivals='${popularArrivalStations}'
         style="display: none;"></div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const chartDataElement = document.getElementById('chartData');
            const popularDeparturesData = JSON.parse(chartDataElement.dataset.popularDepartures);
            const popularArrivalsData = JSON.parse(chartDataElement.dataset.popularArrivals);

            const createPieChart = (ctx, parsedData, label) => {
                if (!ctx) return;
                if (window.myCharts && window.myCharts[ctx.canvas.id]) {
                    window.myCharts[ctx.canvas.id].destroy();
                }
                
                if (!parsedData || Object.keys(parsedData).length === 0) {
                    const canvas = ctx.canvas;
                    const dpr = window.devicePixelRatio || 1;
                    canvas.width = 400 * dpr;
                    canvas.height = 200 * dpr;
                    ctx.scale(dpr, dpr);
                    ctx.font = '16px Arial';
                    ctx.fillStyle = '#888';
                    ctx.textAlign = 'center';
                    ctx.fillText('Không có dữ liệu để hiển thị', canvas.width / (2*dpr), canvas.height / (2*dpr));
                    return;
                }

                const labels = Object.keys(parsedData);
                const data = Object.values(parsedData);
                const backgroundColors = [
                    'rgba(255, 99, 132, 0.8)', 'rgba(54, 162, 235, 0.8)',
                    'rgba(255, 206, 86, 0.8)', 'rgba(75, 192, 192, 0.8)',
                    'rgba(153, 102, 255, 0.8)', 'rgba(255, 159, 64, 0.8)'
                ];

                const chart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: label,
                            data: data,
                            backgroundColor: backgroundColors.slice(0, labels.length)
                        }]
                    }
                });
                if (!window.myCharts) {
                    window.myCharts = {};
                }
                window.myCharts[ctx.canvas.id] = chart;
            };

            const departureCtx = document.getElementById('departureStationChart')?.getContext('2d');
            createPieChart(departureCtx, popularDeparturesData, 'Ga đi');

            const arrivalCtx = document.getElementById('arrivalStationChart')?.getContext('2d');
            createPieChart(arrivalCtx, popularArrivalsData, 'Ga đến');
        });
    </script>
</body>
</html>
