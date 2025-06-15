var contextPath = "/train-booking-system"; // Fallback if not set, though it should be.

let currentCoachInfo = null; // To store coach metadata for use in createSeatElement if needed
let shoppingCart = []; // To store selected seat DTOs, with tripId for context
let seatHoldTimers = {}; // To store timers for individual seat holds { seatId: timerId }
const HOLD_DURATION_MS = 5 * 60 * 1000; // 5 minutes
const SESSION_STORAGE_CART_KEY = "VNR_userShoppingCart";

let currentCarriage; // To store the currently selected carriage element for seat selection
// Helper function to fetch and render coach layout, extracted for re-use
// Variable to store the last parameters used for fetching coach layout
let lastFetchCoachLayoutParams = null;

// Function to load cart from session storage
function loadCartFromSession() {
    const storedCart = sessionStorage.getItem(SESSION_STORAGE_CART_KEY);
    if (storedCart) {
        try {
            const parsedCart = JSON.parse(storedCart);
            const now = new Date();
            const validItems = [];

            parsedCart.forEach((item) => {
                if (item.holdExpiresAt) {
                    const expiresAtDate = new Date(item.holdExpiresAt);
                    if (expiresAtDate > now) {
                        validItems.push(item);
                    } else {
                        console.log(
                            `Seat ${item.seatName} (Trip: ${item.tripId}) hold expired, removing from session-loaded cart.`
                        );
                    }
                } else {
                    console.log(
                        `Seat ${item.seatName} (Trip: ${item.tripId}) has no holdExpiresAt, removing from session-loaded cart.`
                    );
                }
            });
            shoppingCart = validItems;
            shoppingCart.forEach((item) => {
                startSeatHoldTimer(item); // This function also updates timer display in the cart
                // Attempt to find the seat on the map and mark it as selected if it's part of the current view
                const trainItemElem = document.querySelector(
                    `.train-item[data-trip-id="${item.tripId}"]`
                );
                if (trainItemElem) {
                    const seatElementOnMap = trainItemElem.querySelector(
                        `.seat[data-seat-id="${item.seatID}"]`
                    );
                    if (
                        seatElementOnMap &&
                        seatElementOnMap.classList.contains("available")
                    ) {
                        seatElementOnMap.classList.remove("available");
                        seatElementOnMap.classList.add("selected");
                    } else if (
                        seatElementOnMap &&
                        !seatElementOnMap.classList.contains("selected")
                    ) {
                        // If it exists but isn't 'selected', ensure it is.
                        // This handles cases where the seat map might have been re-rendered without full cart awareness.
                        seatElementOnMap.classList.add("selected");
                    }
                }
            });
        } catch (e) {
            console.error(
                "Error parsing shopping cart from session storage:",
                e
            );
            sessionStorage.removeItem(SESSION_STORAGE_CART_KEY); // Clear corrupted data
        }
    }
}

// Function to save cart to session storage
function saveCartToSession() {
    sessionStorage.setItem(
        SESSION_STORAGE_CART_KEY,
        JSON.stringify(shoppingCart)
    );
}

document.addEventListener("DOMContentLoaded", function () {
    loadCartFromSession(); // Load cart from session storage first
    updateCartDisplay(); // Then update the display based on loaded cart

    const trainItems = document.querySelectorAll(".train-item");
    trainItems.forEach((item) => {
        const collapsedSummary = item.querySelector(
            ".train-item-collapsed-summary"
        );
        if (collapsedSummary) {
            collapsedSummary.addEventListener("click", () =>
                toggleTrainItemDetails(item)
            );
        }

        const expandedDetails = item.querySelector(".expanded-details");
        if (expandedDetails) {
            expandedDetails.addEventListener("click", function (event) {
                const carriageItem = event.target.closest(".carriage-item");
                if (carriageItem && carriageItem !== currentCarriage) {
                    // If a different carriage is clicked, select it
                    selectCarriage(carriageItem, item);
                    currentCarriage = carriageItem; // Update global currentCarriage
                }
            });
        }
    });
});

function toggleTrainItemDetails(trainItemElement) {
    const details = trainItemElement.querySelector(".expanded-details");
    const isExpanded = details.style.display === "block";
    details.style.display = isExpanded ? "none" : "block";
    trainItemElement.classList.toggle("expanded", !isExpanded);
}

