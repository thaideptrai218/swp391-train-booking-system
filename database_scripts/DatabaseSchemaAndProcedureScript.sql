USE [master]
GO
/****** Object:  Database [TrainTicketSystemDB_V2]    Script Date: 7/23/2025 12:54:03 PM ******/
CREATE DATABASE [TrainTicketSystemDB_V2]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'TrainTicketSystemDB_V2', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\TrainTicketSystemDB_V2.mdf' , SIZE = 73728KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'TrainTicketSystemDB_V2_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\TrainTicketSystemDB_V2_log.ldf' , SIZE = 73728KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [TrainTicketSystemDB_V2].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ARITHABORT OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET  DISABLE_BROKER 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET RECOVERY FULL 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET  MULTI_USER 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET DB_CHAINING OFF 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'TrainTicketSystemDB_V2', N'ON'
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET QUERY_STORE = ON
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [TrainTicketSystemDB_V2]
GO
/****** Object:  UserDefinedFunction [dbo].[GetApplicableBasePriceKm]    Script Date: 7/23/2025 12:54:03 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE FUNCTION [dbo].[GetApplicableBasePriceKm]
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
/****** Object:  UserDefinedFunction [dbo].[GetDistanceBetweenStationsOnRoute]    Script Date: 7/23/2025 12:54:03 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE FUNCTION [dbo].[GetDistanceBetweenStationsOnRoute]
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
/****** Object:  Table [dbo].[Bookings]    Script Date: 7/23/2025 12:54:03 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Bookings](
	[BookingID] [int] IDENTITY(1,1) NOT NULL,
	[BookingCode] [nvarchar](50) NOT NULL,
	[UserID] [int] NOT NULL,
	[BookingDateTime] [datetime2](7) NOT NULL,
	[TotalPrice] [decimal](10, 2) NOT NULL,
	[BookingStatus] [nvarchar](20) NOT NULL,
	[PaymentStatus] [nvarchar](20) NOT NULL,
	[Source] [nvarchar](50) NULL,
	[ExpiredAt] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[BookingID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Bookings_BookingCode] UNIQUE NONCLUSTERED 
(
	[BookingCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CancellationPolicies]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CancellationPolicies](
	[PolicyID] [int] IDENTITY(1,1) NOT NULL,
	[PolicyName] [nvarchar](150) NOT NULL,
	[HoursBeforeDeparture_Min] [int] NOT NULL,
	[HoursBeforeDeparture_Max] [int] NULL,
	[FeePercentage] [decimal](5, 2) NULL,
	[FixedFeeAmount] [decimal](10, 2) NULL,
	[IsRefundable] [bit] NOT NULL,
	[Description] [nvarchar](max) NULL,
	[IsActive] [bit] NOT NULL,
	[EffectiveFromDate] [date] NOT NULL,
	[EffectiveToDate] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[PolicyID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_CancellationPolicies_PolicyName] UNIQUE NONCLUSTERED 
(
	[PolicyName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Coaches]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Coaches](
	[CoachID] [int] IDENTITY(1,1) NOT NULL,
	[TrainID] [int] NOT NULL,
	[CoachNumber] [int] NOT NULL,
	[CoachName] [nvarchar](50) NOT NULL,
	[CoachTypeID] [int] NOT NULL,
	[Capacity] [int] NOT NULL,
	[PositionInTrain] [int] NOT NULL,
	[IsActive] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CoachID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Coaches_TrainCoachNumber] UNIQUE NONCLUSTERED 
(
	[TrainID] ASC,
	[CoachNumber] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Coaches_TrainPosition] UNIQUE NONCLUSTERED 
(
	[TrainID] ASC,
	[PositionInTrain] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[CoachTypes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[CoachTypes](
	[CoachTypeID] [int] IDENTITY(1,1) NOT NULL,
	[TypeName] [nvarchar](50) NOT NULL,
	[PriceMultiplier] [decimal](5, 2) NOT NULL,
	[IsCompartmented] [bit] NOT NULL,
	[DefaultCompartmentCapacity] [int] NULL,
	[Description] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[CoachTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_CoachTypes_TypeName] UNIQUE NONCLUSTERED 
(
	[TypeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FeaturedRoutes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FeaturedRoutes](
	[FeaturedRouteID] [int] IDENTITY(1,1) NOT NULL,
	[OriginStationID] [int] NOT NULL,
	[DestinationStationID] [int] NOT NULL,
	[RouteID] [int] NULL,
	[DisplayName] [nvarchar](255) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[ImageURL] [nvarchar](512) NULL,
	[DisplayOrder] [int] NOT NULL,
	[IsActive] [bit] NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
	[UpdatedAt] [datetime2](7) NULL,
	[UpdatedByUserID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[FeaturedRouteID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_FeaturedRoutes_OD] UNIQUE NONCLUSTERED 
(
	[OriginStationID] ASC,
	[DestinationStationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Feedback]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Feedback](
	[FeedbackID] [int] IDENTITY(1,1) NOT NULL,
	[UserID] [int] NOT NULL,
	[FeedbackTypeID] [int] NOT NULL,
	[FullName] [nvarchar](100) NOT NULL,
	[Email] [nvarchar](100) NOT NULL,
	[FeedbackContent] [nvarchar](max) NOT NULL,
	[TicketName] [nvarchar](100) NULL,
	[Description] [nvarchar](max) NULL,
	[SubmittedAt] [datetime2](7) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[Response] [nvarchar](max) NULL,
	[RespondedAt] [datetime2](7) NULL,
	[RespondedByUserID] [int] NULL,
	[Source] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[FeedbackID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[FeedbackTypes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[FeedbackTypes](
	[FeedbackTypeID] [int] IDENTITY(1,1) NOT NULL,
	[TypeName] [nvarchar](50) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[IsActive] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[FeedbackTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_FeedbackTypes_TypeName] UNIQUE NONCLUSTERED 
(
	[TypeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[HolidayPrices]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[HolidayPrices](
	[HolidayID] [int] IDENTITY(1,1) NOT NULL,
	[HolidayName] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](255) NULL,
	[StartDate] [date] NOT NULL,
	[EndDate] [date] NOT NULL,
	[DiscountPercentage] [decimal](5, 2) NOT NULL,
	[IsActive] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[HolidayID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Locations]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Locations](
	[LocationID] [int] IDENTITY(1,1) NOT NULL,
	[LocationName] [nvarchar](100) NULL,
	[City] [nvarchar](100) NULL,
	[Region] [nvarchar](100) NULL,
	[Link] [nvarchar](255) NULL,
	[LocationCode] [nvarchar](50) NULL,
PRIMARY KEY CLUSTERED 
(
	[LocationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Messages]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Messages](
	[MessageID] [int] IDENTITY(1,1) NOT NULL,
	[UserID] [int] NOT NULL,
	[StaffID] [int] NULL,
	[Content] [nvarchar](500) NOT NULL,
	[Timestamp] [datetime] NULL,
	[SenderType] [varchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[MessageID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Passengers]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Passengers](
	[PassengerID] [int] IDENTITY(1,1) NOT NULL,
	[FullName] [nvarchar](100) NOT NULL,
	[IDCardNumber] [nvarchar](20) NULL,
	[PassengerTypeID] [int] NOT NULL,
	[DateOfBirth] [date] NULL,
	[UserID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[PassengerID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PassengerTypes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PassengerTypes](
	[PassengerTypeID] [int] IDENTITY(1,1) NOT NULL,
	[TypeName] [nvarchar](50) NOT NULL,
	[DiscountPercentage] [decimal](5, 2) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[RequiresDocument] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[PassengerTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_PassengerTypes_TypeName] UNIQUE NONCLUSTERED 
(
	[TypeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PaymentTransactions]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PaymentTransactions](
	[TransactionID] [int] IDENTITY(1,1) NOT NULL,
	[BookingID] [int] NOT NULL,
	[PaymentGatewayTransactionID] [nvarchar](100) NULL,
	[PaymentGateway] [nvarchar](50) NOT NULL,
	[Amount] [decimal](10, 2) NOT NULL,
	[TransactionDateTime] [datetime2](7) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[Notes] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[TransactionID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[PricingRules]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PricingRules](
	[RuleID] [int] IDENTITY(1,1) NOT NULL,
	[RuleName] [nvarchar](150) NOT NULL,
	[Description] [nvarchar](500) NULL,
	[BasePricePerKm] [decimal](10, 2) NOT NULL,
	[TrainTypeID] [int] NULL,
	[RouteID] [int] NULL,
	[IsForRoundTrip] [bit] NULL,
	[ApplicableDateStart] [date] NULL,
	[ApplicableDateEnd] [date] NULL,
	[Priority] [int] NOT NULL,
	[IsActive] [bit] NOT NULL,
	[EffectiveFromDate] [date] NOT NULL,
	[EffectiveToDate] [date] NULL,
PRIMARY KEY CLUSTERED 
(
	[RuleID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Refunds]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Refunds](
	[RefundID] [int] IDENTITY(1,1) NOT NULL,
	[TicketID] [int] NOT NULL,
	[BookingID] [int] NOT NULL,
	[AppliedPolicyID] [int] NULL,
	[OriginalTicketPrice] [decimal](10, 2) NOT NULL,
	[FeeAmount] [decimal](10, 2) NOT NULL,
	[ActualRefundAmount] [decimal](10, 2) NOT NULL,
	[RequestedAt] [datetime2](7) NULL,
	[ProcessedAt] [datetime2](7) NOT NULL,
	[Status] [nvarchar](20) NOT NULL,
	[RefundMethod] [nvarchar](50) NULL,
	[Notes] [nvarchar](max) NULL,
	[RequestedByUserID] [int] NULL,
	[ProcessedByUserID] [int] NULL,
	[RefundTransactionID] [nvarchar](100) NULL,
PRIMARY KEY CLUSTERED 
(
	[RefundID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Refunds_TicketID] UNIQUE NONCLUSTERED 
(
	[TicketID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Routes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Routes](
	[RouteID] [int] IDENTITY(1,1) NOT NULL,
	[RouteName] [nvarchar](100) NOT NULL,
	[Description] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[RouteID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Routes_RouteName] UNIQUE NONCLUSTERED 
(
	[RouteName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[RouteStations]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[RouteStations](
	[RouteStationID] [int] IDENTITY(1,1) NOT NULL,
	[RouteID] [int] NOT NULL,
	[StationID] [int] NOT NULL,
	[SequenceNumber] [int] NOT NULL,
	[DistanceFromStart] [decimal](10, 2) NOT NULL,
	[DefaultStopTime] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RouteStationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_RouteStations_RouteSequence] UNIQUE NONCLUSTERED 
(
	[RouteID] ASC,
	[SequenceNumber] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_RouteStations_RouteStation] UNIQUE NONCLUSTERED 
(
	[RouteID] ASC,
	[StationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Seats]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Seats](
	[SeatID] [int] IDENTITY(1,1) NOT NULL,
	[CoachID] [int] NOT NULL,
	[SeatNumber] [int] NOT NULL,
	[SeatName] [nvarchar](50) NOT NULL,
	[SeatTypeID] [int] NOT NULL,
	[IsEnabled] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[SeatID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Seats_CoachSeatName] UNIQUE NONCLUSTERED 
(
	[CoachID] ASC,
	[SeatName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Seats_CoachSeatNumber] UNIQUE NONCLUSTERED 
(
	[CoachID] ASC,
	[SeatNumber] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[SeatTypes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SeatTypes](
	[SeatTypeID] [int] IDENTITY(1,1) NOT NULL,
	[TypeName] [nvarchar](50) NOT NULL,
	[PriceMultiplier] [decimal](5, 2) NOT NULL,
	[Description] [nvarchar](max) NULL,
	[BerthLevel] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[SeatTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_SeatTypes_TypeName] UNIQUE NONCLUSTERED 
(
	[TypeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Stations]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Stations](
	[StationID] [int] IDENTITY(1,1) NOT NULL,
	[StationCode] [nvarchar](20) NOT NULL,
	[StationName] [nvarchar](100) NOT NULL,
	[Address] [nvarchar](max) NULL,
	[City] [nvarchar](50) NULL,
	[Region] [nvarchar](50) NULL,
	[PhoneNumber] [nvarchar](30) NULL,
PRIMARY KEY CLUSTERED 
(
	[StationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Stations_StationCode] UNIQUE NONCLUSTERED 
(
	[StationCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Stations_StationName_City] UNIQUE NONCLUSTERED 
(
	[StationName] ASC,
	[City] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TemporarySeatHolds]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TemporarySeatHolds](
	[HoldID] [int] IDENTITY(1,1) NOT NULL,
	[TripID] [int] NOT NULL,
	[SeatID] [int] NOT NULL,
	[CoachID] [int] NOT NULL,
	[legOriginStationId] [int] NOT NULL,
	[legDestinationStationId] [int] NOT NULL,
	[SessionID] [nvarchar](100) NOT NULL,
	[UserID] [int] NULL,
	[ExpiresAt] [datetime2](7) NOT NULL,
	[CreatedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[HoldID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TempRefundRequests]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TempRefundRequests](
	[RefundID] [int] IDENTITY(1,1) NOT NULL,
	[TicketID] [int] NULL,
	[AppliedPolicyID] [int] NULL,
	[FeeAmount] [decimal](10, 2) NULL,
	[ActualRefundAmount] [decimal](10, 2) NULL,
	[RequestedAt] [datetime2](7) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[RefundID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Tickets]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tickets](
	[TicketID] [int] IDENTITY(1,1) NOT NULL,
	[TicketCode] [nvarchar](50) NOT NULL,
	[BookingID] [int] NOT NULL,
	[TripID] [int] NOT NULL,
	[SeatID] [int] NOT NULL,
	[PassengerID] [int] NOT NULL,
	[StartStationID] [int] NOT NULL,
	[EndStationID] [int] NOT NULL,
	[Price] [decimal](10, 2) NOT NULL,
	[TicketStatus] [nvarchar](20) NOT NULL,
	[CoachNameSnapshot] [nvarchar](50) NOT NULL,
	[SeatNameSnapshot] [nvarchar](50) NOT NULL,
	[PassengerNameSnapshot] [nvarchar](100) NOT NULL,
	[PassengerIDCardNumberSnapshot] [nvarchar](20) NULL,
	[FareComponentDetails] [nvarchar](max) NULL,
	[ParentTicketID] [int] NULL,
	[IsRefundable] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[TicketID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Tickets_TicketCode] UNIQUE NONCLUSTERED 
(
	[TicketCode] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TicketStatusHistory]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TicketStatusHistory](
	[HistoryID] [int] IDENTITY(1,1) NOT NULL,
	[TicketID] [int] NOT NULL,
	[OldStatus] [nvarchar](20) NULL,
	[NewStatus] [nvarchar](20) NOT NULL,
	[ChangedAt] [datetime2](7) NOT NULL,
	[Reason] [nvarchar](500) NULL,
	[ChangedByUserID] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[HistoryID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Trains]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Trains](
	[TrainID] [int] IDENTITY(1,1) NOT NULL,
	[TrainName] [nvarchar](50) NOT NULL,
	[TrainTypeID] [int] NOT NULL,
	[IsActive] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[TrainID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Trains_TrainName] UNIQUE NONCLUSTERED 
(
	[TrainName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TrainTypes]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TrainTypes](
	[TrainTypeID] [int] IDENTITY(1,1) NOT NULL,
	[TypeName] [nvarchar](50) NOT NULL,
	[AverageVelocity] [decimal](5, 1) NULL,
	[Description] [nvarchar](max) NULL,
PRIMARY KEY CLUSTERED 
(
	[TrainTypeID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_TrainTypes_TypeName] UNIQUE NONCLUSTERED 
(
	[TypeName] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Trips]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Trips](
	[TripID] [int] IDENTITY(1,1) NOT NULL,
	[TrainID] [int] NOT NULL,
	[RouteID] [int] NOT NULL,
	[DepartureDateTime] [datetime2](7) NOT NULL,
	[ArrivalDateTime] [datetime2](7) NOT NULL,
	[IsHolidayTrip] [bit] NOT NULL,
	[TripStatus] [nvarchar](20) NOT NULL,
	[BasePriceMultiplier] [decimal](5, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[TripID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[TripStations]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[TripStations](
	[TripStationID] [int] IDENTITY(1,1) NOT NULL,
	[TripID] [int] NOT NULL,
	[StationID] [int] NOT NULL,
	[SequenceNumber] [int] NOT NULL,
	[ScheduledArrival] [datetime2](7) NULL,
	[ScheduledDeparture] [datetime2](7) NULL,
	[ActualArrival] [datetime2](7) NULL,
	[ActualDeparture] [datetime2](7) NULL,
PRIMARY KEY CLUSTERED 
(
	[TripStationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_TripStations_TripSequence] UNIQUE NONCLUSTERED 
(
	[TripID] ASC,
	[SequenceNumber] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_TripStations_TripStation] UNIQUE NONCLUSTERED 
(
	[TripID] ASC,
	[StationID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Users]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Users](
	[UserID] [int] IDENTITY(1,1) NOT NULL,
	[FullName] [nvarchar](100) NOT NULL,
	[Email] [nvarchar](100) NOT NULL,
	[PhoneNumber] [nvarchar](20) NOT NULL,
	[PasswordHash] [nvarchar](255) NULL,
	[IDCardNumber] [nvarchar](20) NULL,
	[Role] [nvarchar](20) NOT NULL,
	[IsActive] [bit] NOT NULL,
	[CreatedAt] [datetime2](7) NULL,
	[LastLogin] [datetime2](7) NULL,
	[IsGuestAccount] [bit] NOT NULL,
	[DateOfBirth] [date] NULL,
	[Gender] [nvarchar](20) NULL,
	[Address] [nvarchar](max) NULL,
	[AvatarPath] [nvarchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[UserID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ_Users_Email] UNIQUE NONCLUSTERED 
(
	[Email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Index [IX_TemporarySeatHolds_ExpiresAt]    Script Date: 7/23/2025 12:54:04 PM ******/
