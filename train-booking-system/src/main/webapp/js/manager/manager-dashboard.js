function createPieChart(canvasId, chartLabel, labels, data, titleText) {
  const canvas = document.getElementById(canvasId);
  if (!canvas) {
    // console.error(`Canvas with id ${canvasId} not found.`);
    return;
  }
  const ctx = canvas.getContext("2d");
  const chartData = {
    labels: labels,
    datasets: [
      {
        label: chartLabel,
        data: data,
        backgroundColor: [
          "rgba(255, 99, 132, 0.7)",
          "rgba(54, 162, 235, 0.7)",
          "rgba(255, 206, 86, 0.7)",
          "rgba(75, 192, 192, 0.7)",
          "rgba(153, 102, 255, 0.7)",
          "rgba(255, 159, 64, 0.7)",
          "rgba(201, 203, 207, 0.7)",
          "rgba(255, 87, 34, 0.7)",
          "rgba(0, 150, 136, 0.7)",
          "rgba(121, 85, 72, 0.7)",
        ],
        borderColor: [
          "rgba(255, 99, 132, 1)",
          "rgba(54, 162, 235, 1)",
          "rgba(255, 206, 86, 1)",
          "rgba(75, 192, 192, 1)",
          "rgba(153, 102, 255, 1)",
          "rgba(255, 159, 64, 1)",
          "rgba(201, 203, 207, 1)",
          "rgba(255, 87, 34, 1)",
          "rgba(0, 150, 136, 1)",
          "rgba(121, 85, 72, 1)",
        ],
        borderWidth: 1,
      },
    ],
  };
  new Chart(ctx, {
    type: "pie",
    data: chartData,
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: "top",
        },
        title: {
          display: true,
          text: titleText,
        },
      },
    },
  });
}

// createBarChart function removed as it's no longer used.

document.addEventListener('DOMContentLoaded', function () {
  // Best Sellers Chart
  const bestSellersCanvas = document.getElementById('bestSellersChart');
  if (bestSellersCanvas && bestSellersCanvas.dataset.labels && bestSellersCanvas.dataset.values) {
    try {
      const labels = JSON.parse(bestSellersCanvas.dataset.labels);
      const values = JSON.parse(bestSellersCanvas.dataset.values);
      if (labels && values && labels.length > 0 && values.length > 0 && labels.length === values.length) {
        createPieChart('bestSellersChart', 'Số Vé Bán Được', labels, values, 'Tuyến Đường Bán Chạy Nhất');
      } else if (labels && labels.length === 0) {
        // console.log("No data for Best Sellers chart.");
      }
    } catch (e) {
      console.error("Error processing data for bestSellersChart:", e, "Labels:", bestSellersCanvas.dataset.labels, "Values:", bestSellersCanvas.dataset.values);
    }
  }

  // Popular Origin Stations Chart
  const popularOriginStationsCanvas = document.getElementById('popularOriginStationsChart');
  if (popularOriginStationsCanvas && popularOriginStationsCanvas.dataset.labels && popularOriginStationsCanvas.dataset.values) {
    try {
      const labels = JSON.parse(popularOriginStationsCanvas.dataset.labels);
      const values = JSON.parse(popularOriginStationsCanvas.dataset.values);
      if (labels && values && labels.length > 0 && values.length > 0 && labels.length === values.length) {
        createPieChart('popularOriginStationsChart', 'Số Lần Khởi Hành', labels, values, 'Ga Khởi Hành Phổ Biến Nhất');
      } else if (labels && labels.length === 0) {
        // console.log("No data for Popular Origin Stations chart.");
      }
    } catch (e) {
      console.error("Error processing data for popularOriginStationsChart:", e, "Labels:", popularOriginStationsCanvas.dataset.labels, "Values:", popularOriginStationsCanvas.dataset.values);
    }
  }

  // Popular Destination Stations Chart
  const popularDestinationStationsCanvas = document.getElementById('popularDestinationStationsChart');
  if (popularDestinationStationsCanvas && popularDestinationStationsCanvas.dataset.labels && popularDestinationStationsCanvas.dataset.values) {
    try {
      const labels = JSON.parse(popularDestinationStationsCanvas.dataset.labels);
      const values = JSON.parse(popularDestinationStationsCanvas.dataset.values);
      if (labels && values && labels.length > 0 && values.length > 0 && labels.length === values.length) {
        createPieChart('popularDestinationStationsChart', 'Số Lần Đến', labels, values, 'Ga Đến Phổ Biến Nhất');
      } else if (labels && labels.length === 0) {
        // console.log("No data for Popular Destination Stations chart.");
      }
    } catch (e) {
      console.error("Error processing data for popularDestinationStationsChart:", e, "Labels:", popularDestinationStationsCanvas.dataset.labels, "Values:", popularDestinationStationsCanvas.dataset.values);
    }
  }
  // Removed initializations for weeklyBookingChart, monthlyBookingChart, yearlyBookingChart

  // Sales by Month/Year Chart
  const salesByMonthYearCanvas = document.getElementById('salesByMonthYearChart');
  if (salesByMonthYearCanvas && salesByMonthYearCanvas.dataset.sales) {
    try {
      const salesDataString = salesByMonthYearCanvas.dataset.sales;
      if (salesDataString && salesDataString.trim() !== "") {
        const salesData = JSON.parse(salesDataString);
        if (salesData && salesData.length > 0) {
          createSalesByMonthYearChart(salesData);
        } else {
          // console.log("No data for Sales by Month/Year chart.");
        }
      } else {
        // console.log("Sales data attribute is empty or missing for Sales by Month/Year chart.");
      }
    } catch (e) {
      console.error("Error processing data for salesByMonthYearChart:", e, "Data:", salesByMonthYearCanvas.dataset.sales);
    }
  }
});

