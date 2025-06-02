-- Ensure you are in the context of your database
USE TrainTicketSystemDB_V1_FinalDesign;
GO

-- =========================================================================================
-- Stored Procedure: SearchTrips
-- Description: Searches for train trips based on origin, destination, departure date,
--              and an optional return date.
-- =========================================================================================
IF OBJECT_ID('dbo.SearchTrips', 'P') IS NOT NULL
BEGIN
    DROP PROCEDURE dbo.SearchTrips;
    PRINT 'Stored procedure dbo.SearchTrips dropped and will be recreated.';
END
GO

CREATE PROCEDURE dbo.SearchTrips
    @OriginStationCode NVARCHAR(20),
    @DestinationStationCode NVARCHAR(20),
    @DepartureDate DATE,
    @ReturnDate DATE = NULL -- Optional: Set to a date for round-trip, or NULL for one-way
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @OriginStationID INT;
    DECLARE @DestinationStationID INT;
    DECLARE @OriginStationName NVARCHAR(100);
    DECLARE @DestinationStationName NVARCHAR(100);

    -- 1. Validate Station Codes and Get Station IDs
    SELECT @OriginStationID = StationID, @OriginStationName = StationName
    FROM dbo.Stations
    WHERE StationCode = @OriginStationCode;

    SELECT @DestinationStationID = StationID, @DestinationStationName = StationName
    FROM dbo.Stations
    WHERE StationCode = @DestinationStationCode;

    IF @OriginStationID IS NULL
    BEGIN
        RAISERROR('Invalid Origin Station Code: %s.', 16, 1, @OriginStationCode);
        RETURN;
    END

    IF @DestinationStationID IS NULL
    BEGIN
        RAISERROR('Invalid Destination Station Code: %s.', 16, 1, @DestinationStationCode);
        RETURN;
    END

    IF @OriginStationID = @DestinationStationID
    BEGIN
        RAISERROR('Origin and Destination stations cannot be the same.', 16, 1);
        RETURN;
    END; -- <<<<<<<< ****** ADDED SEMICOLON HERE ******

    -- 2. Perform the Search using a Common Table Expression (CTE)
    WITH FoundTrips AS (
        -- Outbound Trips
        SELECT
            'Outbound' AS LegType,
            T.TripID,
            TR.TrainName,
            R.RouteName,
            OS_Details.StationName AS OriginStation,
            TS_Origin.ScheduledDeparture AS DepartureTime,
            DS_Details.StationName AS DestinationStation,
            TS_Dest.ScheduledArrival AS ArrivalTime,
            DATEDIFF(minute, TS_Origin.ScheduledDeparture, TS_Dest.ScheduledArrival) AS DurationMinutes,
            T.DepartureDateTime AS TripOverallDeparture, -- Overall trip start from its first station
            T.ArrivalDateTime AS TripOverallArrival     -- Overall trip end at its last station
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID
        WHERE
            TS_Origin.StationID = @OriginStationID      -- Use resolved ID
            AND TS_Dest.StationID = @DestinationStationID -- Use resolved ID
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @DepartureDate
            AND TS_Origin.SequenceNumber < TS_Dest.SequenceNumber -- Crucial: Origin must be before Destination
            AND T.TripStatus = 'Scheduled'                -- Only consider scheduled trips
            AND TR.IsActive = 1                           -- Only consider active trains

        UNION ALL

        -- Return Trips (only if @ReturnDate is provided)
        SELECT
            'Return' AS LegType,
            T.TripID,
            TR.TrainName,
            R.RouteName,
            OS_Details.StationName AS OriginStation,    -- This will be the original @DestinationStationName
            TS_Origin.ScheduledDeparture AS DepartureTime,
            DS_Details.StationName AS DestinationStation, -- This will be the original @OriginStationName
            TS_Dest.ScheduledArrival AS ArrivalTime,
            DATEDIFF(minute, TS_Origin.ScheduledDeparture, TS_Dest.ScheduledArrival) AS DurationMinutes,
            T.DepartureDateTime AS TripOverallDeparture,
            T.ArrivalDateTime AS TripOverallArrival
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID -- Origin for return leg
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID -- Destination for return leg
        WHERE
            @ReturnDate IS NOT NULL -- This entire part of UNION ALL is conditional
            AND TS_Origin.StationID = @DestinationStationID -- Swapped for return trip
            AND TS_Dest.StationID = @OriginStationID       -- Swapped for return trip
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @ReturnDate
            AND TS_Origin.SequenceNumber < TS_Dest.SequenceNumber
            AND T.TripStatus = 'Scheduled'
            AND TR.IsActive = 1
    )
    SELECT
        LegType,
        TripID,
        TrainName,
        RouteName,
        OriginStation,
        DepartureTime,
        DestinationStation,
        ArrivalTime,
        DurationMinutes,
        TripOverallDeparture,
        TripOverallArrival
    FROM FoundTrips
    ORDER BY
        LegType,        -- Groups 'Outbound' first, then 'Return'
        DepartureTime;  -- Orders trips within each leg by their departure time