CREATE NONCLUSTERED INDEX [IX_TemporarySeatHolds_ExpiresAt] ON [dbo].[TemporarySeatHolds]
(
	[ExpiresAt] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [IX_TemporarySeatHolds_TripSeat_Active]    Script Date: 7/23/2025 12:54:04 PM ******/
CREATE NONCLUSTERED INDEX [IX_TemporarySeatHolds_TripSeat_Active] ON [dbo].[TemporarySeatHolds]
(
	[TripID] ASC,
	[SeatID] ASC,
	[ExpiresAt] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT (getdate()) FOR [BookingDateTime]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT ('Pending') FOR [BookingStatus]
GO
ALTER TABLE [dbo].[Bookings] ADD  DEFAULT ('Unpaid') FOR [PaymentStatus]
GO
ALTER TABLE [dbo].[CancellationPolicies] ADD  DEFAULT ((1)) FOR [IsRefundable]
GO
ALTER TABLE [dbo].[CancellationPolicies] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[CancellationPolicies] ADD  DEFAULT (getdate()) FOR [EffectiveFromDate]
GO
ALTER TABLE [dbo].[Coaches] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[CoachTypes] ADD  DEFAULT ((1.00)) FOR [PriceMultiplier]
GO
ALTER TABLE [dbo].[CoachTypes] ADD  DEFAULT ((0)) FOR [IsCompartmented]
GO
ALTER TABLE [dbo].[FeaturedRoutes] ADD  DEFAULT ((0)) FOR [DisplayOrder]
GO
ALTER TABLE [dbo].[FeaturedRoutes] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[FeaturedRoutes] ADD  DEFAULT (getdate()) FOR [CreatedAt]
GO
ALTER TABLE [dbo].[Feedback] ADD  DEFAULT (getdate()) FOR [SubmittedAt]
GO
ALTER TABLE [dbo].[Feedback] ADD  DEFAULT ('Pending') FOR [Status]
GO
ALTER TABLE [dbo].[FeedbackTypes] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[Messages] ADD  DEFAULT (getdate()) FOR [Timestamp]
GO
ALTER TABLE [dbo].[PassengerTypes] ADD  DEFAULT ((0.00)) FOR [DiscountPercentage]
GO
ALTER TABLE [dbo].[PassengerTypes] ADD  DEFAULT ((0)) FOR [RequiresDocument]
GO
ALTER TABLE [dbo].[PaymentTransactions] ADD  DEFAULT (getdate()) FOR [TransactionDateTime]
GO
ALTER TABLE [dbo].[PricingRules] ADD  DEFAULT ((0)) FOR [Priority]
GO
ALTER TABLE [dbo].[PricingRules] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[PricingRules] ADD  DEFAULT (getdate()) FOR [EffectiveFromDate]
GO
ALTER TABLE [dbo].[Refunds] ADD  DEFAULT ((0.00)) FOR [FeeAmount]
GO
ALTER TABLE [dbo].[Refunds] ADD  DEFAULT (getdate()) FOR [ProcessedAt]
GO
ALTER TABLE [dbo].[Refunds] ADD  DEFAULT ('Pending') FOR [Status]
GO
ALTER TABLE [dbo].[RouteStations] ADD  DEFAULT ((0.00)) FOR [DistanceFromStart]
GO
ALTER TABLE [dbo].[RouteStations] ADD  DEFAULT ((0)) FOR [DefaultStopTime]
GO
ALTER TABLE [dbo].[Seats] ADD  DEFAULT ((1)) FOR [IsEnabled]
GO
ALTER TABLE [dbo].[SeatTypes] ADD  DEFAULT ((1.00)) FOR [PriceMultiplier]
GO
ALTER TABLE [dbo].[TemporarySeatHolds] ADD  DEFAULT (getdate()) FOR [CreatedAt]
GO
ALTER TABLE [dbo].[TempRefundRequests] ADD  DEFAULT (getdate()) FOR [RequestedAt]
GO
ALTER TABLE [dbo].[Tickets] ADD  CONSTRAINT [DF_TicketStatus]  DEFAULT ('Valid') FOR [TicketStatus]
GO
ALTER TABLE [dbo].[Tickets] ADD  DEFAULT ((1)) FOR [IsRefundable]
GO
ALTER TABLE [dbo].[TicketStatusHistory] ADD  DEFAULT (getdate()) FOR [ChangedAt]
GO
ALTER TABLE [dbo].[Trains] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[Trips] ADD  DEFAULT ((0)) FOR [IsHolidayTrip]
GO
ALTER TABLE [dbo].[Trips] ADD  DEFAULT ('Scheduled') FOR [TripStatus]
GO
ALTER TABLE [dbo].[Trips] ADD  DEFAULT ((1.00)) FOR [BasePriceMultiplier]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT ((1)) FOR [IsActive]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT (getdate()) FOR [CreatedAt]
GO
ALTER TABLE [dbo].[Users] ADD  DEFAULT ((0)) FOR [IsGuestAccount]
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD  CONSTRAINT [FK_Bookings_UserID] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Bookings] CHECK CONSTRAINT [FK_Bookings_UserID]
GO
ALTER TABLE [dbo].[Coaches]  WITH CHECK ADD  CONSTRAINT [FK_Coaches_CoachTypeID] FOREIGN KEY([CoachTypeID])
REFERENCES [dbo].[CoachTypes] ([CoachTypeID])
GO
ALTER TABLE [dbo].[Coaches] CHECK CONSTRAINT [FK_Coaches_CoachTypeID]
GO
ALTER TABLE [dbo].[Coaches]  WITH CHECK ADD  CONSTRAINT [FK_Coaches_TrainID] FOREIGN KEY([TrainID])
REFERENCES [dbo].[Trains] ([TrainID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Coaches] CHECK CONSTRAINT [FK_Coaches_TrainID]
GO
ALTER TABLE [dbo].[FeaturedRoutes]  WITH CHECK ADD  CONSTRAINT [FK_FeaturedRoutes_DestinationStation] FOREIGN KEY([DestinationStationID])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[FeaturedRoutes] CHECK CONSTRAINT [FK_FeaturedRoutes_DestinationStation]
GO
ALTER TABLE [dbo].[FeaturedRoutes]  WITH CHECK ADD  CONSTRAINT [FK_FeaturedRoutes_OriginStation] FOREIGN KEY([OriginStationID])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[FeaturedRoutes] CHECK CONSTRAINT [FK_FeaturedRoutes_OriginStation]
GO
ALTER TABLE [dbo].[FeaturedRoutes]  WITH CHECK ADD  CONSTRAINT [FK_FeaturedRoutes_Route] FOREIGN KEY([RouteID])
REFERENCES [dbo].[Routes] ([RouteID])
GO
ALTER TABLE [dbo].[FeaturedRoutes] CHECK CONSTRAINT [FK_FeaturedRoutes_Route]
GO
ALTER TABLE [dbo].[FeaturedRoutes]  WITH CHECK ADD  CONSTRAINT [FK_FeaturedRoutes_UpdatedByUser] FOREIGN KEY([UpdatedByUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[FeaturedRoutes] CHECK CONSTRAINT [FK_FeaturedRoutes_UpdatedByUser]
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD  CONSTRAINT [FK_Feedback_FeedbackTypeID] FOREIGN KEY([FeedbackTypeID])
REFERENCES [dbo].[FeedbackTypes] ([FeedbackTypeID])
GO
ALTER TABLE [dbo].[Feedback] CHECK CONSTRAINT [FK_Feedback_FeedbackTypeID]
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD  CONSTRAINT [FK_Feedback_RespondedByUserID] FOREIGN KEY([RespondedByUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Feedback] CHECK CONSTRAINT [FK_Feedback_RespondedByUserID]
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD  CONSTRAINT [FK_Feedback_UserID] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Feedback] CHECK CONSTRAINT [FK_Feedback_UserID]
GO
ALTER TABLE [dbo].[Messages]  WITH CHECK ADD FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Passengers]  WITH CHECK ADD  CONSTRAINT [FK_Passengers_PassengerTypeID] FOREIGN KEY([PassengerTypeID])
REFERENCES [dbo].[PassengerTypes] ([PassengerTypeID])
GO
ALTER TABLE [dbo].[Passengers] CHECK CONSTRAINT [FK_Passengers_PassengerTypeID]
GO
ALTER TABLE [dbo].[Passengers]  WITH CHECK ADD  CONSTRAINT [FK_Passengers_UserID] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Passengers] CHECK CONSTRAINT [FK_Passengers_UserID]
GO
ALTER TABLE [dbo].[PaymentTransactions]  WITH CHECK ADD  CONSTRAINT [FK_PaymentTransactions_BookingID] FOREIGN KEY([BookingID])
REFERENCES [dbo].[Bookings] ([BookingID])
GO
ALTER TABLE [dbo].[PaymentTransactions] CHECK CONSTRAINT [FK_PaymentTransactions_BookingID]
GO
ALTER TABLE [dbo].[PricingRules]  WITH CHECK ADD  CONSTRAINT [FK_SR_PricingRules_Route] FOREIGN KEY([RouteID])
REFERENCES [dbo].[Routes] ([RouteID])
GO
ALTER TABLE [dbo].[PricingRules] CHECK CONSTRAINT [FK_SR_PricingRules_Route]
GO
ALTER TABLE [dbo].[PricingRules]  WITH CHECK ADD  CONSTRAINT [FK_SR_PricingRules_TrainType] FOREIGN KEY([TrainTypeID])
REFERENCES [dbo].[TrainTypes] ([TrainTypeID])
GO
ALTER TABLE [dbo].[PricingRules] CHECK CONSTRAINT [FK_SR_PricingRules_TrainType]
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD  CONSTRAINT [FK_Refunds_AppliedPolicyID] FOREIGN KEY([AppliedPolicyID])
REFERENCES [dbo].[CancellationPolicies] ([PolicyID])
GO
ALTER TABLE [dbo].[Refunds] CHECK CONSTRAINT [FK_Refunds_AppliedPolicyID]
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD  CONSTRAINT [FK_Refunds_BookingID] FOREIGN KEY([BookingID])
REFERENCES [dbo].[Bookings] ([BookingID])
GO
ALTER TABLE [dbo].[Refunds] CHECK CONSTRAINT [FK_Refunds_BookingID]
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD  CONSTRAINT [FK_Refunds_ProcessedByUserID] FOREIGN KEY([ProcessedByUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Refunds] CHECK CONSTRAINT [FK_Refunds_ProcessedByUserID]
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD  CONSTRAINT [FK_Refunds_RequestedByUserID] FOREIGN KEY([RequestedByUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[Refunds] CHECK CONSTRAINT [FK_Refunds_RequestedByUserID]
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD  CONSTRAINT [FK_Refunds_TicketID] FOREIGN KEY([TicketID])
REFERENCES [dbo].[Tickets] ([TicketID])
GO
ALTER TABLE [dbo].[Refunds] CHECK CONSTRAINT [FK_Refunds_TicketID]
GO
ALTER TABLE [dbo].[RouteStations]  WITH CHECK ADD  CONSTRAINT [FK_RouteStations_RouteID] FOREIGN KEY([RouteID])
REFERENCES [dbo].[Routes] ([RouteID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[RouteStations] CHECK CONSTRAINT [FK_RouteStations_RouteID]
GO
ALTER TABLE [dbo].[RouteStations]  WITH CHECK ADD  CONSTRAINT [FK_RouteStations_StationID] FOREIGN KEY([StationID])
REFERENCES [dbo].[Stations] ([StationID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[RouteStations] CHECK CONSTRAINT [FK_RouteStations_StationID]
GO
ALTER TABLE [dbo].[Seats]  WITH CHECK ADD  CONSTRAINT [FK_Seats_CoachID] FOREIGN KEY([CoachID])
REFERENCES [dbo].[Coaches] ([CoachID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Seats] CHECK CONSTRAINT [FK_Seats_CoachID]
GO
ALTER TABLE [dbo].[Seats]  WITH CHECK ADD  CONSTRAINT [FK_Seats_SeatTypeID] FOREIGN KEY([SeatTypeID])
REFERENCES [dbo].[SeatTypes] ([SeatTypeID])
GO
ALTER TABLE [dbo].[Seats] CHECK CONSTRAINT [FK_Seats_SeatTypeID]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_Coachh] FOREIGN KEY([CoachID])
REFERENCES [dbo].[Coaches] ([CoachID])
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_Coachh]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_EndStation] FOREIGN KEY([legDestinationStationId])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_EndStation]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_Seat] FOREIGN KEY([SeatID])
REFERENCES [dbo].[Seats] ([SeatID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_Seat]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_StartStation] FOREIGN KEY([legOriginStationId])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_StartStation]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_Trip] FOREIGN KEY([TripID])
REFERENCES [dbo].[Trips] ([TripID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_Trip]
GO
ALTER TABLE [dbo].[TemporarySeatHolds]  WITH CHECK ADD  CONSTRAINT [FK_TemporarySeatHolds_User] FOREIGN KEY([UserID])
REFERENCES [dbo].[Users] ([UserID])
ON DELETE SET NULL
GO
ALTER TABLE [dbo].[TemporarySeatHolds] CHECK CONSTRAINT [FK_TemporarySeatHolds_User]
GO
ALTER TABLE [dbo].[TempRefundRequests]  WITH CHECK ADD  CONSTRAINT [FK_TempRefundRequests_TicketID] FOREIGN KEY([TicketID])
REFERENCES [dbo].[Tickets] ([TicketID])
GO
ALTER TABLE [dbo].[TempRefundRequests] CHECK CONSTRAINT [FK_TempRefundRequests_TicketID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_BookingID] FOREIGN KEY([BookingID])
REFERENCES [dbo].[Bookings] ([BookingID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_BookingID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_EndStationID] FOREIGN KEY([EndStationID])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_EndStationID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_ParentTicketID] FOREIGN KEY([ParentTicketID])
REFERENCES [dbo].[Tickets] ([TicketID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_ParentTicketID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_PassengerID] FOREIGN KEY([PassengerID])
REFERENCES [dbo].[Passengers] ([PassengerID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_PassengerID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_SeatID] FOREIGN KEY([SeatID])
REFERENCES [dbo].[Seats] ([SeatID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_SeatID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_StartStationID] FOREIGN KEY([StartStationID])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_StartStationID]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [FK_Tickets_TripID] FOREIGN KEY([TripID])
REFERENCES [dbo].[Trips] ([TripID])
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [FK_Tickets_TripID]
GO
ALTER TABLE [dbo].[TicketStatusHistory]  WITH CHECK ADD  CONSTRAINT [FK_TicketStatusHistory_ChangedByUserID] FOREIGN KEY([ChangedByUserID])
REFERENCES [dbo].[Users] ([UserID])
GO
ALTER TABLE [dbo].[TicketStatusHistory] CHECK CONSTRAINT [FK_TicketStatusHistory_ChangedByUserID]
GO
ALTER TABLE [dbo].[TicketStatusHistory]  WITH CHECK ADD  CONSTRAINT [FK_TicketStatusHistory_TicketID] FOREIGN KEY([TicketID])
REFERENCES [dbo].[Tickets] ([TicketID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TicketStatusHistory] CHECK CONSTRAINT [FK_TicketStatusHistory_TicketID]
GO
ALTER TABLE [dbo].[Trains]  WITH CHECK ADD  CONSTRAINT [FK_Trains_TrainTypeID] FOREIGN KEY([TrainTypeID])
REFERENCES [dbo].[TrainTypes] ([TrainTypeID])
GO
ALTER TABLE [dbo].[Trains] CHECK CONSTRAINT [FK_Trains_TrainTypeID]
GO
ALTER TABLE [dbo].[Trips]  WITH CHECK ADD  CONSTRAINT [FK_Trips_RouteID] FOREIGN KEY([RouteID])
REFERENCES [dbo].[Routes] ([RouteID])
GO
ALTER TABLE [dbo].[Trips] CHECK CONSTRAINT [FK_Trips_RouteID]
GO
ALTER TABLE [dbo].[Trips]  WITH CHECK ADD  CONSTRAINT [FK_Trips_TrainID] FOREIGN KEY([TrainID])
REFERENCES [dbo].[Trains] ([TrainID])
GO
ALTER TABLE [dbo].[Trips] CHECK CONSTRAINT [FK_Trips_TrainID]
GO
ALTER TABLE [dbo].[TripStations]  WITH CHECK ADD  CONSTRAINT [FK_TripStations_StationID] FOREIGN KEY([StationID])
REFERENCES [dbo].[Stations] ([StationID])
GO
ALTER TABLE [dbo].[TripStations] CHECK CONSTRAINT [FK_TripStations_StationID]
GO
ALTER TABLE [dbo].[TripStations]  WITH CHECK ADD  CONSTRAINT [FK_TripStations_TripID] FOREIGN KEY([TripID])
REFERENCES [dbo].[Trips] ([TripID])
ON DELETE CASCADE
GO
ALTER TABLE [dbo].[TripStations] CHECK CONSTRAINT [FK_TripStations_TripID]
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD CHECK  (([BookingStatus]='Completed' OR [BookingStatus]='Cancelled' OR [BookingStatus]='Confirmed' OR [BookingStatus]='Pending'))
GO
ALTER TABLE [dbo].[Bookings]  WITH CHECK ADD CHECK  (([PaymentStatus]='Refunded' OR [PaymentStatus]='Paid' OR [PaymentStatus]='Unpaid'))
GO
ALTER TABLE [dbo].[FeaturedRoutes]  WITH CHECK ADD  CONSTRAINT [CK_FeaturedRoutes_DifferentStations] CHECK  (([OriginStationID]<>[DestinationStationID]))
GO
ALTER TABLE [dbo].[FeaturedRoutes] CHECK CONSTRAINT [CK_FeaturedRoutes_DifferentStations]
GO
ALTER TABLE [dbo].[Feedback]  WITH CHECK ADD CHECK  (([Status]='Closed' OR [Status]='Resolved' OR [Status]='Reviewed' OR [Status]='Pending'))
GO
ALTER TABLE [dbo].[HolidayPrices]  WITH CHECK ADD CHECK  (([DiscountPercentage]>=(0) AND [DiscountPercentage]<=(100)))
GO
ALTER TABLE [dbo].[Messages]  WITH CHECK ADD CHECK  (([SenderType]='Staff' OR [SenderType]='Customer'))
GO
ALTER TABLE [dbo].[PaymentTransactions]  WITH CHECK ADD CHECK  (([Status]='Cancelled' OR [Status]='Failed' OR [Status]='Success' OR [Status]='Pending'))
GO
ALTER TABLE [dbo].[Refunds]  WITH CHECK ADD CHECK  (([Status]='Failed' OR [Status]='Processed' OR [Status]='Rejected' OR [Status]='Approved' OR [Status]='Pending'))
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [CK_Tickets_DifferentStations] CHECK  (([StartStationID]<>[EndStationID]))
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [CK_Tickets_DifferentStations]
GO
ALTER TABLE [dbo].[Tickets]  WITH CHECK ADD  CONSTRAINT [CK_Ve_TicketStatus] CHECK  (([TicketStatus]='Expired' OR [TicketStatus]='RejectedRefund' OR [TicketStatus]='Refunded' OR [TicketStatus]='Processing' OR [TicketStatus]='Used' OR [TicketStatus]='Valid'))
GO
ALTER TABLE [dbo].[Tickets] CHECK CONSTRAINT [CK_Ve_TicketStatus]
GO
ALTER TABLE [dbo].[TicketStatusHistory]  WITH CHECK ADD CHECK  (([NewStatus]='Expired' OR [NewStatus]='Cancelled' OR [NewStatus]='Used' OR [NewStatus]='Valid'))
GO
ALTER TABLE [dbo].[TicketStatusHistory]  WITH CHECK ADD CHECK  (([OldStatus] IS NULL OR ([OldStatus]='Expired' OR [OldStatus]='Cancelled' OR [OldStatus]='Used' OR [OldStatus]='Valid')))
GO
ALTER TABLE [dbo].[Trips]  WITH CHECK ADD CHECK  (([TripStatus]='Cancelled' OR [TripStatus]='Completed' OR [TripStatus]='In Progress' OR [TripStatus]='Scheduled'))
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD CHECK  (([Role]='Admin' OR [Role]='Staff' OR [Role]='Customer' OR [Role]='Manager' OR [Role]='GUEST'))
GO
ALTER TABLE [dbo].[Users]  WITH CHECK ADD  CONSTRAINT [CK_Users_Gender] CHECK  (([Gender]='Other' OR [Gender]='Female' OR [Gender]='Male' OR [Gender] IS NULL))
GO
ALTER TABLE [dbo].[Users] CHECK CONSTRAINT [CK_Users_Gender]
GO
/****** Object:  StoredProcedure [dbo].[CheckSingleSeatAvailability]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[CheckSingleSeatAvailability]
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
/****** Object:  StoredProcedure [dbo].[GetCoachSeatsWithAvailability]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[GetCoachSeatsWithAvailability]
    @TripID_Input INT,
    @CoachID_Input INT,
    @LegOriginStationID_Input INT,
    @LegDestinationStationID_Input INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Input validations (as in the previous complete version - crucial)
    IF @TripID_Input IS NULL OR @CoachID_Input IS NULL OR @LegOriginStationID_Input IS NULL OR @LegDestinationStationID_Input IS NULL BEGIN RAISERROR('All input parameters must be provided.', 16, 1); RETURN; END
    DECLARE @LegOriginSeq INT, @LegDestSeq INT;
    SELECT @LegOriginSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegOriginStationID_Input;
    SELECT @LegDestSeq = TS.SequenceNumber FROM dbo.TripStations TS WHERE TS.TripID = @TripID_Input AND TS.StationID = @LegDestinationStationID_Input;
    IF @LegOriginSeq IS NULL BEGIN RAISERROR('Origin station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegOriginStationID_Input, @TripID_Input); RETURN; END
    IF @LegDestSeq IS NULL BEGIN RAISERROR('Destination station (ID: %d) not found on Trip (ID: %d).', 16, 1, @LegDestinationStationID_Input, @TripID_Input); RETURN; END
    IF @LegOriginSeq >= @LegDestSeq BEGIN RAISERROR('Invalid leg: Origin sequence (%d) must be < Destination sequence (%d).', 16, 1, @LegOriginSeq, @LegDestSeq); RETURN; END
    IF NOT EXISTS (SELECT 1 FROM dbo.Coaches CO INNER JOIN dbo.Trips TRP ON CO.TrainID = TRP.TrainID WHERE CO.CoachID = @CoachID_Input AND TRP.TripID = @TripID_Input) BEGIN RAISERROR('Coach (ID: %d) does not belong to train on Trip (ID: %d).', 16, 1, @CoachID_Input, @TripID_Input); RETURN; END

    SELECT
        S.SeatID,
        S.SeatName,
        S.SeatNumber AS SeatNumberInCoach,
        S.SeatTypeID,
        ST.TypeName AS SeatTypeName,
        ST.BerthLevel,
        ST.PriceMultiplier AS SeatPriceMultiplier,
        CT.PriceMultiplier AS CoachPriceMultiplier, -- Still need this for price calculation
        TRP.BasePriceMultiplier AS TripBasePriceMultiplier,
        S.IsEnabled,
        CASE -- AvailabilityStatus logic
            WHEN S.IsEnabled = 0 THEN 'Disabled'
            WHEN EXISTS (
                SELECT 1
                FROM dbo.Tickets TKT
                INNER JOIN dbo.TripStations TKT_Start_TS ON TKT.TripID = TKT_Start_TS.TripID AND TKT.StartStationID = TKT_Start_TS.StationID
                INNER JOIN dbo.TripStations TKT_End_TS ON TKT.TripID = TKT_End_TS.TripID AND TKT.EndStationID = TKT_End_TS.StationID
                WHERE TKT.SeatID = S.SeatID AND TKT.TripID = @TripID_Input
                  AND TKT.TicketStatus IN ('Valid', 'Confirmed')
                  AND (TKT_Start_TS.SequenceNumber < @LegDestSeq AND TKT_End_TS.SequenceNumber > @LegOriginSeq)-- 3 < 4 and 
            ) THEN 'Occupied'
            ELSE 'Available'
        END AS AvailabilityStatus
    FROM
        dbo.Seats S
    INNER JOIN
        dbo.Coaches CO ON S.CoachID = CO.CoachID        -- Used to link to CoachTypes
    INNER JOIN
        dbo.SeatTypes ST ON S.SeatTypeID = ST.SeatTypeID
    INNER JOIN
        dbo.CoachTypes CT ON CO.CoachTypeID = CT.CoachTypeID -- Still needed for CoachPriceMultiplier
    INNER JOIN
        dbo.Trips TRP ON TRP.TripID = @TripID_Input     -- For TripBasePriceMultiplier
    WHERE
        S.CoachID = @CoachID_Input
    ORDER BY
        S.SeatNumber;

END
GO
/****** Object:  StoredProcedure [dbo].[GetCoachSeatsWithAvailabilityAndPrice]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[GetCoachSeatsWithAvailabilityAndPrice]
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
                  AND TSH_User.legOriginStationId = @LegOriginStationID_Input -- Exact leg match
                  AND TSH_User.legDestinationStationId = @LegDestinationStationID_Input   -- Exact leg match
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
                INNER JOIN dbo.TripStations TSH_Other_Start_TS ON TSH_Other.TripID = TSH_Other_Start_TS.TripID AND TSH_Other.legOriginStationId = TSH_Other_Start_TS.StationID
                INNER JOIN dbo.TripStations TSH_Other_End_TS ON TSH_Other.TripID = TSH_Other_End_TS.TripID AND TSH_Other.legDestinationStationId = TSH_Other_End_TS.StationID
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
/****** Object:  StoredProcedure [dbo].[SearchTrips]    Script Date: 7/23/2025 12:54:04 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE [dbo].[SearchTrips] -- Use CREATE OR ALTER for easier updates
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
USE [master]
GO
ALTER DATABASE [TrainTicketSystemDB_V2] SET  READ_WRITE 
GO
