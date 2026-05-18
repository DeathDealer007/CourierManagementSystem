# Smart Courier System - Logging Configuration Guide

## Overview
Comprehensive logging implementation using **SLF4J** with **Logback** backend across all microservices.

---

## 📋 What Has Been Added

### 1. **Logback Configuration Files**

Created `logback.xml` in each service's `src/main/resources/`:
- **auth-service/src/main/resources/logback.xml**
- **delivery-service/src/main/resources/logback.xml**
- **tracking-service/src/main/resources/logback.xml**
- **admin-service/src/main/resources/logback.xml**

#### Configuration Features:
- **Console Appender** - Real-time logs in terminal
- **File Appender (All Logs)** - All logs to `./logs/smart-courier.log`
- **File Appender (Error Only)** - Errors only to `./logs/smart-courier-error.log`
- **Async Appender** - Non-blocking file I/O for performance
- **Rolling Policy** - Automatic log rotation:
  - Max file size: 10 MB
  - Max history: 30 days
  - Total capacity: 1 GB per log type
  - Archive location: `./logs/archive/`

#### Log Levels Configuration:
```xml
<!-- Application Package (Your Code) -->
<logger name="com.capg.smartcourier" level="DEBUG" />

<!-- Spring Framework -->
<logger name="org.springframework" level="INFO" />
<logger name="org.springframework.web" level="DEBUG" />
<logger name="org.springframework.security" level="DEBUG" />

<!-- Database -->
<logger name="org.hibernate" level="INFO" />
<logger name="org.hibernate.SQL" level="DEBUG" />
<logger name="com.zaxxer.hikari" level="INFO" />

<!-- Messaging (for Delivery/Tracking services) -->
<logger name="org.springframework.amqp" level="DEBUG" />

<!-- Service Discovery (for Admin service) -->
<logger name="org.springframework.cloud" level="DEBUG" />
<logger name="com.netflix.eureka" level="INFO" />
```

#### Environment-Specific Profiles:
```xml
<!-- Development Profile -->
<springProfile name="dev">
  <root level="DEBUG">
    <appender-ref ref="CONSOLE" />
    <appender-ref ref="ASYNC_FILE" />
  </root>
</springProfile>

<!-- Production Profile -->
<springProfile name="prod">
  <root level="WARN">
    <appender-ref ref="ASYNC_FILE" />
    <appender-ref ref="FILE_ERROR" />
  </root>
</springProfile>
```

---

### 2. **Application Properties Logging Configuration**

Updated `application.properties` in each service with logging settings:

**Auth Service Example:**
```properties
spring.application.name=auth-service
server.port=8081

# Logging Configuration
logging.level.root=INFO
logging.level.com.capg.smartcourier=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.security=DEBUG
logging.file.name=logs/auth-service.log
logging.logback.rollingpolicy.max-file-size=10MB
logging.logback.rollingpolicy.max-history=30
logging.logback.rollingpolicy.total-size-cap=1GB
```

**All Services Updated:**
- auth-service (port 8081)
- delivery-service (port 8082)
- tracking-service (port 8083)
- admin-service (port 8084)

---

### 3. **Logging Usage in Code**

Added SLF4J logging to **AuthService.java** as an example:

```java
package com.capg.smartcourier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {
    
    // Create logger instance
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    public String register(User user) {
        logger.info("Registering new user: {}", user.getUsername());
        try {
            // ... registration logic
            logger.info("User {} registered successfully", user.getUsername());
            return "User registered";
        } catch (Exception e) {
            logger.error("Error registering user {}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
    
    public String login(User user) {
        logger.debug("Login attempt for user: {}", user.getUsername());
        try {
            // ... login logic
            logger.info("User {} logged in successfully", user.getUsername());
            return token;
        } catch (ResourceNotFoundException e) {
            logger.warn("Login failed: User not found - {}", user.getUsername());
            throw e;
        } catch (Exception e) {
            logger.error("Login error for user {}: {}", user.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
```

---

## 🎯 Log Levels Explained

| Level | Usage | Example |
|-------|-------|---------|
| **TRACE** | Very detailed diagnostic info | SQL parameter binding |
| **DEBUG** | Development debugging | Method entry/exit, variable values |
| **INFO** | Important business events | User login, order created, service started |
| **WARN** | Warning conditions | Failed login attempts, deprecated API usage |
| **ERROR** | Error events | Database connection failed, invalid data |
| **FATAL/PANIC** | Critical system errors | System shutdown required |

---

## 📂 Log File Structure

```
project-root/
└── logs/
    ├── smart-courier.log              # All logs (rotated)
    ├── smart-courier-error.log        # Errors only (rotated)
    ├── auth-service.log               # Service-specific
    ├── delivery-service.log
    ├── tracking-service.log
    ├── admin-service.log
    └── archive/
        ├── smart-courier-2026-03-30.1.log
        ├── smart-courier-2026-03-29.1.log
        ├── smart-courier-error-2026-03-30.1.log
        └── ...
```

---

## 🚀 Running with Different Log Levels

### Development Mode (DEBUG level)
```bash
java -jar auth-service-1.0.0.jar --spring.profiles.active=dev
```

### Production Mode (WARN level)
```bash
java -jar auth-service-1.0.0.jar --spring.profiles.active=prod
```

### Custom Log Level at Runtime
```bash
java -jar auth-service-1.0.0.jar --logging.level.com.capg.smartcourier=TRACE
```

