Here’s your updated `README.md` with masked sensitive data and a section on Vault integration:

---

# Craft Photographer Service

## Overview

The **Craft Photographer Service** is a Spring Boot application designed to manage photographers, providing search capabilities based on event types, proximity, and other attributes. It uses Redis for caching, MySQL as the primary database, and supports JWT-based authentication. Secure credentials are managed through HashiCorp Vault.

## Features

- **Photographer Management**: CRUD operations for photographers.
- **Event-based Search**: Filter photographers based on event type.
- **Proximity Search**: Find photographers based on geographical proximity.
- **JWT Authentication**: Secure API access with token-based authentication.
- **Redis Caching**: Improve performance by caching frequently accessed data.
- **Vault Integration**: Secure storage and retrieval of sensitive information.
- **Resilience4J Circuit Breaker**: Handle service failures gracefully.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.3.4**
- **MySQL 8.0**
- **Redis with Lettuce Client**
- **HashiCorp Vault** for secure secrets management.
- **JWT (JSON Web Tokens)** for Authentication
- **Resilience4J** for Circuit Breaking
- **Gradle** as the Build System

---

## Prerequisites

1. **Java 17** installed.
2. **Docker** installed (for Redis, MySQL, and Vault containers).
3. **Gradle** installed (optional if using the wrapper).

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd craft-demo-photographer-service
```

### 2. Set Up Docker Containers

Create a `docker-compose.yml` to launch MySQL, Redis, and Vault services:

```yaml
version: '3.8'

services:
  mysql-db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ****
      MYSQL_DATABASE: photographer_db
      MYSQL_USER: app_user
      MYSQL_PASSWORD: ****
    ports:
      - "3306:3306"
    networks:
      - photographer-service-network

  redis:
    image: redis:latest
    ports:
      - "6379:6379"
    networks:
      - photographer-service-network

  vault:
    image: vault:1.14.2
    environment:
      VAULT_DEV_ROOT_TOKEN_ID: root
      VAULT_DEV_LISTEN_ADDRESS: "0.0.0.0:8200"
    ports:
      - "8200:8200"
    cap_add:
      - IPC_LOCK
    networks:
      - photographer-service-network

networks:
  photographer-service-network:
```

Start the containers:
```bash
docker-compose up -d
```

---

### 3. Configure Vault

Store sensitive data in Vault:
```bash
export VAULT_ADDR='http://localhost:8200'
export VAULT_TOKEN='root'

vault kv put secret/mysql password=****  
vault kv put secret/jwt secret-key=****  
```

---

### 4. Configure `application.properties`

Update the `src/main/resources/application.properties`:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/photographer_db
spring.datasource.username=app_user
spring.datasource.password=${vault.mysql.password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379

jwt.secret-key=${vault.jwt.secret-key}

spring.cloud.vault.uri=http://localhost:8200
spring.cloud.vault.token=root
spring.cloud.vault.kv.enabled=true
spring.cloud.vault.kv.backend=secret

resilience4j.circuitbreaker.instances.proximityService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.proximityService.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.proximityService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.proximityService.waitDurationInOpenState=10000ms
```

---

### 5. Build and Run the Application

Build the project:
```bash
./gradlew clean build
```

Run the application:
```bash
./gradlew bootRun
```

---

## API Endpoints

### Authentication

- **Register User**:  
  `POST /api/auth/register`
  ```json
  {
    "username": "aarin",
    "password": "****",
    "role": "API_USER"
  }
  ```

- **Login**:  
  `POST /api/auth/login`
  ```json
  {
    "username": "aarin",
    "password": "****"
  }
  ```

### Photographers API

- **Get Photographer by ID**:  
  `GET /api/photographers/{id}`

- **Search by Event Type**:  
  `GET /api/photographers/event/{eventType}`

- **Search by Proximity**:  
  `GET /api/photographers/proximity?lat={latitude}&lng={longitude}&radius={radius}`

---

## Exception Handling

Consistent error responses are provided. Example:
```json
{
  "timestamp": "2024-10-24T13:34:16",
  "status": 404,
  "error": "Not Found",
  "message": "The requested endpoint does not exist.",
  "path": "/api/photographers/"
}
```

---

## Caching Strategy

Redis is used to cache expensive operations like proximity search:
```java
@Cacheable(value = "photographersByProximity", key = "{#lat, #lng, #radius}")
public List<PhotographerDTO> getPhotographersByProximity(Long userId, double lat, double lng, double radius) {
    // Implementation
}
```

---

## Circuit Breaker

A Resilience4J circuit breaker is configured for the proximity search service.

---

## Logging

The `RequestResponseLoggingFilter` logs requests and responses along with processing times:
```
INFO  RequestResponseLoggingFilter [uuid=70ffe495, method=GET, uri=/api/photographers/proximity, code=200, sst=12 ms, dbt=27 ms, cct=0 ms, tt=39 ms]
```

---

## Testing

Run the tests:
```bash
./gradlew test
```

---

## Troubleshooting

1. **Redis Issues**:  
   Verify Redis is running on port 6379 and properly configured.

2. **Database Connection Errors**:  
   Ensure MySQL is up and Vault is correctly configured for password retrieval.

---

## License

This project is licensed under the MIT License.