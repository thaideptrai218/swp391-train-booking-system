<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
  <head>
    <title>Manager Home</title>
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/manager/manager-dashboard.css"
    />
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/css/common.css"
    />
  </head>
  <body>
    <div class="dashboard-container">
      <%@ include file="sidebar.jsp" %>

      <div class="main-content">
        <header class="dashboard-header">
          <h1>Chào Mừng, <c:out value="${loggedInUser.fullName}" /></h1>
          <p>
            Chào mừng bạn đến với trang quản lý. Vui lòng sử dụng menu bên trái
            để quản lý hệ thống.
          </p>
        </header>
        <img
          src="${pageContext.request.contextPath}/assets/common/pointer.png"
          alt="Logo"
          class="logo"
        />
      </div>
    </div>
  </body>
</html>
