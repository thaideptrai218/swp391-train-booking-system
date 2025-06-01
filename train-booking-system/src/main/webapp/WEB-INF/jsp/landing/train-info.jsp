<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Thông Tin Loại Tàu</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/train-info.css"
    />
  </head>
  <body>
    <div class="container">
      <h1>Nên chọn loại tàu nào khi đặt vé?</h1>

      <div class="content-flex">
        <div class="text-content">
          <ul>
            <li>Bạn cần đi nhanh, tiện nghi tốt? → Chọn tàu SE1-SE6</li>
            <li>Bạn muốn tiết kiệm chi phí? → Chọn tàu TN</li>
            <li>Bạn di chuyển trong khu vực gần? → Chọn tàu địa phương</li>
            <li>
              Bạn muốn trải nghiệm sang trọng? → Chọn tàu 5 sao hoặc tàu du lịch
            </li>
          </ul>
          <p>
            Hiện nay, Đường sắt Việt Nam vận hành nhiều loại tàu với đặc điểm
            khác nhau nhằm đáp ứng nhu cầu di chuyển của hành khách. Dưới đây là
            thông tin chi tiết về từng loại tàu, giúp bạn lựa chọn chuyến đi phù
            hợp.
          </p>
        </div>
        <div class="image-content">
          <img
            src="${pageContext.request.contextPath}/assets/images/train-info/img1.jpg"
            alt="Nội thất tàu lửa"
          />
        </div>
      </div>

      <div class="train-type-details">
        <h2>1. Tàu SE (Super Express) – Tàu nhanh Thống Nhất</h2>
        <div class="content-flex">
          <div class="text-content">
            <ul>
              <li>Lộ trình: Hà Nội – TP.HCM</li>
              <li>Tốc độ: Nhanh, dừng ít ga hơn các tàu khác</li>
              <li>
                Tiện nghi: Có khoang VIP 2 giường, điều hòa, nội thất hiện đại
              </li>
            </ul>
            <p><strong>Các chuyến tàu SE phổ biến:</strong></p>
            <ul>
              <li>
                SE1/SE2: Chất lượng cao nhất, khoang giường VIP rộng rãi, dịch
                vụ tiện nghi.
              </li>
              <li>
                SE3/SE4: Chạy ban đêm, giúp hành khách nghỉ ngơi và đến nơi vào
                sáng hôm sau
              </li>
              <li>
                SE5/SE6: Hoạt động ban ngày, phù hợp cho những ai muốn ngắm cảnh
                dọc hành trình.
              </li>
              <li>
                SE7/SE8: Xuất phát vào sáng sớm, thích hợp với hành trình dài
                trong ngày.
              </li>
            </ul>
            <p>
              <strong>Lựa chọn phù hợp:</strong> Nếu bạn cần di chuyển nhanh với
              tiện nghi tốt, tàu SE là lựa chọn hợp lý.
            </p>
          </div>
          <div class="image-content">
            <img
              src="${pageContext.request.contextPath}/assets/images/train-info/img2.jpg"
              alt="Tàu SE (Super Express)"
            />
          </div>
        </div>
      </div>

      <div class="train-type-details">
        <h2>2. Tàu TN (Tàu Thống Nhất) – Giá vé tiết kiệm hơn</h2>
        <ul>
          <li>Lộ trình: Hà Nội – TP.HCM</li>
          <li>Tốc độ: Chậm hơn SE, dừng tại nhiều ga hơn</li>
          <li>Giá vé: Thấp hơn so với tàu SE</li>
          <li>
            Tàu TN phù hợp với hành khách muốn tiết kiệm chi phí hoặc cần
            lên/xuống tại các ga nhỏ dọc tuyến.
          </li>
        </ul>
        <p>
          <strong>Lựa chọn phù hợp:</strong> Nếu bạn không quá gấp về thời gian
          và muốn mức giá hợp lý hơn, tàu TN là phương án thích hợp.
        </p>
      </div>

      <div class="train-type-details">
        <h2>3. Tàu địa phương – Kết nối các tuyến ngắn</h2>
        <ul>
          <li>
            Lộ trình: Các tuyến ngắn như Sài Gòn – Phan Thiết, Hà Nội – Hải
            Phòng, Đà Nẵng – Huế
          </li>
          <li>Tốc độ: Trung bình, dừng tại nhiều ga nhỏ</li>
          <li>
            Tiện ích: Đơn giản, phù hợp cho hành khách đi lại giữa các tỉnh lân
            cận.
          </li>
        </ul>
        <p>
          <strong>Lựa chọn phù hợp:</strong> Nếu bạn chỉ di chuyển trong khu vực
          gần, tàu địa phương là lựa chọn thuận tiện và tiết kiệm.
        </p>
      </div>

      <div class="train-type-details">
        <h2>4. Tàu 5 sao – Trải nghiệm cao cấp</h2>
        <div class="image-content centered-image">
          <%-- Assuming you want this image centered above text --%>
          <img
            src="${pageContext.request.contextPath}/assets/images/train-info/img3.jpg"
            alt="Tàu 5 sao"
          />
        </div>
        <ul>
          <li>
            Lộ trình: Sài Gòn – Nha Trang, Sài Gòn – Đà Nẵng, Hà Nội – Đà Nẵng
          </li>
          <li>
            Tiện nghi: Nội thất hiện đại, khoang VIP, nhà hàng trên tàu, ghế
            xoay 180 độ
          </li>
        </ul>
        <p>
          <strong>Các tàu chất lượng cao trên tuyến đường sắt Việt Nam:</strong>
        </p>
        <ul>
          <li>
            SNT2 (Sài Gòn – Nha Trang): Tàu đêm, giường nằm rộng rãi, khoang ghế
            mềm điều chỉnh độ ngả.
          </li>
          <li>
            SE19/SE20 (Hà Nội – Đà Nẵng): Khoang 2 giường VIP, chống ồn, tiện
            nghi cao cấp.
          </li>
          <li>
            Sjourney: Hành trình xuyên Việt 7 ngày, dịch vụ cao cấp, giá vé cao.
          </li>
        </ul>
        <p>
          <strong>Lựa chọn phù hợp:</strong> Nếu bạn tìm kiếm trải nghiệm sang
          trọng, thoải mái như đi máy bay hạng thương gia, tàu 5 sao là lựa chọn
          lý tưởng.
        </p>
      </div>

      <div class="train-type-details introduction-section">
        <h2>Giới thiệu về Đường Sắt Việt Nam</h2>
        <p>
          Đường sắt Việt Nam là hệ thống vận tải đường sắt chính ở Việt Nam, do
          Tổng công ty Đường sắt Việt Nam (dsvn.vn) quản lý. Hệ thống này có
          tổng chiều dài 3.161 km, với tuyến đường sắt Bắc-Nam là tuyến đường
          sắt huyết mạch, nối liền thủ đô Hà Nội với thành phố Hồ Chí Minh và
          nhiều tỉnh thành khác trên cả nước.
        </p>
        <div class="image-content centered-image">
          <img
            src="${pageContext.request.contextPath}/assets/images/train-info/img4.jpg"
            alt="Đường sắt Việt Nam"
          />
        </div>
        <p>
          Tuyến đường sắt Bắc-Nam được khởi công xây dựng từ thời Pháp thuộc.
          Trải qua nhiều thăng trầm lịch sử, đường sắt Việt Nam đã đóng góp vai
          trò quan trọng trong sự phát triển kinh tế - xã hội và an ninh quốc
          phòng của đất nước.
        </p>
        <p>
          Ngày nay, cùng với sự phát triển của các loại hình vận tải khác, đường
          sắt Việt Nam vẫn giữ vai trò quan trọng trong việc vận chuyển hành
          khách và hàng hóa, đặc biệt là trên tuyến đường sắt Bắc-Nam.
        </p>
      </div>

      <div class="station-list-section train-type-details">
        <%-- Reusing train-type-details for consistent spacing/border --%>
        <h2>Danh sách nhà ga tàu phổ biến thuộc Đường Sắt Việt Nam</h2>
        <table class="station-table">
          <thead>
            <tr>
              <th>Tên ga</th>
              <th>Số điện thoại</th>
              <th>Địa chỉ</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Ga Hà Nội</td>
              <td>19000109</td>
              <td>120 Lê Duẩn, Hoàn Kiếm, Hà Nội</td>
            </tr>
            <tr>
              <td>Ga Vinh</td>
              <td>(0238) 3853 426</td>
              <td>
                Số 1 Đường Lệ Ninh - Phường Quán Bàu - Thành phố Vinh - Tỉnh
                Nghệ An
              </td>
            </tr>
            <tr>
              <td>Ga Huế</td>
              <td>(0234) 3822 175</td>
              <td>Số 02 Bùi Thị Xuân - Thành phố Huế - Tỉnh Thừa Thiên Huế</td>
            </tr>
            <tr>
              <td>Ga Sài Gòn</td>
              <td>1900 636 212</td>
              <td>
                Số 01 Nguyễn Thông - Phường 9 - Quận 3 - Thành phố Hồ Chí Minh
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>