---

## 📊 Log Output Format

```
2026-03-30 14:32:15.123 [http-nio-8081-exec-1] INFO  c.capg.smartcourier.service.AuthService - Registering new user: john_doe
2026-03-30 14:32:16.456 [http-nio-8081-exec-1] DEBUG c.capg.smartcourier.entity.User - User properties: username=john_doe, email=john@example.com
2026-03-30 14:32:17.789 [http-nio-8081-exec-1] INFO  c.capg.smartcourier.service.AuthService - User john_doe registered successfully
2026-03-30 14:32:25.012 [http-nio-8081-exec-2] DEBUG c.capg.smartcourier.service.AuthService - Login attempt for user: john_doe
2026-03-30 14:32:25.345 [http-nio-8081-exec-2] INFO  c.capg.smartcourier.service.AuthService - User john_doe logged in successfully
```

**Format Breakdown:**
- `2026-03-30 14:32:15.123` - Timestamp (date, time, milliseconds)
- `[http-nio-8081-exec-1]` - Thread name executing the log
- `INFO` - Log level
- `c.capg.smartcourier.service.AuthService` - Logger name (abbreviated)
- `- ` - Separator
- `Registering new user: john_doe` - Actual log message

---

## 🔍 Viewing Logs

### View All Logs in Real-time
```bash
tail -f logs/smart-courier.log
```

### View Only Errors
```bash
tail -f logs/smart-courier-error.log
```

### Search Logs
```bash
grep "ERROR\|WARN" logs/smart-courier.log
grep "user_id" logs/auth-service.log
```

### Count Log Entries
```bash
grep -c "INFO" logs/smart-courier.log
```

### View Specific Date Range
```bash
grep "2026-03-30 14:" logs/smart-courier.log
```

---

## 💡 Best Practices

### 1. **Use Meaningful Log Messages**
```java
// ✅ Good
logger.info("User {} logged in from IP: {}", username, ipAddress);

// ❌ Bad
logger.info("Login success");
```

### 2. **Use Appropriate Log Levels**
```java
logger.debug("Processing order items");    // Development details
logger.info("Order #123 created");         // User-facing events
logger.warn("Retry attempt 3/5");          // Potential issues
logger.error("Database connection failed", exception);  // Errors
```

### 3. **Include Context in Logs**
```java
logger.info("User {} order {} status changed to {}", 
            userId, orderId, newStatus);
```

### 4. **Log Exceptions Properly**
```java
// ✅ Good - includes exception stack trace
logger.error("Failed to process delivery {}: {}", deliveryId, e.getMessage(), e);

// ❌ Bad - loses exception details
logger.error("Failed: " + e.getMessage());
```

### 5. **Avoid Logging Sensitive Data**
```java
// ❌ Bad - logs password
logger.debug("User login: username={}, password={}", user.getUsername(), user.getPassword());

// ✅ Good - only logs safe info
logger.debug("User login: username={}", user.getUsername());
```

---

## 🔧 Common Logging Patterns

### Pattern 1: Method Entry/Exit
```java
public String processDelivery(Long deliveryId) {
    logger.debug("Entering processDelivery with id: {}", deliveryId);
    try {
        // ... logic
        logger.debug("Exiting processDelivery successfully");
        return result;
    } catch (Exception e) {
        logger.error("Error in processDelivery: {}", e.getMessage(), e);
        throw e;
    }
}
```

### Pattern 2: Business Events
```java
logger.info("Delivery {} status updated from {} to {}", 
            deliveryId, oldStatus, newStatus);
```

### Pattern 3: Performance Monitoring
```java
long startTime = System.currentTimeMillis();
// ... operation
long duration = System.currentTimeMillis() - startTime;
logger.info("Database query took {} ms", duration);
if (duration > 1000) {
    logger.warn("Slow database query detected: {} ms", duration);
}
```

### Pattern 4: Error Recovery
```java
try {
    // ... operation
} catch (TemporaryException e) {
    logger.warn("Temporary error, retrying... Attempt {}/{}", 
                attempt, maxAttempts);
} catch (PermanentException e) {
    logger.error("Permanent error, failing: {}", e.getMessage(), e);
}
```

---

## 📝 Interview Questions About Logging

**Q: What logging framework are you using?**
A: SLF4J (Simple Logging Facade for Java) with Logback backend. SLF4J provides a facade that allows runtime selection of implementation, while Logback is a high-performance, modern replacement for Log4j.

**Q: How do you handle large log files?**
A: Using Logback's RollingFileAppender with SizeAndTimeBasedRollingPolicy. Logs rotate daily or when they exceed 10MB, and archives are automatically deleted after 30 days or when total size exceeds 1GB.

**Q: How do you prevent performance issues from logging?**
A: Using AsyncAppender to handle file writes asynchronously on a background thread, ensuring logging doesn't block application threads.

**Q: How do you structure logs for debugging distributed systems?**
A: Using consistent log patterns with request IDs, user IDs, and operation context. Each log includes timestamp, thread, log level, and meaningful messages with relevant variables.

---

## 🎯 Next Steps

1. **Apply logging to other services** using the same pattern as AuthService
2. **Add request ID logging** for distributed tracing across microservices
3. **Implement log aggregation** using ELK Stack (Elasticsearch, Logstash, Kibana)
4. **Add metrics/monitoring** for log file sizes and error rates

---

**Last Updated:** March 30, 2026
