-- Thêm cột IsLocked cho bảng Stations, Trips, Routes
ALTER TABLE Stations ADD IsLocked BIT NOT NULL DEFAULT 0;

ALTER TABLE Trips ADD IsLocked BIT NOT NULL DEFAULT 0;