function selectCarriage(selectedCarriageElement, trainItemElement) {
    const compositionDisplay = selectedCarriageElement.closest(
        ".train-composition-display"
    );
    if (!compositionDisplay) return;

    compositionDisplay
        .querySelectorAll(".carriage-item")
        .forEach((c) => c.classList.remove("active"));
    selectedCarriageElement.classList.add("active");
    // currentCarriage = selectedCarriageElement; // Update global currentCarriage

    const coachId = selectedCarriageElement.dataset.coachId;
    const coachTypeName = selectedCarriageElement.dataset.coachTypename;
    const coachPosition = selectedCarriageElement.dataset.coachPosition;
    const coachDescription = selectedCarriageElement.dataset.coachDescription;
    const coachCapacity = parseInt(
        selectedCarriageElement.dataset.coachCapacity
    );
    const isCompartmented =
        selectedCarriageElement.dataset.coachIsCompartmented == "true";
    const defaultCompartmentCapacity = selectedCarriageElement.dataset
        .coachDefaultCompartmentCapacity
        ? parseInt(
              selectedCarriageElement.dataset.coachDefaultCompartmentCapacity
          )
        : null;

    const tripId = selectedCarriageElement.dataset.tripId;
    const tripLeg = selectedCarriageElement.dataset.tripLeg;

    const legOriginStationId = trainItemElement.dataset.legOriginStationId;
    const legDestStationId = trainItemElement.dataset.legDestStationId;

    // Update description block
    const descriptionBlockId = `carriageDescription-${tripLeg}-${tripId}`;
    const descriptionBlock = document.getElementById(descriptionBlockId);
    if (descriptionBlock) {
        let descHtml = `<strong>${coachTypeName} - Toa ${coachPosition}</strong>`;
        if (
            coachDescription &&
            coachDescription !== "null" &&
            coachDescription.trim() !== ""
        ) {
            descHtml += `<br>${coachDescription}`;
        }
        descriptionBlock.innerHTML = `<p>${descHtml}</p>`;
    }

    const seatDetailsBlockId = `seatDetailsBlock-${tripLeg}-${tripId}`;
    const seatDetailsBlock = document.getElementById(seatDetailsBlockId);

    const coachLayoutInfo = {
        capacity: coachCapacity,
        isCompartmented: isCompartmented,
        defaultCompartmentCapacity: defaultCompartmentCapacity,
        coachTypeName: coachTypeName,
    };
    const carriageContext = {
        coachId: coachId,
        coachPosition: coachPosition,
        tripLeg: tripLeg,
    };

    if (seatDetailsBlock) {
        fetchAndRenderCoachLayout(
            tripId,
            coachId,
            legOriginStationId,
            legDestStationId,
            seatDetailsBlock,
            coachLayoutInfo,
            carriageContext,
            trainItemElement
        );
    }
}

function generateSeatLayout(
    seatDetailsBlock,
    seatDataList,
    coachLayoutInfo,
    carriageContext
) {
    // Added carriageContext
    seatDetailsBlock.innerHTML = ""; // Clear loading message or previous seats

    if (!seatDataList || seatDataList.length === 0) {
        seatDetailsBlock.innerHTML =
            "<p>Không có thông tin ghế cho toa này.</p>";
        return;
    }

    if (
        coachLayoutInfo.isCompartmented &&
        coachLayoutInfo.defaultCompartmentCapacity > 0
    ) {
        generateCompartmentLayout(
            seatDetailsBlock,
            seatDataList,
            coachLayoutInfo
        );
    } else {
        generateOpenSeatingLayout(
            seatDetailsBlock,
            seatDataList,
            coachLayoutInfo
        );
    }
    addSeatClickListeners(seatDetailsBlock, carriageContext); // Pass carriageContext
}

function generateOpenSeatingLayout(
    seatDetailsBlock,
    seatDataList,
    coachLayoutInfo
) {
    const seatGrid = document.createElement("div");
    seatGrid.className = "seat-grid open-seating-grid";

    const seatsPerRowVisual = 14;
    const aisleAfterIndex = 6;

    let currentVisualRow = document.createElement("div");
    currentVisualRow.className = "seat-row";
    seatGrid.appendChild(currentVisualRow);
    let seatsInCurrentVisualRow = 0;

    seatDataList.forEach((seatDto) => {
        if (seatsInCurrentVisualRow === seatsPerRowVisual) {
            currentVisualRow = document.createElement("div");
            currentVisualRow.className = "seat-row";
            seatGrid.appendChild(currentVisualRow);
            seatsInCurrentVisualRow = 0;
        }

        const seatDiv = createSeatElement(seatDto);
        currentVisualRow.appendChild(seatDiv);
        seatsInCurrentVisualRow++;

        if (
            seatsInCurrentVisualRow === aisleAfterIndex + 1 &&
            seatsInCurrentVisualRow < seatsPerRowVisual
        ) {
            const aisleDiv = document.createElement("div");
            aisleDiv.className = "aisle-spacer";
            currentVisualRow.appendChild(aisleDiv);
        }
    });
    seatDetailsBlock.appendChild(seatGrid);
}

