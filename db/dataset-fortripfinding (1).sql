-- Ensure you are in the context of your database


-- =========================================================================================
-- 0. (Optional) Clean up data from relevant tables if re-running
-- =========================================================================================
PRINT '--- Optional: Deleting existing data (run with caution) ---';
USE TrainTicketSystemDB_V1_FinalDesign;
GO
-- Note: Delete in reverse order of creation due to FK constraints
/*
DELETE FROM TripStations;
DELETE FROM Trips;
DELETE FROM RouteStations;
DELETE FROM Routes;
DELETE FROM Trains;
DELETE FROM TrainTypes;
DELETE FROM Stations;
GO
PRINT '--- Existing data deleted from relevant tables. ---';
*/

-- =========================================================================================
-- 1. Populate Stations (Adding a few more for better route diversity)
-- =========================================================================================
PRINT 'Populating Stations...';
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'SGN')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('SGN', N'Sài Gòn', N'Hồ Chí Minh', N'South');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'DNA')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('DNA', N'Đà Nẵng', N'Đà Nẵng', N'Central');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'HAN')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('HAN', N'Hà Nội', N'Hà Nội', N'North');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'HUE')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('HUE', N'Huế', N'Thừa Thiên Huế', N'Central');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'NTH')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('NTH', N'Nha Trang', N'Khánh Hòa', N'South Central Coast');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'BTH')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('BTH', N'Bình Thuận', N'Bình Thuận', N'South Central Coast');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'DTR')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('DTR', N'Diêu Trì', N'Bình Định', N'South Central Coast'); -- Quy Nhon
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'VIN')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('VIN', N'Vinh', N'Nghệ An', N'North Central');
IF NOT EXISTS (SELECT 1 FROM Stations WHERE StationCode = 'PYN')
    INSERT INTO Stations (StationCode, StationName, City, Region) VALUES ('PYN', N'Tuy Hòa', N'Phú Yên', N'South Central Coast');
PRINT 'Stations populated/verified.';
GO

-- =========================================================================================
-- 2. Populate TrainTypes
-- =========================================================================================
PRINT 'Populating TrainTypes...';
IF NOT EXISTS (SELECT 1 FROM TrainTypes WHERE TypeName = 'SE (Express)')
    INSERT INTO TrainTypes (TypeName, AverageVelocity, Description) VALUES (N'SE (Express)', 70.0, N'Tàu Thống Nhất nhanh, ít điểm dừng.');
IF NOT EXISTS (SELECT 1 FROM TrainTypes WHERE TypeName = 'TN (Local)')
    INSERT INTO TrainTypes (TypeName, AverageVelocity, Description) VALUES (N'TN (Local)', 50.0, N'Tàu Thống Nhất thường, nhiều điểm dừng hơn.');
IF NOT EXISTS (SELECT 1 FROM TrainTypes WHERE TypeName = 'SPT (Regional)')
    INSERT INTO TrainTypes (TypeName, AverageVelocity, Description) VALUES (N'SPT (Regional)', 60.0, N'Tàu nhanh khu đoạn Sài Gòn - Phan Thiết.');
PRINT 'TrainTypes populated/verified.';
GO

-- =========================================================================================
-- 3. Populate Trains
-- =========================================================================================
PRINT 'Populating Trains...';
DECLARE @TrainTypeSE INT, @TrainTypeTN INT, @TrainTypeSPT INT;
SELECT @TrainTypeSE = TrainTypeID FROM TrainTypes WHERE TypeName = N'SE (Express)';
SELECT @TrainTypeTN = TrainTypeID FROM TrainTypes WHERE TypeName = N'TN (Local)';
SELECT @TrainTypeSPT = TrainTypeID FROM TrainTypes WHERE TypeName = N'SPT (Regional)';

IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SE1')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SE1', @TrainTypeSE, 1); -- North-South Express
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SE2')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SE2', @TrainTypeSE, 1); -- South-North Express
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SE3')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SE3', @TrainTypeSE, 1); -- Another North-South Express
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SE4')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SE4', @TrainTypeSE, 1); -- Another South-North Express
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'TN1')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'TN1', @TrainTypeTN, 1); -- North-South Local
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'TN2')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'TN2', @TrainTypeTN, 1); -- South-North Local
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SPT1')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SPT1', @TrainTypeSPT, 1); -- Saigon - Phan Thiet
IF NOT EXISTS (SELECT 1 FROM Trains WHERE TrainName = 'SPT2')
    INSERT INTO Trains (TrainName, TrainTypeID, IsActive) VALUES (N'SPT2', @TrainTypeSPT, 1); -- Phan Thiet - Saigon
PRINT 'Trains populated/verified.';
GO

-- =========================================================================================
-- 4. Populate Routes
-- =========================================================================================
PRINT 'Populating Routes...';
IF NOT EXISTS (SELECT 1 FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)')
    INSERT INTO Routes (RouteName, Description) VALUES (N'North-South Line (Hà Nội - Sài Gòn)', N'Tuyến đường sắt Bắc - Nam chính, chiều từ Hà Nội vào Sài Gòn.');
IF NOT EXISTS (SELECT 1 FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)')
    INSERT INTO Routes (RouteName, Description) VALUES (N'South-North Line (Sài Gòn - Hà Nội)', N'Tuyến đường sắt Bắc - Nam chính, chiều từ Sài Gòn ra Hà Nội.');
IF NOT EXISTS (SELECT 1 FROM Routes WHERE RouteName = N'Sài Gòn - Phan Thiết Line')
    INSERT INTO Routes (RouteName, Description) VALUES (N'Sài Gòn - Phan Thiết Line', N'Tuyến Sài Gòn - Phan Thiết (Bình Thuận) và ngược lại.');
PRINT 'Routes populated/verified.';
GO

-- =========================================================================================
-- 5. Populate RouteStations
-- =========================================================================================
PRINT 'Populating RouteStations...';
DECLARE @RouteNS_ID INT, @RouteSN_ID INT, @RouteSPT_ID INT;
DECLARE @StationHAN INT, @StationVIN INT, @StationHUE INT, @StationDNA INT, @StationDTR INT, @StationPYN INT, @StationNTH INT, @StationBTH INT, @StationSGN INT;

SELECT @RouteNS_ID = RouteID FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)';
SELECT @RouteSN_ID = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';
SELECT @RouteSPT_ID = RouteID FROM Routes WHERE RouteName = N'Sài Gòn - Phan Thiết Line';

SELECT @StationHAN = StationID FROM Stations WHERE StationCode = 'HAN';
SELECT @StationVIN = StationID FROM Stations WHERE StationCode = 'VIN';
SELECT @StationHUE = StationID FROM Stations WHERE StationCode = 'HUE';
SELECT @StationDNA = StationID FROM Stations WHERE StationCode = 'DNA';
SELECT @StationDTR = StationID FROM Stations WHERE StationCode = 'DTR'; -- Dieu Tri
SELECT @StationPYN = StationID FROM Stations WHERE StationCode = 'PYN'; -- Tuy Hoa
SELECT @StationNTH = StationID FROM Stations WHERE StationCode = 'NTH'; -- Nha Trang
SELECT @StationBTH = StationID FROM Stations WHERE StationCode = 'BTH'; -- Binh Thuan (Phan Thiet)
SELECT @StationSGN = StationID FROM Stations WHERE StationCode = 'SGN';

-- Route: North-South Line (Hà Nội - Sài Gòn)
IF @RouteNS_ID IS NOT NULL BEGIN
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationHAN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationHAN, 1, 0.00, 15); -- Hanoi
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationVIN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationVIN, 2, 319.00, 10); -- Vinh
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationHUE)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationHUE, 3, 688.00, 10); -- Hue
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationDNA)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationDNA, 4, 791.00, 15); -- Da Nang
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationDTR)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationDTR, 5, 1096.00, 10); -- Dieu Tri (Quy Nhon)
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationPYN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationPYN, 6, 1198.00, 5); -- Tuy Hoa
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationNTH)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationNTH, 7, 1315.00, 15); -- Nha Trang
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteNS_ID AND StationID = @StationSGN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteNS_ID, @StationSGN, 8, 1726.00, 0); -- Sai Gon (last stop)
END

-- Route: South-North Line (Sài Gòn - Hà Nội) - Reverse of above distances are relative to SGN
IF @RouteSN_ID IS NOT NULL BEGIN
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationSGN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationSGN, 1, 0.00, 15); -- Sai Gon
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationNTH)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationNTH, 2, (1726.00-1315.00), 15); -- Nha Trang
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationPYN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationPYN, 3, (1726.00-1198.00), 5); -- Tuy Hoa
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationDTR)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationDTR, 4, (1726.00-1096.00), 10); -- Dieu Tri (Quy Nhon)
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationDNA)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationDNA, 5, (1726.00-791.00), 15); -- Da Nang
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationHUE)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationHUE, 6, (1726.00-688.00), 10); -- Hue
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationVIN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationVIN, 7, (1726.00-319.00), 10); -- Vinh
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSN_ID AND StationID = @StationHAN)
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSN_ID, @StationHAN, 8, 1726.00, 0); -- Hanoi (last stop)
END

-- Route: Sài Gòn - Phan Thiết Line (and reverse using same RouteID for simplicity in this example, ideally would be two routes)
IF @RouteSPT_ID IS NOT NULL BEGIN
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSPT_ID AND StationID = @StationSGN AND SequenceNumber = 1) -- SGN to BTH leg
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSPT_ID, @StationSGN, 1, 0.00, 10); -- Sai Gon
    IF NOT EXISTS (SELECT 1 FROM RouteStations WHERE RouteID = @RouteSPT_ID AND StationID = @StationBTH AND SequenceNumber = 2) -- SGN to BTH leg
        INSERT INTO RouteStations (RouteID, StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) VALUES (@RouteSPT_ID, @StationBTH, 2, 185.00, 0); -- Binh Thuan (Phan Thiet)
    -- For BTH to SGN, one might use different sequence numbers on the same route if the DB allows, or a separate route.
    -- For simplicity here, we will assume trips define their direction clearly.
    -- For a more robust system, you'd have Route_SGN_BTH and Route_BTH_SGN
END
PRINT 'RouteStations populated/verified.';
GO

-- =========================================================================================
-- 6. Populate Trips (Scheduled Journeys)
-- =========================================================================================
PRINT 'Populating Trips...';
DECLARE @TrainSE1 INT, @TrainSE2 INT, @TrainSE3 INT, @TrainSE4 INT;
DECLARE @TrainSPT1 INT, @TrainSPT2 INT;
DECLARE @RouteNS_ID INT, @RouteSN_ID INT, @RouteSPT_ID_Val INT; -- Renamed to avoid conflict

