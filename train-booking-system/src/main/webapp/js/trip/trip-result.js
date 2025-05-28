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
    // Remove 'active' class from all carriages within the same train item
    const allCarriagesInTrain = trainItemElement.querySelectorAll('.carriage-item');
    allCarriagesInTrain.forEach(c => c.classList.remove('active'));

    // Add 'active' class to the clicked carriage
    selectedCarriageElement.classList.add('active');

    // Update the seat details block (placeholder logic)
    const seatDetailsBlock = trainItemElement.querySelector('.seat-details-block');
    const carriageId = selectedCarriageElement.dataset.carriageId; // Assuming data-carriage-id="1", "2", etc.
    
    if (seatDetailsBlock) {
        seatDetailsBlock.innerHTML = `<p>Hiển thị sơ đồ ghế cho Toa ${carriageId}.</p> 
                                   <p>(Đây là placeholder - sơ đồ ghế thực tế sẽ được tải ở đây.)</p>`;
        // TODO: Implement actual seat loading/rendering logic here
        // This might involve an AJAX call to fetch seat layout for the carriageId and tripId
        // For now, it's just a placeholder message.
    }
}

// Example function for when a trip's "Select" or "Book" button is clicked (if added later)
// function proceedToBooking(tripId) {
//     console.log('Proceed to booking for trip ID:', tripId);
//     // Example: window.location.href = `/your-app-context/booking?tripId=${tripId}`;
// }
