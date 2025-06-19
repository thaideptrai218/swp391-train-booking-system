CREATE OR ALTER PROCEDURE dbo.SearchTrips -- Use CREATE OR ALTER for easier updates
    @OriginStationCode NVARCHAR(20),
    @DestinationStationCode NVARCHAR(20),
    @DepartureDate DATE,
    @ReturnDate DATE = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @OriginStationID INT;
    DECLARE @DestinationStationID INT;
    -- Removed @OriginStationName, @DestinationStationName as they aren't used beyond validation if RAISERROR is used

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
            ABS(ISNULL(RS_Dest.DistanceFromStart, 0) - ISNULL(RS_Origin.DistanceFromStart, 0)) AS DistanceTraveledKm -- Use ABS and ISNULL for safety
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID AND TS_Origin.TripID = TS_Dest.TripID -- ensure same trip
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID
        -- Join to RouteStations for the specific leg's origin
        INNER JOIN dbo.RouteStations RS_Origin
            ON T.RouteID = RS_Origin.RouteID AND RS_Origin.StationID = TS_Origin.StationID
        -- Join to RouteStations for the specific leg's destination
        INNER JOIN dbo.RouteStations RS_Dest
            ON T.RouteID = RS_Dest.RouteID AND RS_Dest.StationID = TS_Dest.StationID
        WHERE
            TS_Origin.StationID = @OriginStationID      -- Use parameter
            AND TS_Dest.StationID = @DestinationStationID -- Use parameter
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @DepartureDate -- Use parameter
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
            ABS(ISNULL(RS_Dest.DistanceFromStart,0) - ISNULL(RS_Origin.DistanceFromStart,0)) AS DistanceTraveledKm
        FROM
            dbo.Trips T
        INNER JOIN dbo.Trains TR ON T.TrainID = TR.TrainID
        INNER JOIN dbo.Routes R ON T.RouteID = R.RouteID
        INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
        INNER JOIN dbo.Stations OS_Details ON TS_Origin.StationID = OS_Details.StationID
        INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID AND TS_Origin.TripID = TS_Dest.TripID
        INNER JOIN dbo.Stations DS_Details ON TS_Dest.StationID = DS_Details.StationID
        INNER JOIN dbo.RouteStations RS_Origin
            ON T.RouteID = RS_Origin.RouteID AND RS_Origin.StationID = TS_Origin.StationID
        INNER JOIN dbo.RouteStations RS_Dest
            ON T.RouteID = RS_Dest.RouteID AND RS_Dest.StationID = TS_Dest.StationID
        WHERE
            @ReturnDate IS NOT NULL
            AND TS_Origin.StationID = @DestinationStationID -- Swapped
            AND TS_Dest.StationID = @OriginStationID       -- Swapped
            AND CAST(TS_Origin.ScheduledDeparture AS DATE) = @ReturnDate -- Use parameter
            AND TS_Origin.SequenceNumber < TS_Dest.SequenceNumber
            AND T.TripStatus = 'Scheduled'
            AND TR.IsActive = 1
    ), SeatAvailabilityPerTrip AS (
    SELECT 
        T.TripID,
        TS_Origin.StationID AS OriginStationID,
        TS_Dest.StationID AS DestinationStationID,
        COUNT(CASE WHEN OT.SeatID IS NOT NULL THEN 1 END) AS OccupiedSeats,
        COUNT(CASE WHEN OT.SeatID IS NULL THEN 1 END) AS AvailableSeats
    FROM dbo.Trips T
    INNER JOIN dbo.TripStations TS_Origin ON T.TripID = TS_Origin.TripID
    INNER JOIN dbo.TripStations TS_Dest ON T.TripID = TS_Dest.TripID
    INNER JOIN Seats S ON T.TrainID IN (
        SELECT TrainID FROM dbo.Coaches C WHERE C.CoachID = S.CoachID
    )
    INNER JOIN Coaches C ON S.CoachID = C.CoachID
    LEFT JOIN (
        SELECT TKT.SeatID, TKT.TripID
        FROM dbo.Tickets TKT
        INNER JOIN dbo.TripStations TKT_Start_TS ON TKT.TripID = TKT_Start_TS.TripID 
            AND TKT.StartStationID = TKT_Start_TS.StationID
        INNER JOIN dbo.TripStations TKT_End_TS ON TKT.TripID = TKT_End_TS.TripID 
            AND TKT.EndStationID = TKT_End_TS.StationID
        WHERE TKT.TicketStatus IN ('Valid', 'Confirmed', 'Paid')
          AND TKT_Start_TS.SequenceNumber < TKT_End_TS.SequenceNumber
    ) AS OT ON OT.SeatID = S.SeatID AND OT.TripID = T.TripID
    WHERE TS_Origin.SequenceNumber < TS_Dest.SequenceNumber
    GROUP BY T.TripID, TS_Origin.StationID, TS_Dest.StationID
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
LEFT JOIN SeatAvailabilityPerTrip SA 
    ON SA.TripID = FT.TripID
    AND SA.OriginStationID = (SELECT StationID FROM dbo.Stations WHERE StationName = FT.OriginStation)
    AND SA.DestinationStationID = (SELECT StationID FROM dbo.Stations WHERE StationName = FT.DestinationStation)
ORDER BY
    FT.LegType,
    FT.DepartureTime;
END
GO

PRINT 'Stored procedure dbo.SearchTrips created/recreated successfully.'; -- Changed message for clarity
GO