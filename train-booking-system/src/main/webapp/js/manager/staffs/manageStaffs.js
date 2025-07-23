// This script assumes a global variable 'contextPath' is defined in the JSP
// e.g., <script>var contextPath = '${pageContext.request.contextPath}';</script>

function toggleStaffStatus(userId, isActive) {
  const message = isActive
    ? "Bạn có chắc chắn muốn khóa nhân viên này không?"
    : "Bạn có chắc chắn muốn mở khóa nhân viên này không?";
  if (confirm(message)) {
    const form = document.createElement("form");
    form.method = "post";
    form.action = window.contextPath ? window.contextPath + "/manageStaffs" : "/manageStaffs";

    const actionInput = document.createElement("input");
    actionInput.type = "hidden";
    actionInput.name = "action";
    actionInput.value = "toggleStatus";
    form.appendChild(actionInput);

    const idInput = document.createElement("input");
    idInput.type = "hidden";
    idInput.name = "userID";
    idInput.value = userId;
    form.appendChild(idInput);

    document.body.appendChild(form);
    form.submit();
  }
}

function removeDiacritics(str) {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

document.addEventListener("DOMContentLoaded", function () {
  const searchInput = document.getElementById("staffSearchInput");
  const table = document.getElementById("staffsTable");
  const allRows = Array.from(table.querySelectorAll("tbody tr"));
  const noStaffRow = allRows.find((row) =>
    row.querySelector("td[colspan='8']")
  );
  const dataRows = allRows.filter((row) => row !== noStaffRow);
  const paginationContainer = document.getElementById(
    "pagination-container"
  );
  const rowsPerPage = 10;
  let currentPage = 1;
  let filteredRows = dataRows;

  const staffFormContainer =
    document.getElementById("staffFormContainer");
  const formTitle = document.getElementById("formTitle");
  const staffForm = document.getElementById("staffForm");
  const formAction = document.getElementById("formAction");
  const userID = document.getElementById("userID");
  const fullName = document.getElementById("fullName");
  const email = document.getElementById("email");
  const phoneNumber = document.getElementById("phoneNumber");
  const idCardNumber = document.getElementById("idCardNumber");
  const gender = document.getElementById("gender");
  const address = document.getElementById("address");
  const passwordFieldContainer = document.getElementById(
    "passwordFieldContainer"
  );
  const passwordInput = document.getElementById("password");
  const isActive = document.getElementById("isActive");
  const showAddFormBtn = document.getElementById("showAddFormBtn");
  const cancelButton = document.getElementById("cancelButton");
  const warningDiv = document.getElementById('staffFormWarning');

  showAddFormBtn.addEventListener("click", function () {
    staffForm.reset();
    formTitle.textContent = "Thêm nhân viên mới";
    formAction.value = "add";
    userID.value = "";
    passwordFieldContainer.style.display = "none";
    passwordInput.required = false;
    staffFormContainer.style.display = "block";
    staffFormContainer.scrollIntoView({ behavior: "smooth" });
  });

  document.querySelectorAll(".edit-btn").forEach((button) => {
    button.addEventListener("click", function () {
      const row = this.closest("tr");
      if (row.dataset.isActive === "false") {
        alert("Không thể sửa thông tin nhân viên đã bị khóa.");
        return;
      }
      formTitle.textContent = "Sửa thông tin nhân viên";
      formAction.value = "update";
      userID.value = row.dataset.userId;
      fullName.value = row.dataset.fullName;
      email.value = row.dataset.email;
      phoneNumber.value = row.dataset.phoneNumber;
      idCardNumber.value = row.dataset.idCardNumber;
      // Map gender from English to Vietnamese for display
      let genderValue = row.dataset.gender;
      if (genderValue === "Male") genderValue = "Nam";
      else if (genderValue === "Female") genderValue = "Nữ";
      else if (genderValue === "Other") genderValue = "Khác";
      gender.value = genderValue;
      address.value = row.dataset.address;
      isActive.checked = row.dataset.isActive === "true";

      passwordFieldContainer.style.display = "none";
      passwordInput.required = false;

      staffFormContainer.style.display = "block";
      staffFormContainer.scrollIntoView({ behavior: "smooth" });
    });
  });

  cancelButton.addEventListener("click", function () {
    staffFormContainer.style.display = "none";
    staffForm.reset();
  });

  function displayRows(page) {
    currentPage = page;
    const start = (page - 1) * rowsPerPage;
    const end = start + rowsPerPage;

    dataRows.forEach((row) => (row.style.display = "none"));
    const rowsToShow = filteredRows.slice(start, end);
    rowsToShow.forEach((row) => (row.style.display = ""));

    if (noStaffRow) {
      noStaffRow.style.display = filteredRows.length === 0 ? "" : "none";
    }
  }

  function setupPagination() {
    paginationContainer.innerHTML = "";
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
      searchInput.value.trim().toLowerCase().replace(/\s+/g, " ")
    );

    filteredRows = dataRows.filter((row) => {
      const idCell = removeDiacritics(
        row.cells[0].textContent.toLowerCase()
      );
      const nameCell = removeDiacritics(
        row.cells[1].textContent.toLowerCase()
      );
      const emailCell = removeDiacritics(
        row.cells[2].textContent.toLowerCase()
      );
      const phoneCell = removeDiacritics(
        row.cells[3].textContent.toLowerCase()
      );
      const idCardCell = removeDiacritics(
        row.cells[4].textContent.toLowerCase()
      );

      return (
        idCell.includes(searchTerm) ||
        nameCell.includes(searchTerm) ||
        emailCell.includes(searchTerm) ||
        phoneCell.includes(searchTerm) ||
        idCardCell.includes(searchTerm)
      );
    });

    displayRows(1);
    setupPagination();
  }

  if (searchInput) {
    searchInput.addEventListener("keyup", filterAndPaginate);
  }

  filterAndPaginate();
});

document.addEventListener('DOMContentLoaded', function() {
  var staffForm = document.getElementById('staffForm');
  var warningDiv = document.getElementById('staffFormWarning');
  staffForm.addEventListener('submit', function(e) {
    var fullName = document.getElementById('fullName').value.trim();
    var email = document.getElementById('email').value.trim();
    var phoneNumber = document.getElementById('phoneNumber').value.trim();
    var idCardNumber = document.getElementById('idCardNumber').value.trim();
    if (!fullName || !email || !phoneNumber || !idCardNumber) {
      e.preventDefault();
      warningDiv.style.display = '';
      setTimeout(function() {
        warningDiv.style.display = 'none';
      }, 8000);
      return false;
    }
  });
});
