document.addEventListener("DOMContentLoaded", function () {
    // ...existing code...
    const departureDateInput = document.getElementById("departure-date");
    const returnDateInput = document.getElementById("return-date");

    const commonDateConfig = {
        dateFormat: "d/m/Y",
        allowInput: true, // Allows manual input, which can be styled
        disableMobile: "true", // Uses Flatpickr on mobile too
        // Removed onOpen custom theme class, using material_blue now
        onChange: function (selectedDates, dateStr, instance) {
            const input = instance.element;
            const label = input.nextElementSibling; // Label is now the next sibling
            if (dateStr) {
                input.classList.add("has-value");
                if (label) label.classList.add("has-value");
            } else {
                input.classList.remove("has-value");
                if (label) label.classList.remove("has-value");
            }
        },
    };

    const departureFlatpickr = flatpickr(departureDateInput, {
        ...commonDateConfig,
        minDate: "today",
        onChange: function (selectedDates, dateStr, instance) {
            commonDateConfig.onChange(selectedDates, dateStr, instance); // Call common handler
            if (returnDateInput._flatpickr && selectedDates[0]) {
                returnDateInput._flatpickr.set("minDate", selectedDates[0]);
            }
        },
    });

    // Define DOM elements related to the return date functionality upfront
    const returnDateGroup = document.getElementById("return-date-group");
    const addReturnDatePrompt = returnDateGroup.querySelector(
        ".add-return-date-prompt"
    );
    // Note: .form-group inside #return-date-group is specific, ensure this selector is robust
    // In index.html, it's <div class="form-group return-date-form-group-element" ...>
    const returnDateFormGroup = returnDateGroup.querySelector(
        ".return-date-form-group-element"
    );
    const clearReturnDateBtn =
        returnDateGroup.querySelector(".clear-return-date");
    // The label is inside returnDateFormGroup
    const returnDateLabel = returnDateFormGroup
        ? returnDateFormGroup.querySelector('label[for="return-date"]')
        : null;

    const returnFlatpickr = flatpickr(returnDateInput, {
        ...commonDateConfig,
        minDate: departureDateInput.value
            ? new Date(departureDateInput.value)
            : "today",
        onOpen: [
            function (selectedDates, dateStr, instance) {
                // Reposition the calendar just before it's shown,
                // after picker-positioning-active styles have hopefully been applied.
                instance.reposition();
            },
        ],
        // appendTo: returnDateGroup, // Removed: Reverting to default body append
    });

    // Make the whole group clickable to open the date picker
    // This loop applies to both departure and return date inputs
    [departureDateInput, returnDateInput].forEach((input) => {
        const group = input.closest(".input-icon-group");
        const label = input.nextElementSibling;

        if (group) {
            group.addEventListener("click", (e) => {
                // Prevent opening if the click is on the input itself or its label,
                // or if it's the return date's clear button.
                if (
                    e.target !== input &&
                    e.target !== label &&
                    !e.target.closest(".clear-return-date")
                ) {
                    input._flatpickr.open();
                }
            });
        }

        // Handle focus and blur for label animation
        input.addEventListener("focus", function () {
            input.classList.add("has-value");
            if (label) label.classList.add("has-value");
        });

        input.addEventListener("blur", function () {
            if (!input.value) {
                input.classList.remove("has-value");
                if (label) label.classList.remove("has-value");
            }
        });

        // Initial check in case the date is pre-filled
        if (input.value) {
            input.classList.add("has-value");
            if (label) label.classList.add("has-value");
        }
    });

    // State variable to track if the return date picker was opened from the prompt
    let openedFromPrompt = false;

    function showReturnDateField() {
        returnDateGroup.classList.add("return-date-active");
        if (returnDateInput.value) {
            returnDateInput.classList.add("has-value");
            if (returnDateLabel) returnDateLabel.classList.add("has-value");
        } else {
            returnDateInput.classList.remove("has-value");
            if (returnDateLabel) returnDateLabel.classList.remove("has-value");
        }
    }

    function showReturnDatePrompt() {
        returnDateGroup.classList.remove("return-date-active");
        returnDateInput.value = "";
        returnDateInput.classList.remove("has-value");
        if (returnDateLabel) returnDateLabel.classList.remove("has-value");
    }

    if (addReturnDatePrompt) {
        addReturnDatePrompt.addEventListener("click", function () {
            returnDateGroup.classList.add("picker-positioning-active"); // Add class for positioning
            openedFromPrompt = true;
            returnDateInput._flatpickr.open();
        });
    }

    if (clearReturnDateBtn) {
        clearReturnDateBtn.addEventListener("click", function (event) {
            event.stopPropagation();
            returnDateInput._flatpickr.close();
            showReturnDatePrompt();
        });
    }

    // Enhance returnFlatpickr configuration for onChange
    const existingReturnOnChange = returnFlatpickr.config.onChange;
    returnFlatpickr.config.onChange = [
        ...(Array.isArray(existingReturnOnChange)
            ? existingReturnOnChange
            : [existingReturnOnChange].filter((f) => f)),
        function (selectedDates, dateStr, instance) {
            if (dateStr) {
                showReturnDateField();
                openedFromPrompt = false;
            } else {
                if (!instance.input.value) {
                    showReturnDatePrompt();
                }
            }
            returnDateGroup.classList.remove("picker-positioning-active"); // Clean up positioning class
        },
    ].filter((f) => f);

    // Enhance returnFlatpickr configuration for onClose
    const existingReturnOnClose = returnFlatpickr.config.onClose || [];
    returnFlatpickr.config.onClose = [
        ...(Array.isArray(existingReturnOnClose)
            ? existingReturnOnClose
            : [existingReturnOnClose].filter((f) => f)),
        function (selectedDates, dateStr, instance) {
            if (
                openedFromPrompt &&
                selectedDates.length === 0 &&
                !instance.input.value
            ) {
                showReturnDatePrompt();
            }
            openedFromPrompt = false;
            returnDateGroup.classList.remove("picker-positioning-active"); // Clean up positioning class
        },
    ].filter((f) => f);

    // Initial state check for return date
    if (returnDateInput.value) {
        showReturnDateField();
    } else {
        showReturnDatePrompt();
    }
    // ...existing code...
});