function generateCompartmentLayout(
    seatDetailsBlock,
    seatDataList,
    coachLayoutInfo
) {
    const compartmentGrid = document.createElement("div");
    compartmentGrid.className = "seat-grid compartment-grid";

    const numCompartments = Math.ceil(
        coachLayoutInfo.capacity / coachLayoutInfo.defaultCompartmentCapacity
    );
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
        compartmentDiv.innerHTML = `<div class="compartment-label">Khoang ${
            i + 1
        }</div>`;

        const berthsContainer = document.createElement("div");
        berthsContainer.className = "berths-container";

        for (let j = 0; j < 2; j++) {
            const berthItem = document.createElement("div");
            berthItem.className = "berth-item";

            for (
                let k = 0;
                k < coachLayoutInfo.defaultCompartmentCapacity / 2;
                k++
            ) {
                if (seatIndex < seatDataList.length) {
                    // Boundary check
                    const seatDto = seatDataList[seatIndex];
                    const seatDiv = createSeatElement(seatDto);
                    if (seatDto.berthLevel) {
                        seatDiv.classList.add(
                            `berth-${seatDto.berthLevel.toLowerCase()}`
                        );
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
}

function fetchAndRenderCoachLayout(
    tripId,
    coachId,
    legOriginStationId,
    legDestinationStationId,
    seatDetailsBlock,
    coachLayoutInfo,
    carriageContext,
    trainItemElement
) {
    // Store the current parameters for potential later use
    lastFetchCoachLayoutParams = {
        tripId,
        coachId,
        legOriginStationId,
        legDestinationStationId,
        seatDetailsBlock, // Note: Storing DOM element references
        coachLayoutInfo,
        carriageContext,
        trainItemElement, // Note: Storing DOM element references
    };

    seatDetailsBlock.innerHTML = "<p>Đang làm mới sơ đồ ghế...</p>"; // Loading indicator

    const bookingDateTimeISO = new Date().toISOString().slice(0, 19);
    const mainResultsContent = trainItemElement.closest(
        ".main-results-content"
    );
    const isRoundTripForFetch = mainResultsContent
        ? mainResultsContent.dataset.isRoundTrip === "true"
        : false;

    const fetchUrl = `${contextPath}/getCoachSeatsWithPrice?tripId=${tripId}&coachId=${coachId}&legOriginStationId=${legOriginStationId}&legDestinationStationId=${legDestinationStationId}&bookingDateTime=${encodeURIComponent(
        bookingDateTimeISO
    )}&isRoundTrip=${isRoundTripForFetch}`;

    fetch(fetchUrl)
        .then((response) => {
            if (!response.ok) {
                return response.json().then((err) => {
                    throw new Error(err.error || "Lỗi mạng khi tải ghế.");
                });
            }
            return response.json();
        })
        .then((seatDataList) => {
            generateSeatLayout(
                seatDetailsBlock,
                seatDataList,
                coachLayoutInfo,
                carriageContext
            );
        })
        .catch((error) => {
            console.error("Error refreshing seat data:", error);
            seatDetailsBlock.innerHTML = `<p>Lỗi khi làm mới sơ đồ ghế: ${error.message}</p>`;
        });
}

function createSeatElement(seatDto) {
    const seatDiv = document.createElement("div");
    seatDiv.className = "seat";
    seatDiv.dataset.seatId = seatDto.seatID;
    seatDiv.dataset.seatName = seatDto.seatName;
    seatDiv.dataset.seatInfo = JSON.stringify(seatDto);
    seatDiv.textContent = seatDto.seatNumberInCoach;

    let titleText = seatDto.availabilityStatus || "Trạng thái không xác định";
    if (
        seatDto.availabilityStatus &&
        seatDto.availabilityStatus.toLowerCase() === "available" &&
        seatDto.calculatedPrice !== null &&
        seatDto.enabled
    ) {
        titleText += `: ${seatDto.calculatedPrice.toLocaleString("vi-VN")} VND`;
    }
    seatDiv.title = titleText;

    if (!seatDto.enabled) {
        seatDiv.classList.add("disabled");
    } else {
        switch (seatDto.availabilityStatus.toLowerCase()) {
            case "available":
                seatDiv.classList.add("available");
                break;
            case "occupied":
                seatDiv.classList.add("occupied");
                break;
            case "heldbyyou":
                seatDiv.classList.add("selected");
                break;
            case "heldbyother":
                seatDiv.classList.add("occupied");
                break;
            case "disabled":
                seatDiv.classList.add("disabled");
                break;
            default:
                console.warn(
                    "Unknown availability status:",
                    seatDto.availabilityStatus
                );
                seatDiv.classList.add("unavailable");
        }
    }
    return seatDiv;
}

// --- NEW FUNCTIONS FOR SEAT HOLDING, TIMERS, AND BOOKING PROCESS ---

async function handleSeatSelectionAttempt(seatDataForCart, seatElement) {
    console.log("Attempting to hold seat:", seatDataForCart.seatName);
    seatElement.classList.add("pending-hold");

    try {
        const response = await fetch(`${contextPath}/api/seats/hold`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                tripId: seatDataForCart.tripId,
                seatId: seatDataForCart.seatID,
                coachId: seatDataForCart.coachId,
                legOriginStationId: seatDataForCart.legOriginStationId,
                legDestinationStationId:
                    seatDataForCart.legDestinationStationId,
            }),
        });

        seatElement.classList.remove("pending-hold");
        const responseData = await response.json();
        console.log(
            "Server response from /api/seats/hold:",
            JSON.stringify(responseData)
        );

        if (response.ok && responseData.status === "success") {
            console.log("Seat hold successful. Server data:", responseData);
            seatElement.classList.add("selected");
            seatElement.classList.remove("available");

            const expiresAtFromServer =
                responseData.data && responseData.data.holdExpiresAt
                    ? responseData.data.holdExpiresAt
                    : null;
            console.log(
                "Value of expiresAtFromServer (from responseData.data.holdExpiresAt):",
                expiresAtFromServer
            );

            const finalCartItem = {
                ...seatDataForCart,
                holdExpiresAt: expiresAtFromServer,
            };
            console.log(
                "Final cart item constructed before adding to cart:",
                JSON.stringify(finalCartItem)
            );

            addToCart(finalCartItem, finalCartItem.tripId);
        } else {
            console.error("Seat hold failed:", responseData.message);
            alert(
                `Không thể giữ ghế ${seatDataForCart.seatName}: ${
                    responseData.message || "Lỗi không xác định."
                }`
            );
            seatElement.classList.remove("selected");
            seatElement.classList.remove("pending-hold");
            seatElement.classList.add("available"); // Reset to available if hold fails

            const messageLowerCase = responseData.message
                ? responseData.message.toLowerCase()
                : "";
            if (
                messageLowerCase.includes("seat is already occupied") ||
                messageLowerCase.includes("seat taken") ||
                messageLowerCase.includes("held by another user")
            ) {
                console.log(
                    "Seat hold failed because seat is taken. Refreshing coach layout."
                );
                if (lastFetchCoachLayoutParams) {
                    fetchAndRenderCoachLayout(
                        lastFetchCoachLayoutParams.tripId,
                        lastFetchCoachLayoutParams.coachId,
                        lastFetchCoachLayoutParams.legOriginStationId,
                        lastFetchCoachLayoutParams.legDestinationStationId,
                        lastFetchCoachLayoutParams.seatDetailsBlock,
                        lastFetchCoachLayoutParams.coachLayoutInfo,
                        lastFetchCoachLayoutParams.carriageContext,
                        lastFetchCoachLayoutParams.trainItemElement
                    );
                } else {
                    console.warn(
                        "Could not gather all necessary data to refresh coach layout after hold failure."
                    );
                }
            }
        }
    } catch (error) {
        seatElement.classList.remove("pending-hold");
        seatElement.classList.remove("selected");
        console.error("Error during seat hold API call:", error);
        alert("Lỗi kết nối khi cố gắng giữ ghế. Vui lòng thử lại.");
    }
}

async function handleSeatDeselectionAttempt(cartItem, seatElementOnMap) {
    console.log("Attempting to release seat:", cartItem.seatName);

    try {
        const response = await fetch(`${contextPath}/api/seats/release`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                tripId: cartItem.tripId,
                seatId: cartItem.seatID,
                legOriginStationId: cartItem.legOriginStationId,
                legDestinationStationId: cartItem.legDestinationStationId,
            }),
        });
        const responseData = await response.json();
        if (response.ok && responseData.status === "success") {
            console.log(
                `Seat release for ${cartItem.seatName} successful on server.`
            );
        } else {
            console.warn(
                `Failed to release seat ${cartItem.seatName} on server: ${responseData.message}`
            );
        }
    } catch (error) {
        console.error("Error during seat release API call:", error);
    } finally {
        clearTimerAndRemoveFromClientCart(cartItem.seatID, cartItem.tripId);

        if (seatElementOnMap) {
            console.log("test");
            seatElementOnMap.classList.remove("selected");
            seatElementOnMap.classList.add("available"); // Reset to available if release is successful
        }
    }
}

