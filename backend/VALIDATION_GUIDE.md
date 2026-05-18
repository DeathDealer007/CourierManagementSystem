# Smart Courier System - Validation Guide

## Overview
This guide explains the comprehensive validation implementation added to the Smart Courier System project using Jakarta Bean Validation (JSR-303/380).

---

## 🔍 What Has Been Added

### 1. **Entity Validation Annotations**

#### Auth Service - User Entity
- `@NotBlank` - Username and Email required
- `@Size` - Username: 3-50 chars, Password: 8-100 chars  
- `@Email` - Valid email format
- `@Pattern` - Username: alphanumeric + `_` and `-` only
- `@Pattern` - Password: Must contain uppercase, lowercase, digit, and special character

**Example:**
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50)
@Pattern(regexp = "^[a-zA-Z0-9_-]+$")
private String username;

@NotBlank(message = "Email is required")
@Email(message = "Email should be valid")
private String email;
```

#### Delivery Service - Delivery Entity
- `@NotBlank` - Tracking number required
- `@Pattern` - Tracking: 10-20 alphanumeric characters
- `@NotNull` - User ID required
- `@Positive` - User ID must be > 0
- `@NotBlank` - Service type required (STANDARD|EXPRESS|OVERNIGHT|SAME_DAY)
- `@NotNull` - Status required (CREATED|PICKED|IN_TRANSIT|DELIVERED|FAILED|CANCELLED)
- `@Positive` - Total amount must be > 0
- `@Digits` - Format: max 10 integer, 2 decimal places

**Example:**
```java
@NotBlank(message = "Tracking number is required")
@Pattern(regexp = "^[A-Z0-9]{10,20}$")
private String trackingNumber;

@NotNull(message = "Total amount is required")
@Positive(message = "Total amount must be greater than 0")
@Digits(integer = 10, fraction = 2)
private Double totalAmount;
```

#### Admin Service - Hub Entity
- `@NotBlank` - Name, Location, Address, Manager required
- `@Size` - Name: 3-100 chars, Location: 3-100 chars, Address: 5-200 chars
- `@Pattern` - Contact number: 10-20 valid phone characters

**Example:**
```java
@NotBlank(message = "Hub name is required")
@Size(min = 3, max = 100)
private String name;

@NotBlank(message = "Contact number is required")
@Pattern(regexp = "^[\\d\\s\\-\\+\\(\\)]{10,20}$")
private String contactNumber;
```

#### Tracking Service - Document Entity
- `@NotBlank` - File name and type required
- `@NotNull` - File size required
- `@Positive` - File size > 0
- `@Max` - File size max 100 MB
- `@Positive` - Delivery ID and User ID must be positive

**Example:**
```java
@NotBlank(message = "File name is required")
@Size(max = 255)
private String fileName;

@NotNull(message = "File size is required")
@Positive(message = "File size must be greater than 0")
@Max(value = 104857600, message = "File size must not exceed 100 MB")
private Long fileSize;
```

#### Tracking Service - DeliveryProof Entity
- `@NotNull` - Delivery ID required
- `@Pattern` - Proof type: (PHOTO|SIGNATURE|VIDEO|DOCUMENT|OTHER)
- `@NotBlank` - Proof data required
- `@NotBlank` - Location required

**Example:**
```java
@NotBlank(message = "Proof type is required")
@Pattern(regexp = "^(PHOTO|SIGNATURE|VIDEO|DOCUMENT|OTHER)$")
private String proofType;

@NotBlank(message = "Proof data is required")
@Size(min = 10)
private String proofData;
```

---

### 2. **Controller Validation**

All controller endpoints have been updated with `@Valid` annotation to enable validation on request bodies.

#### Auth Controller
```java
@PostMapping("/register")
public String register(@Valid @RequestBody User user) {
    return service.register(user);
}

