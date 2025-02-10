package com.tm.core.dao.transaction;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITransactionHandler {
    void executeConsumer(Consumer<Session> consumer);
    <E> void saveEntity(Supplier<E> supplier);
    <E> void updateEntity(Supplier<E> supplier);
    <E> void deleteEntity(Supplier<E> supplier);
}
