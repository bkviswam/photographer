# Craft Photographer Service

## Overview

The **Craft Photographer Service** is a Spring Boot application designed to manage and search for photographers based on event types, proximity, and other attributes. It leverages Redis for caching, MySQL as the primary database, and supports JWT-based authentication.

## Features

- **Photographer Management**: Register, update, and delete photographers.
- **Event-based Search**: Find photographers based on event type.
- **Proximity Search**: Search for photographers by geographical proximity.
- **JWT Authentication**: Secure endpoints with token-based authentication.
- **Redis Caching**: Cache frequently accessed data for improved performance.
- **Resilience4J Circuit Breaker**: Handle network or service disruptions gracefully.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3.3.4**
- **MySQL 8.0**
- **Redis with Lettuce Client**
- **JWT (JSON Web Tokens) for Authentication**
- **Resilience4J for Circuit Breaking**
- **Gradle Build System**

---

## Prerequisites

1. **Java 17** installed.
2. **Docker** installed (for Redis and MySQL containers).
3. **Gradle** installed (optional if using the wrapper).

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd craft-demo-photographer-service
```

### 2. Set Up Docker Containers

Create a `docker-compose.yml` to launch MySQL and Redis services:
```yaml
version: '3.8'

services:
  mysql-db:
    image: mysql:8.0
    container_name: mysql-db-photographer
    environment:
      MYSQL_ROOT_PASSWORD: 1q2w3e4r5t
      MYSQL_DATABASE: photographer_db
      MYSQL_USER: app_user
      MYSQL_PASSWORD: 1q2w3e4r
    ports:
      - "3306:3306"
    networks:
      - photographer-service-network
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:latest
    container_name: redis-photographer
    ports:
      - "6379:6379"
    networks:
      - photographer-service-network

volumes:
  mysql_data:

networks:
  photographer-service-network:
```

Run the containers:
```bash
docker-compose up -d
```

---

### 3. Configure `application.properties`

Update the `src/main/resources/application.properties`:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/photographer_db
spring.datasource.username=app_user
spring.datasource.password=1q2w3e4r
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
spring.cache.redis.time-to-live=300s

resilience4j.circuitbreaker.instances.proximityService.slidingWindowSize=10
resilience4j.circuitbreaker.instances.proximityService.minimumNumberOfCalls=5
resilience4j.circuitbreaker.instances.proximityService.failureRateThreshold=50
resilience4j.circuitbreaker.instances.proximityService.waitDurationInOpenState=10000ms
```

---

### 4. Build and Run the Application

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

### **Authentication**

- **Register User**:  
  `POST /api/auth/register`  
  Payload:
  ```json
  {
    "username": "aarin",
    "password": "flower1",
    "role": "API_USER"
  }
  ```

- **Login**:  
  `POST /api/auth/login`  
  Payload:
  ```json
  {
    "username": "aarin",
    "password": "flower1"
  }
  ```

### **Photographers API**

- **Get All Photographers**:  
  `GET /api/photographers`

- **Get Photographer by ID**:  
  `GET /api/photographers/{id}`

- **Search by Event Type**:  
  `GET /api/photographers/event/{eventType}`

- **Search by Proximity**:  
  `GET /api/photographers/proximity?lat={latitude}&lng={longitude}&radius={radius}`

---

## Exception Handling

A global exception handler ensures that errors are returned in a consistent JSON format. For example:

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

- **Redis** is used to cache expensive operations like proximity search.
- Example:
  ```java
  @Cacheable(value = "photographersByProximity", key = "{#lat, #lng, #radius}")
  public List<PhotographerDTO> getPhotographersByProximity(Long userId, double lat, double lng, double radius) {
      // Implementation
  }
  ```

---

## Circuit Breaker

The **Resilience4J** circuit breaker is configured for the proximity search service. If multiple consecutive failures occur, the circuit opens and prevents further calls until the wait duration elapses.

---

## Logging

The `RequestResponseLoggingFilter` logs request and response times along with UUIDs for easy tracking.

Example log:
```
INFO  c.i.c.p.f.RequestResponseLoggingFilter [70ffe495-f636-4c58-bf4a-bce50d7a33da] 
- uuid=70ffe495-f636-4c58-bf4a-bce50d7a33da, method=GET, uri=/api/photographers/proximity, 
code=200, sst=12 ms, dbt=27 ms, cct=0 ms, tt=39 ms
```

---

## Testing

Run the tests using:
```bash
./gradlew test
```

---

## Troubleshooting

1. **Redis Connection Issues**:
    - Ensure the Redis container is running on port 6379.
    - Verify Redis configuration in `application.properties`.

2. **Database Errors**:
    - Confirm that the MySQL container is up and running.
    - Verify the database credentials in the `application.properties`.

---

## License

This project is licensed under the MIT License.

---

## Conclusion

The **Craft Demo Photographer Service** provides a robust and scalable architecture for managing photographers. With Redis caching, JWT authentication, and circuit breakers, it ensures performance and reliability in distributed environments.