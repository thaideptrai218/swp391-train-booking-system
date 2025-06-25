PRINT 'Creating Table: PricingRules (Simpler Still)';
IF OBJECT_ID('dbo.PricingRules', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.PricingRules (
        RuleID INT PRIMARY KEY IDENTITY(1,1),
        RuleName NVARCHAR(150) NOT NULL,
        Description NVARCHAR(500) NULL,

        -- === Core Pricing Definition ===
        BasePricePerKm DECIMAL(10,2) NOT NULL, -- Every rule MUST define this.

        -- === Scope/Applicability Filters (NULL means "applies to all") ===
        TrainTypeID INT NULL,      -- FK to TrainTypes. For specific train types (e.g., Express).
        RouteID INT NULL,          -- FK to Routes. For specific routes.
        IsForRoundTrip BIT NULL,   -- NULL means applies to both, 0 for one-way, 1 for round-trip.

        -- === Time-Based Applicability (for different rates during specific periods) ===
        ApplicableDateStart DATE NULL,       -- Rule's BasePricePerKm applies from this date (inclusive)
        ApplicableDateEnd DATE NULL,         -- Rule's BasePricePerKm applies up to this date (inclusive)

        -- === Control ===
        Priority INT NOT NULL DEFAULT 0,    -- Higher number = higher priority. Crucial for overrides.
        IsActive BIT NOT NULL DEFAULT 1,
        EffectiveFromDate DATE NOT NULL DEFAULT GETDATE(), -- When this rule definition itself becomes active
        EffectiveToDate DATE NULL,                         -- When this rule definition expires

        -- Foreign Keys
        CONSTRAINT FK_SR_PricingRules_TrainType FOREIGN KEY (TrainTypeID) REFERENCES dbo.TrainTypes(TrainTypeID),
        CONSTRAINT FK_SR_PricingRules_Route FOREIGN KEY (RouteID) REFERENCES dbo.Routes(RouteID)
        -- No CoachTypeCategoryID link needed now
    );
    PRINT 'Table PricingRules (Simpler Still) created.';
END