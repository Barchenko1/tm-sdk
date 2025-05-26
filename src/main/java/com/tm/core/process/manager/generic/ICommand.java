package com.tm.core.process.manager.generic;

public interface ICommand {
    <E> void persistEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);
}
