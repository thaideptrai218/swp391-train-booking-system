<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c" uri="jakarta.tags.core" %> <%@
taglib prefix="fn" uri="jakarta.tags.functions" %> <%@ page
import="java.util.List, vn.vnrailway.model.PassengerType" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Thông tin chuyến tàu - Vetaure</title>
        <meta
            name="description"
            content="Tìm kiếm và xem thông tin chi tiết chuyến tàu"
        />
        <meta name="keywords" content="thông tin tàu hỏa, lịch trình, ga tàu" />
        <meta name="author" content="Vetaure" />
        <link
            rel="icon"
            href="${pageContext.request.contextPath}/assets/images/favicon.ico"
            type="image/x-icon"
        />
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
        <link
            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Sora:wght@400;600&display=swap"
            rel="stylesheet"
        />

        <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/common.css"
        />
        <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/css/trip-info.css"
        />
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
    <body data-context-path="${pageContext.request.contextPath}">
        <%@include file="../../common/header.jsp" %>

        <main class="trip-info-main">
            <div class="container">
                <h1 class="page-title">
                    <i class="fas fa-info-circle"></i>
                    Thông tin chuyến tàu
                </h1>

                <!-- Trip Search Form -->
                <section class="trip-search-section">
                    <div class="search-form-container">
                        <form id="tripSearchForm" class="trip-search-form">
                            <div class="search-inputs">
                                <div class="input-group">
                                    <label for="origin-station">
                                        <i class="fas fa-train"></i>
                                        Ga đi
                                    </label>
                                    <input
                                        type="text"
                                        id="origin-station"
                                        name="origin-station-name"
                                        placeholder="Chọn ga đi"
                                        autocomplete="off"
                                        required
                                    />
                                    <input
                                        type="hidden"
                                        id="origin-station-id"
                                        name="originStationId"
                                    />
                                    <div
                                        id="origin-station-suggestions"
                                        class="autocomplete-suggestions"
                                    ></div>
                                </div>

                                <div class="input-group">
                                    <label for="destination-station">
                                        <i class="fas fa-map-marker-alt"></i>
                                        Ga đến
                                    </label>
                                    <input
                                        type="text"
                                        id="destination-station"
                                        name="destination-station-name"
                                        placeholder="Chọn ga đến"
                                        autocomplete="off"
                                        required
                                    />
                                    <input
                                        type="hidden"
                                        id="destination-station-id"
                                        name="destinationStationId"
                                    />
                                    <div
                                        id="destination-station-suggestions"
                                        class="autocomplete-suggestions"
                                    ></div>
                                </div>

                                <div class="input-group">
                                    <label for="departure-date">
                                        <i class="fas fa-calendar-alt"></i>
                                        Ngày đi
                                    </label>
                                    <input
                                        type="date"
                                        id="departure-date"
                                        name="departure-date"
                                        required
                                    />
                                </div>

                                <button type="submit" class="search-btn">
                                    <i class="fas fa-search"></i>
                                    Tìm chuyến
                                </button>
                            </div>
                        </form>
                    </div>
                </section>

                <!-- Search Results -->
                <section
                    class="search-results-section"
                    id="searchResults"
                    style="display: none"
                >
                    <h2 class="results-title">Kết quả tìm kiếm</h2>
                    <div class="trip-cards-container" id="tripCardsContainer">
                        <!-- Trip cards will be populated here -->
                    </div>
                </section>

                <!-- Trip Details -->
                <section
                    class="trip-details-section"
                    id="tripDetailsSection"
                    style="display: none"
                >
                    <!-- Trip info container will be populated here -->
                    <div class="ticket-container" id="ticketContainer">
                        <!-- Trip details content -->
                    </div>

                    <!-- Stations table will be populated here -->
                    <div
                        class="stations-table-container"
                        id="stationsTableContainer"
                    >
                        <!-- Stations table content -->
                    </div>
                </section>
            </div>
        </main>

        <%@include file="../../common/footer.jsp" %>

        <script src="${pageContext.request.contextPath}/js/trip/trip-info.js"></script>
    </body>
</html>
