document.addEventListener('DOMContentLoaded', function() {
  const trainList = document.getElementById('trainList');
  const trainContainers = Array.from(trainList.querySelectorAll('.train-container'));
  const paginationContainer = document.getElementById('pagination-container');
  const trainsPerPage = 5;
  let currentPage = 1;
  function displayTrains(page) {
    currentPage = page;
    const start = (page - 1) * trainsPerPage;
    const end = start + trainsPerPage;
    trainContainers.forEach((container, idx) => {
      container.style.display = (idx >= start && idx < end) ? '' : 'none';
    });
  }
  function setupPagination() {
    paginationContainer.innerHTML = '';
    const pageCount = Math.ceil(trainContainers.length / trainsPerPage);
    if (pageCount <= 1) return;
    // Previous button
    const prevLink = document.createElement('a');
    prevLink.href = '#';
    prevLink.innerHTML = '&laquo;';
    prevLink.classList.add('page-link');
    if (currentPage === 1) prevLink.classList.add('disabled');
    prevLink.addEventListener('click', function(e) {
      e.preventDefault();
      if (currentPage > 1) {
        displayTrains(currentPage - 1);
        setupPagination();
      }
    });
    paginationContainer.appendChild(prevLink);
    // Page numbers
    for (let i = 1; i <= pageCount; i++) {
      const pageLink = document.createElement('a');
      pageLink.href = '#';
      pageLink.innerText = i;
      pageLink.classList.add('page-link');
      if (i === currentPage) pageLink.classList.add('active');
      pageLink.addEventListener('click', function(e) {
        e.preventDefault();
        displayTrains(i);
        setupPagination();
      });
      paginationContainer.appendChild(pageLink);
    }
    // Next button
    const nextLink = document.createElement('a');
    nextLink.href = '#';
    nextLink.innerHTML = '&raquo;';
    nextLink.classList.add('page-link');
    if (currentPage === pageCount) nextLink.classList.add('disabled');
    nextLink.addEventListener('click', function(e) {
      e.preventDefault();
      if (currentPage < pageCount) {
        displayTrains(currentPage + 1);
        setupPagination();
      }
    });
    paginationContainer.appendChild(nextLink);
  }
  // Initial setup
  displayTrains(1);
  setupPagination();

  // Auto-set next coach number and coach name on modal open
  document.querySelectorAll('a[onclick^="openModal(\'add-coach-modal-"]').forEach(function(btn) {
    btn.addEventListener('click', function() {
      var trainId = btn.getAttribute('onclick').match(/add-coach-modal-(\d+)/)[1];
      var coachCount = document.querySelectorAll('.train-container[data-train-id="' + trainId + '"] .carriage-item').length - 1; // exclude add button
      var nextNumber = coachCount + 1;
      document.getElementById('nextCoachNumber_' + trainId).value = nextNumber;
      document.getElementById('autoCoachName_' + trainId).value = 'Toa ' + nextNumber;
    });
  });

  // Toggle hiển thị coach khi click vào train-header (chỉ 1 train mở, scroll mượt)
  document.querySelectorAll('.train-header').forEach(function(header) {
    header.addEventListener('click', function() {
      var container = header.closest('.train-container');
      var composition = container.querySelector('.train-composition-display');
      if (composition) {
        // Ẩn tất cả các train-composition khác
        document.querySelectorAll('.train-composition-display').forEach(function(other) {
          if (other !== composition) other.style.display = 'none';
        });
        // Toggle coach của train này
        composition.style.display = (composition.style.display === 'none' || composition.style.display === '') ? 'flex' : 'none';
        // Scroll ngang mượt nếu hiển thị
        if (composition.style.display === 'flex') {
          composition.scrollIntoView({behavior: 'smooth', block: 'center'});
        }
      }
    });
  });
});

function openModal(modalId) {
    var modal = document.getElementById(modalId);
    modal.style.display = "block";
}

function closeModal(modalId) {
    var modal = document.getElementById(modalId);
    modal.style.display = "none";
}

window.onclick = function(event) {
    if (event.target.className === 'modal') {
        event.target.style.display = "none";
    }
}

function toggleCoachDetails(trainId, coachId) {
    var coachDetails = document.getElementById('coach-details-' + trainId + '-' + coachId);
    var allCoachDetails = document.querySelectorAll('.train-container[data-train-id="' + trainId + '"] .coach-details');
    
    allCoachDetails.forEach(function(detail) {
        if (detail.id !== coachDetails.id) {
            detail.style.display = 'none';
        }
    });

    if (coachDetails.style.display === "none") {
        coachDetails.style.display = "block";
    } else {
        coachDetails.style.display = "none";
    }
}
