<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Trains and Seats</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/trains-seats/manage-trains-seats.css" />
    <script src="${pageContext.request.contextPath}/js/manager/trains-seats/manage-trains-seats.js" defer></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />
</head>
<body>
    <jsp:include page="../sidebar.jsp" />
    <div class="main-content">
        <h1>Manage Trains, Coaches, and Seats</h1>

        <a href="javascript:void(0);" onclick="openModal('add-train-modal')" class="actions btn-primary-add">Add New Train</a>

        <div id="add-train-modal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal('add-train-modal')"></span>
                <h2>Add Train</h2>
                <form id="add-train-form" action="manage-trains-seats" method="post">
                    <input type="hidden" name="action" value="insert_train" />
                    <table>
                        <tr>
                            <th>Train Name:</th>
                            <td><input type="text" name="trainCode" required /></td>
                        </tr>
                        <tr>
                            <th>Train Type:</th>
                            <td>
                                <select name="typeCode">
                                    <c:forEach var="trainType" items="${listTrainType}">
                                        <option value="${trainType.trainTypeID}">${trainType.typeName}</option>
                                    </c:forEach>
                                </select>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2"><input type="submit" value="Save" class="btn-primary-add" /></td>
                        </tr>
                    </table>
                </form>
            </div>
        </div>

        <div class="train-list" id="trainList">
            <c:forEach var="train" items="${listTrain}">
                <div class="train-container" data-train-id="${train.trainID}">
                    <div class="train-header clickable">
                        <span class="train-name">${train.trainName}</span>
                        <div class="actions">
                            <a href="javascript:void(0);" onclick="openModal('edit-train-modal-${train.trainID}')" <c:if test="${train.isLocked}">style="pointer-events:none;opacity:0.5;"</c:if>>Edit</a>
                            <c:choose>
                                <c:when test="${train.isLocked}">
                                    <a href="manage-trains-seats?action=unlock_train&trainId=${train.trainID}" class="lock-btn" title="Mở khóa">
                                        <i class="fas fa-lock-open"></i>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <a href="manage-trains-seats?action=lock_train&trainId=${train.trainID}" class="lock-btn" title="Khóa">
                                        <i class="fas fa-lock"></i>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <div id="edit-train-modal-${train.trainID}" class="modal">
                        <div class="modal-content">
                            <span class="close" onclick="closeModal('edit-train-modal-${train.trainID}')"></span>
                            <h2>Edit Train: ${train.trainName}</h2>
                            <form id="edit-train-form-${train.trainID}" action="manage-trains-seats" method="post">
                                <input type="hidden" name="action" value="update_train" />
                                <input type="hidden" name="trainCode" value="${train.trainName}" />
                                <table>
                                    <tr>
                                        <th>Train Type:</th>
                                        <td>
                                            <select name="typeCode">
                                                <c:forEach var="trainType" items="${listTrainType}">
                                                    <option value="${trainType.trainTypeID}" <c:if test="${train.trainTypeID == trainType.trainTypeID}">selected</c:if>>
                                                        ${trainType.typeName}
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2"><input type="submit" value="Update" /></td>
                                    </tr>
                                </table>
                            </form>
                        </div>
                    </div>

                    <c:choose>
                      <c:when test="${train.isLocked}">
                        <div class="train-composition-display hidden-coach-list" style="pointer-events:none; opacity:0.5;">
                      </c:when>
                      <c:otherwise>
                        <div class="train-composition-display hidden-coach-list">
                      </c:otherwise>
                    </c:choose>
                        <a href="javascript:void(0);" onclick="openModal('add-coach-modal-${train.trainID}')" class="carriage-item" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>
                            <img src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg" class="carriage-svg-icon" style="opacity:0.5;"/>
                            <span class="carriage-number-label">Add Coach</span>
                        </a>
                        <c:forEach var="coach" items="${coachesByTrain[train.trainID]}">
                            <div class="carriage-item" onclick="<c:if test='${!train.isLocked}'>toggleCoachDetails('${train.trainID}', '${coach.coachID}')</c:if>" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>
                                <img src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg" class="carriage-svg-icon"/>
                                <span class="carriage-number-label">Toa ${coach.coachNumber}</span>
                                <a href="manage-trains-seats?action=delete_coach&id=${coach.coachID}" class="delete-btn" onclick="return confirm('Are you sure you want to delete this coach?');" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>
                                    <i class="fas fa-times"></i>
                                </a>
                            </div>
                        </c:forEach>
                        <div class="train-head-item">
                            <img src="${pageContext.request.contextPath}/assets/icons/trip/train-head.svg" alt="Đầu tàu ${train.trainName}" class="train-head-svg-icon" />
                            <span class="train-name-label">${train.trainName}</span>
                            <c:if test="${train.isLocked}"><span style='color:#ff9800;font-weight:bold;'>(Đã khóa)</span></c:if>
                        </div>
                    </div>

                    <div id="add-coach-modal-${train.trainID}" class="modal">
                        <div class="modal-content">
                            <span class="close" onclick="closeModal('add-coach-modal-${train.trainID}')"></span>
                            <h3>Add Coach to ${train.trainName}</h3>
                            <form id="add-coach-form-${train.trainID}" action="manage-trains-seats" method="post">
                                <input type="hidden" name="action" value="insert_coach" />
                                <input type="hidden" name="trainCode" value="${train.trainName}" />
                                <input type="hidden" name="coachNumber" id="nextCoachNumber_${train.trainID}" />
                                <input type="hidden" name="coachName" id="autoCoachName_${train.trainID}" />
                                <table>
                                    <tr>
                                        <th>Coach Type:</th>
                                        <td>
                                            <select name="typeCode">
                                                <c:forEach var="coachType" items="${listCoachType}">
                                                    <option value="${coachType.coachTypeID}">${coachType.typeName}</option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="2"><input type="submit" value="Add Coach" /></td>
                                    </tr>
                                </table>
                            </form>
                        </div>
                    </div>

                    <c:forEach var="coach" items="${coachesByTrain[train.trainID]}">
                        <c:set var="coachDetailsStyleString" value="" />
                        <c:if test="${train.isLocked}">
                          <c:set var="coachDetailsStyleString" value="pointer-events:none; opacity:0.5;" />
                        </c:if>
                        <div id="coach-details-${train.trainID}-${coach.coachID}" class="coach-details" style="${coachDetailsStyleString}">
                            <div class="coach-info">
                                <h3>Toa ${coach.coachNumber} - ${coach.coachName}</h3>
                                <p>${coach.coachName}</p>
                            </div>
                            <div class="seat-grid">
                                <c:forEach var="seat" items="${seatsByCoach[coach.coachID]}">
                                    <div class="seat" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>
                                        <span>${seat.seatName}</span>
                                        <a href="manage-trains-seats?action=delete_seat&id=${seat.seatID}" class="delete-btn" onclick="return confirm('Are you sure you want to delete this seat?');" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>
                                            <i class="fas fa-times"></i>
                                        </a>
                                    </div>
                                </c:forEach>
                                <a href="javascript:void(0);" onclick="openModal('add-seat-modal-${coach.coachID}')" class="seat add-seat-btn" <c:if test='${train.isLocked}'>style="pointer-events:none;opacity:0.5;"</c:if>>+</a>
                            </div>
                            <div id="add-seat-modal-${coach.coachID}" class="modal">
                                <div class="modal-content">
                                    <span class="close" onclick="closeModal('add-seat-modal-${coach.coachID}')"></span>
                                    <h5>Add Seat to Coach ${coach.coachNumber}</h5>
                                    <form id="add-seat-form-${coach.coachID}" action="manage-trains-seats" method="post">
                                        <input type="hidden" name="action" value="insert_seat" />
                                        <input type="hidden" name="coachId" value="${coach.coachID}" />
                                        <table>
                                            <tr>
                                                <th>Row Letter:</th>
                                                <td><input type="text" name="rowLetter" maxlength="2" value="A" required /></td>
                                            </tr>
                                            <tr>
                                                <th>Seats per Row:</th>
                                                <td><input type="number" name="seatsPerRow" min="1" value="1" required /></td>
                                            </tr>
                                            <tr>
                                                <th>Seat Prefix:</th>
                                                <td><input type="text" name="prefix" value="" maxlength="2" /></td>
                                            </tr>
                                            <tr>
                                                <th>Seat Type:</th>
                                                <td>
                                                    <select name="typeCode" <c:if test='${train.isLocked}'>disabled</c:if>>
                                                        <c:forEach var="seatType" items="${listSeatType}">
                                                            <option value="${seatType.seatTypeID}">${seatType.typeName}</option>
                                                        </c:forEach>
                                                    </select>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2"><input type="submit" value="Add Seats" <c:if test='${train.isLocked}'>disabled</c:if>/></td>
                                            </tr>
                                        </table>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </div>
        <div class="pagination-container" id="pagination-container"></div>
    </div>
</body>
</html>
