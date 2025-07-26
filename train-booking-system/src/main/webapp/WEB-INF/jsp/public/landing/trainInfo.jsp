<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chọn Loại Tàu Phù Hợp - Vetaure</title>
    
    <!-- Meta Tags for SEO -->
    <meta name="description" content="Thông tin chi tiết và đầy đủ về các loại tàu hỏa tại Việt Nam. Hướng dẫn chọn tàu SE, TN, tàu 5 sao và tàu địa phương để có chuyến đi tốt nhất.">
    <meta name="keywords" content="tàu hỏa, đường sắt Việt Nam, vé tàu, chọn tàu, tàu SE, tàu TN, tàu 5 sao, du lịch bằng tàu, vetaure">
    <meta name="author" content="Vetaure">
    
    <!-- Favicon -->
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico" type="image/x-icon">
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    
    <!-- Font Awesome for Icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <!-- Custom Stylesheet -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/landing-page.css">

    <!-- Internal Styles for this page -->
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f7f6;
            color: #333;
            line-height: 1.6;
        }

        .main-content {
            max-width: 1200px;
            margin: 150px auto;
            padding: 2rem;
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }

        .page-header {
            text-align: center;
            margin-bottom: 2.5rem;
            border-bottom: 2px solid #0082c4;
            padding-bottom: 1rem;
        }

        .page-header h1 {
            font-size: 2.8rem;
            color: #005a8c;
            font-weight: 700;
        }

        .page-header p {
            font-size: 1.1rem;
            color: #666;
            max-width: 800px;
            margin: 0.5rem auto 0;
        }

        .train-section {
            margin-bottom: 3rem;
            padding: 2rem;
            border-radius: 8px;
            background: #f9f9f9;
            border-left: 5px solid #0082c4;
        }

        .train-section h2 {
            font-size: 2.2rem;
            color: #005a8c;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
        }
        
        .train-section h2 i {
            margin-right: 15px;
            color: #0082c4;
        }

        .train-section p, .train-section li {
            font-size: 1rem;
            margin-bottom: 0.75rem;
        }

        .train-section ul {
            list-style: none;
            padding-left: 0;
        }

        .train-section li::before {
            content: '✔';
            color: #28a745;
            font-weight: bold;
            display: inline-block;
            width: 1.5em;
            margin-left: -1.5em;
        }

        .train-image {
            width: 100%;
            max-width: 600px;
            margin: 1.5rem auto;
            display: block;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        .highlight-box {
            background-color: #eaf6ff;
            border: 1px solid #bde0fe;
            padding: 1.5rem;
            margin-top: 1.5rem;
            border-radius: 8px;
        }

        .highlight-box strong {
            color: #005a8c;
        }

        .station-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 2rem;
        }

        .station-table th, .station-table td {
            border: 1px solid #ddd;
            padding: 12px 15px;
            text-align: left;
        }

        .station-table th {
            background-color: #0082c4;
            color: white;
            font-weight: 500;
        }

        .station-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .station-table tr:hover {
            background-color: #f1f1f1;
        }
    </style>
</head>

