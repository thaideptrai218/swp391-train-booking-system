<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Xác nhận mua thẻ VIP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/vip-confirm.css" />
</head>
<body>
    <div class="container">
        <h1>Xác nhận thông tin mua thẻ</h1>
        <div class="vip-card-summary">
            <h2><c:out value="${vipCardType.typeName}" /></h2>
            <p class="price"><fmt:formatNumber value="${vipCardType.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" /></p>
            <p class="duration">Thời hạn: ${vipCardType.durationMonths} tháng</p>
            <p class="description"><c:out value="${vipCardType.description}" /></p>
        </div>

        <form action="${pageContext.request.contextPath}/vip-success" method="post">
            <input type="hidden" name="vipCardTypeId" value="${vipCardType.vipCardTypeID}" />
            <input type="hidden" name="durationMonths" value="${vipCardType.durationMonths}" />
            <div class="button-group">
                <button type="submit" class="btn-confirm">Xác nhận và thanh toán</button>
                <a href="${pageContext.request.contextPath}/vip-purchase" class="btn-cancel">Hủy</a>
            </div>
        </form>
    </div>
</body>
</html>