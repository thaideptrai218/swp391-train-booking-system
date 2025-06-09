document.addEventListener('DOMContentLoaded', function() {
    const toggleCurrentPassword = document.getElementById('toggleCurrentPassword');
    const currentPasswordInput = document.getElementById('currentPassword');
    const toggleNewPassword = document.getElementById('toggleNewPassword');
    const newPasswordInput = document.getElementById('newPassword');
    const toggleConfirmNewPassword = document.getElementById('toggleConfirmNewPassword');
    const confirmNewPasswordInput = document.getElementById('confirmNewPassword');
    const emailOrPhoneInput = document.getElementById('emailOrPhone'); // New input

    const changePasswordForm = document.getElementById('changePasswordForm');
    const emailOrPhoneError = document.getElementById('emailOrPhoneError'); // New error span
    const currentPasswordError = document.getElementById('currentPasswordError');
    const newPasswordError = document.getElementById('newPasswordError');
    const confirmNewPasswordError = document.getElementById('confirmNewPasswordError');
    const loadingOverlay = document.getElementById('loadingOverlay');

    function setupPasswordToggle(toggleElement, passwordElement) {
        if (toggleElement && passwordElement) {
            console.log(`Setting up toggle for: ${passwordElement.id}`); // Debugging line
            toggleElement.addEventListener('click', function() {
                const type = passwordElement.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordElement.setAttribute('type', type);
                this.classList.toggle('fa-eye');
                this.classList.toggle('fa-eye-slash');
                console.log(`Toggled visibility for: ${passwordElement.id}`); // Debugging line
            });
        } else {
            console.warn(`Could not find toggle element or password element for setup. Toggle: ${toggleElement}, Password: ${passwordElement}`); // Debugging line
        }
    }

    setupPasswordToggle(toggleCurrentPassword, currentPasswordInput);
    setupPasswordToggle(toggleNewPassword, newPasswordInput);
    setupPasswordToggle(toggleConfirmNewPassword, confirmNewPasswordInput);

    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', function(event) {
            let isValid = true;

            // Clear previous errors
            emailOrPhoneError.textContent = ''; // Clear new error
            currentPasswordError.textContent = '';
            newPasswordError.textContent = '';
            confirmNewPasswordError.textContent = '';

            // Validate email or phone
            if (emailOrPhoneInput.value.trim() === '') {
                emailOrPhoneError.textContent = 'Vui lòng nhập email hoặc số điện thoại.';
                isValid = false;
            }

            // Validate current password
            if (currentPasswordInput.value.trim() === '') {
                currentPasswordError.textContent = 'Mật khẩu cũ không được để trống.';
                isValid = false;
            }

            // Validate new password
            if (newPasswordInput.value.trim() === '') {
                newPasswordError.textContent = 'Mật khẩu mới không được để trống.';
                isValid = false;
            } else if (newPasswordInput.value.length < 8) { // Changed from 6 to 8
                newPasswordError.textContent = 'Mật khẩu mới phải có ít nhất 8 ký tự.';
                isValid = false;
            }

            // Validate confirm new password
            if (confirmNewPasswordInput.value.trim() === '') {
                confirmNewPasswordError.textContent = 'Xác nhận mật khẩu mới không được để trống.';
                isValid = false;
            } else if (newPasswordInput.value !== confirmNewPasswordInput.value) {
                confirmNewPasswordError.textContent = 'Mật khẩu mới và xác nhận mật khẩu mới không khớp.';
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault(); // Prevent form submission if validation fails
            } else {
                // Show loading overlay on successful client-side validation
                loadingOverlay.style.display = 'flex';
            }
        });
    }

    // Hide loading overlay if there's an error message from the server
    const errorMessageFromServer = document.querySelector('.error-message-box');
    if (errorMessageFromServer) {
        loadingOverlay.style.display = 'none';
    }
    
    // Hide loading overlay if there's a success message from the server
    const successMessageFromServer = document.querySelector('.success-message-box');
    if (successMessageFromServer) {
        loadingOverlay.style.display = 'none';
    }
});
