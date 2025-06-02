-- =============================================================================
-- Sample Data Insertion for Table: Users
-- =============================================================================

PRINT 'Inserting abundant data into Users...';
-- Batch 1: Admin and Staff
INSERT INTO Users (FullName, Email, PhoneNumber, Password, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES
(N'Trần Văn Quản Trị', 'admin.super@vnrailway.com', '0901000001', '123', '001080000001', 'Admin', 1, DATEADD(year, -2, GETDATE()), DATEADD(day, -1, GETDATE())),
(N'Nguyễn Thị Điều Phối', 'staff.dispatch01@vnrailway.com', '0902000002', '123', '001085000002', 'Staff', 1, DATEADD(month, -18, GETDATE()), DATEADD(day, -2, GETDATE())),
(N'Lê Minh Hỗ Trợ', 'staff.support01@vnrailway.com', '0903000003', '123', '001090000003', 'Staff', 1, DATEADD(month, -15, GETDATE()), DATEADD(hour, -5, GETDATE())),
(N'Phạm Hùng Kỹ Thuật', 'staff.tech01@vnrailway.com', '0904000004', '123', '001075000004', 'Staff', 0, DATEADD(month, -20, GETDATE()), DATEADD(day, -30, GETDATE())),
(N'Hoàng Bích Phượng', 'staff.accounting01@vnrailway.com', '0905000005', '123', '001088000005', 'Staff', 1, DATEADD(month, -10, GETDATE()), DATEADD(day, -3, GETDATE()));

-- Batch 2: Regular Customers
INSERT INTO Users (FullName, Email, PhoneNumber, Password, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES
(N'Nguyễn Văn An', 'an.nguyen@email.com', '0912345001', '123', '030080000101', 'Customer', 1, DATEADD(day, -300, GETDATE()), DATEADD(day, -5, GETDATE())),
(N'Trần Thị Bình', 'binh.tran@email.com', '0912345002', '123', '030081000102', 'Customer', 1, DATEADD(day, -250, GETDATE()), DATEADD(day, -10, GETDATE())),
(N'Lê Văn Cường', 'cuong.le@email.com', '0912345003', '123', NULL, 'Customer', 1, DATEADD(day, -200, GETDATE()), DATEADD(day, -3, GETDATE())),
(N'Phạm Thị Dung', 'dung.pham@email.com', '0912345004', '123', '030083000104', 'Customer', 1, DATEADD(day, -180, GETDATE()), DATEADD(day, -7, GETDATE())),
(N'Hoàng Văn Em', 'em.hoang@email.com', '0912345005', '123', '030084000105', 'Customer', 0, DATEADD(day, -150, GETDATE()), DATEADD(day, -60, GETDATE())),
(N'Vũ Thị Giang', 'giang.vu@email.com', '0988111222', '123', '031085000106', 'Customer', 1, DATEADD(day, -120, GETDATE()), DATEADD(day, -1, GETDATE())),
(N'Đỗ Văn Hùng', 'hung.do@email.com', '0988111333', '123', '031086000107', 'Customer', 1, DATEADD(day, -100, GETDATE()), DATEADD(day, -15, GETDATE())),
(N'Trịnh Thị Kim', 'kim.trinh@email.com', '0988111444', '123', NULL, 'Customer', 1, DATEADD(day, -90, GETDATE()), DATEADD(day, -20, GETDATE())),
(N'Mai Văn Lộc', 'loc.mai@email.com', '0988111555', '123', '031088000109', 'Customer', 1, DATEADD(day, -80, GETDATE()), DATEADD(hour, -8, GETDATE()));

-- Batch 3: More Customers with varied details
INSERT INTO Users (FullName, Email, PhoneNumber, Password, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES
(N'Bùi Thị Mơ', 'mo.bui@email.com', '0988111666', '123', '031089000110', 'Customer', 1, DATEADD(day, -70, GETDATE()), DATEADD(day, -2, GETDATE())),
(N'Đặng Quốc Nam', 'nam.dang@company.com', '0977001001', '123', '032090000111', 'Customer', 1, DATEADD(day, -65, GETDATE()), DATEADD(day, -4, GETDATE())),
(N'Ông Thị Oanh', 'oanh.ong@personal.net', '0977001002', '123', '032091000112', 'Customer', 1, DATEADD(day, -60, GETDATE()), DATEADD(day, -9, GETDATE())),
(N'Phan Văn Phát', 'phat.phan.vn@domain.org', '0977001003', '123', NULL, 'Customer', 1, DATEADD(day, -55, GETDATE()), DATEADD(day, -6, GETDATE())),
(N'Quách Thị Quyên', 'quyen.quach.lovely@mail.co', '0977001004', '123', '032093000114', 'Customer', 1, DATEADD(day, -50, GETDATE()), DATEADD(day, -11, GETDATE())),
(N'Đinh Công Sử', 'su.dinh.official@work.com', '0977001005', '123', '032094000115', 'Customer', 1, DATEADD(day, -45, GETDATE()), DATEADD(day, -1, GETDATE()));

-- Batch 4: Recently signed-up customers
INSERT INTO Users (FullName, Email, PhoneNumber, Password, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES
(N'Tôn Nữ Thanh Tâm', 'tam.tonnu@newmail.com', '0966002001', '123', '033095000116', 'Customer', 1, DATEADD(day, -20, GETDATE()), DATEADD(day, -2, GETDATE())),
(N'Ưng Hoàng Thông', 'thong.ung.music@artist.com', '0966002002', '123', NULL, 'Customer', 1, DATEADD(day, -15, GETDATE()), NULL),
(N'Vạn Thị Uyên', 'uyen.van.beauty@style.com', '0966002003', '123', '033097000118', 'Customer', 1, DATEADD(day, -10, GETDATE()), DATEADD(day, -1, GETDATE())),
(N'Xa Văn Xuân', 'xuan.xa.travel@blog.com', '0966002004', '123', '033098000119', 'Customer', 1, DATEADD(day, -5, GETDATE()), NULL),
(N'Yên Thị Yến', 'yen.yen.singer@stage.com', '0966002005', '123', '033099000120', 'Customer', 1, DATEADD(day, -2, GETDATE()), DATEADD(hour, -2, GETDATE()));

-- Batch 5: Edge case users
INSERT INTO Users (FullName, Email, PhoneNumber, Password, IDCardNumber, Role, IsActive, CreatedAt, LastLogin) VALUES
(N'Alexandre Gustave Eiffel Tower Junior The Third', 'alex.eiffel.long.name.jr3@paris.fr', '0999888777', '123', 'FR001234567', 'Customer', 1, DATEADD(day, -40, GETDATE()), DATEADD(day, -10, GETDATE())),
(N'김민준 (Kim Min-jun)', 'minjun.kim@seoul.kr', '0911999888', '123', 'KR098765432', 'Customer', 1, DATEADD(day, -35, GETDATE()), DATEADD(day, -5, GETDATE()));

PRINT 'Finished inserting abundant data into Users. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' (for the last batch, total should be sum of batches).';
GO

-- =============================================================================
-- Sample Data Insertion for Table: PassengerTypes
-- =============================================================================

PRINT 'Inserting abundant data into PassengerTypes...';

INSERT INTO PassengerTypes (TypeName, DiscountPercentage, Description, RequiresDocument) VALUES
(N'Người lớn', 0.00, N'Hành khách từ 12 tuổi trở lên, không thuộc các đối tượng ưu tiên khác. Giá vé tiêu chuẩn.', 0),
(N'Trẻ em (6-11 tuổi)', 25.00, N'Hành khách từ đủ 6 tuổi đến dưới 12 tuổi. Cần xuất trình giấy khai sinh hoặc hộ chiếu để xác minh độ tuổi.', 1),
(N'Trẻ em (Dưới 6 tuổi)', 100.00, N'Hành khách dưới 6 tuổi đi cùng người lớn và không chiếm chỗ ngồi riêng. Miễn phí vé. Cần giấy khai sinh.', 1),
(N'Sinh viên', 10.00, N'Hành khách là sinh viên các trường Đại học, Cao đẳng, Trung cấp chuyên nghiệp, Dạy nghề. Cần xuất trình thẻ sinh viên còn hiệu lực.', 1),
(N'Người cao tuổi (Từ 60 tuổi)', 15.00, N'Công dân Việt Nam từ đủ 60 tuổi trở lên. Cần xuất trình Chứng minh nhân dân hoặc Căn cước công dân.', 1),
(N'Người khuyết tật nặng', 50.00, N'Hành khách là người khuyết tật nặng theo quy định. Cần giấy xác nhận khuyết tật.', 1),
(N'Người khuyết tật đặc biệt nặng', 75.00, N'Hành khách là người khuyết tật đặc biệt nặng theo quy định, có thể cần người đi kèm. Cần giấy xác nhận khuyết tật.', 1),
(N'Thương binh, Bệnh binh', 20.00, N'Hành khách là thương binh, bệnh binh có tỷ lệ thương tật/suy giảm khả năng lao động. Cần thẻ thương binh/bệnh binh.', 1),
(N'Học sinh (Phổ thông)', 5.00, N'Hành khách là học sinh các trường phổ thông (tiểu học, THCS, THPT). Chương trình khuyến khích, có thể yêu cầu thẻ học sinh.', 1),
(N'Vé Đoàn (Trên 20 người)', 5.00, N'Áp dụng cho đoàn khách đặt từ 20 vé người lớn trở lên trên cùng một chuyến tàu, cùng hành trình. Liên hệ phòng vé để biết chi tiết.', 0),
(N'Cán bộ công nhân viên Đường sắt', 90.00, N'Dành cho cán bộ, công nhân viên ngành đường sắt theo chính sách nội bộ. Yêu cầu thẻ ngành.', 1),
(N'Khách hàng VIP Vàng', 7.50, N'Chương trình khách hàng thân thiết hạng Vàng. Giảm giá trên giá vé Người lớn.', 0),
(N'Khách hàng VIP Bạch Kim', 12.50, N'Chương trình khách hàng thân thiết hạng Bạch Kim. Giảm giá trên giá vé Người lớn.', 0),
(N'Vé Mua Sớm (>30 ngày)', 5.00, N'Hành khách mua vé trước ngày khởi hành từ 30 ngày trở lên. Áp dụng có điều kiện, không cộng dồn với một số ưu đãi khác.', 0),
(N'Trẻ em (Chiếm chỗ riêng - Dưới 6 tuổi)', 50.00, N'Trường hợp trẻ em dưới 6 tuổi cần có chỗ ngồi riêng, sẽ tính 50% giá vé người lớn. Cần giấy khai sinh.', 1);

PRINT 'Finished inserting abundant data into PassengerTypes. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO

-- =============================================================================
-- Sample Data Insertion for Table: Passengers
-- =============================================================================

PRINT 'Inserting abundant data into Passengers...';

-- Assuming the UserIDs from the previous Users data insertion.
-- UserID 4: Customer Alice (an.nguyen@email.com)
-- UserID 5: Customer Bob (binh.tran@email.com)
-- UserID 6: Customer Charlie (cuong.le@email.com)
-- UserID 7: Customer David (dung.pham@email.com)
-- UserID 8: Customer Eve (em.hoang@email.com)
-- UserID 11: Customer Giang (giang.vu@email.com)
-- UserID 12: Customer Hùng (hung.do@email.com)
-- UserID 16: Customer Nam (nam.dang@company.com)
-- UserID 21: Customer Tâm (tam.tonnu@newmail.com)

-- PassengerTypeID mapping:
-- 1: Người lớn
-- 2: Trẻ em (6-11 tuổi)
-- 3: Trẻ em (Dưới 6 tuổi)
-- 4: Sinh viên
-- 5: Người cao tuổi (Từ 60 tuổi)
-- 6: Người khuyết tật nặng

-- Batch 1: Passengers who are also users (or main passengers for user accounts)
INSERT INTO Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES
(N'Nguyễn Văn An', '030080000101', 1, '1990-03-15', 4),         -- Alice (UserID 4) is PassengerType 'Người lớn'
(N'Trần Thị Bình', '030081000102', 1, '1985-07-22', 5),         -- Bob (UserID 5) is PassengerType 'Người lớn'
(N'Lê Văn Cường', '030082000103', 4, '2002-01-01', 6),         -- Charlie (UserID 6) is PassengerType 'Sinh viên' (assuming no ID card for user, but has one as passenger)
(N'Phạm Thị Dung', '030083000104', 1, '1978-11-05', 7),         -- David (UserID 7) is PassengerType 'Người lớn'
(N'Hoàng Văn Em', '030084000105', 5, '1960-02-29', 8),         -- Eve (UserID 8) is PassengerType 'Người cao tuổi'
(N'Vũ Thị Giang', '031085000106', 1, '1995-09-10', 11),        -- Giang (UserID 11) is PassengerType 'Người lớn'
(N'Đỗ Văn Hùng', '031086000107', 1, '1988-12-20', 12),        -- Hùng (UserID 12) is PassengerType 'Người lớn'
(N'Đặng Quốc Nam', '032090000111', 1, '1992-06-01', 16),       -- Nam (UserID 16) is PassengerType 'Người lớn'
(N'Tôn Nữ Thanh Tâm', '033095000116', 4, '2003-04-18', 21);    -- Tâm (UserID 21) is PassengerType 'Sinh viên'

-- Batch 2: Passengers booked by users (family members, friends - linked to the UserID of the booker)
INSERT INTO Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES
(N'Nguyễn Bảo An', NULL, 2, '2016-08-01', 4),                   -- Child of Alice (UserID 4), 7 years old
(N'Trần Văn Minh', '030081000120', 5, '1955-01-20', 5),         -- Father of Bob (UserID 5), Senior
(N'Lê Thị Hoa', '030082000121', 1, '2001-05-10', 6),           -- Friend of Charlie (UserID 6), Adult
(N'Phạm Gia Hân', NULL, 3, '2020-10-15', 7),                   -- Young child of David (UserID 7), 3 years old, no ID card needed for booking
(N'Hoàng Minh Khang', NULL, 2, '2014-03-03', 8),               -- Grandchild of Eve (UserID 8), 9 years old
(N'Vũ Minh Anh', '031085000122', 4, '2004-07-07', 11),          -- Sibling of Giang (UserID 11), Student
(N'Nguyễn Ngọc Long', '001080123456', 1, '1991-02-14', 4),     -- Another adult passenger booked by Alice
(N'Lê Bảo Châu', NULL, 3, '2021-01-20', 6);                     -- Young child booked by Charlie

-- Batch 3: Passengers not directly linked to a User account (e.g., booked at counter, or user didn't save them)
INSERT INTO Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES
(N'Trần Quốc Tuấn', '045075001001', 1, '1975-04-30', NULL),
(N'Lý Thường Kiệt', '045080001002', 5, '1950-09-02', NULL),
(N'Ngô Quyền', '045085001003', 1, '1980-10-10', NULL),
(N'Mai An Tiêm', NULL, 2, '2017-06-15', NULL),               -- Child, no ID card
(N'Hoàng Diệu', '045090001005', 4, '2001-11-20', NULL),      -- Student
(N'Bà Triệu', '045095001006', 1, '1965-03-08', NULL),
(N'Yết Kiêu', '046070001007', 6, '1982-07-27', NULL),         -- Disabled person
(N'Dã Tượng', '046072001008', 1, '1983-05-19', NULL),
(N'Phạm Ngũ Lão', '046078001009', 5, '1948-12-01', NULL);

-- Batch 4: More diverse passengers for testing different scenarios
INSERT INTO Passengers (FullName, IDCardNumber, PassengerTypeID, DateOfBirth, UserID) VALUES
(N'Hồ Xuân Hương', '050060001010', 1, '1970-01-01', NULL),
(N'Nguyễn Du', '050065001011', 5, '1955-02-02', NULL),
(N'Cao Bá Quát', '050070001012', 1, '1985-03-03', NULL),
(N'Đoàn Thị Điểm', NULL, 3, '2019-04-04', 5),                -- Young child booked by Bob (UserID 5)
(N'Nguyễn Trãi', '050075001014', 5, '1940-05-05', NULL),
(N'Lê Lợi', '050080001015', 1, '1977-06-06', NULL),
(N'Quang Trung', '050082001016', 1, '1989-07-07', NULL),
(N'Bùi Thị Xuân', '050085001017', 1, '1993-08-08', 4);        -- Adult booked by Alice (UserID 4)

PRINT 'Finished inserting abundant data into Passengers. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' (for the last batch, total should be sum of batches).';
GO

-- =============================================================================
-- Sample Data Insertion for Table: Stations
-- =============================================================================

PRINT 'Inserting abundant data into Stations...';

-- Batch 1: Major North-South Line Stations & Key Regional Hubs
INSERT INTO Stations (StationCode, StationName, City, Region, Address) VALUES
('HN', N'Ga Hà Nội', N'Hà Nội', N'Miền Bắc', N'120 Lê Duẩn, Hoàn Kiếm, Hà Nội'),
('NDH', N'Ga Nam Định', N'Nam Định', N'Miền Bắc', N'Trần Đăng Ninh, TP. Nam Định'),
('NBH', N'Ga Ninh Bình', N'Ninh Bình', N'Miền Bắc', N'Hoàng Hoa Thám, TP. Ninh Bình'),
('THO', N'Ga Thanh Hóa', N'Thanh Hóa', N'Miền Bắc Trung Bộ', N'Dương Đình Nghệ, TP. Thanh Hóa'),
('VIN', N'Ga Vinh', N'Nghệ An', N'Miền Bắc Trung Bộ', N'Lê Lợi, TP. Vinh, Nghệ An'),
('DHO', N'Ga Đồng Hới', N'Quảng Bình', N'Miền Bắc Trung Bộ', N'Tiểu khu 10, TP. Đồng Hới'),
('DDH', N'Ga Đông Hà', N'Quảng Trị', N'Miền Bắc Trung Bộ', N'02 Lê Thánh Tôn, TP. Đông Hà'),
('HUE', N'Ga Huế', N'Thừa Thiên Huế', N'Miền Duyên Hải Nam Trung Bộ', N'02 Bùi Thị Xuân, TP. Huế'),
('DN', N'Ga Đà Nẵng', N'Đà Nẵng', N'Miền Duyên Hải Nam Trung Bộ', N'202 Hải Phòng, Thanh Khê, Đà Nẵng'),
('TAM', N'Ga Tam Kỳ', N'Quảng Nam', N'Miền Duyên Hải Nam Trung Bộ', N'Nguyễn Hoàng, TP. Tam Kỳ'),
('QN', N'Ga Quảng Ngãi', N'Quảng Ngãi', N'Miền Duyên Hải Nam Trung Bộ', N'Trần Hưng Đạo, TP. Quảng Ngãi'),
('DIE', N'Ga Diêu Trì', N'Bình Định', N'Miền Duyên Hải Nam Trung Bộ', N'Thị trấn Diêu Trì, Tuy Phước, Bình Định'), -- Hub for Quy Nhon
('TUH', N'Ga Tuy Hòa', N'Phú Yên', N'Miền Duyên Hải Nam Trung Bộ', N'149 Lê Trung Kiên, TP. Tuy Hòa'),
('NT', N'Ga Nha Trang', N'Khánh Hòa', N'Miền Duyên Hải Nam Trung Bộ', N'17 Thái Nguyên, TP. Nha Trang'),
('TC', N'Ga Tháp Chàm', N'Ninh Thuận', N'Miền Duyên Hải Nam Trung Bộ', N'Thị trấn Tháp Chàm, Ninh Thuận'),
('BTH', N'Ga Bình Thuận', N'Bình Thuận', N'Miền Duyên Hải Nam Trung Bộ', N'Mường Mán, Hàm Thuận Nam, Bình Thuận'), -- Hub for Phan Thiet
('LSO', N'Ga Long Khánh', N'Đồng Nai', N'Miền Đông Nam Bộ', N'TP. Long Khánh, Đồng Nai'),
('BHO', N'Ga Biên Hòa', N'Đồng Nai', N'Miền Đông Nam Bộ', N'Hưng Đạo Vương, TP. Biên Hòa'),
('SG', N'Ga Sài Gòn', N'TP. Hồ Chí Minh', N'Miền Đông Nam Bộ', N'01 Nguyễn Thông, Phường 9, Quận 3, TP. HCM');

-- Batch 2: Stations on specific lines (e.g., Hanoi - Lao Cai, Hanoi - Hai Phong)
INSERT INTO Stations (StationCode, StationName, City, Region, Address) VALUES
('YBA', N'Ga Yên Bái', N'Yên Bái', N'Miền Bắc', N'TP. Yên Bái'),
('PHO', N'Ga Phố Lu', N'Lào Cai', N'Miền Bắc', N'Thị trấn Phố Lu, Bảo Thắng, Lào Cai'),
('LC', N'Ga Lào Cai', N'Lào Cai', N'Miền Bắc', N'Khánh Yên, TP. Lào Cai'),
('HP', N'Ga Hải Phòng', N'Hải Phòng', N'Miền Bắc', N'Lương Khánh Thiện, Ngô Quyền, Hải Phòng'),
('HDG', N'Ga Hải Dương', N'Hải Dương', N'Miền Bắc', N'Hồng Quang, TP. Hải Dương'),
('VTR', N'Ga Việt Trì', N'Phú Thọ', N'Miền Bắc', N'Đường Hùng Vương, TP. Việt Trì');

-- Batch 3: Some smaller or less common stations for route variety
INSERT INTO Stations (StationCode, StationName, City, Region, Address) VALUES
('BGY', N'Ga Bắc Giang', N'Bắc Giang', N'Miền Bắc', N'Xương Giang, TP. Bắc Giang'),
('LSN', N'Ga Lạng Sơn', N'Lạng Sơn', N'Miền Bắc', N'TP. Lạng Sơn'),
('CMH', N'Ga Cà Mau', N'Cà Mau', N'Miền Tây Nam Bộ', N'Lý Thường Kiệt, Phường 6, TP.Cà Mau'), -- Assuming hypothetical extension
('CTH', N'Ga Cần Thơ', N'Cần Thơ', N'Miền Tây Nam Bộ', N'Quận Ninh Kiều, TP.Cần Thơ'), -- Assuming hypothetical extension
('PHT', N'Ga Phan Thiết', N'Bình Thuận', N'Miền Duyên Hải Nam Trung Bộ', N'Lê Duẩn, TP. Phan Thiết'), -- Actual city station, often reached from Ga Bình Thuận
('DAL', N'Ga Đà Lạt', N'Lâm Đồng', N'Tây Nguyên', N'Quang Trung, Phường 9, TP. Đà Lạt'), -- Tourist/Historical
('QNH', N'Ga Quy Nhơn', N'Bình Định', N'Miền Duyên Hải Nam Trung Bộ', N'Lê Hồng Phong, TP. Quy Nhơn'); -- Actual city station, often reached from Ga Diêu Trì

-- Batch 4: More stations to ensure good coverage and testing possibilities
INSERT INTO Stations (StationCode, StationName, City, Region, Address) VALUES
(N'PYE', N'Ga Phủ Lý', N'Hà Nam', N'Miền Bắc', N'TP. Phủ Lý, Hà Nam'),
(N'BHA', N'Ga Bỉm Sơn', N'Thanh Hóa', N'Miền Bắc Trung Bộ', N'Thị xã Bỉm Sơn, Thanh Hóa'),
(N'YCH', N'Ga Yên Trung', N'Hà Tĩnh', N'Miền Bắc Trung Bộ', N'Đức Thọ, Hà Tĩnh'), -- Smaller station
(N'QNA', N'Ga Núi Thành', N'Quảng Nam', N'Miền Duyên Hải Nam Trung Bộ', N'Núi Thành, Quảng Nam'),
(N'SON', N'Ga Sông Mao', N'Bình Thuận', N'Miền Duyên Hải Nam Trung Bộ', N'Bắc Bình, Bình Thuận'),
(N'MLY', N'Ga Mẫu山', N'Lạng Sơn', N'Miền Bắc', N'Huyện Lộc Bình, Lạng Sơn'), -- Hypothetical for mountainous region
(N'HGI', N'Ga Hà Giang', N'Hà Giang', N'Miền Bắc', N'TP. Hà Giang, Hà Giang'), -- Hypothetical
(N'CBA', N'Ga Cao Bằng', N'Cao Bằng', N'Miền Bắc', N'TP. Cao Bằng, Cao Bằng'); -- Hypothetical

PRINT 'Finished inserting abundant data into Stations. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' (for the last batch, total should be sum of batches).';
GO

-- =============================================================================
-- Sample Data Insertion for Table: Routes
-- =============================================================================

PRINT 'Inserting abundant data into Routes...';

INSERT INTO Routes (RouteName, Description) VALUES
(N'Tuyến Thống Nhất (Hà Nội - Sài Gòn)', N'Tuyến đường sắt trục chính Bắc - Nam của Việt Nam, kết nối Hà Nội và TP. Hồ Chí Minh.'),
(N'Tuyến Thống Nhất (Sài Gòn - Hà Nội)', N'Chiều ngược lại của tuyến đường sắt trục chính Bắc - Nam.'),
(N'Tuyến Hà Nội - Lào Cai', N'Tuyến đường sắt quan trọng kết nối thủ đô với tỉnh Lào Cai, cửa ngõ Sapa và giao thương với Trung Quốc.'),
(N'Tuyến Lào Cai - Hà Nội', N'Chiều ngược lại của tuyến Hà Nội - Lào Cai.'),
(N'Tuyến Hà Nội - Hải Phòng', N'Tuyến đường sắt kết nối Hà Nội với thành phố cảng Hải Phòng.'),
(N'Tuyến Hải Phòng - Hà Nội', N'Chiều ngược lại của tuyến Hà Nội - Hải Phòng.'),
(N'Tuyến Hà Nội - Đồng Đăng (Lạng Sơn)', N'Tuyến đường sắt kết nối Hà Nội với Lạng Sơn, cửa khẩu Đồng Đăng.'),
(N'Tuyến Đồng Đăng (Lạng Sơn) - Hà Nội', N'Chiều ngược lại của tuyến Hà Nội - Đồng Đăng.'),
(N'Tuyến Đà Nẵng - Sài Gòn', N'Phân đoạn quan trọng của tuyến Thống Nhất, từ Đà Nẵng vào TP. Hồ Chí Minh.'),
(N'Tuyến Sài Gòn - Đà Nẵng', N'Chiều ngược lại của tuyến Đà Nẵng - Sài Gòn.'),
(N'Tuyến Nha Trang - Sài Gòn', N'Phân đoạn từ thành phố biển Nha Trang vào TP. Hồ Chí Minh.'),
(N'Tuyến Sài Gòn - Nha Trang', N'Chiều ngược lại của tuyến Nha Trang - Sài Gòn.'),
(N'Tuyến Vinh - Sài Gòn', N'Kết nối từ thành phố Vinh (Nghệ An) vào TP. Hồ Chí Minh.'),
(N'Tuyến Sài Gòn - Vinh', N'Chiều ngược lại của tuyến Vinh - Sài Gòn.'),
(N'Tuyến Hà Nội - Vinh', N'Tuyến đường sắt từ Hà Nội đến thành phố Vinh.'),
(N'Tuyến Vinh - Hà Nội', N'Chiều ngược lại của tuyến Hà Nội - Vinh.'),
(N'Tuyến Sài Gòn - Phan Thiết (qua Ga Bình Thuận)', N'Tuyến du lịch đến Phan Thiết, thường dừng ở Ga Bình Thuận rồi trung chuyển.'),
(N'Tuyến Phan Thiết (qua Ga Bình Thuận) - Sài Gòn', N'Chiều ngược lại của tuyến Sài Gòn - Phan Thiết.'),
(N'Tuyến Sài Gòn - Quy Nhơn (qua Ga Diêu Trì)', N'Tuyến du lịch đến Quy Nhơn, thường dừng ở Ga Diêu Trì rồi trung chuyển.'),
(N'Tuyến Quy Nhơn (qua Ga Diêu Trì) - Sài Gòn', N'Chiều ngược lại của tuyến Sài Gòn - Quy Nhơn.'),
(N'Tuyến Du lịch Đà Lạt (Ga Đà Lạt - Trại Mát)', N'Tuyến đường sắt răng cưa lịch sử, phục vụ du lịch ngắn.'),
(N'Tuyến Hà Nội - Quán Triều (Thái Nguyên)', N'Tuyến đường sắt kết nối Hà Nội với Thái Nguyên.'),
(N'Tuyến Quán Triều (Thái Nguyên) - Hà Nội', N'Chiều ngược lại của tuyến Hà Nội - Quán Triều.'),
(N'Tuyến Yên Viên - Hạ Long', N'Tuyến đường sắt du lịch đến Hạ Long (đang được nâng cấp/xây dựng lại).'),
(N'Tuyến vành đai Hà Nội (Thử nghiệm)', N'Tuyến đường sắt đô thị hoặc vành đai quanh Hà Nội (mang tính giả định cho mục đích thử nghiệm).');

PRINT 'Finished inserting abundant data into Routes. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO


-- =============================================================================
-- Sample Data Insertion for Table: RouteStations
-- =============================================================================

PRINT 'Inserting abundant data into RouteStations...';

-- !! IMPORTANT !!
-- The StationIDs used below are based on the INSERT order from the Stations sample data.
-- HN=1, NDH=2, NBH=3, THO=4, VIN=5, DHO=6, DDH=7, HUE=8, DN=9, TAM=10,
-- QN=11, DIE=12, TUH=13, NT=14, TC=15, BTH=16, LSO=17, BHO=18, SG=19
-- YBA=20, PHO=21, LC=22, HP=23, HDG=24, VTR=25
-- BGY=26, LSN=27, CMH=28(H), CTH=29(H), PHT=30, DAL=31, QNH=32
-- PYE=33, BHA=34, YCH=35, QNA=36, SON=37, MLY=38(H), HGI=39(H), CBA=40(H)

-- RouteID 1: Tuyến Thống Nhất (Hà Nội - Sài Gòn)
-- HN (1) -> NDH (2) -> NBH (3) -> THO (4) -> VIN (5) -> DHO (6) -> DDH (7) -> HUE (8) -> DN (9) -> TAM (10) -> QN (11) -> DIE (12) -> TUH (13) -> NT (14) -> TC (15) -> BTH (16) -> LSO (17) -> BHO (18) -> SG (19)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(1, 1, 1, 0, 20),        -- Hà Nội
(1, 33, 2, 57, 5),       -- Phủ Lý (PYE)
(1, 2, 3, 87, 10),       -- Nam Định (NDH)
(1, 3, 4, 115, 5),       -- Ninh Bình (NBH)
(1, 34, 5, 155, 5),      -- Bỉm Sơn (BHA)
(1, 4, 6, 175, 15),      -- Thanh Hóa (THO)
(1, 5, 7, 319, 20),      -- Vinh (VIN)
(1, 35, 8, 405, 3),      -- Yên Trung (YCH) - Small stop
(1, 6, 9, 522, 10),      -- Đồng Hới (DHO)
(1, 7, 10, 622, 10),     -- Đông Hà (DDH)
(1, 8, 11, 688, 15),     -- Huế (HUE)
(1, 9, 12, 791, 25),     -- Đà Nẵng (DN)
(1, 10, 13, 865, 5),     -- Tam Kỳ (TAM)
(1, 36, 14, 890, 3),     -- Núi Thành (QNA) - Small stop
(1, 11, 15, 928, 10),    -- Quảng Ngãi (QN)
(1, 12, 16, 1096, 15),   -- Diêu Trì (DIE)
(1, 13, 17, 1198, 5),    -- Tuy Hòa (TUH)
(1, 14, 18, 1315, 20),   -- Nha Trang (NT)
(1, 15, 19, 1408, 10),   -- Tháp Chàm (TC)
(1, 37, 20, 1480, 3),    -- Sông Mao (SON) - Small stop
(1, 16, 21, 1551, 15),   -- Bình Thuận (BTH)
(1, 17, 22, 1649, 5),    -- Long Khánh (LSO)
(1, 18, 23, 1697, 10),   -- Biên Hòa (BHO)
(1, 19, 24, 1726, 30);   -- Sài Gòn (SG)

-- RouteID 2: Tuyến Thống Nhất (Sài Gòn - Hà Nội) - Reverse of RouteID 1
-- SG (19) -> BHO (18) -> LSO (17) -> BTH (16) -> SON (37) -> TC (15) -> NT (14) -> TUH (13) -> DIE (12) -> QN (11) -> QNA (36) -> TAM (10) -> DN (9) -> HUE (8) -> DDH (7) -> DHO (6) -> YCH (35) -> VIN (5) -> THO (4) -> BHA (34) -> NBH (3) -> NDH (2) -> PYE (33) -> HN (1)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(2, 19, 1, 0, 30),       -- Sài Gòn (SG)
(2, 18, 2, 29, 10),      -- Biên Hòa (BHO)
(2, 17, 3, 77, 5),       -- Long Khánh (LSO)
(2, 16, 4, 175, 15),     -- Bình Thuận (BTH)
(2, 37, 5, 246, 3),      -- Sông Mao (SON)
(2, 15, 6, 318, 10),     -- Tháp Chàm (TC)
(2, 14, 7, 411, 20),     -- Nha Trang (NT)
(2, 13, 8, 528, 5),      -- Tuy Hòa (TUH)
(2, 12, 9, 630, 15),     -- Diêu Trì (DIE)
(2, 11, 10, 798, 10),    -- Quảng Ngãi (QN)
(2, 36, 11, 836, 3),     -- Núi Thành (QNA)
(2, 10, 12, 861, 5),     -- Tam Kỳ (TAM)
(2, 9, 13, 935, 25),     -- Đà Nẵng (DN)
(2, 8, 14, 1038, 15),    -- Huế (HUE)
(2, 7, 15, 1104, 10),    -- Đông Hà (DDH)
(2, 6, 16, 1204, 10),    -- Đồng Hới (DHO)
(2, 35, 17, 1321, 3),    -- Yên Trung (YCH)
(2, 5, 18, 1407, 20),    -- Vinh (VIN)
(2, 4, 19, 1551, 15),    -- Thanh Hóa (THO)
(2, 34, 20, 1571, 5),    -- Bỉm Sơn (BHA)
(2, 3, 21, 1611, 5),     -- Ninh Bình (NBH)
(2, 2, 22, 1639, 10),    -- Nam Định (NDH)
(2, 33, 23, 1669, 5),    -- Phủ Lý (PYE)
(2, 1, 24, 1726, 20);    -- Hà Nội (HN)

-- RouteID 3: Tuyến Hà Nội - Lào Cai
-- HN (1) -> VTR (25) -> YBA (20) -> PHO (21) -> LC (22)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(3, 1, 1, 0, 15),        -- Hà Nội
(3, 25, 2, 60, 10),      -- Việt Trì
(3, 20, 3, 155, 10),     -- Yên Bái
(3, 21, 4, 260, 5),      -- Phố Lu
(3, 22, 5, 296, 20);     -- Lào Cai

-- RouteID 4: Tuyến Lào Cai - Hà Nội - Reverse of RouteID 3
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(4, 22, 1, 0, 20),       -- Lào Cai
(4, 21, 2, 36, 5),       -- Phố Lu
(4, 20, 3, 141, 10),     -- Yên Bái
(4, 25, 4, 236, 10),     -- Việt Trì
(4, 1, 5, 296, 15);      -- Hà Nội

-- RouteID 5: Tuyến Hà Nội - Hải Phòng
-- HN (1) -> HDG (24) -> HP (23)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(5, 1, 1, 0, 10),        -- Hà Nội
(5, 24, 2, 57, 10),      -- Hải Dương
(5, 23, 3, 102, 15);     -- Hải Phòng

-- RouteID 6: Tuyến Hải Phòng - Hà Nội - Reverse of RouteID 5
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(6, 23, 1, 0, 15),       -- Hải Phòng
(6, 24, 2, 45, 10),      -- Hải Dương
(6, 1, 3, 102, 10);      -- Hà Nội

-- RouteID 7: Tuyến Hà Nội - Đồng Đăng (Lạng Sơn)
-- HN (1) -> BGY (26) -> LSN (27) (Assuming Đồng Đăng is near LSN for this example)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(7, 1, 1, 0, 10),        -- Hà Nội
(7, 26, 2, 50, 5),       -- Bắc Giang
(7, 27, 3, 150, 15);     -- Lạng Sơn (representing Đồng Đăng area)

-- RouteID 9: Tuyến Đà Nẵng - Sài Gòn
-- DN (9) -> TAM (10) -> QN (11) -> DIE (12) -> TUH (13) -> NT (14) -> TC (15) -> BTH (16) -> LSO (17) -> BHO (18) -> SG (19)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(9, 9, 1, 0, 25),        -- Đà Nẵng (DN)
(9, 10, 2, 74, 5),       -- Tam Kỳ (TAM)
(9, 11, 3, 137, 10),     -- Quảng Ngãi (QN)
(9, 12, 4, 305, 15),     -- Diêu Trì (DIE)
(9, 13, 5, 407, 5),      -- Tuy Hòa (TUH)
(9, 14, 6, 524, 20),     -- Nha Trang (NT)
(9, 15, 7, 617, 10),     -- Tháp Chàm (TC)
(9, 16, 8, 760, 15),     -- Bình Thuận (BTH)
(9, 17, 9, 858, 5),      -- Long Khánh (LSO)
(9, 18, 10, 906, 10),    -- Biên Hòa (BHO)
(9, 19, 11, 935, 30);    -- Sài Gòn (SG)

-- RouteID 15: Tuyến Hà Nội - Vinh
-- HN (1) -> PYE (33) -> NDH (2) -> NBH (3) -> THO (4) -> VIN (5)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(15, 1, 1, 0, 10),       -- Hà Nội
(15, 33, 2, 57, 5),      -- Phủ Lý
(15, 2, 3, 87, 5),       -- Nam Định
(15, 3, 4, 115, 5),      -- Ninh Bình
(15, 4, 5, 175, 10),     -- Thanh Hóa
(15, 5, 6, 319, 15);     -- Vinh

-- RouteID 17: Tuyến Sài Gòn - Phan Thiết (qua Ga Bình Thuận)
-- SG (19) -> BHO (18) -> BTH (16) -> PHT (30) (Assume direct connection from BTH to PHT for this route)
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(17, 19, 1, 0, 15),      -- Sài Gòn
(17, 18, 2, 29, 5),      -- Biên Hòa
(17, 16, 3, 175, 10),    -- Ga Bình Thuận (Hub)
(17, 30, 4, 195, 20);    -- Ga Phan Thiết (City) - Distance from Ga Binh Thuan approx 20km

-- RouteID 21: Tuyến Du lịch Đà Lạt (Ga Đà Lạt - Trại Mát)
-- DAL (31) -> Trai Mat (Assume a hypothetical Trai Mat Station ID, let's use a high non-conflicting one like 99 if not created, or create it)
-- For this example, let's assume there's another station just for this route
-- IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'TMA')
--    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('TMA', N'Ga Trại Mát', N'Lâm Đồng', N'Tây Nguyên');
-- DECLARE @TraiMatStationID INT = (SELECT StationID FROM Stations WHERE StationCode = 'TMA'); -- Use this if you add Trai Mat
-- For now, let's just make a short route to show it exists with just Đà Lạt for simplicity if Trai Mat isn't in the main list
INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES
(21, 31, 1, 0, 60);      -- Ga Đà Lạt (DAL). Let's say it's a round trip or a single station display for this example.

PRINT 'Finished inserting abundant data into RouteStations. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' (for the last batch, total should be sum of batches).';
GO

-- =============================================================================
-- Sample Data Insertion for Table: TrainTypes
-- =============================================================================

PRINT 'Inserting abundant data into TrainTypes...';

INSERT INTO TrainTypes (TypeName, Description) VALUES
(N'Tàu Thống Nhất (SE)', N'Tàu khách nhanh, chất lượng cao, thường chạy các tuyến đường dài như Bắc - Nam. Ký hiệu tàu thường bắt đầu bằng SE (Special Express).'),
(N'Tàu Thống Nhất (TN)', N'Tàu khách thường, chạy tuyến Bắc - Nam, có thể dừng ở nhiều ga hơn và thời gian hành trình dài hơn SE. Ký hiệu tàu thường bắt đầu bằng TN.'),
(N'Tàu Khu vực Nhanh (SPT, SNT, PT)', N'Tàu nhanh chạy các tuyến khu vực, ví dụ Sài Gòn - Phan Thiết (SPT), Sài Gòn - Nha Trang (SNT).'),
(N'Tàu Khu vực Chậm (LC, QB, HP)', N'Tàu chạy các tuyến khu vực hoặc địa phương, thường có nhiều điểm dừng, ví dụ Hà Nội - Lào Cai (LC), Hà Nội - Hải Phòng (HP).'),
(N'Tàu Du lịch Cao cấp (Victoria, Livitrans)', N'Các đoàn tàu được đầu tư đặc biệt cho phân khúc khách du lịch cao cấp, thường nối toa vào các tàu Thống Nhất hoặc chạy riêng trên một số tuyến du lịch trọng điểm.'),
(N'Tàu Hàng Hóa', N'Tàu chuyên dụng chở hàng hóa, không phục vụ hành khách.'),
(N'Tàu Công Vụ / Chuyên Dụng', N'Tàu phục vụ các mục đích đặc biệt của ngành đường sắt như kiểm tra, sửa chữa đường ray, hoặc các nhiệm vụ công vụ khác.');

PRINT 'Finished inserting abundant data into TrainTypes. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO


-- =============================================================================
-- Sample Data Insertion for Table: Trains
-- =============================================================================

PRINT 'Inserting abundant data into Trains...';

-- TrainTypeID mapping (based on above inserts):
-- 1: Tàu Thống Nhất (SE)
-- 2: Tàu Thống Nhất (TN)
-- 3: Tàu Khu vực Nhanh (SPT, SNT, PT)
-- 4: Tàu Khu vực Chậm (LC, QB, HP)
-- 5: Tàu Du lịch Cao cấp

INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES
-- Tàu SE (Thống Nhất nhanh)
('SE1', 1, 1), ('SE2', 1, 1), ('SE3', 1, 1), ('SE4', 1, 1),
('SE5', 1, 1), ('SE6', 1, 1), ('SE7', 1, 1), ('SE8', 1, 1),
('SE9', 1, 0), ('SE10', 1, 1), -- SE9 tạm ngưng
('SE11', 1, 1), ('SE12', 1, 1),
('SE19', 1, 1), ('SE20', 1, 1),
('SE21', 1, 1), ('SE22', 1, 1),
-- Tàu TN (Thống Nhất thường)
('TN1', 2, 1), ('TN2', 2, 1),
('TN3', 2, 0), ('TN4', 2, 1), -- TN3 tạm ngưng
-- Tàu Khu vực Nhanh
('SPT1', 3, 1), ('SPT2', 3, 1), -- Sài Gòn - Phan Thiết
('SNT1', 3, 1), ('SNT2', 3, 1), -- Sài Gòn - Nha Trang
('PT3', 3, 1), ('PT4', 3, 1),   -- Phan Thiết - (ví dụ: Quy Nhơn hoặc điểm khác)
-- Tàu Khu vực Chậm
('LC1', 4, 1), ('LC2', 4, 1), ('LC3', 4, 1), ('LC4', 4, 0), -- Hà Nội - Lào Cai (LC3 tạm ngưng)
('HP1', 4, 1), ('HP2', 4, 1), -- Hà Nội - Hải Phòng
('QB1', 4, 1), ('QB2', 4, 1), -- Hà Nội - Đồng Hới (Quảng Bình)
('DD1', 4, 1), ('DD2', 4, 1), -- Hà Nội - Đồng Đăng
-- Tàu Du lịch Cao cấp
('Victoria Express HNLC', 5, 1), -- Nối vào tàu Hà Nội - Lào Cai
('Livitrans Express HNLC', 5, 1),  -- Nối vào tàu Hà Nội - Lào Cai
('Golden Train SGNQ', 5, 0);    -- Tàu Sài Gòn - Quy Nhơn (ví dụ, đang bảo trì)

PRINT 'Finished inserting abundant data into Trains. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO


-- =============================================================================
-- Sample Data Insertion for Table: CoachTypes
-- =============================================================================

PRINT 'Inserting abundant data into CoachTypes...';

INSERT INTO CoachTypes (TypeName, PriceMultiplier, Description) VALUES
(N'Ngồi cứng thường', 0.8, N'Ghế bằng gỗ hoặc nhựa cứng, không điều hòa. Giá rẻ nhất.'),
(N'Ngồi cứng điều hòa', 1.0, N'Ghế bằng gỗ hoặc nhựa cứng, có trang bị điều hòa không khí.'),
(N'Ngồi mềm thường', 1.1, N'Ghế có đệm mềm, không điều hòa.'),
(N'Ngồi mềm điều hòa', 1.25, N'Ghế có đệm mềm, có trang bị điều hòa không khí. Phổ biến trên nhiều tàu.'),
(N'Ngồi mềm điều hòa (Cải tiến)', 1.35, N'Phiên bản nâng cấp của ghế ngồi mềm điều hòa, tiện nghi hơn.'),
(N'Nằm cứng khoang 6 điều hòa', 1.5, N'Giường cứng 3 tầng trong khoang 6 giường, có điều hòa. Tầng 1, 2, 3 có thể có giá khác nhau chi tiết ở SeatTypes.'),
(N'Nằm mềm khoang 4 điều hòa', 2.0, N'Giường mềm 2 tầng trong khoang 4 giường, có điều hòa. Tầng 1, 2 có thể có giá khác nhau chi tiết ở SeatTypes.'),
(N'Nằm mềm khoang 4 VIP', 2.5, N'Giường mềm cao cấp trong khoang 4 giường, nội thất và dịch vụ tốt hơn.'),
(N'Nằm mềm khoang 2 VIP (Cabin riêng)', 3.5, N'Khoang riêng 2 giường mềm cao cấp, tiện nghi đầy đủ, thường trên các tàu du lịch.'),
(N'Toa ghế ngồi xem phim / giải trí', 1.4, N'Toa chuyên dụng có ghế ngồi thoải mái và màn hình lớn để giải trí.'),
(N'Toa hàng ăn (Dining Car)', 0.0, N'Toa phục vụ ăn uống, không bán vé ghế ngồi cố định ở đây.'),
(N'Toa hành lý', 0.0, N'Toa chuyên chở hành lý ký gửi, không có chỗ cho hành khách.'),
(N'Toa giường nằm khoang 6 thường', 1.2, N'Giường cứng 3 tầng trong khoang 6 giường, không điều hòa.');

PRINT 'Finished inserting abundant data into CoachTypes. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO

-- =============================================================================
-- Sample Data Insertion for Table: SeatTypes
-- =============================================================================

PRINT 'Inserting abundant data into SeatTypes...';

INSERT INTO SeatTypes (TypeName, PriceMultiplier, Description) VALUES
-- Ngồi
(N'Ghế Ngồi Cứng', 1.0, N'Ghế cơ bản, không đánh số cụ thể vị trí (cửa sổ/lối đi)'),
(N'Ghế Ngồi Mềm', 1.0, N'Ghế cơ bản, không đánh số cụ thể vị trí (cửa sổ/lối đi)'), -- PriceMultiplier này sẽ được nhân với CoachType.PriceMultiplier
(N'Ghế Ngồi Mềm (Cửa sổ)', 1.05, N'Ghế mềm gần cửa sổ, có thể có phụ thu nhỏ hoặc là lựa chọn ưu tiên.'),
(N'Ghế Ngồi Mềm (Lối đi)', 1.0, N'Ghế mềm gần lối đi.'),

-- Nằm Khoang 6 (Giá gốc từ CoachType, đây là hệ số điều chỉnh thêm cho vị trí tầng)
(N'Giường Khoang 6 Tầng 1 (Dưới)', 1.1, N'Tầng dưới cùng trong khoang 6 giường, thuận tiện nhất.'),
(N'Giường Khoang 6 Tầng 2 (Giữa)', 1.0, N'Tầng giữa trong khoang 6 giường, giá tiêu chuẩn cho khoang 6.'),
(N'Giường Khoang 6 Tầng 3 (Trên)', 0.9, N'Tầng trên cùng trong khoang 6 giường, giá rẻ hơn một chút.'),

-- Nằm Khoang 4 (Giá gốc từ CoachType, đây là hệ số điều chỉnh thêm cho vị trí tầng)
(N'Giường Khoang 4 Tầng 1 (Dưới)', 1.05, N'Tầng dưới cùng trong khoang 4 giường, rộng rãi và tiện lợi.'),
(N'Giường Khoang 4 Tầng 2 (Trên)', 1.0, N'Tầng trên trong khoang 4 giường.'),

-- Nằm Khoang 2 VIP
(N'Giường Khoang 2 VIP', 1.0, N'Giường trong khoang riêng 2 người.'),

-- Đặc biệt
(N'Ghế Salon (Toa giải trí)', 1.0, N'Ghế trong toa xem phim hoặc phòng giải trí chung.'),
(N'Ghế Phụ', 0.7, N'Ghế nhựa gấp tạm thời, giá rẻ, chỉ mở bán khi hết vé chính thức.');

PRINT 'Finished inserting abundant data into SeatTypes. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO

-- =============================================================================
-- Sample Data Insertion for Table: Coaches
-- =============================================================================

PRINT 'Inserting abundant data into Coaches...';

-- !! IMPORTANT !!
-- TrainID mapping (based on previous Train inserts):
-- SE1=1, SE2=2, SE3=3, SE4=4, SE5=5, SE6=6, SE7=7, SE8=8, SE10=9, ...
-- TN1=13, TN2=14
-- SPT1=15, SPT2=16
-- SNT1=17, SNT2=18
-- LC1=21, LC2=22, LC3=23
-- HP1=25, HP2=26
-- Victoria Express HNLC = 29
-- Livitrans Express HNLC = 30

-- CoachTypeID mapping (based on previous CoachType inserts):
-- 1: Ngồi cứng thường
-- 2: Ngồi cứng điều hòa
-- 3: Ngồi mềm thường
-- 4: Ngồi mềm điều hòa
-- 5: Ngồi mềm điều hòa (Cải tiến)
-- 6: Nằm cứng khoang 6 điều hòa
-- 7: Nằm mềm khoang 4 điều hòa
-- 8: Nằm mềm khoang 4 VIP
-- 9: Nằm mềm khoang 2 VIP (Cabin riêng)
-- 10: Toa ghế ngồi xem phim / giải trí
-- 11: Toa hàng ăn (Dining Car)
-- 12: Toa hành lý
-- 13: Toa giường nằm khoang 6 thường

-- === Train SE1 (TrainID = 1) ===
-- Typical configuration: Baggage, Soft Seats, Soft Seats, Soft Sleeper K4, Soft Sleeper K4, Hard Sleeper K6, Hard Sleeper K6, Dining Car
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(1, 1, N'Toa HL1', 12, 0, 1),         -- Toa hành lý (HL - Hàng Lý)
(1, 2, N'Toa A2', 4, 56, 2),          -- Ngồi mềm điều hòa
(1, 3, N'Toa A3', 4, 56, 3),          -- Ngồi mềm điều hòa
(1, 4, N'Toa A4', 5, 56, 4),          -- Ngồi mềm điều hòa (Cải tiến)
(1, 5, N'Toa An5 (K4)', 7, 28, 5),    -- Nằm mềm khoang 4 ĐH (An - giường nằm)
(1, 6, N'Toa An6 (K4)', 7, 28, 6),    -- Nằm mềm khoang 4 ĐH
(1, 7, N'Toa Bn7 (K6)', 6, 42, 7),    -- Nằm cứng khoang 6 ĐH (Bn - giường nằm)
(1, 8, N'Toa Bn8 (K6)', 6, 42, 8),    -- Nằm cứng khoang 6 ĐH
(1, 9, N'Toa HĐ9', 11, 0, 9);         -- Toa hàng ăn (HĐ - Hàng ăn/Dining)

-- === Train SE2 (TrainID = 2, reverse of SE1 usually has similar composition) ===
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(2, 1, N'Toa HL1', 12, 0, 1),
(2, 2, N'Toa A2', 4, 56, 2),
(2, 3, N'Toa A3', 4, 56, 3),
(2, 4, N'Toa An4 (K4)', 7, 28, 4),
(2, 5, N'Toa An5 (K4)', 7, 28, 5),
(2, 6, N'Toa Bn6 (K6)', 6, 42, 6),
(2, 7, N'Toa Bn7 (K6)', 6, 42, 7),
(2, 8, N'Toa HĐ8', 11, 0, 8);

-- === Train TN1 (TrainID = 13, Thống Nhất Thường) ===
-- Typically more hard seaters and hard sleepers
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(13, 1, N'Toa HL1', 12, 0, 1),
(13, 2, N'Toa B2', 2, 80, 2),         -- Ngồi cứng điều hòa
(13, 3, N'Toa B3', 2, 80, 3),         -- Ngồi cứng điều hòa
(13, 4, N'Toa A4', 4, 56, 4),         -- Ngồi mềm điều hòa
(13, 5, N'Toa Bn5 (K6)', 6, 42, 5),   -- Nằm cứng khoang 6 ĐH
(13, 6, N'Toa Bn6 (K6)', 6, 42, 6),   -- Nằm cứng khoang 6 ĐH
(13, 7, N'Toa Bn7 (K6)', 13, 42, 7),  -- Nằm cứng khoang 6 Thường (không ĐH)
(13, 8, N'Toa HĐ8', 11, 0, 8);

-- === Train SPT1 (TrainID = 15, Sài Gòn - Phan Thiết) ===
-- Mostly soft seaters
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(15, 1, N'Toa A1', 4, 56, 1),
(15, 2, N'Toa A2', 4, 56, 2),
(15, 3, N'Toa A3', 5, 56, 3),         -- Ngồi mềm ĐH (Cải tiến)
(15, 4, N'Toa A4', 5, 56, 4),
(15, 5, N'Toa Ax5', 10, 30, 5);       -- Toa giải trí (Ax - Auxiliary)

-- === Train LC3 (TrainID = 23, Hà Nội - Lào Cai) ===
-- Mix of seaters and sleepers
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(23, 1, N'Toa A1', 4, 56, 1),         -- Ngồi mềm ĐH
(23, 2, N'Toa A2', 4, 56, 2),
(23, 3, N'Toa Bn3 (K6)', 6, 42, 3),   -- Nằm cứng khoang 6 ĐH
(23, 4, N'Toa An4 (K4)', 7, 28, 4);   -- Nằm mềm khoang 4 ĐH

-- === Train Victoria Express HNLC (TrainID = 29) ===
-- High-end sleeper coaches
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(29, 1, N'Victoria S1(K4)', 8, 28, 1),  -- Nằm mềm khoang 4 VIP
(29, 2, N'Victoria S2(K2)', 9, 14, 2),  -- Nằm mềm khoang 2 VIP (7 cabins * 2 berths)
(29, 3, N'Victoria Dining', 11, 0, 3); -- Toa hàng ăn riêng

-- === Train HP1 (TrainID = 25, Hà Nội - Hải Phòng) ===
-- Mostly seaters
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(25, 1, N'Toa A1', 4, 60, 1), -- Ngồi mềm ĐH (có thể sức chứa khác 1 chút)
(25, 2, N'Toa A2', 4, 60, 2),
(25, 3, N'Toa B3', 2, 80, 3), -- Ngồi cứng ĐH
(25, 4, N'Toa B4', 2, 80, 4);

-- Adding a few more coaches for variety on existing trains
-- SE3 (TrainID = 3) - Slightly different config from SE1
INSERT INTO Coaches (TrainID, CoachNumber, CoachName, CoachTypeID, Capacity, PositionInTrain) VALUES
(3, 1, N'Toa HL1', 12, 0, 1),
(3, 2, N'Toa A2', 5, 56, 2),          -- Ngồi mềm ĐH (Cải tiến)
(3, 3, N'Toa A3', 5, 56, 3),
(3, 4, N'Toa An4 (K4)', 8, 28, 4),    -- Nằm mềm khoang 4 VIP
(3, 5, N'Toa An5 (K4)', 7, 28, 5),    -- Nằm mềm khoang 4 ĐH
(3, 6, N'Toa Bn6 (K6)', 6, 42, 6),
(3, 7, N'Toa HĐ7', 11, 0, 7);

PRINT 'Finished inserting abundant data into Coaches. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + ' (for the last batch, total should be sum of batches).';
GO


-- =============================================================================
-- Sample Data Insertion for Table: Seats
-- =============================================================================

PRINT 'Inserting abundant data into Seats...';

-- SeatTypeID mapping (based on previous SeatType inserts):
-- 1: Ghế Ngồi Cứng
-- 2: Ghế Ngồi Mềm
-- 3: Ghế Ngồi Mềm (Cửa sổ)
-- 4: Ghế Ngồi Mềm (Lối đi)
-- 5: Giường Khoang 6 Tầng 1 (Dưới)
-- 6: Giường Khoang 6 Tầng 2 (Giữa)
-- 7: Giường Khoang 6 Tầng 3 (Trên)
-- 8: Giường Khoang 4 Tầng 1 (Dưới)
-- 9: Giường Khoang 4 Tầng 2 (Trên)
-- 10: Giường Khoang 2 VIP
-- 11: Ghế Salon (Toa giải trí)
-- 12: Ghế Phụ

-- Helper variables
DECLARE @CurrentCoachID INT;
DECLARE @SeatCounter INT;
DECLARE @CurrentCapacity INT;
DECLARE @DefaultSeatTypeID INT;
DECLARE @SeatNamePrefix NVARCHAR(10);
DECLARE @SeatNameSuffix NVARCHAR(10);

-- === Seats for Coaches of Train SE1 (TrainID = 1) ===
-- CoachID 1 (Toa HL1 on SE1) - Hành lý, Capacity 0 - No seats

-- CoachID 2 (Toa A2 on SE1, Ngồi mềm điều hòa, Capacity 56, CoachTypeID 4)
-- Assuming SeatTypeID 2 (Ghế Ngồi Mềm) for all. Could alternate Window/Aisle.
SET @CurrentCoachID = (SELECT CoachID FROM Coaches WHERE TrainID=1 AND CoachNumber=2); -- Toa A2 của SE1
SET @CurrentCapacity = (SELECT Capacity FROM Coaches WHERE CoachID=@CurrentCoachID);
SET @DefaultSeatTypeID = 2; -- Ghế Ngồi Mềm
SET @SeatCounter = 1;
WHILE @SeatCounter <= @CurrentCapacity
BEGIN
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled)
    VALUES (@CurrentCoachID, @SeatCounter, CAST(@SeatCounter AS NVARCHAR(10)), @DefaultSeatTypeID, 1);
    SET @SeatCounter = @SeatCounter + 1;
END;

-- CoachID 5 (Toa An5 (K4) on SE1, Nằm mềm khoang 4 ĐH, Capacity 28, CoachTypeID 7)
-- 28 berths -> 7 cabins. Each cabin has 2 lower (T1 - SeatTypeID 8) and 2 upper (T2 - SeatTypeID 9)
SET @CurrentCoachID = (SELECT CoachID FROM Coaches WHERE TrainID=1 AND CoachNumber=5); -- Toa An5 (K4) của SE1
SET @SeatCounter = 1;
DECLARE @CabinCounter INT = 1;
WHILE @CabinCounter <= 7 -- 7 cabins
BEGIN
    -- 2 Lower Berths
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T1', 8, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T1', 8, 1); SET @SeatCounter = @SeatCounter + 1;
    -- 2 Upper Berths
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T2', 9, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T2', 9, 1); SET @SeatCounter = @SeatCounter + 1;
    SET @CabinCounter = @CabinCounter + 1;
END;

-- CoachID 7 (Toa Bn7 (K6) on SE1, Nằm cứng khoang 6 ĐH, Capacity 42, CoachTypeID 6)
-- 42 berths -> 7 cabins. Each cabin has 2 T1 (SeatTypeID 5), 2 T2 (SeatTypeID 6), 2 T3 (SeatTypeID 7)
SET @CurrentCoachID = (SELECT CoachID FROM Coaches WHERE TrainID=1 AND CoachNumber=7); -- Toa Bn7 (K6) của SE1
SET @SeatCounter = 1;
SET @CabinCounter = 1;
WHILE @CabinCounter <= 7 -- 7 cabins
BEGIN
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T1', 5, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T1', 5, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T2', 6, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T2', 6, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T3', 7, 1); SET @SeatCounter = @SeatCounter + 1;
    INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'K'+CAST(@CabinCounter AS NVARCHAR(2))+N'G'+CAST(@SeatCounter AS NVARCHAR(2))+'T3', 7, 1); SET @SeatCounter = @SeatCounter + 1;
    SET @CabinCounter = @CabinCounter + 1;
END;


-- === Seats for Coaches of Train SPT1 (TrainID = 15) ===
-- CoachID for SPT1-A1 (Ngồi mềm ĐH, Capacity 56, CoachTypeID 4)
SET @CurrentCoachID = (SELECT CoachID FROM Coaches WHERE TrainID=15 AND CoachNumber=1); -- Toa A1 của SPT1
IF @CurrentCoachID IS NOT NULL
BEGIN
    SET @CurrentCapacity = (SELECT Capacity FROM Coaches WHERE CoachID=@CurrentCoachID);
    SET @DefaultSeatTypeID = 2; -- Ghế Ngồi Mềm
    SET @SeatCounter = 1;
    WHILE @SeatCounter <= @CurrentCapacity
    BEGIN
        -- Alternate Window (SeatTypeID 3) and Aisle (SeatTypeID 4) for variety if desired, or just use default
        DECLARE @CurrentIterSeatTypeID INT = @DefaultSeatTypeID;
        SET @SeatNameSuffix = '';
        IF @SeatCounter % 4 = 1 OR @SeatCounter % 4 = 0 BEGIN SET @CurrentIterSeatTypeID = 3; SET @SeatNameSuffix = 'W'; END -- Window
        IF @SeatCounter % 4 = 2 OR @SeatCounter % 4 = 3 BEGIN SET @CurrentIterSeatTypeID = 4; SET @SeatNameSuffix = 'A'; END -- Aisle

        INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled)
        VALUES (@CurrentCoachID, @SeatCounter, CAST(@SeatCounter AS NVARCHAR(2)) + @SeatNameSuffix, @CurrentIterSeatTypeID, 1);
        SET @SeatCounter = @SeatCounter + 1;
    END;
