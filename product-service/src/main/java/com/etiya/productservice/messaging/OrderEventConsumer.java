package com.etiya.productservice.messaging;

import com.etiya.productservice.events.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * Spring Cloud Stream consumer. The bean name {@code orderCreated} is referenced by
 * {@code spring.cloud.function.definition} and bound to the input binding
 * {@code orderCreated-in-0} (Kafka topic "order-created") in application.yml.
 */
@Configuration
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @Bean
    public Consumer<OrderCreatedEvent> orderCreated() {
        return event -> log.info("OrderCreated event consumed -> {}", event);
    }
}