function startSeatHoldTimer(cartItem) {
    const timerKey = `${cartItem.tripId}_${cartItem.seatID}`;

    if (seatHoldTimers[timerKey]) {
        clearInterval(seatHoldTimers[timerKey]);
    }

    const expiresAtDate = new Date(cartItem.holdExpiresAt);

    const intervalId = setInterval(() => {
        const now = new Date();
        const timeLeftSeconds = Math.round(
            (expiresAtDate.getTime() - now.getTime()) / 1000
        );

        const listItemElement = document.querySelector(
            `.cart-item-entry[data-trip-id="${cartItem.tripId}"][data-seat-id="${cartItem.seatID}"]`
        );
        const timerDisplayElement = listItemElement
            ? listItemElement.querySelector(".cart-item-remove-icon")
            : null;

        if (timeLeftSeconds > 0) {
            if (timerDisplayElement) {
                const minutes = Math.floor(timeLeftSeconds / 60);
                const seconds = timeLeftSeconds % 60;
                timerDisplayElement.innerHTML = `<span class="timer-countdown" title="Thời gian giữ vé: ${minutes}m ${seconds}s">${minutes}:${seconds
                    .toString()
                    .padStart(2, "0")}</span>`;
                if (!timerDisplayElement.classList.contains("timer-active")) {
                    timerDisplayElement.classList.add("timer-active");
                }
            }
        } else {
            console.log(
                "Timer expired for seat:",
                cartItem.seatName,
                cartItem.seatID
            );
            clearInterval(intervalId);
            delete seatHoldTimers[timerKey];

            if (timerDisplayElement) {
                timerDisplayElement.innerHTML = '<i class="fas fa-trash"></i>';
                timerDisplayElement.classList.remove("timer-active");
            }
            handleHoldExpired(cartItem);
        }
    }, 1000);
    seatHoldTimers[timerKey] = intervalId;
}

