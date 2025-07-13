document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.train-header.clickable').forEach(header => {
        header.addEventListener('click', function() {
            const trainContainer = this.closest('.train-container');
            const compositionDisplay = trainContainer.querySelector('.train-composition-display');
            
            if (compositionDisplay.style.display === 'none') {
                compositionDisplay.style.display = 'flex';
            } else {
                compositionDisplay.style.display = 'none';
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
