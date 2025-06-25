document.addEventListener("DOMContentLoaded", function () {
    const SESSION_STORAGE_CART_KEY = "VNR_userShoppingCart";
    const MAX_PASSENGERS = 10;
    const PAYMENT_TIMEOUT_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    // DOM Elements
    const passengerDetailsBody = document.getElementById(
        "passenger-details-body"
    );
    const passengerRowTemplate = document.getElementById(
        "passenger-row-template"
    );
    const noPassengersRow = document.getElementById("no-passengers-row");
    const passengerCountSpan = document.getElementById("passenger-count");
    const totalPaymentAmountSpan = document.getElementById(
        "total-payment-amount"
    );
    const paymentCountdownSpan = document.getElementById("payment-countdown");
    const paymentForm = document.getElementById("paymentForm");
    const generalErrorMessage = document.getElementById(
        "general-error-message"
    );
    const clearAllSeatsBtn = document.getElementById("clearAllSeatsButton"); // Added

    // Customer Info Fields
    const customerFullNameInput = document.getElementById("customerFullName");
    const customerEmailInput = document.getElementById("customerEmail");
    const customerEmailConfirmInput = document.getElementById(
        "customerEmailConfirm"
    );
    const customerPhoneInput = document.getElementById("customerPhone");
    const customerIDCardInput = document.getElementById("customerIDCard");
    const agreeTermsCheckbox = document.getElementById("agreeTerms");
    const submitPaymentButton = document.getElementById("submitPaymentButton");

    let shoppingCart = [];
    let paymentTimerInterval;

    // --- INITIALIZATION ---
    function initializePage() {
        loadShoppingCart();
        checkCartAndRedirectIfEmpty(); // Check if cart is empty on load
        populatePassengerTypes();
        renderPassengerRows();
        updateTotalPaymentAndPassengerCount();
        startPaymentTimer();
        setupEventListeners();
        checkCartAndRedirectIfEmpty(); // Check on initial load as well
    }

    // --- UTILITY FUNCTIONS ---
    function getAge(dateString) {
        if (!dateString) return -1;
        const birthDate = new Date(dateString);
        if (isNaN(birthDate.getTime())) return -1;
        const today = new Date();
        let age = today.getFullYear() - birthDate.getFullYear();
        const monthDifference = today.getMonth() - birthDate.getMonth();
        if (
            monthDifference < 0 ||
            (monthDifference === 0 && today.getDate() < birthDate.getDate())
        ) {
            age--;
        }
        return age;
    }

    function formatCurrency(value) {
        if (isNaN(parseFloat(value))) return "0";
        return parseFloat(value).toLocaleString("vi-VN", {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0,
        });
    }

    function parseCurrency(value) {
        if (typeof value !== "string") return 0;
        return parseFloat(value.replace(/\./g, "").replace(/,/g, ".")) || 0;
    }

    function displayError(element, message) {
        if (element) {
            element.textContent = message;
            element.style.display = "block";
        }
    }

    function clearError(element) {
        if (element) {
            element.textContent = "";
            element.style.display = "none";
        }
    }

    function clearAllErrors() {
        document
            .querySelectorAll(".error-message")
            .forEach((span) => clearError(span));
    }

    function displayGeneralError(message, isError = true) {
        if (generalErrorMessage) {
            generalErrorMessage.textContent = message;
            generalErrorMessage.style.color = isError ? "red" : "green";
            generalErrorMessage.style.display = message ? "block" : "none";
        }
    }

    // --- API FUNCTIONS ---
    async function releaseSingleSeatAPI(cartItem) {
        if (!cartItem || !cartItem.tripId || !cartItem.seatID) {
            console.warn(
                "Cannot release seat, missing critical cart item data:",
                cartItem
            );
            return true;
        }
        const payload = {
            tripId: cartItem.tripId,
            seatId: cartItem.seatID,
            legOriginStationId: cartItem.legOriginStationId,
            legDestinationStationId: cartItem.legDestinationStationId,
        };
        try {
            const response = await fetch(`${contextPath}/api/seats/release`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });
            if (response.ok) {
                console.log("Seat released on backend:", payload);
                return true;
            } else {
                const errorData = await response.json().catch(() => ({}));
                console.error(
                    "Failed to release seat on backend. Status:",
                    response.status,
                    "Message:",
                    errorData.message
                );
                return false;
            }
        } catch (error) {
            console.error("Error calling releaseSingleSeatAPI:", error);
            return false;
        }
    }

    function loadShoppingCart() {
        const storedCart = sessionStorage.getItem(SESSION_STORAGE_CART_KEY);
        if (storedCart) {
            try {
                shoppingCart = JSON.parse(storedCart);
            } catch (e) {
                console.error("Error parsing shopping cart:", e);
                shoppingCart = [];
                displayGeneralError("Lỗi khi tải giỏ hàng.");
            }
        }
    }

    function validateCartOnLoad() {
        if (shoppingCart.length === 0) {
            if (noPassengersRow) noPassengersRow.style.display = "table-row";
            // displayGeneralError("Không có vé nào trong giỏ hàng. Vui lòng quay lại để chọn vé."); // Message handled by checkCartAndRedirectIfEmpty
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true; // Disable clear all if cart is empty
            return false;
        }
        if (shoppingCart.length > MAX_PASSENGERS) {
            displayGeneralError(
                `Tối đa ${MAX_PASSENGERS} vé. Hiện có ${shoppingCart.length}.`
            );
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true;
            return false;
        }
        if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = false;
        return true;
    }

    function populatePassengerTypes() {
        if (
            !window.passengerTypesData ||
            window.passengerTypesData.length === 0
        ) {
            console.warn("Passenger types data missing.");
        }
    }

    // --- UI RENDERING AND UPDATES ---
    function renderPassengerRows() {
        if (!passengerDetailsBody || !passengerRowTemplate) return;
        passengerDetailsBody.innerHTML = "";

        if (shoppingCart.length === 0) {
            if (noPassengersRow) noPassengersRow.style.display = "table-row";
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true;
            return;
        }
        if (noPassengersRow) noPassengersRow.style.display = "none";
        if (submitPaymentButton) submitPaymentButton.disabled = false;
        if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = false;

        shoppingCart.forEach((cartItem, index) => {
            const newRow = passengerRowTemplate.content
                .cloneNode(true)
                .querySelector("tr");
            newRow.dataset.cartItemIndex = index;

            const inputsAndSelects = newRow.querySelectorAll("input, select");
            inputsAndSelects.forEach((el) => {
                const originalId = el.id;
                if (originalId && originalId.includes("-${index}")) {
                    el.id = originalId.replace("-${index}", `-${index}`);
                }
                const errorSpan = newRow.querySelector(
                    `.${el.name}Error, .${el.className.split(" ")[0]}Error`
                );
                if (errorSpan) {
                    const originalErrorId = errorSpan.id;
                    if (
                        originalErrorId &&
                        originalErrorId.includes("-${index}")
                    ) {
                        errorSpan.id = originalErrorId.replace(
                            "-${index}",
                            `-${index}`
                        );
                    }
                    if (el.hasAttribute("aria-describedby")) {
                        el.setAttribute("aria-describedby", errorSpan.id);
                    }
                }
                const label = newRow.querySelector(
                    `label[for="${originalId}"]`
                );
                if (label) {
                    label.setAttribute("for", el.id);
                }
            });

            const passengerTypeSelect = newRow.querySelector(
                ".passenger-type-selector"
            );
            if (window.passengerTypesData && passengerTypeSelect) {
                window.passengerTypesData.forEach((ptype) => {
                    const option = document.createElement("option");
                    option.value = ptype.passengerTypeID;
                    option.textContent = ptype.typeName;
                    option.dataset.discountPercentage =
                        ptype.discountPercentage;
                    option.dataset.requiresDocument = String(
                        ptype.requiresDocument
                    );
                    option.dataset.isChild = String(
                        ptype.typeName === "Trẻ em" ||
                            ptype.typeName === "Em bé"
                    );
                    passengerTypeSelect.appendChild(option);
                });
            }

            const seatInfoCell = newRow.querySelector(".seat-info-cell");
            if (seatInfoCell) {
                seatInfoCell.querySelector(
                    ".seat-train-route"
                ).textContent = `${cartItem.trainName || "N/A"}: ${
                    cartItem.originStationName || "N/A"
                } - ${cartItem.destinationStationName || "N/A"}`;
                seatInfoCell.querySelector(
                    ".seat-departure-datetime"
                ).textContent = `Khởi hành: ${
                    cartItem.scheduledDepartureDisplay || "N/A"
                }`;
                seatInfoCell.querySelector(
                    ".seat-coach-seat"
                ).textContent = `Toa ${cartItem.coachPosition || "N/A"}, Ghế ${
                    cartItem.seatName || "N/A"
                } (${cartItem.seatNumberInCoach || "N/A"})`;
                const holdTimerSpan =
                    seatInfoCell.querySelector(".seat-hold-timer");
                if (cartItem.holdExpiresAt && holdTimerSpan)
                    startIndividualSeatTimer(cartItem, holdTimerSpan);
                else if (holdTimerSpan) holdTimerSpan.textContent = "";
            }

            newRow.querySelector(".original-price-cell").textContent =
                formatCurrency(cartItem.calculatedPrice);
            newRow.querySelector(".original-price-cell").dataset.basePrice =
                cartItem.calculatedPrice;
            newRow.querySelector(".final-price-cell").textContent =
                formatCurrency(cartItem.calculatedPrice);

            const dobInput = newRow.querySelector(".passenger-dateOfBirth");
            if (dobInput) {
                dobInput.addEventListener("change", (event) =>
                    handleDobChange(event.target)
                );
            }

            const deleteButton = newRow.querySelector(
                ".delete-passenger-button"
            );
            if (deleteButton) {
                deleteButton.addEventListener("click", () =>
                    handleDeleteTicketClick(index)
                );
            }

            passengerDetailsBody.appendChild(newRow);
        });
    }

    function updateTotalPaymentAndPassengerCount() {
        let total = 0;
        const validPassengerRows = Array.from(
            passengerDetailsBody.querySelectorAll("tr:not(#no-passengers-row)")
        );

        validPassengerRows.forEach((row) => {
            const finalPriceCell = row.querySelector(".final-price-cell");
            if (finalPriceCell) {
                total += parseCurrency(finalPriceCell.textContent);
            }
        });

        if (totalPaymentAmountSpan)
            totalPaymentAmountSpan.textContent = formatCurrency(total);
        if (passengerCountSpan)
            passengerCountSpan.textContent = shoppingCart.length;

        if (shoppingCart.length === 0) {
            if (noPassengersRow) noPassengersRow.style.display = "table-row";
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true;
            // displayGeneralError("Không có vé nào trong giỏ hàng. Vui lòng quay lại để chọn vé."); // Message handled by checkCartAndRedirectIfEmpty
        } else {
            if (noPassengersRow) noPassengersRow.style.display = "none";
            if (submitPaymentButton) submitPaymentButton.disabled = false;
            if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = false;
            if (
                generalErrorMessage &&
                generalErrorMessage.textContent.includes(
                    "Không có vé nào trong giỏ hàng"
                )
            ) {
                displayGeneralError("");
            }
        }
    }

    // --- EVENT HANDLERS ---
    function setupEventListeners() {
        if (paymentForm) {
            paymentForm.addEventListener("submit", handleFormSubmission);
        }
        passengerDetailsBody.addEventListener("change", function (event) {
            const target = event.target;
            if (target.classList.contains("passenger-type-selector")) {
                handlePassengerTypeChange(target);
            }
        });
        customerEmailConfirmInput?.addEventListener("input", () =>
            validateEmailConfirmation(true)
        );

        if (clearAllSeatsBtn) {
            // Moved event listener setup here
            clearAllSeatsBtn.addEventListener("click", async function () {
                if (
                    !confirm(
                        "Bạn có chắc chắn muốn hủy tất cả các vé đã chọn không?"
                    )
                ) {
                    return;
                }
                try {
                    const response = await fetch(
                        `${contextPath}/api/seats/releaseAllBySession`,
                        {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/json",
                            },
                        }
                    );
                    const responseData = await response.json();
                    if (response.ok && responseData.status === "success") {
                        alert(
                            responseData.message ||
                                `Đã hủy ${
                                    responseData.data?.releasedCount || 0
                                } vé đã chọn.`
                        );

                        shoppingCart.length = 0;
                        sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);

                        renderPassengerRows();
                        updateTotalPaymentAndPassengerCount();
                        checkCartAndRedirectIfEmpty();
                    } else {
                        alert(
                            `Lỗi: ${
                                responseData.message || "Không thể hủy vé."
                            }`
                        );
                    }
                } catch (error) {
                    console.error("Error releasing all seats:", error);
                    alert("Lỗi kết nối khi cố gắng hủy tất cả vé.");
                }
            });
        }
    }

    function handleDobChange(dobInputElement) {
        const row = dobInputElement.closest("tr");
        if (!row) return;
        const passengerTypeSelect = row.querySelector(
            ".passenger-type-selector"
        );
        const selectedOption =
            passengerTypeSelect.options[passengerTypeSelect.selectedIndex];
        const passengerTypeName = selectedOption.textContent;
        const dobErrorSpan = row.querySelector(".dateOfBirthError");

        clearError(dobErrorSpan);

        if (passengerTypeName === "Trẻ em" && dobInputElement.value) {
            const age = getAge(dobInputElement.value);
            if (age < 0) {
                displayError(dobErrorSpan, "Ngày sinh không hợp lệ.");
                return;
            }
            const dobDate = new Date(dobInputElement.value);
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (dobDate > today) {
                displayError(dobErrorSpan, "Ngày sinh không thể ở tương lai.");
                return;
            }

            if (age < 6) {
                displayError(
                    dobErrorSpan,
                    "Trẻ em dưới 6 tuổi không cần mua vé. Vui lòng không kê khai tại đây."
                );
            } else if (age >= 16) {
                displayError(
                    dobErrorSpan,
                    "Trẻ em phải từ 6 đến dưới 16 tuổi."
                );
            }
        }
    }

    function handlePassengerTypeChange(selectElement) {
        const selectedOption =
            selectElement.options[selectElement.selectedIndex];
        const row = selectElement.closest("tr");
        if (!row || !selectedOption) return;

        const idCardInput = row.querySelector(".passenger-idCardNumber");
        const dobInput = row.querySelector(".passenger-dateOfBirth");
        const discountCell = row.querySelector(".discount-amount-cell");
        const finalPriceCell = row.querySelector(".final-price-cell");
        const originalPriceCell = row.querySelector(".original-price-cell");

        const basePrice = parseFloat(
            originalPriceCell.dataset.basePrice || "0"
        );
        const discountPercentage = parseFloat(
            selectedOption.dataset.discountPercentage || "0"
        );
        const isChild = selectedOption.dataset.isChild === "true";
        const passengerTypeName = selectedOption.textContent;

        const dobErrorSpan = row.querySelector(".dateOfBirthError");
        clearError(dobErrorSpan);

        if (idCardInput) {
            const showIdCard = !isChild;
            idCardInput.style.display = showIdCard ? "block" : "none";
            idCardInput.required = showIdCard;
            if (!showIdCard) {
                clearError(row.querySelector(".idCardNumberError"));
                idCardInput.value = "";
            }
        }

        if (dobInput) {
            const showDob = isChild;
            dobInput.style.display = showDob ? "block" : "none";
            dobInput.required = showDob;
            if (showDob) {
                const today = new Date().toISOString().split("T")[0];
                dobInput.max = today;
                if (dobInput.value) handleDobChange(dobInput);
            } else {
                dobInput.value = "";
            }
        }

        const discountAmount = basePrice * (discountPercentage / 100);
        const finalPrice = basePrice - discountAmount;

        if (discountCell)
            discountCell.textContent = formatCurrency(discountAmount);
        if (finalPriceCell)
            finalPriceCell.textContent = formatCurrency(finalPrice);

        updateTotalPaymentAndPassengerCount();
    }

    async function handleDeleteTicketClick(cartItemIndex) {
        if (cartItemIndex < 0 || cartItemIndex >= shoppingCart.length) {
            console.error(
                "Invalid cart item index for deletion:",
                cartItemIndex
            );
            return;
        }

        const itemToRemove = shoppingCart[cartItemIndex];

        if (
            !confirm(
                `Bạn có chắc chắn muốn xóa vé của hành khách này không?\n${itemToRemove.trainName}: ${itemToRemove.originStationName} - ${itemToRemove.destinationStationName}, Ghế ${itemToRemove.seatName}`
            )
        ) {
            return;
        }

        await releaseSingleSeatAPI(itemToRemove);

        shoppingCart.splice(cartItemIndex, 1);
        sessionStorage.setItem(
            SESSION_STORAGE_CART_KEY,
            JSON.stringify(shoppingCart)
        );

        renderPassengerRows();
        updateTotalPaymentAndPassengerCount();

        displayGeneralError("Đã xóa vé khỏi giỏ hàng.", false);
        setTimeout(() => displayGeneralError(""), 3000);
        checkCartAndRedirectIfEmpty(); // Call after deleting a ticket
    }

    // --- PAYMENT TIMER ---
    function startPaymentTimer() {
        let timeLeft = PAYMENT_TIMEOUT_DURATION_MS;
        updatePaymentCountdownDisplay(timeLeft);

        paymentTimerInterval = setInterval(() => {
            timeLeft -= 1000;
            updatePaymentCountdownDisplay(timeLeft);

            if (timeLeft <= 0) {
                clearInterval(paymentTimerInterval);
                handlePaymentTimeout();
            }
        }, 1000);
    }

    function updatePaymentCountdownDisplay(msLeft) {
        if (!paymentCountdownSpan) return;
        if (msLeft < 0) msLeft = 0;
        const minutes = Math.floor(msLeft / 60000);
        const seconds = Math.floor((msLeft % 60000) / 1000);
        paymentCountdownSpan.textContent = `${String(minutes).padStart(
            2,
            "0"
        )}:${String(seconds).padStart(2, "0")}`;
    }

    async function handlePaymentTimeout() {
        displayGeneralError(
            "Đã hết thời gian thanh toán. Các vé bạn chọn đã được hủy. Vui lòng thực hiện lại."
        );
        if (submitPaymentButton) submitPaymentButton.disabled = true;
        if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true;
        paymentForm
            .querySelectorAll(
                "input, select, button:not(.delete-passenger-button):not(.go-back-button-as-link)"
            )
            .forEach((el) => (el.disabled = true));

        if (shoppingCart.length > 0) {
            // Assuming releaseAllBySession API is robust for this.
            // Or use a specific releaseMultiple if available and different.
            try {
                const response = await fetch(
                    `${contextPath}/api/seats/releaseAllBySession`,
                    {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                    }
                );
                if (response.ok)
                    console.log("Seats released on backend (timeout).");
                else
                    console.error(
                        "Failed to release seats on backend (timeout)."
                    );
            } catch (error) {
                console.error("Error releasing seats (timeout):", error);
            }
        }
        sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);
        shoppingCart = [];
        updateTotalPaymentAndPassengerCount();
        checkCartAndRedirectIfEmpty(); // Redirect if cart becomes empty due to timeout
    }

    function startIndividualSeatTimer(cartItem, timerSpan) {
        if (cartItem.holdExpiresAt) {
            const expiresAtDate = new Date(cartItem.holdExpiresAt);
            const now = new Date();
            const timeLeftMs = expiresAtDate.getTime() - now.getTime();
            if (timeLeftMs > 0) {
                const minutes = Math.floor(timeLeftMs / 60000);
                const seconds = Math.floor((timeLeftMs % 60000) / 1000);
                timerSpan.textContent = `(Giữ đến ${minutes}:${String(
                    seconds
                ).padStart(2, "0")})`;
            } else {
                timerSpan.textContent = "(Đã hết hạn giữ)";
            }
        }
    }

    // --- FORM VALIDATION & SUBMISSION ---
    function validateForm() {
        let isValid = true;
        clearAllErrors();

        const passengerRows = passengerDetailsBody.querySelectorAll(
            "tr:not(#no-passengers-row)"
        );
        passengerRows.forEach((row) => {
            if (!row.querySelector(".passenger-fullName")) return;

            const fullNameInput = row.querySelector(".passenger-fullName");
            const passengerTypeSelect = row.querySelector(
                ".passenger-type-selector"
            );
            const selectedOption =
                passengerTypeSelect.options[passengerTypeSelect.selectedIndex];
            const isChildType = selectedOption.dataset.isChild === "true";
            const passengerTypeName = selectedOption.textContent;

            const idCardInput = row.querySelector(".passenger-idCardNumber");
            const dobInput = row.querySelector(".passenger-dateOfBirth");

            const fullNameErrorSpan = row.querySelector(".fullNameError");
            const passengerTypeErrorSpan = row.querySelector(
                ".passengerTypeError"
            );
            const idErrorSpan = row.querySelector(".idCardNumberError");
            const dobErrorSpan = row.querySelector(".dateOfBirthError");

            clearError(fullNameErrorSpan);
            clearError(passengerTypeErrorSpan);
            clearError(idErrorSpan);
            clearError(dobErrorSpan);

            if (!fullNameInput.value.trim()) {
                displayError(fullNameErrorSpan, "Vui lòng nhập họ tên.");
                isValid = false;
            }
            if (!passengerTypeSelect.value) {
                displayError(
                    passengerTypeErrorSpan,
                    "Vui lòng chọn đối tượng."
                );
                isValid = false;
            }

            if (!isChildType) {
                if (!idCardInput.value.trim()) {
                    displayError(idErrorSpan, "Vui lòng nhập số CMND/CCCD.");
                    isValid = false;
                } else if (!/^\d{9,12}$/.test(idCardInput.value.trim())) {
                    displayError(
                        idErrorSpan,
                        "Số CMND/CCCD không hợp lệ (9-12 số)."
                    );
                    isValid = false;
                }
            } else {
                if (!dobInput.value) {
                    displayError(dobErrorSpan, "Vui lòng chọn ngày sinh.");
                    isValid = false;
                } else {
                    const age = getAge(dobInput.value);
                    const dobDate = new Date(dobInput.value);
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);

                    if (age < 0 || dobDate > today) {
                        displayError(
                            dobErrorSpan,
                            "Ngày sinh không hợp lệ hoặc ở tương lai."
                        );
                        isValid = false;
                    } else if (passengerTypeName === "Trẻ em") {
                        if (age < 6) {
                            displayError(
                                dobErrorSpan,
                                "Trẻ em dưới 6 tuổi không cần mua vé. Vui lòng bỏ chọn."
                            );
                            isValid = false;
                        } else if (age >= 16) {
                            displayError(
                                dobErrorSpan,
                                "Trẻ em phải từ 6 đến dưới 16 tuổi."
                            );
                            isValid = false;
                        }
                    }
                }
            }
        });

        const fieldsToValidate = [
            {
                input: customerFullNameInput,
                errorId: "customerFullNameError",
                msg: "Vui lòng nhập họ tên người đặt vé.",
            },
            {
                input: customerEmailInput,
                errorId: "customerEmailError",
                msg: "Vui lòng nhập email.",
                pattern: /\S+@\S+\.\S+/,
                patternMsg: "Email không hợp lệ.",
            },
            {
                input: customerEmailConfirmInput,
                errorId: "customerEmailConfirmError",
                msg: "Vui lòng nhập lại email.",
            },
            {
                input: customerPhoneInput,
                errorId: "customerPhoneError",
                msg: "Vui lòng nhập số điện thoại.",
                pattern: /^\d{10,11}$/,
                patternMsg: "Số điện thoại không hợp lệ (10-11 số).",
            },
            {
                input: customerIDCardInput,
                errorId: "customerIDCardError",
                pattern: /^\d{9,12}$/,
                patternMsg: "Số CMND/CCCD người đặt vé không hợp lệ (9-12 số).",
                optional: true,
            },
        ];

        fieldsToValidate.forEach((field) => {
            const errorSpan = document.getElementById(field.errorId);
            clearError(errorSpan);
            if (!field.optional && !field.input.value.trim()) {
                displayError(errorSpan, field.msg);
                isValid = false;
            } else if (
                field.pattern &&
                field.input.value.trim() &&
                !field.pattern.test(field.input.value.trim())
            ) {
                displayError(errorSpan, field.patternMsg);
                isValid = false;
            }
        });

        const emailConfirmErrorSpan = document.getElementById(
            "customerEmailConfirmError"
        );
        clearError(emailConfirmErrorSpan);
        if (!customerEmailConfirmInput.value.trim()) {
            displayError(emailConfirmErrorSpan, "Vui lòng nhập lại email.");
            isValid = false;
        } else if (
            customerEmailInput.value.trim() !==
            customerEmailConfirmInput.value.trim()
        ) {
            displayError(emailConfirmErrorSpan, "Email nhập lại không khớp.");
            isValid = false;
        }

        const agreeTermsErrorSpan = document.getElementById("agreeTermsError");
        clearError(agreeTermsErrorSpan);
        if (!agreeTermsCheckbox.checked) {
            displayError(
                agreeTermsErrorSpan,
                "Bạn phải đồng ý với điều khoản."
            );
            isValid = false;
        }

        return isValid;
    }

    function validateEmailConfirmation(showErrorIfEmptyConfirm) {
        const emailErrorSpan = document.getElementById(
            "customerEmailConfirmError"
        );
        clearError(emailErrorSpan);
        if (
            !customerEmailConfirmInput.value.trim() &&
            showErrorIfEmptyConfirm
        ) {
            displayError(emailErrorSpan, "Vui lòng nhập lại email.");
            return false;
        }
        if (
            customerEmailConfirmInput.value.trim() &&
            customerEmailInput.value.trim() !==
                customerEmailConfirmInput.value.trim()
        ) {
            displayError(emailErrorSpan, "Email nhập lại không khớp.");
            return false;
        }
        return true;
    }

    async function handleFormSubmission(event) {
        event.preventDefault();
        if (!validateForm()) {
            displayGeneralError("Vui lòng kiểm tra lại các thông tin đã nhập.");
            const firstError = document.querySelector(
                ".error-message[style*='display: block'], .error-message[style*='display: inline']"
            );
            if (firstError)
                firstError.scrollIntoView({
                    behavior: "smooth",
                    block: "center",
                });
            return;
        }
        displayGeneralError("");

        submitPaymentButton.disabled = true;
        submitPaymentButton.textContent = "Đang xử lý...";

        const passengerData = shoppingCart
            .map((cartItem, index) => {
                const row = passengerDetailsBody.querySelector(
                    `tr[data-cart-item-index="${index}"]`
                );
                if (!row) return null;
                return {
                    originalCartItem: cartItem,
                    fullName: row
                        .querySelector(".passenger-fullName")
                        .value.trim(),
                    passengerTypeID: row.querySelector(
                        ".passenger-type-selector"
                    ).value,
                    idCardNumber:
                        row.querySelector(".passenger-idCardNumber").style
                            .display !== "none"
                            ? row
                                  .querySelector(".passenger-idCardNumber")
                                  .value.trim()
                            : null,
                    dateOfBirth:
                        row.querySelector(".passenger-dateOfBirth").style
                            .display !== "none"
                            ? row.querySelector(".passenger-dateOfBirth").value
                            : null,
                };
            })
            .filter((p) => p !== null);

        const paymentPayload = {
            customerDetails: {
                fullName: customerFullNameInput.value.trim(),
                email: customerEmailInput.value.trim(),
                phoneNumber: customerPhoneInput.value.trim(),
                idCardNumber: customerIDCardInput.value.trim() || null,
            },
            passengers: passengerData,
            paymentMethod: document.getElementById("paymentMethod").value,
            totalAmount: parseCurrency(totalPaymentAmountSpan.textContent),
        };

        console.log(
            "Payment Payload:",
            JSON.stringify(paymentPayload, null, 2)
        );

        try {
            const response = await fetch(
                `${contextPath}/api/booking/initiatePayment`, // Updated servlet path
                {
                    // Ensure this servlet path is correct
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(paymentPayload),
                }
            );
            const responseData = await response.json();

            if (
                response.ok &&
                responseData.status === "VNPAY_REDIRECT" &&
                responseData.paymentUrl
            ) {
                clearInterval(paymentTimerInterval);
                // Do not remove shopping cart from session yet, VNPAY payment is pending
                // sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);
                displayGeneralError(
                    `Đang chuyển hướng đến VNPAY để thanh toán cho đơn hàng ${
                        responseData.bookingCode || ""
                    }...`,
                    false
                );

                window.location.href = responseData.paymentUrl; // Redirect to VNPAY
            } else if (response.ok && responseData.status === "success") {
                // Keep existing success for non-VNPAY or direct success
                clearInterval(paymentTimerInterval);
                sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);
                displayGeneralError(
                    `Đặt vé thành công! Mã đặt vé: ${
                        responseData.bookingCode || ""
                    }. Sẽ chuyển hướng sau 3 giây...`,
                    false
                );
                setTimeout(() => {
                    window.location.href =
                        responseData.redirectUrl ||
                        `${contextPath}/bookingConfirmation.jsp?bookingCode=${responseData.bookingCode}`;
                }, 3000);
            } else {
                displayGeneralError(
                    `Lỗi: ${
                        responseData.message || "Không thể xử lý thanh toán."
                    }`
                );
                submitPaymentButton.disabled = false;
                submitPaymentButton.textContent = "Thanh toán";
            }
        } catch (error) {
            console.error("Payment submission error:", error);
            displayGeneralError("Lỗi kết nối khi thanh toán.");
            submitPaymentButton.disabled = false;
            submitPaymentButton.textContent = "Thanh toán";
        } finally {
            sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);
            shoppingCart = [];
        }
    }

    /**
     * Checks if the shopping cart is empty and if so, triggers the go-back button.
     */
    function checkCartAndRedirectIfEmpty() {
        if (shoppingCart.length === 0) {
            // Check if a message is already being displayed by timeout handler
            if (
                !generalErrorMessage.textContent.includes(
                    "Đã hết thời gian thanh toán"
                )
            ) {
                displayGeneralError(
                    "Giỏ hàng trống. Đang quay lại trang tìm kiếm..."
                );
            }
            const goBackButton = document.querySelector(
                ".go-back-button-as-link"
            );
            if (goBackButton) {
                // Disable other interactions to prevent issues during auto-redirect
                if (submitPaymentButton) submitPaymentButton.disabled = true;
                if (clearAllSeatsBtn) clearAllSeatsBtn.disabled = true;

                // Only redirect if not already in a timeout state that shows a specific message
                if (
                    !generalErrorMessage.textContent.includes(
                        "Đã hết thời gian thanh toán"
                    )
                ) {
                    setTimeout(() => {
                        goBackButton.click();
                    }, 1500);
                }
            } else {
                console.error(
                    "Go back button not found for automatic redirect."
                );
            }
        }
    }

    // --- START ---
    initializePage();
});
