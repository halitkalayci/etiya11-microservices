package com.etiya.orderservice.messaging;

import com.etiya.orderservice.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Publishes order events to Kafka through Spring Cloud Stream.
 *
 * <p>{@link StreamBridge} sends to the binding {@code orderCreated-out-0},
 * which is mapped to the Kafka topic in application.yml.</p>
 */
@Component
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    /** Output binding name; destination (topic) is configured in application.yml. */
    private static final String ORDER_CREATED_BINDING = "orderCreated-out-0";

    private final StreamBridge streamBridge;

    public OrderProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        streamBridge.send(ORDER_CREATED_BINDING, event);
        log.info("OrderCreated event published: {}", event);
    }
}