END

-- === Seats for Coaches of Train Victoria Express (TrainID = 29) ===
-- Coach for Victoria S2(K2) (Nằm mềm khoang 2 VIP, Capacity 14, CoachTypeID 9)
SET @CurrentCoachID = (SELECT CoachID FROM Coaches WHERE TrainID=29 AND CoachName LIKE 'Victoria S2(K2)%');
IF @CurrentCoachID IS NOT NULL
BEGIN
    SET @SeatCounter = 1;
    SET @CabinCounter = 1;
    WHILE @CabinCounter <= 7 -- 7 cabins * 2 berths
    BEGIN
        INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'Cabin'+CAST(@CabinCounter AS NVARCHAR(2))+'G1', 10, 1); SET @SeatCounter = @SeatCounter + 1;
        INSERT INTO Seats (CoachID, SeatNumber, SeatName, SeatTypeID, IsEnabled) VALUES (@CurrentCoachID, @SeatCounter, N'Cabin'+CAST(@CabinCounter AS NVARCHAR(2))+'G2', 10, 1); SET @SeatCounter = @SeatCounter + 1;
        SET @CabinCounter = @CabinCounter + 1;
    END;
END

PRINT 'Finished inserting abundant data into Seats. (Partial generation, expand as needed)';
GO


-- =============================================================================
-- Sample Data Insertion for Table: Trips
-- =============================================================================

