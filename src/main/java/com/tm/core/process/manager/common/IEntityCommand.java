package com.tm.core.process.manager.common;

public interface IEntityCommand {
    <E> void persistEntity(E entity);
    <E> void mergeEntity(E entity);
    <E> void deleteEntity(E entity);
}
