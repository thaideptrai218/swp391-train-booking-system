import React from "react";
import "./style.css";

export const Login = () => {
  return (
    <div className="login">
      <div className="overlap-wrapper">
        <div className="overlap">

          <div className="overlap-group">
            <div className="group">
              {/* Icon Home */}
              <img src="icons/fi-home.svg" alt="Home Icon" className="fi-home" />

              <div className="text-wrapper">Đăng Nhập</div>
              <div className="div">Email</div>
              <div className="text-wrapper-2">Nhập email</div>
              <div className="rectangle" />

              <div className="text-wrapper-3">Mật khẩu</div>
              <div className="text-wrapper-4">Nhập mật khẩu</div>
              <div className="rectangle-2" />

              <div className="div-wrapper">
                <div className="text-wrapper-5">Đăng nhập</div>
              </div>

              <div className="text-wrapper-6">Đăng ký</div>
              <div className="text-wrapper-7">Tạo tài khoản</div>

              <p className="p">Bạn đã có tài khoản?</p>

              <div className="text-wrapper-8">Quên mật khẩu?</div>
              <div className="text-wrapper-9">Nhấn vào đây</div>
              <div className="text-wrapper-10">Tài khoản Google</div>

              {/* Icon Eye */}
              <img src="icons/fi-eye.svg" alt="Eye Icon" className="fi-eye" />
            </div>
          </div>

          <div className="hero" />
        </div>
      </div>
    </div>
  );
};

export default Login;