PRINT 'Inserting data into Trips (approx. 10 trips in the next 10 days)...';

-- !! IMPORTANT !!
-- TrainID mapping (based on previous Train inserts):
-- SE1=1, SE2=2, SE3=3, SE4=4
-- TN1=13
-- SPT1=15
-- LC3=23
-- HP1=25
-- Victoria Express HNLC = 29

-- RouteID mapping (based on previous Route inserts):
-- 1: Tuyến Thống Nhất (Hà Nội - Sài Gòn)
-- 2: Tuyến Thống Nhất (Sài Gòn - Hà Nội)
-- 3: Tuyến Hà Nội - Lào Cai
-- 5: Tuyến Hà Nội - Hải Phòng
-- 9: Tuyến Đà Nẵng - Sài Gòn
-- 15: Tuyến Hà Nội - Vinh
-- 17: Tuyến Sài Gòn - Phan Thiết (qua Ga Bình Thuận)

DECLARE @Today DATETIME2 = GETDATE();

INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, IsHolidayTrip, TripStatus, BasePriceMultiplier) VALUES
-- Chuyến 1: SE1 (HN-SG), khởi hành ngày mai
(1, 1, DATEADD(hour, 19, DATEADD(day, 1, @Today)), DATEADD(hour, 5, DATEADD(day, 3, @Today)), 0, 'Scheduled', 1.0),
-- Chuyến 2: SE2 (SG-HN), khởi hành ngày mai
(2, 2, DATEADD(hour, 19, DATEADD(day, 1, @Today)), DATEADD(hour, 5, DATEADD(day, 3, @Today)), 0, 'Scheduled', 1.0),

