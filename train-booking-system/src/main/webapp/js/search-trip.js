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
                // If opened from prompt, ensure the positioning class is active
                // so that the returnDateFormGroup takes up space for correct calculation.
                if (openedFromPrompt) {
                    returnDateGroup.classList.add("picker-positioning-active");
                }
                setTimeout(() => {
                    instance.reposition();
                    // picker-positioning-active is cleaned up by onChange and onClose
                }, 50); // 50ms delay
            },
        ],
        // appendTo: returnDateGroup, // Removed: Reverting to default body append (Restoring this comment)
    });

    // Make the whole group clickable to open the date picker
    [departureDateInput, returnDateInput].forEach((input) => {
        const group = input.closest(".input-icon-group");
        const label = input.nextElementSibling;

        if (group) {
            if (input === returnDateInput) {
                // Specific logic for the return date group
                group.addEventListener("click", (e) => {
                    if (
                        e.target !== input &&
                        e.target !== label &&
                        !e.target.closest(".clear-return-date") &&
                        !e.target.closest(".add-return-date-prompt") // Don't act if prompt itself is clicked
                    ) {
                        // If the return date field is not yet active (prompt is showing)
                        if (
                            !returnDateGroup.classList.contains(
                                "return-date-active"
                            )
                        ) {
                            // Mimic the prompt click's setup behavior
                            showReturnDateField(); // Make structure visible
                            returnDateGroup.classList.add(
                                "picker-positioning-active"
                            );
                            if (returnDateFormGroup) {
                                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                                const _ = returnDateFormGroup.offsetHeight; // Force reflow
                            }
                            openedFromPrompt = true; // Treat as if opened from prompt
                            input._flatpickr.open();
                        } else {
                            // If field is already active (showing date or empty input), just open normally
                            openedFromPrompt = false; // Not from prompt in this specific path
                            input._flatpickr.open();
                        }
                    }
                });
            } else {
                // Generic logic for other date inputs (e.g., departure)
                group.addEventListener("click", (e) => {
                    if (
                        e.target !== input &&
                        e.target !== label &&
                        !e.target.closest(".clear-return-date") // Should not apply to departure, but good for generic
                    ) {
                        input._flatpickr.open();
                    }
                });
            }
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
        addReturnDatePrompt.addEventListener("click", function (event) {
            // Added event parameter
            // If the calendar is already open, do nothing on subsequent clicks of the prompt
            if (
                returnDateInput._flatpickr &&
                returnDateInput._flatpickr.isOpen
            ) {
                event.stopPropagation(); // Stop event from bubbling to parent group listener
                return;
            }

            // Ensure the return date field's structure is active and takes up layout space
            showReturnDateField(); // This adds 'return-date-active'
            returnDateGroup.classList.add("picker-positioning-active"); // This makes it opacity:0 if needed

            // Force a browser reflow to ensure styles are applied before Flatpickr calculates position
            if (returnDateFormGroup) {
                // Reading offsetHeight is a common trick to trigger reflow
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                const _ = returnDateFormGroup.offsetHeight;
            }

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
                showReturnDateField(); // This makes the form group and input visible
                openedFromPrompt = false; // Reset flag
            } else {
                // If date string is cleared but input still has a value (e.g. manual invalid entry),
                // don't hide prompt yet. If input is truly empty, then hide.
                if (!instance.input.value) {
                    showReturnDatePrompt();
                }
            }
            // Always remove picker-positioning-active after a change or if a date is selected
            returnDateGroup.classList.remove("picker-positioning-active");
        },
    ].filter((f) => f);

    // Enhance returnFlatpickr configuration for onClose
    const existingReturnOnClose = returnFlatpickr.config.onClose || [];
    returnFlatpickr.config.onClose = [
        ...(Array.isArray(existingReturnOnClose)
            ? existingReturnOnClose
            : [existingReturnOnClose].filter((f) => f)),
        function (selectedDates, dateStr, instance) {
            // If opened from prompt and no date was selected (input is still empty)
            if (openedFromPrompt && !instance.input.value) {
                showReturnDatePrompt(); // Revert to showing the prompt
            }
            openedFromPrompt = false; // Reset flag
            returnDateGroup.classList.remove("picker-positioning-active"); // Always clean up
        },
    ].filter((f) => f);

    // Initial state check for return date
    if (returnDateInput.value) {
        showReturnDateField();
    } else {
        showReturnDatePrompt();
    }

    // --- Passenger Selector Logic ---
    const passengerSelector = document.querySelector(".passenger-selector");
    if (passengerSelector) {
        const passengerSummary =
            passengerSelector.querySelector(".passenger-summary");
        const passengerDetails =
            passengerSelector.querySelector(".passenger-details");
        const passengerTotalText = passengerSelector.querySelector(
            ".passenger-total-text"
        ); // Updated selector
        const passengerContainer = passengerSelector.querySelector(
            ".passenger-container"
        ); // New selector for sub-containers
        const passengerGroups =
            passengerDetails.querySelectorAll(".passenger-group");
        const confirmBtn = passengerDetails.querySelector(
            ".confirm-passengers-btn"
        );

        // Map data-type from .passenger-group to the .sub-container in the summary
        const subQuantityElements = {};
        if (passengerContainer) {
            passengerContainer
                .querySelectorAll(".sub-container")
                .forEach((sub) => {
                    subQuantityElements[sub.dataset.type] =
                        sub.querySelector(".sub-quantity");
                });
        }

        const passengerData = {
            // To store current quantities
            adult: 0, // Will be initialized from HTML
            child: 0,
            student_hs: 0,
            student_uni: 0,
            elderly: 0,
            vip: 0,
        };

        // No longer need passengerTypeNames for breakdown text

        function updatePassengerSummary() {
            let totalPassengers = 0;
            for (const type in passengerData) {
                const count = passengerData[type];
                totalPassengers += count;
                // Update the .sub-quantity in the summary view
                if (subQuantityElements[type]) {
                    subQuantityElements[type].textContent = count;
                }
            }

            passengerTotalText.textContent = `${totalPassengers} Hành khách`;

            // Disable confirm button if total passengers is 0 (unless adult is 1 and it's the only one)
            // More robust: ensure at least one adult if adult is the only type with count > 0
            let adultIsOnlyPassenger = passengerData.adult > 0;
            if (adultIsOnlyPassenger) {
                for (const type in passengerData) {
                    if (type !== "adult" && passengerData[type] > 0) {
                        adultIsOnlyPassenger = false;
                        break;
                    }
                }
            }

            if (confirmBtn) {
                // Simplest rule: at least 1 passenger.
                confirmBtn.disabled = totalPassengers === 0;
            }
        }

        passengerGroups.forEach((group) => {
            const type = group.dataset.type;
            const decreaseBtn = group.querySelector(".decrease-btn");
            const increaseBtn = group.querySelector(".increase-btn");
            const quantityDisplay = group.querySelector(".quantity-display"); // In detailed view
            const hiddenInput = group.querySelector('input[type="hidden"]');
            const min = parseInt(group.dataset.min, 10); // min is now 0 for adult too
            const max = parseInt(group.dataset.max, 10) || 10;

            // Initialize passengerData from the detailed view's inputs
            passengerData[type] = parseInt(quantityDisplay.value, 10);

            function updateTotalPassengersAndButtonStates() {
                let currentTotal = 0;
                for (const t in passengerData) {
                    currentTotal += passengerData[t];
                }

                // Update buttons for all groups based on overall total
                passengerGroups.forEach((g) => {
                    const t = g.dataset.type;
                    const currentVal = passengerData[t];
                    const minVal = parseInt(g.dataset.min, 10);
                    const maxVal = parseInt(g.dataset.max, 10);
                    const decBtn = g.querySelector(".decrease-btn");
                    const incBtn = g.querySelector(".increase-btn");

                    if (decBtn) decBtn.disabled = currentVal === minVal;
                    if (incBtn)
                        incBtn.disabled =
                            currentVal === maxVal || currentTotal >= 10; // Overall max 10
                });
                // Special rule for adult decrease: cannot go below 1 if it's the only passenger
                const adultDecreaseBtn = passengerDetails.querySelector(
                    '.passenger-group[data-type="adult"] .decrease-btn'
                );
                if (adultDecreaseBtn) {
                    let nonAdultPassengers = 0;
                    for (const t in passengerData) {
                        if (t !== "adult")
                            nonAdultPassengers += passengerData[t];
                    }
                    if (passengerData.adult === 1 && nonAdultPassengers === 0) {
                        adultDecreaseBtn.disabled = true;
                    } else if (passengerData.adult === 0) {
                        // General min rule for adult
                        adultDecreaseBtn.disabled = true;
                    }
                }
            }

            decreaseBtn.addEventListener("click", () => {
                let currentValue = passengerData[type];
                let canDecrease = true;

                // Prevent adult from going below 1 if it's the only passenger type selected
                if (type === "adult" && currentValue === 1) {
                    let otherPassengersCount = 0;
                    for (const t in passengerData) {
                        if (t !== "adult")
                            otherPassengersCount += passengerData[t];
                    }
                    if (otherPassengersCount === 0) {
                        canDecrease = false;
                    }
                }

                if (currentValue > min && canDecrease) {
                    currentValue--;
                    quantityDisplay.value = currentValue;
                    if (hiddenInput) hiddenInput.value = currentValue;
                    passengerData[type] = currentValue;
                    updatePassengerSummary();
                    updateTotalPassengersAndButtonStates();
                }
            });

            increaseBtn.addEventListener("click", () => {
                let currentValue = passengerData[type];
                let totalCurrentPassengers = 0;
                for (const t in passengerData) {
                    totalCurrentPassengers += passengerData[t];
                }

                if (currentValue < max && totalCurrentPassengers < 10) {
                    // Overall max of 10
                    currentValue++;
                    quantityDisplay.value = currentValue;
                    if (hiddenInput) hiddenInput.value = currentValue;
                    passengerData[type] = currentValue;
                    updatePassengerSummary();
                    updateTotalPassengersAndButtonStates();
                }
            });
            // Initial button states after all passengerData is initialized
        });

        // Call after loop to set initial states correctly based on all passengerData
        function initializeButtonStates() {
            let currentTotal = 0;
            for (const t in passengerData) {
                currentTotal += passengerData[t];
            }

            passengerGroups.forEach((g) => {
                const type = g.dataset.type;
                const currentVal = passengerData[type];
                const minVal = parseInt(g.dataset.min, 10);
                const maxVal = parseInt(g.dataset.max, 10);
                const decBtn = g.querySelector(".decrease-btn");
                const incBtn = g.querySelector(".increase-btn");

                if (decBtn) decBtn.disabled = currentVal === minVal;
                if (incBtn)
                    incBtn.disabled =
                        currentVal === maxVal || currentTotal >= 10;
            });

            // Special rule for adult decrease button initialization
            const adultDecreaseBtn = passengerDetails.querySelector(
                '.passenger-group[data-type="adult"] .decrease-btn'
            );
            if (adultDecreaseBtn) {
                let nonAdultPassengers = 0;
                for (const t in passengerData) {
                    if (t !== "adult") nonAdultPassengers += passengerData[t];
                }
                if (passengerData.adult === 1 && nonAdultPassengers === 0) {
                    adultDecreaseBtn.disabled = true;
                } else if (passengerData.adult === 0) {
                    // General min rule for adult
                    adultDecreaseBtn.disabled = true;
                }
            }
        }

        if (passengerSummary) {
            passengerSummary.addEventListener("click", (e) => {
                if (
                    passengerDetails.classList.contains("expanded") && // Check if it's expanded
                    (e.target.closest("button") || // Clicked on any button
                        e.target.closest(".input-block")) // Clicked within an input-block
                ) {
                    return; // Don't close if interacting with controls inside details
                }
                // const isOpen = passengerDetails.style.display === "grid";
                // passengerDetails.style.display = isOpen ? "none" : "grid";
                passengerDetails.classList.toggle("expanded");
                // passengerSelector.classList.toggle("open", !isOpen); // Removed dropdown arrow related class toggle
                if (passengerDetails.classList.contains("expanded")) {
                    // When opening
                    initializeButtonStates(); // Recalculate button states
                }
            });
        }

        if (confirmBtn) {
            confirmBtn.addEventListener("click", () => {
                // passengerDetails.style.display = "none";
                passengerDetails.classList.remove("expanded");
                // passengerSelector.classList.remove("open"); // Removed dropdown arrow related class toggle
                updatePassengerSummary();
            });
        }

        document.addEventListener("click", function (event) {
            if (
                !passengerSelector.contains(event.target) &&
                // passengerDetails.style.display === "grid" // Check if details are open directly
                passengerDetails.classList.contains("expanded")
            ) {
                // passengerDetails.style.display = "none";
                passengerDetails.classList.remove("expanded");
                // passengerSelector.classList.remove("open"); // Removed dropdown arrow related class toggle
            }
        });

        updatePassengerSummary(); // Initial summary update on page load
        initializeButtonStates(); // Initial button states on page load
    }

    // Window resize listener to reposition active Flatpickr instances
    let resizeTimeout;
    window.addEventListener("resize", function () {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(function () {
            if (departureFlatpickr && departureFlatpickr.isOpen) {
                departureFlatpickr.reposition();
            }
            if (returnFlatpickr && returnFlatpickr.isOpen) {
                // Ensure correct state for repositioning if opened from prompt
                if (
                    openedFromPrompt &&
                    returnDateGroup.classList.contains(
                        "picker-positioning-active"
                    )
                ) {
                    // The class should already be set by the open logic, this is mostly for clarity
                }
                returnFlatpickr.reposition();
            }
        }, 100); // Debounce resize event
    });
});