END
GO

PRINT 'Stored procedure dbo.SearchTrips created/recreated successfully.'; -- Changed message for clarity
GO

-- =========================================================================================
-- Example Usage of the Stored Procedure (KEEP THIS THE SAME)
-- =========================================================================================
PRINT '--- Example: One-Way Trip Search (SGN to DNA on 2023-11-15) ---';
EXEC dbo.SearchTrips
    @OriginStationCode = 'SGN',
    @DestinationStationCode = 'DNA',
    @DepartureDate = '2023-11-15',
    @ReturnDate = NULL;
GO

PRINT '--- Example: One-Way Trip Search (HAN to DNA on 2023-11-15) ---';
EXEC dbo.SearchTrips
    @OriginStationCode = 'HAN',
    @DestinationStationCode = 'DNA',
    @DepartureDate = '2023-11-15'; -- @ReturnDate is NULL by default if not specified
GO

PRINT '--- Example: Round Trip Search (HAN to DNA on 2023-11-15, Return DNA to HAN on 2023-11-17) ---';
EXEC dbo.SearchTrips
    @OriginStationCode = 'HAN',
    @DestinationStationCode = 'DNA',
    @DepartureDate = '2023-11-15',
    @ReturnDate = '2023-11-17';
GO

PRINT '--- Example: Invalid Origin Station Code ---';
BEGIN TRY
    EXEC dbo.SearchTrips
        @OriginStationCode = 'INVALID_CODE',
        @DestinationStationCode = 'DNA',
        @DepartureDate = '2023-11-15';
END TRY
BEGIN CATCH
    PRINT 'Error caught: ' + ERROR_MESSAGE();
END CATCH
GO

PRINT '--- Example: Origin and Destination are the same ---';
BEGIN TRY
    EXEC dbo.SearchTrips
        @OriginStationCode = 'SGN',
        @DestinationStationCode = 'SGN',
        @DepartureDate = '2023-11-15';
END TRY
BEGIN CATCH
    PRINT 'Error caught: ' + ERROR_MESSAGE();
END CATCH
GO

-- =========================================================================================
-- Example Usage of the Stored Procedure
-- =========================================================================================
PRINT '--- Example: One-Way Trip Search (SGN to DNA on 2023-11-15) ---';
EXEC dbo.SearchTrips
    @OriginStationCode = 'SGN',
    @DestinationStationCode = 'DNA',
    @DepartureDate = '2023-11-15',
    @ReturnDate = NULL;
GO

PRINT '--- Example: One-Way Trip Search (HAN to DNA on 2023-11-15) ---';
EXEC dbo.SearchTrips
    @OriginStationCode = 'HAN',
    @DestinationStationCode = 'SGN',
    @DepartureDate = '2025-05-30'; -- @ReturnDate is NULL by default if not specified
GO

PRINT '--- Example: Round Trip Search (HAN to DNA on 2023-11-15, Return DNA to HAN on 2023-11-17) ---';
-- Note: You need to ensure your dataset has trips that would satisfy the return leg (e.g. SE4 on the SGN-HAN route passing DNA on 2023-11-17 or SE2 passing DNA to HAN)
EXEC dbo.SearchTrips
    @OriginStationCode = 'HAN',
    @DestinationStationCode = 'DNA',
    @DepartureDate = '2023-11-15',
    @ReturnDate = '2023-11-17';
GO

PRINT '--- Example: Invalid Origin Station Code ---';
BEGIN TRY
    EXEC dbo.SearchTrips
        @OriginStationCode = 'INVALID_CODE',
        @DestinationStationCode = 'DNA',
        @DepartureDate = '2023-11-15';
END TRY
BEGIN CATCH
    PRINT 'Error caught: ' + ERROR_MESSAGE();
END CATCH
GO

PRINT '--- Example: Origin and Destination are the same ---';
BEGIN TRY
    EXEC dbo.SearchTrips
        @OriginStationCode = 'SGN',
        @DestinationStationCode = 'SGN',
        @DepartureDate = '2023-11-15';
END TRY
BEGIN CATCH
    PRINT 'Error caught: ' + ERROR_MESSAGE();
END CATCH
GO

SELECT * FROM TripStations