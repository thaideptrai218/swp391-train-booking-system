<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!--
  Edit User Page
  This page provides a form for administrators to edit user information.
-->

<!DOCTYPE html>
<html>

<head>
    <!-- Meta tags and Title -->
    <meta charset="UTF-8">
    <title>Sửa thông tin người dùng</title>

    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" integrity="sha512-9usAa10IRO0HhonpyAIVpjrylPvoDwiPUiKdWk5t3PyolY1cOd4DSE0Ga+ri4AuTroPR5aQvXU9xC6qOPnzFeg==" crossorigin="anonymous" referrerpolicy="no-referrer" />

    <!-- Internal Styles -->
    <style>
        /* Styling for form buttons container */
        .form-buttons {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        /* Styling for danger button */
        .btn-danger {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 5px;
            cursor: pointer;
        }

        /* Styling for error messages */
        .error-message {
            color: red;
            font-size: 0.9em;
        }

        /* Styling for fields with errors */
        .error-field {
            border: 1px solid red;
        }
    </style>
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
                <h1>Sửa thông tin người dùng</h1>
            </header>

            <!-- Form section for editing user -->
            <section class="user-table-container">
                <form action="${pageContext.request.contextPath}/admin/editUser" method="post" class="add-user-form">
                    
                    <!-- Hidden User ID -->
                    <input type="hidden" name="userId" value="${user.userID}">
                    
                    <!-- Full Name Input -->
                    <div class="form-group">
                        <label for="fullName">Họ và tên:</label>
                        <input type="text" id="fullName" name="fullName" value="${user.fullName}" required>
                        <span class="error-message" id="fullNameError"></span>
                    </div>
                    
                    <!-- Email Input -->
                    <div class="form-group">
                        <label for="email">Email:</label>
                        <input type="email" id="email" name="email" value="${user.email}" required>
                        <span class="error-message" id="emailError"></span>
                    </div>
                    
                    <!-- Phone Number Input -->
                    <div class="form-group">
                        <label for="phoneNumber">Số điện thoại:</label>
                        <input type="text" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}" required>
                        <span class="error-message" id="phoneNumberError"></span>
                    </div>
                    
                    <!-- Role Selection -->
                    <div class="form-group">
                        <label for="role">Role:</label>
                        <select id="role" name="role">
                            <option value="Admin" ${user.role == 'Admin' ? 'selected' : ''}>Admin</option>
                            <option value="Staff" ${user.role == 'Staff' ? 'selected' : ''}>Staff</option>
                            <option value="Customer" ${user.role == 'Customer' ? 'selected' : ''}>Customer</option>
                            <option value="Manager" ${user.role == 'Manager' ? 'selected' : ''}>Manager</option>
                        </select>
                    </div>
                    
                    <!-- Active Status Selection -->
                    <div class="form-group">
                        <label for="isActive">Trạng thái:</label>
                        <select id="isActive" name="isActive" onchange="toggleReasonField()">
                            <option value="true" ${user.isActive() ? 'selected' : ''}>Hoạt động</option>
                            <option value="false" ${!user.isActive() ? 'selected' : ''}>Bị khóa</option>
                        </select>
                    </div>
                    
                    <!-- Reason for Lock (hidden by default) -->
                    <div class="form-group" id="reason-group" style="display: none;">
                        <label for="reason">Lý do khóa:</label>
                        <input type="text" id="reason" name="reason">
                    </div>
                    
                    <!-- Form Buttons -->
                    <div class="form-buttons">
                        <input type="submit" value="Cập nhật thông tin" class="btn btn-primary">
                    </div>
                </form>
            </section>
        </main>
    </div>

    <!-- JavaScript for form functionality and validation -->
    <script>
        // Function to show/hide the reason field based on the active status
        function toggleReasonField() {
            var isActiveSelect = document.getElementById('isActive');
            var reasonGroup = document.getElementById('reason-group');
            if (isActiveSelect.value === 'false') {
                reasonGroup.style.display = 'block';
            } else {
                reasonGroup.style.display = 'none';
            }
        }
        
        // Run the function on page load to set the initial state
        toggleReasonField();

        // --- Form Validation ---

        // Get form and input elements
        const form = document.querySelector('.add-user-form');
        const fullName = document.getElementById('fullName');
        const email = document.getElementById('email');
        const phoneNumber = document.getElementById('phoneNumber');

        // Get error message elements
        const fullNameError = document.getElementById('fullNameError');
        const emailError = document.getElementById('emailError');
        const phoneNumberError = document.getElementById('phoneNumberError');

        // Add submit event listener to the form
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

            fullName.classList.remove('error-field');
            email.classList.remove('error-field');
            phoneNumber.classList.remove('error-field');
        }
    </script>
</body>

</html>