SELECT @TrainSE1 = TrainID FROM Trains WHERE TrainName = 'SE1';
SELECT @TrainSE2 = TrainID FROM Trains WHERE TrainName = 'SE2';
SELECT @TrainSE3 = TrainID FROM Trains WHERE TrainName = 'SE3';
SELECT @TrainSE4 = TrainID FROM Trains WHERE TrainName = 'SE4';
SELECT @TrainSPT1 = TrainID FROM Trains WHERE TrainName = 'SPT1';
SELECT @TrainSPT2 = TrainID FROM Trains WHERE TrainName = 'SPT2';

SELECT @RouteNS_ID = RouteID FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)';
SELECT @RouteSN_ID = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';
SELECT @RouteSPT_ID_Val = RouteID FROM Routes WHERE RouteName = N'Sài Gòn - Phan Thiết Line';


-- For SE1: Ha Noi (2023-11-15 19:30) to Sai Gon (approx 33 hours -> 2023-11-17 04:30)
IF @TrainSE1 IS NOT NULL AND @RouteNS_ID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE1 AND RouteID = @RouteNS_ID AND DepartureDateTime = '2023-11-15 19:30:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSE1, @RouteNS_ID, '2023-11-15 19:30:00', DATEADD(hour, 33, '2023-11-15 19:30:00'), 'Scheduled', 1.0);

-- For SE3: Ha Noi (2023-11-15 22:00) to Sai Gon (approx 34 hours -> 2023-11-17 08:00)
IF @TrainSE3 IS NOT NULL AND @RouteNS_ID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE3 AND RouteID = @RouteNS_ID AND DepartureDateTime = '2023-11-15 22:00:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSE3, @RouteNS_ID, '2023-11-15 22:00:00', DATEADD(hour, 34, '2023-11-15 22:00:00'), 'Scheduled', 1.05); -- Slightly higher price multiplier

-- For SE2: Sai Gon (2023-11-15 19:30) to Ha Noi (approx 33 hours -> 2023-11-17 04:30)
IF @TrainSE2 IS NOT NULL AND @RouteSN_ID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE2 AND RouteID = @RouteSN_ID AND DepartureDateTime = '2023-11-15 19:30:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSE2, @RouteSN_ID, '2023-11-15 19:30:00', DATEADD(hour, 33, '2023-11-15 19:30:00'), 'Scheduled', 1.0);

-- For SE4: Sai Gon (2023-11-16 08:00) to Ha Noi (approx 34 hours -> 2023-11-17 18:00) -- Next day departure
IF @TrainSE4 IS NOT NULL AND @RouteSN_ID IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE4 AND RouteID = @RouteSN_ID AND DepartureDateTime = '2023-11-16 08:00:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSE4, @RouteSN_ID, '2023-11-16 08:00:00', DATEADD(hour, 34, '2023-11-16 08:00:00'), 'Scheduled', 1.0);

-- For SPT1: Sai Gon (2023-11-15 06:40) to Phan Thiet (Binh Thuan) (approx 4 hours -> 2023-11-15 10:40)
IF @TrainSPT1 IS NOT NULL AND @RouteSPT_ID_Val IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSPT1 AND RouteID = @RouteSPT_ID_Val AND DepartureDateTime = '2023-11-15 06:40:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSPT1, @RouteSPT_ID_Val, '2023-11-15 06:40:00', DATEADD(hour, 4, '2023-11-15 06:40:00'), 'Scheduled', 0.9);

-- For SPT2: Phan Thiet (Binh Thuan) (2023-11-15 13:10) to Sai Gon (approx 4 hours -> 2023-11-15 17:10)
IF @TrainSPT2 IS NOT NULL AND @RouteSPT_ID_Val IS NOT NULL AND NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSPT2 AND RouteID = @RouteSPT_ID_Val AND DepartureDateTime = '2023-11-15 13:10:00')
    INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
    VALUES (@TrainSPT2, @RouteSPT_ID_Val, '2023-11-15 13:10:00', DATEADD(hour, 4, '2023-11-15 13:10:00'), 'Scheduled', 0.9); -- This DepartureDateTime refers to BTH as start for THIS trip on the route

PRINT 'Trips populated/verified.';
GO

-- =========================================================================================
-- 7. Populate TripStations (Actual schedule for each trip)
-- THIS IS THE MOST COMPLEX PART AND REQUIRES CAREFUL TIME CALCULATION
-- For simplicity, I will use the DefaultStopTime from RouteStations and estimate travel time.
-- A real system would have precise scheduling data.
-- =========================================================================================
PRINT 'Populating TripStations...';

-- Helper variables
DECLARE @StationHAN_ID INT, @StationVIN_ID INT, @StationHUE_ID INT, @StationDNA_ID INT, @StationDTR_ID INT, @StationPYN_ID INT, @StationNTH_ID INT, @StationBTH_ID INT, @StationSGN_ID INT;
SELECT @StationHAN_ID = StationID FROM Stations WHERE StationCode = 'HAN';
SELECT @StationVIN_ID = StationID FROM Stations WHERE StationCode = 'VIN';
SELECT @StationHUE_ID = StationID FROM Stations WHERE StationCode = 'HUE';
SELECT @StationDNA_ID = StationID FROM Stations WHERE StationCode = 'DNA';
SELECT @StationDTR_ID = StationID FROM Stations WHERE StationCode = 'DTR';
SELECT @StationPYN_ID = StationID FROM Stations WHERE StationCode = 'PYN';
SELECT @StationNTH_ID = StationID FROM Stations WHERE StationCode = 'NTH';
SELECT @StationBTH_ID = StationID FROM Stations WHERE StationCode = 'BTH';
SELECT @StationSGN_ID = StationID FROM Stations WHERE StationCode = 'SGN';

DECLARE @TripSE1_ID INT, @TripSE2_ID INT, @TripSE3_ID INT, @TripSE4_ID INT, @TripSPT1_ID INT, @TripSPT2_ID INT;
-- Note: Selecting trips based on more unique criteria if multiple trips for same train/route exist on different dates
SELECT @TripSE1_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SE1' AND t.DepartureDateTime = '2023-11-15 19:30:00';
SELECT @TripSE2_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SE2' AND t.DepartureDateTime = '2023-11-15 19:30:00';
SELECT @TripSE3_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SE3' AND t.DepartureDateTime = '2023-11-15 22:00:00';
SELECT @TripSE4_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SE4' AND t.DepartureDateTime = '2023-11-16 08:00:00';
SELECT @TripSPT1_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SPT1' AND t.DepartureDateTime = '2023-11-15 06:40:00';
SELECT @TripSPT2_ID = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SPT2' AND t.DepartureDateTime = '2023-11-15 13:10:00';


-- Trip SE1 (HAN -> SGN), Departs HAN: 2023-11-15 19:30:00
IF @TripSE1_ID IS NOT NULL BEGIN
    PRINT 'Populating TripStations for SE1 (TripID: ' + CAST(@TripSE1_ID AS VARCHAR) + ')';
    DECLARE @CurrentTimeSE1 DATETIME2 = '2023-11-15 19:30:00';
    DECLARE @StopTime INT;

    -- HAN (1)
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationHAN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationHAN_ID, 1, NULL, @CurrentTimeSE1);
    SELECT @StopTime = DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationHAN_ID;
    SET @CurrentTimeSE1 = DATEADD(minute, @StopTime, @CurrentTimeSE1);

    -- VIN (2) Approx HAN-VIN: 319km / ~70kmh = ~4.5h = 270 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 270, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationVIN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationVIN_ID, 2, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationVIN_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationVIN_ID), @CurrentTimeSE1);

    -- HUE (3) Approx VIN-HUE: (688-319)km / ~70kmh = ~5.2h = 315 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 315, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationHUE_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationHUE_ID, 3, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationHUE_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationHUE_ID), @CurrentTimeSE1);

    -- DNA (4) Approx HUE-DNA: (791-688)km / ~70kmh = ~1.5h = 90 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 90, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationDNA_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationDNA_ID, 4, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationDNA_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationDNA_ID), @CurrentTimeSE1);

    -- DTR (5) Approx DNA-DTR: (1096-791)km / ~70kmh = ~4.3h = 260 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 260, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationDTR_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationDTR_ID, 5, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationDTR_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationDTR_ID), @CurrentTimeSE1);

    -- PYN (6) Approx DTR-PYN: (1198-1096)km / ~70kmh = ~1.45h = 87 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 87, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationPYN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationPYN_ID, 6, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationPYN_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationPYN_ID), @CurrentTimeSE1);
    
    -- NTH (7) Approx PYN-NTH: (1315-1198)km / ~70kmh = ~1.67h = 100 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 100, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationNTH_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationNTH_ID, 7, @CurrentTimeSE1, DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationNTH_ID), @CurrentTimeSE1));
    SET @CurrentTimeSE1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = (SELECT RouteID FROM Trips WHERE TripID = @TripSE1_ID) AND StationID = @StationNTH_ID), @CurrentTimeSE1);

    -- SGN (8) Approx NTH-SGN: (1726-1315)km / ~70kmh = ~5.8h = 350 mins
    SET @CurrentTimeSE1 = DATEADD(minute, 350, @CurrentTimeSE1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE1_ID AND StationID = @StationSGN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE1_ID, @StationSGN_ID, 8, @CurrentTimeSE1, NULL); -- Last stop, no departure
    PRINT 'SE1 TripStations times calculated up to SGN. Final Arrival: ' + CONVERT(VARCHAR, @CurrentTimeSE1, 120);
END

-- Trip SE2 (SGN -> HAN), Departs SGN: 2023-11-15 19:30:00 (Similar logic, reversed stations)
-- For brevity, I will only show one full TripStation population. You'd repeat for SE2, SE3, SE4.
-- For SE2, the sequence numbers and stations would be based on the @RouteSN_ID.
-- The travel times between stations remain roughly the same, but are applied in reverse order.
IF @TripSE2_ID IS NOT NULL BEGIN
    PRINT 'Populating TripStations for SE2 (TripID: ' + CAST(@TripSE2_ID AS VARCHAR) + ') - (Simplified, populate similarly to SE1 but for South-North Route)';
    -- SGN (1)
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE2_ID AND StationID = @StationSGN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE2_ID, @StationSGN_ID, 1, NULL, (SELECT DepartureDateTime FROM Trips WHERE TripID = @TripSE2_ID));
    -- NTH (2)
    -- PYN (3)
    -- DTR (4)
    -- DNA (5)
    -- HUE (6)
    -- VIN (7)
    -- HAN (8)
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSE2_ID AND StationID = @StationHAN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSE2_ID, @StationHAN_ID, 8, (SELECT ArrivalDateTime FROM Trips WHERE TripID = @TripSE2_ID), NULL);
    PRINT 'Placeholder for SE2 TripStations. Populate fully for testing.';
