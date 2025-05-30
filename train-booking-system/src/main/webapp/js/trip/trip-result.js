// trip-result.js
document.addEventListener('DOMContentLoaded', function () {
    const trainItems = document.querySelectorAll('.train-item');

    trainItems.forEach(item => {
        // Make the entire train item clickable to expand/collapse
        const collapsedSummary = item.querySelector('.train-item-collapsed-summary');
        if (collapsedSummary) {
            collapsedSummary.addEventListener('click', () => toggleTrainItemDetails(item));
            // Add keyboard accessibility for expansion
            collapsedSummary.addEventListener('keydown', (event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    toggleTrainItemDetails(item);
                }
            });
        }


        const carriageItems = item.querySelectorAll('.carriage-item');
        carriageItems.forEach(carriage => {
            carriage.addEventListener('click', () => selectCarriage(carriage, item));
            // Add keyboard accessibility for carriage selection
            carriage.addEventListener('keydown', (event) => {
                if (event.key === 'Enter' || event.key === ' ') {
                    event.preventDefault();
                    selectCarriage(carriage, item);
                }
            });
        });
    });
});

function toggleTrainItemDetails(trainItemElement) {
    const details = trainItemElement.querySelector('.expanded-details');
    const isExpanded = details.style.display === 'block';

    details.style.display = isExpanded ? 'none' : 'block';
    trainItemElement.classList.toggle('expanded', !isExpanded);

    // Optional: If there's a button, change its text
    // const expandButton = trainItemElement.querySelector('.expand-details-btn');
    // if (expandButton) {
    //     expandButton.textContent = isExpanded ? 'Chi tiết' : 'Ẩn chi tiết';
    // }
}

function selectCarriage(selectedCarriageElement, trainItemElement) {
    // Find the parent .train-composition-display to scope the active class removal
    const compositionDisplay = selectedCarriageElement.closest('.train-composition-display');
    if (!compositionDisplay) return;

    // Remove 'active' class from all carriages within the same composition display
    const allCarriagesInComposition = compositionDisplay.querySelectorAll('.carriage-item');
    allCarriagesInComposition.forEach(c => c.classList.remove('active'));

    // Add 'active' class to the clicked carriage
    selectedCarriageElement.classList.add('active');

    // Retrieve data from the selected carriage
    const coachTypeName = selectedCarriageElement.dataset.coachTypename;
    const coachPosition = selectedCarriageElement.dataset.coachPosition;
    const coachDescription = selectedCarriageElement.dataset.coachDescription;
    const tripId = selectedCarriageElement.dataset.tripId;
    const tripLeg = selectedCarriageElement.dataset.tripLeg; // 'outbound' or 'return'

    // Update the carriage details description block
    const descriptionBlockId = `carriageDescription-${tripLeg}-${tripId}`;
    const descriptionBlock = document.getElementById(descriptionBlockId);
    if (descriptionBlock) {
        if (coachTypeName && coachPosition) {
            let descriptionHtml = `<strong>${coachTypeName} - Toa ${coachPosition}</strong>`;
            if (coachDescription && coachDescription !== "null" && coachDescription.trim() !== "") {
                descriptionHtml += `<br>${coachDescription}`;
            }
            descriptionBlock.innerHTML = `<p>${descriptionHtml}</p>`;
        } else {
            descriptionBlock.innerHTML = `<p>Thông tin chi tiết không có sẵn.</p>`;
        }
    }

    // Update the seat details block (placeholder logic)
    const seatDetailsBlockId = `seatDetailsBlock-${tripLeg}-${tripId}`;
    const seatDetailsBlock = document.getElementById(seatDetailsBlockId);
    if (seatDetailsBlock) {
        seatDetailsBlock.innerHTML = `<p>Sơ đồ ghế cho Toa ${coachPosition}.</p> 
                                   <p>(Đây là placeholder - sơ đồ ghế thực tế sẽ được tải ở đây.)</p>`;
        // TODO: Implement actual seat loading/rendering logic here
    }
}

// Example function for when a trip's "Select" or "Book" button is clicked (if added later)
// function proceedToBooking(tripId) {
//     console.log('Proceed to booking for trip ID:', tripId);
//     // Example: window.location.href = `/your-app-context/booking?tripId=${tripId}`;
// }
