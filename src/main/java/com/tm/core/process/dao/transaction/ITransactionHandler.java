package com.tm.core.process.dao.transaction;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITransactionHandler {
    <E> void persistEntity(E entity);
    <E> void mergeEntity(E entity);
    <E> void deleteEntity(E entity);

    <E> void persistSupplier(Supplier<E> supplier);
    <E> void mergeSupplier(Supplier<E> supplier);
    <E> void deleteSupplier(Supplier<E> supplier);

    void executeConsumer(Consumer<EntityManager> consumer);
}
