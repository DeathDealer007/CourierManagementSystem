package com.capg.smartcourier.feign;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/deliveries")
    List<Object> getAllDeliveries();
    
    @PutMapping("/api/deliveries/{id}")
    Object updateDelivery(@PathVariable Long id, @RequestBody Map<String, String> body);
}