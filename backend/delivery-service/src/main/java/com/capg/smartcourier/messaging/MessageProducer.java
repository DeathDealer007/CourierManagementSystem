package com.capg.smartcourier.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.capg.smartcourier.config.RabbitMQConfig;
import com.capg.smartcourier.event.TrackingEvent;

/**
 * Message Producer
 *
 * Service responsible for publishing messages to RabbitMQ message broker
 * in the Smart Courier System. Handles event-driven communication between
 * microservices, specifically publishing tracking events from delivery-service
 * to tracking-service for real-time delivery updates.
 *
 * Message Flow:
 * 1. Delivery status changes trigger event creation
 * 2. MessageProducer receives TrackingEvent objects
 * 3. Events are serialized to JSON and published to RabbitMQ
 * 4. Tracking-service consumes events for real-time updates
 * 5. WebSocket notifications sent to connected clients
 *
 * RabbitMQ Integration:
 * - Uses RabbitTemplate for message publishing
 * - Configured exchange and routing key from RabbitMQConfig
 * - Automatic JSON serialization of Java objects
 * - Asynchronous message delivery
 *
 * Event Types:
 * - TrackingEvent: Delivery status updates and location changes
 * - Supports extensible event system for future requirements
 *
 * Reliability:
 * - Message persistence in RabbitMQ
 * - Dead letter queues for failed deliveries
 * - Retry mechanisms for transient failures
 * - Monitoring and alerting for message processing
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2024
 */
@Service
public class MessageProducer {

    /**
     * RabbitMQ template for message operations
     * Injected by Spring's dependency injection container
     * Provides high-level API for RabbitMQ interactions
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Send Tracking Event to Message Queue
     *
     * Publishes a TrackingEvent to RabbitMQ for consumption by the tracking service.
     * The event contains delivery status updates that trigger real-time notifications.
     *
     * Publishing Process:
     * 1. Receives TrackingEvent object with delivery update information
     * 2. RabbitTemplate handles JSON serialization automatically
     * 3. Message routed through configured exchange and routing key
     * 4. Asynchronous delivery to tracking-service message consumers
     *
     * Message Structure:
     * - Exchange: Configured routing center for delivery events
     * - Routing Key: Determines queue delivery for tracking updates
     * - Payload: JSON representation of TrackingEvent
     *
     * @param event the TrackingEvent containing delivery status information
     */
    public void sendTrackingEvent(TrackingEvent event) {
        rabbitTemplate.convertAndSend(  //here we are taking the java object as a event, but we are not converting this Java object, the conversion of the java object into JSON is done in rabbitMQ Config file
                RabbitMQConfig.EXCHANGE, //exchange is a routing center
                RabbitMQConfig.ROUTING_KEY, // decides where message goes
                event
        );
    }
}