-- Chuyến 3: SPT1 (SG-Phan Thiết), khởi hành trong 2 ngày tới (chuyến sáng)
(15, 17, DATEADD(hour, 6, DATEADD(day, 2, @Today)), DATEADD(hour, 10, DATEADD(day, 2, @Today)), 0, 'Scheduled', 1.1),
-- Chuyến 4: SPT1 (SG-Phan Thiết), khởi hành trong 3 ngày tới (chuyến chiều) - Giả sử có 2 chuyến SPT1
(15, 17, DATEADD(hour, 15, DATEADD(day, 3, @Today)), DATEADD(hour, 19, DATEADD(day, 3, @Today)), 0, 'Scheduled', 1.0),

-- Chuyến 5: LC3 (HN-Lào Cai), khởi hành trong 4 ngày tới (chuyến đêm)
(23, 3, DATEADD(hour, 22, DATEADD(day, 4, @Today)), DATEADD(hour, 6, DATEADD(day, 5, @Today)), 0, 'Scheduled', 1.05),
-- Chuyến 6: Victoria Express (HN-Lào Cai), khởi hành trong 4 ngày tới (nối theo LC3 hoặc tàu khác)
(29, 3, DATEADD(hour, 21, DATEADD(day, 4, @Today)), DATEADD(hour, 5, DATEADD(day, 5, @Today)), 1, 'Scheduled', 1.8), -- Giả sử là dịp lễ nhỏ

