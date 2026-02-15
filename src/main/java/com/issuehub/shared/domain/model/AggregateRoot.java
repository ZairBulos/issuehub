package com.issuehub.shared.domain.model;

import com.issuehub.shared.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {

    private final List<DomainEvent> events = new ArrayList<>();

    protected void addEvent(DomainEvent event) {
        events.add(event);
    }

    public List<DomainEvent> pullEvents() {
        var domainEvents = new ArrayList<>(events);
        events.clear();
        return domainEvents;
    }

}
