var contextPath = "/train-booking-system"; // Fallback if not set, though it should be.

let currentCoachInfo = null; // To store coach metadata for use in createSeatElement if needed
let shoppingCart = []; // To store selected seat DTOs, with tripId for context
let seatHoldTimers = {}; // To store timers for individual seat holds { seatId: timerId }
const HOLD_DURATION_MS = 5 * 60 * 1000; // 5 minutes

let currentCarriage; // To store the currently selected carriage element for seat selection

document.addEventListener("DOMContentLoaded", function () {
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

function addSeatClickListeners(seatGridContainer, carriageContext) {
    let tripId = null;
    const trainItemElement = seatGridContainer.closest(".train-item"); // This is the correct trainItemElement for general trip info
    if (trainItemElement && trainItemElement.dataset.tripId) {
        tripId = trainItemElement.dataset.tripId;
    } else {
        // Fallback: try to parse from seatDetailsBlock ID if possible
        const seatDetailsBlock = seatGridContainer.closest(
            '[id^="seatDetailsBlock-"]'
        );
        if (seatDetailsBlock && seatDetailsBlock.id) {
            const idParts = seatDetailsBlock.id.split("-");
            if (idParts.length >= 3 && idParts[0] === "seatDetailsBlock") {
                tripId = idParts[idParts.length - 1]; // Assumes format seatDetailsBlock-tripLeg-tripId
            }
        }
    }

    if (!tripId) {
        console.warn(
            "Could not reliably determine tripId for seat grid:",
            seatGridContainer.id
        );
        // If tripId is absolutely essential for cart operations and cannot be found,
        // you might consider not adding click listeners or showing an error.
    }

    seatGridContainer.querySelectorAll(".seat.available").forEach((seat) => {
        seat.addEventListener("click", function () {
            this.classList.toggle("selected");
            const isSelected = this.classList.contains("selected");
            const seatInfoString = this.dataset.seatInfo;

            if (seatInfoString) {
                const seatDataObject = JSON.parse(seatInfoString);

                // Use the tripId determined for the grid.
                const effectiveTripId = tripId || seatDataObject.tripId; // Fallback to seatDataObject.tripId if grid tripId is null

                if (!effectiveTripId) {
                    console.error(
                        "CRITICAL: tripId is missing for cart operation. Seat:",
                        seatDataObject.seatName,
                        "Clicked element:",
                        this
                    );
                    return;
                }

                // Retrieve legOriginStationId and legDestStationId from the parent .train-item
                let legOriginStationId = null;
                let legDestStationId = null;
                // trainItemElement is already defined in the outer scope of addSeatClickListeners
                // but it refers to the container of the whole seat grid.
                // We need to ensure we're using the correct trainItemElement associated with THIS seat's grid.
                // The 'trainItemElement' passed to selectCarriage is the one we need,
                // and 'seatGridContainer' is 'seatDetailsBlock' which is a child of that 'trainItemElement'.
                // So, 'trainItemElement' from the outer scope should be correct.
                if (trainItemElement && trainItemElement.dataset) {
                    legOriginStationId =
                        trainItemElement.dataset.legOriginStationId;
                    legDestStationId =
                        trainItemElement.dataset.legDestStationId;
                }

                if (!legOriginStationId || !legDestStationId) {
                    console.warn(
                        `Could not determine leg origin/destination for cart item. Seat: ${seatDataObject.seatName} on Trip: ${effectiveTripId}. Check trainItemElement dataset.`,
                        trainItemElement
                    );
                    // Decide if this is critical enough to prevent adding to cart or if nulls are acceptable
                }

                console.log(
                    `Seat ${seatDataObject.seatName} (ID: ${seatDataObject.seatID}, Trip: ${effectiveTripId}, Leg: ${legOriginStationId}-${legDestStationId}) clicked. Selected: ${isSelected}`
                );

                if (isSelected) {
                    // Augment seatDataObject with comprehensive information for cart display
                    const seatDataForCart = {
                        ...seatDataObject,
                        // Core IDs for logic
                        tripId: effectiveTripId, // Already established
                        legOriginStationId:
                            trainItemElement?.dataset.legOriginStationId,
                        legDestStationId:
                            trainItemElement?.dataset.legDestStationId,

                        // Contextual display information
                        tripLeg: carriageContext?.tripLeg, // e.g., 'outbound', 'return'
                        coachPosition: carriageContext?.coachPosition,

                        // Information from the train item summary display
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
                        // Note: seatName and seatNumberInCoach are already in seatDataObject
                    };
                    addToCart(seatDataForCart, effectiveTripId); // effectiveTripId is fine, seatDataForCart now also contains it.
                } else {
                    // removeFromCart only needs seatID and tripId
                    removeFromCart(seatDataObject.seatID, effectiveTripId);
                }
            } else {
                console.warn("No seatInfo found for clicked seat:", this);
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
                    const tripIdToRemove = listItem?.dataset.tripId; // tripId is stored on cartItem, not directly on dataset here.
                    // We need to ensure tripId is available for removeFromCart.
                    // The cartItem itself has the tripId. We can find it from shoppingCart.

                    if (seatIdToRemove && tripIdToRemove) {
                        // tripIdToRemove will be derived from cartItem
                        // Find the actual tripId from the shoppingCart based on seatId, as dataset might not be fully reliable here for tripId
                        const itemInCart = shoppingCart.find(
                            (item) =>
                                item.seatID.toString() === seatIdToRemove &&
                                item.tripId === tripIdToRemove
                        );
                        if (itemInCart) {
                            removeFromCart(
                                itemInCart.seatID,
                                itemInCart.tripId
                            );
                        } else {
                            console.error(
                                "Could not find item in cart to remove for seatId:",
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
                // Store seatID and tripId on the list item for easier removal
                listItem.dataset.seatId = cartItem.seatID;
                listItem.dataset.tripId = cartItem.tripId; // Storing tripId here for the click handler

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
        // TODO: Add event listener for proceedButton if not already handled elsewhere
        // proceedButton.onclick = function() { /* handle proceeding to booking */ };
    }
}
