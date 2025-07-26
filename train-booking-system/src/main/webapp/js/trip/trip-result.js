const bookingController = {
    // --- Constants ---
    SESSION_STORAGE_CART_KEY: "VNR_userShoppingCart",
    HOLD_DURATION_MS: 5 * 60 * 1000, // 5 minutes

    // --- State ---
    contextPath: "/train-booking-system",
    shoppingCart: [],
    seatHoldTimers: {},
    currentCarriage: null,
    lastFetchCoachLayoutParams: null,

    // --- Initialization ---
    init() {
        document.addEventListener("DOMContentLoaded", () => {
            this.loadCartFromSession();
            this.updateCartDisplay();
            this.initEventListeners();
        });
    },

    initEventListeners() {
        const trainItems = document.querySelectorAll(".train-item");
        trainItems.forEach((item) => {
            const collapsedSummary = item.querySelector(".train-item-collapsed-summary");
            if (collapsedSummary) {
                collapsedSummary.addEventListener("click", () => this.toggleTrainItemDetails(item));
            }

            const expandedDetails = item.querySelector(".expanded-details");
            if (expandedDetails) {
                expandedDetails.addEventListener("click", (event) => {
                    const carriageItem = event.target.closest(".carriage-item");
                    if (carriageItem && carriageItem !== this.currentCarriage) {
                        this.selectCarriage(carriageItem, item);
                        this.currentCarriage = carriageItem;
                    }
                });
            }
        });
    },

    // --- Cart Management ---
    loadCartFromSession() {
        const storedCart = sessionStorage.getItem(this.SESSION_STORAGE_CART_KEY);
        if (!storedCart) return;

        try {
            const parsedCart = JSON.parse(storedCart);
            const now = new Date();
            const validItems = parsedCart.filter(item => {
                if (!item.holdExpiresAt) return false;
                const expiresAtDate = new Date(item.holdExpiresAt);
                return expiresAtDate > now;
            });

            this.shoppingCart = validItems;
            this.shoppingCart.forEach(item => {
                this.startSeatHoldTimer(item);
                const trainItemElem = document.querySelector(`.train-item[data-trip-id="${item.tripId}"]`);
                if (trainItemElem) {
                    const seatElementOnMap = trainItemElem.querySelector(`.seat[data-seat-id="${item.seatID}"]`);
                    if (seatElementOnMap) {
                        seatElementOnMap.classList.remove("available");
                        seatElementOnMap.classList.add("selected");
                    }
                }
            });
        } catch (e) {
            console.error("Error parsing shopping cart from session storage:", e);
            sessionStorage.removeItem(this.SESSION_STORAGE_CART_KEY);
        }
    },

    saveCartToSession() {
        sessionStorage.setItem(this.SESSION_STORAGE_CART_KEY, JSON.stringify(this.shoppingCart));
    },

    updateItemHoldExpiration() {
        const now = new Date();
        this.shoppingCart.forEach(item => {
            if (item.holdExpiresAt) {
                item.holdExpiresAt = new Date(now.getTime() + this.HOLD_DURATION_MS).toISOString();
            }
        });
        this.saveCartToSession();
    },

    addToCart(seatData, tripId) {
        const cartItem = { ...seatData, tripId };
        if (!this.shoppingCart.find(item => item.seatID === cartItem.seatID && item.tripId === cartItem.tripId)) {
            this.shoppingCart.push(cartItem);
        }
        this.updateCartDisplay();
        this.saveCartToSession();
    },

    // --- UI Updates ---
    toggleTrainItemDetails(trainItemElement) {
        const details = trainItemElement.querySelector(".expanded-details");
        const isExpanded = details.style.display === "block";
        details.style.display = isExpanded ? "none" : "block";
        trainItemElement.classList.toggle("expanded", !isExpanded);
    },

    selectCarriage(selectedCarriageElement, trainItemElement) {
        const compositionDisplay = selectedCarriageElement.closest(".train-composition-display");
        if (!compositionDisplay) return;

        compositionDisplay.querySelectorAll(".carriage-item").forEach(c => c.classList.remove("active"));
        selectedCarriageElement.classList.add("active");

        const coachData = selectedCarriageElement.dataset;
        const tripData = trainItemElement.dataset;

        const coachLayoutInfo = {
            capacity: parseInt(coachData.coachCapacity),
            isCompartmented: coachData.coachIsCompartmented === "true",
            defaultCompartmentCapacity: coachData.coachDefaultCompartmentCapacity ? parseInt(coachData.coachDefaultCompartmentCapacity) : null,
            coachTypeName: coachData.coachTypename,
        };

        const carriageContext = {
            coachId: coachData.coachId,
            coachPosition: coachData.coachPosition,
            tripLeg: coachData.tripLeg,
        };

        this.updateCarriageDescription(coachData);
        this.fetchAndRenderCoachLayout(
            coachData.tripId,
            coachData.coachId,
            tripData.legOriginStationId,
            tripData.legDestStationId,
            coachLayoutInfo,
            carriageContext,
            trainItemElement
        );
    },

    updateCarriageDescription(coachData) {
        const descriptionBlockId = `carriageDescription-${coachData.tripLeg}-${coachData.tripId}`;
        const descriptionBlock = document.getElementById(descriptionBlockId);
        if (descriptionBlock) {
            let descHtml = `<strong>${coachData.coachTypename} - Toa ${coachData.coachPosition}</strong>`;
            if (coachData.coachDescription && coachData.coachDescription !== "null" && coachData.coachDescription.trim() !== "") {
                descHtml += `<br>${coachData.coachDescription}`;
            }
            descriptionBlock.innerHTML = `<p>${descHtml}</p>`;
        }
    },

    // --- Seat Layout Generation ---
    generateSeatLayout(seatDetailsBlock, seatDataList, coachLayoutInfo, carriageContext) {
        seatDetailsBlock.innerHTML = ""; // Clear loading message

        if (!seatDataList || seatDataList.length === 0) {
            seatDetailsBlock.innerHTML = "<p>Không có thông tin ghế cho toa này.</p>";
            return;
        }

        if (coachLayoutInfo.isCompartmented && coachLayoutInfo.defaultCompartmentCapacity > 0) {
            this.generateCompartmentLayout(seatDetailsBlock, seatDataList, coachLayoutInfo);
        } else {
            this.generateOpenSeatingLayout(seatDetailsBlock, seatDataList);
        }
        this.addSeatClickListeners(seatDetailsBlock, carriageContext);
    },

    generateOpenSeatingLayout(seatDetailsBlock, seatDataList) {
        const seatGrid = document.createElement("div");
        seatGrid.className = "seat-grid open-seating-grid";
        const seatsPerRowVisual = 14;
        const aisleAfterIndex = 6;

        let currentVisualRow = document.createElement("div");
        currentVisualRow.className = "seat-row";
        seatGrid.appendChild(currentVisualRow);
        let seatsInCurrentVisualRow = 0;

        seatDataList.forEach(seatDto => {
            if (seatsInCurrentVisualRow === seatsPerRowVisual) {
                currentVisualRow = document.createElement("div");
                currentVisualRow.className = "seat-row";
                seatGrid.appendChild(currentVisualRow);
                seatsInCurrentVisualRow = 0;
            }

            currentVisualRow.appendChild(this.createSeatElement(seatDto));
            seatsInCurrentVisualRow++;

            if (seatsInCurrentVisualRow === aisleAfterIndex + 1 && seatsInCurrentVisualRow < seatsPerRowVisual) {
                const aisleDiv = document.createElement("div");
                aisleDiv.className = "aisle-spacer";
                currentVisualRow.appendChild(aisleDiv);
            }
        });
        seatDetailsBlock.appendChild(seatGrid);
    },

    generateCompartmentLayout(seatDetailsBlock, seatDataList, coachLayoutInfo) {
        const compartmentGrid = document.createElement("div");
        compartmentGrid.className = "seat-grid compartment-grid";
        const numCompartments = Math.ceil(coachLayoutInfo.capacity / coachLayoutInfo.defaultCompartmentCapacity);
        let seatIndex = 0;

        const berthLevels = document.createElement("div");
        berthLevels.className = "berth-levels";
        for (let i = 0; i < coachLayoutInfo.defaultCompartmentCapacity / 2; i++) {
            const berthLevelDiv = document.createElement("div");
            berthLevelDiv.className = `berth-level berth-${i + 1}`;
            berthLevelDiv.textContent = `T${i + 1}`;
            berthLevels.appendChild(berthLevelDiv);
        }
        compartmentGrid.appendChild(berthLevels);

        for (let i = 0; i < numCompartments; i++) {
            const compartmentDiv = document.createElement("div");
            compartmentDiv.className = "compartment";
            compartmentDiv.innerHTML = `<div class="compartment-label">Khoang ${i + 1}</div>`;

            const berthsContainer = document.createElement("div");
            berthsContainer.className = "berths-container";

            for (let j = 0; j < 2; j++) {
                const berthItem = document.createElement("div");
                berthItem.className = "berth-item";
                for (let k = 0; k < coachLayoutInfo.defaultCompartmentCapacity / 2; k++) {
                    if (seatIndex < seatDataList.length) {
                        const seatDto = seatDataList[seatIndex];
                        const seatDiv = this.createSeatElement(seatDto);
                        if (seatDto.berthLevel) {
                            seatDiv.classList.add(`berth-${seatDto.berthLevel.toLowerCase()}`);
                        }
                        berthItem.appendChild(seatDiv);
                        seatIndex++;
                    }
                }
                berthsContainer.appendChild(berthItem);
            }
            compartmentDiv.appendChild(berthsContainer);
            compartmentGrid.appendChild(compartmentDiv);
        }
        seatDetailsBlock.appendChild(compartmentGrid);
    },

    createSeatElement(seatDto) {
        const seatDiv = document.createElement("div");
        seatDiv.className = "seat";
        seatDiv.dataset.seatId = seatDto.seatID;
        seatDiv.dataset.seatName = seatDto.seatName;
        seatDiv.dataset.seatInfo = JSON.stringify(seatDto);
        seatDiv.textContent = seatDto.seatNumberInCoach;

        let titleText = seatDto.availabilityStatus || "Trạng thái không xác định";
        if (seatDto.availabilityStatus?.toLowerCase() === "available" && seatDto.calculatedPrice !== null && seatDto.enabled) {
            titleText += `: ${seatDto.calculatedPrice.toLocaleString("vi-VN")} VND`;
        }
        seatDiv.title = titleText;

        if (!seatDto.enabled) {
            seatDiv.classList.add("disabled");
        } else {
            switch (seatDto.availabilityStatus.toLowerCase()) {
                case "available": seatDiv.classList.add("available"); break;
                case "occupied": seatDiv.classList.add("occupied"); break;
                case "heldbyyou": seatDiv.classList.add("selected"); break;
                case "heldbyother": seatDiv.classList.add("occupied"); break;
                case "disabled": seatDiv.classList.add("disabled"); break;
                default: seatDiv.classList.add("unavailable");
            }
        }
        return seatDiv;
    },

    // --- API Calls ---
    fetchAndRenderCoachLayout(tripId, coachId, legOriginStationId, legDestinationStationId, coachLayoutInfo, carriageContext, trainItemElement) {
        const seatDetailsBlockId = `seatDetailsBlock-${carriageContext.tripLeg}-${tripId}`;
        const seatDetailsBlock = document.getElementById(seatDetailsBlockId);
        if (!seatDetailsBlock) return;

        this.lastFetchCoachLayoutParams = { tripId, coachId, legOriginStationId, legDestinationStationId, coachLayoutInfo, carriageContext, trainItemElement };
        seatDetailsBlock.innerHTML = "<p>Đang làm mới sơ đồ ghế...</p>";

        const bookingDateTimeISO = new Date().toISOString().slice(0, 19);
        const isRoundTripForFetch = trainItemElement.closest(".main-results-content")?.dataset.isRoundTrip === "true";
        const fetchUrl = `${this.contextPath}/api/seats/getSeat?tripId=${tripId}&coachId=${coachId}&legOriginStationId=${legOriginStationId}&legDestinationStationId=${legDestinationStationId}&bookingDateTime=${encodeURIComponent(bookingDateTimeISO)}&isRoundTrip=${isRoundTripForFetch}`;

        fetch(fetchUrl)
            .then(response => {
                if (!response.ok) return response.json().then(err => { throw new Error(err.error || "Lỗi mạng khi tải ghế."); });
                return response.json();
            })
            .then(seatDataList => this.generateSeatLayout(seatDetailsBlock, seatDataList, coachLayoutInfo, carriageContext))
            .catch(error => {
                console.error("Error refreshing seat data:", error);
                seatDetailsBlock.innerHTML = `<p>Lỗi khi làm mới sơ đồ ghế: ${error.message}</p>`;
            });
    },

    async handleSeatSelectionAttempt(seatDataForCart, seatElement) {
        seatElement.classList.add("pending-hold");
        try {
            const response = await fetch(`${this.contextPath}/api/seats/hold`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    tripId: seatDataForCart.tripId,
                    seatId: seatDataForCart.seatID,
                    coachId: seatDataForCart.coachId,
                    legOriginStationId: seatDataForCart.legOriginStationId,
                    legDestinationStationId: seatDataForCart.legDestinationStationId,
                }),
            });

            const responseData = await response.json();
            seatElement.classList.remove("pending-hold");

            if (response.ok && responseData.status === "success") {
                seatElement.classList.add("selected");
                seatElement.classList.remove("available");
                const finalCartItem = { ...seatDataForCart, holdExpiresAt: responseData.data?.holdExpiresAt };
                this.addToCart(finalCartItem, finalCartItem.tripId);
            } else {
                alert(`Không thể giữ ghế ${seatDataForCart.seatName}: ${responseData.message || "Lỗi không xác định."}`);
                seatElement.classList.remove("selected", "pending-hold");
                seatElement.classList.add("available");
                const messageLowerCase = responseData.message?.toLowerCase() || "";
                if (messageLowerCase.includes("seat is already occupied") || messageLowerCase.includes("seat taken") || messageLowerCase.includes("held by another user")) {
                    if (this.lastFetchCoachLayoutParams) {
                        const p = this.lastFetchCoachLayoutParams;
                        this.fetchAndRenderCoachLayout(
                            p.tripId,
                            p.coachId,
                            p.legOriginStationId,
                            p.legDestinationStationId,
                            p.coachLayoutInfo,
                            p.carriageContext,
                            p.trainItemElement
                        );
                    }
                }
            }
        } catch (error) {
            seatElement.classList.remove("pending-hold", "selected");
            console.error("Error during seat hold API call:", error);
            alert("Lỗi kết nối khi cố gắng giữ ghế. Vui lòng thử lại.");
        }
    },

    async handleSeatDeselectionAttempt(cartItem, seatElementOnMap) {
        try {
            const response = await fetch(`${this.contextPath}/api/seats/release`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    tripId: cartItem.tripId,
                    seatId: cartItem.seatID,
                    legOriginStationId: cartItem.legOriginStationId,
                    legDestinationStationId: cartItem.legDestinationStationId,
                }),
            });
            if (!response.ok) {
                const responseData = await response.json();
                console.warn(`Failed to release seat ${cartItem.seatName} on server: ${responseData.message}`);
            }
        } catch (error) {
            console.error("Error during seat release API call:", error);
        } finally {
            this.clearTimerAndRemoveFromClientCart(cartItem.seatID, cartItem.tripId);
            if (seatElementOnMap) {
                seatElementOnMap.classList.remove("selected");
                seatElementOnMap.classList.add("available");
            }
        }
    },

    async initiateBookingProcess() {
        if (this.shoppingCart.length === 0) {
            alert("Vui lòng chọn ít nhất một vé để tiếp tục.");
            return;
        }
        if (this.shoppingCart.length > 10) {
            alert("Bạn chỉ có thể đặt tối đa 10 vé cùng một lúc.");
            return;
        }

        const seatsToBook = this.shoppingCart.map(item => ({
            tripId: item.tripId,
            seatId: item.seatID,
            coachId: item.coachId,
            legOriginStationId: item.legOriginStationId,
            legDestinationStationId: item.legDestinationStationId,
        }));

        try {
            const response = await fetch(`${this.contextPath}/api/booking/initiateBooking`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(seatsToBook),
            });
            const responseData = await response.json();

            if (response.ok && responseData.status === "success") {
                this.updateItemHoldExpiration();
                window.location.href = `${this.contextPath}/ticketPayment`;
            } else {
                alert(`Lỗi khi khởi tạo đặt vé: ${responseData.message || "Không thể tiếp tục, vui lòng thử lại."}`);
            }
        } catch (error) {
            console.error("Error initiating booking:", error);
            alert("Lỗi kết nối khi khởi tạo đặt vé. Vui lòng kiểm tra kết nối và thử lại.");
        }
    },

    // --- Timers and Expiration ---
    startSeatHoldTimer(cartItem) {
        const timerKey = `${cartItem.tripId}_${cartItem.seatID}`;
        if (this.seatHoldTimers[timerKey]) clearInterval(this.seatHoldTimers[timerKey]);

        const expiresAtDate = new Date(cartItem.holdExpiresAt);
        const intervalId = setInterval(() => {
            const now = new Date();
            const timeLeftSeconds = Math.round((expiresAtDate.getTime() - now.getTime()) / 1000);
            const listItemElement = document.querySelector(`.cart-item-entry[data-trip-id="${cartItem.tripId}"][data-seat-id="${cartItem.seatID}"]`);
            const timerDisplayElement = listItemElement?.querySelector(".cart-item-remove-icon");

            if (timeLeftSeconds > 0) {
                if (timerDisplayElement) {
                    const minutes = Math.floor(timeLeftSeconds / 60);
                    const seconds = timeLeftSeconds % 60;
                    timerDisplayElement.innerHTML = `<span class="timer-countdown" title="Thời gian giữ vé: ${minutes}m ${seconds}s">${minutes}:${seconds.toString().padStart(2, "0")}</span>`;
                    timerDisplayElement.classList.add("timer-active");
                }
            } else {
                clearInterval(intervalId);
                delete this.seatHoldTimers[timerKey];
                if (timerDisplayElement) {
                    timerDisplayElement.innerHTML = '<i class="fas fa-trash"></i>';
                    timerDisplayElement.classList.remove("timer-active");
                }
                this.handleHoldExpired(cartItem);
            }
        }, 1000);
        this.seatHoldTimers[timerKey] = intervalId;
    },

    async handleHoldExpired(expiredCartItem) {
        alert(`Thời gian giữ vé cho ghế ${expiredCartItem.seatName} (Tàu ${expiredCartItem.trainName || "N/A"}) đã hết. Vé đã được tự động hủy.`);
        try {
            await fetch(`${this.contextPath}/api/seats/release`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    tripId: expiredCartItem.tripId,
                    seatId: expiredCartItem.seatID,
                    legOriginStationId: expiredCartItem.legOriginStationId,
                    legDestinationStationId: expiredCartItem.legDestinationStationId,
                }),
            });
        } catch (error) {
            console.error("Error releasing expired seat hold on server:", error);
        }
        this.clearTimerAndRemoveFromClientCart(expiredCartItem.seatID, expiredCartItem.tripId);
        if (this.currentCarriage) {
            const trainItemElement = this.currentCarriage.closest(".train-item");
            if (trainItemElement) this.selectCarriage(this.currentCarriage, trainItemElement);
        }
    },

    clearTimerAndRemoveFromClientCart(seatId, tripId) {
        const timerKey = `${tripId}_${seatId}`;
        if (this.seatHoldTimers[timerKey]) {
            clearInterval(this.seatHoldTimers[timerKey]);
            delete this.seatHoldTimers[timerKey];
        }

        const itemIndex = this.shoppingCart.findIndex(item => item.seatID.toString() === seatId.toString() && item.tripId === tripId);
        if (itemIndex > -1) this.shoppingCart.splice(itemIndex, 1);

        const trainItemElem = document.querySelector(`.train-item[data-trip-id="${tripId}"]`);
        if (trainItemElem) {
            const seatElementOnMap = trainItemElem.querySelector(`.seat[data-seat-id="${seatId}"]`);
            if (seatElementOnMap) {
                seatElementOnMap.classList.add("available");
                seatElementOnMap.classList.remove("selected");
            }
        }
        this.updateCartDisplay();
        this.saveCartToSession();
    },

    // --- Event Listeners ---
    addSeatClickListeners(seatGridContainer, carriageContext) {
        const trainItemElement = seatGridContainer.closest(".train-item");
        if (!trainItemElement) return;

        const tripIdFromScope = trainItemElement.dataset.tripId;
        const legOriginStationIdFromScope = trainItemElement.dataset.legOriginStationId;
        const legDestinationStationIdFromScope = trainItemElement.dataset.legDestStationId;

        seatGridContainer.querySelectorAll(".seat").forEach(seatElement => {
            if (seatElement.classList.contains("disabled") || (seatElement.classList.contains("occupied") && !seatElement.classList.contains("selected"))) {
                return;
            }

            seatElement.addEventListener("click", async () => {
                const seatInfoString = seatElement.dataset.seatInfo;
                if (!seatInfoString) return;

                const seatDataObject = JSON.parse(seatInfoString);
                const effectiveTripId = tripIdFromScope || seatDataObject.tripId;
                if (!effectiveTripId) return;

                const isCurrentlySelectedOnUI = seatElement.classList.contains("selected");
                const cartItemExists = this.shoppingCart.find(item => item.seatID === seatDataObject.seatID && item.tripId === effectiveTripId);

                const seatDataForAction = {
                    ...seatDataObject,
                    tripId: effectiveTripId,
                    coachId: carriageContext?.coachId,
                    legOriginStationId: legOriginStationIdFromScope,
                    legDestinationStationId: legDestinationStationIdFromScope,
                    tripLeg: carriageContext?.tripLeg,
                    coachPosition: carriageContext?.coachPosition,
                    trainName: trainItemElement.querySelector(".train-item-collapsed-summary .train-name")?.textContent.trim(),
                    originStationName: trainItemElement.querySelector(".train-item-collapsed-summary .departure-info .trip-station")?.textContent.trim(),
                    destinationStationName: trainItemElement.querySelector(".train-item-collapsed-summary .arrival-info .trip-station")?.textContent.trim(),
                    scheduledDepartureDisplay: trainItemElement.querySelector(".train-item-collapsed-summary .departure-info .trip-time")?.textContent.trim(),
                };

                if (!seatDataForAction.coachId || !seatDataForAction.legOriginStationId || !seatDataForAction.legDestinationStationId) {
                    alert("Lỗi: Không đủ thông tin để xử lý yêu cầu cho ghế này. Vui lòng thử làm mới trang.");
                    return;
                }

                if (seatElement.classList.contains("available")) {
                    await this.handleSeatSelectionAttempt(seatDataForAction, seatElement);
                } else if (isCurrentlySelectedOnUI && cartItemExists) {
                    await this.handleSeatDeselectionAttempt(cartItemExists, seatElement);
                }
            });
        });
    },

    // --- Cart Display ---
    updateCartDisplay() {
        const cartPlaceholder = document.querySelector(".shopping-cart-placeholder");
        if (!cartPlaceholder) return;

        let cartSummaryDetails = cartPlaceholder.querySelector("#cart-summary-details");
        if (!cartSummaryDetails) {
            cartSummaryDetails = this.createCartSummaryDOM(cartPlaceholder);
        }

        const cartItemsListElement = cartSummaryDetails.querySelector("#cart-items-list");
        const proceedButton = cartSummaryDetails.querySelector("#proceed-to-booking-btn");
        const emptyMessageElement = cartItemsListElement.querySelector(".cart-empty-message");

        // Clear existing items except the empty message template
        cartItemsListElement.querySelectorAll(".cart-item-entry").forEach(el => el.remove());

        if (this.shoppingCart.length === 0) {
            emptyMessageElement.style.display = "list-item";
            proceedButton.style.display = "none";
        } else {
            emptyMessageElement.style.display = "none";
            this.shoppingCart.forEach(cartItem => {
                const listItem = this.createCartItemElement(cartItem);
                cartItemsListElement.insertBefore(listItem, emptyMessageElement);
            });
            proceedButton.style.display = "block";
        }
    },

    createCartSummaryDOM(cartPlaceholder) {
        const initialMessage = cartPlaceholder.querySelector("p");
        if (initialMessage) initialMessage.remove();

        const cartSummaryDetails = document.createElement("div");
        cartSummaryDetails.id = "cart-summary-details";
        cartSummaryDetails.innerHTML = `
            <ul id="cart-items-list" class="cart-items-list-modern">
                <li class="cart-empty-message" style="text-align: center; padding: 10px; color: #777;">Chưa có vé nào được chọn.</li>
            </ul>
            <button id="proceed-to-booking-btn" class="button primary-button" style="display:none; margin-top: 15px; width: 100%; padding: 10px; font-size: 1em;">Tiếp tục đặt vé</button>
        `;
        cartPlaceholder.appendChild(cartSummaryDetails);

        cartSummaryDetails.querySelector("#cart-items-list").addEventListener("click", async (event) => {
            const removeIcon = event.target.closest(".cart-item-remove-icon");
            if (removeIcon) {
                const listItem = removeIcon.closest(".cart-item-entry");
                const seatIdToRemove = listItem?.dataset.seatId;
                const tripIdToRemove = listItem?.dataset.tripId;
                if (seatIdToRemove && tripIdToRemove) {
                    const itemInCart = this.shoppingCart.find(item => item.seatID.toString() === seatIdToRemove && item.tripId === tripIdToRemove);
                    if (itemInCart) {
                        await this.handleSeatDeselectionAttempt(itemInCart, null);
                    }
                }
            }
        });

        const proceedButton = cartSummaryDetails.querySelector("#proceed-to-booking-btn");
        proceedButton.addEventListener("click", () => this.initiateBookingProcess());

        return cartSummaryDetails;
    },

    createCartItemElement(cartItem) {
        const listItem = document.createElement("li");
        listItem.className = "cart-item-entry";
        listItem.dataset.seatId = cartItem.seatID;
        listItem.dataset.tripId = cartItem.tripId;

        if (cartItem.holdExpiresAt && !this.seatHoldTimers[`${cartItem.tripId}_${cartItem.seatID}`]) {
            this.startSeatHoldTimer(cartItem);
        }

        const legTypeDisplay = cartItem.tripLeg === "outbound" ? "Chiều đi" : cartItem.tripLeg === "return" ? "Chiều về" : "Chuyến";
        const trainRouteDisplay = `${cartItem.trainName || "N/A"}: ${cartItem.originStationName || "N/A"} - ${cartItem.destinationStationName || "N/A"}`;
        const departureDisplay = cartItem.scheduledDepartureDisplay || "N/A";
        const seatDetailsDisplay = `${cartItem.seatName || "N/A"} (Toa ${cartItem.coachPosition || "N/A"}, Chỗ ${cartItem.seatNumberInCoach || "N/A"})`;
        const priceInfo = cartItem.calculatedPrice !== undefined ? `${cartItem.calculatedPrice.toLocaleString("vi-VN")} VND` : "N/A";

        listItem.innerHTML = `
            <div class="cart-item-info-block">
                <div class="cart-item-leg-type">${legTypeDisplay}</div>
                <div class="cart-item-train-route">${trainRouteDisplay}</div>
                <div class="cart-item-departure">${departureDisplay}</div>
                <div class="cart-item-seat-info">${seatDetailsDisplay}</div>
                <div class="cart-item-price">${priceInfo}</div>
            </div>
            <span class="cart-item-remove-icon" title="Xóa vé này"><i class="fas fa-trash"></i></span>
        `;
        return listItem;
    }
};

bookingController.init();