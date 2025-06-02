-- =============================================================================
-- Database Creation and Schema for TrainTicketSystem_V1_FinalDesign
-- =============================================================================

-- Step 1: Create the Database (if it doesn't exist)
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'TrainTicketSystemDB_V1_FinalDesign')
BEGIN
    CREATE DATABASE TrainTicketSystemDB_V1_FinalDesign
    PRINT 'Database TrainTicketSystemDB_V1_FinalDesign created.';
END
ELSE
BEGIN
    PRINT 'Database TrainTicketSystemDB_V1_FinalDesign already exists.';
END
GO

-- Step 2: Use the Database
USE TrainTicketSystemDB_V1_FinalDesign;
GO


-- --- Enums are handled by CHECK constraints in each table ---

-- Table: Users
PRINT 'Creating Table: Users';
IF OBJECT_ID('Users', 'U') IS NOT NULL DROP TABLE Users; -- Add this to allow re-running
CREATE TABLE Users (
    UserID INT PRIMARY KEY IDENTITY(1,1),
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) NOT NULL,
    PhoneNumber NVARCHAR(20) NOT NULL,
    Password NVARCHAR(100) NOT NULL,
    IDCardNumber NVARCHAR(20) NULL,
    [Role] NVARCHAR(20) NOT NULL CHECK ([Role] IN ('Customer', 'Staff', 'Admin')),
    IsActive BIT NOT NULL DEFAULT 1,
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    LastLogin DATETIME2 NULL,
    CONSTRAINT UQ_Users_Email UNIQUE (Email)
);
GO

-- Table: PassengerTypes
PRINT 'Creating Table: PassengerTypes';
IF OBJECT_ID('PassengerTypes', 'U') IS NOT NULL DROP TABLE PassengerTypes;
CREATE TABLE PassengerTypes (
    PassengerTypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(50) NOT NULL,
    DiscountPercentage DECIMAL(5,2) NOT NULL DEFAULT 0,
    Description NVARCHAR(MAX) NULL,
    RequiresDocument BIT NOT NULL DEFAULT 0,
    CONSTRAINT UQ_PassengerTypes_TypeName UNIQUE (TypeName)
);
GO