END

-- Trip SPT1 (SGN -> BTH), Departs SGN: 2023-11-15 06:40:00
IF @TripSPT1_ID IS NOT NULL BEGIN
    PRINT 'Populating TripStations for SPT1 (TripID: ' + CAST(@TripSPT1_ID AS VARCHAR) + ')';
    DECLARE @CurrentTimeSPT1 DATETIME2 = (SELECT DepartureDateTime FROM Trips WHERE TripID = @TripSPT1_ID);
    DECLARE @SPT1_RouteID INT = (SELECT RouteID FROM Trips WHERE TripID = @TripSPT1_ID);
    
    -- SGN (Seq 1 on this route)
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSPT1_ID AND StationID = @StationSGN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSPT1_ID, @StationSGN_ID, 1, NULL, @CurrentTimeSPT1);
    SET @CurrentTimeSPT1 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = @SPT1_RouteID AND StationID = @StationSGN_ID AND SequenceNumber = 1), @CurrentTimeSPT1);

    -- BTH (Seq 2 on this route) Approx SGN-BTH: 185km / ~60kmh = ~3h = 180 mins (Let's use trip duration from Trips table for consistency)
    -- Duration for SPT1 is 4 hours total (240 mins). Stop time at SGN is 10 mins. So travel SGN->BTH is 230 mins.
    SET @CurrentTimeSPT1 = DATEADD(minute, 230, @CurrentTimeSPT1);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSPT1_ID AND StationID = @StationBTH_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSPT1_ID, @StationBTH_ID, 2, @CurrentTimeSPT1, NULL); -- Last stop for this trip segment
    PRINT 'SPT1 TripStations times calculated. BTH Arrival: ' + CONVERT(VARCHAR, @CurrentTimeSPT1, 120);
END


-- Trip SPT2 (BTH -> SGN), Departs BTH: 2023-11-15 13:10:00
-- IMPORTANT: For SPT2, even though it uses @RouteSPT_ID_Val, the "start" of THIS trip is BTH.
-- The search logic must be smart enough or the TripStation sequence should reflect the actual travel.
-- For simplicity, we'll map BTH as Seq 1 FOR THIS TRIP, and SGN as Seq 2 FOR THIS TRIP.
-- This implies the `SequenceNumber` in `TripStations` is relative to THE TRIP, not necessarily matching `RouteStations` sequence if a trip doesn't cover the full route.
-- For clarity, it's often better to have dedicated routes (Route_SGN_BTH, Route_BTH_SGN).
-- Assuming for this example, SPT2 departs from BTH on the route, and SGN is the destination on that route.
IF @TripSPT2_ID IS NOT NULL BEGIN
    PRINT 'Populating TripStations for SPT2 (TripID: ' + CAST(@TripSPT2_ID AS VARCHAR) + ')';
    DECLARE @CurrentTimeSPT2 DATETIME2 = (SELECT DepartureDateTime FROM Trips WHERE TripID = @TripSPT2_ID); -- This is BTH departure
    DECLARE @SPT2_RouteID INT = (SELECT RouteID FROM Trips WHERE TripID = @TripSPT2_ID);

    -- BTH (As Start for this Trip)
    -- Assuming BTH is Station with Seq 2 in RouteStations for this route. But for THIS TRIP, it's the first stop.
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSPT2_ID AND StationID = @StationBTH_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSPT2_ID, @StationBTH_ID, 1, NULL, @CurrentTimeSPT2); -- Seq 1 for this Trip
    SET @CurrentTimeSPT2 = DATEADD(minute, (SELECT DefaultStopTime FROM RouteStations WHERE RouteID = @SPT2_RouteID AND StationID = @StationBTH_ID AND SequenceNumber = 2), @CurrentTimeSPT2); -- Use BTH's default stop time

    -- SGN (As End for this Trip)
    -- Travel BTH->SGN is 230 mins (similar to SPT1 travel time)
    SET @CurrentTimeSPT2 = DATEADD(minute, 230, @CurrentTimeSPT2);
    IF NOT EXISTS (SELECT 1 FROM TripStations WHERE TripID = @TripSPT2_ID AND StationID = @StationSGN_ID)
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        VALUES (@TripSPT2_ID, @StationSGN_ID, 2, @CurrentTimeSPT2, NULL); -- Seq 2 for this Trip
    PRINT 'SPT2 TripStations times calculated. SGN Arrival: ' + CONVERT(VARCHAR, @CurrentTimeSPT2, 120);
END


PRINT '--- TripStations population attempt complete. Review data carefully. ---';
GO

-- =========================================================================================
-- Sample Queries for Verification
-- =========================================================================================
PRINT '--- Sample Verification Queries ---';

PRINT 'Show all Trips for a specific date (e.g., 2023-11-15 departures)';
SELECT T.TripID, TR.TrainName, R.RouteName, T.DepartureDateTime, T.ArrivalDateTime
FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
JOIN Routes R ON T.RouteID = R.RouteID
WHERE CAST(T.DepartureDateTime AS DATE) = '2023-11-15';
GO

PRINT 'Show TripStations for Trip SE1 (replace with actual TripID if different)';
DECLARE @ExampleTripID_SE1 INT;
SELECT @ExampleTripID_SE1 = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SE1' AND t.DepartureDateTime = '2023-11-15 19:30:00';

SELECT TS.TripStationID, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
FROM TripStations TS
JOIN Stations S ON TS.StationID = S.StationID
WHERE TS.TripID = @ExampleTripID_SE1
ORDER BY TS.SequenceNumber;
GO

PRINT 'Show TripStations for Trip SPT1 (replace with actual TripID if different)';
DECLARE @ExampleTripID_SPT1 INT;
SELECT @ExampleTripID_SPT1 = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SPT1' AND t.DepartureDateTime = '2023-11-15 06:40:00';

SELECT TS.TripStationID, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
FROM TripStations TS
JOIN Stations S ON TS.StationID = S.StationID
WHERE TS.TripID = @ExampleTripID_SPT1
ORDER BY TS.SequenceNumber;
GO

PRINT 'Show TripStations for Trip SPT2 (replace with actual TripID if different)';
DECLARE @ExampleTripID_SPT2 INT;
SELECT @ExampleTripID_SPT2 = TripID FROM Trips t JOIN Trains tr ON t.TrainID = tr.TrainID WHERE tr.TrainName = 'SPT2' AND t.DepartureDateTime = '2023-11-15 13:10:00';

SELECT TS.TripStationID, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
FROM TripStations TS
JOIN Stations S ON TS.StationID = S.StationID
WHERE TS.TripID = @ExampleTripID_SPT2
ORDER BY TS.SequenceNumber;
GO











-- Ensure you are in the context of your database
USE TrainTicketSystemDB_V1_FinalDesign;
GO

PRINT '--- Starting: Adding more Trips and TripStations ---';

-- =========================================================================================
-- Declare common variables
-- =========================================================================================
DECLARE @TrainSE3_ID INT, @TrainSE4_ID INT, @TrainTN1_ID INT, @TrainTN2_ID INT;
DECLARE @RouteNS_ID INT, @RouteSN_ID INT;

-- Get Train IDs
SELECT @TrainSE3_ID = TrainID FROM Trains WHERE TrainName = 'SE3';
SELECT @TrainSE4_ID = TrainID FROM Trains WHERE TrainName = 'SE4';
SELECT @TrainTN1_ID = TrainID FROM Trains WHERE TrainName = 'TN1';
SELECT @TrainTN2_ID = TrainID FROM Trains WHERE TrainName = 'TN2';

-- Get Route IDs
SELECT @RouteNS_ID = RouteID FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)';
SELECT @RouteSN_ID = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';

-- Check if essential IDs were found
IF @TrainSE3_ID IS NULL OR @TrainSE4_ID IS NULL OR @TrainTN1_ID IS NULL OR @TrainTN2_ID IS NULL OR @RouteNS_ID IS NULL OR @RouteSN_ID IS NULL
BEGIN
    PRINT 'Error: Could not find one or more required Train or Route IDs. Ensure previous dataset script was run successfully.';
    -- RAISERROR to stop script if needed, or just print and continue if some might be populated.
    RAISERROR('Essential Train/Route IDs not found. Aborting.', 16, 1);
    RETURN; -- Stop execution
END

-- Station IDs (re-declared for clarity within this script block)
DECLARE @StationHAN_ID INT, @StationVIN_ID INT, @StationHUE_ID INT, @StationDNA_ID INT, @StationDTR_ID INT, @StationPYN_ID INT, @StationNTH_ID INT, @StationSGN_ID INT;
SELECT @StationHAN_ID = StationID FROM Stations WHERE StationCode = 'HAN';
SELECT @StationVIN_ID = StationID FROM Stations WHERE StationCode = 'VIN';
SELECT @StationHUE_ID = StationID FROM Stations WHERE StationCode = 'HUE';
SELECT @StationDNA_ID = StationID FROM Stations WHERE StationCode = 'DNA';
SELECT @StationDTR_ID = StationID FROM Stations WHERE StationCode = 'DTR';
SELECT @StationPYN_ID = StationID FROM Stations WHERE StationCode = 'PYN';
SELECT @StationNTH_ID = StationID FROM Stations WHERE StationCode = 'NTH';
SELECT @StationSGN_ID = StationID FROM Stations WHERE StationCode = 'SGN';

-- =========================================================================================
-- 1. New Trip: SE3 (HAN -> SGN) - Departure: 2023-11-17 22:00:00 (Duration ~34 hours)
-- =========================================================================================
PRINT 'Processing Trip: SE3 (HAN-SGN) departing 2023-11-17 22:00:00';
DECLARE @NewTrip_SE3_DepDateTime DATETIME2 = '2023-11-17 22:00:00';
DECLARE @NewTrip_SE3_ArrDateTime DATETIME2 = DATEADD(hour, 34, @NewTrip_SE3_DepDateTime); -- Approx. 34 hours
DECLARE @NewTrip_SE3_ID INT;

