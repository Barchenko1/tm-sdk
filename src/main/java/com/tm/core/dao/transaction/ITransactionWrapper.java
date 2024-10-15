package com.tm.core.dao.transaction;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ITransactionWrapper {
    <E> void saveEntity(Supplier<E> supplier);
    void saveEntity(Consumer<Session> consumer);
    <E> void updateEntity(Supplier<E> supplier);
    void updateEntity(Consumer<Session> consumer);
    <E> void deleteEntity(Supplier<E> supplier);
    void deleteEntity(Consumer<Session> consumer);
}
