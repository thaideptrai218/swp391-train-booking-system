/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

document.addEventListener("DOMContentLoaded", function () {
    const registerButton = document.querySelector(".wrapper-2");
    
    registerButton.addEventListener("click", function () {
        const name = document.querySelector(".group span").innerText.trim();
        const phone = document.querySelector(".wrapper span").innerText.trim();
        const email = document.querySelector(".box span").innerText.trim();
        const password = document.querySelector(".box-2 span").innerText.trim();
        const confirmPassword = document.querySelector(".section-2 span").innerText.trim();

        if (!name || !phone || !email || !password || !confirmPassword) {
            alert("Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!validateEmail(email)) {
            alert("Email không hợp lệ.");
            return;
        }

        if (password !== confirmPassword) {
            alert("Mật khẩu xác nhận không khớp.");
            return;
        }

        // Giả lập đăng ký thành công
        alert("Đăng ký thành công!");

        // Nếu bạn muốn gửi dữ liệu đến servlet:
        /*
        fetch('RegisterServlet', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, phone, email, password })
        }).then(res => {
            if (res.ok) {
                alert("Đăng ký thành công!");
                window.location.href = 'login.jsp';
            } else {
                alert("Có lỗi xảy ra khi đăng ký.");
            }
        });
        */
    });

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email.toLowerCase());
    }
});

