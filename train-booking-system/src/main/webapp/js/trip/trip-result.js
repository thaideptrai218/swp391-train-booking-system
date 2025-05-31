// trip-result.js

// Assume contextPath is set globally in the JSP, e.g., <script>var contextPath = '${pageContext.request.contextPath}';</script>
// If not, this needs to be adjusted or passed appropriately.
var contextPath = "/train-booking-system"; // Fallback if not set, though it should be.

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
            collapsedSummary.addEventListener("keydown", (event) => {
                if (event.key === "Enter" || event.key === " ") {
                    event.preventDefault();
                    toggleTrainItemDetails(item);
                }
            });
        }

        // Use event delegation for carriage items for potentially dynamic content
        const expandedDetails = item.querySelector(".expanded-details");
        if (expandedDetails) {
            expandedDetails.addEventListener("click", function (event) {
                const carriageItem = event.target.closest(".carriage-item");
                if (carriageItem) {
                    selectCarriage(carriageItem, item);
                }
            });
            expandedDetails.addEventListener("keydown", function (event) {
                const carriageItem = event.target.closest(".carriage-item");
                if (
                    carriageItem &&
                    (event.key === "Enter" || event.key === " ")
                ) {
                    event.preventDefault();
                    selectCarriage(carriageItem, item);
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

        fetch(
            `${contextPath}/getCoachSeats?tripId=${tripId}&coachId=${coachId}&legOriginStationId=${legOriginStationId}&legDestinationStationId=${legDestStationId}`
        )
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
                generateSeatLayout(
                    seatDetailsBlock,
                    seatDataList,
                    coachLayoutInfo
                );
            })
            .catch((error) => {
                console.error("Error fetching seat data:", error);
                seatDetailsBlock.innerHTML = `<p>Lỗi khi tải sơ đồ ghế: ${error.message}</p>`;
            });
    }
}

function generateSeatLayout(seatDetailsBlock, seatDataList, coachLayoutInfo) {
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
    addSeatClickListeners(seatDetailsBlock);
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
    seatDiv.dataset.seatId = seatDto.seatID;
    seatDiv.dataset.seatName = seatDto.seatName;
    seatDiv.textContent = seatDto.seatNumberInCoach; // Display SeatName, fallback to number

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

function addSeatClickListeners(seatGridContainer) {
    seatGridContainer.querySelectorAll(".seat.available").forEach((seat) => {
        seat.addEventListener("click", function () {
            this.classList.toggle("selected");
            // TODO: Add logic to manage selected seats (e.g., add to a list, update price)
            console.log(
                `Seat ${
                    this.dataset.seatName || this.textContent
                } clicked. Selected: ${this.classList.contains("selected")}`
            );
        });
    });
}
