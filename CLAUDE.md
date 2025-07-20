# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based train booking system built with Jakarta EE, featuring role-based access control, real-time seat management, and VNPay payment integration. The system serves different user roles (Admin, Manager, Staff, Customer) with dedicated interfaces and functionality.

## Development Commands

### Building and Running
```bash
# Navigate to the main project directory
cd train-booking-system

# Clean and compile the project
mvn clean compile

# Run tests
mvn test

# Package the application as WAR
mvn clean package

# Run with embedded Tomcat (development)
mvn tomcat7:run

# Deploy to external Tomcat
# Copy target/train-booking-system.war to Tomcat webapps directory
```

### Database Setup
- Database: SQL Server (TrainTicketSystemDB_V2)
- Connection config: `src/main/resources/db.properties`
- Default URL: `jdbc:sqlserver://localhost:1433;databaseName=TrainTicketSystemDB_V2`
- Default credentials: sa/sa123456
- SQL scripts available in: `database_scripts/`

## Architecture Overview

### Technology Stack
- **Backend**: Java 17, Jakarta EE (Servlets 6.0, JSP 4.0)
- **Database**: SQL Server with JDBC
- **Build**: Maven
- **Server**: Tomcat (embedded via Maven plugin)
- **Frontend**: JSP/JSTL, vanilla JavaScript, CSS
- **Payment**: VNPay integration

### Project Structure
```
train-booking-system/
├── src/main/java/vn/vnrailway/
│   ├── config/          # Database configuration
│   ├── controller/      # Servlets organized by role and function
│   │   ├── admin/       # Admin management servlets
│   │   ├── api/         # REST-like API endpoints
│   │   ├── manager/     # Manager operations
│   │   ├── staff/       # Staff operations
│   │   └── customer/    # Customer operations
│   ├── dao/             # Data Access Objects and Repositories
│   ├── dto/             # Data Transfer Objects
│   ├── model/           # Database entity models
│   ├── service/         # Business logic layer
│   └── utils/           # Utility classes
├── src/main/webapp/     # Web resources
│   ├── WEB-INF/jsp/     # JSP templates organized by role
│   ├── css/             # Stylesheets
│   ├── js/              # JavaScript files
│   └── assets/          # Images and static resources
└── database_scripts/    # SQL scripts and stored procedures
```

### Key Architectural Patterns

#### Data Access Layer
- **Repository Pattern**: Interface + Implementation (e.g., `UserRepository` → `UserRepositoryImpl`)
- **DAO Pattern**: Traditional data access objects for complex operations
- **Database Context**: `DBContext` manages connections using JDBC
- All database operations use PreparedStatements to prevent SQL injection

#### Controller Layer
- **Role-based Servlets**: Organized by user role (admin, manager, staff, customer)
- **API Endpoints**: `/api/*` for AJAX and external integrations
- **Security Filter**: `RoleFilter` enforces access control (currently commented out)
- **Session Management**: 60-minute timeout, user stored as "loggedInUser"

#### Business Logic
- **Service Layer**: Minimal but strategic (e.g., `TripService` for complex search logic)
- **Stored Procedures**: Complex operations like trip search and pricing
- **Real-time Features**: Temporary seat holds with session-based management

### Key Business Entities and Relationships

**Core Models:**
- `User` (role-based: Admin, Manager, Staff, Customer)
- `Trip` (scheduled train journeys)
- `Booking` (customer reservations)
- `Ticket` (individual seat assignments)
- `Train` → `Coach` → `Seat` (physical infrastructure)
- `Route` → `RouteStation` (geographic routing)

**Critical Relationships:**
- User → Booking → Ticket (customer booking flow)
- Trip + Seat → Ticket (seat availability management)
- Route + Train → Trip (scheduled services)

### Payment Integration
- **VNPay Gateway**: Vietnamese payment processor
- **Transaction Tracking**: Full audit trail in `PaymentTransaction`
- **Return Handler**: `VNPayReturnServlet` processes payment callbacks
- **Status Management**: Multiple booking states (Pending, Confirmed, Cancelled)

## Development Guidelines

### Database Operations
- Use repository interfaces for new data access methods
- Follow the existing pattern: create interface first, then implementation
- Leverage stored procedures for complex queries (see existing examples in `database_scripts/`)
- Always use PreparedStatements to prevent SQL injection

### Controller Development
- Follow role-based organization: place servlets in appropriate controller subdirectories
- Use `@WebServlet` annotation for URL mapping
- Store user session data as "loggedInUser" attribute
- Return JSON responses for API endpoints using `JsonUtils`

### Security Considerations
- Database credentials are in `db.properties` - ensure this is not committed with real credentials
- Role-based access control is implemented but filter is currently disabled
- Session-based authentication with 60-minute timeout
- All database operations use parameterized queries

### Testing
- Tests should be placed in `src/test/java`
- Current setup uses JUnit 4.11
- Run tests with: `mvn test`

### CSS and JavaScript
- Role-specific CSS files in `css/` directory
- JavaScript organized by functionality in `js/` directory
- Static resources served via default servlet mapping in `web.xml`

## Common Development Tasks

### Adding New User Roles
1. Update `User` model with new role enum
2. Create controller subdirectory for role-specific servlets
3. Add JSP templates in `WEB-INF/jsp/[role]/`
4. Update `RoleFilter` access control logic

### Adding New API Endpoints
1. Create servlet in `controller/api/` subdirectory
2. Use `@WebServlet` with `/api/*` URL pattern
3. Return JSON responses using `JsonUtils.toJson()`
4. Follow existing patterns in `booking/`, `payment/`, `seat/`, `trip/` APIs

### Database Schema Changes
1. Update SQL scripts in `database_scripts/`
2. Modify corresponding model classes in `model/` package
3. Update repository interfaces and implementations
4. Test with existing stored procedures

### Adding New Business Logic
1. Create service interface in `service/` package
2. Implement in `service/impl/` package
3. Inject repositories as constructor dependencies
4. Follow transaction patterns established in `TripService`