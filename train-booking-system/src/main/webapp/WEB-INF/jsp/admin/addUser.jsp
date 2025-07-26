<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--
  Add New User Page
  This page provides a form for administrators to add new users to the system.
-->

<!DOCTYPE html>
<html>

<head>
    <!-- Meta tags and Title -->
    <meta charset="UTF-8">
    <title>Thêm người dùng mới</title>

    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
</head>

<body>
    <!-- Main container for the dashboard -->
    <div class="dashboard-container">
        
        <!-- Include admin layout (sidebar, etc.) -->
        <jsp:include page="adminLayout.jsp" />

        <!-- Main content area -->
        <main class="main-content">
            
            <!-- Header section -->
            <header class="header">
                <h1>Thêm người dùng mới</h1>
            </header>

            <!-- Form section for adding a new user -->
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/addUser" method="post" class="add-user-form">
                    
                    <!-- Full Name Input -->
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" required>
                        <span class="error-message" id="fullNameError"></span>
                    </div>
                    
                    <!-- Email Input -->
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" required>
                        <span class="error-message" id="emailError"></span>
                    </div>
                    
                    <!-- Phone Number Input -->
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" required>
                        <span class="error-message" id="phoneNumberError"></span>
                    </div>
                    
                    <!-- Password Input -->
                    <div class="form-group">
                        <label for="password">Mật khẩu:</label>
                        <input type="password" id="password" name="password" required>
                        <span class="error-message" id="passwordError"></span>
                    </div>
                    
                    <!-- Role Selection -->
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin">Admin</option>
                            <option value="Staff">Staff</option>
                            <option value="Customer">Customer</option>
                            <option value="Manager">Manager</option>
                        </select>
                    </div>
                    
                    <!-- Submit Button -->
                    <input type="submit" value="Thêm người dùng" class="btn btn-primary">
                </form>
            </section>
        </main>
    </div>

    <!-- Internal Styles for error messages -->
    <style>
        .error-message {
            color: red;
            font-size: 0.9em;
        }
        .error-field {
            border: 1px solid red;
        }
    </style>

    <!-- JavaScript for form validation -->
    <script>
        // Get form and input elements
        const form = document.querySelector('.add-user-form');
        const fullName = document.getElementById('fullName');
        const email = document.getElementById('email');
        const phoneNumber = document.getElementById('phoneNumber');
        const password = document.getElementById('password');

        // Get error message elements
        const fullNameError = document.getElementById('fullNameError');
        const emailError = document.getElementById('emailError');
        const phoneNumberError = document.getElementById('phoneNumberError');
        const passwordError = document.getElementById('passwordError');

        // Add submit event listener to the form
        form.addEventListener('submit', function(event) {
            let isValid = true;

            // Clear previous errors before new validation
            clearErrors();

            // --- Validation Rules ---

            // Full Name Validation: Only letters and spaces
            const fullNameRegex = /^[a-zA-Z\sÀ-ỹ]+$/;
            if (!fullNameRegex.test(fullName.value)) {
                fullNameError.textContent = 'Họ và tên không được chứa số hoặc ký tự đặc biệt.';
                fullName.classList.add('error-field');
                isValid = false;
            }

            // Email Validation: Standard email format
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email.value)) {
                emailError.textContent = 'Định dạng email không hợp lệ.';
                email.classList.add('error-field');
                isValid = false;
            }

            // Phone Number Validation: 10 or 11 digits, starting with 0
            const phoneRegex = /^0\d{9,10}$/;
            if (!phoneRegex.test(phoneNumber.value)) {
                phoneNumberError.textContent = 'Số điện thoại phải có 10 hoặc 11 chữ số và bắt đầu bằng 0.';
                phoneNumber.classList.add('error-field');
                isValid = false;
            }

            // Password Validation: Minimum 8 characters
            if (password.value.length < 8) {
                passwordError.textContent = 'Mật khẩu phải có ít nhất 8 ký tự.';
                password.classList.add('error-field');
                isValid = false;
            }

            // Prevent form submission if validation fails
            if (!isValid) {
                event.preventDefault();
            }
        });

        // Function to clear all error messages and styles
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