@PostMapping("/login")
public String login(@Valid @RequestBody User user) {
    return service.login(user);
}
```

#### Delivery Controller
```java
@PostMapping
public Delivery create(@Valid @RequestBody Delivery delivery) {
    return service.createDelivery(delivery);
}
```

#### Tracking Controller
```java
@PostMapping
public Tracking addTracking(@Valid @RequestBody Tracking tracking) {
    return trackingService.addTracking(tracking);
}
```

---

### 3. **Global Exception Handlers**

All services now have enhanced `GlobalExceptionHandler` that handles validation errors gracefully.

#### Handled Exceptions:
1. **MethodArgumentNotValidException** - Catches @Valid constraint violations
2. **ConstraintViolationException** - Catches path variable/parameter violations
3. **ResourceNotFoundException** - Custom 404 errors
4. **IllegalArgumentException** - Bad request errors
5. **FeignException** (Admin Service) - Inter-service communication errors
6. **General Exception** - Fallback handler

#### Response Format for Validation Errors:
```json
{
  "timestamp": 1711830000000,
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid",
    "password": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
  }
}
```

---

## 📋 Validation Rules Summary

| Entity | Field | Rules |
|--------|-------|-------|
| **User** | username | NotBlank, Size(3-50), Pattern(alphanumeric+_-) |
| | email | NotBlank, Email |
| | password | NotBlank, Size(8-100), Pattern(strong) |
| **Delivery** | trackingNumber | NotBlank, Pattern(10-20 alphanumeric) |
| | userId | NotNull, Positive |
| | serviceType | NotBlank, Pattern(enum values) |
| | status | NotBlank, Pattern(enum values) |
| | totalAmount | NotNull, Positive, Digits(10,2) |
| **Hub** | name | NotBlank, Size(3-100) |
| | location | NotBlank, Size(3-100) |
| | address | NotBlank, Size(5-200) |
| | contactNumber | NotBlank, Pattern(phone format) |
| | manager | NotBlank, Size(3-100) |
| **Document** | fileName | NotBlank, Size(max 255) |
| | fileType | NotBlank, Pattern(mime type) |
| | fileSize | NotNull, Positive, Max(100MB) |
| | deliveryId | NotNull, Positive |
| | userId | NotNull, Positive |
| **DeliveryProof** | deliveryId | NotNull, Positive |
| | proofType | NotBlank, Pattern(enum values) |
| | proofData | NotBlank, Size(min 10) |
| | location | NotBlank, Size(3-200) |
| | capturedBy | NotNull, Positive |

---

## 🚀 How to Test

### Using Postman/cURL

#### Invalid Request (should fail validation)
```bash
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "ab",  // ❌ Too short (min 3)
  "email": "invalid-email",  // ❌ Invalid email format
  "password": "weak"  // ❌ Too weak (needs uppercase, lowercase, digit, special char)
}
```

**Response:**
```json
{
  "timestamp": 1711830000000,
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "username": "Username must be between 3 and 50 characters",
    "email": "Email should be valid",
    "password": "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
  }
}
```

#### Valid Request (should succeed)
```bash
POST http://localhost:8081/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",  // ✅ Valid
  "email": "john@example.com",  // ✅ Valid email
  "password": "SecurePass123!"  // ✅ Strong password
}
```

---

## 🔧 Custom Validation Messages

All validation annotations include custom error messages for better user feedback. You can customize them in the entity class annotations:

```java
@NotBlank(message = "Username is required and cannot be empty")
@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
private String username;
```

---

## 📝 Important Notes

1. **Jakarta Bean Validation**: Uses `jakarta.validation.*` annotations (not javax.validation.*)
2. **Automatic Validation**: Controllers don't need explicit validation code - it's automatic with `@Valid`
3. **Error Response**: Validation errors return HTTP 400 (Bad Request) with detailed error messages
4. **Cascade Validation**: For nested objects, use `@Valid` on the field to enable recursive validation
5. **Service Layer**: Services can also use `@Validated` and method parameter validation if needed

---

## 🎯 Benefits of This Implementation

✅ **Type Safety** - Ensures data integrity at boundaries
✅ **User Feedback** - Clear, actionable error messages
✅ **Security** - Prevents invalid/malicious data from entering system
✅ **Consistency** - Unified validation across all microservices
✅ **Maintainability** - Declarative validation is easier to read and modify
✅ **Performance** - Fails fast before reaching business logic

---

## 🔄 Running with Validation

```bash
# Build project
mvn clean package -DskipTests

# Run individual service
cd auth-service
java -jar target/auth-service-1.0.0.jar

# Test with valid data
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "user123", "email": "user@test.com", "password": "SecurePass123!"}'

# Test with invalid data (will return 400 with error details)
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "ab", "email": "invalid"}'
```

---

## 👨‍💼 For Interviews

**Q: What validation patterns do you follow?**

A: We use Jakarta Bean Validation (JSR-380) with annotations like `@NotBlank`, `@Email`, `@Size`, `@Pattern`, `@Positive`, `@Digits` on entity fields. Controllers use `@Valid` to enable validation, and custom `GlobalExceptionHandler` converts validation errors into user-friendly JSON responses.

**Q: How do you handle validation errors?**

A: Validation errors are captured by `MethodArgumentNotValidException` handler in `GlobalExceptionHandler`. We return HTTP 400 with a structured response containing field-level error messages, making it easy for clients to identify and fix issues.

---

**Last Updated:** March 30, 2026
