package com.tm.core.process.dao.common;

public interface IEntityCommandDao {
    <E> void persistEntity(E entity);
    <E> void mergeEntity(E entity);
    <E> void deleteEntity(E entity);
}
