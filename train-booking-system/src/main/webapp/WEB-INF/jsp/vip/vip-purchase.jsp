<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mua thẻ VIP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vip-purchase.css" />
</head>
<body>
    <div class="container">
        <h1>Chọn loại thẻ VIP</h1>
        <div class="vip-cards-container">
            <c:forEach var="vipCardType" items="${vipCardTypes}">
                <div class="vip-card">
                    <h2><c:out value="${vipCardType.typeName}" /></h2>
                    <p class="price"><fmt:formatNumber value="${vipCardType.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" /></p>
                    <p class="duration">Thời hạn: ${vipCardType.durationMonths} tháng</p>
                    <p class="description"><c:out value="${vipCardType.description}" /></p>
                    <form action="${pageContext.request.contextPath}/vip-confirm" method="post">
                        <input type="hidden" name="vipCardTypeId" value="${vipCardType.vipCardTypeID}" />
                        <button type="submit" class="btn-purchase">Mua ngay</button>
                    </form>
                </div>
            </c:forEach>
        </div>
    </div>
</body>
</html>
