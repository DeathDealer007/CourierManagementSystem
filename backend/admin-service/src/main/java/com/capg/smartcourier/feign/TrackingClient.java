package com.capg.smartcourier.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tracking-service")
public interface TrackingClient {

    @GetMapping("/tracking/{trackingNumber}")
    List<Object> getTracking(@PathVariable String trackingNumber);
}