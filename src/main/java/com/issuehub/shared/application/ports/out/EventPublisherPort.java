package com.issuehub.shared.application.ports.out;

import com.issuehub.shared.domain.events.DomainEvent;

import java.util.List;

public interface EventPublisherPort {
    void publish(DomainEvent event);
    void publishAll(List<DomainEvent> events);
}
