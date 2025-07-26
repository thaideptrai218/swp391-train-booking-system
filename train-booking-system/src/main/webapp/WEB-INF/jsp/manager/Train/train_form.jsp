<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Train Form</title>
</head>
<body>
    <h1><c:if test="${train != null}">Edit Train</c:if><c:if test="${train == null}">Add Train</c:if></h1>
    <form action="manage-trains-seats" method="post">
        <c:if test="${train != null}">
            <input type="hidden" name="action" value="update_train" />
            <input type="hidden" name="trainCode" value="<c:out value='${train.trainName}' />" />
        </c:if>
        <c:if test="${train == null}">
            <input type="hidden" name="action" value="insert_train" />
        </c:if>

        <table>
            <tr>
                <th>Train Code:</th>
                <td><input type="text" name="trainCode" value="<c:out value='${train.trainName}' />" <c:if test="${train != null}">readonly</c:if> /></td>
            </tr>
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
                <td colspan="2"><input type="submit" value="Save" /></td>
            </tr>
        </table>
    </form>
</body>
</html>
