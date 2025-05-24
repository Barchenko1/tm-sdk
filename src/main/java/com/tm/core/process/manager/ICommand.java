package com.tm.core.process.manager;

public interface ICommand {
    <E> void persistEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);
}
