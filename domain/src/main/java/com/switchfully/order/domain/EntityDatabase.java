package com.switchfully.order.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Dummy In-Memory Database....
 */
public abstract class EntityDatabase<T extends Entity> {

    private Map<UUID, T> entities;

    public EntityDatabase() {
        entities = new HashMap<>();
    }

    void poplulate(T... entities) {
        this.entities = Arrays.stream(entities)
                .collect(Collectors.toMap(Entity::getId, entity -> entity));
    }

    public Map<UUID, T> getAll() {
        return Collections.unmodifiableMap(entities);
    }

    void save(T entity) {
        entities.put(entity.getId(), entity);
    }

    /**
     * Since we don't use transactions yet, we need a way to reset the database
     * in the tests. We'll use this method. Obviously this is a method that should
     * never be available in production...
     */
    void reset() {
        entities = new HashMap<>();
    }
}
