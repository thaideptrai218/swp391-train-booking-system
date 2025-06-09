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
});
