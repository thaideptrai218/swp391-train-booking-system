-- =========================================================================================
-- I. DATABASE CREATION
-- =========================================================================================
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'TrainTicketSystemDB_V1_FinalDesign')
BEGIN
    CREATE DATABASE TrainTicketSystemDB_V1_FinalDesign;
    PRINT 'Database TrainTicketSystemDB_V1_FinalDesign created.';
END
ELSE
BEGIN
    PRINT 'Database TrainTicketSystemDB_V1_FinalDesign already exists.';
END
GO

USE TrainTicketSystemDB_V1_FinalDesign;
GO

-- =========================================================================================
-- II. TABLE CREATION
-- =========================================================================================

-- Enums are defined as CHECK constraints on NVARCHAR columns.

-- -----------------------------------------------------------------------------------------
-- Table: Users
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Users';
IF OBJECT_ID('dbo.Users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Users (
        UserID INT PRIMARY KEY IDENTITY(1,1),
        FullName NVARCHAR(100) NOT NULL,
        Email NVARCHAR(100) NOT NULL,
        PhoneNumber NVARCHAR(20) NOT NULL,
        PasswordHash NVARCHAR(255) NOT NULL,
        IDCardNumber NVARCHAR(20) NULL,
        Role NVARCHAR(20) NOT NULL CHECK (Role IN ('Customer', 'Staff', 'Admin')),
        IsActive BIT NOT NULL DEFAULT 1,
        CreatedAt DATETIME2 DEFAULT GETDATE(),
        LastLogin DATETIME2 NULL,
        CONSTRAINT UQ_Users_Email UNIQUE (Email)
    );
    PRINT 'Table Users created.';
END
ELSE
BEGIN
    PRINT 'Table Users already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: PassengerTypes
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: PassengerTypes';
IF OBJECT_ID('dbo.PassengerTypes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PassengerTypes (
        PassengerTypeID INT PRIMARY KEY IDENTITY(1,1),
        TypeName NVARCHAR(50) NOT NULL,
        DiscountPercentage DECIMAL(5,2) NOT NULL DEFAULT 0,
        Description NVARCHAR(MAX) NULL,
        RequiresDocument BIT NOT NULL DEFAULT 0,
        CONSTRAINT UQ_PassengerTypes_TypeName UNIQUE (TypeName)
    );
    PRINT 'Table PassengerTypes created.';
END
ELSE
BEGIN
    PRINT 'Table PassengerTypes already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Passengers
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Passengers';
IF OBJECT_ID('dbo.Passengers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Passengers (
        PassengerID INT PRIMARY KEY IDENTITY(1,1),
        FullName NVARCHAR(100) NOT NULL,
        IDCardNumber NVARCHAR(20) NULL,
        PassengerTypeID INT NOT NULL,
        DateOfBirth DATE NULL,
        UserID INT NULL,
        CONSTRAINT FK_Passengers_PassengerTypeID FOREIGN KEY (PassengerTypeID) REFERENCES dbo.PassengerTypes(PassengerTypeID),
        CONSTRAINT FK_Passengers_UserID FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID)
    );
    PRINT 'Table Passengers created.';
