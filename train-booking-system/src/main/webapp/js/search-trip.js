document.addEventListener("DOMContentLoaded", function () {
    const originInput = document.getElementById("origin");
    const destinationInput = document.getElementById("destination");
    const swapButton = document.getElementById("swapStationsBtn");
    const departureDateInput = document.getElementById("departureDate");
    const returnDateInput = document.getElementById("returnDate");
    const searchForm = document.getElementById("searchTripForm");
    const addReturnDateLink = document.getElementById("addReturnDateLink");
    const returnDateInputWrapper = document.getElementById(
        "returnDateInputWrapper"
    );
    const returnDateField = document.querySelector(".return-date-field");

    // Function to handle label animation based on input value
    const handleInputLabel = (inputElement) => {
        if (!inputElement) return;

        const checkValue = () => {
            if (inputElement.value) {
                inputElement.classList.add("has-value");
            } else {
                inputElement.classList.remove("has-value");
            }
        };
        inputElement.addEventListener("input", checkValue);
        inputElement.addEventListener("change", checkValue); // For date inputs after selection
        inputElement.addEventListener("blur", checkValue); // Ensure class is set on blur
        checkValue(); // Initial check in case of pre-filled values
    };

    handleInputLabel(originInput);
    handleInputLabel(destinationInput);
    handleInputLabel(departureDateInput);
    handleInputLabel(returnDateInput);

    // Swap stations functionality
    if (swapButton && originInput && destinationInput) {
        swapButton.addEventListener("click", function () {
            const tempOriginValue = originInput.value;
            originInput.value = destinationInput.value;
            destinationInput.value = tempOriginValue;

            // Trigger input event to update label state
            originInput.dispatchEvent(new Event("input"));
            destinationInput.dispatchEvent(new Event("input"));
        });
    }

    // Function to set min date for date inputs
    const setMinDate = (inputElement) => {
        const today = new Date().toISOString().split("T")[0];
        if (inputElement) {
            if (inputElement.type === "date") {
                inputElement.setAttribute("min", today);
            }
            inputElement.dataset.minDate = today;
        }
    };

    setMinDate(departureDateInput);
    setMinDate(returnDateInput);

    // Show return date input when link is clicked
    if (addReturnDateLink && returnDateInputWrapper && returnDateField) {
        addReturnDateLink.addEventListener("click", function (event) {
            event.preventDefault();
            addReturnDateLink.style.display = "none";
            returnDateInputWrapper.style.display = "flex";
            returnDateField.classList.add("active");
            returnDateInput.focus();
            // If you want to add an icon dynamically for return date when it appears:
            // const iconImg = document.createElement('img');
            // iconImg.src = "${pageContext.request.contextPath}/assets/icons/calendar_icon.png"; // This needs context path, tricky in pure JS.
            // iconImg.alt = "Calendar Icon";
            // iconImg.classList.add('field-icon');
            // returnDateInputWrapper.insertBefore(iconImg, returnDateInputWrapper.firstChild);
        });
    }

    // Date input placeholder and type switching logic (HTML onfocus/onblur handles this)
    // Add 'has-value' class management for date inputs as well
    [departureDateInput, returnDateInput].forEach((dateInput) => {
        if (dateInput) {
            dateInput.addEventListener("focus", function () {
                this.type = "date";
                if (this.dataset.minDate) {
                    this.setAttribute("min", this.dataset.minDate);
                }
            });
            dateInput.addEventListener("blur", function () {
                if (!this.value) {
                    this.type = "text";
                    this.classList.remove("has-value"); // Ensure label goes back if empty
                } else {
                    this.classList.add("has-value");
                }
            });
            // Initial check for date inputs if they might be pre-filled
            if (dateInput.value) {
                dateInput.classList.add("has-value");
                // If pre-filled and type is text, it won't look like a date.
                // This case is less common for fresh forms but good for robustness.
            }
        }
    });

    // Validate dates on form submission
    if (searchForm) {
        searchForm.addEventListener("submit", function (event) {
            const departureDateValue = departureDateInput.value;
            const returnDateValue = returnDateInput.value;

            if (!departureDateValue) {
                alert("Vui lòng chọn ngày đi.");
                event.preventDefault();
                departureDateInput.focus();
                return;
            }

            const departureDate = new Date(departureDateValue);

            // If return date input is visible and has a value
            if (
                returnDateInputWrapper.style.display !== "none" &&
                returnDateValue
            ) {
                const returnDateObj = new Date(returnDateValue);
                if (departureDate > returnDateObj) {
                    alert("Ngày về phải sau hoặc trùng với ngày đi.");
                    event.preventDefault();
                    returnDateInput.focus();
                    return;
                }
            }
            // Add any other client-side validation as needed
        });
    }

    // Ensure return date is not before departure date dynamically
    if (departureDateInput && returnDateInput) {
        departureDateInput.addEventListener("change", function () {
            const depDate = departureDateInput.value;
            if (depDate) {
                returnDateInput.setAttribute("min", depDate);
                // If return date is already set and now invalid, clear or adjust it
                if (returnDateInput.value && returnDateInput.value < depDate) {
                    returnDateInput.value = depDate; // Or clear: returnDateInput.value = '';
                }
            } else {
                // If departure date is cleared, reset min for return date to today or remove it
                const today = new Date().toISOString().split("T")[0];
                returnDateInput.setAttribute("min", today);
            }
        });
    }
});
