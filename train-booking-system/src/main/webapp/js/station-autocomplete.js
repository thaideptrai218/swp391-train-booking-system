document.addEventListener("DOMContentLoaded", function () {
    // --- Station Autocomplete Logic ---
    const originalStationInput = document.getElementById("original-station");
    const destinationStationInput = document.getElementById("destination-station");
    const originalStationIdInput = document.getElementById("original-station-id");
    const destinationStationIdInput = document.getElementById("destination-station-id");
    const originalSuggestionsContainer = document.getElementById("original-station-suggestions");
    const destinationSuggestionsContainer = document.getElementById("destination-station-suggestions");

    let allStations = [];
    let activeAutocompleteInput = null; // To track which input is currently active for suggestions

    function removeDiacritics(str) {
        if (!str) return ""; // Handle null or undefined input
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
        if (!term || !allStations.length) {
            suggestionsContainer.style.display = "none";
            return;
        }

        const lowerTerm = term.toLowerCase(); // Keep this for initial input normalization if desired, but primary comparison uses normalized versions
        const normalizedSearchTerm = removeDiacritics(term).toLowerCase();

        const filteredStations = allStations.filter(station => {
            const normalizedStationName = removeDiacritics(station.stationName).toLowerCase();
            return normalizedStationName.includes(normalizedSearchTerm);
        });

        if (filteredStations.length === 0) {
            suggestionsContainer.style.display = "none";
            return;
        }

        filteredStations.forEach(station => {
            const suggestionItem = document.createElement("div");
            suggestionItem.classList.add("suggestion-item");
            suggestionItem.textContent = station.stationName;
            suggestionItem.addEventListener("click", () => {
                inputElement.value = station.stationName;
                stationIdInputElement.value = station.stationId;
                
                // Ensure 'has-value' class is on the input for label styling
                inputElement.classList.add('has-value');
                // Also ensure the label (if it's a sibling or found via a common parent) gets 'has-value'
                const label = inputElement.closest('.form-group')?.querySelector('label');
                if (label) {
                    label.classList.add('has-value');
                }

                suggestionsContainer.innerHTML = "";
                suggestionsContainer.style.display = "none";
                inputElement.dispatchEvent(new Event('change', { bubbles: true }));
                // No need to explicitly dispatch blur here if the click outside handles hiding suggestions
            });
            suggestionsContainer.appendChild(suggestionItem);
        });
        suggestionsContainer.style.display = "block";
        activeAutocompleteInput = inputElement; 
    }

    function setupAutocomplete(inputElement, suggestionsContainer, stationIdInputElement) {
        if (!inputElement || !suggestionsContainer || !stationIdInputElement) return;

        inputElement.addEventListener("input", function () {
            displaySuggestions(this.value, inputElement, suggestionsContainer, stationIdInputElement);
        });

        inputElement.addEventListener("focus", function () {
            if (this.value.length > 0) { 
                 displaySuggestions(this.value, inputElement, suggestionsContainer, stationIdInputElement);
            }
        });
        
        inputElement.addEventListener("change", function() {
            if (this.value === "") {
                stationIdInputElement.value = "";
                // Remove 'has-value' from input and label if input is cleared
                this.classList.remove('has-value');
                const label = this.closest('.form-group')?.querySelector('label');
                if (label) {
                    label.classList.remove('has-value');
                }
            } else {
                // Ensure 'has-value' is present if input gets a value through other means (e.g. pasting)
                this.classList.add('has-value');
                const label = this.closest('.form-group')?.querySelector('label');
                if (label) {
                    label.classList.add('has-value');
                }
            }
        });

        // Add blur listener to remove 'has-value' if input is empty
        inputElement.addEventListener("blur", function() {
            if (this.value === "") {
                this.classList.remove('has-value');
                const label = this.closest('.form-group')?.querySelector('label');
                if (label) {
                    label.classList.remove('has-value');
                }
            }
        });
    }

    setupAutocomplete(originalStationInput, originalSuggestionsContainer, originalStationIdInput);
    setupAutocomplete(destinationStationInput, destinationSuggestionsContainer, destinationStationIdInput);

    document.addEventListener("click", function (event) {
        const isClickInsideOrigin = originalStationInput && originalStationInput.contains(event.target) || 
                                   originalSuggestionsContainer && originalSuggestionsContainer.contains(event.target);
        const isClickInsideDestination = destinationStationInput && destinationStationInput.contains(event.target) || 
                                        destinationSuggestionsContainer && destinationSuggestionsContainer.contains(event.target);

        if (!isClickInsideOrigin && originalSuggestionsContainer) {
            originalSuggestionsContainer.style.display = "none";
        }
        if (!isClickInsideDestination && destinationSuggestionsContainer) {
            destinationSuggestionsContainer.style.display = "none";
        }
        if (!isClickInsideOrigin && !isClickInsideDestination) {
            activeAutocompleteInput = null;
        }
    });
    
    fetchAllStations();
});
