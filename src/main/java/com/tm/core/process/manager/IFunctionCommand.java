package com.tm.core.process.manager;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommand {
    <E> void persistSupplier(Supplier<E> entitySupplier);
    <E> void updateSupplier(Supplier<E> entitySupplier);
    <E> void deleteSupplier(Supplier<E> entitySupplier);

    void executeConsumer(Consumer<Session> consumer);
}