IF @TrainSE3_ID IS NOT NULL AND @RouteNS_ID IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE3_ID AND RouteID = @RouteNS_ID AND DepartureDateTime = @NewTrip_SE3_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainSE3_ID, @RouteNS_ID, @NewTrip_SE3_DepDateTime, @NewTrip_SE3_ArrDateTime, 'Scheduled', 1.05);
        SET @NewTrip_SE3_ID = SCOPE_IDENTITY();
        PRINT 'Trip SE3 (ID: ' + CAST(@NewTrip_SE3_ID AS VARCHAR) + ') created.';

        -- Populate TripStations for this new SE3 trip
        DECLARE @CurrentTime DATETIME2 = @NewTrip_SE3_DepDateTime;
        DECLARE @TotalDurationMinutes INT = DATEDIFF(minute, @NewTrip_SE3_DepDateTime, @NewTrip_SE3_ArrDateTime);
        DECLARE @SumIntermediateStopTimes INT;
        DECLARE @TotalRouteDistance DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes = SUM(rs.DefaultStopTime)
        FROM RouteStations rs
        WHERE rs.RouteID = @RouteNS_ID
          AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID) -- Exclude first station
          AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID); -- Exclude last station
        IF @SumIntermediateStopTimes IS NULL SET @SumIntermediateStopTimes = 0;

        SELECT @TotalRouteDistance = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteNS_ID;
        
        DECLARE @NetTravelTimeMinutes INT = @TotalDurationMinutes - @SumIntermediateStopTimes;

        DECLARE @PrevStationTime DATETIME2 = @NewTrip_SE3_DepDateTime; -- This is departure time from the previous station
        DECLARE @PrevStationDistance DECIMAL(10,2) = 0;

        -- Temporary table to hold route station details for calculation ease
        DECLARE @TripRouteStations TABLE (
            StationID INT,
            SequenceNumber INT,
            DistanceFromStart DECIMAL(10,2),
            DefaultStopTime INT,
            ScheduledArrival DATETIME2 NULL,
            ScheduledDeparture DATETIME2 NULL
        );
        INSERT INTO @TripRouteStations (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime)
        SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime
        FROM RouteStations WHERE RouteID = @RouteNS_ID ORDER BY SequenceNumber;

        -- First station
        UPDATE @TripRouteStations
        SET ScheduledArrival = NULL,
            ScheduledDeparture = @NewTrip_SE3_DepDateTime
        WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations);
        
        SET @PrevStationTime = @NewTrip_SE3_DepDateTime; -- Departure from first station

        -- Iterate through remaining stations
        DECLARE cur CURSOR FOR
            SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime
            FROM @TripRouteStations
            WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations) -- Skip first station as it's handled
            ORDER BY SequenceNumber;
        
        DECLARE @CurrentStationID INT, @CurrentSeq INT, @CurrentDist DECIMAL(10,2), @CurrentStopTime INT;
        OPEN cur;
        FETCH NEXT FROM cur INTO @CurrentStationID, @CurrentSeq, @CurrentDist, @CurrentStopTime;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance DECIMAL(10,2) = @CurrentDist - @PrevStationDistance;
            DECLARE @SegmentTravelTimeMinutes INT;

            IF @TotalRouteDistance > 0
                SET @SegmentTravelTimeMinutes = ROUND((@SegmentDistance / @TotalRouteDistance) * @NetTravelTimeMinutes, 0);
            ELSE
                SET @SegmentTravelTimeMinutes = 0; -- Should not happen if route is defined

            DECLARE @ScheduledArrivalTime DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes, @PrevStationTime);
            DECLARE @ScheduledDepartureTime DATETIME2 = @ScheduledArrivalTime;

            IF @CurrentSeq < (SELECT MAX(SequenceNumber) FROM @TripRouteStations) -- Not the last station
                SET @ScheduledDepartureTime = DATEADD(minute, @CurrentStopTime, @ScheduledArrivalTime);
            ELSE -- Last station
                SET @ScheduledDepartureTime = NULL; -- No departure from last station
                SET @ScheduledArrivalTime = @NewTrip_SE3_ArrDateTime; -- Ensure final arrival matches trip arrival

            UPDATE @TripRouteStations
            SET ScheduledArrival = @ScheduledArrivalTime,
                ScheduledDeparture = @ScheduledDepartureTime
            WHERE StationID = @CurrentStationID AND SequenceNumber = @CurrentSeq;

            SET @PrevStationTime = @ScheduledDepartureTime;
            SET @PrevStationDistance = @CurrentDist;

            FETCH NEXT FROM cur INTO @CurrentStationID, @CurrentSeq, @CurrentDist, @CurrentStopTime;
        END
        CLOSE cur;
        DEALLOCATE cur;

        -- Insert into actual TripStations table
        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @NewTrip_SE3_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture
        FROM @TripRouteStations
        WHERE NOT EXISTS (
            SELECT 1 FROM TripStations ts
            WHERE ts.TripID = @NewTrip_SE3_ID AND ts.StationID = [@TripRouteStations].StationID
        );
        PRINT 'TripStations for SE3 (ID: ' + CAST(@NewTrip_SE3_ID AS VARCHAR) + ') populated.';
    END
    ELSE
    BEGIN
        PRINT 'Trip SE3 (HAN-SGN) departing ' + CONVERT(VARCHAR, @NewTrip_SE3_DepDateTime, 120) + ' already exists.';
    END
END
ELSE
BEGIN
    PRINT 'Could not create Trip SE3 (HAN-SGN) departing ' + CONVERT(VARCHAR, @NewTrip_SE3_DepDateTime, 120) + ' due to missing Train/Route ID.';
END
GO


-- =========================================================================================
-- 2. New Trip: SE4 (SGN -> HAN) - Departure: 2023-11-17 22:00:00 (Duration ~34 hours)
-- =========================================================================================
PRINT 'Processing Trip: SE4 (SGN-HAN) departing 2023-11-17 22:00:00';
DECLARE @NewTrip_SE4_DepDateTime DATETIME2 = '2023-11-17 22:00:00';
DECLARE @NewTrip_SE4_ArrDateTime DATETIME2 = DATEADD(hour, 34, @NewTrip_SE4_DepDateTime);
DECLARE @NewTrip_SE4_ID INT;
DECLARE @TrainSE4_ID_Local INT; SELECT @TrainSE4_ID_Local = TrainID FROM Trains WHERE TrainName = 'SE4';
DECLARE @RouteSN_ID_Local INT;  SELECT @RouteSN_ID_Local = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';


IF @TrainSE4_ID_Local IS NOT NULL AND @RouteSN_ID_Local IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE4_ID_Local AND RouteID = @RouteSN_ID_Local AND DepartureDateTime = @NewTrip_SE4_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainSE4_ID_Local, @RouteSN_ID_Local, @NewTrip_SE4_DepDateTime, @NewTrip_SE4_ArrDateTime, 'Scheduled', 1.0);
        SET @NewTrip_SE4_ID = SCOPE_IDENTITY();
        PRINT 'Trip SE4 (ID: ' + CAST(@NewTrip_SE4_ID AS VARCHAR) + ') created.';

        -- Populate TripStations (using the same logic as for SE3, but with @RouteSN_ID_Local)
        DECLARE @CurrentTime_SE4 DATETIME2 = @NewTrip_SE4_DepDateTime;
        DECLARE @TotalDurationMinutes_SE4 INT = DATEDIFF(minute, @NewTrip_SE4_DepDateTime, @NewTrip_SE4_ArrDateTime);
        DECLARE @SumIntermediateStopTimes_SE4 INT;
        DECLARE @TotalRouteDistance_SE4 DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes_SE4 = SUM(rs.DefaultStopTime)
        FROM RouteStations rs
        WHERE rs.RouteID = @RouteSN_ID_Local
          AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local)
          AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local);
        IF @SumIntermediateStopTimes_SE4 IS NULL SET @SumIntermediateStopTimes_SE4 = 0;

        SELECT @TotalRouteDistance_SE4 = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteSN_ID_Local;
        
        DECLARE @NetTravelTimeMinutes_SE4 INT = @TotalDurationMinutes_SE4 - @SumIntermediateStopTimes_SE4;

        DECLARE @PrevStationTime_SE4 DATETIME2 = @NewTrip_SE4_DepDateTime;
        DECLARE @PrevStationDistance_SE4 DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_SE4 TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_SE4 (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime)
        SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteSN_ID_Local ORDER BY SequenceNumber;

        UPDATE @TripRouteStations_SE4 SET ScheduledArrival = NULL, ScheduledDeparture = @NewTrip_SE4_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE4);
        SET @PrevStationTime_SE4 = @NewTrip_SE4_DepDateTime;

        DECLARE cur_SE4 CURSOR FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_SE4 WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE4) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID_SE4 INT, @CurrentSeq_SE4 INT, @CurrentDist_SE4 DECIMAL(10,2), @CurrentStopTime_SE4 INT;
        OPEN cur_SE4;
        FETCH NEXT FROM cur_SE4 INTO @CurrentStationID_SE4, @CurrentSeq_SE4, @CurrentDist_SE4, @CurrentStopTime_SE4;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance_SE4 DECIMAL(10,2) = @CurrentDist_SE4 - @PrevStationDistance_SE4;
            DECLARE @SegmentTravelTimeMinutes_SE4 INT;
            IF @TotalRouteDistance_SE4 > 0 SET @SegmentTravelTimeMinutes_SE4 = ROUND((@SegmentDistance_SE4 / @TotalRouteDistance_SE4) * @NetTravelTimeMinutes_SE4, 0); ELSE SET @SegmentTravelTimeMinutes_SE4 = 0;
            DECLARE @ScheduledArrivalTime_SE4 DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes_SE4, @PrevStationTime_SE4);
            DECLARE @ScheduledDepartureTime_SE4 DATETIME2 = @ScheduledArrivalTime_SE4;
            IF @CurrentSeq_SE4 < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_SE4) SET @ScheduledDepartureTime_SE4 = DATEADD(minute, @CurrentStopTime_SE4, @ScheduledArrivalTime_SE4); ELSE BEGIN SET @ScheduledDepartureTime_SE4 = NULL; SET @ScheduledArrivalTime_SE4 = @NewTrip_SE4_ArrDateTime; END
            UPDATE @TripRouteStations_SE4 SET ScheduledArrival = @ScheduledArrivalTime_SE4, ScheduledDeparture = @ScheduledDepartureTime_SE4 WHERE StationID = @CurrentStationID_SE4 AND SequenceNumber = @CurrentSeq_SE4;
            SET @PrevStationTime_SE4 = @ScheduledDepartureTime_SE4; SET @PrevStationDistance_SE4 = @CurrentDist_SE4;
            FETCH NEXT FROM cur_SE4 INTO @CurrentStationID_SE4, @CurrentSeq_SE4, @CurrentDist_SE4, @CurrentStopTime_SE4;
        END
        CLOSE cur_SE4; DEALLOCATE cur_SE4;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @NewTrip_SE4_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_SE4
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @NewTrip_SE4_ID AND ts.StationID = [@TripRouteStations_SE4].StationID);
        PRINT 'TripStations for SE4 (ID: ' + CAST(@NewTrip_SE4_ID AS VARCHAR) + ') populated.';
    END
    ELSE
    BEGIN
        PRINT 'Trip SE4 (SGN-HAN) departing ' + CONVERT(VARCHAR, @NewTrip_SE4_DepDateTime, 120) + ' already exists.';
    END
