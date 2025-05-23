<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Tìm kiếm vé tàu</title>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/search-trip.css" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
        />
        <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/flatpickr/dist/themes/material_blue.css"
        />
        <script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
    </head>
    <body>
        <div class="search-trip-wrapper">
            <div class="search-panel">
                <form
                    action="<c:url value='/searchTrip'/>"
                    method="POST"
                    id="searchTripForm"
                    class="search-form-flex"
                >
                    <div class="search-bar">
                        <div class="input-section">
                            <div class="input-icon-group">
                                <span class="input-icon"
                                    ><i class="fa-solid fa-train-subway"></i
                                ></span>
                                <div class="form-group">
                                    <input
                                        type="text"
                                        id="original-station"
                                        name="original-station"
                                        autocomplete="off"
                                        onfocus="this.classList.add('has-value')"
                                        onblur="if(!this.value) this.classList.remove('has-value')"
                                        required
                                    />
                                    <label for="original-station">Ga đi</label>
                                </div>
                            </div>
                            <div class="input-icon-group">
                                <span class="input-icon"
                                    ><i class="fa-solid fa-location-dot"></i
                                ></span>
                                <div class="form-group">
                                    <input
                                        type="text"
                                        id="destination-station"
                                        name="destination-station"
                                        autocomplete="off"
                                        onfocus="this.classList.add('has-value')"
                                        onblur="if(!this.value) this.classList.remove('has-value')"
                                        required
                                    />
                                    <label for="destination-station"
                                        >Ga đến</label
                                    >
                                </div>
                            </div>
                            <div class="input-icon-group date-group">
                                <span class="input-icon"
                                    ><i class="fa-solid fa-calendar-days"></i
                                ></span>
                                <div class="form-group">
                                    <input
                                        type="date"
                                        id="departure-date"
                                        name="departure-date"
                                        autocomplete="off"
                                        required
                                    />
                                    <label for="departure-date">Ngày đi</label>
                                </div>
                            </div>
                            <div
                                class="input-icon-group date-group"
                                id="return-date-group"
                            >
                                <div class="add-return-date-prompt">
                                    <i class="fa-solid fa-plus"></i> Thêm ngày
                                    về
                                </div>
                                <span class="input-icon return-date-icon-element" style="display: none;"
                                    ><i class="fa-solid fa-calendar-plus"></i
                                ></span>
                                <div class="form-group return-date-form-group-element" style="display: none;">
                                    <input
                                        type="date"
                                        id="return-date"
                                        name="return-date"
                                        autocomplete="off"
                                    />
                                    <label for="return-date">Ngày về</label>
                                    <button
                                        type="button"
                                        class="clear-return-date"
                                        aria-label="Clear return date"
                                    >
                                        <i class="fa-solid fa-times"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                        <button class="submit-button" type="submit">
                            Tìm kiếm
                        </button>
                    </div>
                    <div class="passengers-bar"></div>
                </form>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/js/search-trip.js"></script>
    </body>
</html>
