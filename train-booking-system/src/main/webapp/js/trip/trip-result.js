var contextPath = "/train-booking-system"; // Fallback if not set, though it should be.

let currentCoachInfo = null; // To store coach metadata for use in createSeatElement if needed
let shoppingCart = []; // To store selected seat DTOs, with tripId for context
let seatHoldTimers = {}; // To store timers for individual seat holds { seatId: timerId }
const HOLD_DURATION_MS = 5 * 60 * 1000; // 5 minutes
const SESSION_STORAGE_CART_KEY = "VNR_userShoppingCart";

let currentCarriage; // To store the currently selected carriage element for seat selection

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
                    // Item must have a hold expiration
                    const expiresAtDate = new Date(item.holdExpiresAt);
                    if (expiresAtDate > now) {
                        // And the hold must be in the future
                        validItems.push(item);
                    } else {
                        console.log(
                            `Seat ${item.seatName} (Trip: ${item.tripId}) hold expired, removing from session-loaded cart.`
                        );
                        // Server cleanup job is the backup for releasing the hold on the server.
                    }
                } else {
                    // Items without holdExpiresAt are considered invalid for persisting an active hold.
                    console.log(
                        `Seat ${item.seatName} (Trip: ${item.tripId}) has no holdExpiresAt, removing from session-loaded cart.`
                    );
                }
            });
            shoppingCart = validItems; // Assign the filtered list

            // Re-initialize timers for items that are still validly held
            // and update their visual representation on the seat map if the seat is currently displayed
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
                }
                currentCarriage = carriageItem; // Store the current carriage for later use
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

    const tripId = selectedCarriageElement.dataset.tripId; // or trainItemElement.dataset.tripId
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

    // Fetch and display seats
    const seatDetailsBlockId = `seatDetailsBlock-${tripLeg}-${tripId}`;
    const seatDetailsBlock = document.getElementById(seatDetailsBlockId);
    if (seatDetailsBlock) {
        seatDetailsBlock.innerHTML = "<p>Đang tải sơ đồ ghế...</p>"; // Loading indicator

        const bookingDateTimeISO = new Date().toISOString().slice(0, 19); // YYYY-MM-DDTHH:mm:ss
        const mainResultsContent = trainItemElement.closest(
            ".main-results-content"
        );
        const isRoundTripForFetch = mainResultsContent
            ? mainResultsContent.dataset.isRoundTrip === "true"
            : false;

        const fetchUrl = `${contextPath}/getCoachSeatsWithPrice?tripId=${tripId}&coachId=${coachId}&legOriginStationId=${legOriginStationId}&legDestinationStationId=${legDestStationId}&bookingDateTime=${encodeURIComponent(
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
                const coachLayoutInfo = {
                    capacity: coachCapacity,
                    isCompartmented: isCompartmented,
                    defaultCompartmentCapacity: defaultCompartmentCapacity,
                    coachTypeName: coachTypeName,
                };
                const carriageContext = {
                    // Collect context here
                    coachId: coachId, // Add coachId to the context
                    coachPosition: coachPosition,
                    tripLeg: tripLeg, // from selectedCarriageElement.dataset.tripLeg
                    // trainName, origin/dest station names, departure time will be sourced from trainItemElement in addSeatClickListeners
                };
                generateSeatLayout(
                    seatDetailsBlock,
                    seatDataList,
                    coachLayoutInfo,
                    carriageContext // Pass context
                );
            })
            .catch((error) => {
                console.error("Error fetching seat data:", error);
                seatDetailsBlock.innerHTML = `<p>Lỗi khi tải sơ đồ ghế: ${error.message}</p>`;
            });
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

    // Example: 2-aisle-2 layout (4 seats per visual row)
    const seatsPerRowVisual = 14;
    const aisleAfterIndex = 6; // Aisle after the 2nd seat (0-indexed) in a visual row of 4

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
        coachLayoutInfo.capacity / coachLayoutInfo.defaultCompartmentCapacity // 7
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
        berthsContainer.className = "berths-container"; // For styling berths within a compartment

        for (let j = 0; j < 2; j++) {
            const berthItem = document.createElement("div");
            berthItem.className = "berth-item";

            for (
                let k = 0;
                k < coachLayoutInfo.defaultCompartmentCapacity / 2;
                k++
            ) {
                const seatDto = seatDataList[seatIndex];
                const seatDiv = createSeatElement(seatDto);
                // Add berth level specific class if available
                if (seatDto.berthLevel) {
                    seatDiv.classList.add(
                        `berth-${seatDto.berthLevel.toLowerCase()}`
                    );
                }
                berthItem.appendChild(seatDiv);
                seatIndex++;
            }
            berthsContainer.appendChild(berthItem);
        }
        compartmentDiv.appendChild(berthsContainer);
        compartmentGrid.appendChild(compartmentDiv);
    }
    seatDetailsBlock.appendChild(compartmentGrid);
}

function createSeatElement(seatDto) {
    const seatDiv = document.createElement("div");
    seatDiv.className = "seat";
    seatDiv.dataset.seatId = seatDto.seatID; // Keep for convenience
    seatDiv.dataset.seatName = seatDto.seatName; // Keep for convenience
    seatDiv.dataset.seatInfo = JSON.stringify(seatDto); // Store the full seat DTO
    seatDiv.textContent = seatDto.seatNumberInCoach;

    let titleText = seatDto.availabilityStatus || "Trạng thái không xác định";
    if (
        seatDto.availabilityStatus &&
        seatDto.availabilityStatus.toLowerCase() === "available" &&
        seatDto.calculatedPrice !== null && // Price should be raw number
        seatDto.enabled
    ) {
        titleText += `: ${seatDto.calculatedPrice}`; // Append raw price
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
            case "disabled": // Double check if SP uses 'Disabled' for S.IsEnabled=0 or other reasons
                seatDiv.classList.add("disabled");
                break;
            default:
                seatDiv.classList.add("unavailable"); // Fallback
        }
    }
    return seatDiv;
}

// --- NEW FUNCTIONS FOR SEAT HOLDING, TIMERS, AND BOOKING PROCESS ---

/**
 * Handles the attempt to select and hold a seat via API.
 * @param {object} seatDataForCart - The fully augmented seat data object.
 * @param {HTMLElement} seatElement - The clicked seat div element on the map.
 */
async function handleSeatSelectionAttempt(seatDataForCart, seatElement) {
    console.log("Attempting to hold seat:", seatDataForCart.seatName);
    seatElement.classList.add("pending-hold"); // Visual feedback for pending operation

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
        console.log("Server response from /api/seats/hold:", JSON.stringify(responseData)); // Log the raw server response

        if (response.ok && responseData.status === "success") {
            console.log("Seat hold successful. Server data:", responseData);
            seatElement.classList.add("selected"); // Confirm selection on UI
            
            // Correctly access the nested holdExpiresAt property
            const expiresAtFromServer = responseData.data && responseData.data.holdExpiresAt ? responseData.data.holdExpiresAt : null;
            console.log("Value of expiresAtFromServer (from responseData.data.holdExpiresAt):", expiresAtFromServer); 

            const finalCartItem = {
                ...seatDataForCart,
                holdExpiresAt: expiresAtFromServer, 
            };
            console.log("Final cart item constructed before adding to cart:", JSON.stringify(finalCartItem)); 
            
            addToCart(finalCartItem, finalCartItem.tripId); // This will trigger updateCartDisplay and timer start
        } else {
            console.error("Seat hold failed:", responseData.message);
            alert(
                `Không thể giữ ghế ${seatDataForCart.seatName}: ${
                    responseData.message || "Lỗi không xác định."
                }`
            );
            seatElement.classList.remove("selected"); // Ensure it's not marked selected if API fails
        }
    } catch (error) {
        seatElement.classList.remove("pending-hold");
        seatElement.classList.remove("selected");
        console.error("Error during seat hold API call:", error);
        alert("Lỗi kết nối khi cố gắng giữ ghế. Vui lòng thử lại.");
    }
}

/**
 * Handles the deselection of a seat (releasing the hold).
 * @param {object} cartItem - The cart item object to be deselected.
 * @param {HTMLElement} seatElement - The seat div element on the map (optional, may not be available if called from cart).
 */
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
            // Continue with client-side removal anyway for UX consistency. Server cleanup job is the backup.
        }
    } catch (error) {
        console.error("Error during seat release API call:", error);
        // Continue with client-side removal
    } finally {
        clearTimerAndRemoveFromClientCart(cartItem.seatID, cartItem.tripId);
        // If seatElementOnMap is provided (i.e., deselection from map), update its class
        if (seatElementOnMap) {
            seatElementOnMap.classList.remove("selected");
        }
        // If deselection was from cart trash icon, clearTimerAndRemoveFromClientCart will call updateCartDisplay
        // which removes the item from cart UI. The seat on map might need separate update if not handled by clearTimer...
    }
}

/**
 * Starts and manages the countdown timer for a specific cart item.
 * The timer display is updated on the trash icon of the cart item.
 * @param {object} cartItem - The cart item, must include holdExpiresAt, tripId, seatID.
 */
function startSeatHoldTimer(cartItem) {
    const timerKey = `${cartItem.tripId}_${cartItem.seatID}`;

    if (seatHoldTimers[timerKey]) {
        // Clear existing timer for this seat if any (e.g., on refresh hold)
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

/**
 * Called when a seat's hold timer expires.
 * @param {object} expiredCartItem - The cart item whose hold has expired.
 */
async function handleHoldExpired(expiredCartItem) {
    alert(
        `Thời gian giữ vé cho ghế ${expiredCartItem.seatName} (Tàu ${
            expiredCartItem.trainName || "N/A"
        }) đã hết. Vé đã được tự động hủy.`
    );

    // Attempt to release on server
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
            const errorData = await response.json().catch(() => ({})); // Try to parse error, default to empty obj
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
}

/**
 * Clears a seat's timer, removes it from the client-side shoppingCart, and updates UI.
 * @param {number|string} seatId - The ID of the seat to remove.
 * @param {string} tripId - The ID of the trip for the seat.
 */
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
        shoppingCart.splice(itemIndex, 1);
        console.log(
            `Removed seat ${seatId} (Trip: ${tripId}) from client cart.`
        );
    }

    const trainItemElem = document.querySelector(
        `.train-item[data-trip-id="${tripId}"]`
    );
    if (trainItemElem) {
        const seatElementOnMap = trainItemElem.querySelector(
            `.seat[data-seat-id="${seatId}"]`
        );
        if (seatElementOnMap) {
            seatElementOnMap.classList.remove("selected");
            // TODO: Refresh coach or mark as available based on actual server status if needed
        }
    }
    updateCartDisplay();
    saveCartToSession(); // Save cart to session after removing or when hold expires
}

/**
 * Handles the "Proceed to Booking" button action.
 */
async function initiateBookingProcess() {
    if (shoppingCart.length === 0) {
        alert("Vui lòng chọn ít nhất một vé để tiếp tục.");
        return;
    }

    const seatsToBook = shoppingCart.map((item) => ({
        tripId: item.tripId,
        seatId: item.seatID,
        coachId: item.coachId,
        legOriginStationId: item.legOriginStationId,
        legDestinationStationId: item.legDestinationStationId,
        // calculatedPrice: item.calculatedPrice // Optional: send price for server verification
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
            Object.values(seatHoldTimers).forEach(clearInterval); // Clear all client-side timers
            seatHoldTimers = {};
            shoppingCart = []; // Clear client cart as server now manages the state for payment page
            updateCartDisplay(); // Update UI to show empty cart
            saveCartToSession(); // Save the now-empty cart to session before redirect

            // Redirect to the URL provided by the server, or a default payment page
            window.location.href =
                responseData.redirectUrl || `${contextPath}/ticketPayment.jsp`;
        } else {
            alert(
                `Lỗi khi khởi tạo đặt vé: ${
                    responseData.message ||
                    "Không thể tiếp tục, vui lòng thử lại."
                }`
            );
            // Potentially refresh seat statuses if server indicates some seats became unavailable
            // This might involve re-fetching the current coach's seat layout.
        }
    } catch (error) {
        console.error("Error initiating booking:", error);
        alert(
            "Lỗi kết nối khi khởi tạo đặt vé. Vui lòng kiểm tra kết nối và thử lại."
        );
    }
}

// --- END OF NEW FUNCTIONS ---

function addSeatClickListeners(seatGridContainer, carriageContext) {
    let tripIdFromScope = null; // Renamed to avoid conflict with seatDataForAction.tripId
    let legOriginStationIdFromScope = null;
    let legDestinationStationIdFromScope = null;

    const trainItemElement = seatGridContainer.closest(".train-item");
    if (trainItemElement && trainItemElement.dataset.tripId) {
        tripIdFromScope = trainItemElement.dataset.tripId;
        // Ensure these are consistently named with how they are set in HTML (data-leg-origin-station-id)
        legOriginStationIdFromScope =
            trainItemElement.dataset.legOriginStationId;
        legDestinationStationIdFromScope =
            trainItemElement.dataset.legDestStationId; // dataset.legDestStationId from JSP
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
        .querySelectorAll(".seat.available") // Only attach to initially available seats
        .forEach((seatElement) => {
            seatElement.addEventListener("click", function () {
                // 'this' refers to seatElement
                const seatInfoString = this.dataset.seatInfo;
                if (!seatInfoString) {
                    console.warn("No seatInfo found for clicked seat:", this);
                    return;
                }

                const seatDataObject = JSON.parse(seatInfoString);

                // effectiveTripId should be the one associated with this specific seat's context
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

                const isCurrentlySelected = this.classList.contains("selected");

                // Prepare the full data object for the action (hold or release)
                const seatDataForAction = {
                    ...seatDataObject, // Includes seatID, seatName, calculatedPrice, etc.
                    tripId: effectiveTripId,
                    coachId: carriageContext?.coachId, // Get coachId from passed carriageContext
                    legOriginStationId: legOriginStationIdFromScope,
                    legDestinationStationId: legDestinationStationIdFromScope, // Use value from trainItemElement.dataset

                    // Display properties from context
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

                // Validate essential data for API calls
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

                if (!isCurrentlySelected) {
                    // Was not selected, now trying to select
                    console.log(
                        `Attempting to select seat: ${seatDataForAction.seatName}`
                    );
                    handleSeatSelectionAttempt(seatDataForAction, this);
                } else {
                    // Was selected, now trying to deselect
                    console.log(
                        `Attempting to deselect seat: ${seatDataForAction.seatName}`
                    );
                    const cartItemToDeselect = shoppingCart.find(
                        (item) =>
                            item.seatID === seatDataForAction.seatID &&
                            item.tripId === seatDataForAction.tripId
                    );
                    if (cartItemToDeselect) {
                        handleSeatDeselectionAttempt(cartItemToDeselect, this);
                    } else {
                        this.classList.remove("selected"); // Was selected on UI but not in cart
                        console.warn(
                            "Seat was marked selected on UI but not found in cart for deselection:",
                            seatDataForAction
                        );
                    }
                }
            });
        });
}

// Function to add a seat to the shopping cart
function addToCart(seatData, tripId) {
    // Ensure tripId is associated with the seat data in the cart
    const cartItem = { ...seatData, tripId: tripId };
    // Avoid adding duplicates for the same seat in the same trip
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
    saveCartToSession(); // Save cart to session after adding
}

// Function to remove a seat from the shopping cart
function removeFromCart(seatId, tripId) {
    const initialCartLength = shoppingCart.length;
    shoppingCart = shoppingCart.filter(
        (item) => !(item.seatID === seatId && item.tripId === tripId)
    );
    if (shoppingCart.length < initialCartLength) {
        console.log(
            `Seat with ID ${seatId} (Trip: ${tripId}) removed from cart.`
        );
    }
    updateCartDisplay();
}

// Function to update the cart display in the HTML
function updateCartDisplay() {
    console.log("Current Cart:", shoppingCart); // Keep console log for debugging
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
    console.log(`Total Price: ${totalPrice.toFixed(2)}`); // Keep console log for debugging

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

    // Initialize cart summary HTML structure if it doesn't exist
    if (!cartSummaryDetails) {
        // Remove the initial <p>(Chưa có vé nào)</p> if it exists
        const initialMessage = cartPlaceholder.querySelector("p");
        if (
            initialMessage &&
            initialMessage.textContent.includes("Chưa có vé nào")
        ) {
            initialMessage.remove();
        }

        cartSummaryDetails = document.createElement("div");
        cartSummaryDetails.id = "cart-summary-details";
        // Simplified HTML structure: only the list and the proceed button
        cartSummaryDetails.innerHTML = `
            <ul id="cart-items-list" class="cart-items-list-modern">
                <li class="cart-empty-message" style="text-align: center; padding: 10px; color: #777;">Chưa có vé nào được chọn.</li>
            </ul>
            <button id="proceed-to-booking-btn" class="button primary-button" style="display:none; margin-top: 15px; width: 100%; padding: 10px; font-size: 1em;">Tiếp tục đặt vé</button>
        `;
        cartPlaceholder.appendChild(cartSummaryDetails);

        // Add event listener for remove buttons ONCE when the list is created
        const cartItemsList =
            cartSummaryDetails.querySelector("#cart-items-list");
        if (cartItemsList) {
            cartItemsList.addEventListener("click", function (event) {
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
                                item.seatID.toString() === seatIdToRemove && // Ensure types match for comparison
                                item.tripId === tripIdToRemove
                        );
                        if (itemInCart) {
                            // Call handleSeatDeselectionAttempt instead of removeFromCart directly
                            handleSeatDeselectionAttempt(itemInCart, null); // Pass null for seatElement as it's a cart action
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

    // Get references to the dynamic elements
    // const cartCountElement = cartSummaryDetails.querySelector("#cart-item-count"); // Removed
    // const cartTotalPriceElement = cartSummaryDetails.querySelector("#cart-total-price"); // Removed
    const cartItemsListElement =
        cartSummaryDetails.querySelector("#cart-items-list");
    const proceedButton = cartSummaryDetails.querySelector(
        "#proceed-to-booking-btn"
    );
    const emptyMessageElement = cartItemsListElement.querySelector(
        ".cart-empty-message"
    );

    // Update cart count and total price - DOM elements removed, console log remains
    // if (cartCountElement) { // Removed
    //     cartCountElement.textContent = shoppingCart.length;
    // }
    // if (cartTotalPriceElement) { // Removed
    //     cartTotalPriceElement.textContent = totalPrice.toLocaleString('vi-VN', { style: 'decimal', minimumFractionDigits: 0, maximumFractionDigits: 0 }); // Format for VND
    // }

    // Update list of cart items
    if (cartItemsListElement) {
        // Clear previous items, but keep the empty message template if needed
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

                // Start timer if item has holdExpiresAt and no active timer
                if (cartItem.holdExpiresAt) {
                    const timerKey = `${cartItem.tripId}_${cartItem.seatID}`;
                    if (!seatHoldTimers[timerKey]) {
                        startSeatHoldTimer(cartItem);
                    }
                    // If timer is already running, startSeatHoldTimer's logic will update its display
                    // or it will be updated by its own interval.
                    // To ensure display is current on cart re-render, we might need to manually update timer text here too,
                    // or rely on startSeatHoldTimer to handle it if called.
                    // For now, startSeatHoldTimer handles DOM update.
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

    // Show/hide proceed button
    if (proceedButton) {
        proceedButton.style.display =
            shoppingCart.length > 0 ? "block" : "none";
        if (!proceedButton.dataset.listenerAttached) {
            // Attach listener only once
            proceedButton.addEventListener("click", initiateBookingProcess);
            proceedButton.dataset.listenerAttached = "true";
        }
    }
}
