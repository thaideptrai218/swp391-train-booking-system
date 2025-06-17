<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tất Cả Địa Điểm Nổi Bật</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing-page.css">
    <style>
        /* Inherit styles from landing-page.css where possible */
        body {
            font-family: var(--font-primary, Arial, sans-serif);
            background-color: var(--color-bg-body, #f4f4f4);
            color: var(--color-text-default, #333);
            margin: 0;
            padding: 20px 0; /* Add some padding to top/bottom of body */
        }
        .container {
            max-width: 1200px; /* Consistent with landing page */
            width: 90%;
            margin: 0 auto;
            padding: 20px;
        }
        .all-locations-header {
            font-family: var(--font-secondary, Arial, sans-serif);
            font-size: 32px; /* Consistent with .section-main-title */
            color: var(--color-secondary-blue, #10375c);
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
        }

        /* Filters and Sorting Styling */
        .filters-and-sorting {
            background-color: var(--color-bg-card, #fff);
            padding: 20px;
            margin-bottom: 30px;
            border-radius: var(--border-radius-card, 12px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
            display: flex;
            flex-wrap: wrap; /* Allow items to wrap on smaller screens */
            gap: 20px; /* Space between form groups */
            align-items: flex-end; /* Align items to the bottom */
        }
        .filters-and-sorting form {
            display: contents; /* Allows form to act as a direct child for flex layout */
        }
        .filters-and-sorting .form-group {
            display: flex;
            flex-direction: column;
            flex-grow: 1; /* Allow form groups to grow */
            min-width: 180px; /* Minimum width for form groups */
        }
        .filters-and-sorting label {
            margin-bottom: 8px;
            font-weight: 600; /* Bolder labels */
            font-size: 1em; /* Slightly larger label */
            color: var(--color-text-light, #555);
        }
        .filters-and-sorting select,
        .filters-and-sorting input[type="text"] {
            padding: 10px 12px; /* More padding */
            border: 1px solid var(--color-border-medium, #ccc);
            border-radius: var(--border-radius-input, 4px);
            font-size: 1em;
            font-family: var(--font-primary, Arial, sans-serif);
            transition: border-color 0.3s ease;
        }
        .filters-and-sorting select:focus,
        .filters-and-sorting input[type="text"]:focus {
            border-color: var(--color-primary, #007bff);
            outline: none;
            box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
        }
        .filters-and-sorting button {
            padding: 12px 20px; /* Larger button */
            background-color: var(--color-primary, #007bff);
            color: var(--color-white, #fff);
            border: none;
            border-radius: var(--border-radius-input, 4px);
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s ease;
            align-self: flex-end;
            min-width: 100px; /* Ensure button has a decent width */
        }
        .filters-and-sorting button:hover {
            background-color: var(--color-primary-dark, #0056b3);
        }

        /* Location Grid and Cards Styling */
        .location-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); /* Responsive columns */
            gap: 25px; /* Increased gap */
        }
        /* Using existing .location-card styles from landing-page.css if they are suitable */
        /* If specific overrides are needed for this page, they can be added here */
        .location-card { /* Styles from landing-page.css are good, ensure they are applied */
            background-color: var(--color-bg-card, #fff);
            border-radius: var(--border-radius-card, 12px);
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            overflow: hidden;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            display: flex;
            flex-direction: column;
        }
        .location-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 16px rgba(0,0,0,0.12);
        }
        .location-card img { /* card-image from landing-page.css */
            width: 100%;
            height: 200px;
            object-fit: cover;
            transition: transform 0.3s ease;
        }
        .location-card:hover img {
            transform: scale(1.05);
        }
        .location-card-content { /* card-content from landing-page.css */
            padding: 18px;
            flex-grow: 1;
            display: flex;
            flex-direction: column;
        }
        .location-card-content h3 { /* card-title from landing-page.css */
            font-family: var(--font-secondary, Arial, sans-serif);
            font-size: 20px;
            font-weight: 700;
            color: var(--color-secondary-dark-blue, #0d2b4c);
            margin-top: 0;
            margin-bottom: 10px; /* Increased margin */
            line-height: 1.3;
        }
        .location-card-content p {
            font-family: var(--font-primary, Arial, sans-serif);
            font-size: 15px; /* Slightly larger paragraph text */
            color: var(--color-text-light, #555);
            line-height: 1.6;
            margin-bottom: 8px;
        }
        .location-card-content p a {
            color: var(--color-primary, #007bff);
            text-decoration: none;
            font-weight: 500;
        }
        .location-card-content p a:hover {
            text-decoration: underline;
            color: var(--color-primary-dark, #0056b3);
        }


        /* Pagination Styling */
        .pagination {
            text-align: center;
            margin: 40px 0 20px; /* More margin top */
        }
        .pagination a, .pagination span {
            display: inline-block;
            padding: 10px 15px; /* Larger padding */
            margin: 0 5px; /* Slightly more margin */
            border: 1px solid var(--color-border-light, #ddd);
            color: var(--color-primary, #007bff);
            text-decoration: none;
            border-radius: var(--border-radius-input, 4px);
            transition: background-color 0.3s, color 0.3s, border-color 0.3s;
            font-weight: 500;
        }
        .pagination a:hover {
            background-color: var(--color-primary, #007bff);
            color: var(--color-white, #fff);
            border-color: var(--color-primary, #007bff);
        }
        .pagination .current-page {
            background-color: var(--color-primary, #007bff);
            color: var(--color-white, #fff);
            border-color: var(--color-primary, #007bff);
            cursor: default;
        }
        .pagination .disabled {
            color: var(--color-text-light, #aaa);
            pointer-events: none;
            border-color: var(--color-border-light, #ddd);
            background-color: var(--color-bg-body, #f4f4f4); /* Lighter background for disabled */
        }

        /* Back Link Styling */
        .back-link {
            display: inline-block; /* Changed to inline-block for better centering if needed */
            text-align: center;
            margin-top: 30px;
            padding: 12px 25px; /* Consistent with .btn */
            background-color: var(--color-accent-cyan, #4daac8);
            color: var(--color-white, #fff);
            text-decoration: none;
            border-radius: var(--border-radius-main, 22px); /* Consistent with .btn */
            font-weight: 600;
            transition: background-color 0.3s ease;
        }
        .back-link:hover {
            background-color: var(--color-accent-cyan-dark, #357a94);
        }
        .back-link-container { /* Optional: for centering the back link */
            text-align: center;
            margin-top: 20px;
        }

    </style>
</head>
<body>
    <div class="container">
        <h1 class="all-locations-header">Tất Cả Địa Điểm Nổi Bật</h1>

        <!-- Filters and Sorting Form -->
        <div class="filters-and-sorting">
            <form action="${contextPath}/all-locations" method="GET" id="filterSortForm">
                <input type="hidden" name="page" value="1"> <%-- Reset to page 1 on new filter/sort --%>
                <div class="form-group">
                    <label for="filterRegion">Lọc theo Vùng:</label>
                    <input type="text" id="filterRegion" name="filterRegion" value="${param.filterRegion}" placeholder="Nhập vùng">
                </div>
                <div class="form-group">
                    <label for="filterCity">Lọc theo Thành phố:</label>
                    <input type="text" id="filterCity" name="filterCity" value="${param.filterCity}" placeholder="Nhập thành phố">
                </div>
                <div class="form-group">
                    <label for="sortField">Sắp xếp theo:</label>
                    <select id="sortField" name="sortField">
                        <option value="locationName" ${param.sortField == 'locationName' ? 'selected' : ''}>Tên Địa Điểm</option>
                        <option value="city" ${param.sortField == 'city' ? 'selected' : ''}>Thành Phố</option>
                        <option value="region" ${param.sortField == 'region' ? 'selected' : ''}>Vùng</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="sortOrder">Thứ tự:</label>
                    <select id="sortOrder" name="sortOrder">
                        <option value="ASC" ${param.sortOrder == 'ASC' ? 'selected' : ''}>Tăng dần (A-Z)</option>
                        <option value="DESC" ${param.sortOrder == 'DESC' ? 'selected' : ''}>Giảm dần (Z-A)</option>
                    </select>
                </div>
                <button type="submit">Áp dụng</button>
            </form>
        </div>


        <div class="location-grid">
            <c:if test="${empty allLocations}">
                <p>Không có địa điểm nào để hiển thị với lựa chọn hiện tại.</p>
            </c:if>
            <c:forEach var="location" items="${allLocations}">
                <div class="location-card">
                    <img src="${contextPath}/assets/images/landing/locations/${location.locationCode}.jpg" alt="${location.locationName}" onerror="this.onerror=null; this.src='https://via.placeholder.com/300x200?text=Image+Not+Found';">
                    <div class="location-card-content">
                        <h3>${location.locationName}</h3>
                        <p>Thành phố: ${location.city}</p>
                        <p>Vùng: ${location.region}</p>
                        <c:if test="${not empty location.link}">
                            <p><a href="${location.link}" target="_blank">Tìm hiểu thêm</a></p>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Pagination Controls -->
        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <%-- Previous Page Link --%>
                <c:choose>
                    <c:when test="${currentPage > 1}">
                        <a href="${contextPath}/all-locations?page=${currentPage - 1}">&laquo; Trước</a>
                    </c:when>
                    <c:otherwise>
                        <span class="disabled">&laquo; Trước</span>
                    </c:otherwise>
                </c:choose>

                <%-- Page Number Links --%>
                <c:forEach var="i" begin="1" end="${totalPages}">
                    <c:choose>
                        <c:when test="${i == currentPage}">
                            <span class="current-page">${i}</span>
                        </c:when>
                        <c:otherwise>
                            <a href="${contextPath}/all-locations?page=${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <%-- Next Page Link --%>
                <c:choose>
                    <c:when test="${currentPage < totalPages}">
                        <a href="${contextPath}/all-locations?page=${currentPage + 1}">Sau &raquo;</a>
                    </c:when>
                    <c:otherwise>
                        <span class="disabled">Sau &raquo;</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>

        <a href="${contextPath}/landing" class="back-link">&larr; Quay lại Trang Chủ</a>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const filterForm = document.getElementById('filterSortForm');
            const filterRegionInput = document.getElementById('filterRegion');
            const filterCityInput = document.getElementById('filterCity');
            const locationCards = document.querySelectorAll('.location-card');

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

            function applyFilters() {
                const regionFilterNormalized = normalizeString(filterRegionInput.value);
                const cityFilterNormalized = normalizeString(filterCityInput.value);

                locationCards.forEach(card => {
                    const locationNameElement = card.querySelector('h3');
                    const cityElement = card.querySelector('p:nth-of-type(1)'); // Assuming city is the first <p>
                    const regionElement = card.querySelector('p:nth-of-type(2)'); // Assuming region is the second <p>

                    const locationName = locationNameElement ? normalizeString(locationNameElement.textContent) : '';
                    const city = cityElement ? normalizeString(cityElement.textContent.replace('Thành phố: ', '')) : '';
                    const region = regionElement ? normalizeString(regionElement.textContent.replace('Vùng: ', '')) : '';
                    
                    const matchesRegion = regionFilterNormalized === '' || region.includes(regionFilterNormalized);
                    const matchesCity = cityFilterNormalized === '' || city.includes(cityFilterNormalized);

                    if (matchesRegion && matchesCity) {
                        card.style.display = '';
                    } else {
                        card.style.display = 'none';
                    }
                });
            }

            // Apply filters on input change for instant feedback (client-side filtering)
            // Note: This will filter the currently loaded page. For full dataset filtering, server-side is needed.
            // The form submission will still handle server-side filtering and sorting.
            if (filterRegionInput) {
                filterRegionInput.addEventListener('input', applyFilters);
            }
            if (filterCityInput) {
                filterCityInput.addEventListener('input', applyFilters);
            }

            // If you want the form to submit and re-fetch from server on every input for region/city,
            // you might change the 'input' event listeners to submit the form.
            // However, the current setup allows client-side preview filtering + server-side on "Áp dụng".

            // Preserve filter/sort values in pagination links
            const paginationLinks = document.querySelectorAll('.pagination a');
            paginationLinks.forEach(link => {
                const url = new URL(link.href);
                const params = new URLSearchParams(url.search);

                if (filterRegionInput.value) params.set('filterRegion', filterRegionInput.value);
                if (filterCityInput.value) params.set('filterCity', filterCityInput.value);
                
                const sortField = document.getElementById('sortField').value;
                const sortOrder = document.getElementById('sortOrder').value;
                if (sortField) params.set('sortField', sortField);
                if (sortOrder) params.set('sortOrder', sortOrder);
                
                link.href = `${url.pathname}?${params.toString()}`;
            });
        });
    </script>
</body>
</html>
