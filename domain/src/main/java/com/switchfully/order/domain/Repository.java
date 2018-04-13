package com.switchfully.order.domain;

import java.util.Map;
import java.util.UUID;

public abstract class Repository<T extends Entity, U extends EntityDatabase<T>> {

    private U database;

    public Repository(U database) {
        this.database = database;
    }

    protected U getDatabase() {
        return database;
    }

    public T save(T entity) {
        entity.generateId();
        database.save(entity);
        return entity;
    }

    public T update(T entity) {
        database.save(entity);
        return entity;
    }

    public Map<UUID, T> getAll() {
        return database.getAll();
    }

    public T get(UUID entityId) {
        return database.getAll().get(entityId);
    }

    /**
     * Since we don't use transactions yet, we need a way to reset the database
     * in the tests. We'll use this method. Obviously this is a method that should
     * never be available in production...
     */
    public void reset() {
        database.reset();
    }
}
