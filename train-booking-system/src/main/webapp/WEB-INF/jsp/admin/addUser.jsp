<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm người dùng mới</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>
<body>
    <div class="dashboard-container">
        <jsp:include page="adminLayout.jsp" />
        <main class="main-content">
            <header class="header">
                <h1>Thêm người dùng mới</h1>
            </header>
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/addUser" method="post" class="add-user-form">
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" required>
                        <span class="error-message" id="fullNameError"></span>
                    </div>
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" required>
                        <span class="error-message" id="emailError"></span>
                    </div>
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" required>
                        <span class="error-message" id="phoneNumberError"></span>
                    </div>
                    <div class="form-group">
                        <label for="password">Mật khẩu:</label>
                        <input type="password" id="password" name="password" required>
                        <span class="error-message" id="passwordError"></span>
                    </div>
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin">Admin</option>
                            <option value="Staff">Staff</option>
                            <option value="Customer">Customer</option>
                            <option value="Manager">Manager</option>
                        </select>
                    </div>
                    <input type="submit" value="Thêm người dùng" class="btn btn-primary">
                </form>
            </section>
        </main>
    </div>
    <style>
        .error-message {
            color: red;
            font-size: 0.9em;
        }
        .error-field {
            border: 1px solid red;
        }
    </style>
    <script>
        const form = document.querySelector('.add-user-form');
        const fullName = document.getElementById('fullName');
        const email = document.getElementById('email');
        const phoneNumber = document.getElementById('phoneNumber');
        const password = document.getElementById('password');

        const fullNameError = document.getElementById('fullNameError');
        const emailError = document.getElementById('emailError');
        const phoneNumberError = document.getElementById('phoneNumberError');
        const passwordError = document.getElementById('passwordError');

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

            // Password Validation
            if (password.value.length < 8) {
                passwordError.textContent = 'Mật khẩu phải có ít nhất 8 ký tự.';
                password.classList.add('error-field');
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
            passwordError.textContent = '';

            fullName.classList.remove('error-field');
            email.classList.remove('error-field');
            phoneNumber.classList.remove('error-field');
            password.classList.remove('error-field');
        }
    </script>
</body>
</html>
