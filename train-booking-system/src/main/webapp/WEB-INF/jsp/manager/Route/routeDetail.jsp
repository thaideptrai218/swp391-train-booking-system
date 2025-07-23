<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Tuyến Đường - <c:out value="${currentRoute.routeName}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/routes/manageRoutes.css"> <%-- Assuming same CSS can be reused --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.15.0/Sortable.min.js"></script>
    <style>
        /* Basic Modal Styling (copied from manageRoutes.jsp) */
        .modal {
            display: none; /* Hidden by default */
            position: fixed; /* Stay in place */
            z-index: 1000; /* Sit on top */
            left: 0;
            top: 0;
            width: 100%; /* Full width */
            height: 100%; /* Full height */
            overflow: auto; /* Enable scroll if needed */
            background-color: rgb(0,0,0); /* Fallback color */
            background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
            padding-top: 60px;
        }

        .modal-content {
            background-color: #fefefe;
            margin: 5% auto; /* 15% from the top and centered */
            padding: 20px;
            border: 1px solid #888;
            width: 80%; /* Could be more or less, depending on screen size */
            max-width: 500px;
            border-radius: 8px;
        }

        .close-btn {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }

        .close-btn:hover,
        .close-btn:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
        .route-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        .route-header h2 {
            margin: 0;
        }
        .non-draggable {
            cursor: not-allowed !important;
            /* opacity: 0.7; */ /* Optional: visual cue */
        }
        .stations-table tbody tr.non-draggable:hover {
            background-color: inherit; /* Prevent hover effect on non-draggable rows */
        }
        .route-actions .btn {
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <jsp:include page="../sidebar.jsp" />

    <div class="main-content" id="mainContent">
        <a href="${pageContext.request.contextPath}/manageRoutes" class="btn btn-secondary mb-3"><i class="fas fa-arrow-left"></i> Quay Lại Danh Sách Tuyến</a>
        
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="success-message" style="background-color: #d4edda; color: #155724; padding: 10px; border: 1px solid #c3e6cb; border-radius: 4px; margin-bottom: 20px;">
                ${successMessage}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty currentRoute}">
                <h1>Tuyến Đường Không Tìm Thấy</h1>
                <p>Tuyến đường bạn yêu cầu không tồn tại hoặc đã bị xóa.</p>
            </c:when>
            <c:otherwise>
                <div class="route-header">
                    <h2><c:out value="${currentRoute.routeName}" /> (ID: ${currentRoute.routeID})</h2>
                    <div class="route-actions">
                        <a href="${pageContext.request.contextPath}/manageRoutes?action=editRoute&routeId=${currentRoute.routeID}#editRouteForm" class="btn btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                        <form action="${pageContext.request.contextPath}/manageRoutes" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa tuyến đường này và tất cả các trạm của nó không?');">
                            <input type="hidden" name="action" value="deleteRoute">
                            <input type="hidden" name="routeId" value="${currentRoute.routeID}">
                            <button type="submit" class="btn btn-danger"><i class="fas fa-trash"></i> Xóa</button>
                        </form>
                    </div>
                </div>
                <p><strong>Mô tả:</strong> <c:out value="${currentRoute.description}" /></p>
                
                <h3>Các Trạm Trong Tuyến:</h3>
                <table class="stations-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên Trạm</th>
                            <th>Khoảng Cách (km)</th>
                            <th>Thời Gian Dừng (phút)</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="hasStations" value="${false}" />
                        <c:forEach items="${routeDetailsForCurrentRoute}" var="detail" varStatus="loopStatus">
                            <c:set var="hasStations" value="${true}" />
                            <tr class="${(loopStatus.first || loopStatus.last) && fn:length(routeDetailsForCurrentRoute) > 1 ? 'non-draggable' : ''}">
                                <td>${detail.stationID}</td>
                                <td><c:out value="${detail.stationName}" /></td>
                                <td><fmt:formatNumber value="${detail.distanceFromStart}" minFractionDigits="1" maxFractionDigits="2"/></td>
                                <td><c:out value="${detail.defaultStopTime}" /></td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/manager/routeDetail?action=editRouteStation&routeId=${detail.routeID}&stationId=${detail.stationID}#editRouteStationForm" class="btn btn-warning btn-sm"><i class="fas fa-edit"></i> Sửa Trạm</a>
                                    <form action="${pageContext.request.contextPath}/manageRoutes" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa trạm này khỏi tuyến đường không?');">
                                        <input type="hidden" name="action" value="removeStationFromRoute">
                                        <input type="hidden" name="routeId" value="${detail.routeID}">
                                        <input type="hidden" name="stationId" value="${detail.stationID}">
                                        <button type="submit" class="btn btn-danger btn-sm"><i class="fas fa-times-circle"></i> Xóa Trạm</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${not hasStations}">
                            <tr><td colspan="4">Tuyến đường này chưa có trạm nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
                <button data-route-id="${currentRoute.routeID}" data-route-name="<c:out value='${currentRoute.routeName}'/>" onclick="prepareAndOpenAddStationModal(this)" class="btn btn-primary mt-3"><i class="fas fa-plus"></i> Thêm Trạm Vào Tuyến Này</button>
            
                <!-- Edit Route Form (conditionally shown) -->
                <c:if test="${not empty routeToEdit && routeToEdit.routeID == currentRoute.routeID}">
                    <div class="container mt-4" id="editRouteForm">
                        <h2><i class="fas fa-edit"></i> Chỉnh Sửa Tuyến Đường: <c:out value="${routeToEdit.routeName}"/></h2>
                        <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                            <input type="hidden" name="action" value="updateRoute">
                            <input type="hidden" name="routeId" value="${routeToEdit.routeID}">
                            <div class="form-group">
                                <label for="editRouteName">Tên Tuyến Đường:</label>
                                <input type="text" id="editRouteName" name="routeName" value="<c:out value="${routeToEdit.routeName}"/>" required class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="editDescription">Mô Tả:</label>
                                <textarea id="editDescription" name="description" class="form-control"><c:out value="${routeToEdit.description}"/></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập Nhật Tuyến Đường</button>
                            <a href="${pageContext.request.contextPath}/manager/routeDetail?routeId=${currentRoute.routeID}" class="btn btn-secondary">Hủy</a>
                        </form>
                    </div>
                </c:if>

                <!-- Edit RouteStation Form (conditionally shown) -->
                <c:if test="${not empty routeStationToEdit && routeStationToEdit.routeID == currentRoute.routeID}">
                    <div class="container mt-4" id="editRouteStationForm">
                        <h2><i class="fas fa-edit"></i> Chỉnh Sửa Trạm Trong Tuyến: <c:out value="${currentRoute.routeName}"/></h2>
                        <h4>Trạm: <c:out value="${routeStationToEdit.stationName}"/></h4>
                        <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                            <input type="hidden" name="action" value="updateRouteStation">
                            <input type="hidden" name="routeId" value="${routeStationToEdit.routeID}">
                            <input type="hidden" name="originalStationId" value="${routeStationToEdit.stationID}"> 
                            
                            <div class="form-group">
                                <label for="editRsStationId">Trạm:</label>
                                <c:set var="isStationLocked" value="${isFirstStation || isLastStation}" />
                                <select id="editRsStationId" name="stationId" required class="form-control" ${isStationLocked ? 'disabled' : ''}>
                                    <c:forEach items="${allStations}" var="station">
                                        <option value="${station.stationID}" ${station.stationID == routeStationToEdit.stationID ? 'selected' : ''}>
                                            <c:out value="${station.stationName}" /> 
                                        </option>
                                    </c:forEach>
                                </select>
                                <c:if test="${isStationLocked}">
                                    <input type="hidden" name="stationId" value="${routeStationToEdit.stationID}">
                                </c:if>
                            </div>
                            <div class="form-group">
                                <label for="editRsDistanceFromStart">Khoảng Cách Từ Điểm Đầu (km):</label>
                                <input type="number" id="editRsDistanceFromStart" name="distanceFromStart" value="${routeStationToEdit.distanceFromStart}" required step="0.1" min="0" class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="editRsDefaultStopTime">Thời Gian Dừng Mặc Định (phút):</label>
                                <input type="number" id="editRsDefaultStopTime" name="defaultStopTime" value="${routeStationToEdit.defaultStopTime}" required min="0" class="form-control">
                            </div>
                            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập Nhật Trạm</button>
                            <a href="${pageContext.request.contextPath}/manager/routeDetail?routeId=${currentRoute.routeID}#route-${currentRoute.routeID}" class="btn btn-secondary">Hủy</a>
                        </form>
                    </div>
                </c:if>
            </c:otherwise>
        </c:choose>
    </div> <!-- End Main Content -->

    <!-- Modal for Adding Station to Route -->
    <div id="addStationModal" class="modal">
        <div class="modal-content">
            <span class="close-btn" onclick="closeAddStationModal()">&times;</span>
            <h2>Thêm Trạm vào Tuyến: <span id="modalRouteName"></span></h2>
            <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                <input type="hidden" name="action" value="addStationToRoute">
                <input type="hidden" id="modalRouteIdForStation" name="routeIdForStation">
                
                <div class="form-group">
                    <label for="modalStationId">Chọn Trạm:</label>
                    <select id="modalStationId" name="stationId" required class="form-control">
                        <option value="">-- Chọn Trạm --</option>
                        <c:forEach items="${availableStationsToAdd}" var="station">
                                <option value="${station.stationID}"><c:out value="${station.stationName}" /> (ID: ${station.stationID})</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="modalDistanceFromStart">Khoảng Cách Từ Điểm Đầu (km):</label>
                    <input type="number" id="modalDistanceFromStart" name="distanceFromStart" required step="0.1" min="0" class="form-control">
                </div>
                <div class="form-group">
                    <label for="modalDefaultStopTime">Thời Gian Dừng Mặc Định (phút):</label>
                    <input type="number" id="modalDefaultStopTime" name="defaultStopTime" required min="0" class="form-control">
                </div>
                <button type="submit" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Thêm Trạm</button>
            </form>
        </div>
    </div>

    <script>
        var addStationModal = document.getElementById("addStationModal");

        function prepareAndOpenAddStationModal(buttonElement) {
            const routeId = buttonElement.dataset.routeId;
            const routeName = buttonElement.dataset.routeName;
            openAddStationModal(routeId, routeName);
        }

        function openAddStationModal(routeId, routeName) {
            document.getElementById("modalRouteIdForStation").value = routeId;
            document.getElementById("modalRouteName").textContent = routeName;
            addStationModal.style.display = "block";
        }

        function closeAddStationModal() {
            addStationModal.style.display = "none";
        }

        window.onclick = function(event) {
            if (event.target == addStationModal) {
                closeAddStationModal();
            }
        }
        
        document.addEventListener("DOMContentLoaded", function() {
            const urlParams = new URLSearchParams(window.location.search);
            const action = urlParams.get('action');
            const currentRouteId = "${currentRoute.routeID}"; // Get current route ID for comparison

            if (action === 'editRoute' && urlParams.get('routeId') === currentRouteId && window.location.hash === '#editRouteForm') {
                 const formElement = document.getElementById('editRouteForm');
                 if(formElement) formElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
            if (action === 'editRouteStation' && urlParams.get('routeId') === currentRouteId && window.location.hash === '#editRouteStationForm') {
                  const formElement = document.getElementById('editRouteStationForm');
                 if(formElement) formElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }

            const stationsTableBody = document.querySelector('.stations-table tbody');
            if (stationsTableBody) {
                new Sortable(stationsTableBody, {
                    animation: 150,
                    // Omitting 'handle' makes entire sortable items draggable by default.
                    // Omitting 'items' makes all direct children of stationsTableBody potential sortable items.
                    filter: '.non-draggable', // Elements with this class will not be draggable.
                    preventOnFilter: true,    // This is important to stop the drag attempt on filtered items.
                    ghostClass: 'sortable-ghost',
                    chosenClass: 'sortable-chosen',
                    dragClass: 'sortable-drag',
                    onMove: function (/**Event*/evt, /**Event*/originalEvent) {
                        // evt.dragged is the item being dragged
                        // evt.related is the item being dragged over (the potential target sibling)
                        // evt.to is the target list (tbody)
                        
                        // Prevent dragging of non-draggable items (already handled by filter, but good defense)
                        if (evt.dragged.classList.contains('non-draggable')) {
                            return false; 
                        }

                        // Prevent dropping onto/displacing a non-draggable item
                        if (evt.related.classList.contains('non-draggable')) {
                            return false;
                        }
                        
                        // Additional check: Prevent a draggable item from being placed
                        // as the new first child if the actual first child is non-draggable.
                        // evt.willInsertAfter is true if dragging below 'related', false if above.
                        // newDraggableIndex is the index it would take.
                        const children = Array.from(evt.to.children);
                        const firstChild = children[0];
                        const lastChild = children[children.length - 1];

                        if (firstChild && firstChild.classList.contains('non-draggable')) {
                            if (evt.related === firstChild && evt.willInsertAfter === false) {
                                // Trying to drop *before* the first non-draggable item
                                return false;
                            }
                             // If the item would end up at index 0 and it's not the original first item
                            if (evt.newDraggableIndex === 0 && evt.dragged !== firstChild) {
                                return false;
                            }
                        }

                        if (lastChild && lastChild.classList.contains('non-draggable')) {
                             if (evt.related === lastChild && evt.willInsertAfter === true) {
                                 // Trying to drop *after* the last non-draggable item
                                 return false;
                             }
                             // If the item would end up at the last index and it's not the original last item
                             if (evt.newDraggableIndex === (children.length -1) && evt.dragged !== lastChild) {
                                 return false;
                             }
                        }
                        
                        return true; // Allow other moves
                    },
                    onEnd: function (evt) {
                        sendNewStationOrder(currentRouteId, stationsTableBody);
                    }
                });
            }

            function sendNewStationOrder(routeId, tbody) {
                const stationsOrder = [];
                tbody.querySelectorAll('tr').forEach(function(row, index) {
                    const firstCellText = row.cells[0].textContent;
                    const stationIdMatch = firstCellText.match(/\(ID: (\d+)\)/);
                    if (stationIdMatch && stationIdMatch[1]) {
                        stationsOrder.push({
                            stationId: parseInt(stationIdMatch[1]),
                            sequenceNumber: index + 1 // Sequence is 1-based
                        });
                    }
                });

                if (stationsOrder.length > 0) {
                    fetch('${pageContext.request.contextPath}/manageRoutes', { // Action still points to manageRoutes servlet for backend logic
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: new URLSearchParams({
                            action: 'updateStationOrder',
                            routeId: routeId,
                            stationsOrder: JSON.stringify(stationsOrder)
                        })
                    })
                    .then(response => response.json())
                    .then(data => {
                        const messageContainer = document.querySelector('.main-content'); // General area for messages
                        let feedbackDiv = document.getElementById('updateFeedbackMessage');
                        if (!feedbackDiv) {
                            feedbackDiv = document.createElement('div');
                            feedbackDiv.id = 'updateFeedbackMessage';
                            feedbackDiv.style.padding = '10px';
                            feedbackDiv.style.border = '1px solid';
                            feedbackDiv.style.borderRadius = '4px';
                            feedbackDiv.style.marginTop = '10px';
                            feedbackDiv.style.marginBottom = '10px';
                            // Insert after h3 "Các Trạm Trong Tuyến:"
                            const stationsHeader = Array.from(document.querySelectorAll('h3')).find(h => h.textContent.includes('Các Trạm Trong Tuyến:'));
                            if(stationsHeader) stationsHeader.insertAdjacentElement('afterend', feedbackDiv);
                            else messageContainer.insertBefore(feedbackDiv, messageContainer.firstChild); // Fallback
                        }

                        if (data.success) {
                            feedbackDiv.className = 'success-message';
                            feedbackDiv.style.backgroundColor = '#d4edda';
                            feedbackDiv.style.color = '#155724';
                            feedbackDiv.style.borderColor = '#c3e6cb';
                            feedbackDiv.textContent = data.message || 'Thứ tự trạm đã được cập nhật!';
                        } else {
                            feedbackDiv.className = 'error-message';
                            feedbackDiv.style.backgroundColor = '#f8d7da';
                            feedbackDiv.style.color = '#721c24';
                            feedbackDiv.style.borderColor = '#f5c6cb';
                            feedbackDiv.textContent = 'Lỗi cập nhật thứ tự trạm: ' + (data.message || 'Vui lòng thử lại.');
                        }
                        setTimeout(() => {
                            if (feedbackDiv) feedbackDiv.remove();
                        }, 7000);
                    })
                    .catch(error => {
                        console.error('Error sending station order:', error);
                        const messageContainer = document.querySelector('.main-content');
                        let feedbackDiv = document.getElementById('updateFeedbackMessage');
                        if (!feedbackDiv) {
                             feedbackDiv = document.createElement('div');
                            feedbackDiv.id = 'updateFeedbackMessage';
                             feedbackDiv.style.padding = '10px';
                            feedbackDiv.style.border = '1px solid';
                            feedbackDiv.style.borderRadius = '4px';
                            feedbackDiv.style.marginTop = '10px';
                            feedbackDiv.style.marginBottom = '10px';
                            const stationsHeader = Array.from(document.querySelectorAll('h3')).find(h => h.textContent.includes('Các Trạm Trong Tuyến:'));
                            if(stationsHeader) stationsHeader.insertAdjacentElement('afterend', feedbackDiv);
                            else messageContainer.insertBefore(feedbackDiv, messageContainer.firstChild);
                        }
                        feedbackDiv.className = 'error-message';
                        feedbackDiv.style.backgroundColor = '#f8d7da';
                        feedbackDiv.style.color = '#721c24';
                        feedbackDiv.style.borderColor = '#f5c6cb';
                        feedbackDiv.textContent = 'Lỗi mạng khi cập nhật thứ tự trạm. Vui lòng kiểm tra kết nối và thử lại.';
                        setTimeout(() => {
                            if (feedbackDiv) feedbackDiv.remove();
                        }, 7000);
                    });
                }
            }
        });
    </script>
</body>
</html>
