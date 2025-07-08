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
  // Data is now expected to be in global variable salesByMonthYearDataForChart
  console.log("Attempting to render Sales by Month/Year Chart. Raw global data (salesByMonthYearDataForChart):", salesByMonthYearDataForChart);
  if (typeof salesByMonthYearDataForChart !== 'undefined' && salesByMonthYearDataForChart !== null) {
    console.log("Sales by Month/Year data is defined and not null. Length:", salesByMonthYearDataForChart.length);
    if (salesByMonthYearDataForChart.length > 0) {
      console.log("Calling createSalesByMonthChart with data:", salesByMonthYearDataForChart);
      createSalesByMonthChart(salesByMonthYearDataForChart);
    } else {
      console.log("No data for Sales by Month/Year chart (global data array is empty).");
    }
  } else {
    console.log("Global variable salesByMonthYearDataForChart is undefined or null.");
    // Check if the canvas exists, if so, it means the JSP part for "no data" message should be shown
    const salesByMonthYearCanvas = document.getElementById('salesByMonthYearChart');
    if (!salesByMonthYearCanvas) {
        // This case should ideally not happen if JSP structure is consistent
        // console.error("Canvas for Sales by Month/Year chart not found and no global data.");
    }
  }

  // Sales by Week Chart
  // Data is now expected to be in global variable salesByWeekDataForChart
  console.log("Attempting to render Sales by Week Chart. Raw global data (salesByWeekDataForChart):", salesByWeekDataForChart);
  if (typeof salesByWeekDataForChart !== 'undefined' && salesByWeekDataForChart !== null) {
    console.log("Sales by Week data is defined and not null. Length:", salesByWeekDataForChart.length);
    if (salesByWeekDataForChart.length > 0) {
      console.log("Calling createSalesByWeekChart with data:", salesByWeekDataForChart);
      createSalesByWeekChart(salesByWeekDataForChart);
    } else {
      console.log("No data for Sales by Week chart (global data array is empty).");
    }
  } else {
    console.log("Global variable salesByWeekDataForChart is undefined or null.");
    // Check if the canvas exists, if so, it means the JSP part for "no data" message should be shown
    const salesByWeekCanvas = document.getElementById('salesByWeekChart');
    if (!salesByWeekCanvas) {
        // This case should ideally not happen if JSP structure is consistent
        // console.error("Canvas for Sales by Week chart not found and no global data.");
    }
  }
});

function createSalesByWeekChart(salesData) {
  const canvas = document.getElementById('salesByWeekChart');
  if (!canvas) {
    // console.error("Canvas with id salesByWeekChart not found.");
    return;
  }
  const ctx = canvas.getContext('2d');

  // Sort data by year and week to ensure chronological order
  salesData.sort((a, b) => {
    if (a.year !== b.year) {
      return a.year - b.year;
    }
    return a.week - b.week;
  });

  const labels = salesData.map(item => item.weekOfYear); // e.g., "2023-W25"
  const dataValues = salesData.map(item => item.totalSales);

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Doanh Thu Theo Tuần',
        data: dataValues,
        backgroundColor: 'rgba(75, 192, 192, 0.7)', // Teal
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1
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
          text: 'Doanh Thu Bán Vé Theo Tuần'
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
            text: 'Tuần (Năm-WTuần)'
          }
        },
        y: {
          title: {
            display: true,
            text: 'Tổng Doanh Thu (VND)'
          },
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', minimumFractionDigits: 0 }).format(value);
            }
          }
        }
      }
    }
  });
}

// Updated function for Sales by Month
function createSalesByMonthChart(salesData) {
  const canvas = document.getElementById('salesByMonthYearChart'); // Canvas ID remains the same
  if (!canvas) {
    // console.error("Canvas with id salesByMonthYearChart not found.");
    return;
  }
  const ctx = canvas.getContext('2d');

  // Data is already sorted by month/year from servlet if SalesByMonthYearDTO is ordered
  // salesData is expected to be an array of { "label": "Tháng M YYYY", "totalSales": X }
  const labels = salesData.map(item => item.label);
  const dataValues = salesData.map(item => item.totalSales);

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: [{
        label: 'Doanh Thu Theo Tháng',
        data: dataValues,
        backgroundColor: 'rgba(54, 162, 235, 0.7)', // Blue
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1
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
          text: 'Doanh Thu Bán Vé Theo Tháng' // Updated title
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
            text: 'Tháng (Tháng YYYY)' // Updated x-axis title
          }
        },
        y: {
          title: {
            display: true,
            text: 'Tổng Doanh Thu (VND)'
          },
          beginAtZero: true,
          ticks: {
            callback: function(value) {
              return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', minimumFractionDigits: 0 }).format(value);
            }
          }
        }
      }
    }
  });
}
