USE TrainTicketSystemDB_V2
GO

ALTER FUNCTION dbo.fn_GetSeatStatusSummary (
    @FromStation INT,
    @ToStation INT,
    @Trip INT
)
RETURNS TABLE
AS
RETURN
(
    WITH SeatStatusCTE AS (
        SELECT 
            T.TripID,
            CASE 
                WHEN EXISTS (
                    SELECT 1
                    FROM Tickets tic 
                    JOIN TripStations ts3 ON tic.TripID = ts3.TripID AND tic.StartStationID = ts3.StationID
                    JOIN TripStations ts4 ON tic.TripID = ts4.TripID AND tic.EndStationID = ts4.StationID
                    WHERE 
                        T.TripID = tic.TripID
                        AND tic.SeatID = S.SeatID
                        AND tic.TicketStatus IN ('Valid', 'Confirmed', 'Paid') 
                        AND ts3.SequenceNumber < ts2.SequenceNumber 
                        AND ts4.SequenceNumber > ts1.SequenceNumber
                ) THEN 'Booked'
                WHEN EXISTS (
                    SELECT 1
                    FROM TemporarySeatHolds tsh
                    JOIN TripStations ts3 ON tsh.TripID = ts3.TripID AND tsh.legOriginStationId = ts3.StationID
                    JOIN TripStations ts4 ON tsh.TripID = ts4.TripID AND tsh.legDestinationStationId = ts4.StationID
                    WHERE 
                        T.TripID = tsh.TripID
                        AND tsh.SeatID = S.SeatID
                        AND ts3.SequenceNumber < ts2.SequenceNumber 
                        AND ts4.SequenceNumber > ts1.SequenceNumber
                        AND tsh.ExpiresAt > GETDATE()
                ) THEN 'Booked'
                ELSE 'Available' 
            END AS Status
        FROM Trips T 
        JOIN TripStations TS1 ON T.TripID = TS1.TripID AND TS1.StationID = @FromStation
        JOIN TripStations TS2 ON T.TripID = TS2.TripID AND TS2.StationID = @ToStation
        JOIN Coaches C ON T.TrainID = C.TrainID
        JOIN Seats S ON C.CoachID = S.CoachID
        WHERE 
            T.TripID = @Trip
            AND TS1.SequenceNumber < TS2.SequenceNumber
    )

    SELECT 
        TripID, 
        COUNT (CASE WHEN Status = 'Available' THEN 1 END) AS AvailableSeats,
        COUNT (CASE WHEN Status = 'Booked' THEN 1 END) AS OccupiedSeats
    FROM SeatStatusCTE
    GROUP BY TripID
);
GO