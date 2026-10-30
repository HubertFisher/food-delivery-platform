package kz.alitulegenov.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain event published when an order changes state (used across RabbitMQ and Kafka).
 */
public record OrderEvent(
        UUID eventId,
        UUID orderId,
        String customerId,
        String customerEmail,
        String restaurantId,
        String restaurantName,
        OrderEventType eventType,
        BigDecimal totalAmount,
        String deliveryAddress,
        List<OrderItemPayload> items,
        Instant occurredAt
) {
    public enum OrderEventType {
        ORDER_CREATED,
        ORDER_PAID,
        ORDER_PREPARING,
        ORDER_READY_FOR_PICKUP,
        ORDER_DELIVERING,
        ORDER_DELIVERED,
        ORDER_CANCELLED
    }

    public record OrderItemPayload(
            String menuItemId,
            String name,
            int quantity,
            BigDecimal unitPrice
    ) {
    }
}
