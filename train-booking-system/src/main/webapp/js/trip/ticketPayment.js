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
        autoPopulateCustomerInfo(); // Auto-populate customer info for logged-in users
        checkCartAndRedirectIfEmpty(); // Check on initial load as well
    }
    
    // Auto-populate customer information for logged-in users
    function autoPopulateCustomerInfo() {
        if (typeof loggedInUser !== 'undefined' && loggedInUser) {
            // Populate full name if available
            if (loggedInUser.fullName && loggedInUser.fullName.trim() !== '') {
                customerFullNameInput.value = loggedInUser.fullName.trim();
            }
            
            // Populate email (main field only, leave confirmation empty for user to type)
            if (loggedInUser.email && loggedInUser.email.trim() !== '') {
                customerEmailInput.value = loggedInUser.email.trim();
            }
            
            // Populate phone number if available
            if (loggedInUser.phoneNumber && loggedInUser.phoneNumber.trim() !== '') {
                customerPhoneInput.value = loggedInUser.phoneNumber.trim();
            }
            
            // Populate ID card number if available
            if (loggedInUser.idCardNumber && loggedInUser.idCardNumber.trim() !== '') {
                customerIDCardInput.value = loggedInUser.idCardNumber.trim();
            }
        }
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
            
            // Add error styling to associated input
            const input = findAssociatedInput(element);
            if (input) {
                input.classList.add("error");
                input.classList.remove("valid");
            }
        }
    }

    function clearError(element) {
        if (element) {
            element.textContent = "";
            element.style.display = "none";
            
            // Add valid styling to associated input
            const input = findAssociatedInput(element);
            if (input && input.value && input.value.trim()) {
                input.classList.add("valid");
                input.classList.remove("error");
            } else if (input) {
                input.classList.remove("error", "valid");
            }
        }
    }

    function findAssociatedInput(errorElement) {
        if (!errorElement) return null;
        
        // For passenger table rows
        const row = errorElement.closest("tr");
        if (row) {
            if (errorElement.classList.contains("fullNameError")) {
                return row.querySelector(".passenger-fullName");
            }
            if (errorElement.classList.contains("passengerTypeError")) {
                return row.querySelector(".passenger-type-selector");
            }
            if (errorElement.classList.contains("idCardNumberError")) {
                return row.querySelector(".passenger-idCardNumber");
            }
            if (errorElement.classList.contains("dateOfBirthError")) {
                return row.querySelector(".passenger-dateOfBirth");
            }
        }
        
        // For customer fields
        const errorId = errorElement.id;
        if (errorId) {
            const inputId = errorId.replace("Error", "");
            return document.getElementById(inputId);
        }
        
        return null;
    }

    function clearAllErrors() {
        document
            .querySelectorAll(".error-message")
            .forEach((span) => clearError(span));
        
        // Also clear all input styling
        document
            .querySelectorAll("input.error, select.error, input.valid, select.valid")
            .forEach((input) => {
                input.classList.remove("error", "valid");
            });
    }

    function displayGeneralError(message, isError = true) {
        if (generalErrorMessage) {
            generalErrorMessage.textContent = message;
            generalErrorMessage.style.color = isError ? "red" : "green";
            generalErrorMessage.style.display = message ? "block" : "none";
        }
    }

    // Real-time validation for passenger fields
    function validateFieldRealTime(field, isBlur = false) {
        if (!field) return;
        
        const row = field.closest("tr");
        if (!row) return;
        
        if (field.classList.contains("passenger-fullName")) {
            const errorSpan = row.querySelector(".fullNameError");
            const value = field.value ? field.value.trim() : "";
            if (!value && isBlur) {
                displayError(errorSpan, "Vui lòng nhập họ tên.");
            } else if (value.length > 0 && value.length < 2) {
                displayError(errorSpan, "Họ tên phải có ít nhất 2 ký tự.");
            } else if (value.length > 100) {
                displayError(errorSpan, "Họ tên không được quá 100 ký tự.");
            } else if (value) {
                clearError(errorSpan);
            }
        }
        
        if (field.classList.contains("passenger-idCardNumber")) {
            const errorSpan = row.querySelector(".idCardNumberError");
            const value = field.value ? field.value.trim() : "";
            
            if (field.style.display !== "none" && isBlur && !value) {
                displayError(errorSpan, "Vui lòng nhập số CMND/CCCD.");
            } else if (value && !/^\d{9,12}$/.test(value)) {
                displayError(errorSpan, "Số CMND/CCCD không hợp lệ (9-12 số).");
            } else if (value && /^\d{9,12}$/.test(value)) {
                // Check for duplicate ID numbers
                const duplicateError = checkForDuplicateId(value, row);
                if (duplicateError) {
                    displayError(errorSpan, duplicateError);
                } else {
                    clearError(errorSpan);
                }
            }
        }
        
        if (field.classList.contains("passenger-type-selector")) {
            const errorSpan = row.querySelector(".passengerTypeError");
            if (!field.value && isBlur) {
                displayError(errorSpan, "Vui lòng chọn đối tượng.");
            } else if (field.value) {
                clearError(errorSpan);
            }
        }
    }

    // Check for duplicate ID numbers across all passengers (excluding customer ID - customers can book for themselves)
    function checkForDuplicateId(idValue, currentRow) {
        if (!idValue || idValue.length === 0) return null;
        
        const allIdInputs = passengerDetailsBody.querySelectorAll(".passenger-idCardNumber");
        const currentRowIndex = Array.from(passengerDetailsBody.querySelectorAll("tr")).indexOf(currentRow);
        
        let duplicateCount = 0;
        let duplicateRowNumbers = [];
        
        allIdInputs.forEach((input, index) => {
            if (input.style.display !== "none" && input.value && input.value.trim() === idValue) {
                duplicateCount++;
                const row = input.closest("tr");
                const rowIndex = Array.from(passengerDetailsBody.querySelectorAll("tr")).indexOf(row);
                if (rowIndex !== currentRowIndex) {
                    duplicateRowNumbers.push(rowIndex);
                }
            }
        });
        
        if (duplicateCount > 1) {
            return `Số CMND/CCCD này đã được sử dụng cho hành khách khác.`;
        }
        
        // Note: We allow customer ID to match passenger ID (customer booking for themselves)
        return null;
    }

    // Validate all ID fields for duplicates
    function validateAllIdFields() {
        let hasError = false;
        const idInputs = passengerDetailsBody.querySelectorAll(".passenger-idCardNumber");
        
        // Clear previous duplicate errors
        idInputs.forEach(input => {
            const row = input.closest("tr");
            const errorSpan = row.querySelector(".idCardNumberError");
            if (errorSpan && errorSpan.textContent.includes("đã được sử dụng")) {
                clearError(errorSpan);
            }
        });
        
        // Check each ID field for duplicates
        idInputs.forEach(input => {
            if (input.style.display !== "none" && input.value && input.value.trim()) {
                const row = input.closest("tr");
                const errorSpan = row.querySelector(".idCardNumberError");
                const duplicateError = checkForDuplicateId(input.value.trim(), row);
                
                if (duplicateError) {
                    displayError(errorSpan, duplicateError);
                    hasError = true;
                }
            }
        });
        
        return !hasError;
    }

    // Real-time validation for customer fields
    function validateCustomerField(field, errorSpanId, requiredMessage, pattern, patternMessage) {
        if (!field) return;
        
        const errorSpan = document.getElementById(errorSpanId);
        const value = field.value ? field.value.trim() : "";
        
        clearError(errorSpan);
        
        if (!value && requiredMessage) {
            displayError(errorSpan, requiredMessage);
            return false;
        }
        
        if (value && pattern && !pattern.test(value)) {
            displayError(errorSpan, patternMessage);
            return false;
        }
        
        // Note: We allow customer ID to match passenger ID (customer booking for themselves)
        // No additional validation needed here
        
        return true;
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
        
        // Store current form data before clearing
        const currentFormData = preserveCurrentFormData();
        
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
            newRow.dataset.seatId = cartItem.seatID;
            newRow.dataset.tripId = cartItem.tripId;

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
        
        // Restore form data after rendering
        restoreFormData(currentFormData);
    }

    // Store form data before re-rendering
    function preserveCurrentFormData() {
        const formData = {};
        const rows = passengerDetailsBody.querySelectorAll("tr:not(#no-passengers-row)");
        
        rows.forEach((row) => {
            const seatId = row.dataset.seatId;
            const tripId = row.dataset.tripId;
            if (!seatId || !tripId) return;
            
            const key = `${tripId}_${seatId}`;
            const fullNameInput = row.querySelector(".passenger-fullName");
            const passengerTypeSelect = row.querySelector(".passenger-type-selector");
            const idCardInput = row.querySelector(".passenger-idCardNumber");
            const dobInput = row.querySelector(".passenger-dateOfBirth");
            
            if (fullNameInput || passengerTypeSelect || idCardInput || dobInput) {
                formData[key] = {
                    fullName: fullNameInput?.value || "",
                    passengerType: passengerTypeSelect?.value || "",
                    idCardNumber: idCardInput?.value || "",
                    dateOfBirth: dobInput?.value || "",
                    // Preserve VIP validation data
                    vipValidated: row.dataset.vipValidated || "false",
                    vipDiscountPercentage: row.dataset.vipDiscountPercentage || "0",
                    vipTypeName: row.dataset.vipTypeName || "",
                    vipIcon: row.dataset.vipIcon || "",
                    vipMemberName: row.dataset.vipMemberName || ""
                };
            }
        });
        
        return formData;
    }

    // Restore form data after re-rendering
    function restoreFormData(formData) {
        if (!formData || Object.keys(formData).length === 0) return;
        
        const rows = passengerDetailsBody.querySelectorAll("tr:not(#no-passengers-row)");
        
        rows.forEach((row) => {
            const seatId = row.dataset.seatId;
            const tripId = row.dataset.tripId;
            if (!seatId || !tripId) return;
            
            const key = `${tripId}_${seatId}`;
            const savedData = formData[key];
            if (!savedData) return;
            
            const fullNameInput = row.querySelector(".passenger-fullName");
            const passengerTypeSelect = row.querySelector(".passenger-type-selector");
            const idCardInput = row.querySelector(".passenger-idCardNumber");
            const dobInput = row.querySelector(".passenger-dateOfBirth");
            
            if (fullNameInput && savedData.fullName) {
                fullNameInput.value = savedData.fullName;
            }
            
            if (passengerTypeSelect && savedData.passengerType) {
                passengerTypeSelect.value = savedData.passengerType;
                // Trigger change event to update pricing and field visibility
                const changeEvent = new Event('change', { bubbles: true });
                passengerTypeSelect.dispatchEvent(changeEvent);
            }
            
            if (idCardInput && savedData.idCardNumber) {
                idCardInput.value = savedData.idCardNumber;
            }
            
            if (dobInput && savedData.dateOfBirth) {
                dobInput.value = savedData.dateOfBirth;
                // Trigger change event for validation
                const changeEvent = new Event('change', { bubbles: true });
                dobInput.dispatchEvent(changeEvent);
            }
            
            // Restore VIP validation data
            if (savedData.vipValidated === "true") {
                row.dataset.vipValidated = "true";
                row.dataset.vipDiscountPercentage = savedData.vipDiscountPercentage;
                row.dataset.vipTypeName = savedData.vipTypeName;
                row.dataset.vipIcon = savedData.vipIcon;
                row.dataset.vipMemberName = savedData.vipMemberName;
                
                // Restore VIP indicator
                const passengerTypeCell = row.querySelector('.passenger-name-type-cell');
                if (passengerTypeCell && savedData.vipTypeName && savedData.vipIcon) {
                    // Remove existing VIP indicator
                    const existingIndicator = passengerTypeCell.querySelector('.vip-member-indicator');
                    if (existingIndicator) {
                        existingIndicator.remove();
                    }
                    
                    // Add VIP indicator
                    const vipIndicator = document.createElement('div');
                    vipIndicator.className = 'vip-member-indicator';
                    vipIndicator.innerHTML = `${savedData.vipIcon} VIP ${savedData.vipTypeName} (-${savedData.vipDiscountPercentage}%)`;
                    vipIndicator.style.cssText = `
                        font-size: 0.8em;
                        color: #B8860B;
                        font-weight: bold;
                        margin-top: 2px;
                        padding: 2px 4px;
                        background: linear-gradient(135deg, #FFD700, #FFA500);
                        border-radius: 3px;
                        display: inline-block;
                    `;
                    passengerTypeCell.appendChild(vipIndicator);
                }
            }
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
        
        // Enhanced passenger form validation
        passengerDetailsBody.addEventListener("change", function (event) {
            const target = event.target;
            if (target.classList.contains("passenger-type-selector")) {
                handlePassengerTypeChange(target);
            }
        });
        
        passengerDetailsBody.addEventListener("input", function (event) {
            const target = event.target;
            validateFieldRealTime(target);
        });
        
        passengerDetailsBody.addEventListener("blur", function (event) {
            const target = event.target;
            validateFieldRealTime(target, true);
        }, true);
        
        customerEmailConfirmInput?.addEventListener("input", () =>
            validateEmailConfirmation(true)
        );
        
        // Add real-time validation for customer fields
        customerFullNameInput?.addEventListener("blur", () => validateCustomerField(customerFullNameInput, "customerFullNameError", "Vui lòng nhập họ tên người đặt vé."));
        customerEmailInput?.addEventListener("blur", () => validateCustomerField(customerEmailInput, "customerEmailError", "Vui lòng nhập email.", /\S+@\S+\.\S+/, "Email không hợp lệ."));
        customerPhoneInput?.addEventListener("blur", () => validateCustomerField(customerPhoneInput, "customerPhoneError", "Vui lòng nhập số điện thoại.", /^\d{10,11}$/, "Số điện thoại không hợp lệ (10-11 số)."));
        customerIDCardInput?.addEventListener("blur", () => validateCustomerField(customerIDCardInput, "customerIDCardError", null, /^\d{9,12}$/, "Số CMND/CCCD không hợp lệ (9-12 số)."));

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

        // Check if VIP Member is selected
        if (passengerTypeName.includes("VIP") || passengerTypeName.includes("Thành viên VIP")) {
            // Show VIP validation modal
            showVIPModal(row);
            return; // Exit early, price calculation will happen after VIP validation
        }

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

        // Calculate discount
        let totalDiscountAmount = basePrice * (discountPercentage / 100);
        let finalPrice = basePrice - totalDiscountAmount;

        // Apply additional VIP discount if passenger is validated VIP member
        if (row.dataset.vipValidated === "true") {
            const vipDiscountPercentage = parseFloat(row.dataset.vipDiscountPercentage || "0");
            const vipDiscountAmount = finalPrice * (vipDiscountPercentage / 100);
            finalPrice = finalPrice - vipDiscountAmount;
            totalDiscountAmount = totalDiscountAmount + vipDiscountAmount;
            
            // Add VIP indicator to the discount cell
            if (discountCell) {
                const vipIcon = row.dataset.vipIcon || "👑";
                const vipTypeName = row.dataset.vipTypeName || "VIP";
                discountCell.innerHTML = `${formatCurrency(totalDiscountAmount)}<br><small style="color: #B8860B;">${vipIcon} VIP ${vipTypeName}</small>`;
            }
        } else {
            if (discountCell)
                discountCell.textContent = formatCurrency(totalDiscountAmount);
        }

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
        if (!cartItem.holdExpiresAt) return;
        
        const expiresAtDate = new Date(cartItem.holdExpiresAt);
        const seatKey = `${cartItem.tripId}_${cartItem.seatID}`;
        
        function updateTimer() {
            const now = new Date();
            const timeLeftMs = expiresAtDate.getTime() - now.getTime();
            
            if (timeLeftMs > 0) {
                const minutes = Math.floor(timeLeftMs / 60000);
                const seconds = Math.floor((timeLeftMs % 60000) / 1000);
                timerSpan.textContent = `(Giữ đến ${minutes}:${String(seconds).padStart(2, "0")})`;
                timerSpan.className = "seat-hold-timer";
            } else {
                timerSpan.innerHTML = `<i class="fas fa-clock icon-expired-hold" style="color: #ff6b6b;"></i> Đã hết hạn giữ`;
                timerSpan.className = "seat-hold-timer expired";
                
                // Show expired holds notice
                const expiredNotice = document.getElementById("expired-holds-notice");
                if (expiredNotice) {
                    expiredNotice.style.display = "block";
                }
                
                // Mark the row as expired
                const row = timerSpan.closest("tr");
                if (row) {
                    row.classList.add("expired-hold-row");
                    const deleteButton = row.querySelector(".delete-passenger-button");
                    if (deleteButton) {
                        deleteButton.innerHTML = '<i class="fas fa-exclamation-triangle" style="color: #ff6b6b;"></i> Hủy vé hết hạn';
                        deleteButton.style.backgroundColor = "#ff6b6b";
                        deleteButton.style.color = "white";
                    }
                }
                
                clearInterval(window.seatTimers?.[seatKey]);
                if (window.seatTimers) delete window.seatTimers[seatKey];
                return;
            }
        }
        
        // Initialize timer storage
        if (!window.seatTimers) window.seatTimers = {};
        
        // Clear existing timer if any
        if (window.seatTimers[seatKey]) {
            clearInterval(window.seatTimers[seatKey]);
        }
        
        // Initial update
        updateTimer();
        
        // Set interval for updates
        window.seatTimers[seatKey] = setInterval(updateTimer, 1000);
    }

    // --- FORM VALIDATION & SUBMISSION ---
    function validateForm() {
        let isValid = true;
        clearAllErrors();

        const passengerRows = passengerDetailsBody.querySelectorAll(
            "tr:not(#no-passengers-row)"
        );
        
        // Enhanced passenger validation
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

            // Enhanced name validation
            const fullNameValue = fullNameInput.value ? fullNameInput.value.trim() : "";
            if (!fullNameValue) {
                displayError(fullNameErrorSpan, "Vui lòng nhập họ tên.");
                isValid = false;
            } else if (fullNameValue.length < 2) {
                displayError(fullNameErrorSpan, "Họ tên phải có ít nhất 2 ký tự.");
                isValid = false;
            } else if (fullNameValue.length > 100) {
                displayError(fullNameErrorSpan, "Họ tên không được quá 100 ký tự.");
                isValid = false;
            } else if (!/^[a-zA-ZÀ-ỹ\s]+$/.test(fullNameValue)) {
                displayError(fullNameErrorSpan, "Họ tên chỉ được chứa chữ cái và khoảng trắng.");
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
                const idValue = idCardInput.value ? idCardInput.value.trim() : "";
                if (!idValue) {
                    displayError(idErrorSpan, "Vui lòng nhập số CMND/CCCD.");
                    isValid = false;
                } else if (!/^\d{9,12}$/.test(idValue)) {
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

        // Check for duplicate ID numbers
        if (!validateAllIdFields()) {
            isValid = false;
        }

        // Enhanced customer validation
        const fieldsToValidate = [
            {
                input: customerFullNameInput,
                errorId: "customerFullNameError",
                msg: "Vui lòng nhập họ tên người đặt vé.",
                minLength: 2,
                maxLength: 100,
                pattern: /^[a-zA-ZÀ-ỹ\s]+$/,
                patternMsg: "Họ tên chỉ được chứa chữ cái và khoảng trắng.",
            },
            {
                input: customerEmailInput,
                errorId: "customerEmailError",
                msg: "Vui lòng nhập email.",
                pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                patternMsg: "Email không hợp lệ.",
                maxLength: 254,
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
                pattern: /^0\d{9,10}$/,
                patternMsg: "Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10-11 số).",
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
            const value = field.input && field.input.value ? field.input.value.trim() : "";
            
            if (!field.optional && !value) {
                displayError(errorSpan, field.msg);
                isValid = false;
            } else if (value) {
                if (field.minLength && value.length < field.minLength) {
                    displayError(errorSpan, `${field.input.placeholder || 'Trường này'} phải có ít nhất ${field.minLength} ký tự.`);
                    isValid = false;
                } else if (field.maxLength && value.length > field.maxLength) {
                    displayError(errorSpan, `${field.input.placeholder || 'Trường này'} không được quá ${field.maxLength} ký tự.`);
                    isValid = false;
                } else if (field.pattern && !field.pattern.test(value)) {
                    displayError(errorSpan, field.patternMsg);
                    isValid = false;
                }
            }
        });

        // Email confirmation validation
        const emailConfirmErrorSpan = document.getElementById(
            "customerEmailConfirmError"
        );
        clearError(emailConfirmErrorSpan);
        const emailValue = customerEmailInput && customerEmailInput.value ? customerEmailInput.value.trim() : "";
        const emailConfirmValue = customerEmailConfirmInput && customerEmailConfirmInput.value ? customerEmailConfirmInput.value.trim() : "";
        
        if (!emailConfirmValue) {
            displayError(emailConfirmErrorSpan, "Vui lòng nhập lại email.");
            isValid = false;
        } else if (emailValue !== emailConfirmValue) {
            displayError(emailConfirmErrorSpan, "Email nhập lại không khớp.");
            isValid = false;
        }

        // Terms agreement validation
        const agreeTermsErrorSpan = document.getElementById("agreeTermsError");
        clearError(agreeTermsErrorSpan);
        if (!agreeTermsCheckbox || !agreeTermsCheckbox.checked) {
            displayError(
                agreeTermsErrorSpan,
                "Bạn phải đồng ý với điều khoản."
            );
            isValid = false;
        }

        // Check for expired seat holds
        const expiredRows = passengerDetailsBody.querySelectorAll(".expired-hold-row");
        if (expiredRows.length > 0) {
            displayGeneralError("Có vé đã hết hạn giữ. Vui lòng xóa các vé hết hạn trước khi thanh toán.");
            isValid = false;
        }

        // Note: We allow customer ID to match passenger ID (customer can book for themselves)
        // This validation has been removed to allow self-booking

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

    // === VIP MODAL FUNCTIONS ===
    function showVIPModal(row) {
        window.currentVIPRow = row;
        window.vipValidationResult = null;
        
        // Reset modal state
        document.getElementById('vipIdInput').value = '';
        document.getElementById('vipValidationMessage').textContent = '';
        document.getElementById('vipValidationMessage').className = 'validation-message';
        document.getElementById('vipValidationResult').style.display = 'none';
        document.getElementById('validateVIPBtn').style.display = 'inline-block';
        document.getElementById('confirmVIPBtn').style.display = 'none';
        
        // Show modal
        document.getElementById('vipValidationModal').style.display = 'block';
        document.getElementById('modalOverlay').style.display = 'block';
        document.body.style.overflow = 'hidden';
        
        // Focus on input
        setTimeout(() => {
            document.getElementById('vipIdInput').focus();
        }, 100);
    }

    function closeVIPModal() {
        document.getElementById('vipValidationModal').style.display = 'none';
        document.getElementById('modalOverlay').style.display = 'none';
        document.body.style.overflow = 'auto';
        
        // If VIP validation was not successful and row is not already VIP validated, reset passenger type to default
        if (!window.vipValidationResult || !window.vipValidationResult.isValid) {
            if (window.currentVIPRow && window.currentVIPRow.dataset.vipValidated !== "true") {
                const passengerTypeSelect = window.currentVIPRow.querySelector('.passenger-type-selector');
                if (passengerTypeSelect) {
                    passengerTypeSelect.selectedIndex = 0; // Reset to default
                    handlePassengerTypeChange(passengerTypeSelect);
                }
            }
        }
        
        window.currentVIPRow = null;
        window.vipValidationResult = null;
    }

    async function validateVIPCredentials() {
        const idCardNumber = document.getElementById('vipIdInput').value.trim();
        const messageElement = document.getElementById('vipValidationMessage');
        const resultElement = document.getElementById('vipValidationResult');
        const loadingElement = document.getElementById('vipLoadingIndicator');
        const validateBtn = document.getElementById('validateVIPBtn');
        const confirmBtn = document.getElementById('confirmVIPBtn');
        
        // Reset previous state
        messageElement.textContent = '';
        messageElement.className = 'validation-message';
        resultElement.style.display = 'none';
        
        // Validate input
        if (!idCardNumber) {
            messageElement.textContent = 'Vui lòng nhập số CMND/CCCD';
            messageElement.className = 'validation-message error';
            return;
        }
        
        if (!/^\d{9,12}$/.test(idCardNumber)) {
            messageElement.textContent = 'Số CMND/CCCD không hợp lệ. Vui lòng nhập 9-12 chữ số.';
            messageElement.className = 'validation-message error';
            return;
        }
        
        try {
            // Show loading
            loadingElement.style.display = 'block';
            validateBtn.disabled = true;
            
            const response = await fetch(contextPath + "/api/vip/validateCredentials", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: "idCardNumber=" + encodeURIComponent(idCardNumber)
            });
            
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            
            const data = await response.json();
            
            if (data.success) {
                window.vipValidationResult = data;
                
                if (data.isValid) {
                    // Show success result
                    resultElement.innerHTML = `
                        <div class="vip-result-card">
                            <div>
                                <span class="vip-icon">${data.vipIcon}</span>
                                <strong>VIP ${data.vipTypeName}</strong>
                            </div>
                            <div style="margin-top: 10px;">
                                <div>Thành viên: <strong>${data.fullName}</strong></div>
                                <div class="vip-discount">Giảm giá: ${data.discountPercentage}%</div>
                            </div>
                        </div>
                    `;
                    resultElement.style.display = 'block';
                    messageElement.textContent = data.message;
                    messageElement.className = 'validation-message success';
                    
                    // Show confirm button, hide validate button
                    validateBtn.style.display = 'none';
                    confirmBtn.style.display = 'inline-block';
                } else {
                    // Show validation failed message
                    messageElement.textContent = data.message;
                    messageElement.className = 'validation-message error';
                    resultElement.style.display = 'none';
                }
            } else {
                messageElement.textContent = data.message || 'Có lỗi xảy ra khi xác thực VIP';
                messageElement.className = 'validation-message error';
            }
            
        } catch (error) {
            console.error("Error validating VIP credentials:", error);
            messageElement.textContent = 'Lỗi kết nối. Vui lòng thử lại.';
            messageElement.className = 'validation-message error';
        } finally {
            // Hide loading
            loadingElement.style.display = 'none';
            validateBtn.disabled = false;
        }
    }

    function confirmVIPSelection() {
        if (!window.vipValidationResult || !window.vipValidationResult.isValid || !window.currentVIPRow) {
            return;
        }
        
        // Set VIP data to the row
        window.currentVIPRow.dataset.vipValidated = "true";
        window.currentVIPRow.dataset.vipDiscountPercentage = window.vipValidationResult.discountPercentage;
        window.currentVIPRow.dataset.vipTypeName = window.vipValidationResult.vipTypeName;
        window.currentVIPRow.dataset.vipIcon = window.vipValidationResult.vipIcon;
        window.currentVIPRow.dataset.vipMemberName = window.vipValidationResult.fullName;
        
        // Auto-fill passenger name if available
        const nameInput = window.currentVIPRow.querySelector('.passenger-fullName');
        if (nameInput && window.vipValidationResult.fullName) {
            nameInput.value = window.vipValidationResult.fullName;
        }
        
        // Update passenger type to "Người lớn" (Adult) since VIP members are typically adults
        const passengerTypeSelect = window.currentVIPRow.querySelector('.passenger-type-selector');
        if (passengerTypeSelect) {
            // Find and select "Người lớn" or similar adult type
            for (let i = 0; i < passengerTypeSelect.options.length; i++) {
                const option = passengerTypeSelect.options[i];
                if (option.textContent.includes("Người lớn") || option.textContent.includes("Adult")) {
                    passengerTypeSelect.selectedIndex = i;
                    break;
                }
            }
        }
        
        // Auto-fill ID card number from the validated VIP credential
        const idCardInput = window.currentVIPRow.querySelector('.passenger-idCardNumber');
        if (idCardInput) {
            const validatedIdCardNumber = document.getElementById('vipIdInput').value.trim();
            idCardInput.value = validatedIdCardNumber;
            idCardInput.style.display = 'block'; // Make sure it's visible
            idCardInput.required = true;
        }
        
        // Add VIP indicator to the passenger type cell
        const passengerTypeCell = window.currentVIPRow.querySelector('.passenger-name-type-cell');
        if (passengerTypeCell) {
            // Remove existing VIP indicator
            const existingIndicator = passengerTypeCell.querySelector('.vip-member-indicator');
            if (existingIndicator) {
                existingIndicator.remove();
            }
            
            // Add new VIP indicator
            const vipIndicator = document.createElement('div');
            vipIndicator.className = 'vip-member-indicator';
            vipIndicator.innerHTML = `${window.vipValidationResult.vipIcon} VIP ${window.vipValidationResult.vipTypeName} (-${window.vipValidationResult.discountPercentage}%)`;
            vipIndicator.style.cssText = `
                font-size: 0.8em;
                color: #B8860B;
                font-weight: bold;
                margin-top: 2px;
                padding: 2px 4px;
                background: linear-gradient(135deg, #FFD700, #FFA500);
                border-radius: 3px;
                display: inline-block;
            `;
            passengerTypeCell.appendChild(vipIndicator);
        }
        
        // Recalculate price with VIP discount
        if (passengerTypeSelect) {
            handlePassengerTypeChange(passengerTypeSelect);
        }
        
        // Close modal
        closeVIPModal();
    }

    // Make VIP functions globally available
    window.showVIPModal = showVIPModal;
    window.closeVIPModal = closeVIPModal;
    window.validateVIPCredentials = validateVIPCredentials;
    window.confirmVIPSelection = confirmVIPSelection;

    // Handle Enter key in VIP input
    document.addEventListener('DOMContentLoaded', function() {
        const vipInput = document.getElementById('vipIdInput');
        if (vipInput) {
            vipInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    validateVIPCredentials();
                }
            });
        }
    });

    // --- START ---
    initializePage();
});
