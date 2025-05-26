<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Book Train - Landing Page</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/global.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/landing-page.css">
</head>
<body>
    <div class="main">
        <div class="div"> <%-- This corresponds to .main .div in CSS --%>

            <%-- Overlap 10: Top Background and Hero --%>
            <div class="overlap-10">
                <img class="top-BG" alt="Top background" src="${pageContext.request.contextPath}/assets/icons/landing/top_BG.png" />
                <div class="hero">
                    <img class="logo-2" alt="Logo" src="${pageContext.request.contextPath}/assets/icons/landing/logo.png" />
                    <div class="top-nav-2">
                        <%-- Navigation items from original JSP, adapt classes as needed --%>
                        <%-- Example: <div class="text-wrapper-nav">Tìm vé</div> --%>
                    </div>
                    <div class="details">
                        <div class="text-7">
                            <span class="text-wrapper-23">Đến với chúng tôi</span>
                            <span class="text-wrapper-24"><br />Trải nghiệm dịch vụ chất lượng</span>
                        </div>
                        <div class="detail-button">
                            <div class="group-2">
                                <div class="overlap-group-7">
                                    <div class="text-wrapper-21">Tìm hiểu thêm</div>
                                </div>
                            </div>
                            <div class="overlap-wrapper">
                                <div class="overlap-group-7"> <%-- Assuming similar button style --%>
                                    <div class="text-wrapper-22">0983868888</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- Search Route Section --%>
            <div class="search-route">
                <div class="overlap-9">
                    <div class="train-route">
                        <div class="text-6">Hành trình tàu</div>
                        <div class="stations-detail">
                            <div class="text-5">Danh sách tuyến đường sắt và Ga</div>
                            <div class="list">
                                <div class="overlap-group-6">
                                    <%-- Repeat for each station --%>
                                    <div class="list-5"> <%-- list-2, list-3, list-4 for others --%>
                                        <img class="image-24" alt="Station image" src="path/to/station-image.png" /> <%-- Placeholder --%>
                                        <p class="l-o-cai-a-ch-ph-ng">
                                            <span class="text-wrapper-19">Lào Cai</span>
                                            <span class="text-wrapper-20"><br />Địa chỉ: Phường Phố Mới,thị xã Lào Cai, tỉnh Lào Cai<br />Điện thoại: 020-830.093</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="train">
                        <img class="image-23" alt="Train map" src="path/to/train-map.png" /> <%-- Placeholder --%>
                        <div class="text-3">Bản đồ hành trình</div>
                        <div class="text-4">Tìm kiếm:</div>
                        <div class="rectangle-4"></div> <%-- Search input would go here --%>
                    </div>
                </div>
            </div>

            <%-- Title Descriptions Section --%>
            <div class="title-descriptions">
                <div class="text-wrapper-18">Đến với chúng tôi</div>
                <p class="text-wrapper-17">Để khám phá hết vẻ đẹp của dải đất hình chữ S và trải nghiệm cuộc sống thi vị, có lẽ không gì tuyệt vời hơn một chuyến tàu dọc theo chiều dài đất nước. Khi đoàn tàu lăn bánh cũng là lúc hành khách được thư giãn ngắm nhìn Việt Nam với khung cảnh thiên nhiên và cuộc sống thường ngày bình dị qua ô cửa con tàu.</p>
                <img class="img-3" alt="Descriptive image" src="path/to/descriptive-image.png" /> <%-- Placeholder --%>
            </div>

            <%-- Hot Locations Section --%>
            <div class="hot-locations">
                <div class="title">Địa điểm nổi bật</div>
                <div class="locations">
                    <div class="hai-phong">
                        <img class="image-22" alt="Hải Phòng" src="path/to/haiphong.png" /> <%-- Placeholder --%>
                        <div class="text-wrapper-16">Hải Phòng</div>
                    </div>
                    <div class="TPHCM">
                        <img class="image-21" alt="TPHCM" src="path/to/tphcm.png" /> <%-- Placeholder --%>
                        <div class="text-wrapper-15">Thành phố Hồ Chí Minh</div>
                    </div>
                    <div class="hue">
                        <img class="image-20" alt="Huế" src="path/to/hue.png" /> <%-- Placeholder --%>
                        <div class="text-wrapper-14">Huế</div>
                    </div>
                    <div class="HN">
                        <img class="image-19" alt="Hà Nội" src="path/to/hanoi.png" /> <%-- Placeholder --%>
                        <div class="text-wrapper-13">Hà Nội</div>
                    </div>
                </div>
                <button class="overlap-group-wrapper">
                    <div class="overlap-group-5">
                        <div class="text-wrapper-12">Tìm hiểu thêm</div>
                    </div>
                </button>
                <div class="next-button">
                     <%-- Next/Prev buttons for slider --%>
                </div>
            </div>

            <%-- Overlap: Booking and Footer sections --%>
            <div class="overlap">
                <div class="booking">
                    <div class="overlap-8">
                        <div class="text-wrapper-11">Đặt vé ngay tại đây</div>
                        <p class="t-n-h-ng-tr-i-nghi-m">
                            <span class="span">Tận hưởng trải nghiệm dịch vụ tốt nhất và đến nơi mà bạn mơ ước</span>
                            <span class="text-wrapper-20"><br />Liên hệ ngay: 0983868888</span>
                        </p>
                        <button class="button">
                            <div class="div-wrapper"><div class="text-wrapper-10">Đặt vé</div></div>
                        </button>
                    </div>
                </div>

                <div class="cooperationis">
                     <div class="overlap-4">
                        <div class="BG"></div>
                        <div class="line-2"> <%-- Hỗ trợ / Đối tác thanh toán --%>
                            <div class="detail-4"> <%-- Hỗ trợ --%>
                                <div class="text-wrapper-9">Hỗ trợ</div>
                                <p class="text-2">
                                    Hướng dẫn thanh toán<br />
                                    Quy chế Vexere.com<br />
                                    Chính sách bảo mật thông tin<br />
                                    Chính sách bảo mật thanh toán<br />
                                    Chính sách và quy trình giải quyết tranh chấp, khiếu nại<br />
                                    Câu hỏi thường gặp<br />
                                    Tra cứu đơn hàng
                                </p>
                            </div>
                            <div class="detail-3"> <%-- Đối tác thanh toán --%>
                                 <div class="text-wrapper-8">Đối tác thanh toán</div>
                                 <div class="company-logo">
                                     <%-- Placeholder for company logos --%>
                                 </div>
                            </div>
                        </div>
                        <div class="line"> <%-- Chứng nhận / Tải ứng dụng --%>
                            <div class="overlap-5">
                                <div class="detail-2"> <%-- Chứng nhận --%>
                                    <div class="text-wrapper-7">Chứng nhận</div>
                                     <%-- Placeholder for certification images --%>
                                </div>
                                <div class="detail"> <%-- Tải ứng dụng --%>
                                    <div class="text-wrapper-6">Tải ứng dụng Vetaure</div>
                                     <%-- Placeholder for app store images --%>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="footer">
                    <div class="overlap-group">
                        <div class="footer-down">
                            <div class="overlap-2">
                                <div class="rectangle"></div>
                                <div class="down">
                                    <div class="rectangle-2"></div>
                                    <div class="text-wrapper">2025. Copyright and All rights reserved.</div>
                                </div>
                                <div class="left-footer">
                                    <div class="overlap-group-2">
                                        <img class="logo" alt="Footer Logo" src="path/to/footer-logo.png" /> <%-- Placeholder --%>
                                        <p class="text">Sự thỏa mãn của bạn là niềm vui của chúng tôi</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="footer-up">
                            <div class="overlap-3">
                                <div class="rectangle-3"></div>
                                <div class="right-footer">
                                    <div class="img">
                                        <%-- Social media icons placeholders --%>
                                    </div>
                                </div>
                                <p class="p">Kết nối với chúng tôi thông qua mạng xã hội</p>
                            </div>
                        </div>
                        <div class="help">
                            <div class="upper-wrapper">
                                <div class="upper">
                                    <div class="upper-detail">
                                        <div class="text-wrapper-4">Hỗ trợ</div>
                                        <div class="text-wrapper-5">Tin tức</div>
                                        <div class="text-wrapper-2">FAQ</div>
                                        <div class="text-wrapper-3">Liên hệ</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
