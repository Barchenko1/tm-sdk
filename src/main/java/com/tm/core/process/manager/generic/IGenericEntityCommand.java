package com.tm.core.process.manager.generic;

public interface IGenericEntityCommand {
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);
}