END
ELSE
BEGIN
    PRINT 'Could not create Trip SE4 (SGN-HAN) departing ' + CONVERT(VARCHAR, @NewTrip_SE4_DepDateTime, 120) + ' due to missing Train/Route ID.';
END
GO

-- =========================================================================================
-- 3. New Trip: TN1 (Local HAN -> SGN) - Departure: 2023-11-18 10:00:00 (Duration ~40 hours)
-- =========================================================================================
PRINT 'Processing Trip: TN1 (HAN-SGN) departing 2023-11-18 10:00:00';
DECLARE @NewTrip_TN1_DepDateTime DATETIME2 = '2023-11-18 10:00:00';
DECLARE @NewTrip_TN1_ArrDateTime DATETIME2 = DATEADD(hour, 40, @NewTrip_TN1_DepDateTime); -- Slower train
DECLARE @NewTrip_TN1_ID INT;
DECLARE @TrainTN1_ID_Local INT; SELECT @TrainTN1_ID_Local = TrainID FROM Trains WHERE TrainName = 'TN1';
DECLARE @RouteNS_ID_Local_TN1 INT;  SELECT @RouteNS_ID_Local_TN1 = RouteID FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)';

IF @TrainTN1_ID_Local IS NOT NULL AND @RouteNS_ID_Local_TN1 IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainTN1_ID_Local AND RouteID = @RouteNS_ID_Local_TN1 AND DepartureDateTime = @NewTrip_TN1_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainTN1_ID_Local, @RouteNS_ID_Local_TN1, @NewTrip_TN1_DepDateTime, @NewTrip_TN1_ArrDateTime, 'Scheduled', 0.9); -- Cheaper/Slower
        SET @NewTrip_TN1_ID = SCOPE_IDENTITY();
        PRINT 'Trip TN1 (ID: ' + CAST(@NewTrip_TN1_ID AS VARCHAR) + ') created.';

        -- Populate TripStations (using the same logic)
        DECLARE @CurrentTime_TN1 DATETIME2 = @NewTrip_TN1_DepDateTime;
        DECLARE @TotalDurationMinutes_TN1 INT = DATEDIFF(minute, @NewTrip_TN1_DepDateTime, @NewTrip_TN1_ArrDateTime);
        DECLARE @SumIntermediateStopTimes_TN1 INT;
        DECLARE @TotalRouteDistance_TN1 DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes_TN1 = SUM(rs.DefaultStopTime)
        FROM RouteStations rs
        WHERE rs.RouteID = @RouteNS_ID_Local_TN1
          AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID_Local_TN1)
          AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID_Local_TN1);
        IF @SumIntermediateStopTimes_TN1 IS NULL SET @SumIntermediateStopTimes_TN1 = 0;
        
        SELECT @TotalRouteDistance_TN1 = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteNS_ID_Local_TN1;
        
        DECLARE @NetTravelTimeMinutes_TN1 INT = @TotalDurationMinutes_TN1 - @SumIntermediateStopTimes_TN1;

        DECLARE @PrevStationTime_TN1 DATETIME2 = @NewTrip_TN1_DepDateTime;
        DECLARE @PrevStationDistance_TN1 DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_TN1 TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_TN1 (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime)
        SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteNS_ID_Local_TN1 ORDER BY SequenceNumber;

        UPDATE @TripRouteStations_TN1 SET ScheduledArrival = NULL, ScheduledDeparture = @NewTrip_TN1_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_TN1);
        SET @PrevStationTime_TN1 = @NewTrip_TN1_DepDateTime;

        DECLARE cur_TN1 CURSOR FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_TN1 WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_TN1) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID_TN1 INT, @CurrentSeq_TN1 INT, @CurrentDist_TN1 DECIMAL(10,2), @CurrentStopTime_TN1 INT;
        OPEN cur_TN1;
        FETCH NEXT FROM cur_TN1 INTO @CurrentStationID_TN1, @CurrentSeq_TN1, @CurrentDist_TN1, @CurrentStopTime_TN1;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance_TN1 DECIMAL(10,2) = @CurrentDist_TN1 - @PrevStationDistance_TN1;
            DECLARE @SegmentTravelTimeMinutes_TN1 INT;
            IF @TotalRouteDistance_TN1 > 0 SET @SegmentTravelTimeMinutes_TN1 = ROUND((@SegmentDistance_TN1 / @TotalRouteDistance_TN1) * @NetTravelTimeMinutes_TN1, 0); ELSE SET @SegmentTravelTimeMinutes_TN1 = 0;
            DECLARE @ScheduledArrivalTime_TN1 DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes_TN1, @PrevStationTime_TN1);
            DECLARE @ScheduledDepartureTime_TN1 DATETIME2 = @ScheduledArrivalTime_TN1;
            IF @CurrentSeq_TN1 < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_TN1) SET @ScheduledDepartureTime_TN1 = DATEADD(minute, @CurrentStopTime_TN1, @ScheduledArrivalTime_TN1); ELSE BEGIN SET @ScheduledDepartureTime_TN1 = NULL; SET @ScheduledArrivalTime_TN1 = @NewTrip_TN1_ArrDateTime; END
            UPDATE @TripRouteStations_TN1 SET ScheduledArrival = @ScheduledArrivalTime_TN1, ScheduledDeparture = @ScheduledDepartureTime_TN1 WHERE StationID = @CurrentStationID_TN1 AND SequenceNumber = @CurrentSeq_TN1;
            SET @PrevStationTime_TN1 = @ScheduledDepartureTime_TN1; SET @PrevStationDistance_TN1 = @CurrentDist_TN1;
            FETCH NEXT FROM cur_TN1 INTO @CurrentStationID_TN1, @CurrentSeq_TN1, @CurrentDist_TN1, @CurrentStopTime_TN1;
        END
        CLOSE cur_TN1; DEALLOCATE cur_TN1;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @NewTrip_TN1_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_TN1
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @NewTrip_TN1_ID AND ts.StationID = [@TripRouteStations_TN1].StationID);
        PRINT 'TripStations for TN1 (ID: ' + CAST(@NewTrip_TN1_ID AS VARCHAR) + ') populated.';
    END
    ELSE
    BEGIN
        PRINT 'Trip TN1 (HAN-SGN) departing ' + CONVERT(VARCHAR, @NewTrip_TN1_DepDateTime, 120) + ' already exists.';
    END
END
ELSE
BEGIN
    PRINT 'Could not create Trip TN1 (HAN-SGN) departing ' + CONVERT(VARCHAR, @NewTrip_TN1_DepDateTime, 120) + ' due to missing Train/Route ID.';
END
GO

-- =========================================================================================
-- 4. New Trip: TN2 (Local SGN -> HAN) - Departure: 2023-11-18 10:00:00 (Duration ~40 hours)
-- =========================================================================================
PRINT 'Processing Trip: TN2 (SGN-HAN) departing 2023-11-18 10:00:00';
DECLARE @NewTrip_TN2_DepDateTime DATETIME2 = '2023-11-18 10:00:00';
DECLARE @NewTrip_TN2_ArrDateTime DATETIME2 = DATEADD(hour, 40, @NewTrip_TN2_DepDateTime);
DECLARE @NewTrip_TN2_ID INT;
DECLARE @TrainTN2_ID_Local INT; SELECT @TrainTN2_ID_Local = TrainID FROM Trains WHERE TrainName = 'TN2';
DECLARE @RouteSN_ID_Local_TN2 INT;  SELECT @RouteSN_ID_Local_TN2 = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';


IF @TrainTN2_ID_Local IS NOT NULL AND @RouteSN_ID_Local_TN2 IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainTN2_ID_Local AND RouteID = @RouteSN_ID_Local_TN2 AND DepartureDateTime = @NewTrip_TN2_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainTN2_ID_Local, @RouteSN_ID_Local_TN2, @NewTrip_TN2_DepDateTime, @NewTrip_TN2_ArrDateTime, 'Scheduled', 0.9);
        SET @NewTrip_TN2_ID = SCOPE_IDENTITY();
        PRINT 'Trip TN2 (ID: ' + CAST(@NewTrip_TN2_ID AS VARCHAR) + ') created.';

        -- Populate TripStations (using the same logic)
        DECLARE @CurrentTime_TN2 DATETIME2 = @NewTrip_TN2_DepDateTime;
        DECLARE @TotalDurationMinutes_TN2 INT = DATEDIFF(minute, @NewTrip_TN2_DepDateTime, @NewTrip_TN2_ArrDateTime);
        DECLARE @SumIntermediateStopTimes_TN2 INT;
        DECLARE @TotalRouteDistance_TN2 DECIMAL(10,2);

        SELECT @SumIntermediateStopTimes_TN2 = SUM(rs.DefaultStopTime)
        FROM RouteStations rs
        WHERE rs.RouteID = @RouteSN_ID_Local_TN2
          AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local_TN2)
          AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local_TN2);
        IF @SumIntermediateStopTimes_TN2 IS NULL SET @SumIntermediateStopTimes_TN2 = 0;
        
        SELECT @TotalRouteDistance_TN2 = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteSN_ID_Local_TN2;
        
        DECLARE @NetTravelTimeMinutes_TN2 INT = @TotalDurationMinutes_TN2 - @SumIntermediateStopTimes_TN2;

        DECLARE @PrevStationTime_TN2 DATETIME2 = @NewTrip_TN2_DepDateTime;
        DECLARE @PrevStationDistance_TN2 DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_TN2 TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_TN2 (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime)
        SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteSN_ID_Local_TN2 ORDER BY SequenceNumber;

        UPDATE @TripRouteStations_TN2 SET ScheduledArrival = NULL, ScheduledDeparture = @NewTrip_TN2_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_TN2);
        SET @PrevStationTime_TN2 = @NewTrip_TN2_DepDateTime;

        DECLARE cur_TN2 CURSOR FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_TN2 WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_TN2) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID_TN2 INT, @CurrentSeq_TN2 INT, @CurrentDist_TN2 DECIMAL(10,2), @CurrentStopTime_TN2 INT;
        OPEN cur_TN2;
        FETCH NEXT FROM cur_TN2 INTO @CurrentStationID_TN2, @CurrentSeq_TN2, @CurrentDist_TN2, @CurrentStopTime_TN2;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance_TN2 DECIMAL(10,2) = @CurrentDist_TN2 - @PrevStationDistance_TN2;
            DECLARE @SegmentTravelTimeMinutes_TN2 INT;
            IF @TotalRouteDistance_TN2 > 0 SET @SegmentTravelTimeMinutes_TN2 = ROUND((@SegmentDistance_TN2 / @TotalRouteDistance_TN2) * @NetTravelTimeMinutes_TN2, 0); ELSE SET @SegmentTravelTimeMinutes_TN2 = 0;
            DECLARE @ScheduledArrivalTime_TN2 DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes_TN2, @PrevStationTime_TN2);
            DECLARE @ScheduledDepartureTime_TN2 DATETIME2 = @ScheduledArrivalTime_TN2;
            IF @CurrentSeq_TN2 < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_TN2) SET @ScheduledDepartureTime_TN2 = DATEADD(minute, @CurrentStopTime_TN2, @ScheduledArrivalTime_TN2); ELSE BEGIN SET @ScheduledDepartureTime_TN2 = NULL; SET @ScheduledArrivalTime_TN2 = @NewTrip_TN2_ArrDateTime; END
            UPDATE @TripRouteStations_TN2 SET ScheduledArrival = @ScheduledArrivalTime_TN2, ScheduledDeparture = @ScheduledDepartureTime_TN2 WHERE StationID = @CurrentStationID_TN2 AND SequenceNumber = @CurrentSeq_TN2;
            SET @PrevStationTime_TN2 = @ScheduledDepartureTime_TN2; SET @PrevStationDistance_TN2 = @CurrentDist_TN2;
            FETCH NEXT FROM cur_TN2 INTO @CurrentStationID_TN2, @CurrentSeq_TN2, @CurrentDist_TN2, @CurrentStopTime_TN2;
        END
        CLOSE cur_TN2; DEALLOCATE cur_TN2;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @NewTrip_TN2_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_TN2
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @NewTrip_TN2_ID AND ts.StationID = [@TripRouteStations_TN2].StationID);
        PRINT 'TripStations for TN2 (ID: ' + CAST(@NewTrip_TN2_ID AS VARCHAR) + ') populated.';
    END
    ELSE
    BEGIN
        PRINT 'Trip TN2 (SGN-HAN) departing ' + CONVERT(VARCHAR, @NewTrip_TN2_DepDateTime, 120) + ' already exists.';
    END
