# API Gateway

API Gateway cho Microservices architecture với JWT authentication và Swagger documentation.

## Tính năng

- ✅ **JWT Authentication**: Xác thực và phân quyền dựa trên JWT token
- ✅ **Swagger/OpenAPI**: Documentation tự động với Swagger UI
- ✅ **Service Discovery**: Tích hợp với Eureka Server
- ✅ **CORS Configuration**: Hỗ trợ Cross-Origin Resource Sharing
- ✅ **Load Balancing**: Phân tải cho các microservices
- ✅ **Security**: Spring Security với WebFlux
- ✅ **Actuator**: Monitoring và health check endpoints

## Cấu hình

### Dependencies chính

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Security
- JWT (jjwt)
- SpringDoc OpenAPI (Swagger)
- Spring Boot Actuator
- Lombok

### Ports

- API Gateway: `8080`
- Eureka Server: `8761`

### Environment Variables

Tạo file `.env` từ `.env.example`:

```bash
JWT_SECRET=your-secret-key-here
EUREKA_SERVER=http://localhost:8761/eureka/
```

## Sử dụng

### 1. Khởi động Eureka Server

```bash
cd discovery-server/discovery-server
mvn spring-boot:run
```

### 2. Khởi động API Gateway

```bash
cd api-gateway/api-gateway
mvn spring-boot:run
```

### 3. Truy cập Swagger UI

```
http://localhost:8080/swagger-ui.html
```

### 4. API Endpoints

#### Public Endpoints (không cần JWT)

- `GET /api/auth/**` - Authentication endpoints
- `GET /api/gateway/health` - Health check
- `GET /api/gateway/info` - Gateway info
- `GET /actuator/**` - Actuator endpoints

#### Protected Endpoints (cần JWT)

- `GET /api/users/**` - User service routes
- `GET /api/orders/**` - Order service routes

### JWT Authentication

#### Headers

```
Authorization: Bearer <your-jwt-token>
```

#### Example Request

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Cấu trúc Project

```
api-gateway/
├── src/main/java/com/example/microservice/api_gateway/
│   ├── ApiGatewayApplication.java
│   ├── config/
│   │   ├── CorsConfig.java          # CORS configuration
│   │   ├── JwtUtil.java              # JWT utility class
│   │   ├── OpenApiConfig.java        # Swagger configuration
│   │   └── SecurityConfig.java       # Security configuration
│   ├── controller/
│   │   └── GatewayController.java    # Gateway endpoints
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── filter/
│   │   └── AuthenticationFilter.java # JWT authentication filter
│   └── model/
│       └── ErrorResponse.java        # Error response model
└── src/main/resources/
    └── application.yml               # Application configuration
```

## Configuration Files

### application.yml

Cấu hình routes, JWT, Swagger, CORS, logging, và Eureka client.

### Routes Configuration

Routes được cấu hình trong `application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
          filters:
            - AuthenticationFilter
```

## Monitoring

### Actuator Endpoints

- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`
- Prometheus: `http://localhost:8080/actuator/prometheus`

### Eureka Dashboard

```
http://localhost:8761
```

## Logging

Log levels được cấu hình trong `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.example.microservice: DEBUG
    org.springframework.cloud.gateway: DEBUG
```

## Security Notes

- JWT secret key nên được lưu trong environment variables
- Không commit sensitive data vào git
- Sử dụng HTTPS trong production
- JWT token expiration: 24 hours (có thể cấu hình)

## Troubleshooting

### Gateway không kết nối được với Eureka

Kiểm tra Eureka Server đã chạy chưa và URL trong `application.yml` đúng chưa.

### JWT validation failed

- Kiểm tra token có hợp lệ không
- Kiểm tra JWT secret key có đúng không
- Kiểm tra token có hết hạn chưa

### CORS errors

Kiểm tra CORS configuration trong `application.yml` và `CorsConfig.java`.
