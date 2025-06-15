<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Tuyến Đường</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/manageRoutes.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.15.0/Sortable.min.js"></script>
    <style>
        /* Basic Modal Styling */
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
    </style>
</head>
<body>
    <jsp:include page="sidebar.jsp" />

    <div class="main-content" id="mainContent">
        <h1><i class="fas fa-route"></i> Quản Lý Tuyến Đường</h1>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        <c:if test="${not empty successMessage}">
            <div class="success-message" style="background-color: #d4edda; color: #155724; padding: 10px; border: 1px solid #c3e6cb; border-radius: 4px; margin-bottom: 20px;">
                ${successMessage}
            </div>
        </c:if>

        <!-- Add New Route Form -->
        <div class="container">
            <h2><i class="fas fa-plus-circle"></i> Thêm Tuyến Đường Mới</h2>
            <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                <input type="hidden" name="action" value="addRoute">
                <div class="form-group">
                    <label for="routeName">Tên Tuyến Đường:</label>
                    <input type="text" id="routeName" name="routeName" required>
                </div>
                <div class="form-group">
                    <label for="description">Mô Tả:</label>
                    <textarea id="description" name="description"></textarea>
                </div>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu Tuyến Đường</button>
            </form>
        </div>

        <!-- List of Routes and their Stations -->
        <h2><i class="fas fa-list-ul"></i> Danh Sách Tuyến Đường Hiện Có</h2>
        <c:choose>
            <c:when test="${empty allRoutes}">
                <p>Chưa có tuyến đường nào được tạo.</p>
            </c:when>
            <c:otherwise>
                <c:forEach items="${allRoutes}" var="route">
                    <div class="route-block" id="route-${route.routeID}">
                        <h3>
                            <c:out value="${route.routeName}" /> (ID: ${route.routeID})
                            <a href="${pageContext.request.contextPath}/manageRoutes?action=editRoute&routeId=${route.routeID}#editRouteForm" class="btn btn-warning btn-sm"><i class="fas fa-edit"></i> Sửa</a>
                            <form action="${pageContext.request.contextPath}/manageRoutes" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa tuyến đường này và tất cả các trạm của nó không?');">
                                <input type="hidden" name="action" value="deleteRoute">
                                <input type="hidden" name="routeId" value="${route.routeID}">
                                <button type="submit" class="btn btn-danger btn-sm"><i class="fas fa-trash"></i> Xóa</button>
                            </form>
                        </h3>
                        <p><strong>Mô tả:</strong> <c:out value="${route.description}" /></p>
                        
                        <h4>Các Trạm Trong Tuyến:</h4>
                        <table class="stations-table">
                            <thead>
                                <tr>
                                    <th>Tên Trạm</th>
                                    <th>Khoảng Cách (km)</th>
                                    <th>Thời Gian Dừng (phút)</th>
                                    <th>Hành Động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:set var="hasStations" value="${false}" />
                                <c:forEach items="${routeDetails}" var="detail">
                                    <c:if test="${detail.routeID == route.routeID}">
                                        <c:set var="hasStations" value="${true}" />
                                        <tr>
                                            <td><c:out value="${detail.stationName}" /> (ID: ${detail.stationID})</td>
                                            <td><fmt:formatNumber value="${detail.distanceFromStart}" minFractionDigits="1" maxFractionDigits="2"/></td>
                                            <td><c:out value="${detail.defaultStopTime}" /></td>
                                            <td class="actions">
                                                <a href="${pageContext.request.contextPath}/manageRoutes?action=editRouteStation&routeId=${detail.routeID}&stationId=${detail.stationID}#editRouteStationForm" class="btn btn-warning btn-sm"><i class="fas fa-edit"></i> Sửa Trạm</a>
                                                <form action="${pageContext.request.contextPath}/manageRoutes" method="post" style="display: inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa trạm này khỏi tuyến đường không?');">
                                                    <input type="hidden" name="action" value="removeStationFromRoute">
                                                    <input type="hidden" name="routeId" value="${detail.routeID}">
                                                    <input type="hidden" name="stationId" value="${detail.stationID}">
                                                    <button type="submit" class="btn btn-danger btn-sm"><i class="fas fa-times-circle"></i> Xóa Trạm</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                <c:if test="${not hasStations}">
                                    <tr><td colspan="4">Tuyến đường này chưa có trạm nào.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                        <button data-route-id="${route.routeID}" data-route-name="<c:out value='${route.routeName}'/>" onclick="prepareAndOpenAddStationModal(this)" class="btn btn-primary btn-sm"><i class="fas fa-plus"></i> Thêm Trạm Vào Tuyến Này</button>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <!-- Edit Route Form (hidden by default or shown via query param) -->
        <c:if test="${not empty routeToEdit}">
            <div class="container" id="editRouteForm">
                <h2><i class="fas fa-edit"></i> Chỉnh Sửa Tuyến Đường: <c:out value="${routeToEdit.routeName}"/></h2>
                <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                    <input type="hidden" name="action" value="updateRoute">
                    <input type="hidden" name="routeId" value="${routeToEdit.routeID}">
                    <div class="form-group">
                        <label for="editRouteName">Tên Tuyến Đường:</label>
                        <input type="text" id="editRouteName" name="routeName" value="<c:out value="${routeToEdit.routeName}"/>" required>
                    </div>
                    <div class="form-group">
                        <label for="editDescription">Mô Tả:</label>
                        <textarea id="editDescription" name="description"><c:out value="${routeToEdit.description}"/></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập Nhật Tuyến Đường</button>
                    <a href="${pageContext.request.contextPath}/manageRoutes" class="btn">Hủy</a>
                </form>
            </div>
        </c:if>

        <!-- Edit RouteStation Form (Modal or inline) -->
         <c:if test="${not empty routeStationToEdit}">
            <div class="container" id="editRouteStationForm">
                <h2><i class="fas fa-edit"></i> Chỉnh Sửa Trạm Trong Tuyến: <c:out value="${routeStationToEdit.routeName}"/></h2>
                <h4>Trạm: <c:out value="${routeStationToEdit.stationName}"/></h4>
                <form action="${pageContext.request.contextPath}/manageRoutes" method="post">
                    <input type="hidden" name="action" value="updateRouteStation">
                    <input type="hidden" name="routeId" value="${routeStationToEdit.routeID}">
                    <input type="hidden" name="originalStationId" value="${routeStationToEdit.stationID}"> 
                    
                    <div class="form-group">
                        <label for="editRsStationId">Trạm:</label>
                        <select id="editRsStationId" name="stationId" required>
                            <c:forEach items="${allStations}" var="station">
                                <option value="${station.stationID}" ${station.stationID == routeStationToEdit.stationID ? 'selected' : ''}>
                                    <c:out value="${station.stationName}" /> (ID: ${station.stationID})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <%-- Sequence number is now managed by drag-and-drop --%>
                    <%-- <div class="form-group">
                        <label for="editRsSequenceNumber">Thứ Tự:</label>
                        <input type="number" id="editRsSequenceNumber" name="sequenceNumber" value="${routeStationToEdit.sequenceNumber}" required min="1">
                    </div> --%>
                    <div class="form-group">
                        <label for="editRsDistanceFromStart">Khoảng Cách Từ Điểm Đầu (km):</label>
                        <input type="number" id="editRsDistanceFromStart" name="distanceFromStart" value="${routeStationToEdit.distanceFromStart}" required step="0.1" min="0">
                    </div>
                    <div class="form-group">
                        <label for="editRsDefaultStopTime">Thời Gian Dừng Mặc Định (phút):</label>
                        <input type="number" id="editRsDefaultStopTime" name="defaultStopTime" value="${routeStationToEdit.defaultStopTime}" required min="0">
                    </div>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập Nhật Trạm</button>
                    <a href="${pageContext.request.contextPath}/manageRoutes#route-${routeStationToEdit.routeID}" class="btn">Hủy</a>
                </form>
            </div>
        </c:if>

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
                    <select id="modalStationId" name="stationId" required>
                        <option value="">-- Chọn Trạm --</option>
                        <c:forEach items="${allStations}" var="station">
                                <option value="${station.stationID}"><c:out value="${station.stationName}" /> (ID: ${station.stationID})</option>
                        </c:forEach>
                    </select>
                </div>
                <%-- Sequence number will be automatically assigned when adding a new station --%>
                <%-- <div class="form-group">
                    <label for="modalSequenceNumber">Thứ Tự Trong Tuyến:</label>
                    <input type="number" id="modalSequenceNumber" name="sequenceNumber" required min="1">
                </div> --%>
                <div class="form-group">
                    <label for="modalDistanceFromStart">Khoảng Cách Từ Điểm Đầu (km):</label>
                    <input type="number" id="modalDistanceFromStart" name="distanceFromStart" required step="0.1" min="0">
                </div>
                <div class="form-group">
                    <label for="modalDefaultStopTime">Thời Gian Dừng Mặc Định (phút):</label>
                    <input type="number" id="modalDefaultStopTime" name="defaultStopTime" required min="0">
                </div>
                <button type="submit" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Thêm Trạm</button>
            </form>
        </div>
    </div>

    <script>
        // Modal script
        var addStationModal = document.getElementById("addStationModal");

        function prepareAndOpenAddStationModal(buttonElement) {
            const routeId = buttonElement.dataset.routeId;
            const routeName = buttonElement.dataset.routeName; // Browser handles HTML un-escaping from data-*
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

        // Close modal if user clicks outside of it
        window.onclick = function(event) {
            if (event.target == addStationModal) {
                closeAddStationModal();
            }
        }
        
        // Highlight route after redirect
        document.addEventListener("DOMContentLoaded", function() {
            const urlParams = new URLSearchParams(window.location.search);
            const highlightRouteId = urlParams.get('highlightRoute');
            if (highlightRouteId) {
                const routeElement = document.getElementById('route-' + highlightRouteId);
                if (routeElement) {
                    routeElement.style.backgroundColor = '#d1ecf1'; // A light blue highlight
                    routeElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
            const action = urlParams.get('action');
            if (action === 'editRoute' && window.location.hash === '#editRouteForm') {
                 const formElement = document.getElementById('editRouteForm');
                 if(formElement) formElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
            if (action === 'editRouteStation' && window.location.hash === '#editRouteStationForm') {
                  const formElement = document.getElementById('editRouteStationForm');
                 if(formElement) formElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }

            // Initialize SortableJS for each stations table
            document.querySelectorAll('.stations-table tbody').forEach(function(tbody) {
                new Sortable(tbody, {
                    animation: 150,
                    handle: 'tr', // Entire row is draggable
                    ghostClass: 'sortable-ghost', // Class for the dragging ghost
                    chosenClass: 'sortable-chosen', // Class for the chosen item
                    dragClass: 'sortable-drag', // Class for the dragging item
                    onEnd: function (evt) {
                        const table = evt.from.closest('.stations-table');
                        const routeBlock = table.closest('.route-block');
                        const routeId = routeBlock.id.replace('route-', '');
                        
                        // updateSequenceNumbers(tbody); // No longer needed as the column is removed
                        sendNewStationOrder(routeId, tbody);
                    }
                });
            });

            // function updateSequenceNumbers(tbody) { // This function is no longer needed
            //     tbody.querySelectorAll('tr').forEach(function(row, index) {
            //         const sequenceCell = row.cells[1]; 
            //         if (sequenceCell) {
            //             sequenceCell.textContent = index + 1;
            //         }
            //     });
            // }

            function sendNewStationOrder(routeId, tbody) {
                const stationsOrder = [];
                tbody.querySelectorAll('tr').forEach(function(row, index) {
                    // Extract stationId. Assuming station ID is part of the first cell's content like "(ID: X)"
                    // or available in a data attribute on the row or a specific cell.
                    // For this example, let's assume the station ID is in the first cell text like "Station Name (ID: 123)"
                    const firstCellText = row.cells[0].textContent;
                    const stationIdMatch = firstCellText.match(/\(ID: (\d+)\)/);
                    if (stationIdMatch && stationIdMatch[1]) {
                        stationsOrder.push({
                            stationId: parseInt(stationIdMatch[1]),
                            sequenceNumber: index + 1
                        });
                    }
                });

                if (stationsOrder.length > 0) {
                    fetch('${pageContext.request.contextPath}/manageRoutes', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        // body: `action=updateStationOrder&routeId=${routeId}&stationsOrder=${JSON.stringify(stationsOrder)}`
                        // Sending as form data as servlets typically expect that
                        body: new URLSearchParams({
                            action: 'updateStationOrder',
                            routeId: routeId,
                            stationsOrder: JSON.stringify(stationsOrder)
                        })
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            console.log('Station order updated successfully.');
                            // Optionally, display a success message to the user
                            // Find the route block to append the message
                            const routeBlock = document.getElementById('route-' + routeId);
                            if (routeBlock) {
                                let successDiv = routeBlock.querySelector('.update-success-message');
                                if (!successDiv) {
                                    successDiv = document.createElement('div');
                                    successDiv.className = 'success-message update-success-message'; // Use existing styles
                                    successDiv.style.backgroundColor = '#d4edda';
                                    successDiv.style.color = '#155724';
                                    successDiv.style.padding = '10px';
                                    successDiv.style.border = '1px solid #c3e6cb';
                                    successDiv.style.borderRadius = '4px';
                                    successDiv.style.marginTop = '10px';
                                    // Insert after the table or button
                                    const addButton = routeBlock.querySelector('.btn-primary.btn-sm');
                                    if (addButton) {
                                        addButton.insertAdjacentElement('afterend', successDiv);
                                    } else {
                                         routeBlock.appendChild(successDiv); // fallback
                                    }
                                }
                                successDiv.textContent = data.message || 'Thứ tự trạm đã được cập nhật!';
                                setTimeout(() => {
                                    if (successDiv) successDiv.remove();
                                }, 5000); // Remove message after 5 seconds
                            }
                        } else {
                            console.error('Failed to update station order:', data.message);
                            // Optionally, display an error message
                             const routeBlock = document.getElementById('route-' + routeId);
                            if (routeBlock) {
                                let errorDiv = routeBlock.querySelector('.update-error-message');
                                if (!errorDiv) {
                                    errorDiv = document.createElement('div');
                                    errorDiv.className = 'error-message update-error-message'; // Use existing styles
                                    errorDiv.style.backgroundColor = '#f8d7da';
                                    errorDiv.style.color = '#721c24';
                                    errorDiv.style.padding = '10px';
                                    errorDiv.style.border = '1px solid #f5c6cb';
                                    errorDiv.style.borderRadius = '4px';
                                    errorDiv.style.marginTop = '10px';
                                    const addButton = routeBlock.querySelector('.btn-primary.btn-sm');
                                    if (addButton) {
                                        addButton.insertAdjacentElement('afterend', errorDiv);
                                    } else {
                                         routeBlock.appendChild(errorDiv);
                                    }
                                }
                                errorDiv.textContent = 'Lỗi cập nhật thứ tự trạm: ' + (data.message || 'Vui lòng thử lại.');
                                 setTimeout(() => {
                                    if (errorDiv) errorDiv.remove();
                                }, 7000);
                            }
                        }
                    })
                    .catch(error => {
                        console.error('Error sending station order:', error);
                        // Optionally, display a network error message
                        const routeBlock = document.getElementById('route-' + routeId);
                        if (routeBlock) {
                             let errorDiv = routeBlock.querySelector('.update-error-message');
                            if (!errorDiv) {
                                errorDiv = document.createElement('div');
                                errorDiv.className = 'error-message update-error-message';
                                errorDiv.style.backgroundColor = '#f8d7da';
                                errorDiv.style.color = '#721c24';
                                errorDiv.style.padding = '10px';
                                errorDiv.style.border = '1px solid #f5c6cb';
                                errorDiv.style.borderRadius = '4px';
                                errorDiv.style.marginTop = '10px';
                                const addButton = routeBlock.querySelector('.btn-primary.btn-sm');
                                if (addButton) {
                                    addButton.insertAdjacentElement('afterend', errorDiv);
                                } else {
                                     routeBlock.appendChild(errorDiv);
                                }
                            }
                            errorDiv.textContent = 'Lỗi mạng khi cập nhật thứ tự trạm. Vui lòng kiểm tra kết nối và thử lại.';
                             setTimeout(() => {
                                if (errorDiv) errorDiv.remove();
                            }, 7000);
                        }
                    });
                }
            }
        });
    </script>
</body>
</html>