END
ELSE
BEGIN
    PRINT 'Could not create Trip TN2 (SGN-HAN) departing ' + CONVERT(VARCHAR, @NewTrip_TN2_DepDateTime, 120) + ' due to missing Train/Route ID.';
END
GO


PRINT '--- Finished: Adding more Trips and TripStations ---';
GO

-- =========================================================================================
-- Sample Queries for Verification of NEWLY ADDED trips
-- =========================================================================================
PRINT '--- Sample Verification Queries for newly added trips ---';

PRINT 'Show all Trips departing on 2023-11-17 or 2023-11-18';
SELECT T.TripID, TR.TrainName, R.RouteName, T.DepartureDateTime, T.ArrivalDateTime, T.TripStatus
FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
JOIN Routes R ON T.RouteID = R.RouteID
WHERE CAST(T.DepartureDateTime AS DATE) IN ('2023-11-17', '2023-11-18')
ORDER BY T.DepartureDateTime, TR.TrainName;
GO

PRINT 'Show TripStations for the new SE3 trip (HAN-SGN departing 2023-11-17 22:00:00)';
DECLARE @Verify_SE3_ID INT;
SELECT @Verify_SE3_ID = TripID FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
WHERE TR.TrainName = 'SE3' AND T.DepartureDateTime = '2023-11-17 22:00:00';

IF @Verify_SE3_ID IS NOT NULL
BEGIN
    SELECT TS.TripStationID, T.TrainName AS TripTrain, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
    FROM TripStations TS
    JOIN Stations S ON TS.StationID = S.StationID
    JOIN Trips TINFO ON TS.TripID = TINFO.TripID
    JOIN Trains T ON TINFO.TrainID = T.TrainID
    WHERE TS.TripID = @Verify_SE3_ID
    ORDER BY TS.SequenceNumber;
END ELSE PRINT 'Could not find TripID for SE3 departing 2023-11-17 22:00:00 to verify TripStations.';
GO

PRINT 'Show TripStations for the new TN1 trip (HAN-SGN departing 2023-11-18 10:00:00)';
DECLARE @Verify_TN1_ID INT;
SELECT @Verify_TN1_ID = TripID FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
WHERE TR.TrainName = 'TN1' AND T.DepartureDateTime = '2023-11-18 10:00:00';

IF @Verify_TN1_ID IS NOT NULL
BEGIN
    SELECT TS.TripStationID, T.TrainName AS TripTrain, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
    FROM TripStations TS
    JOIN Stations S ON TS.StationID = S.StationID
    JOIN Trips TINFO ON TS.TripID = TINFO.TripID
    JOIN Trains T ON TINFO.TrainID = T.TrainID
    WHERE TS.TripID = @Verify_TN1_ID
    ORDER BY TS.SequenceNumber;
END ELSE PRINT 'Could not find TripID for TN1 departing 2023-11-18 10:00:00 to verify TripStations.';
GO

-- Ensure you are in the context of your database
USE TrainTicketSystemDB_V1_FinalDesign;
GO

PRINT '--- Starting: Adding Trips and TripStations for 2025-05-30 ---';

-- =========================================================================================
-- Declare common variables
-- =========================================================================================
DECLARE @TrainSE1_ID INT, @TrainSE2_ID INT, @TrainSPT1_ID INT;
DECLARE @RouteNS_ID INT, @RouteSN_ID INT, @RouteSPT_ID INT;

-- Get Train IDs
SELECT @TrainSE1_ID = TrainID FROM Trains WHERE TrainName = 'SE1';
SELECT @TrainSE2_ID = TrainID FROM Trains WHERE TrainName = 'SE2';
SELECT @TrainSPT1_ID = TrainID FROM Trains WHERE TrainName = 'SPT1';

-- Get Route IDs
SELECT @RouteNS_ID = RouteID FROM Routes WHERE RouteName = N'North-South Line (Hà Nội - Sài Gòn)';
SELECT @RouteSN_ID = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';
SELECT @RouteSPT_ID = RouteID FROM Routes WHERE RouteName = N'Sài Gòn - Phan Thiết Line';

-- Check if essential IDs were found
IF @TrainSE1_ID IS NULL OR @TrainSE2_ID IS NULL OR @TrainSPT1_ID IS NULL OR
   @RouteNS_ID IS NULL OR @RouteSN_ID IS NULL OR @RouteSPT_ID IS NULL
BEGIN
    PRINT 'Error: Could not find one or more required Train or Route IDs. Ensure base dataset script was run successfully.';
    RAISERROR('Essential Train/Route IDs not found. Aborting.', 16, 1);
    RETURN; -- Stop execution
END

-- Station IDs (re-declared for clarity within this script block)
DECLARE @StationHAN_ID INT, @StationVIN_ID INT, @StationHUE_ID INT, @StationDNA_ID INT, @StationDTR_ID INT, @StationPYN_ID INT, @StationNTH_ID INT, @StationBTH_ID INT, @StationSGN_ID INT;
SELECT @StationHAN_ID = StationID FROM Stations WHERE StationCode = 'HAN';
SELECT @StationVIN_ID = StationID FROM Stations WHERE StationCode = 'VIN';
SELECT @StationHUE_ID = StationID FROM Stations WHERE StationCode = 'HUE';
SELECT @StationDNA_ID = StationID FROM Stations WHERE StationCode = 'DNA';
SELECT @StationDTR_ID = StationID FROM Stations WHERE StationCode = 'DTR';
SELECT @StationPYN_ID = StationID FROM Stations WHERE StationCode = 'PYN';
SELECT @StationNTH_ID = StationID FROM Stations WHERE StationCode = 'NTH';
SELECT @StationBTH_ID = StationID FROM Stations WHERE StationCode = 'BTH';
SELECT @StationSGN_ID = StationID FROM Stations WHERE StationCode = 'SGN';

-- =========================================================================================
-- Helper Function/Procedure (Inline for this script) for TripStations population
-- This is to avoid repeating the complex cursor logic excessively.
-- In a real scenario, this would be a proper stored procedure.
-- For this script, we'll adapt the logic directly within each trip's block.
-- =========================================================================================

-- =========================================================================================
-- 1. New Trip: SE1 (HAN -> SGN) - Departure: 2025-05-30 19:30:00 (Duration ~33 hours)
-- =========================================================================================
PRINT 'Processing Trip: SE1 (HAN-SGN) departing 2025-05-30 19:30:00';
DECLARE @Trip_SE1_Date_DepDateTime DATETIME2 = '2025-05-30 19:30:00';
DECLARE @Trip_SE1_Date_ArrDateTime DATETIME2 = DATEADD(hour, 33, @Trip_SE1_Date_DepDateTime); -- Approx. 33 hours
DECLARE @Trip_SE1_Date_ID INT;

