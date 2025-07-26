function submitSort(sortField, currentSortOrder) {
  const form = document.getElementById("sortForm");
  form.elements["sortField"].value = sortField;
  if (form.elements["currentSortField"].value === sortField && currentSortOrder === "ASC") {
    form.elements["sortOrder"].value = "DESC";
  } else {
    form.elements["sortOrder"].value = "ASC";
  }
  form.elements["currentSortField"].value = sortField;
  form.submit();
}

function handleHolidayChange(selectElement, tripId) {
  const multiplierDisplayInput = document.getElementById('basePriceMultiplierDisplay_' + tripId);
  const saveMultiplierButton = document.getElementById('saveMultiplierBtn_' + tripId);
  const holidayForm = selectElement.form;

  if (selectElement.value === 'true') {
    multiplierDisplayInput.readOnly = false;
    if(saveMultiplierButton) saveMultiplierButton.style.display = 'inline-flex';
  } else {
    multiplierDisplayInput.readOnly = true;
    multiplierDisplayInput.value = '1.00';
    if(saveMultiplierButton) saveMultiplierButton.style.display = 'none';
  }
  holidayForm.elements['action'].value = 'updateHolidayStatus';
  holidayForm.submit();
}

function submitMultiplierForm(tripId) {
  const holidaySelect = document.getElementById('isHolidayTrip_' + tripId);
  const multiplierDisplay = document.getElementById('basePriceMultiplierDisplay_' + tripId);

  const form = document.createElement('form');
  form.method = 'POST';
  form.action = window.contextPath ? window.contextPath + '/manageTrips' : '/manageTrips';

  const actionInput = document.createElement('input');
  actionInput.type = 'hidden';
  actionInput.name = 'action';
  actionInput.value = 'updateBasePriceMultiplier';
  form.appendChild(actionInput);

  const tripIdInput = document.createElement('input');
  tripIdInput.type = 'hidden';
  tripIdInput.name = 'tripId';
  tripIdInput.value = tripId;
  form.appendChild(tripIdInput);

  const multiplierInput = document.createElement('input');
  multiplierInput.type = 'hidden';
  multiplierInput.name = 'basePriceMultiplier';
  multiplierInput.value = multiplierDisplay.value;
  form.appendChild(multiplierInput);

  const isHolidayInput = document.createElement('input');
  isHolidayInput.type = 'hidden';
  isHolidayInput.name = 'isHolidayTripHidden';
  isHolidayInput.value = holidaySelect.value;
  form.appendChild(isHolidayInput);

  document.body.appendChild(form);
  form.submit();
}

