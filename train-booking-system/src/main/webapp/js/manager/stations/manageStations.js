function removeDiacritics(str) {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}
document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("searchInput");
  const stationFormContainer = document.getElementById(
    "stationFormContainer"
  );

  const formTitle = document.getElementById("formTitle");
  const stationForm = document.getElementById("stationForm");
  const stationIDInput = document.getElementById("stationID");
  const stationCodeInput = document.getElementById("stationCode");
  const stationNameInput = document.getElementById("stationName");
  const addressInput = document.getElementById("address");
  const cityInput = document.getElementById("city");
  const regionInput = document.getElementById("region");
  const phoneNumberInput = document.getElementById("phoneNumber");
  const submitButton = document.getElementById("submitButton");
  const cancelButton = document.getElementById("cancelButton");
  const showAddFormBtn = document.getElementById("showAddFormBtn");
  const table = document.getElementById("stationsTable");
  const allRows = Array.from(table.querySelectorAll("tbody tr"));
  const noStationsRow = allRows.find((row) =>
    row.querySelector("td[colspan='8']")
  );
  const dataRows = allRows.filter((row) => row !== noStationsRow);
  const paginationContainer = document.getElementById(
    "pagination-container"
  );
  const rowsPerPage = 10;
  let currentPage = 1;
  let filteredRows = dataRows;

  function displayRows(page) {
    currentPage = page;
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;

    dataRows.forEach((row) => (row.style.display = "none"));

    const rowsToShow = filteredRows.slice(start, end);
    rowsToShow.forEach((row) => (row.style.display = ""));

    if (noStationsRow) {
      noStationsRow.style.display =
        filteredRows.length === 0 ? "" : "none";
    }
  }

//Phân trang và hiển thị các hàng
  function setupPagination() {
    paginationContainer.innerHTML = ""; //reset cũ
    const pageCount = Math.ceil(filteredRows.length / rowsPerPage);

    if (pageCount <= 1) return;

    const prevLink = document.createElement("a");
    prevLink.href = "#";
    prevLink.innerHTML = "&laquo;";
    prevLink.classList.add("page-link");
    if (currentPage === 1) {
      prevLink.classList.add("disabled");
    }
    prevLink.addEventListener("click", (e) => {
      e.preventDefault();
      if (currentPage > 1) {
        displayRows(currentPage - 1);
        setupPagination();
      }
    });
    paginationContainer.appendChild(prevLink);

    for (let i = 1; i <= pageCount; i++) {
      const pageLink = document.createElement("a");
      pageLink.href = "#";
      pageLink.innerText = i;
      pageLink.classList.add("page-link");
      if (i === currentPage) {
        pageLink.classList.add("active");
      }
      pageLink.addEventListener("click", (e) => {
        e.preventDefault();
        displayRows(i);
        setupPagination();
      });
      paginationContainer.appendChild(pageLink);
    }

    const nextLink = document.createElement("a");
    nextLink.href = "#";
    nextLink.innerHTML = "&raquo;";
    nextLink.classList.add("page-link");
    if (currentPage === pageCount) {
      nextLink.classList.add("disabled");
    }
    nextLink.addEventListener("click", (e) => {
      e.preventDefault();
      if (currentPage < pageCount) {
        displayRows(currentPage + 1);
        setupPagination();
      }
    });
    paginationContainer.appendChild(nextLink);
  }

  function filterAndPaginate() {
    const searchTerm = removeDiacritics(
      searchInput.value.toLowerCase().trim().replace(/\s+/g, " ")
    );

    filteredRows = dataRows.filter((row) => {
      const idCell = removeDiacritics(
        row.cells[0].textContent.toLowerCase()
      );
      const nameCell = removeDiacritics(
        row.cells[1].textContent.toLowerCase()
      );
      return (
        idCell.includes(searchTerm) ||
        nameCell.includes(searchTerm)
      );
    });

    displayRows(1);
    setupPagination();
  }

  if (searchInput) {
    searchInput.addEventListener("keyup", filterAndPaginate);
  }

  //chuyển sang add servlet 
  showAddFormBtn.addEventListener("click", function () {
    window.location.href = "addStation";
  });

  //xử lí nút khóa/mở khóa
  document.querySelectorAll(".lock-btn").forEach((button) => {
    button.addEventListener("click", function () {
      const row = this.closest("tr");
      const editLink = row.querySelector(".edit-btn");
      const isLocked = this.classList.toggle("locked");
      const icon = this.querySelector("i");
      const text = this.querySelector("span");

      if (isLocked) {
        editLink.style.pointerEvents = "none"; //Vô hiệu hóa
        editLink.style.opacity = "0.5";
        icon.className = "fas fa-lock-open"; //đổi icon
        text.textContent = "Mở khóa";
      } else {
        editLink.style.pointerEvents = "auto";
        editLink.style.opacity = "1";
        icon.className = "fas fa-lock";
        text.textContent = "Khóa";
      }
    });
  });

  // Xử lý click checkbox hoạt động
  document.querySelectorAll('.active-checkbox').forEach(function(checkbox) {
    checkbox.addEventListener('change', function() {
      const stationId = this.getAttribute('data-id');
      const isActive = this.checked;
      const confirmMsg = isActive ? 'Bạn có chắc chắn muốn kích hoạt ga này?' : 'Bạn có chắc chắn muốn vô hiệu hóa ga này?';

      if (!confirm(confirmMsg)) {
        this.checked = !isActive;
        return;
      }
      // Gửi AJAX cập nhật trạng thái hoạt động
      fetch('manageStations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `command=updateActiveStatus&stationID=${stationId}&isActive=${isActive}`
      })
      .then(res => res.ok ? location.reload() : alert('Có lỗi xảy ra!'));
    });
  });

  filterAndPaginate();
});
