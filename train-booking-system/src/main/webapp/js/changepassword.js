document.addEventListener('DOMContentLoaded', function() {
    const toggleOldPassword = document.getElementById('toggleOldPassword');
    const oldPassword = document.getElementById('oldPassword');
    const toggleNewPassword = document.getElementById('toggleNewPassword');
    const newPassword = document.getElementById('newPassword');
    const toggleConfirmPassword = document.getElementById('toggleConfirmPassword');
    const confirmPassword = document.getElementById('confirmPassword');

    function setupPasswordToggle(toggleElement, passwordElement) {
        if (toggleElement && passwordElement) {
            toggleElement.addEventListener('click', function() {
                const type = passwordElement.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordElement.setAttribute('type', type);
                this.classList.toggle('fa-eye');
                this.classList.toggle('fa-eye-slash');
            });
        }
    }

    setupPasswordToggle(toggleOldPassword, oldPassword);
    setupPasswordToggle(toggleNewPassword, newPassword);
    setupPasswordToggle(toggleConfirmPassword, confirmPassword);
});