function createSalesByMonthYearChart(salesData) {
  const canvas = document.getElementById('salesByMonthYearChart');
  if (!canvas) {
    // console.error("Canvas with id salesByMonthYearChart not found.");
    return;
  }
  const ctx = canvas.getContext('2d');

  // Process data: group by month, then by year for datasets
  const monthlyLabels = [
    "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
  ];

  const years = [...new Set(salesData.map(item => item.year))].sort((a, b) => a - b);
  const datasets = [];
  const backgroundColors = [
    'rgba(54, 162, 235, 0.7)',  // Blue
    'rgba(255, 99, 132, 0.7)',  // Red
    'rgba(75, 192, 192, 0.7)',  // Green
    'rgba(255, 206, 86, 0.7)',  // Yellow
    'rgba(153, 102, 255, 0.7)', // Purple
    'rgba(255, 159, 64, 0.7)'   // Orange
  ];
   const borderColors = [
    'rgba(54, 162, 235, 1)',
    'rgba(255, 99, 132, 1)',
    'rgba(75, 192, 192, 1)',
    'rgba(255, 206, 86, 1)',
    'rgba(153, 102, 255, 1)',
    'rgba(255, 159, 64, 1)'
  ];


  years.forEach((year, index) => {
    const yearData = monthlyLabels.map((monthName, monthIndex) => {
      const monthData = salesData.find(d => d.year === year && d.month === (monthIndex + 1));
      return monthData ? monthData.totalSales : 0;
    });
    datasets.push({
      label: `Năm ${year}`,
      data: yearData,
      backgroundColor: backgroundColors[index % backgroundColors.length],
      borderColor: borderColors[index % borderColors.length],
      borderWidth: 1
    });
  });

  new Chart(ctx, {
    type: 'bar', // Column chart
    data: {
      labels: monthlyLabels,
      datasets: datasets
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
          text: 'Doanh Thu Bán Vé Theo Tháng và Năm'
        },
        tooltip: {
            callbacks: {
                label: function(context) {
                    let label = context.dataset.label || '';
                    if (label) {
                        label += ': ';
                    }
                    if (context.parsed.y !== null) {
                        label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                    }
                    return label;
                }
            }
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Tháng'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Tổng Doanh Thu (VND)'
          },
          beginAtZero: true,
          ticks: {
            callback: function(value, index, values) {
              return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', minimumFractionDigits: 0 }).format(value);
            }
          }
        }
      }
    }
  });
}
