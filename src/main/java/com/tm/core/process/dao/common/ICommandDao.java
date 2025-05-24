package com.tm.core.process.dao.common;

public interface ICommandDao {
    <E> void persistEntity(E entity);
    <E> void mergeEntity(E entity);
    <E> void deleteEntity(E entity);
}
