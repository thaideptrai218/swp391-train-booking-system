document.addEventListener("DOMContentLoaded", function () {
    const SESSION_STORAGE_CART_KEY = "VNR_userShoppingCart";
    const MAX_PASSENGERS = 10;
    const PAYMENT_TIMEOUT_DURATION_MS = 5 * 60 * 1000; // 5 minutes

    // DOM Elements
    const passengerDetailsBody = document.getElementById("passenger-details-body");
    const passengerRowTemplate = document.getElementById("passenger-row-template");
    const noPassengersRow = document.getElementById("no-passengers-row");
    const passengerCountSpan = document.getElementById("passenger-count");
    const totalPaymentAmountSpan = document.getElementById("total-payment-amount");
    const paymentCountdownSpan = document.getElementById("payment-countdown");
    const paymentForm = document.getElementById("paymentForm");
    const generalErrorMessage = document.getElementById("general-error-message");

    // Customer Info Fields
    const customerFullNameInput = document.getElementById("customerFullName");
    const customerEmailInput = document.getElementById("customerEmail");
    const customerEmailConfirmInput = document.getElementById("customerEmailConfirm");
    const customerPhoneInput = document.getElementById("customerPhone");
    const customerIDCardInput = document.getElementById("customerIDCard");
    const agreeTermsCheckbox = document.getElementById("agreeTerms");
    const submitPaymentButton = document.getElementById("submitPaymentButton");

    let shoppingCart = [];
    let paymentTimerInterval;

    // --- INITIALIZATION ---
    function initializePage() {
        loadShoppingCart();
        if (!validateCartOnLoad()) {
            return; // Stop further processing if initial cart validation fails
        }
        populatePassengerTypes(); // Ensure passenger types are available for dynamic rows
        renderPassengerRows();
        updateTotalPayment();
        startPaymentTimer();
        setupEventListeners();
    }

    function loadShoppingCart() {
        const storedCart = sessionStorage.getItem(SESSION_STORAGE_CART_KEY);
        if (storedCart) {
            try {
                shoppingCart = JSON.parse(storedCart);
                // Optional: Filter out expired holds if individual timers were still running
                // and somehow persisted to this page. For now, assume backend handles final validity.
            } catch (e) {
                console.error("Error parsing shopping cart from session storage:", e);
                shoppingCart = [];
                displayGeneralError("Lỗi khi tải giỏ hàng. Vui lòng thử lại.");
            }
        }
    }

    function validateCartOnLoad() {
        if (shoppingCart.length === 0) {
            if (noPassengersRow) noPassengersRow.style.display = "table-row";
            displayGeneralError("Không có vé nào trong giỏ hàng. Vui lòng quay lại để chọn vé.");
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            return false;
        }
        if (shoppingCart.length > MAX_PASSENGERS) {
            displayGeneralError(`Bạn chỉ có thể đặt tối đa ${MAX_PASSENGERS} vé. Hiện tại có ${shoppingCart.length} vé.`);
            if (submitPaymentButton) submitPaymentButton.disabled = true;
            // Consider redirecting or more drastic action
            return false;
        }
        if (passengerCountSpan) passengerCountSpan.textContent = shoppingCart.length;
        return true;
    }
    
    function populatePassengerTypes() {
        // This function assumes `passengerTypesData` is globally available from JSP
        // and populates the <select> in the template if needed, or ensures data is ready.
        // For now, we assume passengerTypesData is correctly populated in the JSP script tag.
        if (!window.passengerTypesData || window.passengerTypesData.length === 0) {
            console.warn("Passenger types data is not available. Discounts and type-specific logic may fail.");
            // Potentially fetch it via AJAX if not provided by JSP
        }
    }

    // --- RENDERING PASSENGER ROWS ---
    function renderPassengerRows() {
        if (!passengerDetailsBody || !passengerRowTemplate) return;
        passengerDetailsBody.innerHTML = ''; // Clear existing (except template)

        if (shoppingCart.length === 0) {
            if (noPassengersRow) noPassengersRow.style.display = "table-row";
            return;
        }
        if (noPassengersRow) noPassengersRow.style.display = "none";


        shoppingCart.forEach((cartItem, index) => {
            const newRow = passengerRowTemplate.content.cloneNode(true).querySelector("tr");
            newRow.dataset.cartItemIndex = index; // Link row to cart item

            // Populate passenger type dropdown
            const passengerTypeSelect = newRow.querySelector(".passenger-type-selector");
            if (window.passengerTypesData && passengerTypeSelect) {
                window.passengerTypesData.forEach(ptype => {
                    const option = document.createElement("option");
                    option.value = ptype.passengerTypeID;
                    option.textContent = ptype.typeName;
                    option.dataset.discountPercentage = ptype.discountPercentage;
                    option.dataset.requiresDocument = ptype.requiresDocument;
                    // A more robust way to check for child: by typeName or a dedicated flag from backend
                    option.dataset.isChild = (ptype.typeName === 'Trẻ em' || ptype.typeName === 'Em bé'); 
                    passengerTypeSelect.appendChild(option);
                });
            }
            
            // Populate seat information
            const seatInfoCell = newRow.querySelector(".seat-info-cell");
            if (seatInfoCell) {
                seatInfoCell.querySelector(".seat-train-route").textContent = `${cartItem.trainName || 'N/A'}: ${cartItem.originStationName || 'N/A'} - ${cartItem.destinationStationName || 'N/A'}`;
                seatInfoCell.querySelector(".seat-departure-datetime").textContent = `Khởi hành: ${cartItem.scheduledDepartureDisplay || 'N/A'}`;
                seatInfoCell.querySelector(".seat-coach-seat").textContent = `Toa ${cartItem.coachPosition || 'N/A'}, Ghế ${cartItem.seatName || 'N/A'} (${cartItem.seatNumberInCoach || 'N/A'})`;
                // seatInfoCell.querySelector(".seat-description").textContent = cartItem.seatDescription || ''; // If available
                
                // Individual seat hold timer (if applicable and data exists)
                const holdTimerSpan = seatInfoCell.querySelector(".seat-hold-timer");
                if (cartItem.holdExpiresAt && holdTimerSpan) {
                    startIndividualSeatTimer(cartItem, holdTimerSpan, index);
                } else if (holdTimerSpan) {
                    holdTimerSpan.textContent = ''; // No specific hold info for this item on this page
                }
            }

            const originalPriceCell = newRow.querySelector(".original-price-cell");
            if (originalPriceCell) {
                originalPriceCell.textContent = formatCurrency(cartItem.calculatedPrice);
                originalPriceCell.dataset.basePrice = cartItem.calculatedPrice;
            }
            
            // Set initial final price same as original, discount is 0
            const finalPriceCell = newRow.querySelector(".final-price-cell");
            if (finalPriceCell) finalPriceCell.textContent = formatCurrency(cartItem.calculatedPrice);


            passengerDetailsBody.appendChild(newRow);
        });
    }

    // --- EVENT LISTENERS ---
    function setupEventListeners() {
        if (paymentForm) {
            paymentForm.addEventListener("submit", handleFormSubmission);
        }

        passengerDetailsBody.addEventListener("change", function(event) {
            if (event.target.classList.contains("passenger-type-selector")) {
                handlePassengerTypeChange(event.target);
            }
        });
        
        // Real-time validation for customer inputs (optional, can be on blur)
        customerEmailConfirmInput?.addEventListener('input', () => validateEmailConfirmation(true));

    }

    function handlePassengerTypeChange(selectElement) {
        const selectedOption = selectElement.options[selectElement.selectedIndex];
        const row = selectElement.closest("tr");
        if (!row || !selectedOption) return;

        const idCardInput = row.querySelector(".passenger-idCardNumber");
        const dobInput = row.querySelector(".passenger-dateOfBirth");
        const discountCell = row.querySelector(".discount-amount-cell");
        const finalPriceCell = row.querySelector(".final-price-cell");
        const originalPriceCell = row.querySelector(".original-price-cell");
        
        const basePrice = parseFloat(originalPriceCell.dataset.basePrice || "0");
        const discountPercentage = parseFloat(selectedOption.dataset.discountPercentage || "0");
        const requiresDocument = selectedOption.dataset.requiresDocument === "true";
        const isChild = selectedOption.dataset.isChild === "true";

        if (idCardInput) {
            idCardInput.style.display = requiresDocument && !isChild ? "block" : "none";
            idCardInput.required = requiresDocument && !isChild;
        }
        if (dobInput) {
            dobInput.style.display = isChild ? "block" : "none";
            dobInput.required = isChild;
             if (isChild) { // Set max date for DOB to today for children
                const today = new Date().toISOString().split('T')[0];
                dobInput.max = today;
            }
        }

        const discountAmount = basePrice * (discountPercentage / 100);
        const finalPrice = basePrice - discountAmount;

        if (discountCell) discountCell.textContent = formatCurrency(discountAmount);
        if (finalPriceCell) finalPriceCell.textContent = formatCurrency(finalPrice);
        
        updateTotalPayment();
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
        paymentCountdownSpan.textContent = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    }

    async function handlePaymentTimeout() {
        displayGeneralError("Đã hết thời gian thanh toán. Các vé bạn chọn đã được hủy. Vui lòng thực hiện lại.");
        if (submitPaymentButton) submitPaymentButton.disabled = true;
        // Disable all form inputs
        paymentForm.querySelectorAll("input, select, button").forEach(el => el.disabled = true);

        // Attempt to release seats on backend
        if (shoppingCart.length > 0) {
            const seatsToRelease = shoppingCart.map(item => ({
                tripId: item.tripId,
                seatId: item.seatID,
                legOriginStationId: item.legOriginStationId, // Ensure these are in cartItem
                legDestinationStationId: item.legDestinationStationId // Ensure these are in cartItem
            }));
            try {
                // Assuming contextPath is globally available (from JSP)
                const response = await fetch(`${contextPath}/api/seats/releaseMultiple`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(seatsToRelease)
                });
                if (response.ok) {
                    console.log("Seats released on backend due to payment timeout.");
                } else {
                    console.error("Failed to release seats on backend after payment timeout.");
                }
            } catch (error) {
                console.error("Error releasing seats on backend:", error);
            }
        }
        sessionStorage.removeItem(SESSION_STORAGE_CART_KEY);
        shoppingCart = [];
    }
    
    // --- INDIVIDUAL SEAT HOLD TIMER (if needed on this page) ---
    function startIndividualSeatTimer(cartItem, timerSpan, cartItemIndex) {
        // This is more complex if holds from trip-result.js are still "live"
        // For now, this is a placeholder or assumes holds are "soft-reserved"
        // by the backend for the main payment duration.
        // If true live individual timers are needed here, it's a deeper integration.
        // For simplicity, let's assume the backend handles the hold validity during final payment.
        // This span can show the original hold time if desired, but not actively count down to removal here.
        if (cartItem.holdExpiresAt) {
            const expiresAtDate = new Date(cartItem.holdExpiresAt);
            const now = new Date();
            const timeLeftMs = expiresAtDate.getTime() - now.getTime();
            if (timeLeftMs > 0) {
                 const minutes = Math.floor(timeLeftMs / 60000);
                 const seconds = Math.floor((timeLeftMs % 60000) / 1000);
                 timerSpan.textContent = `(Giữ đến ${minutes}:${String(seconds).padStart(2, '0')})`;
            } else {
                timerSpan.textContent = "(Đã hết hạn giữ)";
                // Potentially mark this row as invalid or remove from cart
            }
        }
    }

    // --- CALCULATIONS ---
    function updateTotalPayment() {
        let total = 0;
        const rows = passengerDetailsBody.querySelectorAll("tr");
        rows.forEach(row => {
            if (row.id === 'no-passengers-row' && row.style.display !== 'none') return;
            if (!row.querySelector(".final-price-cell")) return; // Skip if it's not a proper passenger row

            const finalPriceText = row.querySelector(".final-price-cell").textContent;
            total += parseCurrency(finalPriceText);
        });
        if (totalPaymentAmountSpan) totalPaymentAmountSpan.textContent = formatCurrency(total);
    }

    // --- FORM VALIDATION & SUBMISSION ---
    function validateForm() {
        let isValid = true;
        clearAllErrors();

        // Validate passenger details
        const passengerRows = passengerDetailsBody.querySelectorAll("tr");
        passengerRows.forEach((row, index) => {
            if (row.id === 'no-passengers-row') return;
            if (!row.querySelector(".passenger-fullName")) return;


            const fullNameInput = row.querySelector(".passenger-fullName");
            const passengerTypeSelect = row.querySelector(".passenger-type-selector");
            const idCardInput = row.querySelector(".passenger-idCardNumber");
            const dobInput = row.querySelector(".passenger-dateOfBirth");

            if (!fullNameInput.value.trim()) {
                displayError(row.querySelector(".fullNameError"), "Vui lòng nhập họ tên.");
                isValid = false;
            }
            if (!passengerTypeSelect.value) {
                displayError(row.querySelector(".passengerTypeError"), "Vui lòng chọn đối tượng.");
                isValid = false;
            }
            if (idCardInput.required && !idCardInput.value.trim()) {
                displayError(row.querySelector(".idCardNumberError"), "Vui lòng nhập số CMND/CCCD.");
                isValid = false;
            } else if (idCardInput.required && idCardInput.value.trim() && !/^\d{9,12}$/.test(idCardInput.value.trim())) {
                displayError(row.querySelector(".idCardNumberError"), "Số CMND/CCCD không hợp lệ (9-12 số).");
                isValid = false;
            }

            if (dobInput.required && !dobInput.value) {
                displayError(row.querySelector(".dateOfBirthError"), "Vui lòng chọn ngày sinh.");
                isValid = false;
            } else if (dobInput.required && dobInput.value) {
                const dob = new Date(dobInput.value);
                const today = new Date();
                today.setHours(0,0,0,0); // Compare dates only
                if (dob > today) {
                    displayError(row.querySelector(".dateOfBirthError"), "Ngày sinh không thể ở tương lai.");
                    isValid = false;
                }
            }
        });

        // Validate customer details
        if (!customerFullNameInput.value.trim()) {
            displayError(document.getElementById("customerFullNameError"), "Vui lòng nhập họ tên người đặt vé.");
            isValid = false;
        }
        if (!customerEmailInput.value.trim()) {
            displayError(document.getElementById("customerEmailError"), "Vui lòng nhập email.");
            isValid = false;
        } else if (!/\S+@\S+\.\S+/.test(customerEmailInput.value.trim())) {
            displayError(document.getElementById("customerEmailError"), "Email không hợp lệ.");
            isValid = false;
        }
        if (!validateEmailConfirmation(false)) { // Pass false to not show error if empty yet
             isValid = false;
        }
        if (!customerPhoneInput.value.trim()) {
            displayError(document.getElementById("customerPhoneError"), "Vui lòng nhập số điện thoại.");
            isValid = false;
        } else if (!/^\d{10,11}$/.test(customerPhoneInput.value.trim())) {
            displayError(document.getElementById("customerPhoneError"), "Số điện thoại không hợp lệ (10-11 số).");
            isValid = false;
        }
        if (customerIDCardInput.value.trim() && !/^\d{9,12}$/.test(customerIDCardInput.value.trim())) {
             displayError(document.getElementById("customerIDCardError"), "Số CMND/CCCD người đặt vé không hợp lệ (9-12 số).");
            isValid = false;
        }


        if (!agreeTermsCheckbox.checked) {
            displayError(document.getElementById("agreeTermsError"), "Bạn phải đồng ý với điều khoản.");
            isValid = false;
        }
        
        return isValid;
    }
    
    function validateEmailConfirmation(showErrorIfEmptyConfirm) {
        const emailErrorSpan = document.getElementById("customerEmailConfirmError");
        if (!customerEmailConfirmInput.value.trim() && !showErrorIfEmptyConfirm && !customerEmailInput.value.trim()) {
            // If both are empty and we are not forced to show error for confirm, it's fine for now
            clearError(emailErrorSpan);
            return true;
        }
        if (!customerEmailConfirmInput.value.trim()) {
             displayError(emailErrorSpan, "Vui lòng nhập lại email.");
             return false;
        }
        if (customerEmailInput.value.trim() !== customerEmailConfirmInput.value.trim()) {
            displayError(emailErrorSpan, "Email nhập lại không khớp.");
            return false;
        }
        clearError(emailErrorSpan);
        return true;
    }


    async function handleFormSubmission(event) {
        event.preventDefault();
        if (!validateForm()) {
            displayGeneralError("Vui lòng kiểm tra lại các thông tin đã nhập.");
            return;
        }
        displayGeneralError(""); // Clear general error

        submitPaymentButton.disabled = true;
        submitPaymentButton.textContent = "Đang xử lý...";

        const passengerData = [];
        const passengerRows = passengerDetailsBody.querySelectorAll("tr");
        passengerRows.forEach((row, cartIndex) => {
            if (row.id === 'no-passengers-row' || !row.querySelector(".passenger-fullName")) return;

            const cartItem = shoppingCart[row.dataset.cartItemIndex]; // Get original cart item
            passengerData.push({
                originalCartItem: cartItem, // Include original seat details, price, tripId etc.
                fullName: row.querySelector(".passenger-fullName").value.trim(),
                passengerTypeID: row.querySelector(".passenger-type-selector").value,
                idCardNumber: row.querySelector(".passenger-idCardNumber").style.display !== 'none' ? row.querySelector(".passenger-idCardNumber").value.trim() : null,
                dateOfBirth: row.querySelector(".passenger-dateOfBirth").style.display !== 'none' ? row.querySelector(".passenger-dateOfBirth").value : null,
            });
        });

        const paymentPayload = {
            customerDetails: {
                fullName: customerFullNameInput.value.trim(),
                email: customerEmailInput.value.trim(),
                phoneNumber: customerPhoneInput.value.trim(),
                idCardNumber: customerIDCardInput.value.trim() || null,
            },
            passengers: passengerData,
            paymentMethod: document.getElementById("paymentMethod").value,
            totalAmount: parseCurrency(totalPaymentAmountSpan.textContent)
            // shoppingCart: shoppingCart // Send the whole cart for backend to re-verify prices/availability
        };

        console.log("Payment Payload:", JSON.stringify(paymentPayload, null, 2));

        try {
            // Assuming contextPath is globally available
            const response = await fetch(`${contextPath}/processPaymentServlet`, { // Ensure this servlet path is correct
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(paymentPayload)
            });

            const responseData = await response.json();

            if (response.ok && responseData.status === "success") {
                clearInterval(paymentTimerInterval); // Stop payment timer
                sessionStorage.removeItem(SESSION_STORAGE_CART_KEY); // Clear cart
                // Redirect to success page or display success message
                // window.location.href = responseData.redirectUrl || `${contextPath}/bookingSuccess.jsp`;
                displayGeneralError(`Đặt vé thành công! Mã đặt vé: ${responseData.bookingCode || ''}. Sẽ chuyển hướng sau 3 giây...`, false);
                setTimeout(() => {
                     window.location.href = responseData.redirectUrl || `${contextPath}/bookingConfirmation.jsp?bookingCode=${responseData.bookingCode}`;
                }, 3000);

            } else {
                displayGeneralError(`Lỗi: ${responseData.message || "Không thể xử lý thanh toán. Vui lòng thử lại."}`);
                submitPaymentButton.disabled = false;
                submitPaymentButton.textContent = "Thanh toán";
            }
        } catch (error) {
            console.error("Payment submission error:", error);
            displayGeneralError("Lỗi kết nối khi thực hiện thanh toán. Vui lòng thử lại.");
            submitPaymentButton.disabled = false;
            submitPaymentButton.textContent = "Thanh toán";
        }
    }

    // --- UTILITY FUNCTIONS ---
    function formatCurrency(value) {
        if (isNaN(parseFloat(value))) return "0";
        return parseFloat(value).toLocaleString('vi-VN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }

    function parseCurrency(value) {
        if (typeof value !== 'string') return 0;
        return parseFloat(value.replace(/\./g, '').replace(/,/g, '.')) || 0; // Assuming vi-VN format like 1.000.000
    }
    
    function displayError(element, message) {
        if (element) {
            element.textContent = message;
            element.style.display = 'block'; // Or 'inline' depending on span
        }
    }

    function clearError(element) {
        if (element) {
            element.textContent = '';
            element.style.display = 'none';
        }
    }
    
    function clearAllErrors() {
        document.querySelectorAll(".error-message").forEach(span => clearError(span));
    }
    
    function displayGeneralError(message, isError = true) {
        if(generalErrorMessage) {
            generalErrorMessage.textContent = message;
            generalErrorMessage.style.color = isError ? 'red' : 'green';
        }
    }

    // --- START ---
    initializePage();
});
