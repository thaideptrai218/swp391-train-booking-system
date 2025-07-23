<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <%@ page import="vn.vnrailway.utils.DateUtils" %>
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8" />
                    <title>Thêm chính sách hủy vé</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/sidebar.css" />
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/manager/createPolicy.css" />
                    <link rel="stylesheet"
                        href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
                    <style>
                        .form-container {
                            max-width: 600px;
                            margin: 40px auto;
                            padding: 30px;
                            background-color: #ffffff;
                            border-radius: 12px;
                            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                        }

                        .form-container h1 {
                            text-align: center;
                            color: #007bff;
                            margin-bottom: 25px;
                        }

                        .policy-form .form-group {
                            margin-bottom: 15px;
                        }

                        .policy-form label {
                            display: block;
                            margin-bottom: 6px;
                            font-weight: 600;
                            color: #333;
                        }

                        .policy-form input,
                        .policy-form select,
                        .policy-form textarea {
                            width: 100%;
                            padding: 8px 12px;
                            border: 1px solid #ccc;
                            border-radius: 6px;
                            font-size: 1rem;
                        }

                        .policy-form textarea {
                            resize: vertical;
                        }

                        .form-actions {
                            display: flex;
                            justify-content: space-between;
                            margin-top: 25px;
                        }

                        .btn-save,
                        .btn-cancel {
                            padding: 10px 20px;
                            font-weight: bold;
                            border: none;
                            border-radius: 6px;
                            text-decoration: none;
                            display: inline-flex;
                            align-items: center;
                            gap: 8px;
                            transition: background 0.3s ease;
                        }

                        .btn-save {
                            background-color: #28a745;
                            color: white;
                        }

                        .btn-save:hover {
                            background-color: #218838;
                        }

                        .btn-cancel {
                            background-color: #6c757d;
                            color: white;
                        }

                        .btn-cancel:hover {
                            background-color: #5a6268;
                        }

                        .alert {
                            padding: 15px;
                            margin: 20px 0;
                            border: 1px solid transparent;
                            border-radius: 6px;
                        }

                        .alert-danger {
                            color: #721c24;
                            background-color: #f8d7da;
                            border-color: #f5c6cb;
                        }

                        .alert-success {
                            color: #155724;
                            background-color: #d4edda;
                            border-color: #c3e6cb;
                        }

                        .alert ul {
                            margin: 0;
                            padding-left: 20px;
                        }
                    </style>
                </head>

                <body>
                    <%@ include file="sidebar.jsp" %>

                        <div class="main-content">
                            <div class="form-container">
                                <h1><i class="fas fa-plus-circle"></i> Thêm chính sách hủy vé</h1>
                                <c:if test="${not empty validationErrors}">
                                    <div class="alert alert-danger">
                                        <strong><i class="fas fa-exclamation-triangle"></i> Có lỗi xảy ra:</strong>
                                        <ul>
                                            <c:forEach var="error" items="${validationErrors}">
                                                <li>${error}</li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:if>

                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger">
                                        <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                                    </div>
                                </c:if>
                                <form method="post" action="${pageContext.request.contextPath}/createPolicy"
                                    class="policy-form">
                                    <div class="form-group">
                                        <label for="policyName">Tên chính sách:</label>
                                        <input type="text" id="policyName" name="policyName" value="${policyName}" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="hoursMin">Giờ tối thiểu:</label>
                                        <input type="number" id="hoursMin" name="hoursMin" value="${hoursMin}" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="hoursMax">Giờ tối đa:</label>
                                        <input type="number" id="hoursMax" name="hoursMax" value="${hoursMax}">
                                    </div>

                                    <div class="form-group">
                                        <label for="feePercentage">Phí %:</label>
                                        <input type="number" step="0.01" id="feePercentage" name="feePercentage" value="${feePercentage}">
                                    </div>

                                    <div class="form-group">
                                        <label for="fixedFeeAmount">Phí cố định:</label>
                                        <input type="number" step="0.01" id="fixedFeeAmount" name="fixedFeeAmount" value="${fixedFeeAmount}">
                                    </div>

                                    <div class="form-group">
                                        <label for="isRefundable">Có hoàn tiền không?</label>
                                        <select id="isRefundable" name="isRefundable">
                                            <option value="1" <c:if test="${isRefundable == 1}">selected</c:if>>Có</option>
                                            <option value="0" <c:if test="${isRefundable == 0}">selected</c:if>>Không</option>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="description">Mô tả:</label>
                                        <textarea id="description" name="description" rows="3">${description}</textarea>
                                    </div>

                                    <div class="form-group">
                                        <label for="isActive">Trạng thái:</label>
                                        <select id="isActive" name="isActive">
                                            <option value="1" <c:if test="${isActive == 1}">selected</c:if>>Đang áp dụng</option>
                                            <option value="0" <c:if test="${isActive == 0}">selected</c:if>>Không áp dụng</option>
                                        </select>
                                    </div>

                                    <div class="form-group">
                                        <label for="effectiveFromDate">Áp dụng từ ngày:</label>
                                        <input type="date" id="effectiveFromDate" name="effectiveFromDate" value="${effectiveFromDate}" required>
                                    </div>

                                    <div class="form-group">
                                        <label for="effectiveToDate">Áp dụng đến ngày:</label>
                                        <input type="date" id="effectiveToDate" name="effectiveToDate" value="${effectiveToDate}">
                                    </div>

                                    <div class="form-actions">
                                        <button type="submit" class="btn-save">
                                            <i class="fas fa-save"></i> Lưu chính sách
                                        </button>
                                        <a href="${pageContext.request.contextPath}/manageCancellationPolicies"
                                            class="btn-cancel">
                                            <i class="fas fa-arrow-left"></i> Quay lại danh sách
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                </body>

                </html>