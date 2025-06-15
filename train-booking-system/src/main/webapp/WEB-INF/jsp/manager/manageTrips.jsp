<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Manage Trips</title>
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/manageTrips.css"
    />
  </head>
  <body>
    <div class="dashboard-container">
      <%@ include file="sidebar.jsp" %>
      <div class="main-content">
        <header class="dashboard-header">
          <h1>Manage Trips</h1>
        </header>

        <section class="content-section">
          <div class="controls-container">
            <a
              href="${pageContext.request.contextPath}/manageTrips?action=showAddForm"
              class="action-add"
              >Add New Trip</a
            >
            <!-- Filter and Sort Form has been removed -->
          </div>
          <%-- End of controls-container --%>

          <c:if test="${not empty requestScope.errorMessage}">
            <p class="message error-message">${requestScope.errorMessage}</p>
          </c:if>
          <c:if test="${not empty requestScope.successMessage}">
            <p class="message success-message">
              ${requestScope.successMessage}
            </p>
          </c:if>

          <div class="table-container">
            <table>
              <thead>
                <tr>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=tripID&sortOrder=${param.sortField == 'tripID' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Trip ID</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=trainName&sortOrder=${param.sortField == 'trainName' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Train Name</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=routeName&sortOrder=${param.sortField == 'routeName' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Route Name</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=departureDateTime&sortOrder=${param.sortField == 'departureDateTime' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Departure</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=arrivalDateTime&sortOrder=${param.sortField == 'arrivalDateTime' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Arrival</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=isHolidayTrip&sortOrder=${param.sortField == 'isHolidayTrip' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Holiday Trip</a
                    >
                  </th>
                  <th>
                    <a
                      href="${pageContext.request.contextPath}/manageTrips?action=list&sortField=tripStatus&sortOrder=${param.sortField == 'tripStatus' && param.sortOrder == 'ASC' ? 'DESC' : 'ASC'}"
                      >Status</a
                    >
                  </th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <c:choose>
                  <c:when test="${not empty listTrips}">
                    <c:forEach var="trip" items="${listTrips}">
                      <tr>
                        <td>${trip.tripID}</td>
                        <td><c:out value="${trip.trainName}" /></td>
                        <td><c:out value="${trip.routeName}" /></td>
                        <td>
                          <c:out value="${trip.departureDateTime}" />
                        </td>
                        <td>
                          <c:out value="${trip.arrivalDateTime}" />
                        </td>
                        <td>${trip.holidayTrip ? 'Yes' : 'No'}</td>
                        <td><c:out value="${trip.tripStatus}" /></td>
                        <td class="table-actions">
                          <a
                            href="${pageContext.request.contextPath}/manageTrips?action=showEditForm&tripId=${trip.tripID}"
                            class="action-edit"
                            >Edit</a
                          >
                          <a
                            href="${pageContext.request.contextPath}/manageTrips?action=delete&tripId=${trip.tripID}"
                            class="action-delete"
                            onclick="return confirm('Are you sure you want to delete this trip?');"
                            >Delete</a
                          >
                        </td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td colspan="9">No trips found.</td>
                    </tr>
                  </c:otherwise>
                </c:choose>
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </div>
  </body>
</html>
