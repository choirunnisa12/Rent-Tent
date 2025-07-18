# Camping Rental Management System

A Spring Boot application for camping equipment rental operations, built with JWT, pagination, sorting, validation, testing with JUnit, Swagger, Lombok, and MySQL.

## Table of Contents

- Features
- Tech Stack
- Prerequisites
- Getting Started
- API Endpoints
- Project Structure
- Testing

## Features

- Authentication: JWT-based login system with secure token management
- User Management: Full CRUD operations for system users
- Admin Management: Complete admin data handling
- Product Management: Camping equipment inventory and rental tracking
- Transaction Management: Rental transactions and payment tracking
- Wallet System: User wallet and balance management
- Weather Integration: Weather data for camping planning
- API Documentation: Swagger UI for easy API exploration
- Testing: Unit and integration tests for reliability
- Security: Rate limiting, security headers, and input validation
- CI/CD: Automated testing and deployment pipeline
- Monitoring: Health checks and application metrics

## Tech Stack

- Java 17 - Using LTS version for stability
- Spring Boot 3.3.0 - For rapid development and microservices
- Spring Security - Handles authentication and authorization
- Spring Data JPA - Database operations and entity management
- MySQL - Reliable database for rental data
- JWT - Stateless authentication tokens
- Lombok - Reduces boilerplate code
- Maven - Build and dependency management
- Swagger/OpenAPI - API documentation
- Docker - Containerization for easy deployment

## Prerequisites

You'll need these installed on your machine:

- Java 17 or newer
- Maven 3.6+
- MySQL 8.0+
- Git
- Docker (optional)

## Getting Started

1. Clone and Setup
```bash
git clone https://github.com/choirunnisa12/Rent-Tent.git
cd Rent-Tent
```

2. Database Setup
First, create the database:
```sql
CREATE DATABASE camping;
```

Then update the database config in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.url=jdbc:mysql://localhost:3306/camping
```

3. Build and Run
```bash
# Build the project
mvn clean install

# Run in development mode
mvn spring-boot:run
```

The app will be running at http://localhost:8004

## API Endpoints

Once running, check out the API docs at:
- Swagger UI: http://localhost:8004/swagger-ui.html

### Example: Register

**Request (Valid):**
```json
POST /api/v1/auth/register/user
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Request (Invalid):**
```json
POST /api/v1/auth/register/user
{
  "name": "Jo",
  "email": "invalid-email",
  "password": "123"
}
```

**Response (Validation Error):**
```json
{
  "timestamp": "2024-05-01T12:34:56.789+00:00",
  "status": 400,
  "errors": [
    "Name must be between 3 and 50 characters",
    "Email should be valid",
    "Password must be at least 6 characters"
  ],
  "path": "/api/v1/auth/register/user"
}
```

### Example: Login

**Request (Valid):**
```json
POST /api/v1/auth/login/user
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "message": "User logged in successfully",
  "timestamp": "2024-05-01T12:35:10.123+00:00"
}
```

## Main Endpoints

### Authentication:
- POST /api/v1/auth/register/user - Create new user account
- POST /api/v1/auth/register/admin - Create new admin account
- POST /api/v1/auth/login/user - User login and get JWT token
- POST /api/v1/auth/login/admin - Admin login and get JWT token

### Users:
- GET /api/v1/users - List all users (Admin only)
- GET /api/v1/users/{id} - Get specific user
- PUT /api/v1/users/{id} - Update user
- DELETE /api/v1/users/{id} - Remove user

### Products:
- GET /api/v1/products - List all camping products
- GET /api/v1/products/{id} - Get product details
- POST /api/v1/products - Add new product (Admin only)
- PUT /api/v1/products/{id} - Update product (Admin only)
- DELETE /api/v1/products/{id} - Remove product (Admin only)

### Transactions:
- GET /api/v1/transactions - List all transactions
- GET /api/v1/transactions/{id} - Get transaction details
- POST /api/v1/transactions - Create new rental transaction
- PUT /api/v1/transactions/{id} - Update transaction
- DELETE /api/v1/transactions/{id} - Remove transaction

### Wallet:
- GET /api/v1/wallets - List all wallets
- GET /api/v1/wallets/{id} - Get wallet details
- POST /api/v1/wallets - Create new wallet
- PUT /api/v1/wallets/{id} - Update wallet balance
- DELETE /api/v1/wallets/{id} - Remove wallet

### Weather:
- GET /api/v1/weather - Get current weather data
- GET /api/v1/weather/forecast - Get weather forecast

## Project Structure

```
src/main/java/com/code/camping
├── CampingApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── TransactionController.java
│   ├── UserController.java
│   ├── WalletController.java
│   └── WeatherController.java
├── entity/
│   ├── Admin.java
│   ├── Product.java
│   ├── Transaction.java
│   ├── User.java
│   ├── Wallet.java
│   └── Weather.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── repository/
│   ├── AdminRepository.java
│   ├── ProductRepository.java
│   ├── TransactionRepository.java
│   ├── UserRepository.java
│   └── WalletRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtils.java
├── service/
│   ├── AdminService.java
│   ├── ProductService.java
│   ├── TransactionService.java
│   ├── UserService.java
│   ├── WalletService.java
│   ├── WeatherService.java
│   └── impl/
│       ├── AdminServiceImpl.java
│       ├── ProductServiceImpl.java
│       ├── TransactionServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── WalletServiceImpl.java
│       └── WeatherServiceImpl.java
└── utils/
    ├── dto/
    │   ├── request/
    │   │   ├── LoginAdminRequest.java
    │   │   ├── LoginUserRequest.java
    │   │   ├── ProductRequest.java
    │   │   ├── RegisterAdminRequest.java
    │   │   ├── RegisterUserRequest.java
    │   │   ├── TransactionRequest.java
    │   │   └── WalletRequest.java
    │   ├── response/
    │   │   ├── AdminResponse.java
    │   │   ├── LoginAdminResponse.java
    │   │   ├── LoginUserResponse.java
    │   │   ├── ProductResponse.java
    │   │   ├── TransactionResponse.java
    │   │   ├── UserResponse.java
    │   │   └── WalletResponse.java
    │   └── webResponse/
    │       ├── PageResponse.java
    │       ├── Res.java
    │       └── WebResponse.java
    ├── DateTimeFormatUtil.java
    └── GeneralSpecification.java
```

## Testing

Run the tests with:
```bash
mvn test
```

The project includes:
- Unit tests for service layer
- Integration tests for API endpoints
- Test coverage reporting with JaCoCo
- Automated testing in CI/CD pipeline

## Contributing

Feel free to contribute! Here's how:
1. Fork the repo
2. Create a feature branch
3. Make your changes
4. Add tests if needed
5. Submit a pull request
