function togglePassword(inputId, icon) {
    const input = document.getElementById(inputId);
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('fa-eye-slash', 'fa-eye');
    } else {
        input.type = 'password';
        icon.classList.replace('fa-eye', 'fa-eye-slash');
    }
}

document.getElementById('newPasswordForm').addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const newPasswordError = document.getElementById('newPasswordError');
    const confirmPasswordError = document.getElementById('confirmPasswordError');
    
    let isValid = true;
    newPasswordError.textContent = '';
    confirmPasswordError.textContent = '';
    
    if (newPassword.length < 8) {
        newPasswordError.textContent = 'Mật khẩu phải có ít nhất 8 ký tự';
        isValid = false;
    }
    
    if (newPassword !== confirmPassword) {
        confirmPasswordError.textContent = 'Mật khẩu xác nhận không khớp';
        isValid = false;
    }
    
    if (!isValid) {
        e.preventDefault();
    }
});