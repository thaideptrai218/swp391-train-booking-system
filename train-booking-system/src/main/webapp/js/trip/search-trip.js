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
