package com.tm.core.process.manager.common;

public interface IEntityCommand {
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);
}
