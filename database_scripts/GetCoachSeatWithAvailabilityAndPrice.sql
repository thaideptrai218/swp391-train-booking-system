IF OBJECT_ID('dbo.GetDistanceBetweenStationsOnRoute', 'FN') IS NOT NULL
    DROP FUNCTION dbo.GetDistanceBetweenStationsOnRoute;
GO

CREATE FUNCTION dbo.GetDistanceBetweenStationsOnRoute
(
    @RouteID_Input INT,
    @OriginStationID_Input INT,
    @DestinationStationID_Input INT
)
RETURNS DECIMAL(10,2)
AS
BEGIN
    DECLARE @OriginDistance DECIMAL(10,2);
    DECLARE @DestinationDistance DECIMAL(10,2);
    DECLARE @CalculatedDistance DECIMAL(10,2);

    -- Validate inputs within UDF for robustness, or rely on SP to validate before calling
    IF @RouteID_Input IS NULL OR @OriginStationID_Input IS NULL OR @DestinationStationID_Input IS NULL RETURN NULL;
    IF NOT EXISTS (SELECT 1 FROM dbo.Routes WHERE RouteID = @RouteID_Input) RETURN NULL;
    IF NOT EXISTS (SELECT 1 FROM dbo.Stations WHERE StationID = @OriginStationID_Input) RETURN NULL;
    IF NOT EXISTS (SELECT 1 FROM dbo.Stations WHERE StationID = @DestinationStationID_Input) RETURN NULL;
    IF @OriginStationID_Input = @DestinationStationID_Input RETURN 0.00;


    SELECT @OriginDistance = RS.DistanceFromStart
    FROM dbo.RouteStations RS
    WHERE RS.RouteID = @RouteID_Input AND RS.StationID = @OriginStationID_Input;

    SELECT @DestinationDistance = RS.DistanceFromStart
    FROM dbo.RouteStations RS
    WHERE RS.RouteID = @RouteID_Input AND RS.StationID = @DestinationStationID_Input;

    IF @OriginDistance IS NULL OR @DestinationDistance IS NULL
    BEGIN
        RETURN NULL; -- Stations not on this specific route's RouteStations definition
    END

    SET @CalculatedDistance = ABS(@DestinationDistance - @OriginDistance);
    RETURN @CalculatedDistance;
END
GO
PRINT 'Function dbo.GetDistanceBetweenStationsOnRoute created/verified.';
GO



IF OBJECT_ID('dbo.GetApplicableBasePriceKm', 'FN') IS NOT NULL
    DROP FUNCTION dbo.GetApplicableBasePriceKm;
GO

CREATE FUNCTION dbo.GetApplicableBasePriceKm
(
    @TrainTypeID_Input INT,
    @RouteID_Input INT,
    @IsRoundTrip_Input BIT,
    @BookingDateTime_Input DATETIME2 -- Used to check against PricingRule's ApplicableDateStart/End
)
RETURNS DECIMAL(10,2)
AS
BEGIN
    DECLARE @BasePricePerKm_Result DECIMAL(10,2);

    SELECT TOP 1
        @BasePricePerKm_Result = PR.BasePricePerKm
    FROM dbo.PricingRules PR
    WHERE
        PR.IsActive = 1
        -- Check if the rule definition itself is currently active
        AND @BookingDateTime_Input >= PR.EffectiveFromDate
        AND @BookingDateTime_Input < ISNULL(DATEADD(day, 1, PR.EffectiveToDate), DATEADD(day, 1, '9999-12-30')) -- Inclusive of EffectiveToDate

        -- Check if the @BookingDateTime_Input falls within the rule's specific applicable date range (for seasonal/event pricing)
        AND (@BookingDateTime_Input >= ISNULL(PR.ApplicableDateStart, @BookingDateTime_Input))
        AND (@BookingDateTime_Input < ISNULL(DATEADD(day, 1, PR.ApplicableDateEnd), DATEADD(day, 1, @BookingDateTime_Input)))

        -- Match on context parameters (NULL in rule means "applies to all")
        AND (PR.TrainTypeID IS NULL OR PR.TrainTypeID = @TrainTypeID_Input)
        AND (PR.RouteID IS NULL OR PR.RouteID = @RouteID_Input)
        AND (PR.IsForRoundTrip IS NULL OR PR.IsForRoundTrip = @IsRoundTrip_Input)
    ORDER BY
        PR.Priority DESC,
        -- Tie-breaking: prefer rules that are more specific
        (CASE WHEN PR.TrainTypeID IS NOT NULL THEN 0 ELSE 1 END) ASC,
        (CASE WHEN PR.RouteID IS NOT NULL THEN 0 ELSE 1 END) ASC,
        (CASE WHEN PR.IsForRoundTrip IS NOT NULL THEN 0 ELSE 1 END) ASC,
        (CASE WHEN PR.ApplicableDateStart IS NOT NULL OR PR.ApplicableDateEnd IS NOT NULL THEN 0 ELSE 1 END) ASC; -- Rules with specific date ranges are more specific

    RETURN @BasePricePerKm_Result; -- Will be NULL if no matching rule is found
