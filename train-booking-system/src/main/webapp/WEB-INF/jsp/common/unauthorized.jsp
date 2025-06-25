<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Access Denied</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/common.css"
    />
    <%-- Assuming you have a common.css --%>
    <style>
      body {
        display: flex;
        justify-content: center; /* Horizontally center the container */
        align-items: flex-start; /* Align container to the top */
        height: 100vh;
        margin: 0;
        font-family: "Comic Sans MS", "Chalkboard SE", "Arial", sans-serif;
        background-image: url("${pageContext.request.contextPath}/assets/common/cat_gun.png");
        background-size: cover;
        background-position: center center;
        background-repeat: no-repeat;
        color: #e74c3c;
        text-align: center;
        overflow-y: auto; /* Allow scrolling if content overflows, though aiming for top placement */
        padding-top: 5vh; /* Add some padding from the top of the viewport */
      }
      .container {
        width: 90%;
        max-width: 600px; /* Max width for the content box */
        padding: 30px;
        background-color: rgba(
          44,
          62,
          80,
          0.85
            /* Semi-transparent dark slate gray - to contrast with light cat image parts */
        );
        border-radius: 15px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        /* position: relative; No longer needed for ::before */
        /* z-index: 1; No longer needed for ::before */
        color: #ecf0f1; /* Light text color for dark container background */
      }
      /* Removed .container::before pseudo-element */
      h1 {
        color: #e74c3c; /* Keep strong red for warning heading */
        font-size: 2.5em; /* Larger heading */
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
      }
      p {
        font-size: 1.3em;
        color: #ecf0f1; /* Light text color for readability on dark container */
        margin-bottom: 15px;
      }
      a {
        display: inline-block;
        margin-top: 20px;
        padding: 10px 20px;
        color: #fff;
        background-color: #e74c3c; /* Red button to match warning theme */
        text-decoration: none;
        border-radius: 5px;
        transition: background-color 0.3s ease;
      }
      a:hover {
        background-color: #c0392b; /* Darker red on hover */
      }
      .link-container {
        display: flex;
        justify-content: space-around; /* Distributes space evenly around items */
        align-items: center; /* Vertically aligns items in the center */
        width: 100%; /* Ensures the container takes full width to allow even spacing */
        margin-top: 20px; /* Adds some space above the link container */
      }
      .link-container a {
        margin-top: 0; /* Reset margin-top for individual links as container handles spacing */
        /* Adjust padding or margins on individual links if more specific spacing is needed */
      }
    </style>
  </head>
  <body>
    <div class="container">
      <h1>TRUY CẬP BỊ TỪ CHỐI</h1>
      <p>Bạn không thể qua các trang khác bằng cách tà đạo.</p>
      <p>Nếu bạn nghĩ đây là lỗi thì nó là tính năng</p>
      <p>Không thì hãy hỏi con mèo</p>
      <p>
        ĐIỀU NÀY CÓ THỂ DO BẠN ĐANG CỐ GẮNG TRUY CẬP ĐẾN MỘT ĐỊA CHỈ BỊ HẠN CHẾ
        (QUAY LẠI MÀN HÌNH CHÍNH)
      </p>
      <p>HOẶC PHIÊN ĐĂNG NHẬP CỦA BẠN ĐÃ HẾT HẠN (QUAY LẠI MÀN ĐĂNG NHẬP).</p>
      <div class="link-container">
        <a href="${pageContext.request.contextPath}/landing">Màn hình chính</a>
        <a href="${pageContext.request.contextPath}/login"
          >Màn hình đăng nhập</a
        >
      </div>
      <%-- Or a more specific link based on user role if available, e.g., back
      to their dashboard --%>
    </div>
  </body>
</html>