async function handleHoldExpired(expiredCartItem) {
    alert(
        `Thời gian giữ vé cho ghế ${expiredCartItem.seatName} (Tàu ${
            expiredCartItem.trainName || "N/A"
        }) đã hết. Vé đã được tự động hủy.`
    );

    try {
        const response = await fetch(`${contextPath}/api/seats/release`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                tripId: expiredCartItem.tripId,
                seatId: expiredCartItem.seatID,
                legOriginStationId: expiredCartItem.legOriginStationId,
                legDestinationStationId:
                    expiredCartItem.legDestinationStationId,
            }),
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            console.warn(
                `Server failed to release expired hold for seat ${
                    expiredCartItem.seatID
                }: ${response.status} - ${
                    errorData.message || "Unknown server error"
                }`
            );
        } else {
            const result = await response.json();
            if (result.status === "success") {
                console.log(
                    `Successfully released expired hold for seat ${expiredCartItem.seatID} on server.`
                );
            } else {
                console.warn(
                    `Server reported failure to release expired hold for seat ${expiredCartItem.seatID}: ${result.message}`
                );
            }
        }
    } catch (error) {
        console.error("Error releasing expired seat hold on server:", error);
    }

    clearTimerAndRemoveFromClientCart(
        expiredCartItem.seatID,
        expiredCartItem.tripId
    );
    // After hold expires and is cleared, refresh the current coach view
    if (currentCarriage) {
        const trainItemElement = currentCarriage.closest(".train-item");
        if (trainItemElement) {
            selectCarriage(currentCarriage, trainItemElement); // Re-select to refresh
        }
    }
}

