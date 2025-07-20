<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8" />
                <title>Sửa Chính Sách Hủy Vé</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/sidebar.css" />
                <style>
                    body {
                        font-family: 'Segoe UI', sans-serif;
                        background-color: #f9f9f9;
                    }

                    .main-content {
                        margin-left: 250px;
                        padding: 20px;
                    }

                    .container {
                        max-width: 800px;
                        margin: auto;
                        background: #fff;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        padding: 30px;
                    }

                    h1 {
                        text-align: center;
                        margin-bottom: 25px;
                    }

                    form label {
                        display: block;
                        margin: 12px 0 6px;
                        font-weight: bold;
                    }

                    input[type="text"],
                    input[type="number"],
                    input[type="date"],
                    textarea,
                    select {
                        width: 100%;
                        padding: 10px;
                        border: 1px solid #ccc;
                        border-radius: 4px;
                    }

                    textarea {
                        resize: vertical;
                    }

                    button {
                        margin-top: 20px;
                        padding: 10px 20px;
                        background-color: #0078d4;
                        color: white;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                    }

                    button:hover {
                        background-color: #005fa3;
                    }

                    a {
                        display: inline-block;
                        margin-top: 20px;
                        color: #0078d4;
                        text-decoration: none;
                    }

                    a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>

            <body>
                <%@ include file="sidebar.jsp" %>

                    <div class="main-content">
                        <div class="container">
                            <h1>Sửa Chính Sách Hủy Vé</h1>

                            <form method="post" action="${pageContext.request.contextPath}/updatePolicy">
                                <input type="hidden" name="policyID" value="${policy.policyID}" />

                                <label for="policyName">Tên chính sách:</label>
                                <input type="text" name="policyName" value="${policy.policyName}" required />

                                <label for="hoursMin">Số giờ tối thiểu trước khi khởi hành:</label>
                                <input type="number" name="hoursMin" value="${policy.hoursBeforeDepartureMin}"
                                    required />

                                <label for="hoursMax">Số giờ tối đa (nếu có):</label>
                                <input type="number" name="hoursMax" value="${policy.hoursBeforeDepartureMax}" />

                                <label for="feePercentage">% Phí hủy:</label>
                                <input type="number" step="0.01" name="feePercentage" value="${policy.feePercentage}" />

                                <label for="fixedFeeAmount">Phí cố định (VNĐ):</label>
                                <input type="number" step="0.01" name="fixedFeeAmount"
                                    value="${policy.fixedFeeAmount}" />

                                <label for="isRefundable">Có hoàn tiền không?</label>
                                <select name="isRefundable">
                                    <option value="1" ${policy.refundable ? 'selected' : '' }>Có</option>
                                    <option value="0" ${!policy.refundable ? 'selected' : '' }>Không</option>
                                </select>

                                <label for="description">Mô tả:</label>
                                <textarea name="description" rows="3">${policy.description}</textarea>

                                <label for="isActive">Trạng thái:</label>
                                <select name="isActive">
                                    <option value="1" ${policy.active ? 'selected' : '' }>Đang áp dụng</option>
                                    <option value="0" ${!policy.active ? 'selected' : '' }>Không áp dụng</option>
                                </select>

                                <fmt:formatDate var="fromDate" value="${policy.effectiveFromDate}"
                                    pattern="yyyy-MM-dd" />
                                <fmt:formatDate var="toDate" value="${policy.effectiveToDate}" pattern="yyyy-MM-dd" />

                                <label for="effectiveFromDate">Áp dụng từ ngày:</label>
                                <input type="date" name="effectiveFromDate" value="${fromDate}" required />

                                <label for="effectiveToDate">Áp dụng đến ngày:</label>
                                <input type="date" name="effectiveToDate" value="${toDate}" />

                                <button type="submit">Cập nhật</button>
                            </form>

                            <a href="${pageContext.request.contextPath}/manageCancellationPolicies">← Quay lại danh
                                sách</a>
                        </div>
                    </div>
            </body>

            </html>