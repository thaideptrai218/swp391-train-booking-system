<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<hj th
<head>
    <meta charset="UTF-8">
    <title>Sửa thông tin người dùng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        .form-buttons {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .btn-danger {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
        }
        .error-message {
            color: red;
            font-size: 0.9em;
        }
        .error-field {
            border: 1px solid red;
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Sửa thông tin người dùng</h1>
            </header>
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/editUser" method="post" class="add-user-form">
                    <input type="hidden" name="userId" value="${user.userID}">
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" value="${user.fullName}" required>
                        <span class="error-message" id="fullNameError"></span>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" value="${user.email}" required>
                        <span class="error-message" id="emailError"></span>
                    </div>
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}" required>
                        <span class="error-message" id="phoneNumberError"></span>
                    </div>
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin" ${user.role == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Staff" ${user.role == 'Staff' ? 'selected' : ''}>Staff</option>
                            <option value="Customer" ${user.role == 'Customer' ? 'selected' : ''}>Customer</option>
                            <option value="Manager" ${user.role == 'Manager' ? 'selected' : ''}>Manager</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="isActive">Trạng thái:</label>
                        <select id="isActive" name="isActive" onchange="toggleReasonField()">
                            <option value="true" ${user.isActive() ? 'selected' : ''}>Hoạt động</option>
                            <option value="false" ${!user.isActive() ? 'selected' : ''}>Bị khóa</option>
                        </select>
                    </div>
                    <div class="form-group" id="reason-group" style="display: none;">
                        <label for="reason">Lý do khóa:</label>
                        <input type="text" id="reason" name="reason">
                    </div>
                    <div class="form-buttons">
                        <input type="submit" value="Cập nhật thông tin" class="btn btn-primary">
                    </div>
                </form>
            </section>
        </main>
    </div>
    <script>
        function toggleReasonField() {
            var isActiveSelect = document.getElementById('isActive');
            var reasonGroup = document.getElementById('reason-group');
            if (isActiveSelect.value === 'false') {
                reasonGroup.style.display = 'block';
            } else {
                reasonGroup.style.display = 'none';
            }
        }
        // Run on page load
        toggleReasonField();

        const form = document.querySelector('.add-user-form');
        const fullName = document.getElementById('fullName');
        const email = document.getElementById('email');
        const phoneNumber = document.getElementById('phoneNumber');

        const fullNameError = document.getElementById('fullNameError');
        const emailError = document.getElementById('emailError');
        const phoneNumberError = document.getElementById('phoneNumberError');

        form.addEventListener('submit', function(event) {
            let isValid = true;

            // Clear previous errors
            clearErrors();

            // Full Name Validation
            const fullNameRegex = /^[a-zA-Z\sÀ-ỹ]+$/;
            if (!fullNameRegex.test(fullName.value)) {
                fullNameError.textContent = 'Họ và tên không được chứa số hoặc ký tự đặc biệt.';
                fullName.classList.add('error-field');
                isValid = false;
            }

            // Email Validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email.value)) {
                emailError.textContent = 'Định dạng email không hợp lệ.';
                email.classList.add('error-field');
                isValid = false;
            }

            // Phone Number Validation
            const phoneRegex = /^0\d{9,10}$/;
            if (!phoneRegex.test(phoneNumber.value)) {
                phoneNumberError.textContent = 'Số điện thoại phải có 10 hoặc 11 chữ số và bắt đầu bằng 0.';
                phoneNumber.classList.add('error-field');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault();
            }
        });

        function clearErrors() {
            fullNameError.textContent = '';
            emailError.textContent = '';
            phoneNumberError.textContent = '';

            fullName.classList.remove('error-field');
            email.classList.remove('error-field');
            phoneNumber.classList.remove('error-field');
        }
    </script>
</body>
</html>
