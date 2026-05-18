# Use lightweight Java image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/eureka-server-1.0.0.jar app.jar

# Expose port
EXPOSE 8761

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]