IF @TrainSE1_ID IS NOT NULL AND @RouteNS_ID IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE1_ID AND RouteID = @RouteNS_ID AND DepartureDateTime = @Trip_SE1_Date_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainSE1_ID, @RouteNS_ID, @Trip_SE1_Date_DepDateTime, @Trip_SE1_Date_ArrDateTime, 'Scheduled', 1.0);
        SET @Trip_SE1_Date_ID = SCOPE_IDENTITY();
        PRINT 'Trip SE1 (ID: ' + CAST(@Trip_SE1_Date_ID AS VARCHAR) + ') for 2025-05-30 created.';

        -- Populate TripStations for this new SE1 trip
        DECLARE @CurrentTime DATETIME2 = @Trip_SE1_Date_DepDateTime;
        DECLARE @TotalDurationMinutes INT = DATEDIFF(minute, @Trip_SE1_Date_DepDateTime, @Trip_SE1_Date_ArrDateTime);
        DECLARE @SumIntermediateStopTimes INT;
        DECLARE @TotalRouteDistance DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes = SUM(rs.DefaultStopTime)
        FROM RouteStations rs
        WHERE rs.RouteID = @RouteNS_ID
          AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID) 
          AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteNS_ID);
        IF @SumIntermediateStopTimes IS NULL SET @SumIntermediateStopTimes = 0;

        SELECT @TotalRouteDistance = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteNS_ID;
        
        DECLARE @NetTravelTimeMinutes INT = @TotalDurationMinutes - @SumIntermediateStopTimes;

        DECLARE @PrevStationTime DATETIME2 = @Trip_SE1_Date_DepDateTime; 
        DECLARE @PrevStationDistance DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_SE1_Date TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_SE1_Date (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime)
        SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteNS_ID ORDER BY SequenceNumber;

        UPDATE @TripRouteStations_SE1_Date SET ScheduledArrival = NULL, ScheduledDeparture = @Trip_SE1_Date_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE1_Date);
        SET @PrevStationTime = @Trip_SE1_Date_DepDateTime;

        DECLARE cur CURSOR LOCAL FAST_FORWARD FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_SE1_Date WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE1_Date) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID INT, @CurrentSeq INT, @CurrentDist DECIMAL(10,2), @CurrentStopTime INT;
        OPEN cur;
        FETCH NEXT FROM cur INTO @CurrentStationID, @CurrentSeq, @CurrentDist, @CurrentStopTime;

        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance DECIMAL(10,2) = @CurrentDist - @PrevStationDistance;
            DECLARE @SegmentTravelTimeMinutes INT;
            IF @TotalRouteDistance > 0 SET @SegmentTravelTimeMinutes = ROUND((@SegmentDistance / @TotalRouteDistance) * @NetTravelTimeMinutes, 0); ELSE SET @SegmentTravelTimeMinutes = 0;
            
            DECLARE @ScheduledArrivalTime DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes, @PrevStationTime);
            DECLARE @ScheduledDepartureTime DATETIME2 = @ScheduledArrivalTime;

            IF @CurrentSeq < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_SE1_Date) 
                SET @ScheduledDepartureTime = DATEADD(minute, @CurrentStopTime, @ScheduledArrivalTime);
            ELSE 
            BEGIN 
                SET @ScheduledDepartureTime = NULL; 
                SET @ScheduledArrivalTime = @Trip_SE1_Date_ArrDateTime; -- Ensure last arrival matches trip
            END

            UPDATE @TripRouteStations_SE1_Date SET ScheduledArrival = @ScheduledArrivalTime, ScheduledDeparture = @ScheduledDepartureTime WHERE StationID = @CurrentStationID AND SequenceNumber = @CurrentSeq;
            SET @PrevStationTime = @ScheduledDepartureTime; SET @PrevStationDistance = @CurrentDist;
            FETCH NEXT FROM cur INTO @CurrentStationID, @CurrentSeq, @CurrentDist, @CurrentStopTime;
        END
        CLOSE cur; DEALLOCATE cur;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @Trip_SE1_Date_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_SE1_Date
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @Trip_SE1_Date_ID AND ts.StationID = [@TripRouteStations_SE1_Date].StationID);
        PRINT 'TripStations for SE1 (ID: ' + CAST(@Trip_SE1_Date_ID AS VARCHAR) + ') populated.';
    END
    ELSE PRINT 'Trip SE1 (HAN-SGN) departing ' + CONVERT(VARCHAR, @Trip_SE1_Date_DepDateTime, 120) + ' already exists.';
END
ELSE PRINT 'Could not create Trip SE1 (HAN-SGN) for 2025-05-30 due to missing Train/Route ID.';
GO

-- =========================================================================================
-- 2. New Trip: SE2 (SGN -> HAN) - Departure: 2025-05-30 19:30:00 (Duration ~33 hours)
-- =========================================================================================
PRINT 'Processing Trip: SE2 (SGN-HAN) departing 2025-05-30 19:30:00';
DECLARE @Trip_SE2_Date_DepDateTime DATETIME2 = '2025-05-30 19:30:00';
DECLARE @Trip_SE2_Date_ArrDateTime DATETIME2 = DATEADD(hour, 33, @Trip_SE2_Date_DepDateTime);
DECLARE @Trip_SE2_Date_ID INT;
DECLARE @TrainSE2_ID_Local INT; SELECT @TrainSE2_ID_Local = TrainID FROM Trains WHERE TrainName = 'SE2';
DECLARE @RouteSN_ID_Local INT;  SELECT @RouteSN_ID_Local = RouteID FROM Routes WHERE RouteName = N'South-North Line (Sài Gòn - Hà Nội)';

IF @TrainSE2_ID_Local IS NOT NULL AND @RouteSN_ID_Local IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSE2_ID_Local AND RouteID = @RouteSN_ID_Local AND DepartureDateTime = @Trip_SE2_Date_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainSE2_ID_Local, @RouteSN_ID_Local, @Trip_SE2_Date_DepDateTime, @Trip_SE2_Date_ArrDateTime, 'Scheduled', 1.0);
        SET @Trip_SE2_Date_ID = SCOPE_IDENTITY();
        PRINT 'Trip SE2 (ID: ' + CAST(@Trip_SE2_Date_ID AS VARCHAR) + ') for 2025-05-30 created.';

        -- Populate TripStations for this new SE2 trip
        DECLARE @CurrentTime_SE2_Date DATETIME2 = @Trip_SE2_Date_DepDateTime;
        DECLARE @TotalDurationMinutes_SE2_Date INT = DATEDIFF(minute, @Trip_SE2_Date_DepDateTime, @Trip_SE2_Date_ArrDateTime);
        DECLARE @SumIntermediateStopTimes_SE2_Date INT;
        DECLARE @TotalRouteDistance_SE2_Date DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes_SE2_Date = SUM(rs.DefaultStopTime) FROM RouteStations rs WHERE rs.RouteID = @RouteSN_ID_Local AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local) AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSN_ID_Local);
        IF @SumIntermediateStopTimes_SE2_Date IS NULL SET @SumIntermediateStopTimes_SE2_Date = 0;
        SELECT @TotalRouteDistance_SE2_Date = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteSN_ID_Local;
        DECLARE @NetTravelTimeMinutes_SE2_Date INT = @TotalDurationMinutes_SE2_Date - @SumIntermediateStopTimes_SE2_Date;
        DECLARE @PrevStationTime_SE2_Date DATETIME2 = @Trip_SE2_Date_DepDateTime; 
        DECLARE @PrevStationDistance_SE2_Date DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_SE2_Date TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_SE2_Date (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteSN_ID_Local ORDER BY SequenceNumber;
        UPDATE @TripRouteStations_SE2_Date SET ScheduledArrival = NULL, ScheduledDeparture = @Trip_SE2_Date_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE2_Date);
        SET @PrevStationTime_SE2_Date = @Trip_SE2_Date_DepDateTime;

        DECLARE cur_SE2_Date CURSOR LOCAL FAST_FORWARD FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_SE2_Date WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SE2_Date) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID_SE2_Date INT, @CurrentSeq_SE2_Date INT, @CurrentDist_SE2_Date DECIMAL(10,2), @CurrentStopTime_SE2_Date INT;
        OPEN cur_SE2_Date; FETCH NEXT FROM cur_SE2_Date INTO @CurrentStationID_SE2_Date, @CurrentSeq_SE2_Date, @CurrentDist_SE2_Date, @CurrentStopTime_SE2_Date;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance_SE2_Date DECIMAL(10,2) = @CurrentDist_SE2_Date - @PrevStationDistance_SE2_Date;
            DECLARE @SegmentTravelTimeMinutes_SE2_Date INT;
            IF @TotalRouteDistance_SE2_Date > 0 SET @SegmentTravelTimeMinutes_SE2_Date = ROUND((@SegmentDistance_SE2_Date / @TotalRouteDistance_SE2_Date) * @NetTravelTimeMinutes_SE2_Date, 0); ELSE SET @SegmentTravelTimeMinutes_SE2_Date = 0;
            DECLARE @ScheduledArrivalTime_SE2_Date DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes_SE2_Date, @PrevStationTime_SE2_Date);
            DECLARE @ScheduledDepartureTime_SE2_Date DATETIME2 = @ScheduledArrivalTime_SE2_Date;
            IF @CurrentSeq_SE2_Date < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_SE2_Date) SET @ScheduledDepartureTime_SE2_Date = DATEADD(minute, @CurrentStopTime_SE2_Date, @ScheduledArrivalTime_SE2_Date); ELSE BEGIN SET @ScheduledDepartureTime_SE2_Date = NULL; SET @ScheduledArrivalTime_SE2_Date = @Trip_SE2_Date_ArrDateTime; END
            UPDATE @TripRouteStations_SE2_Date SET ScheduledArrival = @ScheduledArrivalTime_SE2_Date, ScheduledDeparture = @ScheduledDepartureTime_SE2_Date WHERE StationID = @CurrentStationID_SE2_Date AND SequenceNumber = @CurrentSeq_SE2_Date;
            SET @PrevStationTime_SE2_Date = @ScheduledDepartureTime_SE2_Date; SET @PrevStationDistance_SE2_Date = @CurrentDist_SE2_Date;
            FETCH NEXT FROM cur_SE2_Date INTO @CurrentStationID_SE2_Date, @CurrentSeq_SE2_Date, @CurrentDist_SE2_Date, @CurrentStopTime_SE2_Date;
        END
        CLOSE cur_SE2_Date; DEALLOCATE cur_SE2_Date;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @Trip_SE2_Date_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_SE2_Date
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @Trip_SE2_Date_ID AND ts.StationID = [@TripRouteStations_SE2_Date].StationID);
        PRINT 'TripStations for SE2 (ID: ' + CAST(@Trip_SE2_Date_ID AS VARCHAR) + ') populated.';
    END
    ELSE PRINT 'Trip SE2 (SGN-HAN) departing ' + CONVERT(VARCHAR, @Trip_SE2_Date_DepDateTime, 120) + ' already exists.';
END
ELSE PRINT 'Could not create Trip SE2 (SGN-HAN) for 2025-05-30 due to missing Train/Route ID.';
GO

-- =========================================================================================
-- 3. New Trip: SPT1 (SGN -> BTH) - Departure: 2025-05-30 06:40:00 (Duration ~4 hours)
-- =========================================================================================
PRINT 'Processing Trip: SPT1 (SGN-BTH) departing 2025-05-30 06:40:00';
DECLARE @Trip_SPT1_Date_DepDateTime DATETIME2 = '2025-05-30 06:40:00';
DECLARE @Trip_SPT1_Date_ArrDateTime DATETIME2 = DATEADD(hour, 4, @Trip_SPT1_Date_DepDateTime);
DECLARE @Trip_SPT1_Date_ID INT;
DECLARE @TrainSPT1_ID_Local INT; SELECT @TrainSPT1_ID_Local = TrainID FROM Trains WHERE TrainName = 'SPT1';
DECLARE @RouteSPT_ID_Local INT;  SELECT @RouteSPT_ID_Local = RouteID FROM Routes WHERE RouteName = N'Sài Gòn - Phan Thiết Line';