-- Chuyến 7: HP1 (HN-Hải Phòng), khởi hành trong 5 ngày tới (chuyến sáng sớm)
(25, 5, DATEADD(hour, 6, DATEADD(day, 5, @Today)), DATEADD(hour, 8, DATEADD(day, 5, @Today)), 0, 'Scheduled', 1.0),
-- Chuyến 8: HP1 (HN-Hải Phòng), khởi hành trong 5 ngày tới (chuyến trưa)
(25, 5, DATEADD(hour, 14, DATEADD(day, 5, @Today)), DATEADD(hour, 16, DATEADD(day, 5, @Today)), 0, 'Scheduled', 1.0),

-- Chuyến 9: SE3 (HN-SG), khởi hành trong 7 ngày tới, giá cao hơn một chút
(3, 1, DATEADD(hour, 22, DATEADD(day, 7, @Today)), DATEADD(hour, 8, DATEADD(day, 9, @Today)), 0, 'Scheduled', 1.15),

-- Chuyến 10: TN1 (HN-Vinh), khởi hành trong 8 ngày tới (tàu thường, tuyến ngắn)
(13, 15, DATEADD(hour, 9, DATEADD(day, 8, @Today)), DATEADD(hour, 17, DATEADD(day, 8, @Today)), 0, 'Scheduled', 0.95),

