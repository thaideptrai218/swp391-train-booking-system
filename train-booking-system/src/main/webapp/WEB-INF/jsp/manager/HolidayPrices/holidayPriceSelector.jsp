<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<h2>Chọn Giá Ngày Lễ</h2>
<table id="holidayPricesTable">
    <thead>
        <tr>
            <th>Ngày lễ</th>
            <th>Chi tiết</th>
            <th>Ngày bắt đầu</th>
            <th>Ngày kết thúc</th>
            <th>Chọn</th>
        </tr>
    </thead>
    <tbody>
        <c:if test="${empty listHolidayPrices}">
            <tr>
                <td colspan="5" class="no-rules">Không có dữ liệu</td>
            </tr>
        </c:if>
        <c:forEach var="holiday" items="${listHolidayPrices}">
            <tr data-holiday-name="${holiday.holidayName}" data-description="${holiday.description}" data-start-date="<fmt:formatDate value="${holiday.startDate}" pattern="yyyy-MM-dd" />" data-end-date="<fmt:formatDate value="${holiday.endDate}" pattern="yyyy-MM-dd" />">
                <td><c:out value="${holiday.holidayName}" /></td>
                <td><c:out value="${holiday.description}" /></td>
                <td><fmt:formatDate value="${holiday.startDate}" pattern="dd-MM-yyyy" /></td>
                <td><fmt:formatDate value="${holiday.endDate}" pattern="dd-MM-yyyy" /></td>
                <td><button type="button" class="select-holiday-btn">Chọn</button></td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<script>
document.addEventListener('DOMContentLoaded', function() {
    const selectButtons = document.querySelectorAll('.select-holiday-btn');
    selectButtons.forEach(button => {
        button.addEventListener('click', function() {
            const row = this.closest('tr');
            const holidayName = row.dataset.holidayName;
            const description = row.dataset.description;
            const startDate = row.dataset.startDate;
            const endDate = row.dataset.endDate;

            document.querySelector('input[name="ruleName"]').value = holidayName;
            document.querySelector('textarea[name="description"]').value = description;
            document.querySelector('input[name="effectiveFromDate"]').value = startDate;
            document.querySelector('input[name="effectiveToDate"]').value = endDate;
        });
    });
});
</script>
