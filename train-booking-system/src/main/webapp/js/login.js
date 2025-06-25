document.addEventListener('DOMContentLoaded', function() {
    const togglePassword = document.getElementById('togglePassword');
    const passwordField = document.getElementById('password');
    const identifierField = document.getElementById('identifier'); // New combined field
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
        const savedIdentifier = localStorage.getItem('rememberedIdentifier');
        const savedPassword = localStorage.getItem('rememberedPassword');
        const rememberMeChecked = localStorage.getItem('rememberMeChecked');

        if (rememberMeChecked === 'true') {
            rememberMeCheckbox.checked = true;
            if (identifierField && savedIdentifier) {
                identifierField.value = savedIdentifier;
            }
            if (passwordField && savedPassword) {
                passwordField.value = savedPassword;
            }
        }

        // Save/clear credentials on form submission
        loginForm.addEventListener('submit', function() {
            if (rememberMeCheckbox.checked) {
                localStorage.setItem('rememberedIdentifier', identifierField ? identifierField.value : '');
                localStorage.setItem('rememberedPassword', passwordField ? passwordField.value : '');
                localStorage.setItem('rememberMeChecked', 'true');
            } else {
                localStorage.removeItem('rememberedIdentifier');
                localStorage.removeItem('rememberedPassword');
                localStorage.setItem('rememberMeChecked', 'false');
            }
        });
    }
});
