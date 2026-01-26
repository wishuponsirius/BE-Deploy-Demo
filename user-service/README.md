# User Service

User Service for Investment System - handles user authentication and authorization.

## Features
- User registration
- User login with JWT token
- Token validation
- Password encryption with BCrypt
- Integration with Eureka Discovery Server
- RESTful API endpoints

## Technology Stack
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (JSON Web Token)
- MySQL
- Eureka Client

## API Endpoints

### Authentication Endpoints

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
    "orgName": "Organization Name",
    "email": "user@example.com",
    "password": "password123"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}

Response:
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "type": "Bearer",
        "id": 1,
        "email": "user@example.com",
        "orgName": "Organization Name",
        "role": "USER"
    }
}
```

#### Validate Token
```
GET /api/auth/validate
Authorization: Bearer {token}
```

### User Endpoints

#### Get Current User
```
GET /api/users/me
Authorization: Bearer {token}
```

## Configuration

Update `application.yml` with your database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/investment_system
    username: your_username
    password: your_password
```

## Running the Service

1. Start Discovery Server (Eureka) on port 8761
2. Start MySQL database
3. Run the User Service:

```bash
cd user-service
mvn clean install
mvn spring-boot:run
```

The service will start on port 8081 and register with Eureka.

## Security

- Passwords are encrypted using BCrypt
- JWT tokens are used for authentication
- Token expiration: 24 hours
- CORS enabled for all origins (configure as needed)

## Database Schema

The service uses the `institutional_user` table with the following structure:
- id (Primary Key)
- org_name
- contact_email (Unique)
- password (Encrypted)
- is_active
- role
- create_date
- update_date
