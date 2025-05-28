<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Tìm kiếm vé tàu</title>
        <link
            rel="stylesheet"
            type="text/css"
            href="${pageContext.request.contextPath}/css/search-trip.css"
        />
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
    <body data-context-path="${pageContext.request.contextPath}">
        <section class="search-trip-wrapper">
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
                                        name="original-station-name" 
                                        autocomplete="off"
                                        onfocus="this.classList.add('has-value')"
                                        onblur="if(!this.value) this.classList.remove('has-value')"
                                        required
                                    />
                                    <label for="original-station">Ga đi</label>
                                    <input type="hidden" id="original-station-id" name="originalStationId" />
                                    <div id="original-station-suggestions" class="autocomplete-suggestions"></div>
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
                                        name="destination-station-name"
                                        autocomplete="off"
                                        onfocus="this.classList.add('has-value')"
                                        onblur="if(!this.value) this.classList.remove('has-value')"
                                        required
                                    />
                                    <label for="destination-station"
                                        >Ga đến</label
                                    >
                                    <input type="hidden" id="destination-station-id" name="destinationStationId" />
                                    <div id="destination-station-suggestions" class="autocomplete-suggestions"></div>
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
                                <span
                                    class="input-icon return-date-icon-element"
                                    style="display: none"
                                    ><i class="fa-solid fa-calendar-plus"></i
                                ></span>
                                <div
                                    class="form-group return-date-form-group-element"
                                    style="display: none"
                                >
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

                    
                    <div class="passengers-bar">
                        <div class="passenger-selector">
                            <div class="passenger-summary" tabindex="0">
                                <div class="passenger-count-display">
                                    <span class="passenger-icon"><i class="fa-solid fa-user-group"></i></span>
                                    <span class="passenger-total-text">1 Hành khách</span>
                                </div>
                                <div class="passenger-container">
                                    <div class="sub-container" data-type="adult" data-discount-info="-0%">
                                        <span class="sub-icon"><i class="fa-solid fa-user"></i></span>
                                        <span class="sub-quantity">1</span>
                                        <span class="sub-label">Người lớn</span>
                                    </div>
                                    <div class="sub-container" data-type="child" data-discount-info="-25%">
                                        <span class="sub-icon"><i class="fa-solid fa-child"></i></span>
                                        <span class="sub-quantity">0</span>
                                        <span class="sub-label">Trẻ em</span>
                                    </div>
                                    <div class="sub-container" data-type="student_hs" data-discount-info="-10%">
                                        <span class="sub-icon"><i class="fa-solid fa-user-graduate"></i></span>
                                        <span class="sub-quantity">0</span>
                                        <span class="sub-label">Học sinh</span>
                                    </div>
                                    <div class="sub-container" data-type="student_uni" data-discount-info="-10%">
                                        <span class="sub-icon"><i class="fa-solid fa-graduation-cap"></i></span>
                                        <span class="sub-quantity">0</span>
                                        <span class="sub-label">Sinh viên</span>
                                    </div>
                                    <div class="sub-container" data-type="elderly" data-discount-info="-15%">
                                        <span class="sub-icon"><i class="fa-solid fa-person-cane"></i></span>
                                        <span class="sub-quantity">0</span>
                                        <span class="sub-label">Người cao tuổi</span>
                                    </div>
                                    <div class="sub-container" data-type="vip" data-discount-info="-20%">
                                        <span class="sub-icon"><i class="fa-solid fa-star"></i></span>
                                        <span class="sub-quantity">0</span>
                                        <span class="sub-label">VIP</span>
                                    </div>
                                    <div class="passenger-details">
                                <div class="passenger-group" data-type="adult" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Người lớn <span class="discount-detail-text">(GIẢM 0%)</span></p>
                                        <p class="desc">Hành khách từ 10 tuổi trở lên.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng người lớn"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="1" readonly aria-live="polite">
                                        <input type="hidden" name="adult_quantity" value="1">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng người lớn"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                                <div class="passenger-group" data-type="child" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Trẻ em <span class="discount-detail-text">(GIẢM 25%)</span></p>
                                        <p class="desc">Từ 6 đến dưới 10 tuổi.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng trẻ em"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="0" readonly aria-live="polite">
                                        <input type="hidden" name="child_quantity" value="0">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng trẻ em"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                                 <div class="passenger-group" data-type="student_hs" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Học sinh <span class="discount-detail-text">(GIẢM 10%)</span></p>
                                        <p class="desc">Học sinh có thẻ.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng học sinh"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="0" readonly aria-live="polite">
                                        <input type="hidden" name="student_hs_quantity" value="0">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng học sinh"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                                <div class="passenger-group" data-type="student_uni" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Sinh viên <span class="discount-detail-text">(GIẢM 10%)</span></p>
                                        <p class="desc">Sinh viên có thẻ.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng sinh viên"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="0" readonly aria-live="polite">
                                        <input type="hidden" name="student_uni_quantity" value="0">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng sinh viên"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                                <div class="passenger-group" data-type="elderly" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Người cao tuổi <span class="discount-detail-text">(GIẢM 15%)</span></p>
                                        <p class="desc">Công dân Việt Nam từ 60 tuổi trở lên.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng người cao tuổi"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="0" readonly aria-live="polite">
                                        <input type="hidden" name="elderly_quantity" value="0">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng người cao tuổi"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                                <div class="passenger-group" data-type="vip" data-min="0" data-max="10">
                                    <div class="passenger-info">
                                        <p class="title">Khách VIP <span class="discount-detail-text">(GIẢM 20%)</span></p>
                                        <p class="desc">Hành khách có thẻ VIP.</p>
                                    </div>
                                    <div class="input-block">
                                        <button type="button" class="decrease-btn" aria-label="Giảm số lượng khách VIP"><i class="fa-solid fa-minus"></i></button>
                                        <input type="text" class="quantity-display" value="0" readonly aria-live="polite">
                                        <input type="hidden" name="vip_quantity" value="0">
                                        <button type="button" class="increase-btn" aria-label="Tăng số lượng khách VIP"><i class="fa-solid fa-plus"></i></button>
                                    </div>
                                </div>
                            </div>
                                </div>
                            </div>
                            
                        </div>
                    </div>
                </form>
            </div>
        </section>

        <script src="${pageContext.request.contextPath}/js/search-trip.js"></script>
        <script src="${pageContext.request.contextPath}/js/passenger-selector.js"></script>
        <script src="${pageContext.request.contextPath}/js/station-autocomplete.js"></script>
    </body>
</html>
