package co.crediyacorp.model.external_services.utils;

import java.time.Instant;

public interface DomainEvent {
    String getEventName();
    Instant getOccurredOn();
}