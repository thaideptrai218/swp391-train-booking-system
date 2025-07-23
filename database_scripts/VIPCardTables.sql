-- VIP Card System Database Tables
USE TrainTicketSystemDB_V2;

-- Create TemporaryVIPPurchases table (similar to booking system)
CREATE TABLE TemporaryVIPPurchases (
    TempVIPPurchaseID int IDENTITY(1,1) PRIMARY KEY,
    SessionID nvarchar(255) NOT NULL,
    UserID int NULL,
    VIPCardTypeID int NOT NULL,
    DurationMonths int NOT NULL,
    Price decimal(10, 2) NOT NULL,
    CreatedAt datetime2(7) NOT NULL DEFAULT GETDATE(),
    ExpiresAt datetime2(7) NOT NULL,
    FOREIGN KEY (VIPCardTypeID) REFERENCES VIPCardTypes(VIPCardTypeID),
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

-- Create UserVIPCards table
CREATE TABLE UserVIPCards (
    UserVIPCardID int IDENTITY(1,1) PRIMARY KEY,
    UserID int NOT NULL,
    VIPCardTypeID int NOT NULL,
    PurchaseDate datetime2(7) NOT NULL DEFAULT GETDATE(),
    ExpiryDate datetime2(7) NOT NULL,
    IsActive bit NOT NULL DEFAULT 1,
    TransactionReference nvarchar(100) NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID),
    FOREIGN KEY (VIPCardTypeID) REFERENCES VIPCardTypes(VIPCardTypeID)
);

-- Add index for better performance
CREATE INDEX IX_UserVIPCards_UserID_Active ON UserVIPCards (UserID, IsActive);

-- Add unique constraint: one active VIP card per user
CREATE UNIQUE INDEX IX_UserVIPCards_OneActivePerUser ON UserVIPCards (UserID) WHERE IsActive = 1;

-- Insert Yearly VIP Card Types (Base prices)
INSERT INTO VIPCardTypes (TypeName, Price, DiscountPercentage, DurationMonths, Description) VALUES
('Thẻ Đồng', 199000.00, 5.00, 12, 'Thẻ VIP cơ bản với giảm giá 5% cho tất cả vé tàu. Thời hạn 12 tháng. Phù hợp cho khách hàng thường xuyên đi tàu.'),
('Thẻ Bạc', 499000.00, 10.00, 12, 'Thẻ VIP trung cấp với giảm giá 10% cho tất cả vé tàu. Thời hạn 12 tháng. Ưu tiên đặt chỗ trước 24h. Tích lũy điểm thưởng x1.5'),
('Thẻ Vàng', 899000.00, 15.00, 12, 'Thẻ VIP cao cấp với giảm giá 15% cho tất cả vé tàu. Thời hạn 12 tháng. Ưu tiên đặt chỗ trước 48h. Miễn phí hủy vé 1 lần/tháng. Tích lũy điểm thưởng x2'),
('Thẻ Kim Cương', 1599000.00, 20.00, 12, 'Thẻ VIP cao cấp nhất với giảm giá 20% cho tất cả vé tàu. Thời hạn 12 tháng. Ưu tiên đặt chỗ trước 72h. Miễn phí hủy vé không giới hạn. Hỗ trợ khách hàng 24/7. Tích lũy điểm thưởng x3');

-- Insert 3-month VIP Card Types (25% of yearly price)
INSERT INTO VIPCardTypes (TypeName, Price, DiscountPercentage, DurationMonths, Description) VALUES
('Thẻ Đồng 3 Tháng', 49750.00, 5.00, 3, 'Thẻ VIP cơ bản 3 tháng với giảm giá 5% cho tất cả vé tàu. Phù hợp cho khách hàng muốn trải nghiệm VIP ngắn hạn.'),
('Thẻ Bạc 3 Tháng', 124750.00, 10.00, 3, 'Thẻ VIP trung cấp 3 tháng với giảm giá 10% cho tất cả vé tàu. Ưu tiên đặt chỗ trước 24h. Tích lũy điểm thưởng x1.5'),
('Thẻ Vàng 3 Tháng', 224750.00, 15.00, 3, 'Thẻ VIP cao cấp 3 tháng với giảm giá 15% cho tất cả vé tàu. Ưu tiên đặt chỗ trước 48h. Miễn phí hủy vé 1 lần/tháng. Tích lũy điểm thưởng x2'),
('Thẻ Kim Cương 3 Tháng', 399750.00, 20.00, 3, 'Thẻ VIP cao cấp nhất 3 tháng với giảm giá 20% cho tất cả vé tàu. Ưu tiên đặt chỗ trước 72h. Miễn phí hủy vé không giới hạn. Hỗ trợ khách hàng 24/7. Tích lũy điểm thưởng x3');