document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('stationSearchInput');
    const locationListContainer = document.querySelector('.location-list'); 
    
    if (!searchInput) {
        console.warn('Search input with ID "stationSearchInput" not found.');
        return;
    }
    if (!locationListContainer) {
        console.warn('Container with class "location-list" not found.');
        return;
    }

    const locationItems = locationListContainer.getElementsByClassName('location-item');

    // Helper function to remove accents and convert to lowercase
    function normalizeString(str) {
        if (typeof str !== 'string') return '';
        return str.normalize('NFD')
                  .replace(/[\u0300-\u036f]/g, "") // Remove combining diacritical marks
                  .replace(/Đ/g, 'D') // Replace capital Vietnamese D with D
                  .replace(/đ/g, 'd') // Replace small Vietnamese d with d
                  .toLowerCase()
                  .trim();
    }

    searchInput.addEventListener('input', function () {
        const searchTermNormalized = normalizeString(searchInput.value);

        Array.from(locationItems).forEach(function (item) {
            const stationNameElement = item.querySelector('.location-info h4');
            if (stationNameElement) {
                const stationNameOriginal = stationNameElement.textContent;
                const stationNameNormalized = normalizeString(stationNameOriginal);
                
                if (stationNameNormalized.includes(searchTermNormalized)) {
                    item.style.display = ''; 
                } else {
                    item.style.display = 'none';
                }
            }
        });
    });
});

// Hot Locations Carousel Functionality
document.addEventListener('DOMContentLoaded', function () {
    const hotLocationsCarousel = document.getElementById('hotLocationsCarousel');
    const prevLocationButton = document.querySelector('.prev-location');
    const nextLocationButton = document.querySelector('.next-location');

    if (hotLocationsCarousel) {
        // Button navigation
        if (prevLocationButton && nextLocationButton) {
            const cardWidth = () => {
                const firstCard = hotLocationsCarousel.querySelector('.location-card');
                if (firstCard) {
                    return firstCard.offsetWidth + 20; // card width + gap (20px from CSS)
                }
                return 340; // Default fallback
            };

            prevLocationButton.addEventListener('click', () => {
                hotLocationsCarousel.scrollBy({ left: -cardWidth(), behavior: 'smooth' });
            });

            nextLocationButton.addEventListener('click', () => {
                hotLocationsCarousel.scrollBy({ left: cardWidth(), behavior: 'smooth' });
            });
        } else {
            if (!prevLocationButton) console.warn('Previous location button with class "prev-location" not found.');
            if (!nextLocationButton) console.warn('Next location button with class "next-location" not found.');
        }

        // Drag-to-scroll functionality
        let isDown = false;
        let startX;
        let scrollLeft;

        hotLocationsCarousel.addEventListener('mousedown', (e) => {
            isDown = true;
            hotLocationsCarousel.classList.add('grabbing');
            startX = e.pageX - hotLocationsCarousel.offsetLeft;
            scrollLeft = hotLocationsCarousel.scrollLeft;
            // Prevent default drag behavior (e.g., image dragging)
            e.preventDefault(); 
        });

        hotLocationsCarousel.addEventListener('mouseleave', () => {
            if (!isDown) return;
            isDown = false;
            hotLocationsCarousel.classList.remove('grabbing');
        });

        hotLocationsCarousel.addEventListener('mouseup', () => {
            if (!isDown) return;
            isDown = false;
            hotLocationsCarousel.classList.remove('grabbing');
        });

        hotLocationsCarousel.addEventListener('mousemove', (e) => {
            if (!isDown) return;
            e.preventDefault();
            const x = e.pageX - hotLocationsCarousel.offsetLeft;
            const walk = (x - startX) * 2; // Multiplier for faster scrolling
            hotLocationsCarousel.scrollLeft = scrollLeft - walk;
        });

    } else {
        console.warn('Hot locations carousel with ID "hotLocationsCarousel" not found.');
    }
});

// Station Info Popup Functions
function showStationPopup(name, address, phone, stationCode) { // Changed stationID to stationCode
    const contextPath = document.body.dataset.contextPath || ''; // Get context path from body data attribute

    // Populate new modal structure
    document.getElementById('modalHeaderStationName').textContent = name; // For the header
    document.getElementById('modalBodyStationName').textContent = name; // For the right panel in the body
    document.getElementById('modalStationAddress').textContent = address;
    document.getElementById('modalStationPhone').textContent = phone;
    // If you add a hotline field in JSP:
    // document.getElementById('modalStationHotline').textContent = hotline; // Assuming you pass hotline
    
    const modalImage = document.getElementById('modalStationImage');
    if (stationCode) { // Changed stationID to stationCode
        modalImage.src = `${contextPath}/assets/images/landing/stations/${stationCode}.jpg`; // Changed station${stationID} to ${stationCode}
        modalImage.onerror = function() { 
            this.onerror=null; this.src='https://via.placeholder.com/300x200?text=Image+Not+Found'; 
            console.warn('Modal image not found for stationCode:', stationCode, 'at path:', this.src); // Changed stationID to stationCode
        };
        modalImage.style.display = 'block'; // Show image element
    } else {
        modalImage.style.display = 'none'; // Hide if no stationCode
        modalImage.src = ''; // Clear src
    }
    
    document.getElementById('stationModal').style.display = 'block';
}

function closeStationPopup() {
    document.getElementById('stationModal').style.display = 'none';
}

// Close modal if user clicks outside of the modal content
window.onclick = function(event) {
    const modal = document.getElementById('stationModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
}
