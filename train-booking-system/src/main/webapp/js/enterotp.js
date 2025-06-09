document.getElementById('otpForm').addEventListener('submit', function(e) {
    const otp = document.getElementById('otp').value;
    const otpError = document.getElementById('otpError');
    
    otpError.textContent = '';
    
    if (!/^\d{6}$/.test(otp)) {
        otpError.textContent = 'Mã OTP phải là 6 chữ số';
        e.preventDefault();
    }
});

// Resend OTP functionality
document.getElementById('resendOtp').addEventListener('click', function(e) {
    e.preventDefault();
    // Add your resend OTP logic here
    fetch('${pageContext.request.contextPath}/resendotp', {
        method: 'POST',
        credentials: 'same-origin'
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert('Mã OTP mới đã được gửi đến email của bạn');
        } else {
            alert('Có lỗi xảy ra. Vui lòng thử lại sau');
        }
    });
});