-- Table: Passengers
PRINT 'Creating Table: Passengers';
IF OBJECT_ID('Passengers', 'U') IS NOT NULL DROP TABLE Passengers;
CREATE TABLE Passengers (
    PassengerID INT PRIMARY KEY IDENTITY(1,1),
    FullName NVARCHAR(100) NOT NULL,
    IDCardNumber NVARCHAR(20) NULL,
    PassengerTypeID INT NOT NULL,
    DateOfBirth DATE NULL,
    UserID INT NULL,
    CONSTRAINT FK_Passengers_PassengerTypeID FOREIGN KEY (PassengerTypeID) REFERENCES PassengerTypes(PassengerTypeID),
    CONSTRAINT FK_Passengers_UserID FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table: Stations
PRINT 'Creating Table: Stations';
IF OBJECT_ID('Stations', 'U') IS NOT NULL DROP TABLE Stations;
CREATE TABLE Stations (
    StationID INT PRIMARY KEY IDENTITY(1,1),
    StationCode NVARCHAR(20) NOT NULL,
    StationName NVARCHAR(100) NOT NULL,
    Address NVARCHAR(MAX) NULL,
    City NVARCHAR(50) NULL,
    Region NVARCHAR(50) NULL,
    CONSTRAINT UQ_Stations_StationCode UNIQUE (StationCode),
    CONSTRAINT UQ_Stations_StationName_City UNIQUE (StationName, City)
);
GO

-- Table: Routes
PRINT 'Creating Table: Routes';
IF OBJECT_ID('Routes', 'U') IS NOT NULL DROP TABLE Routes;
CREATE TABLE Routes (
    RouteID INT PRIMARY KEY IDENTITY(1,1),
    RouteName NVARCHAR(100) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    CONSTRAINT UQ_Routes_RouteName UNIQUE (RouteName)
);
GO

-- Table: RouteStations
PRINT 'Creating Table: RouteStations';
IF OBJECT_ID('RouteStations', 'U') IS NOT NULL DROP TABLE RouteStations;
CREATE TABLE RouteStations (
    RouteStationID INT PRIMARY KEY IDENTITY(1,1),
    RouteID INT NOT NULL,
    StationID INT NOT NULL,
    SequenceNumber INT NOT NULL,
    DistanceFromStart DECIMAL(10,2) NOT NULL DEFAULT 0,
    DefaultStopTime INT NOT NULL DEFAULT 0, -- (minutes)
    CONSTRAINT FK_RouteStations_RouteID FOREIGN KEY (RouteID) REFERENCES Routes(RouteID),
    CONSTRAINT FK_RouteStations_StationID FOREIGN KEY (StationID) REFERENCES Stations(StationID),
    CONSTRAINT UQ_RouteStations_RouteStation UNIQUE (RouteID, StationID),
    CONSTRAINT UQ_RouteStations_RouteSequence UNIQUE (RouteID, SequenceNumber)
);
GO

-- Table: TrainTypes
PRINT 'Creating Table: TrainTypes';
IF OBJECT_ID('TrainTypes', 'U') IS NOT NULL DROP TABLE TrainTypes;
CREATE TABLE TrainTypes (
    TrainTypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(50) NOT NULL,
    Description NVARCHAR(MAX) NULL,
    CONSTRAINT UQ_TrainTypes_TypeName UNIQUE (TypeName)
);
GO

-- Table: Trains
PRINT 'Creating Table: Trains';
IF OBJECT_ID('Trains', 'U') IS NOT NULL DROP TABLE Trains;
CREATE TABLE Trains (
    TrainID INT PRIMARY KEY IDENTITY(1,1),
    TrainName NVARCHAR(50) NOT NULL, -- Train code like SE1, TN2
    TrainTypeID INT NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_Trains_TrainTypeID FOREIGN KEY (TrainTypeID) REFERENCES TrainTypes(TrainTypeID),
    CONSTRAINT UQ_Trains_TrainName UNIQUE (TrainName)
);
GO

-- Table: CoachTypes
PRINT 'Creating Table: CoachTypes';
IF OBJECT_ID('CoachTypes', 'U') IS NOT NULL DROP TABLE CoachTypes;
CREATE TABLE CoachTypes (
    CoachTypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(50) NOT NULL, -- e.g., Soft Seat AC, Hard Berth Non-AC
    PriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    Description NVARCHAR(MAX) NULL,
    CONSTRAINT UQ_CoachTypes_TypeName UNIQUE (TypeName)
);
GO

-- Table: Coaches
PRINT 'Creating Table: Coaches';
IF OBJECT_ID('Coaches', 'U') IS NOT NULL DROP TABLE Coaches;
CREATE TABLE Coaches (
    CoachID INT PRIMARY KEY IDENTITY(1,1),
    TrainID INT NOT NULL,
    CoachNumber INT NOT NULL, -- Integer identifier/order for the coach
    CoachName NVARCHAR(50) NOT NULL, -- Display name like "Toa 5", "Toa VIP A"
    CoachTypeID INT NOT NULL,
    Capacity INT NOT NULL, -- Capacity of this specific coach
    PositionInTrain INT NOT NULL, -- Physical position in the train (1, 2, 3...)
    CONSTRAINT FK_Coaches_TrainID FOREIGN KEY (TrainID) REFERENCES Trains(TrainID),
    CONSTRAINT FK_Coaches_CoachTypeID FOREIGN KEY (CoachTypeID) REFERENCES CoachTypes(CoachTypeID),
    CONSTRAINT UQ_Coaches_TrainCoachNumber UNIQUE (TrainID, CoachNumber),
    CONSTRAINT UQ_Coaches_TrainPosition UNIQUE (TrainID, PositionInTrain)
);
GO

-- Table: SeatTypes
PRINT 'Creating Table: SeatTypes';
IF OBJECT_ID('SeatTypes', 'U') IS NOT NULL DROP TABLE SeatTypes;
CREATE TABLE SeatTypes (
    SeatTypeID INT PRIMARY KEY IDENTITY(1,1),
    TypeName NVARCHAR(50) NOT NULL, -- e.g., Soft Seat, Upper Berth AC
    PriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    Description NVARCHAR(MAX) NULL,
    CONSTRAINT UQ_SeatTypes_TypeName UNIQUE (TypeName)
);
GO

-- Table: Seats
PRINT 'Creating Table: Seats';
IF OBJECT_ID('Seats', 'U') IS NOT NULL DROP TABLE Seats;
CREATE TABLE Seats (
    SeatID INT PRIMARY KEY IDENTITY(1,1),
    CoachID INT NOT NULL,
    SeatNumber INT NOT NULL, -- Integer identifier/order for the seat within the coach
    SeatName NVARCHAR(50) NOT NULL, -- Display name like "12A", "G25"
    SeatTypeID INT NOT NULL,
    IsEnabled BIT NOT NULL DEFAULT 1, -- Is the seat available for sale?
    CONSTRAINT FK_Seats_CoachID FOREIGN KEY (CoachID) REFERENCES Coaches(CoachID),
    CONSTRAINT FK_Seats_SeatTypeID FOREIGN KEY (SeatTypeID) REFERENCES SeatTypes(SeatTypeID),
    CONSTRAINT UQ_Seats_CoachSeatNumber UNIQUE (CoachID, SeatNumber),
    CONSTRAINT UQ_Seats_CoachSeatName UNIQUE (CoachID, SeatName) -- Ensuring display name is unique within a coach
);
GO

-- Table: Trips
PRINT 'Creating Table: Trips';
IF OBJECT_ID('Trips', 'U') IS NOT NULL DROP TABLE Trips;
CREATE TABLE Trips (
    TripID INT PRIMARY KEY IDENTITY(1,1),
    TrainID INT NOT NULL,
    RouteID INT NOT NULL,
    DepartureDateTime DATETIME2 NOT NULL,
    ArrivalDateTime DATETIME2 NOT NULL,
    IsHolidayTrip BIT NOT NULL DEFAULT 0,
    TripStatus NVARCHAR(20) NOT NULL DEFAULT 'Scheduled' CHECK (TripStatus IN ('Scheduled', 'In Progress', 'Completed', 'Cancelled')),
    BasePriceMultiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    CONSTRAINT FK_Trips_TrainID FOREIGN KEY (TrainID) REFERENCES Trains(TrainID),
    CONSTRAINT FK_Trips_RouteID FOREIGN KEY (RouteID) REFERENCES Routes(RouteID)
);
GO

-- Table: TripStations
PRINT 'Creating Table: TripStations';
IF OBJECT_ID('TripStations', 'U') IS NOT NULL DROP TABLE TripStations;
CREATE TABLE TripStations (
    TripStationID INT PRIMARY KEY IDENTITY(1,1),
    TripID INT NOT NULL,
    StationID INT NOT NULL,
    SequenceNumber INT NOT NULL, -- Sequence of station in this trip
    ScheduledArrival DATETIME2 NULL,
    ScheduledDeparture DATETIME2 NULL,
    ActualArrival DATETIME2 NULL,
    ActualDeparture DATETIME2 NULL,
    CONSTRAINT FK_TripStations_TripID FOREIGN KEY (TripID) REFERENCES Trips(TripID),
    CONSTRAINT FK_TripStations_StationID FOREIGN KEY (StationID) REFERENCES Stations(StationID),
    CONSTRAINT UQ_TripStations_TripStation UNIQUE (TripID, StationID),
    CONSTRAINT UQ_TripStations_TripSequence UNIQUE (TripID, SequenceNumber)
);
GO

-- Table: PricingRules
PRINT 'Creating Table: PricingRules';
IF OBJECT_ID('PricingRules', 'U') IS NOT NULL DROP TABLE PricingRules;
CREATE TABLE PricingRules (
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
    [Priority] INT NOT NULL DEFAULT 0, -- Rule application priority
    EffectiveFromDate DATE NOT NULL,
    EffectiveToDate DATE NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_PricingRules_TrainTypeID FOREIGN KEY (TrainTypeID) REFERENCES TrainTypes(TrainTypeID),
    CONSTRAINT FK_PricingRules_CoachTypeID FOREIGN KEY (CoachTypeID) REFERENCES CoachTypes(CoachTypeID),
    CONSTRAINT FK_PricingRules_SeatTypeID FOREIGN KEY (SeatTypeID) REFERENCES SeatTypes(SeatTypeID),
    CONSTRAINT FK_PricingRules_PassengerTypeID FOREIGN KEY (PassengerTypeID) REFERENCES PassengerTypes(PassengerTypeID),
    CONSTRAINT FK_PricingRules_RouteID FOREIGN KEY (RouteID) REFERENCES Routes(RouteID),
    CONSTRAINT FK_PricingRules_TripID FOREIGN KEY (TripID) REFERENCES Trips(TripID)
);
GO

-- Table: Bookings
PRINT 'Creating Table: Bookings';
IF OBJECT_ID('Bookings', 'U') IS NOT NULL DROP TABLE Bookings;
CREATE TABLE Bookings (
    BookingID INT PRIMARY KEY IDENTITY(1,1),
    BookingCode NVARCHAR(50) NOT NULL,
    UserID INT NOT NULL,
    BookingDateTime DATETIME2 NOT NULL DEFAULT GETDATE(),
    TotalPrice DECIMAL(10,2) NOT NULL,
    BookingStatus NVARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (BookingStatus IN ('Pending', 'Confirmed', 'Cancelled', 'Completed')),
    PaymentStatus NVARCHAR(20) NOT NULL DEFAULT 'Unpaid' CHECK (PaymentStatus IN ('Unpaid', 'Paid', 'Refunded')),
    Source NVARCHAR(50) NULL, -- e.g., Web, App, Counter
    ExpiredAt DATETIME2 NULL, -- Booking hold expiration
    CONSTRAINT UQ_Bookings_BookingCode UNIQUE (BookingCode),
    CONSTRAINT FK_Bookings_UserID FOREIGN KEY (UserID) REFERENCES Users(UserID)
);
GO

-- Table: Tickets
PRINT 'Creating Table: Tickets';
IF OBJECT_ID('Tickets', 'U') IS NOT NULL DROP TABLE Tickets;
CREATE TABLE Tickets (
    TicketID INT PRIMARY KEY IDENTITY(1,1),
    TicketCode NVARCHAR(50) NOT NULL,
    BookingID INT NOT NULL,
    TripID INT NOT NULL,
    SeatID INT NOT NULL, -- FK to the physical Seat
    PassengerID INT NOT NULL,
    StartStationID INT NOT NULL,
    EndStationID INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL, -- Final calculated price
    TicketStatus NVARCHAR(20) NOT NULL DEFAULT 'Valid' CHECK (TicketStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
    CoachNameSnapshot NVARCHAR(50) NOT NULL, -- Snapshot of Coaches.CoachName at time of booking
    SeatNameSnapshot NVARCHAR(50) NOT NULL,   -- Snapshot of Seats.SeatName at time of booking
    PassengerName NVARCHAR(100) NOT NULL,     -- Snapshot
    PassengerIDCardNumber NVARCHAR(20) NULL,  -- Snapshot
    FareComponentDetails NVARCHAR(MAX) NULL,  -- JSON string of fare breakdown
    ParentTicketID INT NULL,                  -- For exchanged tickets
    CONSTRAINT UQ_Tickets_TicketCode UNIQUE (TicketCode),
    CONSTRAINT FK_Tickets_BookingID FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID),
    CONSTRAINT FK_Tickets_TripID FOREIGN KEY (TripID) REFERENCES Trips(TripID),
    CONSTRAINT FK_Tickets_SeatID FOREIGN KEY (SeatID) REFERENCES Seats(SeatID),
    CONSTRAINT FK_Tickets_PassengerID FOREIGN KEY (PassengerID) REFERENCES Passengers(PassengerID),
    CONSTRAINT FK_Tickets_StartStationID FOREIGN KEY (StartStationID) REFERENCES Stations(StationID),
    CONSTRAINT FK_Tickets_EndStationID FOREIGN KEY (EndStationID) REFERENCES Stations(StationID),
    CONSTRAINT FK_Tickets_ParentTicketID FOREIGN KEY (ParentTicketID) REFERENCES Tickets(TicketID)
);
GO

-- Table: PaymentTransactions
PRINT 'Creating Table: PaymentTransactions';
IF OBJECT_ID('PaymentTransactions', 'U') IS NOT NULL DROP TABLE PaymentTransactions;
CREATE TABLE PaymentTransactions (
    TransactionID INT PRIMARY KEY IDENTITY(1,1),
    BookingID INT NOT NULL,
    PaymentGatewayTransactionID NVARCHAR(100) NULL,
    PaymentGateway NVARCHAR(50) NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    TransactionDateTime DATETIME2 NOT NULL DEFAULT GETDATE(),
    Status NVARCHAR(20) NOT NULL CHECK (Status IN ('Pending', 'Success', 'Failed', 'Cancelled')),
    Notes NVARCHAR(MAX) NULL,
    CONSTRAINT FK_PaymentTransactions_BookingID FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID)
);
GO