-- Chuyến 11 (Bonus): Một chuyến đã hoàn thành để test
(4, 2, DATEADD(hour, 19, DATEADD(day, -5, @Today)), DATEADD(hour, 5, DATEADD(day, -3, @Today)), 0, 'Completed', 1.0),
-- Chuyến 12 (Bonus): Một chuyến bị hủy
(1, 1, DATEADD(hour, 19, DATEADD(day, 6, @Today)), DATEADD(hour, 5, DATEADD(day, 8, @Today)), 0, 'Cancelled', 1.0);


PRINT 'Finished inserting data into Trips. Total records: ' + CAST(@@ROWCOUNT AS VARCHAR) + '.';
GO




-- =============================================================================
-- Manual Data Insertion for Table: TripStations (Full for TripID = 1: HN-SG)
-- =============================================================================

PRINT 'Manually inserting full TripStations for TripID = 1 (SE1 HN-SG)...';

-- BẠN CẦN ĐẢM BẢO TripID = 1 VÀ CÁC StationID DƯỚI ĐÂY KHỚP VỚI DATABASE CỦA BẠN
-- Lấy thời gian khởi hành và kết thúc thực tế của TripID = 1 từ bảng Trips
DECLARE @Trip1_Departure DATETIME2;
DECLARE @Trip1_Arrival_Final DATETIME2; -- Thời gian đến ga cuối cùng theo bảng Trips

