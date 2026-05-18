package com.capg.smartcourier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
@SpringBootApplication
@EnableDiscoveryClient
public class DeliveryServiceApplication {

    /**
     * Main method to start the Delivery Service application
     *
     * Initializes the Spring Boot application context, configures all
     * beans, establishes database connections, registers with Eureka
     * server, and starts listening for HTTP requests.
     *
     * Startup Process:
     * 1. Load application configuration
     * 2. Initialize Spring context and auto-configuration
     * 3. Connect to MySQL database
     * 4. Register with Eureka discovery server
     * 5. Start embedded Tomcat server on configured port
     * 6. Initialize RabbitMQ message listeners
     *
     * @param args command line arguments (can include Spring profiles, etc.)
     */
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
        System.out.println("This is delivery service");
    }
}
