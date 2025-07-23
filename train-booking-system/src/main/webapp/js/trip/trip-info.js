/**
 * Trip Information Page JavaScript
 * Handles trip search, display, and detailed information functionality
 */

class TripInfoManager {
    constructor() {
        this.contextPath = document.body.getAttribute('data-context-path') || '';
        this.currentSearchData = null;
        this.currentSearchResults = [];
        this.selectedTripId = null;
        this.allStations = [];
        
        this.initializeEventListeners();
        this.initializeDatePicker();
        this.initializeAutocomplete();
    }

    initializeEventListeners() {
        const searchForm = document.getElementById('tripSearchForm');
        if (searchForm) {
            searchForm.addEventListener('submit', this.handleSearchSubmit.bind(this));
        }
        
        // Debug: Test station API
        this.debugStationAPI();
    }
    
    async debugStationAPI() {
        try {
            console.log('Testing station API...');
            const response = await fetch(`${this.contextPath}/api/stations/all`);
            const stations = await response.json();
            console.log('Stations API response:', stations);
            if (stations && stations.length > 0) {
                console.log('Sample station:', stations[0]);
            }
        } catch (error) {
            console.error('Station API test failed:', error);
        }
    }

    async initializeAutocomplete() {
        // Fetch all stations first
        await this.fetchAllStations();
        
        // Setup autocomplete for both inputs
        this.setupAutocomplete('origin-station', 'origin-station-id', 'origin-station-suggestions');
        this.setupAutocomplete('destination-station', 'destination-station-id', 'destination-station-suggestions');
    }

