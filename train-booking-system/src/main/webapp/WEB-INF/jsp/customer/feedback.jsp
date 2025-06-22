<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Feedback</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }
        h1 { color: #2c3e50; }
        h2 { color: #34495e; }
        ul { margin: 15px 0; }
        li { margin-bottom: 10px; }
        .container { max-width: 800px; margin: auto; }
        strong { color: #2c3e50; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Feedback</h1>
    </div>

    <h2>Gửi phản hồi của bạn</h2>
    <form action="submitFeedback" method="post">
        <label for="title">Tiêu đề:</label><br>
        <input type="text" id="title" name="title"><br><br>

        <label for="comment">Bình luận:</label><br>
        <textarea id="comment" name="comment" rows="4" cols="50"></textarea><br><br>

        <input type="submit" value="Gửi">
    </form>
</body>
</html>