function removeDiacritics(str) {
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("tripSearchInput");
  const table = document.getElementById("tripsTable");
  const tbody = table.querySelector("tbody");
  const allRows = Array.from(table.querySelectorAll("tbody tr"));
  const noTripsRow = allRows.find(row => row.querySelector("td[colspan='6']"));
  const dataRows = allRows.filter(row => row !== noTripsRow);
  const paginationContainer = document.getElementById("pagination-container");
  const rowsPerPage = 10;
  let currentPage = 1;
  let filteredRows = dataRows;

  function displayRows(page) {
      currentPage = page;
      const start = (page - 1) * rowsPerPage;
      const end = start + rowsPerPage;

      dataRows.forEach(row => row.style.display = 'none');

      const rowsToShow = filteredRows.slice(start, end);
      rowsToShow.forEach(row => row.style.display = '');

      if (noTripsRow) {
          noTripsRow.style.display = filteredRows.length === 0 ? '' : 'none';
      }
  }

  function setupPagination() {
      paginationContainer.innerHTML = "";
      const pageCount = Math.ceil(filteredRows.length / rowsPerPage);

      if (pageCount <= 1) return;

      const prevLink = document.createElement('a');
      prevLink.href = '#';
      prevLink.innerHTML = '&laquo;';
      prevLink.classList.add('page-link');
      if (currentPage === 1) {
          prevLink.classList.add('disabled');
      }
      prevLink.addEventListener('click', (e) => {
          e.preventDefault();
          if (currentPage > 1) {
              displayRows(currentPage - 1);
              setupPagination();
          }
      });
      paginationContainer.appendChild(prevLink);

      for (let i = 1; i <= pageCount; i++) {
          const pageLink = document.createElement('a');
          pageLink.href = '#';
          pageLink.innerText = i;
          pageLink.classList.add('page-link');
          if (i === currentPage) {
              pageLink.classList.add('active');
          }
          pageLink.addEventListener('click', (e) => {
              e.preventDefault();
              displayRows(i);
              setupPagination();
          });
          paginationContainer.appendChild(pageLink);
      }

      const nextLink = document.createElement('a');
      nextLink.href = '#';
      nextLink.innerHTML = '&raquo;';
      nextLink.classList.add('page-link');
      if (currentPage === pageCount) {
          nextLink.classList.add('disabled');
      }
      nextLink.addEventListener('click', (e) => {
          e.preventDefault();
          if (currentPage < pageCount) {
              displayRows(currentPage + 1);
              setupPagination();
          }
      });
      paginationContainer.appendChild(nextLink);
  }

  function filterAndPaginate() {
      const searchTerm = searchInput.value.trim();
      
      // Tạo form để submit search với các filter hiện tại
      const searchForm = document.createElement('form');
      searchForm.method = 'GET';
      searchForm.action = window.location.pathname;
      
      // Thêm các tham số hiện tại
      const urlParams = new URLSearchParams(window.location.search);
      for (let [key, value] of urlParams.entries()) {
          if (key !== 'searchTerm') { // Không override searchTerm
              const input = document.createElement('input');
              input.type = 'hidden';
              input.name = key;
              input.value = value;
              searchForm.appendChild(input);
          }
      }
      
      // Thêm searchTerm mới
      const searchInputHidden = document.createElement('input');
      searchInputHidden.type = 'hidden';
      searchInputHidden.name = 'searchTerm';
      searchInputHidden.value = searchTerm;
      searchForm.appendChild(searchInputHidden);
      
      document.body.appendChild(searchForm);
      searchForm.submit();
  }

  if (searchInput) {
      let searchTimeout;
      searchInput.addEventListener("keyup", function() {
          clearTimeout(searchTimeout);
          searchTimeout = setTimeout(() => {
              filterAndPaginate();
          }, 500); // Đợi 500ms sau khi user ngừng gõ
      });
  }

  // Không cần gọi filterAndPaginate() khi load trang nữa
  // vì giờ search được xử lý bởi backend
});

document.addEventListener('DOMContentLoaded', function() {
  document.querySelectorAll('.holiday-yesno-select').forEach(function(yesnoSelect) {
    yesnoSelect.addEventListener('change', function() {
      var tripId = this.dataset.tripId;
      var holidaySelect = document.getElementById('holidaySelect_' + tripId);
      var multiplierInput = document.getElementById('basePriceMultiplierDisplay_' + tripId);
      var hiddenMultiplier = document.getElementById('basePriceMultiplier_' + tripId);
      var hiddenIsHoliday = document.getElementById('isHolidayTrip_' + tripId);
      if (this.value === '1') {
        holidaySelect.style.display = 'inline-block';
        hiddenIsHoliday.value = 1;
        if (holidaySelect.value) {
          var event = new Event('change');
          holidaySelect.dispatchEvent(event);
        }
      } else {
        holidaySelect.style.display = 'none';
        multiplierInput.value = '1.00';
        hiddenMultiplier.value = '1.00';
        hiddenIsHoliday.value = 0;
      }
    });
  });
  document.querySelectorAll('.holiday-select').forEach(function(select) {
    select.addEventListener('change', function() {
      var tripId = this.dataset.tripId;
      var discount = this.value;
      var multiplierInput = document.getElementById('basePriceMultiplierDisplay_' + tripId);
      var hiddenMultiplier = document.getElementById('basePriceMultiplier_' + tripId);
      var hiddenIsHoliday = document.getElementById('isHolidayTrip_' + tripId);
      if (discount && !isNaN(discount)) {
        var multiplier = (parseFloat(discount) / 100).toFixed(2);
        multiplierInput.value = multiplier;
        hiddenMultiplier.value = multiplier;
        hiddenIsHoliday.value = 1;
      } else {
        multiplierInput.value = '1.00';
        hiddenMultiplier.value = '1.00';
        hiddenIsHoliday.value = 0;
      }
    });
  });
});
