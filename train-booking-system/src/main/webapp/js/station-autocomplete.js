document.addEventListener("DOMContentLoaded", function () {
    const originalStationInput = document.getElementById("original-station");
    const destinationStationInput = document.getElementById("destination-station");
    const originalStationIdInput = document.getElementById("original-station-id");
    const destinationStationIdInput = document.getElementById("destination-station-id");
    const originalSuggestionsContainer = document.getElementById("original-station-suggestions");
    const destinationSuggestionsContainer = document.getElementById("destination-station-suggestions");

    let allStations = [];
    // activeAutocompleteInput is no longer strictly needed with individual suggestion states
    // let activeAutocompleteInput = null; 

    function removeDiacritics(str) {
        if (!str) return "";
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    async function fetchAllStations() {
        const contextPath = document.body.dataset.contextPath || (window.contextPath || '');
        try {
            const response = await fetch(`${contextPath}/api/stations/all`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allStations = await response.json();
        } catch (error) {
            console.error("Could not fetch stations:", error);
        }
    }

    function displaySuggestions(term, inputElement, suggestionsContainer, stationIdInputElement) {
        suggestionsContainer.innerHTML = "";
        inputElement.dataset.highlightedSuggestionIndex = "-1"; // Reset highlighted index
        
        if (!term.trim() || !allStations.length) {
            suggestionsContainer.style.display = "none";
            inputElement.dataset.currentSuggestions = JSON.stringify([]);
            return;
        }

        const normalizedSearchTerm = removeDiacritics(term).toLowerCase();

        const localCurrentSuggestions = allStations.map(station => ({
            ...station,
            normalizedName: removeDiacritics(station.stationName).toLowerCase()
        }))
        .filter(station => station.normalizedName.includes(normalizedSearchTerm))
        .sort((a, b) => {
            const aIsExact = a.normalizedName === normalizedSearchTerm;
            const bIsExact = b.normalizedName === normalizedSearchTerm;
            const aStartsWith = a.normalizedName.startsWith(normalizedSearchTerm);
            const bStartsWith = b.normalizedName.startsWith(normalizedSearchTerm);

            if (aIsExact && !bIsExact) return -1;
            if (!aIsExact && bIsExact) return 1;
            if (aStartsWith && !bStartsWith) return -1;
            if (!aStartsWith && bStartsWith) return 1;
            return a.stationName.localeCompare(b.stationName); // Alphabetical for other "includes"
        });
        
        inputElement.dataset.currentSuggestions = JSON.stringify(localCurrentSuggestions);

        if (localCurrentSuggestions.length === 0) {
            suggestionsContainer.style.display = "none";
            return;
        }

        localCurrentSuggestions.forEach((station, index) => {
            const suggestionItem = document.createElement("div");
            suggestionItem.classList.add("suggestion-item");
            suggestionItem.textContent = station.stationName;
            suggestionItem.dataset.stationId = station.stationId; // Store ID for easy access

            suggestionItem.addEventListener("click", () => {
                inputElement.value = station.stationName;
                stationIdInputElement.value = station.stationId;
                inputElement.classList.add('has-value');
                const label = inputElement.closest('.form-group')?.querySelector('label');
                if (label) label.classList.add('has-value');
                
                suggestionsContainer.innerHTML = "";
                suggestionsContainer.style.display = "none";
                inputElement.dataset.validStationSelected = "true";
                inputElement.dispatchEvent(new Event('change', { bubbles: true }));
            });

            if (index === 0) { // Automatically highlight the first item
                suggestionItem.classList.add("active");
                inputElement.dataset.highlightedSuggestionIndex = "0";
            }
            suggestionsContainer.appendChild(suggestionItem);
        });
        suggestionsContainer.style.display = "block";
    }

    function setupAutocomplete(inputElement, suggestionsContainer, stationIdInputElement) {
        if (!inputElement || !suggestionsContainer || !stationIdInputElement) return;

        inputElement.dataset.validStationSelected = "false"; // Initialize flag

        inputElement.addEventListener("input", function () {
            this.dataset.validStationSelected = "false"; // Reset flag when user types
            displaySuggestions(this.value, inputElement, suggestionsContainer, stationIdInputElement);
        });

        inputElement.addEventListener("focus", function () {
            // Only show suggestions on focus if:
            // 1. There's text in the input AND
            // 2. A valid station has NOT already been selected for this input.
            if (this.value.trim().length > 0 && this.dataset.validStationSelected !== "true") {
                displaySuggestions(this.value, inputElement, suggestionsContainer, stationIdInputElement);
            }
            // If a valid station IS selected (validStationSelected === "true"), 
            // focusing the input will do nothing initially. Suggestions will only reappear
            // if the user starts typing (handled by the 'input' event listener which resets validStationSelected).
        });

        inputElement.addEventListener("keydown", function(event) {
            const items = suggestionsContainer.querySelectorAll(".suggestion-item");
            let highlightedIndex = parseInt(this.dataset.highlightedSuggestionIndex || "-1", 10);
            const localCurrentSuggestions = JSON.parse(this.dataset.currentSuggestions || "[]");

            if (!items.length && event.key !== "Escape") return;

            switch (event.key) {
                case "ArrowDown":
                    event.preventDefault();
                    if (items.length > 0) {
                        items[highlightedIndex]?.classList.remove("active");
                        highlightedIndex = (highlightedIndex + 1) % items.length;
                        items[highlightedIndex]?.classList.add("active");
                        items[highlightedIndex]?.scrollIntoView({ block: "nearest" });
                        this.dataset.highlightedSuggestionIndex = highlightedIndex.toString();
                    }
                    break;
                case "ArrowUp":
                    event.preventDefault();
                     if (items.length > 0) {
                        items[highlightedIndex]?.classList.remove("active");
                        highlightedIndex = (highlightedIndex - 1 + items.length) % items.length;
                        items[highlightedIndex]?.classList.add("active");
                        items[highlightedIndex]?.scrollIntoView({ block: "nearest" });
                        this.dataset.highlightedSuggestionIndex = highlightedIndex.toString();
                    }
                    break;
                case "Enter":
                    event.preventDefault();
                    if (highlightedIndex > -1 && items[highlightedIndex]) {
                        items[highlightedIndex].click(); // Triggers selection logic including validStationSelected
                    } else if (localCurrentSuggestions.length > 0) { // Fallback: if no explicit highlight, select first
                        items[0].click();
                    }
                    suggestionsContainer.style.display = "none";
                    this.dataset.highlightedSuggestionIndex = "-1";
                    break;
                case "Escape":
                    suggestionsContainer.style.display = "none";
                    this.dataset.highlightedSuggestionIndex = "-1";
                    break;
            }
        });
        
        inputElement.addEventListener("blur", function() {
            const inputSelf = this; // Reference to the input element for use in setTimeout

            setTimeout(() => {
                // If focus has moved to one of its own suggestion items, a click is pending, so don't interfere.
                if (suggestionsContainer.contains(document.activeElement) && 
                    document.activeElement.classList.contains('suggestion-item')) {
                    return; 
                }

                // If a valid station was explicitly chosen by click or Enter, validStationSelected will be "true".
                // In this case, the input is valid, so do nothing more to clear it.
                if (inputSelf.dataset.validStationSelected === "true") {
                    suggestionsContainer.style.display = "none"; // Still hide suggestions
                    inputSelf.dataset.highlightedSuggestionIndex = "-1"; // Reset highlight
                    return;
                }

                // At this point, no valid suggestion was explicitly selected.
                // Now, check if the current text *happens* to be an exact match for a station.
                let textIsExactMatch = false;
                if (inputSelf.value.trim() !== "") {
                    const normalizedCurrentText = removeDiacritics(inputSelf.value).toLowerCase();
                    for (const station of allStations) {
                        if (removeDiacritics(station.stationName).toLowerCase() === normalizedCurrentText) {
                            // Text is an exact match. Update to canonical name and set ID.
                            inputSelf.value = station.stationName; 
                            stationIdInputElement.value = station.stationId;
                            inputSelf.classList.add('has-value');
                            const label = inputSelf.closest('.form-group')?.querySelector('label');
                            if (label) label.classList.add('has-value');
                            inputSelf.dataset.validStationSelected = "true"; // Now it's a valid selection
                            textIsExactMatch = true;
                            break;
                        }
                    }
                }

                // If, after all checks, the text is not an exact match for any station, clear it.
                if (!textIsExactMatch) {
                    inputSelf.value = "";
                    stationIdInputElement.value = "";
                    inputSelf.classList.remove('has-value');
                    const label = inputSelf.closest('.form-group')?.querySelector('label');
                    if (label) label.classList.remove('has-value');
                    inputSelf.dataset.validStationSelected = "false"; 
                }
                
                suggestionsContainer.style.display = "none"; // Always hide suggestions on blur
                inputSelf.dataset.highlightedSuggestionIndex = "-1";

            }, 150); // Delay to allow click on suggestion to register
        });

        inputElement.addEventListener("change", function() { // Handles programmatic changes or paste
            if (this.value === "") {
                stationIdInputElement.value = "";
                this.classList.remove('has-value');
                const label = this.closest('.form-group')?.querySelector('label');
                if (label) label.classList.remove('has-value');
                this.dataset.validStationSelected = "false";
            } else {
                // If value changes, it's not yet confirmed as a valid selection from suggestions
                // unless a click or Enter event sets validStationSelected to true.
                // The blur event will handle final validation if not selected from suggestions.
                this.dataset.validStationSelected = "false"; 
                this.classList.add('has-value'); // Keep label floated if there's text
                const label = this.closest('.form-group')?.querySelector('label');
                if (label) label.classList.add('has-value');
            }
        });
    }

    setupAutocomplete(originalStationInput, originalSuggestionsContainer, originalStationIdInput);
    setupAutocomplete(destinationStationInput, destinationSuggestionsContainer, destinationStationIdInput);

    // Global click listener to hide suggestions if clicked outside
    // This is a fallback; blur with timeout should handle most cases.
    document.addEventListener("click", function (event) {
        const isOriginTarget = originalStationInput?.contains(event.target) || originalSuggestionsContainer?.contains(event.target);
        const isDestinationTarget = destinationStationInput?.contains(event.target) || destinationSuggestionsContainer?.contains(event.target);

        if (!isOriginTarget && originalSuggestionsContainer) {
            originalSuggestionsContainer.style.display = "none";
        }
        if (!isDestinationTarget && destinationSuggestionsContainer) {
            destinationSuggestionsContainer.style.display = "none";
        }
    });
    
    fetchAllStations();
});
