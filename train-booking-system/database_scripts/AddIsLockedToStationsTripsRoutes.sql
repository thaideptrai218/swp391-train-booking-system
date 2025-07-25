-- Thêm cột IsActive cho bảng Stations
ALTER TABLE [dbo].[Stations]
ADD [IsActive] BIT NOT NULL CONSTRAINT DF_Stations_IsActive DEFAULT (1);
GO

-- Thêm cột IsActive cho bảng Routes
ALTER TABLE [dbo].[Routes]
ADD [IsActive] BIT NOT NULL CONSTRAINT DF_Routes_IsActive DEFAULT (1);
GO
