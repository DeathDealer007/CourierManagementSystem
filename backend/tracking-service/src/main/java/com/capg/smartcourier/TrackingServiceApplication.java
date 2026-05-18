package com.capg.smartcourier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Tracking Service Application
 *
 * Main entry point for the Tracking Service microservice in the Smart Courier System.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TrackingServiceApplication {

    /**
     * Main method to start the Tracking Service application
     *
     * Initializes the Spring Boot application context, configures all
     * beans, establishes database and message queue connections,
     * registers with Eureka server, and starts listening for events.
     *
     * Startup Process:
     * 1. Load application configuration
     * 2. Initialize Spring context and auto-configuration
     * 3. Connect to MySQL database
     * 4. Register with Eureka discovery server
     * 5. Connect to RabbitMQ for event consumption
     * 6. Start WebSocket server for real-time updates
     * 7. Start embedded Tomcat server on configured port
     *
     * @param args command line arguments (can include Spring profiles, etc.)
     */
    public static void main(String[] args) {
        SpringApplication.run(TrackingServiceApplication.class, args);
        System.out.println("Tracking service is started");
    }
}