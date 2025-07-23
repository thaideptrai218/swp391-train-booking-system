-- Add VIP Member as a passenger type
-- This allows users to select "VIP Member" which will trigger VIP credential validation

INSERT INTO PassengerTypes (TypeName, DiscountPercentage, Description, RequiresDocument)
VALUES ('Thành viên VIP', 0.00, 'Thành viên VIP - yêu cầu xác thực thẻ VIP', 1);

-- Note: Base discount is 0% because actual VIP discount will be applied after credential validation
-- RequiresDocument = 1 because VIP members must provide ID for validation