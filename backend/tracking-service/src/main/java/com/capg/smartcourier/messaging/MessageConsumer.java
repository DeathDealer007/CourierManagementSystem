package com.capg.smartcourier.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.smartcourier.entity.Tracking;
import com.capg.smartcourier.event.TrackingEvent;
import com.capg.smartcourier.repository.TrackingRepository;

@Service
// RabbitMQ consumer for tracking events.
// - Listens on tracking.queue
// - Transforms incoming TrackingEvent into Tracking entity and saves it to DB
// - This is how DeliveryService events are materialized into tracking history
public class MessageConsumer {

    @Autowired
    private TrackingRepository repo;

    @RabbitListener(queues = "tracking.queue")
    public void receiveMessage(TrackingEvent event) {

        Tracking tracking = new Tracking();
        tracking.setTrackingNumber(event.getTrackingNumber());
        tracking.setStatus(event.getStatus());
        tracking.setLocation(event.getLocation());
        tracking.setMessage(event.getMessage());
        tracking.setDescription(event.getMessage());
        tracking.setTimestamp(java.time.LocalDateTime.now());

        repo.save(tracking);

        System.out.println("Tracking created from RabbitMQ: " + event.getTrackingNumber());
    }
}