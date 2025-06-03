document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');

    const fullNameInput = document.getElementById('FullName');
    const phoneInput = document.getElementById('phone');
    const emailInput = document.getElementById('email');
    const idCardNumberInput = document.getElementById('idCardNumber');

    const fullNameError = document.getElementById('fullNameError');
    const phoneError = document.getElementById('phoneError');
    const emailError = document.getElementById('emailError');
    const passwordError = document.getElementById('passwordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    const idCardNumberError = document.getElementById('idCardNumberError');

    const registerForm = document.querySelector('.register-form');

    // Helper function to show error
    function showError(inputElement, messageElement, message) {
        inputElement.classList.add('input-error');
        messageElement.textContent = message;
        // Trigger reflow to restart animation
        inputElement.style.animation = 'none';
        void inputElement.offsetWidth; // Trigger reflow
        inputElement.style.animation = null;
    }

    // Helper function to clear error
    function clearError(inputElement, messageElement) {
        inputElement.classList.remove('input-error');
        messageElement.textContent = '';
    }

    // Validation functions
    function validatePassword() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        let isValid = true;

        if (password.length < 8) {
            showError(passwordInput, passwordError, 'Mật khẩu phải có ít nhất 8 ký tự.');
            isValid = false;
        } else {
            clearError(passwordInput, passwordError);
        }

        if (password !== confirmPassword) {
            showError(confirmPasswordInput, confirmPasswordError, 'Mật khẩu xác nhận không khớp.');
            isValid = false;
        } else {
            clearError(confirmPasswordInput, confirmPasswordError);
        }
        return isValid;
    }

    function validateEmail() {
        const email = emailInput.value;
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        if (!emailRegex.test(email)) {
            showError(emailInput, emailError, 'Email không đúng định dạng.');
            return false;
        } else {
            clearError(emailInput, emailError);
            return true;
        }
    }

    function validatePhone() {
        const phone = phoneInput.value;
        if (!phone.startsWith('0') || !/^\d+$/.test(phone)) {
            showError(phoneInput, phoneError, 'Số điện thoại phải bắt đầu bằng 0 và chỉ chứa các chữ số.');
            return false;
        } else {
            clearError(phoneInput, phoneError);
            return true;
        }
    }

    function validateIdCardNumber() {
        const idCardNumber = idCardNumberInput.value;
        if (idCardNumber.length !== 12 || !/^\d+$/.test(idCardNumber)) {
            showError(idCardNumberInput, idCardNumberError, 'CCCD phải có đúng 12 chữ số.');
            return false;
        } else {
            clearError(idCardNumberInput, idCardNumberError);
            return true;
        }
    }

    // Event Listeners for real-time validation
    passwordInput.addEventListener('input', validatePassword);
    confirmPasswordInput.addEventListener('input', validatePassword);
    emailInput.addEventListener('input', validateEmail);
    phoneInput.addEventListener('input', validatePhone);
    idCardNumberInput.addEventListener('input', validateIdCardNumber);

    // Form submission validation
    registerForm.addEventListener('submit', function(event) {
        const isPasswordValid = validatePassword();
        const isEmailValid = validateEmail();
        const isPhoneValid = validatePhone();
        const isIdCardNumberValid = validateIdCardNumber();

        if (!isPasswordValid || !isEmailValid || !isPhoneValid || !isIdCardNumberValid) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });

    // Password visibility toggle
    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });
    }

    if (toggleConfirmPassword && confirmPasswordInput) {
        toggleConfirmPassword.addEventListener('click', function() {
            const type = confirmPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            confirmPasswordInput.setAttribute('type', type);
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });
    }
});
