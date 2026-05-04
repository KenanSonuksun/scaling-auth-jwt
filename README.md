![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green)
![Docker](https://img.shields.io/badge/scaling-auth-blue)


# Scaling Auth - JWT

A production-oriented Spring Boot focused on JWT-based authentication under horizontal scaling.

This project demonstrates how stateless authentication behaves when the same backend application runs across multiple instances behind a load balancer.

## What this project covers

### JWT-Based Authentication
Implemented with:
- JWT access token generation
- stateless Spring Security configuration
- bearer token authentication
- protected API endpoints
- role-based authorization
- admin-only API access

### Horizontal Scaling Behavior
The project shows how JWT authentication behaves across multiple backend instances.

Covered scenarios:
- single instance JWT flow
- multi-instance JWT flow
- Nginx load balancer routing
- token validation across different backend instances

### Stateless Security
Unlike session-based authentication, this project does not rely on:
- server-side HTTP session
- sticky session
- shared session store

Each request carries its own authentication information through the bearer token.

## Key architecture ideas

### 1. Stateless Authentication
Authentication state is not stored in backend instance memory.
The token travels with the request and can be validated by any instance.

### 2. Multi-Instance Compatibility
A token issued by one backend instance can be validated by another instance, as long as both use the same signing secret.

This is the core reason JWT is considered scale-friendly.

### 3. Load Balancer Awareness
Requests are routed through Nginx so authentication behavior can be observed in a real multi-instance setup.

### 4. Role-Based Authorization
The project includes:
- authenticated user endpoint
- admin-only endpoint
- role-aware access control through Spring Security

## Main technologies
- Java
- Spring Boot
- Spring Security
- PostgreSQL
- Flyway
- JWT
- Nginx
- Docker

## How to run

### 1. Start PostgreSQL
docker rm -f scaling-auth-jwt-postgres
docker run --name scaling-auth-jwt-postgres \
  -e POSTGRES_DB=scaling_auth_jwt \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16

### 2. Run single instance
DB_HOST=localhost \
DB_PORT=5432 \
DB_NAME=scaling_auth_jwt \
DB_USERNAME=postgres \
DB_PASSWORD=postgres \
mvn spring-boot:run

Default app port:
8080

### 3. Run multi-instance setup
Open two terminals.

Instance 1:
SERVER_PORT=8081 \
APP_INSTANCE_ID=instance-1 \
DB_HOST=localhost \
DB_PORT=5432 \
DB_NAME=scaling_auth_jwt \
DB_USERNAME=postgres \
DB_PASSWORD=postgres \
mvn spring-boot:run

Instance 2:
SERVER_PORT=8082 \
APP_INSTANCE_ID=instance-2 \
DB_HOST=localhost \
DB_PORT=5432 \
DB_NAME=scaling_auth_jwt \
DB_USERNAME=postgres \
DB_PASSWORD=postgres \
mvn spring-boot:run

### 4. Configure Nginx
Create this file:
infra/nginx/nginx.conf

Content:
events {}

http {
    upstream scaling_auth_jwt_backend {
        server host.docker.internal:8081;
        server host.docker.internal:8082;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://scaling_auth_jwt_backend;
            proxy_set_header Host $http_host;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Real-IP $remote_addr;
        }
    }
}

### 5. Start Nginx load balancer
Run this command from the project root:
docker rm -f scaling-auth-jwt-nginx
docker run --name scaling-auth-jwt-nginx \
  -p 8090:80 \
  -v "$(pwd)/infra/nginx/nginx.conf:/etc/nginx/nginx.conf:ro" \
  -d nginx:latest

Load balancer entry point:
http://localhost:8090

## Test cases

### 1. Register user
POST /api/users/register
Content-Type: application/json

{
  "username": "user",
  "email": "user@example.com",
  "password": "Password123"
}

### 2. Public ping
GET /api/public/ping

Expected:
- instance id in response
- no authentication required

### 3. Login as normal user
POST /api/auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "Password123"
}

Expected:
- accessToken
- tokenType
- expiresAt
- issuedByInstance

### 4. Call authenticated endpoint
GET /api/auth/me
Authorization: Bearer <ACCESS_TOKEN>

Expected:
- authenticated user info
- instance id of the backend that handled the request

### 5. Admin seed login
Admin user is created automatically at startup.

Credentials:
- username: admin
- password: Admin123

Login request:
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "Admin123"
}

### 6. Call admin-only endpoint
GET /api/admin/ping
Authorization: Bearer <ADMIN_ACCESS_TOKEN>

Expected:
- success for admin
- authenticated response
- instance id

### 7. Verify user cannot access admin endpoint
GET /api/admin/ping
Authorization: Bearer <USER_ACCESS_TOKEN>

Expected:
- 403 Forbidden

### 8. Multi-instance JWT validation test
Use Nginx:
http://localhost:8090

Login through load balancer:
POST http://localhost:8090/api/auth/login

Then call:
GET http://localhost:8090/api/auth/me
Authorization: Bearer <ACCESS_TOKEN>

Repeat multiple times.

Expected:
- request may be served by different instances
- token should still be accepted
- authentication should remain valid

This proves that the token can be validated across multiple backend instances without shared session state.