-- Table: TicketStatusHistory
PRINT 'Creating Table: TicketStatusHistory';
IF OBJECT_ID('TicketStatusHistory', 'U') IS NOT NULL DROP TABLE TicketStatusHistory;
CREATE TABLE TicketStatusHistory (
    HistoryID INT PRIMARY KEY IDENTITY(1,1),
    TicketID INT NOT NULL,
    OldStatus NVARCHAR(20) NULL CHECK (OldStatus IS NULL OR OldStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
    NewStatus NVARCHAR(20) NOT NULL CHECK (NewStatus IN ('Valid', 'Used', 'Cancelled', 'Expired')),
    ChangedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
    Reason NVARCHAR(500) NULL,
    ChangedByUserID INT NULL,
    CONSTRAINT FK_TicketStatusHistory_TicketID FOREIGN KEY (TicketID) REFERENCES Tickets(TicketID),
    CONSTRAINT FK_TicketStatusHistory_ChangedByUserID FOREIGN KEY (ChangedByUserID) REFERENCES Users(UserID)
);
GO

-- Table: CancellationPolicies
PRINT 'Creating Table: CancellationPolicies';
IF OBJECT_ID('CancellationPolicies', 'U') IS NOT NULL DROP TABLE CancellationPolicies;
CREATE TABLE CancellationPolicies (
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
GO

-- Table: Refunds
PRINT 'Creating Table: Refunds';
IF OBJECT_ID('Refunds', 'U') IS NOT NULL DROP TABLE Refunds;
CREATE TABLE Refunds (
    RefundID INT PRIMARY KEY IDENTITY(1,1),
    TicketID INT NOT NULL,
    BookingID INT NOT NULL,
    AppliedPolicyID INT NULL,
    OriginalTicketPrice DECIMAL(10,2) NOT NULL,
    FeeAmount DECIMAL(10,2) NOT NULL DEFAULT 0,
    ActualRefundAmount DECIMAL(10,2) NOT NULL,
    RequestedAt DATETIME2 NOT NULL DEFAULT GETDATE(),
    ProcessedAt DATETIME2 NULL,
    Status NVARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (Status IN ('Pending', 'Approved', 'Rejected', 'Processed', 'Failed')),
    RefundMethod NVARCHAR(50) NULL,
    Notes NVARCHAR(MAX) NULL,
    RequestedByUserID INT NULL,
    ProcessedByUserID INT NULL,
    RefundTransactionID NVARCHAR(100) NULL,
    CONSTRAINT UQ_Refunds_TicketID UNIQUE (TicketID),
    CONSTRAINT FK_Refunds_TicketID FOREIGN KEY (TicketID) REFERENCES Tickets(TicketID),
    CONSTRAINT FK_Refunds_BookingID FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID),
    CONSTRAINT FK_Refunds_AppliedPolicyID FOREIGN KEY (AppliedPolicyID) REFERENCES CancellationPolicies(PolicyID),
    CONSTRAINT FK_Refunds_RequestedByUserID FOREIGN KEY (RequestedByUserID) REFERENCES Users(UserID),
    CONSTRAINT FK_Refunds_ProcessedByUserID FOREIGN KEY (ProcessedByUserID) REFERENCES Users(UserID)
);
GO

PRINT 'All tables created successfully in TrainTicketSystemDB_V1_FinalDesign.';
GO