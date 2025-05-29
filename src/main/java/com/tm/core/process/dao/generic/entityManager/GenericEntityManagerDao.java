package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericEntityManagerDao extends AbstractGenericEntityManagerDao {

    public GenericEntityManagerDao(EntityManager entityManager, String entityPackage) {
        super(entityManager, entityPackage);
    }

    @Override
    public <E> void persistEntity(E entity) {
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeEntity(E entity) {
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteEntity(E entity) {
        entityManager.remove(entity);
    }

    @Override
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityList(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityList(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntity(clazz, graph, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntity(clazz, graph, parameters);
    }

    @Override
    public <E> List<E> getGraphEntityListClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityListClose(clazz, graph, parameters);
    }

    @Override
    public <E> E getGraphEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntityClose(Class<E> clazz, String graph, Parameter... parameters) {
        return fetchHandler.getGraphOptionalEntityClose(clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntity(clazz, namedQuery, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityListClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityListClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getNamedQueryEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntityClose(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return fetchHandler.getNamedQueryOptionalEntityClose(clazz, namedQuery, parameters);
    }

    @Override
    public <E> void persistSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.persist(entity);
    }

    @Override
    public <E> void mergeSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.merge(entity);
    }

    @Override
    public <E> void deleteSupplier(Supplier<E> supplier) {
        E entity = supplier.get();
        entityManager.remove(entity);
    }

    @Override
    public void executeConsumer(Consumer<EntityManager> consumer) {
        consumer.accept(entityManager);
    }
}