END
GO

PRINT 'Function dbo.GetApplicableBasePriceKm created.';
GO



IF OBJECT_ID('dbo.GetCoachSeatsWithAvailabilityAndPrice', 'P') IS NOT NULL
    DROP PROCEDURE dbo.GetCoachSeatsWithAvailabilityAndPrice;
GO

CREATE PROCEDURE dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input INT,
    @CoachID_Input INT,
    @LegOriginStationID_Input INT,
    @LegDestinationStationID_Input INT,
    @BookingDateTime_Input DATETIME2,
    @IsRoundTrip_Input BIT,
    @CurrentUserSessionID_Input NVARCHAR(100) = NULL -- Added parameter
AS
BEGIN
    SET NOCOUNT ON;

    -- Input validations
    IF @TripID_Input IS NULL OR @CoachID_Input IS NULL OR @LegOriginStationID_Input IS NULL OR @LegDestinationStationID_Input IS NULL OR @BookingDateTime_Input IS NULL OR @IsRoundTrip_Input IS NULL
    BEGIN RAISERROR('All input parameters (except optional CurrentUserSessionID) must be provided.', 16, 1); RETURN; END

    DECLARE @LegOriginSeq INT, @LegDestSeq INT;
    SELECT @LegOriginSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegOriginStationID_Input;
    SELECT @LegDestSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegDestinationStationID_Input;

    IF @LegOriginSeq IS NULL BEGIN RAISERROR('Origin station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegOriginStationID_Input, @TripID_Input); RETURN; END
    IF @LegDestSeq IS NULL BEGIN RAISERROR('Destination station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegDestinationStationID_Input, @TripID_Input); RETURN; END
    IF @LegOriginSeq >= @LegDestSeq BEGIN RAISERROR('Invalid leg: Origin sequence (%d) must be < Destination sequence (%d).', 16, 1, @LegOriginSeq, @LegDestSeq); RETURN; END

    IF NOT EXISTS (
        SELECT 1 FROM dbo.Coaches CO INNER JOIN dbo.Trains TRN ON CO.TrainID = TRN.TrainID
        INNER JOIN dbo.Trips TRP ON TRN.TrainID = TRP.TrainID
        WHERE CO.CoachID = @CoachID_Input AND TRP.TripID = @TripID_Input
    )
    BEGIN RAISERROR('Coach (ID: %d) does not belong to the train operating Trip (ID: %d).', 16, 1, @CoachID_Input, @TripID_Input); RETURN; END

    -- Variables for price calculation
    DECLARE @RouteID INT;
    DECLARE @TrainID INT;
    DECLARE @TrainTypeID INT;
    DECLARE @DistanceTraveled DECIMAL(10,2);
    DECLARE @BasePricePerKm_Context DECIMAL(10,2);

    SELECT @RouteID = T.RouteID, @TrainID = T.TrainID
    FROM dbo.Trips T WHERE T.TripID = @TripID_Input;

    IF @RouteID IS NULL BEGIN RAISERROR('Trip (ID: %d) details not found.', 16, 1, @TripID_Input); RETURN; END

    SELECT @TrainTypeID = Tr.TrainTypeID FROM dbo.Trains Tr WHERE Tr.TrainID = @TrainID;

    IF @TrainTypeID IS NULL BEGIN RAISERROR('Train (ID: %d) details not found, cannot get TrainTypeID.', 16, 1, @TrainID); RETURN; END

    SET @DistanceTraveled = dbo.GetDistanceBetweenStationsOnRoute(@RouteID, @LegOriginStationID_Input, @LegDestinationStationID_Input);

    IF @DistanceTraveled IS NULL
    BEGIN RAISERROR('Could not calculate distance for the leg (RouteID: %d, Origin: %d, Dest: %d). Stations might not be on route.', 16, 1, @RouteID, @LegOriginStationID_Input, @LegDestinationStationID_Input); RETURN; END

    IF @DistanceTraveled = 0 AND @LegOriginStationID_Input <> @LegDestinationStationID_Input
    BEGIN RAISERROR('Segment distance calculated as zero for different stations. Check RouteStations data.', 16, 1); RETURN; END

    SET @BasePricePerKm_Context = dbo.GetApplicableBasePriceKm(
        @TrainTypeID,
        @RouteID,
        @IsRoundTrip_Input,
        @BookingDateTime_Input
    );

    -- Main SELECT statement
    SELECT
        S.SeatID,
        S.SeatName,
        S.SeatNumber AS SeatNumberInCoach,
        S.SeatTypeID,
        ST.TypeName AS SeatTypeName,
        ST.BerthLevel,
        ST.PriceMultiplier AS SeatPriceMultiplier,
        CT.PriceMultiplier AS CoachPriceMultiplier,
        TRP.BasePriceMultiplier AS TripBasePriceMultiplier,
        S.IsEnabled,
        CASE -- Availability Status (Order of checks is important)
            WHEN S.IsEnabled = 0 THEN 'Disabled'
            -- 1. Check if held by the CURRENT user for the EXACT leg
            WHEN @CurrentUserSessionID_Input IS NOT NULL AND EXISTS (
                SELECT 1 FROM dbo.TemporarySeatHolds TSH_User
                WHERE TSH_User.SeatID = S.SeatID
                  AND TSH_User.TripID = @TripID_Input
                  AND TSH_User.LegStartStationID = @LegOriginStationID_Input -- Exact leg match
                  AND TSH_User.LegEndStationID = @LegDestinationStationID_Input   -- Exact leg match
                  AND TSH_User.SessionID = @CurrentUserSessionID_Input
                  AND TSH_User.ExpiresAt > GETDATE()
            ) THEN 'HeldByYou'
            -- 2. Check for committed tickets (Occupied by anyone for an overlapping leg)
            WHEN EXISTS (
                SELECT 1 FROM dbo.Tickets TKT
                INNER JOIN dbo.TripStations TKT_Start_TS ON TKT.TripID = TKT_Start_TS.TripID AND TKT.StartStationID = TKT_Start_TS.StationID
                INNER JOIN dbo.TripStations TKT_End_TS ON TKT.TripID = TKT_End_TS.TripID AND TKT.EndStationID = TKT_End_TS.StationID
                WHERE TKT.SeatID = S.SeatID AND TKT.TripID = @TripID_Input
                  AND TKT.TicketStatus IN ('Valid', 'Confirmed', 'Paid') -- Consider all finalized statuses
                  AND (TKT_Start_TS.SequenceNumber < @LegDestSeq AND TKT_End_TS.SequenceNumber > @LegOriginSeq) -- Overlap
            ) THEN 'Occupied'
            -- 3. Check if held by OTHER users (overlapping leg)
            WHEN EXISTS (
                SELECT 1 FROM dbo.TemporarySeatHolds TSH_Other
                INNER JOIN dbo.TripStations TSH_Other_Start_TS ON TSH_Other.TripID = TSH_Other_Start_TS.TripID AND TSH_Other.LegStartStationID = TSH_Other_Start_TS.StationID
                INNER JOIN dbo.TripStations TSH_Other_End_TS ON TSH_Other.TripID = TSH_Other_End_TS.TripID AND TSH_Other.LegEndStationID = TSH_Other_End_TS.StationID
                WHERE TSH_Other.SeatID = S.SeatID
                  AND TSH_Other.TripID = @TripID_Input
                  AND (@CurrentUserSessionID_Input IS NULL OR TSH_Other.SessionID <> @CurrentUserSessionID_Input) -- Not the current user
                  AND TSH_Other.ExpiresAt > GETDATE()
                  AND (TSH_Other_Start_TS.SequenceNumber < @LegDestSeq AND TSH_Other_End_TS.SequenceNumber > @LegOriginSeq) -- Overlap
            ) THEN 'HeldByOther'
            ELSE 'Available'
        END AS AvailabilityStatus,
        CASE
            WHEN S.IsEnabled = 0 THEN NULL
            WHEN @BasePricePerKm_Context IS NULL THEN NULL
            WHEN @DistanceTraveled = 0 AND @LegOriginStationID_Input = @LegDestinationStationID_Input THEN 0.00
            ELSE CAST(
                    @DistanceTraveled *
                    @BasePricePerKm_Context *
                    TRP.BasePriceMultiplier *
                    CT.PriceMultiplier *
                    ST.PriceMultiplier
                AS DECIMAL(10,0)) -- Consider if DECIMAL(10,2) or other precision is needed for final price
        END AS CalculatedPrice
    FROM
        dbo.Seats S
    INNER JOIN dbo.Coaches CO ON S.CoachID = CO.CoachID
    INNER JOIN dbo.SeatTypes ST ON S.SeatTypeID = ST.SeatTypeID
    INNER JOIN dbo.CoachTypes CT ON CO.CoachTypeID = CT.CoachTypeID
    INNER JOIN dbo.Trips TRP ON TRP.TripID = @TripID_Input -- Ensures we use the Trip's BasePriceMultiplier
    WHERE
        S.CoachID = @CoachID_Input
    ORDER BY
        S.SeatNumber;
END
GO

PRINT 'Stored Procedure dbo.GetCoachSeatsWithAvailabilityAndPrice (with CurrentUserSessionID_Input) updated.';
GO


PRINT 'Inserting Basic Dataset for PricingRules (Simpler Still)...';
DELETE FROM dbo.PricingRules; -- Clear existing simplified rules
SET IDENTITY_INSERT dbo.PricingRules ON;
INSERT INTO dbo.PricingRules (RuleID, RuleName, BasePricePerKm, TrainTypeID, RouteID, IsForRoundTrip, ApplicableDateStart, ApplicableDateEnd, Priority, EffectiveFromDate, IsActive) VALUES
-- General Defaults (Lowest Priority)
(1, 'Default One-Way',            10.00, NULL, NULL, 0,    NULL,         NULL,         0, '2023-01-01', 1), -- Base per km
(2, 'Default Round-Trip',          9.00, NULL, NULL, 1,    NULL,         NULL,         0, '2023-01-01', 1), -- Slightly cheaper for round trip

-- TrainType Specific
(3, 'Express Train One-Way',      15.00, 2,    NULL, 0,    NULL,         NULL,         10, '2023-01-01', 1), -- TrainTypeID 2 = Express
(4, 'Express Train Round-Trip',   13.50, 2,    NULL, 1,    NULL,         NULL,         10, '2023-01-01', 1),
(5, 'Standard Train One-Way',      8.00, 1,    NULL, 0,    NULL,         NULL,         10, '2023-01-01', 1), -- TrainTypeID 1 = Standard

-- Route Specific
(6, 'North-South Summer One-Way', 12.00, NULL, 1,    0,    '2024-06-01', '2024-08-31', 20, '2023-01-01', 1), -- RouteID 1, during summer
(7, 'Hanoi-DN Express Route OW',  18.00, NULL, 2,    0,    NULL,         NULL,         20, '2023-01-01', 1), -- RouteID 2

-- Combination: TrainType and Route Specific
(8, 'Express on North-South OW',  16.00, 2,    1,    0,    NULL,         NULL,         30, '2023-01-01', 1),

-- Most Specific: TrainType, Route, Dates
(9, 'Express on N-S Summer OW',   17.50, 2,    1,    0,    '2024-06-01', '2024-08-31', 40, '2023-01-01', 1);
SET IDENTITY_INSERT dbo.PricingRules OFF;
GO



PRINT '--- Testing dbo.GetDistanceBetweenStationsOnRoute ---';

-- Test 1.1: Valid leg on Route 1
PRINT 'Test 1.1: Hanoi (1) to Vinh (3) on Route 1 (North-South Line)';
SELECT dbo.GetDistanceBetweenStationsOnRoute(3, 6, 13) AS Distance_HN_VINH; -- Expected: ABS(300 - 0) = 300

-- Test 1.2: Valid leg on Route 1, reverse
PRINT 'Test 1.2: Da Nang (4) to Nam Dinh (2) on Route 1 (North-South Line)';
SELECT dbo.GetDistanceBetweenStationsOnRoute(1, 4, 2) AS Distance_DN_ND; -- Expected: ABS(80 - 750) = 670

-- Test 1.3: Valid leg on Route 2
PRINT 'Test 1.3: Hanoi (1) to Da Nang (4) on Route 2 (Hanoi-Danang Express)';
SELECT dbo.GetDistanceBetweenStationsOnRoute(2, 1, 4) AS Distance_HN_DN_R2; -- Expected: ABS(720 - 0) = 720

-- Test 1.4: Station not on route
PRINT 'Test 1.4: Hanoi (1) to Sai Gon (5) on Route 2 (Hanoi-Danang Express - SG not on this route)';
SELECT dbo.GetDistanceBetweenStationsOnRoute(2, 1, 5) AS Distance_InvalidStation; -- Expected: NULL

-- Test 1.5: Invalid Route ID
PRINT 'Test 1.5: Invalid Route ID 99';
SELECT dbo.GetDistanceBetweenStationsOnRoute(99, 1, 4) AS Distance_InvalidRoute; -- Expected: NULL

-- Test 1.6: Same station
PRINT 'Test 1.6: Hanoi (1) to Hanoi (1) on Route 1';
SELECT dbo.GetDistanceBetweenStationsOnRoute(1, 1, 1) AS Distance_SameStation; -- Expected: 0.00
GO


PRINT '--- Testing dbo.GetApplicableBasePriceKm ---';
DECLARE @TestBookingDate DATETIME2 = '2024-07-15'; -- A date within summer for RuleID 6 & 9
DECLARE @TestOffSeasonDate DATETIME2 = '2024-03-15';

-- Test 2.1: Default One-Way (Standard Train, Non-Specific Route)
PRINT 'Test 2.1: Standard Train (1), Generic Route (e.g., not 1 or 2 specified), One-Way, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(1, NULL, 0, @TestOffSeasonDate) AS PriceKm_Std_Gen_OW; -- Expected: 8.00 (Rule 5) or 10.00 (Rule 1) depending on tie-break

-- Test 2.2: Default Round-Trip
PRINT 'Test 2.2: Standard Train (1), Generic Route, Round-Trip, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(1, NULL, 1, @TestOffSeasonDate) AS PriceKm_Std_Gen_RT; -- Expected: 9.00 (Rule 2)

-- Test 2.3: Express Train One-Way (Generic Route)
PRINT 'Test 2.3: Express Train (2), Generic Route, One-Way, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(2, NULL, 0, @TestOffSeasonDate) AS PriceKm_Exp_Gen_OW; -- Expected: 15.00 (Rule 3)

-- Test 2.4: North-South Summer One-Way (Generic Train Type)
PRINT 'Test 2.4: Generic Train, North-South Route (1), One-Way, Summer';
SELECT dbo.GetApplicableBasePriceKm(NULL, 1, 0, @TestBookingDate) AS PriceKm_Gen_NS_Summer_OW; -- Expected: 12.00 (Rule 6)

-- Test 2.5: Express on North-South Summer One-Way (Most Specific)
PRINT 'Test 2.5: Express Train (2), North-South Route (1), One-Way, Summer';
SELECT dbo.GetApplicableBasePriceKm(2, 1, 0, @TestBookingDate) AS PriceKm_Exp_NS_Summer_OW; -- Expected: 17.50 (Rule 9)

-- Test 2.6: Express on North-South One-Way (Off-Season)
PRINT 'Test 2.6: Express Train (2), North-South Route (1), One-Way, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(2, 1, 0, @TestOffSeasonDate) AS PriceKm_Exp_NS_OW_OffSeason; -- Expected: 16.00 (Rule 8)

-- Test 2.7: Hanoi-DN Express Route One-Way (Generic Train Type)
PRINT 'Test 2.7: Generic Train, Hanoi-DN Route (2), One-Way, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(NULL, 2, 0, @TestOffSeasonDate) AS PriceKm_Gen_HDN_OW; -- Expected: 18.00 (Rule 7)

-- Test 2.8: No matching specific rule, should use default.
PRINT 'Test 2.8: TrainType 99 (non-existent), Route 99, One-Way, Off-Season';
SELECT dbo.GetApplicableBasePriceKm(99, 99, 0, @TestOffSeasonDate) AS PriceKm_NonMatch_OW; -- Expected: 10.00 (Rule 1)
GO


PRINT '--- Testing dbo.GetCoachSeatsWithAvailabilityAndPrice ---';
DECLARE @BookingDate DATETIME2 = '2024-07-15'; -- Summer
DECLARE @OffSeasonBookingDate DATETIME2 = '2024-03-15';

-- Test 3.1: Trip 1 (TN1 - Standard Train, Route 1), Coach 2 (Soft Seat), Hanoi to Vinh, One-Way, Off-Season
-- TrainTypeID = 1, RouteID = 1, IsRoundTrip = 0, Date = Off-Season
-- Expected BasePriceKm: Standard Train One-Way (Rule 5) = 8.00
-- Distance HN-VINH = 300km
-- TripBaseMultiplier for Trip 1 = 1.0
-- CoachType 2 (Soft Seat AC) Multiplier = 1.0
-- SeatType 1 (Standard) Multiplier = 1.0
-- Expected Price = 300 * 8.00 * 1.0 * 1.0 * 1.0 = 2400.00
PRINT 'Test 3.1: TN1 (Std), Coach 2 (SoftSeat), HN(1)-VINH(3), OW, Off-Season (2024-03-15)';
EXEC dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input = 1,
    @CoachID_Input = 2,
    @LegOriginStationID_Input = 1,
    @LegDestinationStationID_Input = 3,
    @BookingDateTime_Input = @OffSeasonBookingDate,
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = NULL; -- Test without session ID

-- Test 3.2: Trip 2 (SE1 - Express Train, Route 1), Coach 4 (Sleeper), Hanoi to Sai Gon, One-Way, Summer
-- TrainTypeID = 2, RouteID = 1, IsRoundTrip = 0, Date = Summer ('2024-07-15')
-- Expected BasePriceKm: Express on N-S Summer OW (Rule 9) = 17.50
-- Distance HN-SG = 1700km
-- TripBaseMultiplier for Trip 2 = 1.10
-- CoachType 3 (Sleeper Berth AC L1) Multiplier = 1.5
-- Seats:
--   Seat 5 (Lower Berth, SeatTypeID 3, Multiplier 1.0): 1700 * 17.50 * 1.10 * 1.5 * 1.0 = 49087.50 -> 49087.50
--   Seat 6 (Upper Berth, SeatTypeID 4, Multiplier 0.98): 1700 * 17.50 * 1.10 * 1.5 * 0.98 = 48105.75 -> 48105.75
PRINT 'Test 3.2: SE1 (Exp), Coach 4 (Sleeper), HN(1)-SG(5), OW, Summer (2024-07-15)';
EXEC dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input = 2,
    @CoachID_Input = 4,
    @LegOriginStationID_Input = 1, -- Hanoi on Trip 2 Route
    @LegDestinationStationID_Input = 5, -- Sai Gon on Trip 2 Route
    @BookingDateTime_Input = @BookingDate,
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = 'TestSessionID123'; -- Test with a session ID

-- Test 3.3: Trip 3 (SE3 - Express Train, Route 2), Coach 5 (Soft Seat), Hanoi to Da Nang, Round-Trip, Off-Season
-- TrainTypeID = 2, RouteID = 2, IsRoundTrip = 1, Date = Off-Season
-- Expected BasePriceKm: For Express (2) on Route (2) for RoundTrip (1), no specific rule,
-- so it might fall back. Let's check rule application:
--  - IsForRoundTrip=1, TrainTypeID=2 -> Rule 4 (Express Train Round-Trip) -> 13.50 (Priority 10)
--  - IsForRoundTrip=1, RouteID=2 -> No specific rule.
--  - Default Round-Trip (Rule 2) -> 9.00 (Priority 0)
-- So, Rule 4 (13.50) should win.
-- Distance HN-DN on Route 2 = 720km
-- TripBaseMultiplier for Trip 3 = 1.0
-- CoachType 2 (Soft Seat AC) Multiplier = 1.0
-- SeatType 1 (Standard) Multiplier = 1.0
-- Expected Price = 720 * 13.50 * 1.0 * 1.0 * 1.0 = 9720.00
PRINT 'Test 3.3: SE3 (Exp), Coach 5 (SoftSeat), HN(1)-DN(4) on Route 2, RT, Off-Season (2024-03-15)';
EXEC dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input = 3,
    @CoachID_Input = 5,
    @LegOriginStationID_Input = 1,
    @LegDestinationStationID_Input = 4,
    @BookingDateTime_Input = @OffSeasonBookingDate,
    @IsRoundTrip_Input = 1,
    @CurrentUserSessionID_Input = NULL;

-- Test 3.4: Invalid Leg (Origin after Destination)
PRINT 'Test 3.4: Invalid Leg - DaNang(4) to Hanoi(1) with sequences reversed';
EXEC dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input = 1,
    @CoachID_Input = 1,
    @LegOriginStationID_Input = 4, -- Seq higher
    @LegDestinationStationID_Input = 1, -- Seq lower
    @BookingDateTime_Input = @BookingDate,
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = NULL;

-- Test 3.5: Price cannot be determined (e.g., if no pricing rule matches at all - hard to force with defaults)
PRINT 'Test 3.5: Future date where no rule definition is effective';
EXEC dbo.GetCoachSeatsWithAvailabilityAndPrice
    @TripID_Input = 10,
    @CoachID_Input = 1,
    @LegOriginStationID_Input = 13,
    @LegDestinationStationID_Input = 11,
    @BookingDateTime_Input = '2025-06-06', 
    @IsRoundTrip_Input = 0,
    @CurrentUserSessionID_Input = NULL;
GO
