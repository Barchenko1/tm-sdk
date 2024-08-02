package com.tm.core.dao.general;

public interface IRelationshipEntityDao {
    <E> void saveEntity(E entity);
    <E> void updateEntity(E entity);
    <E> void deleteEntity(E entity);

}
