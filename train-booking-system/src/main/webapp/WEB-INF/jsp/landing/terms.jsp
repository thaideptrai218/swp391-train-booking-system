<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Điều khoản và Câu hỏi thường gặp - Vetaure.com</title>
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/landing-page.css"
    />
    <style>
      body {
        font-family: Arial, sans-serif;
        line-height: 1.6;
        margin: 0;
        padding: 0;
        background-color: #f4f4f4;
        color: #333;
      }
      .container {
        width: 80%;
        margin: auto;
        overflow: hidden;
        padding: 20px;
        background-color: #fff;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
      h1,
      h2,
      h3 {
        color: #0056b3; /* Adjust color to match image */
      }
      h1 {
        text-align: center;
        margin-bottom: 20px;
      }
      .section {
        margin-bottom: 30px;
      }
      .section h2 {
        border-bottom: 2px solid #0056b3;
        padding-bottom: 10px;
        margin-bottom: 15px;
      }
      .section h3 {
        margin-top: 20px;
        margin-bottom: 10px;
      }
      ul {
        list-style-type: none;
        padding-left: 0;
      }
      ul li {
        margin-bottom: 10px;
        padding-left: 20px;
        position: relative;
      }
      ul li::before {
        content: "◆"; /* Diamond bullet */
        position: absolute;
        left: 0;
        color: #0056b3; /* Adjust color to match image */
        font-size: 1.2em;
        line-height: 1;
      }
      .faq-question {
        font-weight: bold;
      }
      .faq-answer {
        padding-left: 20px;
        margin-bottom: 15px;
      }
      .faq-answer ul li::before {
        content: "-"; /* Dash for sub-bullets */
        font-size: 1em;
      }
      .note {
        font-style: italic;
        color: #555;
        margin-top: 10px;
      }
    </style>
  </head>
  <body>
    <%-- <jsp:include page="header.jsp" /> --%>

    <div class="container">
      <h1>Các quy định</h1>

      <div class="section">
        <h2>Các Điều Kiện & Điều Khoản</h2>
        <ul>
          <li>1.1. Quy định vận chuyển.</li>
          <li>1.2. Điều kiện sử dụng hệ thống mua vé trực tuyến.</li>
          <li>1.3. Điều khoản sử dụng website Vetaure.com.</li>
        </ul>
      </div>

      <div class="section">
        <h2>Phương thức thanh toán</h2>
        <%-- Add content for payment methods if available in the image or if
        needed --%>
      </div>

      <div class="section">
        <h2>Chính sách hoàn trả vé</h2>
        <ul>
          <li>3.1. Chính sách hoàn trả vé, đổi vé.</li>
          <li>3.2. Quy định thời gian hoàn tiền.</li>
        </ul>
      </div>

      <div class="section">
        <h2>Chính sách bảo mật thông tin</h2>
        <%-- Add content for privacy policy if available in the image or if
        needed --%>
      </div>

      <h1>Các câu hỏi thường gặp khi đi tàu hỏa?</h1>

      <div class="section">
        <h3 class="faq-question">
          Làm thế nào để đặt vé tàu online trên Vetaure?
        </h3>
        <div class="faq-answer">
          <ul>
            <li>
              Bước 1: Truy cập Website
              <a href="http://Vetaure.com" target="_blank">Vetaure.com</a>.
            </li>
            <li>
              Bước 2: Nhập thông tin hành trình (điểm đi, điểm đến, ngày khởi
              hành).
            </li>
            <li>Bước 3: Chọn chuyến tàu, toa tàu, loại ghế phù hợp.</li>
            <li>
              Bước 4: Thanh toán trực tuyến bằng thẻ ngân hàng hoặc ví điện tử.
            </li>
            <li>Bước 5: Nhận mã vé điện tử qua email hoặc SMS.</li>
          </ul>
          <p class="note">
            Với Vetaure, bạn có thể đặt vé mọi lúc, mọi nơi, nhanh chóng và an
            toàn.
          </p>
        </div>

        <h3 class="faq-question">Làm sao để mua được vé tàu giá rẻ?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Cần chuẩn bị những gì khi đi tàu hoả?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Quy định về hành lý khi đi tàu hoả là gì?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Vé tàu có được đổi trả không?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Làm sao để kiểm tra thông tin vé tàu?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Trẻ em đi tàu hoả có cần mua vé không?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">
          Vé tàu Online có khác gì so với vé mua tại ga không?
        </h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">
          Tôi cần làm gì nếu không nhận được mã vé sau khi thanh toán?
        </h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Có cần in vé tàu khi đã mua online không?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">Làm sao để huỷ / đổi vé tàu online?</h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">
          Phương thức thanh toán nào được chấp nhận khi thanh toán online?
        </h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">
          Tôi có thể mua vé tàu online cho người khác không?
        </h3>
        <%-- Add answer if available --%>

        <h3 class="faq-question">
          Cần chuẩn bị những gì để lên tàu khi mua vé online?
        </h3>
        <%-- Add answer if available --%>
      </div>
    </div>

    <%-- <jsp:include page="footer.jsp" /> --%>
  </body>
</html>