function clearTimerAndRemoveFromClientCart(seatId, tripId) {
    const timerKey = `${tripId}_${seatId}`;
    if (seatHoldTimers[timerKey]) {
        clearInterval(seatHoldTimers[timerKey]);
        delete seatHoldTimers[timerKey];
    }

    const itemIndex = shoppingCart.findIndex(
        (item) =>
            item.seatID.toString() === seatId.toString() &&
            item.tripId === tripId
    );
    if (itemIndex > -1) {
        console.log(itemIndex);
        shoppingCart.splice(itemIndex, 1);
        console.log(
            `Removed seat ${seatId} (Trip: ${tripId}) from client cart.`
        );
    }

    const trainItemElem = document.querySelector(
        `.train-item[data-trip-id="${tripId}"]`
    );
    console.log(trainItemElem);
    console.log(currentCarriage);
    console.log(shoppingCart[itemIndex]?.coachId.toString());

    const seatElementOnMap = trainItemElem.querySelector(
        `.seat[data-seat-id="${seatId}"]`
    );
    console.log("Seat element on map:", seatElementOnMap);
    if (seatElementOnMap) {
        console.log("Lol");
        seatElementOnMap.classList.add("available"); // Reset to available
        seatElementOnMap.classList.remove("selected");
    }
    updateCartDisplay();
    saveCartToSession();
}

async function initiateBookingProcess() {
    if (shoppingCart.length === 0) {
        alert("Vui lòng chọn ít nhất một vé để tiếp tục.");
        return;
    }

    if (shoppingCart.length > 10) {
        // MAX_PASSENGERS, assuming 10
        alert("Bạn chỉ có thể đặt tối đa 10 vé cùng một lúc.");
        return;
    }

    const seatsToBook = shoppingCart.map((item) => ({
        tripId: item.tripId,
        seatId: item.seatID,
        coachId: item.coachId,
        legOriginStationId: item.legOriginStationId,
        legDestinationStationId: item.legDestinationStationId,
    }));

    console.log("Initiating booking with seats:", seatsToBook);

    try {
        const response = await fetch(`${contextPath}/api/booking/initiate`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(seatsToBook),
        });
        const responseData = await response.json();

        if (response.ok && responseData.status === "success") {
            console.log(
                "Booking initiated successfully, redirecting...",
                responseData
            );
            window.location.href = `${contextPath}/ticketPayment`; // Rely on server-provided redirect URL
        } else {
            alert(
                `Lỗi khi khởi tạo đặt vé: ${
                    responseData.message ||
                    "Không thể tiếp tục, vui lòng thử lại."
                }`
            );
        }
    } catch (error) {
        console.error("Error initiating booking:", error);
        alert(
            "Lỗi kết nối khi khởi tạo đặt vé. Vui lòng kiểm tra kết nối và thử lại."
        );
    }
}