<body>
    <!-- Header -->
    <jsp:include page="../../common/header.jsp" />

    <!-- Main Content -->
    <main class="main-content">
        <header class="page-header">
            <h1>Thông Tin Các Loại Tàu Hỏa Việt Nam</h1>
            <p>Tìm hiểu chi tiết về các loại tàu để lựa chọn cho mình một hành trình phù hợp và thoải mái nhất.</p>
        </header>

        <!-- Section for SE Trains -->
        <section class="train-section">
            <h2><i class="fas fa-rocket"></i>Tàu SE (Super Express) – Tàu Nhanh Thống Nhất</h2>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image2.png" alt="Tàu SE hiện đại" class="train-image">
            <ul>
                <li><strong>Lộ trình:</strong> Hà Nội - TP.HCM (tuyến đường sắt Bắc-Nam).</li>
                <li><strong>Tốc độ:</strong> Nhanh, dừng ở ít ga hơn so với các loại tàu khác.</li>
                <li><strong>Tiện nghi:</strong> Có khoang VIP 2 giường, điều hòa, nội thất hiện đại, sạch sẽ.</li>
            </ul>
            
            <h4>Các chuyến tàu SE phổ biến:</h4>
            <ul>
                <li><strong>SE1/SE2:</strong> Tàu chất lượng cao nhất, có khoang giường VIP rộng rãi và dịch vụ tiện nghi.</li>
                <li><strong>SE3/SE4:</strong> Tàu chạy ban đêm, giúp hành khách tiết kiệm thời gian và có thể nghỉ ngơi.</li>
                <li><strong>SE5/SE6:</strong> Tàu hoạt động ban ngày, lý tưởng cho những ai muốn ngắm cảnh đẹp dọc đường.</li>
                <li><strong>SE7/SE8:</strong> Tàu xuất phát vào sáng sớm, phù hợp cho các hành trình dài trong ngày.</li>
            </ul>
            <div class="highlight-box">
                <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn ưu tiên tốc độ, sự thoải mái và tiện nghi cao, tàu SE là lựa chọn hàng đầu.</p>
            </div>
        </section>

        <!-- Section for TN Trains -->
        <section class="train-section">
            <h2><i class="fas fa-piggy-bank"></i>Tàu TN (Tàu Thống Nhất) – Giá Vé Tiết Kiệm</h2>
            <ul>
                <li><strong>Lộ trình:</strong> Hà Nội - TP.HCM.</li>
                <li><strong>Tốc độ:</strong> Chậm hơn tàu SE vì dừng ở nhiều ga phụ hơn.</li>
                <li><strong>Giá vé:</strong> Thấp hơn đáng kể so với tàu SE.</li>
                <li>Phù hợp cho hành khách muốn tiết kiệm chi phí hoặc cần lên/xuống tại các ga nhỏ.</li>
            </ul>
            <div class="highlight-box">
                <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn không quá gấp về thời gian và muốn một mức giá hợp lý, tàu TN là phương án tối ưu.</p>
            </div>
        </section>

        <!-- Section for Local Trains -->
        <section class="train-section">
            <h2><i class="fas fa-map-signs"></i>Tàu Địa Phương – Kết Nối Các Tuyến Ngắn</h2>
            <ul>
                <li><strong>Lộ trình:</strong> Các tuyến ngắn như Sài Gòn – Phan Thiết, Hà Nội – Hải Phòng, Đà Nẵng – Huế.</li>
                <li><strong>Tốc độ:</strong> Trung bình, dừng tại nhiều ga nhỏ để phục vụ nhu cầu đi lại của người dân địa phương.</li>
                <li><strong>Tiện ích:</strong> Đơn giản, chủ yếu là ghế ngồi, phù hợp cho các chuyến đi ngắn.</li>
            </ul>
            <div class="highlight-box">
                <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn chỉ di chuyển trong khu vực gần, tàu địa phương là lựa chọn thuận tiện và kinh tế.</p>
            </div>
        </section>

        <!-- Section for 5-Star Trains -->
        <section class="train-section">
            <h2><i class="fas fa-star"></i>Tàu 5 Sao – Trải Nghiệm Cao Cấp</h2>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image3.png" alt="Nội thất sang trọng của tàu 5 sao" class="train-image">
            <ul>
                <li><strong>Lộ trình:</strong> Các tuyến du lịch nổi tiếng như Sài Gòn – Nha Trang, Sài Gòn – Đà Nẵng, Hà Nội – Đà Nẵng.</li>
                <li><strong>Tiện nghi:</strong> Nội thất sang trọng, khoang VIP, nhà hàng trên tàu, ghế xoay 180 độ, wifi, và nhiều dịch vụ cao cấp khác.</li>
            </ul>

            <h4>Các tàu chất lượng cao nổi bật:</h4>
            <ul>
                <li><strong>SNT2 (Sài Gòn – Nha Trang):</strong> Tàu đêm với giường nằm rộng rãi, khoang ghế mềm có thể điều chỉnh độ ngả.</li>
                <li><strong>SE19/SE20 (Hà Nội – Đà Nẵng):</strong> Có khoang 2 giường VIP, chống ồn tốt, mang lại trải nghiệm riêng tư và yên tĩnh.</li>
                <li><strong>Sjourney:</strong> Hành trình xuyên Việt 7 ngày với dịch vụ đẳng cấp, nhưng giá vé khá cao.</li>
            </ul>
            <div class="highlight-box">
                <p><strong>Lựa chọn phù hợp:</strong> Nếu bạn tìm kiếm một trải nghiệm du lịch sang trọng, thoải mái như đi máy bay hạng thương gia, tàu 5 sao là lựa chọn không thể bỏ qua.</p>
            </div>
        </section>
        
        <!-- Section about Vietnam Railway -->
        <section class="train-section">
            <h2><i class="fas fa-info-circle"></i>Giới Thiệu Về Đường Sắt Việt Nam</h2>
            <img src="${pageContext.request.contextPath}/assets/images/train-info/image4.png" alt="Bản đồ đường sắt Việt Nam" class="train-image">
            <p>Đường sắt Việt Nam, do Tổng công ty Đường sắt Việt Nam (dsvn.vn) quản lý, là hệ thống vận tải đường sắt quốc gia với tổng chiều dài 3.161 km. Tuyến đường sắt Bắc-Nam là tuyến huyết mạch, kết nối Hà Nội và TP. Hồ Chí Minh, đi qua nhiều tỉnh thành quan trọng.</p>
            <p>Với lịch sử lâu đời từ thời Pháp thuộc, đường sắt Việt Nam đã và đang đóng vai trò quan trọng trong sự phát triển kinh tế - xã hội và an ninh quốc phòng của đất nước.</p>
        </section>

        <!-- Station Info Table -->
        <section class="train-section">
            <h2><i class="fas fa-building"></i>Thông Tin Một Số Ga Tàu Lớn</h2>
            <table class="station-table" aria-label="Thông tin các ga tàu lớn">
                <thead>
                    <tr>
                        <th scope="col">Tên ga</th>
                        <th scope="col">Số điện thoại</th>
                        <th scope="col">Địa chỉ</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Ga Hà Nội</td>
                        <td>1900 0109</td>
                        <td>120 Lê Duẩn, Hoàn Kiếm, Hà Nội</td>
                    </tr>
                    <tr>
                        <td>Ga Vinh</td>
                        <td>(0238) 3853 426</td>
                        <td>Số 1 Đường Lê Ninh, Phường Quán Bàu, Thành phố Vinh, Tỉnh Nghệ An</td>
                    </tr>
                    <tr>
                        <td>Ga Huế</td>
                        <td>(0234) 3822 175</td>
                        <td>Số 02 Bùi Thị Xuân, Thành phố Huế, Tỉnh Thừa Thiên Huế</td>
                    </tr>
                    <tr>
                        <td>Ga Sài Gòn</td>
                        <td>1900 636 212</td>
                        <td>Số 01 Nguyễn Thông, Phường 9, Quận 3, Thành phố Hồ Chí Minh</td>
                    </tr>
                </tbody>
            </table>
        </section>
    </main>

    <!-- Footer -->
    <jsp:include page="../../common/footer.jsp" />
</body>
</html>
