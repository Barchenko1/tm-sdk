package com.tm.core.process.dao.generic.entityManager;

import com.tm.core.finder.parameter.Parameter;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class GenericEntityManagerDao extends AbstractGenericEntityManagerDao implements IGenericEntityManagerDao {
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
        return queryService.getGraphEntityList(entityManager, clazz, graphName, parameters);
    }

    @Override
    public <E> List<E> getNamedQueryEntityList(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryEntityList(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> E getGraphEntity(Class<E> clazz, String graphName, Parameter... parameters) {
        return queryService.getGraphEntity(entityManager, clazz, graphName, parameters);
    }

    @Override
    public <E> E getNamedQueryEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryEntity(entityManager, clazz, namedQuery, parameters);
    }

    @Override
    public <E> Optional<E> getGraphOptionalEntity(Class<E> clazz, String graph, Parameter... parameters) {
        return queryService.getGraphOptionalEntity(entityManager, clazz, graph, parameters);
    }

    @Override
    public <E> Optional<E> getNamedQueryOptionalEntity(Class<E> clazz, String namedQuery, Parameter... parameters) {
        return queryService.getNamedQueryOptionalEntity(entityManager, clazz, namedQuery, parameters);
    }
}