SELECT @Trip1_Departure = DepartureDateTime, @Trip1_Arrival_Final = ArrivalDateTime
FROM Trips
WHERE TripID = 1; -- Giả sử đây là chuyến SE1 HN-SG của bạn

IF @Trip1_Departure IS NOT NULL AND @Trip1_Arrival_Final IS NOT NULL
BEGIN
    -- Ga Hà Nội (StationID=1, Seq=1)
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 1, 1, NULL, @Trip1_Departure);

    -- Ga Phủ Lý (StationID=33, Seq=2) - Ước tính đến sau 50 phút, dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 33, 2, DATEADD(minute, 50, @Trip1_Departure), DATEADD(minute, 50 + 5, @Trip1_Departure));

    -- Ga Nam Định (StationID=2, Seq=3) - Ước tính đến Phủ Lý + 40 phút, dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 2, 3, DATEADD(minute, 50 + 5 + 40, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10, @Trip1_Departure));

    -- Ga Ninh Bình (StationID=3, Seq=4) - Ước tính đến Nam Định + 30 phút, dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 3, 4, DATEADD(minute, 50 + 5 + 40 + 10 + 30, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5, @Trip1_Departure));

    -- Ga Bỉm Sơn (StationID=34, Seq=5) - Ước tính đến Ninh Bình + 35 phút, dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 34, 5, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5, @Trip1_Departure));

    -- Ga Thanh Hóa (StationID=4, Seq=6) - Ước tính đến Bỉm Sơn + 25 phút, dừng 15 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 4, 6, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15, @Trip1_Departure));

    -- Ga Vinh (StationID=5, Seq=7) - Ước tính đến Thanh Hóa + 145 phút (2h25m), dừng 20 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 5, 7, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20, @Trip1_Departure));

    -- Ga Yên Trung (StationID=35, Seq=8) - Ước tính đến Vinh + 80 phút (1h20m), dừng 3 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 35, 8, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3, @Trip1_Departure));

    -- Ga Đồng Hới (StationID=6, Seq=9) - Ước tính đến Yên Trung + 127 phút (2h07m), dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 6, 9, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10, @Trip1_Departure));

    -- Ga Đông Hà (StationID=7, Seq=10) - Ước tính đến Đồng Hới + 100 phút (1h40m), dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 7, 10, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100 + 10, @Trip1_Departure));

    -- Ga Huế (StationID=8, Seq=11) - Ước tính đến Đông Hà + 66 phút (1h06m), dừng 15 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 8, 11, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100 + 10 + 66, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100 + 10 + 66 + 15, @Trip1_Departure));

    -- Ga Đà Nẵng (StationID=9, Seq=12) - Ước tính đến Huế + 103 phút (1h43m), dừng 25 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 9, 12, DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100 + 10 + 66 + 15 + 103, @Trip1_Departure), DATEADD(minute, 50 + 5 + 40 + 10 + 30 + 5 + 35 + 5 + 25 + 15 + 145 + 20 + 80 + 3 + 127 + 10 + 100 + 10 + 66 + 15 + 103 + 25, @Trip1_Departure));

    -- Ga Tam Kỳ (StationID=10, Seq=13) - Ước tính đến Đà Nẵng + 74 phút (1h14m), dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 10, 13, DATEADD(minute, 1038 + 74, @Trip1_Departure), DATEADD(minute, 1038 + 74 + 5, @Trip1_Departure)); -- Tổng phút đến Đà Nẵng là 1038

    -- Ga Núi Thành (StationID=36, Seq=14) - Ước tính đến Tam Kỳ + 25 phút, dừng 3 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 36, 14, DATEADD(minute, 1038 + 74 + 5 + 25, @Trip1_Departure), DATEADD(minute, 1038 + 74 + 5 + 25 + 3, @Trip1_Departure));

    -- Ga Quảng Ngãi (StationID=11, Seq=15) - Ước tính đến Núi Thành + 35 phút, dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 11, 15, DATEADD(minute, 1038 + 74 + 5 + 25 + 3 + 35, @Trip1_Departure), DATEADD(minute, 1038 + 74 + 5 + 25 + 3 + 35 + 10, @Trip1_Departure));

    -- Ga Diêu Trì (StationID=12, Seq=16) - Ước tính đến Quảng Ngãi + 168 phút (2h48m), dừng 15 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 12, 16, DATEADD(minute, 1038 + 74 + 5 + 25 + 3 + 35 + 10 + 168, @Trip1_Departure), DATEADD(minute, 1038 + 74 + 5 + 25 + 3 + 35 + 10 + 168 + 15, @Trip1_Departure));

    -- Ga Tuy Hòa (StationID=13, Seq=17) - Ước tính đến Diêu Trì + 102 phút (1h42m), dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 13, 17, DATEADD(minute, 1361 + 102, @Trip1_Departure), DATEADD(minute, 1361 + 102 + 5, @Trip1_Departure)); -- Tổng phút đến Diêu Trì là 1361

    -- Ga Nha Trang (StationID=14, Seq=18) - Ước tính đến Tuy Hòa + 117 phút (1h57m), dừng 20 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 14, 18, DATEADD(minute, 1361 + 102 + 5 + 117, @Trip1_Departure), DATEADD(minute, 1361 + 102 + 5 + 117 + 20, @Trip1_Departure));

    -- Ga Tháp Chàm (StationID=15, Seq=19) - Ước tính đến Nha Trang + 93 phút (1h33m), dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 15, 19, DATEADD(minute, 1605 + 93, @Trip1_Departure), DATEADD(minute, 1605 + 93 + 10, @Trip1_Departure)); -- Tổng phút đến Nha Trang là 1605

    -- Ga Sông Mao (StationID=37, Seq=20) - Ước tính đến Tháp Chàm + 72 phút (1h12m), dừng 3 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 37, 20, DATEADD(minute, 1605 + 93 + 10 + 72, @Trip1_Departure), DATEADD(minute, 1605 + 93 + 10 + 72 + 3, @Trip1_Departure));

    -- Ga Bình Thuận (StationID=16, Seq=21) - Ước tính đến Sông Mao + 71 phút (1h11m), dừng 15 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 16, 21, DATEADD(minute, 1780 + 71, @Trip1_Departure), DATEADD(minute, 1780 + 71 + 15, @Trip1_Departure)); -- Tổng phút đến Sông Mao là 1780

    -- Ga Long Khánh (StationID=17, Seq=22) - Ước tính đến Bình Thuận + 98 phút (1h38m), dừng 5 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 17, 22, DATEADD(minute, 1866 + 98, @Trip1_Departure), DATEADD(minute, 1866 + 98 + 5, @Trip1_Departure)); -- Tổng phút đến Bình Thuận là 1866

    -- Ga Biên Hòa (StationID=18, Seq=23) - Ước tính đến Long Khánh + 48 phút, dừng 10 phút
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 18, 23, DATEADD(minute, 1866 + 98 + 5 + 48, @Trip1_Departure), DATEADD(minute, 1866 + 98 + 5 + 48 + 10, @Trip1_Departure));

    -- Ga Sài Gòn (StationID=19, Seq=24) - Ga cuối, thời gian đến lấy từ Trips.ArrivalDateTime
    INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
    VALUES (1, 19, 24, @Trip1_Arrival_Final, NULL);

    PRINT 'Finished inserting TripStations for TripID = 1.';
END
ELSE
BEGIN
    PRINT 'TripID = 1 (SE1 HN-SG) not found or its Departure/Arrival times are NULL. Cannot populate TripStations.';
END
GO