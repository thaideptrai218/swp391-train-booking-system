<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ page
import="vn.vnrailway.utils.FormatUtils" %> <%@ taglib prefix="c"
uri="jakarta.tags.core" %> <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
  <head>
    <title>Kết quả tìm kiếm chuyến tàu</title>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/reset.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/trip-result.css"
    />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"
    />
  </head>
  <body>
    <div class="page-wrapper container">
      <aside class="sidebar">
        <div class="shopping-cart-placeholder">
          <h3><i class="fas fa-shopping-cart"></i> Giỏ hàng</h3>
          <p>(Chưa có vé nào)</p>
        </div>
        <div class="mini-search-placeholder">
          <h3><i class="fas fa-search"></i> Tìm kiếm lại</h3>
          <p>(Form tìm kiếm thu gọn)</p>
          <%-- Placeholder for a mini search form --%>
        </div>
      </aside>

      <main
        class="main-results-content"
        data-is-round-trip="${not empty returnDateDisplay}"
      >
        <%-- Outbound Trips Section --%>
        <section class="trip-section outbound-section">
          <div class="trip-header">
            <h2 class="title">
              Chiều đi: <c:out value="${departureDateDisplay}" /> từ
              <c:out value="${originStationDisplay}" /> đến
              <c:out value="${destinationStationDisplay}" />
            </h2>

            <button
              class="change-station-btn"
              type="button"
              onclick="window.location.href='${pageContext.request.contextPath}/searchTrip'"
            >
              <i class="fas fa-exchange-alt"></i> Thay đổi ga
            </button>
          </div>
          <c:choose>
            <c:when test="${not empty outboundTrips}">
              <div class="train-list">
                <c:forEach var="trip" items="${outboundTrips}">
                  <div
                    class="train-item"
                    data-trip-id="${trip.tripId}"
                    data-leg-origin-station-id="${trip.originStationId}"
                    data-leg-dest-station-id="${trip.destinationStationId}"
                    tabindex="0"
                  >
                    <div class="train-item-collapsed-summary">
                      <span class="train-name">${trip.trainName}</span>
                      <span class="departure-info">
                        <span class="trip-time"
                          ><fmt:formatDate
                            value="${trip.scheduledDepartureAsDate}"
                            pattern="dd/MM HH:mm"
                        /></span>
                        <span class="trip-station"
                          >${trip.originStationName}</span
                        >
                      </span>
                      <span class="duration-info">
                        <img
                          src="${pageContext.request.contextPath}/assets/icons/train-icon.png"
                          alt="Train Icon"
                          class="train-duration-icon"
                        />
                        <span class="duration-text"
                          >${FormatUtils.formatDuration(trip.durationMinutes)}</span
                        >
                      </span>
                      <span class="arrival-info">
                        <span class="trip-time"
                          ><fmt:formatDate
                            value="${trip.scheduledArrivalAsDate}"
                            pattern="dd/MM HH:mm"
                        /></span>
                        <span class="trip-station"
                          >${trip.destinationStationName}</span
                        >
                      </span>
                      <span class="seats-info">
                        Còn trống: ${trip.availableSeat} <br /> Đã đặt:
                        ${trip.occupiedSeat}
                      </span>
                    </div>
                    <div class="expanded-details" style="display: none">
                      <div
                        class="train-composition-display"
                        id="trainComposition-outbound-${trip.tripId}"
                      >
                        <c:if test="${not empty trip.coaches}">
                          <c:forEach var="coachInfo" items="${trip.coaches}">
                            <div
                              class="carriage-item"
                              data-coach-id="${coachInfo.coachId}"
                              data-coach-typename="${coachInfo.coachTypeName}"
                              data-coach-position="${coachInfo.positionInTrain}"
                              data-coach-description="${coachInfo.coachTypeDescription}"
                              data-coach-capacity="${coachInfo.capacity}"
                              data-coach-is-compartmented="${coachInfo.isCompartmented()}"
                              data-coach-default-compartment-capacity="${not empty coachInfo.defaultCompartmentCapacity ? coachInfo.defaultCompartmentCapacity : ''}"
                              data-trip-id="${trip.tripId}"
                              data-trip-leg="outbound"
                              tabindex="0"
                            >
                              <img
                                src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg"
                                alt="Toa ${coachInfo.positionInTrain}"
                                class="carriage-svg-icon"
                              />
                              <span class="carriage-number-label"
                                >Toa ${coachInfo.positionInTrain}</span
                              >
                            </div>
                          </c:forEach>
                        </c:if>
                        <div class="train-head-item">
                          <img
                            src="${pageContext.request.contextPath}/assets/icons/trip/train-head.svg"
                            alt="Đầu tàu ${trip.trainName}"
                            class="train-head-svg-icon"
                          />
                          <span class="train-name-label"
                            >${trip.trainName}</span
                          >
                        </div>
                      </div>
                      <div
                        class="carriage-details-description"
                        id="carriageDescription-outbound-${trip.tripId}"
                      >
                        <p>Chọn một toa để xem chi tiết.</p>
                      </div>
                      <div
                        class="seat-details-block"
                        id="seatDetailsBlock-outbound-${trip.tripId}"
                      >
                        <p>Sơ đồ ghế sẽ được hiển thị ở đây.</p>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <p class="no-results">Không tìm thấy chuyến đi nào phù hợp.</p>
            </c:otherwise>
          </c:choose>
        </section>

        <%-- Return Trips Section (Optional) --%>
        <c:if test="${not empty returnDateDisplay}">
          <section class="trip-section return-section">
            <h2>
              Chiều về: <c:out value="${returnDateDisplay}" /> từ
              <c:out value="${destinationStationDisplay}" /> đến
              <c:out value="${originStationDisplay}" />
            </h2>
            <c:choose>
              <c:when test="${not empty returnTrips}">
                <div class="train-list">
                  <c:forEach var="trip" items="${returnTrips}">
                    <div
                      class="train-item"
                      data-trip-id="${trip.tripId}"
                      data-leg-origin-station-id="${trip.originStationId}"
                      data-leg-dest-station-id="${trip.destinationStationId}"
                      tabindex="0"
                    >
                      <div class="train-item-collapsed-summary">
                        <span class="train-name">${trip.trainName}</span>
                        <span class="departure-info">
                          <span class="trip-time"
                            ><fmt:formatDate
                              value="${trip.scheduledDepartureAsDate}"
                              pattern="dd/MM HH:mm"
                          /></span>
                          <span class="trip-station"
                            >${trip.originStationName}</span
                          >
                        </span>
                        <span class="duration-info">
                          <img
                            src="${pageContext.request.contextPath}/assets/icons/train-icon.png"
                            alt="Train Icon"
                            class="train-duration-icon"
                          />
                          <span class="duration-text"
                            >${FormatUtils.formatDuration(trip.durationMinutes)}</span
                          >
                        </span>
                        <span class="arrival-info">
                          <span class="trip-time"
                            ><fmt:formatDate
                              value="${trip.scheduledArrivalAsDate}"
                              pattern="dd/MM HH:mm"
                          /></span>
                          <span class="trip-station"
                            >${trip.destinationStationName}</span
                          >
                        </span>
                        <span class="seats-info">
                          Còn trống: -- <br /> Đã đặt: --
                        </span>
                      </div>
                      <div class="expanded-details" style="display: none">
                        <div
                          class="train-composition-display"
                          id="trainComposition-return-${trip.tripId}"
                        >
                          <c:if test="${not empty trip.coaches}">
                            <c:forEach var="coachInfo" items="${trip.coaches}">
                              <div
                                class="carriage-item"
                                data-coach-id="${coachInfo.coachId}"
                                data-coach-typename="${coachInfo.coachTypeName}"
                                data-coach-position="${coachInfo.positionInTrain}"
                                data-coach-description="${coachInfo.coachTypeDescription}"
                                data-coach-capacity="${coachInfo.capacity}"
                                data-coach-is-compartmented="${coachInfo.isCompartmented()}"
                                data-coach-default-compartment-capacity="${not empty coachInfo.defaultCompartmentCapacity ? coachInfo.defaultCompartmentCapacity : ''}"
                                data-trip-id="${trip.tripId}"
                                data-trip-leg="return"
                                tabindex="0"
                              >
                                <img
                                  src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg"
                                  alt="Toa ${coachInfo.positionInTrain}"
                                  class="carriage-svg-icon"
                                />
                                <span class="carriage-number-label"
                                  >Toa ${coachInfo.positionInTrain}</span
                                >
                              </div>
                            </c:forEach>
                          </c:if>
                          <div class="train-head-item">
                            <img
                              src="${pageContext.request.contextPath}/assets/icons/trip/train-head.svg"
                              alt="Đầu tàu ${trip.trainName}"
                              class="train-head-svg-icon"
                            />
                            <span class="train-name-label"
                              >${trip.trainName}</span
                            >
                          </div>
                        </div>
                        <div
                          class="carriage-details-description"
                          id="carriageDescription-return-${trip.tripId}"
                        >
                          <p>Chọn một toa để xem chi tiết.</p>
                        </div>
                        <div
                          class="seat-details-block"
                          id="seatDetailsBlock-return-${trip.tripId}"
                        >
                          <p>Sơ đồ ghế sẽ được hiển thị ở đây.</p>
                        </div>
                      </div>
                    </div>
                  </c:forEach>
                </div>
              </c:when>
              <c:otherwise>
                <p class="no-results">Không tìm thấy chuyến về nào phù hợp.</p>
              </c:otherwise>
            </c:choose>
          </section>
        </c:if>
      </main>
    </div>

    <script src="${pageContext.request.contextPath}/js/trip/trip-result.js"></script>
  </body>
</html>
