-- Stored Procedure to get seat type pricing summary for a trip
-- This procedure returns aggregated pricing information by seat type
-- without the detailed seat-by-seat availability from GetCoachSeatsWithAvailabilityAndPrice

IF OBJECT_ID('dbo.GetTripSeatTypePricing', 'P') IS NOT NULL
    DROP PROCEDURE dbo.GetTripSeatTypePricing;
GO

CREATE PROCEDURE dbo.GetTripSeatTypePricing
    @TripID_Input INT,
    @LegOriginStationID_Input INT,
    @LegDestinationStationID_Input INT,
    @BookingDateTime_Input DATETIME2,
    @IsRoundTrip_Input BIT,
    @CurrentUserSessionID_Input NVARCHAR(100) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Input validations
    IF @TripID_Input IS NULL OR @LegOriginStationID_Input IS NULL OR @LegDestinationStationID_Input IS NULL OR @BookingDateTime_Input IS NULL OR @IsRoundTrip_Input IS NULL
    BEGIN 
        RAISERROR('All input parameters (except optional CurrentUserSessionID) must be provided.', 16, 1); 
        RETURN; 
    END

    DECLARE @LegOriginSeq INT, @LegDestSeq INT;
    SELECT @LegOriginSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegOriginStationID_Input;
    SELECT @LegDestSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegDestinationStationID_Input;

    IF @LegOriginSeq IS NULL 
    BEGIN 
        RAISERROR('Origin station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegOriginStationID_Input, @TripID_Input); 
        RETURN; 
    END
    
    IF @LegDestSeq IS NULL 
    BEGIN 
        RAISERROR('Destination station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegDestinationStationID_Input, @TripID_Input); 
        RETURN; 
    END
    
    IF @LegOriginSeq >= @LegDestSeq 
    BEGIN 
        RAISERROR('Invalid leg: Origin sequence (%d) must be < Destination sequence (%d).', 16, 1, @LegOriginSeq, @LegDestSeq); 
        RETURN; 
    END

    -- Variables for price calculation
    DECLARE @RouteID INT;
    DECLARE @TrainID INT;
    DECLARE @TrainTypeID INT;
    DECLARE @DistanceTraveled DECIMAL(10,2);
    DECLARE @BasePricePerKm_Context DECIMAL(10,2);

    SELECT @RouteID = T.RouteID, @TrainID = T.TrainID
    FROM dbo.Trips T WHERE T.TripID = @TripID_Input;

    IF @RouteID IS NULL 
    BEGIN 
        RAISERROR('Trip (ID: %d) details not found.', 16, 1, @TripID_Input); 
        RETURN; 
    END

    SELECT @TrainTypeID = Tr.TrainTypeID FROM dbo.Trains Tr WHERE Tr.TrainID = @TrainID;

    IF @TrainTypeID IS NULL 
    BEGIN 
        RAISERROR('Train (ID: %d) details not found, cannot get TrainTypeID.', 16, 1, @TrainID); 
        RETURN; 
    END

    SET @DistanceTraveled = dbo.GetDistanceBetweenStationsOnRoute(@RouteID, @LegOriginStationID_Input, @LegDestinationStationID_Input);

    IF @DistanceTraveled IS NULL
    BEGIN 
        RAISERROR('Could not calculate distance for the leg (RouteID: %d, Origin: %d, Dest: %d). Stations might not be on route.', 16, 1, @RouteID, @LegOriginStationID_Input, @LegDestinationStationID_Input); 
        RETURN; 
    END

    SET @BasePricePerKm_Context = dbo.GetApplicableBasePriceKm(
        @TrainTypeID,
        @RouteID,
        @IsRoundTrip_Input,
        @BookingDateTime_Input
    );

    -- Use CTE to calculate seat availability first, then aggregate
    WITH SeatAvailability AS (
        SELECT 
            S.SeatID,
            S.SeatTypeID,
            ST.TypeName AS SeatTypeName,
            ST.Description AS SeatTypeDescription,
            CT.TypeName AS CoachTypeName,
            CT.Description AS CoachTypeDescription,
            ST.PriceMultiplier AS SeatPriceMultiplier,
            CT.PriceMultiplier AS CoachPriceMultiplier,
            TRP.BasePriceMultiplier AS TripBasePriceMultiplier,
            CASE 
                WHEN S.IsEnabled = 0 THEN 0
                -- Check for committed tickets (Occupied)
                WHEN EXISTS (
                    SELECT 1 FROM dbo.Tickets TKT
                    INNER JOIN dbo.TripStations TKT_Start_TS ON TKT.TripID = TKT_Start_TS.TripID AND TKT.StartStationID = TKT_Start_TS.StationID
                    INNER JOIN dbo.TripStations TKT_End_TS ON TKT.TripID = TKT_End_TS.TripID AND TKT.EndStationID = TKT_End_TS.StationID
                    WHERE TKT.SeatID = S.SeatID AND TKT.TripID = @TripID_Input
                      AND TKT.TicketStatus IN ('Valid', 'Confirmed', 'Paid')
                      AND (TKT_Start_TS.SequenceNumber < @LegDestSeq AND TKT_End_TS.SequenceNumber > @LegOriginSeq)
                ) THEN 0
                -- Check if held by OTHER users (overlapping leg)
                WHEN EXISTS (
                    SELECT 1 FROM dbo.TemporarySeatHolds TSH_Other
                    INNER JOIN dbo.TripStations TSH_Other_Start_TS ON TSH_Other.TripID = TSH_Other_Start_TS.TripID AND TSH_Other.legOriginStationId = TSH_Other_Start_TS.StationID
                    INNER JOIN dbo.TripStations TSH_Other_End_TS ON TSH_Other.TripID = TSH_Other_End_TS.TripID AND TSH_Other.legDestinationStationId = TSH_Other_End_TS.StationID
                    WHERE TSH_Other.SeatID = S.SeatID
                      AND TSH_Other.TripID = @TripID_Input
                      AND (@CurrentUserSessionID_Input IS NULL OR TSH_Other.SessionID <> @CurrentUserSessionID_Input)
                      AND TSH_Other.ExpiresAt > GETDATE()
                      AND (TSH_Other_Start_TS.SequenceNumber < @LegDestSeq AND TSH_Other_End_TS.SequenceNumber > @LegOriginSeq)
                ) THEN 0
                ELSE 1
            END AS IsAvailable
        FROM
            dbo.Seats S
        INNER JOIN dbo.Coaches CO ON S.CoachID = CO.CoachID
        INNER JOIN dbo.SeatTypes ST ON S.SeatTypeID = ST.SeatTypeID
        INNER JOIN dbo.CoachTypes CT ON CO.CoachTypeID = CT.CoachTypeID
        INNER JOIN dbo.Trains TRN ON CO.TrainID = TRN.TrainID
        INNER JOIN dbo.Trips TRP ON TRN.TrainID = TRP.TrainID
        WHERE
            TRP.TripID = @TripID_Input
            AND S.IsEnabled = 1
    )
    
    -- Main SELECT statement - Aggregate by seat type
    SELECT
        SA.SeatTypeID,
        SA.SeatTypeName,
        SA.SeatTypeDescription,
        SA.CoachTypeName,
        SA.CoachTypeDescription,
        SA.SeatPriceMultiplier,
        SA.CoachPriceMultiplier,
        SA.TripBasePriceMultiplier,
        COUNT(SA.SeatID) AS TotalSeats,
        SUM(SA.IsAvailable) AS AvailableSeats,
        CASE
            WHEN @BasePricePerKm_Context IS NULL THEN NULL
            WHEN @DistanceTraveled = 0 AND @LegOriginStationID_Input = @LegDestinationStationID_Input THEN 0.00
            ELSE CAST(
                    @DistanceTraveled *
                    @BasePricePerKm_Context *
                    SA.TripBasePriceMultiplier *
                    SA.CoachPriceMultiplier *
                    SA.SeatPriceMultiplier
                AS DECIMAL(10,0))
        END AS CalculatedPrice,
        -- Additional descriptive information
        CASE 
            WHEN SA.SeatTypeDescription IS NOT NULL AND SA.SeatTypeDescription <> '' 
            THEN SA.SeatTypeName + ' - ' + SA.SeatTypeDescription
            ELSE SA.SeatTypeName
        END AS DisplayDescription
    FROM
        SeatAvailability SA
    GROUP BY
        SA.SeatTypeID,
        SA.SeatTypeName,
        SA.SeatTypeDescription,
        SA.CoachTypeName,
        SA.CoachTypeDescription,
        SA.SeatPriceMultiplier,
        SA.CoachPriceMultiplier,
        SA.TripBasePriceMultiplier
    HAVING
        COUNT(SA.SeatID) > 0 -- Only include seat types that exist
    ORDER BY
        CalculatedPrice ASC,
        SA.SeatTypeName ASC;
END
GO

PRINT 'Stored Procedure dbo.GetTripSeatTypePricing created successfully.';
GO

-- Test the procedure
PRINT '--- Testing dbo.GetTripSeatTypePricing ---';
DECLARE @TestBookingDate DATETIME2 = '2024-07-15';

-- Test 1: Trip 1 (Standard Train), Hanoi to Vinh, One-Way
PRINT 'Test 1: Trip 1 (Standard Train), Hanoi(1) to Vinh(3), One-Way';
EXEC dbo.GetTripSeatTypePricing
    @TripID_Input = 1,
    @LegOriginStationID_Input = 1,  -- Hanoi
    @LegDestinationStationID_Input = 3,  -- Vinh
    @BookingDateTime_Input = @TestBookingDate,
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = NULL;

-- Test 2: Trip 2 (Express Train), Hanoi to Sai Gon, One-Way
PRINT 'Test 2: Trip 2 (Express Train), Hanoi(1) to Sai Gon(5), One-Way';
EXEC dbo.GetTripSeatTypePricing
    @TripID_Input = 2,
    @LegOriginStationID_Input = 1,  -- Hanoi
    @LegDestinationStationID_Input = 5,  -- Sai Gon
    @BookingDateTime_Input = @TestBookingDate,
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = 'TestSession123';
GO