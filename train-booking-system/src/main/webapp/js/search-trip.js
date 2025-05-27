document.addEventListener("DOMContentLoaded", function () {
    // ...existing code...
    const departureDateInput = document.getElementById("departure-date");
    const returnDateInput = document.getElementById("return-date");

    const commonDateConfig = {
        dateFormat: "d/m/Y", // Assuming Vietnamese date format, adjust if needed
        allowInput: true, 
        disableMobile: "true", 
        onChange: function (selectedDates, dateStr, instance) {
            const input = instance.element;
            const label = input.closest('.form-group').querySelector('label'); // More robust label finding
            if (dateStr) {
                input.classList.add("has-value");
                if (label) label.classList.add("has-value");
            } else {
                input.classList.remove("has-value");
                if (label) label.classList.remove("has-value");
            }
        },
    };

    if (departureDateInput) {
        const departureFlatpickr = flatpickr(departureDateInput, {
            ...commonDateConfig,
            minDate: "today",
            onChange: function (selectedDates, dateStr, instance) {
                commonDateConfig.onChange(selectedDates, dateStr, instance); 
                if (returnDateInput && returnDateInput._flatpickr && selectedDates[0]) {
                    returnDateInput._flatpickr.set("minDate", selectedDates[0]);
                }
            },
        });
    }


    const returnDateGroup = document.getElementById("return-date-group");
    let addReturnDatePrompt, returnDateFormGroup, clearReturnDateBtn, returnDateLabel, returnFlatpickr;

    if (returnDateGroup) {
        addReturnDatePrompt = returnDateGroup.querySelector(".add-return-date-prompt");
        returnDateFormGroup = returnDateGroup.querySelector(".return-date-form-group-element");
        clearReturnDateBtn = returnDateGroup.querySelector(".clear-return-date");
        if (returnDateFormGroup) {
            returnDateLabel = returnDateFormGroup.querySelector('label[for="return-date"]');
        }

        if (returnDateInput) {
             returnFlatpickr = flatpickr(returnDateInput, {
                ...commonDateConfig,
                minDate: departureDateInput && departureDateInput.value ? new Date(departureDateInput.value) : "today",
                onOpen: [
                    function (selectedDates, dateStr, instance) {
                        if (openedFromPrompt) {
                            returnDateGroup.classList.add("picker-positioning-active");
                        }
                        setTimeout(() => {
                            instance.reposition();
                        }, 50); 
                    },
                ],
            });
        }
    }


    [departureDateInput, returnDateInput].forEach((input) => {
        if (!input) return; // Skip if element doesn't exist

        const group = input.closest(".input-icon-group");
        // For date inputs, the label is typically a sibling or inside a parent .form-group
        const label = input.closest('.form-group') ? input.closest('.form-group').querySelector('label') : null;


        if (group) {
            if (input === returnDateInput && returnDateGroup) { // Check returnDateGroup exists
                group.addEventListener("click", (e) => {
                    if (
                        e.target !== input &&
                        (!label || e.target !== label) &&
                        (!clearReturnDateBtn || !e.target.closest(".clear-return-date")) &&
                        (!addReturnDatePrompt || !e.target.closest(".add-return-date-prompt")) 
                    ) {
                        if (
                            !returnDateGroup.classList.contains("return-date-active")
                        ) {
                            showReturnDateField(); 
                            returnDateGroup.classList.add("picker-positioning-active");
                            if (returnDateFormGroup) {
                                const _ = returnDateFormGroup.offsetHeight; 
                            }
                            openedFromPrompt = true; 
                            if (input._flatpickr) input._flatpickr.open();
                        } else {
                            openedFromPrompt = false; 
                            if (input._flatpickr) input._flatpickr.open();
                        }
                    }
                });
            } else {
                group.addEventListener("click", (e) => {
                    if (
                        e.target !== input &&
                        (!label || e.target !== label) &&
                        (!clearReturnDateBtn || !e.target.closest(".clear-return-date")) 
                    ) {
                       if (input._flatpickr) input._flatpickr.open();
                    }
                });
            }
        }

        input.addEventListener("focus", function () {
            input.classList.add("has-value");
            if (label) label.classList.add("has-value");
        });

        input.addEventListener("blur", function () {
        // For date fields, this original logic is fine.
        if (!input.value) {
            input.classList.remove("has-value");
            if (label) label.classList.remove("has-value");
        }
    });

    if (input.value) {
            input.classList.add("has-value");
            if (label) label.classList.add("has-value");
        }
    });

    let openedFromPrompt = false;

    function showReturnDateField() {
        if (!returnDateGroup || !returnDateInput) return;
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
        if (!returnDateGroup || !returnDateInput) return;
        returnDateGroup.classList.remove("return-date-active");
        returnDateInput.value = "";
        returnDateInput.classList.remove("has-value");
        if (returnDateLabel) returnDateLabel.classList.remove("has-value");
    }

    if (addReturnDatePrompt && returnDateInput && returnDateInput._flatpickr) {
        addReturnDatePrompt.addEventListener("click", function (event) {
            if (returnDateInput._flatpickr && returnDateInput._flatpickr.isOpen) {
                event.stopPropagation(); 
                return;
            }
            showReturnDateField(); 
            if (returnDateGroup) returnDateGroup.classList.add("picker-positioning-active"); 
            if (returnDateFormGroup) {
                const _ = returnDateFormGroup.offsetHeight;
            }
            openedFromPrompt = true;
            returnDateInput._flatpickr.open();
        });
    }

    if (clearReturnDateBtn && returnDateInput && returnDateInput._flatpickr) {
        clearReturnDateBtn.addEventListener("click", function (event) {
            event.stopPropagation();
            returnDateInput._flatpickr.close();
            showReturnDatePrompt();
        });
    }
    
    if (returnFlatpickr) { // Ensure returnFlatpickr is initialized
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
                if(returnDateGroup) returnDateGroup.classList.remove("picker-positioning-active");
            },
        ].filter((f) => f);

        const existingReturnOnClose = returnFlatpickr.config.onClose || [];
        returnFlatpickr.config.onClose = [
            ...(Array.isArray(existingReturnOnClose)
                ? existingReturnOnClose
                : [existingReturnOnClose].filter((f) => f)),
            function (selectedDates, dateStr, instance) {
                if (openedFromPrompt && !instance.input.value) {
                    showReturnDatePrompt(); 
                }
                openedFromPrompt = false; 
                if(returnDateGroup) returnDateGroup.classList.remove("picker-positioning-active"); 
            },
        ].filter((f) => f);
    }


    if (returnDateInput) { // Check if returnDateInput exists
        if (returnDateInput.value) {
            showReturnDateField();
        } else {
            showReturnDatePrompt();
        }
    }


    // --- Passenger Selector Logic ---
    const passengerSelector = document.querySelector(".passenger-selector");
    if (passengerSelector) {
        const passengerSummary = passengerSelector.querySelector(".passenger-summary");
        const passengerDetails = passengerSelector.querySelector(".passenger-details");
        const passengerTotalText = passengerSelector.querySelector(".passenger-total-text"); 
        const passengerContainer = passengerSelector.querySelector(".passenger-container"); 
        const passengerGroups = passengerDetails.querySelectorAll(".passenger-group");
        const confirmBtn = passengerDetails.querySelector(".confirm-passengers-btn");

        const subQuantityElements = {};
        if (passengerContainer) {
            passengerContainer.querySelectorAll(".sub-container").forEach((sub) => {
                subQuantityElements[sub.dataset.type] = sub.querySelector(".sub-quantity");
            });
        }

        const passengerData = { adult: 0, child: 0, student_hs: 0, student_uni: 0, elderly: 0, vip: 0 };

        function updatePassengerSummary() {
            let totalPassengers = 0;
            for (const type in passengerData) {
                const count = passengerData[type];
                totalPassengers += count;
                if (subQuantityElements[type]) {
                    subQuantityElements[type].textContent = count;
                }
            }
            if(passengerTotalText) passengerTotalText.textContent = `${totalPassengers} Hành khách`;
            
            let adultIsOnlyPassenger = passengerData.adult > 0;
            if (adultIsOnlyPassenger) {
                for (const type in passengerData) {
                    if (type !== "adult" && passengerData[type] > 0) {
                        adultIsOnlyPassenger = false;
                        break;
                    }
                }
            }
            if (confirmBtn) confirmBtn.disabled = totalPassengers === 0;
        }

        passengerGroups.forEach((group) => {
            const type = group.dataset.type;
            const decreaseBtn = group.querySelector(".decrease-btn");
            const increaseBtn = group.querySelector(".increase-btn");
            const quantityDisplay = group.querySelector(".quantity-display"); 
            const hiddenInput = group.querySelector('input[type="hidden"]');
            const min = parseInt(group.dataset.min, 10); 
            const max = parseInt(group.dataset.max, 10) || 10;

            if(quantityDisplay) passengerData[type] = parseInt(quantityDisplay.value, 10);


            function updateTotalPassengersAndButtonStates() {
                let currentTotal = 0;
                for (const t in passengerData) {
                    currentTotal += passengerData[t];
                }

                passengerGroups.forEach((g) => {
                    const t = g.dataset.type;
                    const currentVal = passengerData[t];
                    const minVal = parseInt(g.dataset.min, 10);
                    const maxVal = parseInt(g.dataset.max, 10);
                    const decBtn = g.querySelector(".decrease-btn");
                    const incBtn = g.querySelector(".increase-btn");

                    if (decBtn) decBtn.disabled = currentVal === minVal;
                    if (incBtn) incBtn.disabled = currentVal === maxVal || currentTotal >= 10; 
                });
                
                const adultDecreaseBtn = passengerDetails.querySelector('.passenger-group[data-type="adult"] .decrease-btn');
                if (adultDecreaseBtn) {
                    let nonAdultPassengers = 0;
                    for (const t in passengerData) {
                        if (t !== "adult") nonAdultPassengers += passengerData[t];
                    }
                    if (passengerData.adult === 1 && nonAdultPassengers === 0) {
                        adultDecreaseBtn.disabled = true;
                    } else if (passengerData.adult === 0) {
                        adultDecreaseBtn.disabled = true;
                    }
                }
            }
            if(decreaseBtn){
                decreaseBtn.addEventListener("click", () => {
                    let currentValue = passengerData[type];
                    let canDecrease = true;
                    if (type === "adult" && currentValue === 1) {
                        let otherPassengersCount = 0;
                        for (const t in passengerData) {
                            if (t !== "adult") otherPassengersCount += passengerData[t];
                        }
                        if (otherPassengersCount === 0) canDecrease = false;
                    }
                    if (currentValue > min && canDecrease) {
                        currentValue--;
                        if(quantityDisplay) quantityDisplay.value = currentValue;
                        if (hiddenInput) hiddenInput.value = currentValue;
                        passengerData[type] = currentValue;
                        updatePassengerSummary();
                        updateTotalPassengersAndButtonStates();
                    }
                });
            }
            if(increaseBtn){
                increaseBtn.addEventListener("click", () => {
                    let currentValue = passengerData[type];
                    let totalCurrentPassengers = 0;
                    for (const t in passengerData) totalCurrentPassengers += passengerData[t];
                    if (currentValue < max && totalCurrentPassengers < 10) {
                        currentValue++;
                        if(quantityDisplay) quantityDisplay.value = currentValue;
                        if (hiddenInput) hiddenInput.value = currentValue;
                        passengerData[type] = currentValue;
                        updatePassengerSummary();
                        updateTotalPassengersAndButtonStates();
                    }
                });
            }
        });

        function initializeButtonStates() {
            let currentTotal = 0;
            for (const t in passengerData) currentTotal += passengerData[t];
            passengerGroups.forEach((g) => {
                const type = g.dataset.type;
                const currentVal = passengerData[type];
                const minVal = parseInt(g.dataset.min, 10);
                const maxVal = parseInt(g.dataset.max, 10);
                const decBtn = g.querySelector(".decrease-btn");
                const incBtn = g.querySelector(".increase-btn");
                if (decBtn) decBtn.disabled = currentVal === minVal;
                if (incBtn) incBtn.disabled = currentVal === maxVal || currentTotal >= 10;
            });
            const adultDecreaseBtn = passengerDetails.querySelector('.passenger-group[data-type="adult"] .decrease-btn');
            if (adultDecreaseBtn) {
                let nonAdultPassengers = 0;
                for (const t in passengerData) {
                    if (t !== "adult") nonAdultPassengers += passengerData[t];
                }
                if (passengerData.adult === 1 && nonAdultPassengers === 0) {
                    adultDecreaseBtn.disabled = true;
                } else if (passengerData.adult === 0) {
                    adultDecreaseBtn.disabled = true;
                }
            }
        }

        if (passengerSummary) {
            passengerSummary.addEventListener("click", (e) => {
                if (passengerDetails.classList.contains("expanded") && (e.target.closest("button") || e.target.closest(".input-block"))) {
                    return; 
                }
                passengerDetails.classList.toggle("expanded");
                if (passengerDetails.classList.contains("expanded")) {
                    initializeButtonStates(); 
                }
            });
        }

        if (confirmBtn) {
            confirmBtn.addEventListener("click", () => {
                passengerDetails.classList.remove("expanded");
                updatePassengerSummary();
            });
        }

        document.addEventListener("click", function (event) {
            if (!passengerSelector.contains(event.target) && passengerDetails.classList.contains("expanded")) {
                passengerDetails.classList.remove("expanded");
            }
        });

        updatePassengerSummary(); 
        initializeButtonStates(); 
    }

    let resizeTimeout;
    window.addEventListener("resize", function () {
        clearTimeout(resizeTimeout);
        resizeTimeout = setTimeout(function () {
            if (departureDateInput && departureDateInput._flatpickr && departureDateInput._flatpickr.isOpen) {
                departureDateInput._flatpickr.reposition();
            }
            if (returnDateInput && returnDateInput._flatpickr && returnDateInput._flatpickr.isOpen) {
                if (openedFromPrompt && returnDateGroup && returnDateGroup.classList.contains("picker-positioning-active")) {
                    // No specific action needed here beyond what flatpickr does
                }
                returnDateInput._flatpickr.reposition();
            }
        }, 100); 
    });

});
