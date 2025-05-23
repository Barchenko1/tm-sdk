package com.tm.core.process.dao.transaction;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITransactionHandler {
    <E> void persistSupplier(Supplier<E> supplier);
    <E> void updateSupplier(Supplier<E> supplier);
    <E> void deleteSupplier(Supplier<E> supplier);

    void executeConsumer(Consumer<Session> consumer);
}
