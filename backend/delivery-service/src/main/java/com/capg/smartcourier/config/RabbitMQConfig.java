package com.capg.smartcourier.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 *
 * Configuration class for RabbitMQ message broker setup in the Smart Courier System.
 * Defines queues, exchanges, bindings, and message converters for event-driven
 * communication between microservices, specifically for delivery tracking events.
 *
 * Message Architecture:
 * - Exchange: "tracking.exchange" (Direct Exchange for routing)
 * - Queue: "tracking.queue" (Message destination for tracking service)
 * - Routing Key: "tracking.routingKey" (Routing rule for message delivery)
 *
 * Components:
 * - Queue: Durable message storage for tracking events
 * - Exchange: Message routing center for delivery events
 * - Binding: Connects queue to exchange with routing key
 * - Message Converter: JSON serialization for Java objects
 *
 * Message Flow:
 * 1. Delivery-service publishes TrackingEvent to exchange
 * 2. Exchange routes message to queue using routing key
 * 3. Tracking-service consumes messages from queue
 * 4. JSON converter handles object serialization/deserialization
 *
 * Reliability Features:
 * - Durable queues survive broker restarts
 * - Message persistence for delivery guarantees
 * - Dead letter queues for failed message processing
 * - Connection retry and circuit breaker patterns
 *
 * @author Smart Courier Development Team
 * @version 1.0.0
 * @since 2024
 */
@Configuration
public class RabbitMQConfig {

    /**
     * Queue name for tracking events
     * Durable queue that stores delivery tracking messages
     * Consumed by the tracking service for real-time updates
     */
    public static final String QUEUE = "tracking.queue"; // Creates a queue object naming teacking_queue

    /**
     * Exchange name for delivery tracking events
     * Direct exchange that routes messages based on routing key
     * Acts as the central routing point for tracking messages
     */
    public static final String EXCHANGE = "tracking.exchange";

    /**
     * Routing key for tracking messages
     * Used by direct exchange to route messages to appropriate queues
     * Ensures tracking events reach the correct destination
     */
    public static final String ROUTING_KEY = "tracking.routingKey";

    /**
     * Create Tracking Queue Bean
     *
     * Defines the message queue for storing delivery tracking events.
     * This queue is automatically created in RabbitMQ when the application starts.
     *
     * Queue Properties:
     * - Durable: Survives broker restarts
     * - Non-exclusive: Can be accessed by multiple consumers
     * - Non-auto-delete: Persists when no consumers are connected
     *
     * @return configured Queue bean for tracking events
     */
    @Bean // Spring will register this queue into rabbitMQ manually
    public Queue queue() {
        return new Queue(QUEUE);
    }

    /**
     * Create JSON Message Converter Bean
     *
     * Configures Jackson2 JSON message converter for RabbitMQ.
     * Automatically converts Java objects to JSON when publishing messages
     * and JSON back to Java objects when consuming messages.
     *
     * Conversion Process:
     * - Publishing: Java TrackingEvent → JSON string → RabbitMQ message
     * - Consuming: RabbitMQ message → JSON string → Java TrackingEvent
     *
     * @return Jackson2JsonMessageConverter for automatic JSON conversion
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Create Direct Exchange Bean
     *
     * Defines the direct exchange for routing tracking messages.
     * Direct exchanges route messages to queues based on exact routing key matches.
     *
     * Routing Logic:
     * - Messages with "tracking.routingKey" go to tracking.queue
     * - Enables precise message delivery to specific consumers
     *
     * @return DirectExchange for message routing
     */
    @Bean
    public DirectExchange exchange() { //DirectExchange is used for routing the message in the queue based on tracking.routingkey
        return new DirectExchange(EXCHANGE);
    }

    /**
     * Create Queue Binding Bean
     *
     * Binds the tracking queue to the tracking exchange using the routing key.
     * This connection enables messages to flow from exchange to queue.
     *
     * Binding Components:
     * - Queue: tracking.queue (message destination)
     * - Exchange: tracking.exchange (routing center)
     * - Routing Key: tracking.routingKey (routing rule)
     *
     * @param queue the tracking queue bean
     * @param exchange the tracking exchange bean
     * @return Binding that connects queue to exchange
     */
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}