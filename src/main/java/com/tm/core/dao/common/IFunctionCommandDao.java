package com.tm.core.dao.common;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommandDao {
    <E> void saveEntity(Supplier<E> supplier);
    <E> void updateEntity(Supplier<E> supplier);
    <E> void deleteEntity(Supplier<E> supplier);

    void saveEntity(Consumer<Session> consumer);
    void updateEntity(Consumer<Session> consumer);
    void deleteEntity(Consumer<Session> consumer);
}
