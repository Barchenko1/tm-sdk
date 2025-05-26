package com.tm.core.process.manager;

import jakarta.persistence.EntityManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommand {
    <E> void persistSupplier(Supplier<E> entitySupplier);
    <E> void mergeSupplier(Supplier<E> entitySupplier);
    <E> void deleteSupplier(Supplier<E> entitySupplier);

    void executeConsumer(Consumer<EntityManager> consumer);
}
