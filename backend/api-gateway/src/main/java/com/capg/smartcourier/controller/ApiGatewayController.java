package com.capg.smartcourier.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
@Tag(name = "API Gateway", description = "API Gateway Controller")
public class ApiGatewayController {

    @GetMapping("/services")
    @Operation(summary = "Get available services", description = "Returns information about available courier services")
    public Map<String, Object> getServices() {
        Map<String, Object> services = new HashMap<>();
        services.put("standard", Map.of(
            "name", "Standard Delivery",
            "description", "Regular delivery service",
            "estimatedDays", "3-5 days",
            "price", 10.99
        ));
        services.put("express", Map.of(
            "name", "Express Delivery",
            "description", "Fast delivery service",
            "estimatedDays", "1-2 days",
            "price", 19.99
        ));
        services.put("overnight", Map.of(
            "name", "Overnight Delivery",
            "description", "Next day delivery",
            "estimatedDays", "1 day",
            "price", 29.99
        ));
        return services;
    }
}