function addSeatClickListeners(seatGridContainer, carriageContext) {
    let tripIdFromScope = null;
    let legOriginStationIdFromScope = null;
    let legDestinationStationIdFromScope = null;

    const trainItemElement = seatGridContainer.closest(".train-item");
    if (trainItemElement && trainItemElement.dataset.tripId) {
        tripIdFromScope = trainItemElement.dataset.tripId;
        legOriginStationIdFromScope =
            trainItemElement.dataset.legOriginStationId;
        legDestinationStationIdFromScope =
            trainItemElement.dataset.legDestStationId;
    }

    if (!tripIdFromScope) {
        console.warn(
            "Could not reliably determine tripId for seat grid:",
            seatGridContainer.id
        );
    }
    if (!legOriginStationIdFromScope || !legDestinationStationIdFromScope) {
        console.warn(
            "Could not reliably determine leg origin/destination station IDs for seat grid:",
            seatGridContainer.id,
            trainItemElement.dataset
        );
    }

    seatGridContainer
        .querySelectorAll(".seat") // Attach to all seats to handle deselection of 'selected' ones too
        .forEach((seatElement) => {
            if (
                seatElement.classList.contains("disabled") ||
                seatElement.classList.contains("occupied")
            ) {
                if (!seatElement.classList.contains("selected")) {
                    // if it's occupied but not by you
                    return; // Don't add click listener to disabled or occupied (by others) seats
                }
            }

            seatElement.addEventListener("click", async function () {
                // Made async for await inside
                const seatInfoString = this.dataset.seatInfo;
                if (!seatInfoString) {
                    console.warn("No seatInfo found for clicked seat:", this);
                    return;
                }

                const seatDataObject = JSON.parse(seatInfoString);
                const effectiveTripId =
                    tripIdFromScope || seatDataObject.tripId;

                if (!effectiveTripId) {
                    console.error(
                        "CRITICAL: tripId is missing for seat operation. Seat:",
                        seatDataObject.seatName,
                        this
                    );
                    return;
                }

                const isCurrentlySelectedOnUI =
                    this.classList.contains("selected");
                const cartItemExists = shoppingCart.find(
                    (item) =>
                        item.seatID === seatDataObject.seatID &&
                        item.tripId === effectiveTripId
                );

                const seatDataForAction = {
                    ...seatDataObject,
                    tripId: effectiveTripId,
                    coachId: carriageContext?.coachId,
                    legOriginStationId: legOriginStationIdFromScope,
                    legDestinationStationId: legDestinationStationIdFromScope,
                    tripLeg: carriageContext?.tripLeg,
                    coachPosition: carriageContext?.coachPosition,
                    trainName: trainItemElement
                        ?.querySelector(
                            ".train-item-collapsed-summary .train-name"
                        )
                        ?.textContent.trim(),
                    originStationName: trainItemElement
                        ?.querySelector(
                            ".train-item-collapsed-summary .departure-info .trip-station"
                        )
                        ?.textContent.trim(),
                    destinationStationName: trainItemElement
                        ?.querySelector(
                            ".train-item-collapsed-summary .arrival-info .trip-station"
                        )
                        ?.textContent.trim(),
                    scheduledDepartureDisplay: trainItemElement
                        ?.querySelector(
                            ".train-item-collapsed-summary .departure-info .trip-time"
                        )
                        ?.textContent.trim(),
                };

                if (
                    !seatDataForAction.coachId ||
                    !seatDataForAction.legOriginStationId ||
                    !seatDataForAction.legDestinationStationId
                ) {
                    console.error(
                        "CRITICAL: Missing coachId, legOriginStationId, or legDestinationStationId for API call.",
                        seatDataForAction
                    );
                    alert(
                        "Lỗi: Không đủ thông tin để xử lý yêu cầu cho ghế này. Vui lòng thử làm mới trang."
                    );
                    return;
                }

                // If the seat is available to be selected (not disabled, not occupied by others)
                if (this.classList.contains("available")) {
                    console.log(
                        `Attempting to select available seat: ${seatDataForAction.seatName}`
                    );
                    await handleSeatSelectionAttempt(seatDataForAction, this);
                } else if (isCurrentlySelectedOnUI && cartItemExists) {
                    // If seat is selected by this user, try to deselect
                    console.log(
                        `Attempting to deselect seat: ${seatDataForAction.seatName}`
                    );
                    await handleSeatDeselectionAttempt(cartItemExists, this);
                } else {
                    // Seat is occupied by others or disabled, and not selected by this user. Do nothing or inform.
                    console.log(
                        `Seat ${seatDataForAction.seatName} is not available for selection or deselection by you.`
                    );
                }
            });
        });
}

function addToCart(seatData, tripId) {
    const cartItem = { ...seatData, tripId: tripId };
    if (
        !shoppingCart.find(
            (item) =>
                item.seatID === cartItem.seatID &&
                item.tripId === cartItem.tripId
        )
    ) {
        shoppingCart.push(cartItem);
        console.log(
            `Seat ${cartItem.seatName} (Trip: ${cartItem.tripId}) added to cart.`
        );
    }
    updateCartDisplay();
    saveCartToSession();
}