    async fetchAllStations() {
        try {
            const response = await fetch(`${this.contextPath}/api/stations/all`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            this.allStations = await response.json();
            console.log('Fetched stations:', this.allStations.length);
        } catch (error) {
            console.error("Could not fetch stations:", error);
            this.allStations = [];
        }
    }

    removeDiacritics(str) {
        if (!str) return "";
        return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
    }

    setupAutocomplete(inputId, hiddenInputId, suggestionsId) {
        const inputElement = document.getElementById(inputId);
        const hiddenInputElement = document.getElementById(hiddenInputId);
        const suggestionsContainer = document.getElementById(suggestionsId);

        if (!inputElement || !hiddenInputElement || !suggestionsContainer) {
            console.error(`Autocomplete setup failed for ${inputId}. Missing elements:`, {
                input: !!inputElement,
                hidden: !!hiddenInputElement,
                suggestions: !!suggestionsContainer
            });
            return;
        }

        console.log(`Setting up autocomplete for ${inputId}`);

        let highlightedIndex = -1;

        inputElement.addEventListener('input', (e) => {
            highlightedIndex = -1;
            this.displaySuggestions(e.target.value, inputElement, suggestionsContainer, hiddenInputElement);
        });

        inputElement.addEventListener('focus', (e) => {
            if (e.target.value.trim().length > 0) {
                this.displaySuggestions(e.target.value, inputElement, suggestionsContainer, hiddenInputElement);
            }
        });

        inputElement.addEventListener('blur', (e) => {
            // Delay to allow click on suggestion
            setTimeout(() => {
                suggestionsContainer.style.display = 'none';
                highlightedIndex = -1;
            }, 150);
        });

        inputElement.addEventListener('keydown', (e) => {
            const suggestions = suggestionsContainer.querySelectorAll('.suggestion-item');
            
            switch (e.key) {
                case 'ArrowDown':
                    e.preventDefault();
                    if (suggestions.length > 0) {
                        if (highlightedIndex >= 0) {
                            suggestions[highlightedIndex].classList.remove('active');
                        }
                        highlightedIndex = (highlightedIndex + 1) % suggestions.length;
                        suggestions[highlightedIndex].classList.add('active');
                    }
                    break;
                    
                case 'ArrowUp':
                    e.preventDefault();
                    if (suggestions.length > 0) {
                        if (highlightedIndex >= 0) {
                            suggestions[highlightedIndex].classList.remove('active');
                        }
                        highlightedIndex = highlightedIndex <= 0 ? suggestions.length - 1 : highlightedIndex - 1;
                        suggestions[highlightedIndex].classList.add('active');
                    }
                    break;
                    
                case 'Enter':
                    e.preventDefault();
                    if (highlightedIndex >= 0 && suggestions[highlightedIndex]) {
                        suggestions[highlightedIndex].click();
                    }
                    break;
                    
                case 'Escape':
                    suggestionsContainer.style.display = 'none';
                    highlightedIndex = -1;
                    break;
            }
        });
    }

    displaySuggestions(term, inputElement, suggestionsContainer, hiddenInputElement) {
        suggestionsContainer.innerHTML = "";
        
        if (!term.trim() || !this.allStations.length) {
            suggestionsContainer.style.display = "none";
            return;
        }

        const normalizedSearchTerm = this.removeDiacritics(term).toLowerCase();

        const matchingStations = this.allStations
            .map(station => ({
                ...station,
                normalizedName: this.removeDiacritics(station.stationName).toLowerCase()
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
                return a.stationName.localeCompare(b.stationName);
            })
            .slice(0, 10); // Limit to 10 suggestions

        if (matchingStations.length === 0) {
            suggestionsContainer.style.display = "none";
            return;
        }

        matchingStations.forEach((station, index) => {
            const suggestionItem = document.createElement("div");
            suggestionItem.classList.add("suggestion-item");
            suggestionItem.textContent = station.stationName;

            suggestionItem.addEventListener("click", () => {
                inputElement.value = station.stationName;
                hiddenInputElement.value = station.stationId;
                suggestionsContainer.style.display = "none";
                
                // Clear validation message if any
                console.log(`Selected station: ${station.stationName} (ID: ${station.stationId})`);
            });

            if (index === 0) {
                suggestionItem.classList.add("active");
            }
            suggestionsContainer.appendChild(suggestionItem);
        });

        suggestionsContainer.style.display = "block";
    }

    initializeDatePicker() {
        const dateInput = document.getElementById('departure-date');
        if (dateInput) {
            flatpickr(dateInput, {
                dateFormat: 'Y-m-d',
                minDate: 'today',
                maxDate: new Date().fp_incr(90), // 90 days from today
                locale: 'vi'
            });
        }
    }

    async handleSearchSubmit(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const searchData = {
            originStationId: document.getElementById('origin-station-id').value,
            originStationName: document.getElementById('origin-station').value,
            destinationStationId: document.getElementById('destination-station-id').value,
            destinationStationName: document.getElementById('destination-station').value,
            departureDate: formData.get('departure-date')
        };

        // Validate form data
        if (!this.validateSearchData(searchData)) {
            return;
        }

        this.currentSearchData = searchData;
        await this.searchTrips(searchData);
    }

    validateSearchData(data) {
        if (!data.originStationId || !data.destinationStationId) {
            this.showError('Vui lòng chọn ga đi và ga đến từ danh sách gợi ý');
            return false;
        }

        if (data.originStationId === data.destinationStationId) {
            this.showError('Ga đi và ga đến không thể giống nhau');
            return false;
        }

        if (!data.departureDate) {
            this.showError('Vui lòng chọn ngày đi');
            return false;
        }

        return true;
    }

    async searchTrips(searchData) {
        try {
            // No loading animation for trip-cards-container
            const params = new URLSearchParams({
                'originStationId': searchData.originStationId,
                'destinationStationId': searchData.destinationStationId,
                'departureDate': searchData.departureDate
            });

            const response = await fetch(`${this.contextPath}/api/trip/search?${params}`);
            const result = await response.json();

            if (result.success && result.trips && result.trips.length > 0) {
                this.currentSearchResults = result.trips;
                this.displayTripCards(result.trips, searchData);
            } else {
                this.currentSearchResults = [];
                this.showNoResults();
            }

        } catch (error) {
            console.error('Search error:', error);
            this.showError('Có lỗi xảy ra khi tìm kiếm. Vui lòng thử lại.');
        }
    }

    displayTripCards(trips, searchData) {
        const container = document.getElementById('tripCardsContainer');
        const resultsSection = document.getElementById('searchResults');
        
        if (!container || !resultsSection) return;

        container.innerHTML = '';

        trips.forEach(trip => {
            const tripCard = this.createTripCard(trip, searchData);
            container.appendChild(tripCard);
        });

        resultsSection.style.display = 'block';
        resultsSection.scrollIntoView({ behavior: 'smooth' });
    }

    createTripCard(trip, searchData) {
        const card = document.createElement('div');
        card.className = 'trip-card';
        card.dataset.tripId = trip.tripId;

        // Use TripSearchResultDTO fields: scheduledDepartureAsDate, scheduledArrivalAsDate
        const departureTime = this.formatDateTime(trip.scheduledDepartureAsDate);
        const arrivalTime = this.formatDateTime(trip.scheduledArrivalAsDate);
        const duration = this.formatDuration(trip.durationMinutes);

        card.innerHTML = `
            <div class="trip-card-header">
                <div class="train-info">
                    <i class="fas fa-train train-icon"></i>
                    <span class="train-code">${trip.trainName}</span>
                    <span class="duration-badge">${duration}</span>
                </div>
            </div>
            <div class="trip-route-info">
                <div class="station-info">
                    <div class="station-name">${trip.originStationName || searchData.originStationName}</div>
                    <div class="station-time">${departureTime}</div>
                </div>
                <div class="route-arrow">➡️</div>
                <div class="station-info">
                    <div class="station-name">${trip.destinationStationName || searchData.destinationStationName}</div>
                    <div class="station-time">${arrivalTime}</div>
                </div>
            </div>
        `;

        // Add click event listener
        card.addEventListener('click', () => {
            this.selectTrip(trip.tripId, card);
        });

        return card;
    }

    async selectTrip(tripId, cardElement) {
        // Update UI selection
        document.querySelectorAll('.trip-card').forEach(card => {
            card.classList.remove('selected');
        });
        cardElement.classList.add('selected');

        this.selectedTripId = tripId;

        // Find the trip data from our current search results for consistent basic info
        const selectedTrip = this.currentSearchResults.find(trip => trip.tripId === tripId);
        
        if (selectedTrip) {
            // Show consistent basic trip info immediately
            this.displayTicketContainerFromSearchData(selectedTrip);
            
            // Then load detailed stations information
            await this.loadAndDisplayStationsTable(tripId);
        } else {
            // Fallback to loading from API if not found
            await this.loadTripDetails(tripId);
        }
        
        // Show the details section
        const detailsSection = document.getElementById('tripDetailsSection');
        detailsSection.style.display = 'block';
        detailsSection.scrollIntoView({ behavior: 'smooth' });
    }

    async loadAndDisplayStationsTable(tripId) {
        try {
            const response = await fetch(`${this.contextPath}/api/trip/info?tripId=${tripId}`);
            const result = await response.json();

            if (result.success && result.stations) {
                this.displayStationsTable(result.stations);
                // Show the stations table container
                const stationsContainer = document.getElementById('stationsTableContainer');
                if (stationsContainer) {
                    stationsContainer.style.display = 'block';
                }
            } else {
                console.warn('No stations data available');
                // Hide the stations table if no data
                const stationsContainer = document.getElementById('stationsTableContainer');
                if (stationsContainer) {
                    stationsContainer.style.display = 'none';
                }
            }

        } catch (error) {
            console.error('Stations table loading error:', error);
            // Hide the stations table on error
            const stationsContainer = document.getElementById('stationsTableContainer');
            if (stationsContainer) {
                stationsContainer.style.display = 'none';
            }
        }
    }

    async loadTripDetails(tripId) {
        try {
            const response = await fetch(`${this.contextPath}/api/trip/info?tripId=${tripId}`);
            const result = await response.json();

            if (result.success) {
                this.displayTripDetails(result);
            } else {
                this.showError(result.error || 'Không thể tải thông tin chuyến tàu');
            }

        } catch (error) {
            console.error('Trip details error:', error);
            this.showError('Có lỗi xảy ra khi tải thông tin chi tiết');
        }
    }

    displayTripDetailsFromSearchData(trip) {
        console.log('Trip data from search:', trip);
        
        this.displayTicketContainerFromSearchData(trip);
        // For now, hide stations table since we don't have station-by-station data from search
        const stationsContainer = document.getElementById('stationsTableContainer');
        if (stationsContainer) {
            stationsContainer.style.display = 'none';
        }
        
        const detailsSection = document.getElementById('tripDetailsSection');
        detailsSection.style.display = 'block';
        detailsSection.scrollIntoView({ behavior: 'smooth' });
    }

    displayTripDetails(tripData) {
        console.log('Trip details data:', tripData);
        console.log('Stations data:', tripData.stations);
        
        this.displayTicketContainer(tripData);
        this.displayStationsTable(tripData.stations);
        
        const detailsSection = document.getElementById('tripDetailsSection');
        detailsSection.style.display = 'block';
        detailsSection.scrollIntoView({ behavior: 'smooth' });
    }

    displayTicketContainerFromSearchData(trip) {
        const container = document.getElementById('ticketContainer');
        if (!container) return;

        const trainName = trip.trainName;
        
        // Use the same date formatting as trip cards for consistency
        const departureDateTime = this.formatDateTime(trip.scheduledDepartureAsDate);
        const arrivalDateTime = this.formatDateTime(trip.scheduledArrivalAsDate);
        const duration = this.formatDuration(trip.durationMinutes);

        container.innerHTML = `
            <h2 class="train-title">
                <i class="fas fa-train"></i>
                Tàu ${trainName}
            </h2>
            
            <div class="ticket-info">
                <div class="station">
                    <p class="label">${trip.originStationName || this.currentSearchData.originStationName}</p>
                    <p class="datetime">${departureDateTime}</p>
                </div>
                
                <div class="arrow">
                    <i class="fas fa-long-arrow-alt-right"></i>
                </div>
                
                <div class="station">
                    <p class="label">${trip.destinationStationName || this.currentSearchData.destinationStationName}</p>
                    <p class="datetime">${arrivalDateTime}</p>
                </div>
            </div>
            
            <div class="duration">
                ${duration}
            </div>
        `;
    }

    displayTicketContainer(tripData) {
        const container = document.getElementById('ticketContainer');
        if (!container) return;

        const summary = tripData.summary;
        const trainName = summary.trainName;
        
        // Format dates and times - handle the concatenated date-time string from backend
        let departureDateTime = '--';
        let arrivalDateTime = '--';
        
        try {
            if (summary.originStation.departureTime) {
                const depDateTime = new Date(summary.originStation.departureTime);
                if (!isNaN(depDateTime.getTime())) {
                    departureDateTime = this.formatFullDateTime(summary.originStation.departureTime);
                }
            }
            
            if (summary.destinationStation.arrivalTime) {
                const arrDateTime = new Date(summary.destinationStation.arrivalTime);
                if (!isNaN(arrDateTime.getTime())) {
                    arrivalDateTime = this.formatFullDateTime(summary.destinationStation.arrivalTime);
                }
            }
        } catch (error) {
            console.error('Error formatting ticket container dates:', error);
        }
        
        const duration = this.formatDuration(summary.totalDuration);

        container.innerHTML = `
            <h2 class="train-title">
                <i class="fas fa-train"></i>
                Tàu ${trainName}
            </h2>
            
            <div class="ticket-info">
                <div class="station">
                    <p class="label">${summary.originStation.stationName}</p>
                    <p class="datetime">${departureDateTime}</p>
                </div>
                
                <div class="arrow">
                    <i class="fas fa-long-arrow-alt-right"></i>
                </div>
                
                <div class="station">
                    <p class="label">${summary.destinationStation.stationName}</p>
                    <p class="datetime">${arrivalDateTime}</p>
                </div>
            </div>
            
            <div class="duration">
                ${duration}
            </div>
        `;
    }

    displayStationsTable(stations) {
        const container = document.getElementById('stationsTableContainer');
        if (!container || !stations || stations.length === 0) return;

        // Get user's selected origin and destination station IDs for highlighting
        const originStationId = this.currentSearchData ? parseInt(this.currentSearchData.originStationId) : null;
        const destinationStationId = this.currentSearchData ? parseInt(this.currentSearchData.destinationStationId) : null;
        
        // Find the indices of origin and destination stations for journey segment highlighting
        let originIndex = -1;
        let destinationIndex = -1;
        
        if (originStationId && destinationStationId) {
            stations.forEach((station, index) => {
                if (station.stationId === originStationId) originIndex = index;
                if (station.stationId === destinationStationId) destinationIndex = index;
            });
        }

        let tableHTML = `
            <table class="train-stations-table">
                <thead>
                    <tr>
                        <th colspan="6">Các ga trong hành trình</th>
                    </tr>
                    <tr>
                        <th>STT</th>
                        <th>Ga đi</th>
                        <th>Cự ly (Km)</th>
                        <th>Ngày đi</th>
                        <th>Giờ đến</th>
                        <th>Giờ đi</th>
                    </tr>
                </thead>
                <tbody>
        `;

        stations.forEach((station, index) => {
            // For the first station, no arrival time; for the last station, no departure time
            const arrivalTime = index === 0 ? '--' : 
                (station.scheduledDepartureTime ? this.formatTime(station.scheduledDepartureTime) : '--');
            const departureTime = index === stations.length - 1 ? '--' : 
                (station.scheduledDepartureTime ? this.formatTime(station.scheduledDepartureTime) : '--');
            const date = station.scheduledDepartureDate ? 
                this.formatDate(station.scheduledDepartureDate) : '--';

            // Determine if this station should be highlighted
            let rowClass = '';
            let stationLabel = '';
            
            if (station.stationId === originStationId) {
                rowClass = 'origin-station-row';
                stationLabel = ' <span class="station-label origin-label">Ga đi</span>';
            } else if (station.stationId === destinationStationId) {
                rowClass = 'destination-station-row';
                stationLabel = ' <span class="station-label destination-label">Ga đến</span>';
            } else if (originIndex !== -1 && destinationIndex !== -1 && 
                       index > Math.min(originIndex, destinationIndex) && 
                       index < Math.max(originIndex, destinationIndex)) {
                // This station is between the user's origin and destination
                rowClass = 'journey-segment-row';
            }

            tableHTML += `
                <tr class="${rowClass}">
                    <td>${index + 1}</td>
                    <td class="station-name-cell">${station.stationName}${stationLabel}</td>
                    <td>${Math.round(station.distanceFromStart || 0)}</td>
                    <td>${date}</td>
                    <td>${arrivalTime}</td>
                    <td>${departureTime}</td>
                </tr>
            `;
        });

        tableHTML += `
                </tbody>
            </table>
        `;

        // Add legend if we have highlighted stations
        if (originStationId || destinationStationId) {
            const hasJourneySegment = originIndex !== -1 && destinationIndex !== -1 && 
                                    Math.abs(destinationIndex - originIndex) > 1;
            
            tableHTML += `
                <div class="stations-legend">
                    <h4><i class="fas fa-info-circle"></i> Chú thích</h4>
                    <div class="legend-items">
                        ${originStationId ? '<div class="legend-item"><span class="legend-color origin-legend"></span> Ga đi của bạn</div>' : ''}
                        ${destinationStationId ? '<div class="legend-item"><span class="legend-color destination-legend"></span> Ga đến của bạn</div>' : ''}
                        ${hasJourneySegment ? '<div class="legend-item"><span class="legend-color journey-legend"></span> Các ga trung gian trong hành trình</div>' : ''}
                    </div>
                </div>
            `;
        }

        container.innerHTML = tableHTML;
    }

    // Utility methods for formatting
    formatDateTime(dateTimeString) {
        if (!dateTimeString) return '--';
        
        try {
            const date = new Date(dateTimeString);
            if (isNaN(date.getTime())) {
                console.warn('Invalid date:', dateTimeString);
                return '--';
            }
            
            // Return just time for trip cards (dd/MM HH:mm format like in tripResult.jsp)
            return date.toLocaleDateString('vi-VN', {
                day: '2-digit',
                month: '2-digit'
            }) + ' ' + date.toLocaleTimeString('vi-VN', {
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
            });
        } catch (error) {
            console.error('Error formatting date time:', dateTimeString, error);
            return '--';
        }
    }

    formatFullDateTime(dateTimeString) {
        if (!dateTimeString) return '--';
        const date = new Date(dateTimeString);
        return `${this.formatDate(date)} ${this.formatTime(date)}`;
    }

    formatDate(date) {
        if (!date) return '--';
        
        try {
            // Handle different date formats
            let dateObj;
            if (typeof date === 'string') {
                // Handle string dates like "2025-07-24" or date-time strings
                dateObj = new Date(date);
            } else if (date instanceof Date) {
                dateObj = date;
            } else if (Array.isArray(date) && date.length >= 3) {
                // Handle LocalDate array format [year, month, day]
                dateObj = new Date(date[0], date[1] - 1, date[2]); // month is 0-indexed
            } else {
                console.warn('Unexpected date format:', date);
                return '--';
            }
            
            // Check if date is valid
            if (isNaN(dateObj.getTime())) {
                console.warn('Invalid date:', date);
                return '--';
            }
            
            return dateObj.toLocaleDateString('vi-VN', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
            });
        } catch (error) {
            console.error('Error formatting date:', date, error);
            return '--';
        }
    }

    formatTime(time) {
        if (!time) return '--';
        
        try {
            let timeObj;
            if (typeof time === 'string') {
                // Handle time strings like "06:00:00" or "06:00"
                if (time.includes(':')) {
                    return time.substring(0, 5); // Return HH:MM format
                }
                timeObj = new Date(time);
            } else if (time instanceof Date) {
                timeObj = time;
            } else if (Array.isArray(time) && time.length >= 2) {
                // Handle LocalTime array format [hour, minute, second]
                const hours = String(time[0]).padStart(2, '0');
                const minutes = String(time[1]).padStart(2, '0');
                return `${hours}:${minutes}`;
            } else {
                console.warn('Unexpected time format:', time);
                return '--';
            }
            
            if (timeObj && !isNaN(timeObj.getTime())) {
                return timeObj.toLocaleTimeString('vi-VN', {
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: false
                });
            }
            
            return '--';
        } catch (error) {
            console.error('Error formatting time:', time, error);
            return '--';
        }
    }

    formatDuration(duration) {
        if (!duration) return '--';
        
        try {
            let minutes;
            
            // Handle different duration formats
            if (typeof duration === 'number') {
                minutes = Math.round(duration);
            } else if (typeof duration === 'string') {
                minutes = parseInt(duration);
            } else {
                console.warn('Unexpected duration format:', duration);
                return '--';
            }
            
            if (isNaN(minutes) || minutes < 0) {
                return '--';
            }
            
            const days = Math.floor(minutes / (24 * 60));
            const hours = Math.floor((minutes % (24 * 60)) / 60);
            const mins = minutes % 60;

            let result = '';
            if (days > 0) result += `${days}d `;
            if (hours > 0) result += `${hours}h `;
            if (mins > 0) result += `${mins}m`;

            return result.trim() || '0m';
        } catch (error) {
            console.error('Error formatting duration:', duration, error);
            return '--';
        }
    }

    // UI helper methods

    showError(message) {
        const container = document.getElementById('tripCardsContainer');
        if (container) {
            container.innerHTML = `
                <div class="error-message">
                    <i class="fas fa-exclamation-triangle"></i>
                    ${message}
                </div>
            `;
        }
        
        const resultsSection = document.getElementById('searchResults');
        if (resultsSection) {
            resultsSection.style.display = 'block';
        }
    }

    showNoResults() {
        const container = document.getElementById('tripCardsContainer');
        if (container) {
            container.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-search"></i>
                    <p>Không tìm thấy chuyến tàu nào phù hợp với tiêu chí tìm kiếm.</p>
                    <p>Vui lòng thử thay đổi ngày hoặc tuyến đường khác.</p>
                </div>
            `;
        }
        
        const resultsSection = document.getElementById('searchResults');
        if (resultsSection) {
            resultsSection.style.display = 'block';
        }
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    new TripInfoManager();
});