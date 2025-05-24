package com.tm.core.process.dao;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommandDao {
    <E> void persistSupplier(Supplier<E> supplier);
    <E> void updateSupplier(Supplier<E> supplier);
    <E> void deleteSupplier(Supplier<E> supplier);

    void executeConsumer(Consumer<EntityManager> consumer);
}
