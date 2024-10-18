package com.tm.core.util.helper;

public interface IHibernateInitializer {
    <E> E initializeEntity(Class<?> clazz, long id);
    <E> void initializeInnerEntities(E entity);
}
