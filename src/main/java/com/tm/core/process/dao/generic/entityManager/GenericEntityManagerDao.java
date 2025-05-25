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
    public <E> List<E> getGraphEntityList(Class<E> clazz, String graphName, Parameter... parameters) {
       try {
           return queryService.getGraphEntityList(entityManager, clazz, graphName, parameters);
       } finally {
           entityManager.clear();
       }
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graphName, Parameter... parameters) {
        try {
            return queryService.getGraphEntity(entityManager, clazz, graphName, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        try {
            return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
        } finally {
            entityManager.clear();
        }
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        try {
            return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
        } finally {
            entityManager.clear();
        }
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
