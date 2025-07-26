USE [TrainTicketSystemDB_V2]
GO
/****** Object:  StoredProcedure [dbo].[SearchTrips]    Script Date: 7/27/2025 1:20:55 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[SearchTrips]
    @OriginStationCode NVARCHAR(20),
    @DestinationStationCode NVARCHAR(20),
    @DepartureDate DATE,
    @ReturnDate DATE = NULL,
	@NumberOfPassenger INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @OriginStationID INT;
    DECLARE @DestinationStationID INT;

    SELECT @OriginStationID = StationID FROM dbo.Stations WHERE StationCode = @OriginStationCode;
    SELECT @DestinationStationID = StationID FROM dbo.Stations WHERE StationCode = @DestinationStationCode;

    IF @OriginStationID IS NULL BEGIN RAISERROR('Invalid Origin Station Code: %s.', 16, 1, @OriginStationCode); RETURN; END
    IF @DestinationStationID IS NULL BEGIN RAISERROR('Invalid Destination Station Code: %s.', 16, 1, @DestinationStationCode); RETURN; END
    IF @OriginStationID = @DestinationStationID BEGIN RAISERROR('Origin and Destination stations cannot be the same.', 16, 1); RETURN; END
    ;

    WITH FoundTrips AS (
        -- Outbound Trips
        SELECT
            'Outbound' AS LegType,
            T.TripID,
            TR.TrainID,
            TR.TrainName,
            R.RouteName,
            OS_Details.StationName AS OriginStation,
            TS_Origin.ScheduledDeparture AS DepartureTime,
            DS_Details.StationName AS DestinationStation,
            TS_Dest.ScheduledArrival AS ArrivalTime,
            DATEDIFF(minute, TS_Origin.ScheduledDeparture, TS_Dest.ScheduledArrival) AS DurationMinutes,
            T.DepartureDateTime AS TripOverallDeparture,
            T.ArrivalDateTime AS TripOverallArrival,
            ABS(ISNULL(RS_Dest.DistanceFromStart, 0) - ISNULL(RS_Origin.DistanceFromStart, 0)) AS DistanceTraveledKm,
            @OriginStationID AS LegOriginStationID, -- Added to pass to the function
            @DestinationStationID AS LegDestinationStationID -- Added to pass to the function
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID AND TS_Origin.TripID = TS_Dest.TripID
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID
        INNER JOIN dbo.RouteStations RS_Origin ON T.RouteID = RS_Origin.RouteID AND RS_Origin.StationID = TS_Origin.StationID
        INNER JOIN dbo.RouteStations RS_Dest ON T.RouteID = RS_Dest.RouteID AND RS_Dest.StationID = TS_Dest.StationID
        WHERE
            TS_Origin.StationID = @OriginStationID
            AND TS_Dest.StationID = @DestinationStationID
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @DepartureDate
            AND TS_Origin.SequenceNumber < TS_Dest.SequenceNumber
            AND T.TripStatus = 'Scheduled'
            AND TR.IsActive = 1

        UNION ALL

        -- Return Trips
        SELECT
            'Return' AS LegType,
            T.TripID,
            TR.TrainID,
            TR.TrainName,
            R.RouteName,
            OS_Details.StationName AS OriginStation,
            TS_Origin.ScheduledDeparture AS DepartureTime,
            DS_Details.StationName AS DestinationStation,
            TS_Dest.ScheduledArrival AS ArrivalTime,
            DATEDIFF(minute, TS_Origin.ScheduledDeparture, TS_Dest.ScheduledArrival) AS DurationMinutes,
            T.DepartureDateTime AS TripOverallDeparture,
            T.ArrivalDateTime AS TripOverallArrival,
            ABS(ISNULL(RS_Dest.DistanceFromStart,0) - ISNULL(RS_Origin.DistanceFromStart,0)) AS DistanceTraveledKm,
            @DestinationStationID AS LegOriginStationID, -- Swapped for return trip
            @OriginStationID AS LegDestinationStationID  -- Swapped for return trip
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID AND TS_Origin.TripID = TS_Dest.TripID
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID
        INNER JOIN dbo.RouteStations RS_Origin ON T.RouteID = RS_Origin.RouteID AND RS_Origin.StationID = TS_Origin.StationID
        INNER JOIN dbo.RouteStations RS_Dest ON T.RouteID = RS_Dest.RouteID AND RS_Dest.StationID = TS_Dest.StationID
        WHERE
            @ReturnDate IS NOT NULL
            AND TS_Origin.StationID = @DestinationStationID -- Swapped
            AND TS_Dest.StationID = @OriginStationID      -- Swapped
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @ReturnDate
            AND TS_Origin.SequenceNumber < TS_Dest.SequenceNumber
            AND T.TripStatus = 'Scheduled'
            AND TR.IsActive = 1
    )
SELECT
    FT.LegType,
    FT.TripID,
    FT.TrainID,
    FT.TrainName,
    FT.RouteName,
    FT.OriginStation,
    FT.DepartureTime,
    FT.DestinationStation,
    FT.ArrivalTime,
    FT.DurationMinutes,
    FT.DistanceTraveledKm,
    FT.TripOverallDeparture,
    FT.TripOverallArrival,
    ISNULL(SA.AvailableSeats, 0) AS AvailableSeats,
    ISNULL(SA.OccupiedSeats, 0) AS OccupiedSeats
FROM FoundTrips FT
-- Replaced the CTE with an OUTER APPLY to the function for each row in FoundTrips
OUTER APPLY dbo.fn_GetSeatStatusSummary(FT.LegOriginStationID, FT.LegDestinationStationID, FT.TripID) AS SA
WHERE ISNULL(SA.AvailableSeats, 0) >= @NumberOfPassenger
ORDER BY
    FT.LegType,
    FT.DepartureTime;
END
    