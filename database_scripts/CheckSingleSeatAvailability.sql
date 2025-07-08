IF OBJECT_ID('dbo.CheckSingleSeatAvailability', 'P') IS NOT NULL
    DROP PROCEDURE dbo.CheckSingleSeatAvailability;
GO

CREATE PROCEDURE dbo.CheckSingleSeatAvailability
    @TripID_Input INT,
    @SeatID_Input INT,
    @LegOriginStationID_Input INT, -- This is LegStartStationID for the request
    @LegDestinationStationID_Input INT -- This is LegEndStationID for the request
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @AvailabilityStatus VARCHAR(20);
    DECLARE @LegOriginSeq INT, @LegDestSeq INT;

    -- Validate core inputs (basic check)
    IF @TripID_Input IS NULL OR @SeatID_Input IS NULL OR @LegOriginStationID_Input IS NULL OR @LegDestinationStationID_Input IS NULL
    BEGIN
        RAISERROR('TripID, SeatID, LegOriginStationID, and LegDestinationStationID must be provided.', 16, 1);
        SELECT 'Error: Invalid Input' AS AvailabilityStatus;
        RETURN;
    END

    -- Get sequence numbers for the requested leg from TripStations
    SELECT @LegOriginSeq = TS.SequenceNumber 
    FROM dbo.TripStations TS 
    WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegOriginStationID_Input;

    SELECT @LegDestSeq = TS.SequenceNumber 
    FROM dbo.TripStations TS 
    WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegDestinationStationID_Input;

    IF @LegOriginSeq IS NULL
    BEGIN
        RAISERROR('Requested Leg Origin Station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegOriginStationID_Input, @TripID_Input);
        SELECT 'Error: Invalid Leg Origin' AS AvailabilityStatus;
        RETURN;
    END
    IF @LegDestSeq IS NULL
    BEGIN
        RAISERROR('Requested Leg Destination Station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegDestinationStationID_Input, @TripID_Input);
        SELECT 'Error: Invalid Leg Destination' AS AvailabilityStatus;
        RETURN;
    END
    IF @LegOriginSeq >= @LegDestSeq
    BEGIN
        RAISERROR('Invalid leg: Origin sequence (%d) must be less than Destination sequence (%d).', 16, 1, @LegOriginSeq, @LegDestSeq);
        SELECT 'Error: Invalid Leg Sequence' AS AvailabilityStatus;
        RETURN;
    END

    -- Check seat status
    SELECT @AvailabilityStatus = 
        CASE
            -- 1. Check if the seat itself is disabled
            WHEN S.IsEnabled = 0 THEN 'Disabled'
            
            -- 2. Check for permanent bookings (Tickets table) that overlap the requested leg
            WHEN EXISTS (
                SELECT 1 
                FROM dbo.Tickets TKT
                INNER JOIN dbo.TripStations TKT_Start_TS ON TKT.TripID = TKT_Start_TS.TripID AND TKT.StartStationID = TKT_Start_TS.StationID
                INNER JOIN dbo.TripStations TKT_End_TS ON TKT.TripID = TKT_End_TS.TripID AND TKT.EndStationID = TKT_End_TS.StationID
                WHERE TKT.SeatID = S.SeatID 
                  AND TKT.TripID = @TripID_Input
                  AND TKT.TicketStatus IN ('Valid', 'Confirmed') -- Define your final booked statuses
                  -- Overlap condition: Ticket's leg overlaps with the requested leg
                  AND (TKT_Start_TS.SequenceNumber < @LegDestSeq AND TKT_End_TS.SequenceNumber > @LegOriginSeq)
            ) THEN 'Occupied'
            
            -- 3. Check for active temporary holds (TemporarySeatHolds table) by ANY session that overlap the requested leg
            WHEN EXISTS (
                SELECT 1 
                FROM dbo.TemporarySeatHolds TSH
                INNER JOIN dbo.TripStations TSH_LegStart_TS ON TSH.TripID = TSH_LegStart_TS.TripID AND TSH.legOriginStationId = TSH_LegStart_TS.StationID
                INNER JOIN dbo.TripStations TSH_LegEnd_TS ON TSH.TripID = TSH_LegEnd_TS.TripID AND TSH.legDestinationStationId = TSH_LegEnd_TS.StationID
                WHERE TSH.SeatID = S.SeatID 
                  AND TSH.TripID = @TripID_Input
                  AND TSH.ExpiresAt > GETDATE() -- Active hold
                  -- Overlap condition: Hold's leg overlaps with the requested leg
                  AND (TSH_LegStart_TS.SequenceNumber < @LegDestSeq AND TSH_LegEnd_TS.SequenceNumber > @LegOriginSeq)
            ) THEN 'Occupied'
            
            -- 4. If none of the above, it's available
            ELSE 'Available'
        END
    FROM dbo.Seats S
    WHERE S.SeatID = @SeatID_Input; 
    -- Assuming SeatID is globally unique. If SeatID is only unique per Coach, you'd need CoachID_Input
    -- and join with Coaches table to ensure you're checking the seat on the correct coach of the trip.
    -- For simplicity here, assuming SeatID is sufficient to identify the seat globally or within the context of the trip.

    IF @AvailabilityStatus IS NULL
    BEGIN
        -- This implies SeatID_Input does not exist in the Seats table.
        RAISERROR('Seat (ID: %d) not found.', 16, 1, @SeatID_Input);
        SELECT 'Error: Seat Not Found' AS AvailabilityStatus;
        RETURN;
    END

    -- Output the status
    SELECT @AvailabilityStatus AS AvailabilityStatus;

END
GO

PRINT 'Stored Procedure dbo.CheckSingleSeatAvailability created successfully.';
GO

EXEC dbo.CheckSingleSeatAvailability
	@TripID_Input = 8,
	@seatID_Input = 23,
	@LegOriginStationID_Input = 13,
	@LegDestinationStationID_input = 11