package com.tm.core.process.dao;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommandDao {
    <E> void saveEntity(Supplier<E> supplier);
    <E> void updateEntity(Supplier<E> supplier);
    <E> void deleteEntity(Supplier<E> supplier);

    void executeConsumer(Consumer<Session> consumer);
}
