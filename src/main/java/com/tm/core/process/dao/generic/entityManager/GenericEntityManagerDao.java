package com.tm.core.process.dao.generic.entityManager;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericEntityManagerDao extends AbstractGenericEntityManagerDao {

    public GenericEntityManagerDao(EntityManager entityManager, String entityPackage) {
        super(entityManager, entityPackage);
    }

    @Override
    public <E> void persistEntity(E entity) {
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        entityManager.remove(entity);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.remove(entity);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        consumer.accept(entityManager);
    }

    @Override
    public <T> T executeFunction(Function<EntityManager, T> function) {
        return function.apply(entityManager);
    }
}
