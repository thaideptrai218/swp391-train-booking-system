document.addEventListener('DOMContentLoaded', function () {
    const switchStationsBtn = document.getElementById('switchStationsBtn');
    const originStationInput = document.getElementById('originStation');
    const destinationStationInput = document.getElementById('destinationStation');

    const addReturnDateBtn = document.getElementById('addReturnDateBtn');
    const returnDateFieldWrapper = document.getElementById('returnDateFieldWrapper');
    const returnDateInput = document.getElementById('returnDate');

    const totalPassengerDisplay = document.getElementById('totalPassengerDisplay');
    const passengerDropdown = document.getElementById('passengerDropdown');
    const totalPassengerText = document.getElementById('totalPassengerText');

    const numAdultsInput = document.getElementById('numAdults');
    const numChildrenInput = document.getElementById('numChildren');
    const numStudentsInput = document.getElementById('numStudents');
    const numElderlyInput = document.getElementById('numElderly');
    const numGroupInput = document.getElementById('numGroup');

    // Switch Origin and Destination
    if (switchStationsBtn && originStationInput && destinationStationInput) {
        switchStationsBtn.addEventListener('click', function () {
            const temp = originStationInput.value;
            originStationInput.value = destinationStationInput.value;
            destinationStationInput.value = temp;
        });
    }

    // Toggle Return Date
    if (addReturnDateBtn && returnDateFieldWrapper && returnDateInput) {
        addReturnDateBtn.addEventListener('click', function () {
            if (returnDateFieldWrapper.style.display === 'none') {
                returnDateFieldWrapper.style.display = 'flex';
                addReturnDateBtn.textContent = '- Bỏ ngày về';
                // Optionally, set a default return date or focus
            } else {
                returnDateFieldWrapper.style.display = 'none';
                returnDateInput.value = ''; // Clear return date
                addReturnDateBtn.textContent = '+ Thêm ngày về';
            }
        });
    }

    // Toggle Passenger Dropdown
    if (totalPassengerDisplay && passengerDropdown) {
        totalPassengerDisplay.addEventListener('click', function () {
            passengerDropdown.style.display = passengerDropdown.style.display === 'none' ? 'flex' : 'none';
        });
    }

    // Update Total Passenger Count
    function updateTotalPassengerDisplay() {
        if (!totalPassengerText || !numAdultsInput || !numChildrenInput || !numStudentsInput || !numElderlyInput || !numGroupInput) return;

        const adults = parseInt(numAdultsInput.value) || 0;
        const children = parseInt(numChildrenInput.value) || 0;
        const students = parseInt(numStudentsInput.value) || 0;
        const elderly = parseInt(numElderlyInput.value) || 0;
        const group = parseInt(numGroupInput.value) || 0;

        const total = adults + children + students + elderly + group;

        if (total === 1 && adults === 1 && children === 0 && students === 0 && elderly === 0 && group === 0) {
            totalPassengerText.textContent = '1 Hành khách';
        } else {
            totalPassengerText.textContent = total + ' Hành khách';
        }
    }

    const passengerInputs = [numAdultsInput, numChildrenInput, numStudentsInput, numElderlyInput, numGroupInput];
    passengerInputs.forEach(input => {
        if (input) {
            input.addEventListener('change', updateTotalPassengerDisplay);
            input.addEventListener('input', updateTotalPassengerDisplay); // For immediate update as user types/uses spinners
        }
    });

    // Initial call to set passenger display correctly
    updateTotalPassengerDisplay();

    // Basic Form Validation (Client-side)
    const searchTripForm = document.getElementById('searchTripForm');
    if (searchTripForm) {
        searchTripForm.addEventListener('submit', function(event) {
            let isValid = true;
            let errorMessage = '';

            if (!originStationInput.value.trim()) {
                isValid = false;
                errorMessage += 'Vui lòng chọn nơi xuất phát.\n';
                originStationInput.classList.add('input-error');
            } else {
                originStationInput.classList.remove('input-error');
            }

            if (!destinationStationInput.value.trim()) {
                isValid = false;
                errorMessage += 'Vui lòng chọn nơi đến.\n';
                destinationStationInput.classList.add('input-error');
            } else {
                destinationStationInput.classList.remove('input-error');
            }

            const departureDateInput = document.getElementById('departureDate');
            if (!departureDateInput.value) {
                isValid = false;
                errorMessage += 'Vui lòng chọn ngày đi.\n';
                departureDateInput.classList.add('input-error');
            } else {
                departureDateInput.classList.remove('input-error');
            }
            
            // Ensure at least one adult
            if (numAdultsInput && (parseInt(numAdultsInput.value) || 0) < 1) {
                 const totalPassengers = (parseInt(numAdultsInput.value) || 0) +
                                    (parseInt(numChildrenInput.value) || 0) +
                                    (parseInt(numStudentsInput.value) || 0) +
                                    (parseInt(numElderlyInput.value) || 0) +
                                    (parseInt(numGroupInput.value) || 0);
                if (totalPassengers > 0) { // Only enforce if other passengers are selected
                    isValid = false;
                    errorMessage += 'Phải có ít nhất 1 người lớn đi kèm.\n';
                    numAdultsInput.classList.add('input-error');
                } else if (totalPassengers === 0) { // If no passengers at all
                     isValid = false;
                    errorMessage += 'Vui lòng chọn ít nhất 1 hành khách.\n';
                    numAdultsInput.classList.add('input-error');
                }
            } else if (numAdultsInput) {
                numAdultsInput.classList.remove('input-error');
            }


            if (!isValid) {
                event.preventDefault(); // Stop form submission
                alert('Lỗi:\n' + errorMessage);
            }
        });

        // Remove error class on input
        [originStationInput, destinationStationInput, document.getElementById('departureDate'), numAdultsInput].forEach(input => {
            if (input) {
                input.addEventListener('input', () => input.classList.remove('input-error'));
            }
        });
    }
    // Add a class for input error styling in CSS
    const style = document.createElement('style');
    style.innerHTML = `.input-error { border-color: red !important; }`;
    document.head.appendChild(style);

});
