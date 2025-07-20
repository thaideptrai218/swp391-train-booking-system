<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Trains and Seats</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/manager/manage-trains-seats.css" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css" />
</head>
<body>
    <%@ include file="sidebar.jsp" %>
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
                            <a href="javascript:void(0);" onclick="openModal('edit-train-modal-${train.trainID}')">Edit</a>
                            <a href="manage-trains-seats?action=delete_train&trainCode=${train.trainName}" onclick="return confirm('Are you sure you want to delete this train?');">
                                <i class="fas fa-times-circle"></i>
                            </a>
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

                    <div class="train-composition-display" style="display: none;">
                        <div class="train-head-item">
                            <img src="${pageContext.request.contextPath}/assets/icons/trip/train-head.svg" alt="Đầu tàu ${train.trainName}" class="train-head-svg-icon" />
                            <span class="train-name-label">${train.trainName}</span>
                        </div>
                        <c:forEach var="coach" items="${coachesByTrain[train.trainID]}">
                            <div class="carriage-item" onclick="toggleCoachDetails('${train.trainID}', '${coach.coachID}')">
                                <img src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg" class="carriage-svg-icon"/>
                                <span class="carriage-number-label">Toa ${coach.coachNumber}</span>
                                <a href="manage-trains-seats?action=delete_coach&id=${coach.coachID}" class="delete-btn" onclick="return confirm('Are you sure you want to delete this coach?');">
                                    <i class="fas fa-times"></i>
                                </a>
                            </div>
                        </c:forEach>
                        <a href="javascript:void(0);" onclick="openModal('add-coach-modal-${train.trainID}')" class="carriage-item">
                            <img src="${pageContext.request.contextPath}/assets/icons/trip/train-carriage.svg" class="carriage-svg-icon" style="opacity:0.5;"/>
                            <span class="carriage-number-label">Add Coach</span>
                        </a>
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
                        <div id="coach-details-${train.trainID}-${coach.coachID}" class="coach-details" style="display:none;">
                            <div class="coach-info">
                                <h3>Toa ${coach.coachNumber} - ${coach.coachName}</h3>
                                <p>${coach.coachName}</p>
                            </div>
                            <div class="seat-grid">
                                <c:forEach var="seat" items="${seatsByCoach[coach.coachID]}">
                                    <div class="seat">
                                        <span>${seat.seatName}</span>
                                        <a href="manage-trains-seats?action=delete_seat&id=${seat.seatID}" class="delete-btn" onclick="return confirm('Are you sure you want to delete this seat?');">
                                            <i class="fas fa-times"></i>
                                        </a>
                                    </div>
                                </c:forEach>
                                <a href="javascript:void(0);" onclick="openModal('add-seat-modal-${coach.coachID}')" class="seat add-seat-btn">+</a>
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
                                                <th>Seat Number:</th>
                                                <td><input type="text" name="seatNumber" required /></td>
                                            </tr>
                                            <tr>
                                                <th>Seat Type:</th>
                                                <td>
                                                    <select name="typeCode">
                                                        <c:forEach var="seatType" items="${listSeatType}">
                                                            <option value="${seatType.seatTypeID}">${seatType.typeName}</option>
                                                        </c:forEach>
                                                    </select>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td colspan="2"><input type="submit" value="Add Seat" /></td>
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
    <script src="${pageContext.request.contextPath}/js/manager/manage-trains-seats.js"></script>
    <style>
      .btn-primary-add {
        background: linear-gradient(90deg, #007bff 0%, #0056b3 100%) !important;
        color: #fff !important;
        border: none !important;
        border-radius: 24px !important;
        padding: 12px 28px !important;
        font-size: 1.15em !important;
        font-weight: 800 !important;
        box-shadow: 0 2px 8px rgba(0,123,255,0.13) !important;
        transition: background 0.2s, color 0.2s, box-shadow 0.2s !important;
        display: inline-block !important;
        margin-bottom: 18px !important;
        margin-top: 8px !important;
        letter-spacing: 0.5px;
      }
      .btn-primary-add:hover {
        background: linear-gradient(90deg, #0056b3 0%, #007bff 100%) !important;
        color: #fff !important;
        box-shadow: 0 4px 16px rgba(0, 123, 255, 0.18) !important;
        text-decoration: none !important;
      }
    </style>
    <script>
      document.addEventListener('DOMContentLoaded', function() {
        const trainList = document.getElementById('trainList');
        const trainContainers = Array.from(trainList.querySelectorAll('.train-container'));
        const paginationContainer = document.getElementById('pagination-container');
        const trainsPerPage = 5;
        let currentPage = 1;
        function displayTrains(page) {
          currentPage = page;
          const start = (page - 1) * trainsPerPage;
          const end = start + trainsPerPage;
          trainContainers.forEach((container, idx) => {
            container.style.display = (idx >= start && idx < end) ? '' : 'none';
          });
        }
        function setupPagination() {
          paginationContainer.innerHTML = '';
          const pageCount = Math.ceil(trainContainers.length / trainsPerPage);
          if (pageCount <= 1) return;
          // Previous button
          const prevLink = document.createElement('a');
          prevLink.href = '#';
          prevLink.innerHTML = '&laquo;';
          prevLink.classList.add('page-link');
          if (currentPage === 1) prevLink.classList.add('disabled');
          prevLink.addEventListener('click', function(e) {
            e.preventDefault();
            if (currentPage > 1) {
              displayTrains(currentPage - 1);
              setupPagination();
            }
          });
          paginationContainer.appendChild(prevLink);
          // Page numbers
          for (let i = 1; i <= pageCount; i++) {
            const pageLink = document.createElement('a');
            pageLink.href = '#';
            pageLink.innerText = i;
            pageLink.classList.add('page-link');
            if (i === currentPage) pageLink.classList.add('active');
            pageLink.addEventListener('click', function(e) {
              e.preventDefault();
              displayTrains(i);
              setupPagination();
            });
            paginationContainer.appendChild(pageLink);
          }
          // Next button
          const nextLink = document.createElement('a');
          nextLink.href = '#';
          nextLink.innerHTML = '&raquo;';
          nextLink.classList.add('page-link');
          if (currentPage === pageCount) nextLink.classList.add('disabled');
          nextLink.addEventListener('click', function(e) {
            e.preventDefault();
            if (currentPage < pageCount) {
              displayTrains(currentPage + 1);
              setupPagination();
            }
          });
          paginationContainer.appendChild(nextLink);
        }
        // Initial setup
        displayTrains(1);
        setupPagination();

        // Auto-set next coach number and coach name on modal open
        document.querySelectorAll('a[onclick^="openModal(\'add-coach-modal-"]').forEach(function(btn) {
          btn.addEventListener('click', function() {
            var trainId = btn.getAttribute('onclick').match(/add-coach-modal-(\d+)/)[1];
            var coachCount = document.querySelectorAll('.train-container[data-train-id="' + trainId + '"] .carriage-item').length - 1; // exclude add button
            var nextNumber = coachCount + 1;
            document.getElementById('nextCoachNumber_' + trainId).value = nextNumber;
            document.getElementById('autoCoachName_' + trainId).value = 'Toa ' + nextNumber;
          });
        });
      });
    </script>
</body>
</html>
