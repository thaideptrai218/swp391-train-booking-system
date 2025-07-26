<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý loại thẻ VIP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/sidebar.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/managePrice.css" />
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f4f4f4;
            display: flex;
        }
        .main-content {
            flex-grow: 1;
            padding: 20px;
        }
        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            color: #333;
            margin-bottom: 20px;
        }
        .add-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 16px;
        }
        .add-button:hover {
            background-color: #0056b3;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
            vertical-align: middle;
        }
        th {
            background-color: #007bff;
            color: white;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .actions a {
            margin-right: 10px;
            text-decoration: none;
            padding: 5px 10px;
            border-radius: 3px;
            font-size: 14px;
        }
        .actions .edit {
            background-color: #ffc107;
            color: #333;
        }
        .actions .delete {
            background-color: #dc3545;
            color: white;
        }
        .actions .edit:hover {
            background-color: #e0a800;
        }
        .actions .delete:hover {
            background-color: #c82333;
        }
        .no-rules {
            text-align: center;
            font-size: 18px;
            color: #777;
            padding: 20px;
        }
    </style>
</head>
<body>
    <%@ include file="sidebar.jsp" %>
    <div class="main-content">
        <div class="container">
            <h1>Quản lý loại thẻ VIP</h1>

            <a href="${pageContext.request.contextPath}/manageVIPCardTypes?action=new" class="add-button">Thêm loại thẻ mới</a>

            <c:if test="${empty listVIPCardTypes}">
                <p class="no-rules">Không tìm thấy loại thẻ VIP nào.</p>
            </c:if>

            <c:if test="${not empty listVIPCardTypes}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên loại thẻ</th>
                            <th>Giá</th>
                            <th>Giảm giá (%)</th>
                            <th>Thời hạn (tháng)</th>
                            <th>Mô tả</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="vipCardType" items="${listVIPCardTypes}">
                            <tr>
                                <td>${vipCardType.vipCardTypeID}</td>
                                <td><c:out value="${vipCardType.typeName}" /></td>
                                <td><fmt:formatNumber value="${vipCardType.price}" type="currency" currencySymbol="₫" maxFractionDigits="0" /></td>
                                <td><fmt:formatNumber value="${vipCardType.discountPercentage}" maxFractionDigits="2" />%</td>
                                <td>${vipCardType.durationMonths}</td>
                                <td><c:out value="${vipCardType.description}" /></td>
                                <td class="actions">
                                    <a href="${pageContext.request.contextPath}/manageVIPCardTypes?action=edit&id=${vipCardType.vipCardTypeID}" class="edit">Sửa</a>
                                    <a href="${pageContext.request.contextPath}/manageVIPCardTypes?action=delete&id=${vipCardType.vipCardTypeID}" class="delete" onclick="return confirm('Bạn có chắc chắn muốn xóa loại thẻ này không?');">Xóa</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>
</body>
</html>
