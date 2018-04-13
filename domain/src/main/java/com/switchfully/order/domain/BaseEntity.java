package com.switchfully.order.domain;

import java.util.UUID;

public abstract class BaseEntity {

    private UUID id;

    protected BaseEntity(UUID id) {
        this.id = id;
    }

    public void generateId() throws IllegalStateException {
        if (id != null) {
            throw new IllegalStateException("Generating an ID for a customer that already has " +
                    "an ID (" + id + ") is not allowed.");
        }
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

}
