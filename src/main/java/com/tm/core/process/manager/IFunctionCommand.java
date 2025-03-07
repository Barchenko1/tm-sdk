package com.tm.core.process.manager;

import com.tm.core.finder.parameter.Parameter;
import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IFunctionCommand {
    <E> void saveEntity(Supplier<E> entitySupplier);
    <E> void updateEntity(Supplier<E> entitySupplier);
    <E> void deleteEntity(Supplier<E> entitySupplier);

    void executeConsumer(Consumer<Session> consumer);
}