END
ELSE
BEGIN
    PRINT 'Table Passengers already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Stations
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Stations';
IF OBJECT_ID('dbo.Stations', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Stations (
        StationID INT PRIMARY KEY IDENTITY(1,1),
        StationCode NVARCHAR(20) NOT NULL,
        StationName NVARCHAR(100) NOT NULL,
        Address NVARCHAR(MAX) NULL,
        City NVARCHAR(50) NULL,
        Region NVARCHAR(50) NULL,
		PhoneNumber DECIMAL(5,1),
        CONSTRAINT UQ_Stations_StationCode UNIQUE (StationCode),
        CONSTRAINT UQ_Stations_StationName_City UNIQUE (StationName, City)
    );
    PRINT 'Table Stations created.';
END
ELSE
BEGIN
    PRINT 'Table Stations already exists.';
END
GO
-- -----------------------------------------------------------------------------------------
-- Table: Routes
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Routes';
IF OBJECT_ID('dbo.Routes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Routes (
        RouteID INT PRIMARY KEY IDENTITY(1,1),
        RouteName NVARCHAR(100) NOT NULL,
        Description NVARCHAR(MAX) NULL,
        CONSTRAINT UQ_Routes_RouteName UNIQUE (RouteName)
    );
    PRINT 'Table Routes created.';
END
ELSE
BEGIN
    PRINT 'Table Routes already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: RouteStations
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: RouteStations';
IF OBJECT_ID('dbo.RouteStations', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.RouteStations (
        RouteStationID INT PRIMARY KEY IDENTITY(1,1),
        RouteID INT NOT NULL,
        StationID INT NOT NULL,
        SequenceNumber INT NOT NULL,
        DistanceFromStart DECIMAL(10,2) NOT NULL DEFAULT 0,
        DefaultStopTime INT NOT NULL DEFAULT 0, -- (minutes)
        CONSTRAINT FK_RouteStations_RouteID FOREIGN KEY (RouteID) REFERENCES dbo.Routes(RouteID),
        CONSTRAINT FK_RouteStations_StationID FOREIGN KEY (StationID) REFERENCES dbo.Stations(StationID),
        CONSTRAINT UQ_RouteStations_RouteStation UNIQUE (RouteID, StationID),
        CONSTRAINT UQ_RouteStations_RouteSequence UNIQUE (RouteID, SequenceNumber)
    );
    PRINT 'Table RouteStations created.';
END
ELSE
BEGIN
    PRINT 'Table RouteStations already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: TrainTypes
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: TrainTypes';
IF OBJECT_ID('dbo.TrainTypes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.TrainTypes (
        TrainTypeID INT PRIMARY KEY IDENTITY(1,1),
        TypeName NVARCHAR(50) NOT NULL,
        AverageVelocity DECIMAL(5,1) NULL,
        Description NVARCHAR(MAX) NULL,
        CONSTRAINT UQ_TrainTypes_TypeName UNIQUE (TypeName)
    );
    PRINT 'Table TrainTypes created.';
END
ELSE
BEGIN
    PRINT 'Table TrainTypes already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Trains
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Trains';
IF OBJECT_ID('dbo.Trains', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Trains (
        TrainID INT PRIMARY KEY IDENTITY(1,1),
        TrainName NVARCHAR(50) NOT NULL,
        TrainTypeID INT NOT NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        CONSTRAINT UQ_Trains_TrainName UNIQUE (TrainName),
        CONSTRAINT FK_Trains_TrainTypeID FOREIGN KEY (TrainTypeID) REFERENCES dbo.TrainTypes(TrainTypeID)
    );
    PRINT 'Table Trains created.';
END
ELSE
BEGIN
    PRINT 'Table Trains already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: CoachTypes
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: CoachTypes';
IF OBJECT_ID('dbo.CoachTypes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.CoachTypes (
        CoachTypeID INT PRIMARY KEY IDENTITY(1,1),
        TypeName NVARCHAR(50) NOT NULL,
        PriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
        Description NVARCHAR(MAX) NULL,
        CONSTRAINT UQ_CoachTypes_TypeName UNIQUE (TypeName)
    );
    PRINT 'Table CoachTypes created.';
END
ELSE
BEGIN
    PRINT 'Table CoachTypes already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Coaches
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Coaches';
IF OBJECT_ID('dbo.Coaches', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Coaches (
        CoachID INT PRIMARY KEY IDENTITY(1,1),
        TrainID INT NOT NULL,
        CoachNumber INT NOT NULL, -- Logical identifier/order
        CoachName NVARCHAR(50) NOT NULL, -- Display name, e.g., "Toa 5"
        CoachTypeID INT NOT NULL,
        Capacity INT NOT NULL,
        PositionInTrain INT NOT NULL, -- Physical position
        CONSTRAINT FK_Coaches_TrainID FOREIGN KEY (TrainID) REFERENCES dbo.Trains(TrainID),
        CONSTRAINT FK_Coaches_CoachTypeID FOREIGN KEY (CoachTypeID) REFERENCES dbo.CoachTypes(CoachTypeID),
        CONSTRAINT UQ_Coaches_TrainCoachNumber UNIQUE (TrainID, CoachNumber),
        CONSTRAINT UQ_Coaches_TrainPosition UNIQUE (TrainID, PositionInTrain)
    );
    PRINT 'Table Coaches created.';
END
ELSE
BEGIN
    PRINT 'Table Coaches already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: SeatTypes
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: SeatTypes';
IF OBJECT_ID('dbo.SeatTypes', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.SeatTypes (
        SeatTypeID INT PRIMARY KEY IDENTITY(1,1),
        TypeName NVARCHAR(50) NOT NULL,
        PriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
        Description NVARCHAR(MAX) NULL,
        CONSTRAINT UQ_SeatTypes_TypeName UNIQUE (TypeName)
    );
    PRINT 'Table SeatTypes created.';
END
ELSE
BEGIN
    PRINT 'Table SeatTypes already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Seats
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Seats';
IF OBJECT_ID('dbo.Seats', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Seats (
        SeatID INT PRIMARY KEY IDENTITY(1,1),
        CoachID INT NOT NULL,
        SeatNumber INT NOT NULL, -- Logical identifier/order within coach
        SeatName NVARCHAR(50) NOT NULL, -- Display name, e.g., "12A", "G25"
        SeatTypeID INT NOT NULL,
        IsEnabled BIT NOT NULL DEFAULT 1,
        CONSTRAINT FK_Seats_CoachID FOREIGN KEY (CoachID) REFERENCES dbo.Coaches(CoachID),
        CONSTRAINT FK_Seats_SeatTypeID FOREIGN KEY (SeatTypeID) REFERENCES dbo.SeatTypes(SeatTypeID),
        CONSTRAINT UQ_Seats_CoachSeatNumber UNIQUE (CoachID, SeatNumber),
        CONSTRAINT UQ_Seats_CoachSeatName UNIQUE (CoachID, SeatName)
    );
    PRINT 'Table Seats created.';
END
ELSE
BEGIN
    PRINT 'Table Seats already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Trips
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Trips';
IF OBJECT_ID('dbo.Trips', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Trips (
        TripID INT PRIMARY KEY IDENTITY(1,1),
        TrainID INT NOT NULL,
        RouteID INT NOT NULL,
        DepartureDateTime DATETIME2 NOT NULL, -- Departure from first station of the trip's route
        ArrivalDateTime DATETIME2 NOT NULL, -- Arrival at last station of the trip's route
        IsHolidayTrip BIT NOT NULL DEFAULT 0,
        TripStatus NVARCHAR(20) NOT NULL DEFAULT 'Scheduled' CHECK (TripStatus IN ('Scheduled', 'In Progress', 'Completed', 'Cancelled')),
        BasePriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
        CONSTRAINT FK_Trips_TrainID FOREIGN KEY (TrainID) REFERENCES dbo.Trains(TrainID),
        CONSTRAINT FK_Trips_RouteID FOREIGN KEY (RouteID) REFERENCES dbo.Routes(RouteID)
    );
    PRINT 'Table Trips created.';
END
ELSE
BEGIN
    PRINT 'Table Trips already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: TripStations
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: TripStations';
IF OBJECT_ID('dbo.TripStations', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.TripStations (
        TripStationID INT PRIMARY KEY IDENTITY(1,1),
        TripID INT NOT NULL,
        StationID INT NOT NULL,
        SequenceNumber INT NOT NULL, -- Sequence for this specific trip's stops
        ScheduledArrival DATETIME2 NULL,
        ScheduledDeparture DATETIME2 NULL,
        ActualArrival DATETIME2 NULL,
        ActualDeparture DATETIME2 NULL,
        CONSTRAINT FK_TripStations_TripID FOREIGN KEY (TripID) REFERENCES dbo.Trips(TripID),
        CONSTRAINT FK_TripStations_StationID FOREIGN KEY (StationID) REFERENCES dbo.Stations(StationID),
        CONSTRAINT UQ_TripStations_TripStation UNIQUE (TripID, StationID), -- Assuming a trip visits a station once
        CONSTRAINT UQ_TripStations_TripSequence UNIQUE (TripID, SequenceNumber)
    );
    PRINT 'Table TripStations created.';
END
ELSE
BEGIN
    PRINT 'Table TripStations already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: PricingRules
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: PricingRules';
IF OBJECT_ID('dbo.PricingRules', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PricingRules (
        RuleID INT PRIMARY KEY IDENTITY(1,1),
        RuleName NVARCHAR(150) NOT NULL DEFAULT 'Default Rule',
        TrainTypeID INT NULL,
        CoachTypeID INT NULL,
        SeatTypeID INT NULL,
        PassengerTypeID INT NULL,
        RouteID INT NULL,
        TripID INT NULL,
        BasePricePerKm DECIMAL(10,2) NULL,
        FixedPrice DECIMAL(10,2) NULL,
        HolidaySurchargePercentage DECIMAL(5,2) NOT NULL DEFAULT 0,
        BookingTimeWindow_HoursBeforeDeparture_Min INT NULL,
        BookingTimeWindow_HoursBeforeDeparture_Max INT NULL,
        IsForRoundTrip BIT NOT NULL DEFAULT 0,
        Priority INT NOT NULL DEFAULT 0,
        EffectiveFromDate DATE NOT NULL,
        EffectiveToDate DATE NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        CONSTRAINT FK_PricingRules_TrainTypeID FOREIGN KEY (TrainTypeID) REFERENCES dbo.TrainTypes(TrainTypeID),
        CONSTRAINT FK_PricingRules_CoachTypeID FOREIGN KEY (CoachTypeID) REFERENCES dbo.CoachTypes(CoachTypeID),
        CONSTRAINT FK_PricingRules_SeatTypeID FOREIGN KEY (SeatTypeID) REFERENCES dbo.SeatTypes(SeatTypeID),
        CONSTRAINT FK_PricingRules_PassengerTypeID FOREIGN KEY (PassengerTypeID) REFERENCES dbo.PassengerTypes(PassengerTypeID),
        CONSTRAINT FK_PricingRules_RouteID FOREIGN KEY (RouteID) REFERENCES dbo.Routes(RouteID),
        CONSTRAINT FK_PricingRules_TripID FOREIGN KEY (TripID) REFERENCES dbo.Trips(TripID)
    );
    PRINT 'Table PricingRules created.';
END
ELSE
BEGIN
    PRINT 'Table PricingRules already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Bookings
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Bookings';
IF OBJECT_ID('dbo.Bookings', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Bookings (
        BookingID INT PRIMARY KEY IDENTITY(1,1),
        BookingCode NVARCHAR(50) NOT NULL,
        UserID INT NOT NULL,
        BookingDateTime DATETIME2 NOT NULL DEFAULT GETDATE(),
        TotalPrice DECIMAL(10,2) NOT NULL,
        BookingStatus NVARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (BookingStatus IN ('Pending', 'Confirmed', 'Cancelled', 'Completed')),
        PaymentStatus NVARCHAR(20) NOT NULL DEFAULT 'Unpaid' CHECK (PaymentStatus IN ('Unpaid', 'Paid', 'Refunded')),
        Source NVARCHAR(50) NULL,
        ExpiredAt DATETIME2 NULL,
        CONSTRAINT UQ_Bookings_BookingCode UNIQUE (BookingCode),
        CONSTRAINT FK_Bookings_UserID FOREIGN KEY (UserID) REFERENCES dbo.Users(UserID)
    );
    PRINT 'Table Bookings created.';
END
ELSE
BEGIN
    PRINT 'Table Bookings already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Tickets
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Tickets';
IF OBJECT_ID('dbo.Tickets', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Tickets (
        TicketID INT PRIMARY KEY IDENTITY(1,1),
        TicketCode NVARCHAR(50) NOT NULL,
        BookingID INT NOT NULL,
        TripID INT NOT NULL,
        SeatID INT NOT NULL, -- Links to the physical seat
        PassengerID INT NOT NULL,
        StartStationID INT NOT NULL,
        EndStationID INT NOT NULL,
        Price DECIMAL(10,2) NOT NULL,
        TicketStatus NVARCHAR(20) NOT NULL DEFAULT 'Valid' CHECK (TicketStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
        CoachNameSnapshot NVARCHAR(50) NOT NULL, -- Snapshot of Coaches.CoachName
        SeatNameSnapshot NVARCHAR(50) NOT NULL, -- Snapshot of Seats.SeatName
        PassengerName NVARCHAR(100) NOT NULL, -- Snapshot
        PassengerIDCardNumber NVARCHAR(20) NULL, -- Snapshot
        FareComponentDetails NVARCHAR(MAX) NULL, -- JSON/XML of price calculation
        ParentTicketID INT NULL, -- For exchanges
        CONSTRAINT UQ_Tickets_TicketCode UNIQUE (TicketCode),
        CONSTRAINT FK_Tickets_BookingID FOREIGN KEY (BookingID) REFERENCES dbo.Bookings(BookingID),
        CONSTRAINT FK_Tickets_TripID FOREIGN KEY (TripID) REFERENCES dbo.Trips(TripID),
        CONSTRAINT FK_Tickets_SeatID FOREIGN KEY (SeatID) REFERENCES dbo.Seats(SeatID),
        CONSTRAINT FK_Tickets_PassengerID FOREIGN KEY (PassengerID) REFERENCES dbo.Passengers(PassengerID),
        CONSTRAINT FK_Tickets_StartStationID FOREIGN KEY (StartStationID) REFERENCES dbo.Stations(StationID),
        CONSTRAINT FK_Tickets_EndStationID FOREIGN KEY (EndStationID) REFERENCES dbo.Stations(StationID),
        CONSTRAINT FK_Tickets_ParentTicketID FOREIGN KEY (ParentTicketID) REFERENCES dbo.Tickets(TicketID) -- Self-referencing FK
    );
    PRINT 'Table Tickets created.';
END
ELSE
BEGIN
    PRINT 'Table Tickets already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: PaymentTransactions
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: PaymentTransactions';
IF OBJECT_ID('dbo.PaymentTransactions', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PaymentTransactions (
        TransactionID INT PRIMARY KEY IDENTITY(1,1),
        BookingID INT NOT NULL,
        PaymentGatewayTransactionID NVARCHAR(100) NULL,
        PaymentGateway NVARCHAR(50) NOT NULL,
        Amount DECIMAL(10,2) NOT NULL,
        TransactionDateTime DATETIME2 NOT NULL DEFAULT GETDATE(),
        Status NVARCHAR(20) NOT NULL CHECK (Status IN ('Pending', 'Success', 'Failed', 'Cancelled')),
        Notes NVARCHAR(MAX) NULL,
        CONSTRAINT FK_PaymentTransactions_BookingID FOREIGN KEY (BookingID) REFERENCES dbo.Bookings(BookingID)
    );
    PRINT 'Table PaymentTransactions created.';
END
ELSE
BEGIN
    PRINT 'Table PaymentTransactions already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: TicketStatusHistory
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: TicketStatusHistory';
IF OBJECT_ID('dbo.TicketStatusHistory', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.TicketStatusHistory (
        HistoryID INT PRIMARY KEY IDENTITY(1,1),
        TicketID INT NOT NULL,
        OldStatus NVARCHAR(20) NULL CHECK (OldStatus IS NULL OR OldStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
        NewStatus NVARCHAR(20) NOT NULL CHECK (NewStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
        ChangedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
        Reason NVARCHAR(500) NULL,
        ChangedByUserID INT NULL, -- Can be NULL if system-changed
        CONSTRAINT FK_TicketStatusHistory_TicketID FOREIGN KEY (TicketID) REFERENCES dbo.Tickets(TicketID),
        CONSTRAINT FK_TicketStatusHistory_ChangedByUserID FOREIGN KEY (ChangedByUserID) REFERENCES dbo.Users(UserID)
    );
    PRINT 'Table TicketStatusHistory created.';
END
ELSE
BEGIN
    PRINT 'Table TicketStatusHistory already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: CancellationPolicies
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: CancellationPolicies';
IF OBJECT_ID('dbo.CancellationPolicies', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.CancellationPolicies (
        PolicyID INT PRIMARY KEY IDENTITY(1,1),
        PolicyName NVARCHAR(150) NOT NULL,
        HoursBeforeDeparture_Min INT NOT NULL,
        HoursBeforeDeparture_Max INT NULL,
        FeePercentage DECIMAL(5,2) NULL,
        FixedFeeAmount DECIMAL(10,2) NULL,
        IsRefundable BIT NOT NULL DEFAULT 1,
        Description NVARCHAR(MAX) NULL,
        IsActive BIT NOT NULL DEFAULT 1,
        EffectiveFromDate DATE NOT NULL DEFAULT GETDATE(),
        EffectiveToDate DATE NULL,
        CONSTRAINT UQ_CancellationPolicies_PolicyName UNIQUE (PolicyName)
    );
    PRINT 'Table CancellationPolicies created.';
END
ELSE
BEGIN
    PRINT 'Table CancellationPolicies already exists.';
END
GO

-- -----------------------------------------------------------------------------------------
-- Table: Refunds
-- -----------------------------------------------------------------------------------------
PRINT 'Creating Table: Refunds';
IF OBJECT_ID('dbo.Refunds', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Refunds (
        RefundID INT PRIMARY KEY IDENTITY(1,1),
        TicketID INT NOT NULL,
        BookingID INT NOT NULL, -- Redundant but good for quick lookups from booking context
        AppliedPolicyID INT NULL,
        OriginalTicketPrice DECIMAL(10,2) NOT NULL,
        FeeAmount DECIMAL(10,2) NOT NULL DEFAULT 0,
        ActualRefundAmount DECIMAL(10,2) NOT NULL,
        RequestedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
        ProcessedAt DATETIME2 NULL,
        Status NVARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Approved', 'Rejected', 'Processed', 'Failed')),
        RefundMethod NVARCHAR(50) NULL,
        Notes NVARCHAR(MAX) NULL,
        RequestedByUserID INT NULL, -- User who requested
        ProcessedByUserID INT NULL, -- Staff/Admin who processed
        RefundTransactionID NVARCHAR(100) NULL, -- Link to payment gateway refund transaction
        CONSTRAINT UQ_Refunds_TicketID UNIQUE (TicketID), -- One refund record per ticket
        CONSTRAINT FK_Refunds_TicketID FOREIGN KEY (TicketID) REFERENCES dbo.Tickets(TicketID),
        CONSTRAINT FK_Refunds_BookingID FOREIGN KEY (BookingID) REFERENCES dbo.Bookings(BookingID),
        CONSTRAINT FK_Refunds_AppliedPolicyID FOREIGN KEY (AppliedPolicyID) REFERENCES dbo.CancellationPolicies(PolicyID),
        CONSTRAINT FK_Refunds_RequestedByUserID FOREIGN KEY (RequestedByUserID) REFERENCES dbo.Users(UserID),
        CONSTRAINT FK_Refunds_ProcessedByUserID FOREIGN KEY (ProcessedByUserID) REFERENCES dbo.Users(UserID)
    );
    PRINT 'Table Refunds created.';
END
ELSE
BEGIN
    PRINT 'Table Refunds already exists.';
END
GO

PRINT '--- ALL TABLES DEFINED ---';
GO

ALTER TABLE Stations
ADD PhoneNumber varchar(10)