IF @TrainSPT1_ID_Local IS NOT NULL AND @RouteSPT_ID_Local IS NOT NULL
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Trips WHERE TrainID = @TrainSPT1_ID_Local AND RouteID = @RouteSPT_ID_Local AND DepartureDateTime = @Trip_SPT1_Date_DepDateTime)
    BEGIN
        INSERT INTO Trips (TrainID, RouteID, DepartureDateTime, ArrivalDateTime, TripStatus, BasePriceMultiplier)
        VALUES (@TrainSPT1_ID_Local, @RouteSPT_ID_Local, @Trip_SPT1_Date_DepDateTime, @Trip_SPT1_Date_ArrDateTime, 'Scheduled', 0.9);
        SET @Trip_SPT1_Date_ID = SCOPE_IDENTITY();
        PRINT 'Trip SPT1 (ID: ' + CAST(@Trip_SPT1_Date_ID AS VARCHAR) + ') for 2025-05-30 created.';

        -- Populate TripStations for this new SPT1 trip
        DECLARE @CurrentTime_SPT1_Date DATETIME2 = @Trip_SPT1_Date_DepDateTime;
        DECLARE @TotalDurationMinutes_SPT1_Date INT = DATEDIFF(minute, @Trip_SPT1_Date_DepDateTime, @Trip_SPT1_Date_ArrDateTime);
        DECLARE @SumIntermediateStopTimes_SPT1_Date INT;
        DECLARE @TotalRouteDistance_SPT1_Date DECIMAL(10,2);
        
        SELECT @SumIntermediateStopTimes_SPT1_Date = SUM(rs.DefaultStopTime) FROM RouteStations rs WHERE rs.RouteID = @RouteSPT_ID_Local AND rs.SequenceNumber > (SELECT MIN(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSPT_ID_Local) AND rs.SequenceNumber < (SELECT MAX(SequenceNumber) FROM RouteStations WHERE RouteID = @RouteSPT_ID_Local);
        IF @SumIntermediateStopTimes_SPT1_Date IS NULL SET @SumIntermediateStopTimes_SPT1_Date = 0; -- SPT route only has 2 stops, so this will be 0.
        SELECT @TotalRouteDistance_SPT1_Date = MAX(rs.DistanceFromStart) FROM RouteStations rs WHERE rs.RouteID = @RouteSPT_ID_Local;
        DECLARE @NetTravelTimeMinutes_SPT1_Date INT = @TotalDurationMinutes_SPT1_Date - @SumIntermediateStopTimes_SPT1_Date;
        DECLARE @PrevStationTime_SPT1_Date DATETIME2 = @Trip_SPT1_Date_DepDateTime; 
        DECLARE @PrevStationDistance_SPT1_Date DECIMAL(10,2) = 0;

        DECLARE @TripRouteStations_SPT1_Date TABLE ( StationID INT, SequenceNumber INT, DistanceFromStart DECIMAL(10,2), DefaultStopTime INT, ScheduledArrival DATETIME2 NULL, ScheduledDeparture DATETIME2 NULL );
        INSERT INTO @TripRouteStations_SPT1_Date (StationID, SequenceNumber, DistanceFromStart, DefaultStopTime) SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM RouteStations WHERE RouteID = @RouteSPT_ID_Local ORDER BY SequenceNumber;
        UPDATE @TripRouteStations_SPT1_Date SET ScheduledArrival = NULL, ScheduledDeparture = @Trip_SPT1_Date_DepDateTime WHERE SequenceNumber = (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SPT1_Date);
        SET @PrevStationTime_SPT1_Date = @Trip_SPT1_Date_DepDateTime;

        DECLARE cur_SPT1_Date CURSOR LOCAL FAST_FORWARD FOR SELECT StationID, SequenceNumber, DistanceFromStart, DefaultStopTime FROM @TripRouteStations_SPT1_Date WHERE SequenceNumber > (SELECT MIN(SequenceNumber) FROM @TripRouteStations_SPT1_Date) ORDER BY SequenceNumber;
        DECLARE @CurrentStationID_SPT1_Date INT, @CurrentSeq_SPT1_Date INT, @CurrentDist_SPT1_Date DECIMAL(10,2), @CurrentStopTime_SPT1_Date INT;
        OPEN cur_SPT1_Date; FETCH NEXT FROM cur_SPT1_Date INTO @CurrentStationID_SPT1_Date, @CurrentSeq_SPT1_Date, @CurrentDist_SPT1_Date, @CurrentStopTime_SPT1_Date;
        WHILE @@FETCH_STATUS = 0
        BEGIN
            DECLARE @SegmentDistance_SPT1_Date DECIMAL(10,2) = @CurrentDist_SPT1_Date - @PrevStationDistance_SPT1_Date;
            DECLARE @SegmentTravelTimeMinutes_SPT1_Date INT;
            IF @TotalRouteDistance_SPT1_Date > 0 SET @SegmentTravelTimeMinutes_SPT1_Date = ROUND((@SegmentDistance_SPT1_Date / @TotalRouteDistance_SPT1_Date) * @NetTravelTimeMinutes_SPT1_Date, 0); ELSE SET @SegmentTravelTimeMinutes_SPT1_Date = 0;
            DECLARE @ScheduledArrivalTime_SPT1_Date DATETIME2 = DATEADD(minute, @SegmentTravelTimeMinutes_SPT1_Date, @PrevStationTime_SPT1_Date);
            DECLARE @ScheduledDepartureTime_SPT1_Date DATETIME2 = @ScheduledArrivalTime_SPT1_Date;
            IF @CurrentSeq_SPT1_Date < (SELECT MAX(SequenceNumber) FROM @TripRouteStations_SPT1_Date) SET @ScheduledDepartureTime_SPT1_Date = DATEADD(minute, @CurrentStopTime_SPT1_Date, @ScheduledArrivalTime_SPT1_Date); ELSE BEGIN SET @ScheduledDepartureTime_SPT1_Date = NULL; SET @ScheduledArrivalTime_SPT1_Date = @Trip_SPT1_Date_ArrDateTime; END
            UPDATE @TripRouteStations_SPT1_Date SET ScheduledArrival = @ScheduledArrivalTime_SPT1_Date, ScheduledDeparture = @ScheduledDepartureTime_SPT1_Date WHERE StationID = @CurrentStationID_SPT1_Date AND SequenceNumber = @CurrentSeq_SPT1_Date;
            SET @PrevStationTime_SPT1_Date = @ScheduledDepartureTime_SPT1_Date; SET @PrevStationDistance_SPT1_Date = @CurrentDist_SPT1_Date;
            FETCH NEXT FROM cur_SPT1_Date INTO @CurrentStationID_SPT1_Date, @CurrentSeq_SPT1_Date, @CurrentDist_SPT1_Date, @CurrentStopTime_SPT1_Date;
        END
        CLOSE cur_SPT1_Date; DEALLOCATE cur_SPT1_Date;

        INSERT INTO TripStations (TripID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture)
        SELECT @Trip_SPT1_Date_ID, StationID, SequenceNumber, ScheduledArrival, ScheduledDeparture FROM @TripRouteStations_SPT1_Date
        WHERE NOT EXISTS (SELECT 1 FROM TripStations ts WHERE ts.TripID = @Trip_SPT1_Date_ID AND ts.StationID = [@TripRouteStations_SPT1_Date].StationID);
        PRINT 'TripStations for SPT1 (ID: ' + CAST(@Trip_SPT1_Date_ID AS VARCHAR) + ') populated.';
    END
    ELSE PRINT 'Trip SPT1 (SGN-BTH) departing ' + CONVERT(VARCHAR, @Trip_SPT1_Date_DepDateTime, 120) + ' already exists.';
END
ELSE PRINT 'Could not create Trip SPT1 (SGN-BTH) for 2025-05-30 due to missing Train/Route ID.';
GO


PRINT '--- Finished: Adding Trips and TripStations for 2025-05-30 ---';
GO

-- =========================================================================================
-- Sample Queries for Verification of NEWLY ADDED trips for 2025-05-30
-- =========================================================================================
PRINT '--- Sample Verification Queries for trips on 2025-05-30 ---';

PRINT 'Show all Trips departing on 2025-05-30';
SELECT T.TripID, TR.TrainName, R.RouteName, T.DepartureDateTime, T.ArrivalDateTime, T.TripStatus
FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
JOIN Routes R ON T.RouteID = R.RouteID
WHERE CAST(T.DepartureDateTime AS DATE) = '2025-05-30'
ORDER BY T.DepartureDateTime, TR.TrainName;
GO

PRINT 'Show TripStations for SE1 trip (HAN-SGN departing 2025-05-30 19:30:00)';
DECLARE @Verify_SE1_2025_ID INT;
SELECT @Verify_SE1_2025_ID = TripID FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
WHERE TR.TrainName = 'SE1' AND T.DepartureDateTime = '2025-05-30 19:30:00';

IF @Verify_SE1_2025_ID IS NOT NULL
BEGIN
    SELECT TS.TripStationID, T.TrainName AS TripTrain, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
    FROM TripStations TS
    JOIN Stations S ON TS.StationID = S.StationID
    JOIN Trips TINFO ON TS.TripID = TINFO.TripID
    JOIN Trains T ON TINFO.TrainID = T.TrainID
    WHERE TS.TripID = @Verify_SE1_2025_ID
    ORDER BY TS.SequenceNumber;
END ELSE PRINT 'Could not find TripID for SE1 departing 2025-05-30 19:30:00 to verify TripStations.';
GO

PRINT 'Show TripStations for SPT1 trip (SGN-BTH departing 2025-05-30 06:40:00)';
DECLARE @Verify_SPT1_2025_ID INT;
SELECT @Verify_SPT1_2025_ID = TripID FROM Trips T
JOIN Trains TR ON T.TrainID = TR.TrainID
WHERE TR.TrainName = 'SPT1' AND T.DepartureDateTime = '2025-05-30 06:40:00';

IF @Verify_SPT1_2025_ID IS NOT NULL
BEGIN
    SELECT TS.TripStationID, T.TrainName AS TripTrain, TS.TripID, S.StationName, TS.SequenceNumber, TS.ScheduledArrival, TS.ScheduledDeparture
    FROM TripStations TS
    JOIN Stations S ON TS.StationID = S.StationID
    JOIN Trips TINFO ON TS.TripID = TINFO.TripID
    JOIN Trains T ON TINFO.TrainID = T.TrainID
    WHERE TS.TripID = @Verify_SPT1_2025_ID
    ORDER BY TS.SequenceNumber;
END ELSE PRINT 'Could not find TripID for SPT1 departing 2025-05-30 06:40:00 to verify TripStations.';
GO