function updateCartDisplay() {
    console.log("Current Cart:", shoppingCart);
    let totalPrice = 0;
    shoppingCart.forEach((item) => {
        if (item.calculatedPrice !== undefined) {
            totalPrice += item.calculatedPrice;
        } else {
            console.warn(
                `Seat ${item.seatName} (Trip: ${item.tripId}, ID: ${item.seatID}) is missing calculatedPrice information.`
            );
        }
    });
    console.log(`Total Price: ${totalPrice.toFixed(2)}`);

    const cartPlaceholder = document.querySelector(
        ".shopping-cart-placeholder"
    );
    if (!cartPlaceholder) {
        console.error("Shopping cart placeholder not found in DOM.");
        return;
    }

    let cartSummaryDetails = cartPlaceholder.querySelector(
        "#cart-summary-details"
    );

    if (!cartSummaryDetails) {
        const initialMessage = cartPlaceholder.querySelector("p");
        if (
            initialMessage &&
            initialMessage.textContent.includes("Chưa có vé nào")
        ) {
            initialMessage.remove();
        }

        cartSummaryDetails = document.createElement("div");
        cartSummaryDetails.id = "cart-summary-details";
        cartSummaryDetails.innerHTML = `
            <ul id="cart-items-list" class="cart-items-list-modern">
                <li class="cart-empty-message" style="text-align: center; padding: 10px; color: #777;">Chưa có vé nào được chọn.</li>
            </ul>
            <button id="proceed-to-booking-btn" class="button primary-button" style="display:none; margin-top: 15px; width: 100%; padding: 10px; font-size: 1em;">Tiếp tục đặt vé</button>
        `;
        cartPlaceholder.appendChild(cartSummaryDetails);

        const cartItemsList =
            cartSummaryDetails.querySelector("#cart-items-list");
        if (cartItemsList) {
            cartItemsList.addEventListener("click", async function (event) {
                // Made async
                const removeIcon = event.target.closest(
                    ".cart-item-remove-icon"
                );
                if (removeIcon) {
                    const listItem = removeIcon.closest(".cart-item-entry");
                    const seatIdToRemove = listItem?.dataset.seatId;
                    const tripIdToRemove = listItem?.dataset.tripId;

                    if (seatIdToRemove && tripIdToRemove) {
                        const itemInCart = shoppingCart.find(
                            (item) =>
                                item.seatID.toString() === seatIdToRemove &&
                                item.tripId === tripIdToRemove
                        );
                        if (itemInCart) {
                            await handleSeatDeselectionAttempt(
                                itemInCart,
                                null
                            );
                        } else {
                            console.error(
                                "Could not find item in cart to remove (trash icon click) for seatId:",
                                seatIdToRemove,
                                "and tripId:",
                                tripIdToRemove
                            );
                        }
                    } else {
                        console.error(
                            "Missing seatId or tripId for removal from cart item:",
                            listItem
                        );
                    }
                }
            });
        }
    }

    const cartItemsListElement =
        cartSummaryDetails.querySelector("#cart-items-list");
    const proceedButton = cartSummaryDetails.querySelector(
        "#proceed-to-booking-btn"
    );
    const emptyMessageElement = cartItemsListElement.querySelector(
        ".cart-empty-message"
    );

    if (cartItemsListElement) {
        while (
            cartItemsListElement.firstChild &&
            !cartItemsListElement.firstChild.classList?.contains(
                "cart-empty-message"
            )
        ) {
            cartItemsListElement.removeChild(cartItemsListElement.firstChild);
        }

        if (shoppingCart.length === 0) {
            if (emptyMessageElement)
                emptyMessageElement.style.display = "list-item";
        } else {
            if (emptyMessageElement) emptyMessageElement.style.display = "none";
            shoppingCart.forEach((cartItem) => {
                const listItem = document.createElement("li");
                listItem.className = "cart-item-entry";
                listItem.dataset.seatId = cartItem.seatID;
                listItem.dataset.tripId = cartItem.tripId;

                if (cartItem.holdExpiresAt) {
                    const timerKey = `${cartItem.tripId}_${cartItem.seatID}`;
                    if (!seatHoldTimers[timerKey]) {
                        startSeatHoldTimer(cartItem);
                    }
                }

                const legTypeDisplay =
                    cartItem.tripLeg === "outbound"
                        ? "Chiều đi"
                        : cartItem.tripLeg === "return"
                        ? "Chiều về"
                        : "Chuyến";
                const trainRouteDisplay = `${cartItem.trainName || "N/A"}: ${
                    cartItem.originStationName || "N/A"
                } - ${cartItem.destinationStationName || "N/A"}`;
                const departureDisplay =
                    cartItem.scheduledDepartureDisplay || "N/A";
                const seatDetailsDisplay = `${
                    cartItem.seatName || "N/A"
                } (Toa ${cartItem.coachPosition || "N/A"}, Chỗ ${
                    cartItem.seatNumberInCoach || "N/A"
                })`;
                const priceInfo =
                    cartItem.calculatedPrice !== undefined
                        ? cartItem.calculatedPrice.toLocaleString("vi-VN", {
                              style: "decimal",
                              minimumFractionDigits: 0,
                              maximumFractionDigits: 0,
                          }) + " VND"
                        : "N/A";

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
                cartItemsListElement.insertBefore(
                    listItem,
                    emptyMessageElement
                );
            });
        }
    }

    if (proceedButton) {
        proceedButton.style.display =
            shoppingCart.length > 0 ? "block" : "none";
        if (!proceedButton.dataset.listenerAttached) {
            proceedButton.addEventListener("click", initiateBookingProcess);
            proceedButton.dataset.listenerAttached = "true";
        }
    }
}
