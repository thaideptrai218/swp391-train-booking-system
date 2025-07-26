document.addEventListener("DOMContentLoaded", function () {
    // --- Passenger Selector Logic ---
    const passengerSelector = document.querySelector(".passenger-selector");
    if (passengerSelector) {
        const passengerSummary =
            passengerSelector.querySelector(".passenger-summary");
        const passengerDetails =
            passengerSelector.querySelector(".passenger-details");
        const passengerTotalText = passengerSelector.querySelector(
            ".passenger-total-text"
        );
        const passengerTotalNumber = passengerSelector.querySelector(
            ".passenger-total-number"
        );
        const passengerContainer = passengerSelector.querySelector(
            ".passenger-container"
        );
        const passengerGroups =
            passengerDetails.querySelectorAll(".passenger-group");
        const confirmBtn = passengerDetails.querySelector(
            ".confirm-passengers-btn"
        );

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
            adult: 0,
            child: 0,
            student_hs: 0,
            student_uni: 0,
            elderly: 0,
            vip: 0,
        };

        function updatePassengerSummary() {
            let totalPassengers = 0;
            for (const type in passengerData) {
                const count = passengerData[type];
                totalPassengers += count;
                if (subQuantityElements[type]) {
                    subQuantityElements[type].textContent = count;
                }
            }
            if (passengerTotalNumber)
                passengerTotalNumber.value = `${totalPassengers}`;

            if (passengerTotalText)
                passengerTotalText.textContent = `${totalPassengers} Hành khách`;

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

            if (quantityDisplay)
                passengerData[type] = parseInt(quantityDisplay.value, 10);

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
                    if (incBtn)
                        incBtn.disabled =
                            currentVal === maxVal || currentTotal >= 10;
                });

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
                        adultDecreaseBtn.disabled = true;
                    }
                }
            }
            if (decreaseBtn) {
                decreaseBtn.addEventListener("click", () => {
                    let currentValue = passengerData[type];
                    let canDecrease = true;
                    if (type === "adult" && currentValue === 1) {
                        let otherPassengersCount = 0;
                        for (const t in passengerData) {
                            if (t !== "adult")
                                otherPassengersCount += passengerData[t];
                        }
                        if (otherPassengersCount === 0) canDecrease = false;
                    }
                    if (currentValue > min && canDecrease) {
                        currentValue--;
                        if (quantityDisplay)
                            quantityDisplay.value = currentValue;
                        if (hiddenInput) hiddenInput.value = currentValue;
                        passengerData[type] = currentValue;
                        updatePassengerSummary();
                        updateTotalPassengersAndButtonStates();
                    }
                });
            }
            if (increaseBtn) {
                increaseBtn.addEventListener("click", () => {
                    let currentValue = passengerData[type];
                    let totalCurrentPassengers = 0;
                    for (const t in passengerData)
                        totalCurrentPassengers += passengerData[t];
                    if (currentValue < max && totalCurrentPassengers < 10) {
                        currentValue++;
                        if (quantityDisplay)
                            quantityDisplay.value = currentValue;
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
                if (incBtn)
                    incBtn.disabled =
                        currentVal === maxVal || currentTotal >= 10;
            });
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
                    adultDecreaseBtn.disabled = true;
                }
            }
        }

        if (passengerSummary) {
            passengerSummary.addEventListener("click", (e) => {
                if (
                    passengerDetails.classList.contains("expanded") &&
                    (e.target.closest("button") ||
                        e.target.closest(".input-block"))
                ) {
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
            if (
                passengerSelector &&
                !passengerSelector.contains(event.target) &&
                passengerDetails &&
                passengerDetails.classList.contains("expanded")
            ) {
                passengerDetails.classList.remove("expanded");
            }
        });

        updatePassengerSummary();
        initializeButtonStates();
    }
});
