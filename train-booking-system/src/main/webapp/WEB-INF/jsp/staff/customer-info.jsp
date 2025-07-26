<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Thông tin khách hàng</title>
    
    <!-- External Stylesheets -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/staff-dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />

    <!-- Internal Styles -->
    <style>
        /* General container styling */
        .main-content {
            padding: 20px;
        }

        /* Styling for the filter and search container */
        .filter-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .filter-container form {
            display: flex;
            align-items: center;
            gap: 15px; /* Spacing between form elements */
            flex-grow: 1;
        }

        /* Styling for form inputs and selects */
        .filter-container input[type="text"],
        .filter-container select {
            padding: 10px 15px;
            border: 1px solid #ced4da;
            border-radius: 4px;
            font-size: 14px;
            height: 40px; /* Consistent height */
        }
        
        .filter-container input[type="text"] {
            width: 300px; /* Wider search input */
        }

        /* Styling for the filter button */
        .filter-container button {
            padding: 10px 25px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .filter-container button:hover {
            background-color: #0056b3;
        }

        /* Table styling */
        .customer-list table {
            width: 100%;
            border-collapse: collapse;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .customer-list th,
        .customer-list td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }

        .customer-list thead th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        
        .customer-list tbody tr:hover {
            background-color: #f5f5f5;
        }

        /* Column-specific styling */
        .col-id {
            width: 5%; /* Smaller ID column */
        }
        .col-name {
            width: 15%;
        }
        .col-email {
            width: 22%;
        }
        .col-phone {
            width: 12%;
        }
        .col-dob {
            width: 10%;
        }
        .col-gender {
            width: 8%;
        }
        .col-address {
            width: 18%; /* Fixed width for address */
            max-width: 200px; /* Max width before scroll */
        }
        .col-status {
            width: 10%;
        }
        
        /* Style for the scrollable address cell */
        .address-scroll {
            max-width: 100%;
            overflow-x: auto; /* Enable horizontal scroll */
            white-space: nowrap; /* Prevent text wrapping */
            padding-bottom: 5px; /* Space for scrollbar */
        }
        
        /* Custom scrollbar for address */
        .address-scroll::-webkit-scrollbar {
            height: 6px;
        }
        .address-scroll::-webkit-scrollbar-track {
            background: #f1f1f1;
        }
        .address-scroll::-webkit-scrollbar-thumb {
            background: #888;
            border-radius: 3px;
        }
        .address-scroll::-webkit-scrollbar-thumb:hover {
            background: #555;
        }

        /* Pagination styling */
        .pagination {
            margin-top: 20px;
            text-align: center;
        }

        .pagination a, .pagination span {
            display: inline-block;
            padding: 8px 12px;
            margin: 0 4px;
            text-decoration: none;
            color: #007bff;
            border: 1px solid #007bff;
            border-radius: 4px;
            transition: background-color 0.3s, color 0.3s;
        }

        .pagination a.active, .pagination span.active {
            background-color: #007bff;
            color: white;
            border-color: #007bff;
        }
        
        .pagination span.disabled {
            color: #6c757d;
            pointer-events: none;
            border-color: #dee2e6;
        }

        .pagination a:hover:not(.active) {
            background-color: #e9ecef;
        }
    </style>
</head>

<body>
    <div class="dashboard-container">
        
        <!-- Sidebar -->
        <jsp:include page="sidebar.jsp" />

        <!-- Main Content -->
        <main class="main-content">
            <header class="header">
                <h1>Thông tin khách hàng</h1>
            </header>
            
            <section class="customer-list">
                <h3>Danh sách người dùng</h3>

                <!-- Search and Filter Form -->
                <div class="filter-container">
                    <form action="${pageContext.request.contextPath}/customer-info" method="get" id="filterForm">
                        <select name="searchField">
                            <option value="fullName" ${param.searchField == 'fullName' ? 'selected' : ''}>Tên</option>
                            <option value="email" ${param.searchField == 'email' ? 'selected' : ''}>Email</option>
                            <option value="phoneNumber" ${param.searchField == 'phoneNumber' ? 'selected' : ''}>Số điện thoại</option>
                            <option value="address" ${param.searchField == 'address' ? 'selected' : ''}>Địa chỉ</option>
                        </select>
                        <input type="text" name="searchTerm" placeholder="Nhập từ khóa tìm kiếm..." value="${param.searchTerm}">
                        
                        <select name="genderFilter">
                            <option value="all" ${empty param.genderFilter or param.genderFilter == 'all' ? 'selected' : ''}>Tất cả giới tính</option>
                            <option value="Male" ${param.genderFilter == 'Male' ? 'selected' : ''}>Nam</option>
                            <option value="Female" ${param.genderFilter == 'Female' ? 'selected' : ''}>Nữ</option>
                            <option value="Other" ${param.genderFilter == 'Other' ? 'selected' : ''}>Khác</option>
                        </select>
                        
                        <button type="submit">Tìm kiếm & Lọc</button>
                    </form>
                </div>

                <table class="table table-bordered table-hover">
                    <thead>
                        <tr>
                            <th class="col-id">ID</th>
                            <th class="col-name">Họ và tên</th>
                            <th class="col-email">Email</th>
                            <th class="col-phone">Số điện thoại</th>
                            <th class="col-dob">Ngày sinh</th>
                            <th class="col-gender">Giới tính</th>
                            <th class="col-address">Địa chỉ</th>
                            <th class="col-status">Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:if test="${empty customers}">
                            <tr>
                                <td colspan="8" style="text-align: center; padding: 20px;">Không tìm thấy khách hàng nào phù hợp.</td>
                            </tr>
                        </c:if>
                        <c:forEach var="customer" items="${customers}">
                            <tr>
                                <td class="col-id">${customer.userID}</td>
                                <td class="col-name">${customer.fullName}</td>
                                <td class="col-email">${customer.email}</td>
                                <td class="col-phone">${customer.phoneNumber}</td>
                                <td class="col-dob">
                                    <fmt:formatDate value="${customer.dateOfBirthAsDate}" pattern="dd/MM/yyyy" />
                                </td>
                                <td class="col-gender">
                                    <c:choose>
                                        <c:when test="${customer.gender == 'Male'}">Nam</c:when>
                                        <c:when test="${customer.gender == 'Female'}">Nữ</c:when>
                                        <c:when test="${customer.gender == 'Other'}">Khác</c:when>
                                        <c:otherwise>${customer.gender}</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="col-address">
                                    <div class="address-scroll">${customer.address}</div>
                                </td>
                                <td class="col-status">
                                    <c:choose>
                                        <c:when test="${customer.isActive}">
                                            <span style="color: green; font-weight: bold;">Hoạt động</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: red; font-weight: bold;">Bị khóa</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <!-- Pagination -->
                <div class="pagination">
                    <c:set var="totalPages" value="${totalPages}" />
                    
                    <!-- Previous Page Link -->
                    <c:if test="${currentPage > 1}">
                        <a href="#" data-page="${currentPage - 1}">Previous</a>
                    </c:if>
                    <c:if test="${currentPage <= 1}">
                        <span class="disabled">Previous</span>
                    </c:if>

                    <!-- Page Number Links -->
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="#" data-page="${i}" class="${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>

                    <!-- Next Page Link -->
                    <c:if test="${currentPage < totalPages}">
                        <a href="#" data-page="${currentPage + 1}">Next</a>
                    </c:if>
                    <c:if test="${currentPage >= totalPages}">
                        <span class="disabled">Next</span>
                    </c:if>
                </div>
            </section>
        </main>
    </div>
    
    <script src="${pageContext.request.contextPath}/js/staff-dashboard.js"></script>
    
    <!-- Inline script for form validation and pagination -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const filterForm = document.getElementById('filterForm');
            const paginationLinks = document.querySelectorAll('.pagination a[data-page]');

            // Handle form submission for search/filter
            if (filterForm) {
                filterForm.addEventListener('submit', function(event) {
                    const searchTermInput = filterForm.querySelector('input[name="searchTerm"]');
                    if (searchTermInput) {
                        searchTermInput.value = searchTermInput.value.trim().replace(/\s+/g, ' ');
                    }
                });
            }

            // Handle pagination clicks
            paginationLinks.forEach(function(link) {
                link.addEventListener('click', function(event) {
                    event.preventDefault();
                    const page = this.getAttribute('data-page');
                    
                    if (filterForm) {
                        let pageInput = filterForm.querySelector('input[name="page"]');
                        if (!pageInput) {
                            pageInput = document.createElement('input');
                            pageInput.type = 'hidden';
                            pageInput.name = 'page';
                            filterForm.appendChild(pageInput);
                        }
                        pageInput.value = page;
                        filterForm.submit();
                    }
                });
            });
        });
    </script>
</body>

</html>
