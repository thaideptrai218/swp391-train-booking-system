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
