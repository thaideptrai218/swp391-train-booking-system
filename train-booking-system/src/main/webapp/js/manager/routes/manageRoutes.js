function removeDiacritics(str) {
    return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("routeSearchInput");
  const table = document.getElementById("routesTable");
  const allRows = table ? Array.from(table.querySelectorAll("tbody tr")) : [];
  const noResultsRow = allRows.find(row => row.querySelector("td[colspan='3']"));
  const dataRows = allRows.filter(row => row !== noResultsRow);
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

      if (noResultsRow) {
          noResultsRow.style.display = filteredRows.length === 0 ? '' : 'none';
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
      const searchTerm = removeDiacritics(searchInput.value.trim().toLowerCase().replace(/\s+/g, " "));
      
      filteredRows = dataRows.filter(row => {
          const idCell = row.cells[0];
          const nameCell = row.cells[1];
          if (idCell && nameCell) {
              const idText = removeDiacritics(idCell.textContent.toLowerCase());
              const nameText = removeDiacritics(nameCell.textContent.toLowerCase());
              return idText.includes(searchTerm) || nameText.includes(searchTerm);
          }
          return false;
      });

      displayRows(1);
      setupPagination();
  }

  if (searchInput) {
      searchInput.addEventListener("keyup", filterAndPaginate);
  }

  const editDepartureSelect = document.getElementById('editDepartureStationId');
  const editArrivalSelect = document.getElementById('editArrivalStationId');

  function updateStationOptions(sourceSelect, targetSelect) {
      const selectedValue = sourceSelect.value;
      for (let i = 0; i < targetSelect.options.length; i++) {
          const option = targetSelect.options[i];
          if (option.value === selectedValue && option.value !== "") { // Don't hide the placeholder
              option.style.display = 'none';
          } else {
              option.style.display = '';
          }
      }
  }

  if (editDepartureSelect && editArrivalSelect) {
      editDepartureSelect.addEventListener('change', function() {
          updateStationOptions(this, editArrivalSelect);
      });
      editArrivalSelect.addEventListener('change', function() {
          updateStationOptions(this, editDepartureSelect);
      });
      // Initial sync for edit form when page loads with pre-selected values
      updateStationOptions(editDepartureSelect, editArrivalSelect);
      updateStationOptions(editArrivalSelect, editDepartureSelect);
  }

  const urlParams = new URLSearchParams(window.location.search);
  const action = urlParams.get('action');
  const routeId = urlParams.get('routeId');

  if (action === 'showEditForm' && routeId) {
    const editFormContainer = document.getElementById('editRouteFormContainer');
    if (editFormContainer) {
      editFormContainer.style.display = 'block';
      // Scroll to the form if the anchor is present
      if (window.location.hash === '#editRouteFormContainer') {
           editFormContainer.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }
  
  filterAndPaginate();
});
