document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('togglePassword');
    const passwordField = document.getElementById('password');
    const emailField = document.getElementById('email');
    const phoneField = document.getElementById('phone');
    const rememberMeCheckbox = document.getElementById('rememberMe');
    const loginForm = document.querySelector('.login-form');

    // Password toggle functionality
    if (togglePassword && passwordField) {
        togglePassword.addEventListener('click', function() {
            const type = passwordField.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordField.setAttribute('type', type);
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });
    }

    // Remember Me functionality
    if (rememberMeCheckbox && loginForm) {
        // Load saved credentials on page load
        const savedEmail = localStorage.getItem('rememberedEmail');
        const savedPhone = localStorage.getItem('rememberedPhone');
        const savedPassword = localStorage.getItem('rememberedPassword');
        const rememberMeChecked = localStorage.getItem('rememberMeChecked');

        if (rememberMeChecked === 'true') {
            rememberMeCheckbox.checked = true;
            if (emailField && savedEmail) {
                emailField.value = savedEmail;
            }
            if (phoneField && savedPhone) {
                phoneField.value = savedPhone;
            }
            if (passwordField && savedPassword) {
                passwordField.value = savedPassword;
            }
        }

        // Save/clear credentials on form submission
        loginForm.addEventListener('submit', function() {
            if (rememberMeCheckbox.checked) {
                localStorage.setItem('rememberedEmail', emailField ? emailField.value : '');
                localStorage.setItem('rememberedPhone', phoneField ? phoneField.value : '');
                localStorage.setItem('rememberedPassword', passwordField ? passwordField.value : '');
                localStorage.setItem('rememberMeChecked', 'true');
            } else {
                localStorage.removeItem('rememberedEmail');
                localStorage.removeItem('rememberedPhone');
                localStorage.removeItem('rememberedPassword');
                localStorage.setItem('rememberMeChecked', 'false');
            }
        });
    }
});
