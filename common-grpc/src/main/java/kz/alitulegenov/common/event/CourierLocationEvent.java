package kz.alitulegenov.common.event;

import java.time.Instant;
import java.util.UUID;

/**
 * High-frequency telemetry event streamed via Apache Kafka to track courier GPS coordinates in real time.
 */
public record CourierLocationEvent(
        UUID eventId,
        String courierId,
        UUID activeOrderId,
        double latitude,
        double longitude,
        Instant timestamp
) {
}
