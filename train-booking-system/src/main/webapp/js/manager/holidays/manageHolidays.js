document.addEventListener("DOMContentLoaded", function () {
  const checkboxes = document.querySelectorAll(".status-checkbox");
  checkboxes.forEach((checkbox) => {
    checkbox.addEventListener("change", function () {
      const id = this.dataset.id;
      const isActive = this.checked;

      fetch(window.contextPath ? window.contextPath + "/manageHolidays" : "/manageHolidays", {
        method: "POST",
        headers: {
          "Content-Type": "application/x-www-form-urlencoded",
        },
        body:
          "action=updateHolidayStatus&id=" + id + "&isActive=" + isActive,
      })
        .then((response) => {
          if (!response.ok) {
            alert("Error updating status");
            this.checked = !isActive; // Revert the checkbox on error
          }
        })
        .catch(() => {
          alert("Error updating status");
          this.checked = !isActive; // Revert the checkbox on error
        });
    });
  });

  function setupPaginationForTable(tableId, containerId) {
    const table = document.getElementById(tableId);
    if (!table) return;

    const allRows = Array.from(table.querySelectorAll("tbody tr"));
    const noDataRow = allRows.find((row) =>
      row.querySelector("td[colspan]")
    );
    const dataRows = allRows.filter((row) => row !== noDataRow);
    const paginationContainer = document.getElementById(containerId);
    const rowsPerPage = 5;
    let currentPage = 1;

    function displayRows(page) {
      currentPage = page;
      const start = (page - 1) * rowsPerPage;
      const end = start + rowsPerPage;

      dataRows.forEach((row) => (row.style.display = "none"));
      const rowsToShow = dataRows.slice(start, end);
      rowsToShow.forEach((row) => (row.style.display = ""));

      if (noDataRow) {
        noDataRow.style.display = dataRows.length === 0 ? "" : "none";
      }
    }

    function setupPagination() {
      paginationContainer.innerHTML = "";
      const pageCount = Math.ceil(dataRows.length / rowsPerPage);

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

    displayRows(1);
    setupPagination();
  }

  setupPaginationForTable(
    "holidaysTable",
    "holidays-pagination-container"
  );
});
